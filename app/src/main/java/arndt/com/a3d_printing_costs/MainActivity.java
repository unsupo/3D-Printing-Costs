package arndt.com.a3d_printing_costs;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.materialdrawer.Drawer;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

import arndt.com.a3d_printing_costs.consumables.ConsumablesObj;
import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.materials.MaterialObj;
import arndt.com.a3d_printing_costs.postprocessings.PostProcessingsObj;
import arndt.com.a3d_printing_costs.preparations.PreparationObj;
import arndt.com.a3d_printing_costs.printers.PrinterObj;
import arndt.com.a3d_printing_costs.utilities.MenuCreator;

import static arndt.com.a3d_printing_costs.dao.DataSingleton.ENERGY_COSTS;
import static arndt.com.a3d_printing_costs.dao.DataSingleton.LABOR_COSTS;
import static arndt.com.a3d_printing_costs.dao.DataSingleton.MARKUP;

public class MainActivity extends AppCompatActivity {
    private static final String TIME_HR = "Time Hour", TIME_MN = "Time Minute";
    private Drawer result = null;
    private static final String WEIGHT = "WEIGHT";

    MaterialBetterSpinner materialBetterSpinnerF, materialBetterSpinnerP;
    private PieChart chart;
    ArrayList<PieEntry> entries = new ArrayList<>();
    private PrinterObj printer;
    private MaterialObj material;
    double weight;
    TimePicker tpHourMin;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataSingleton.getInstance(this);
        setContentView(R.layout.activity_main);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.quote));
        if(result.getCurrentSelection() != R.id.menu_1)
            result.setSelection(R.id.menu_1);
        materialBetterSpinnerF = findViewById(R.id.spinnerFillament);
        materialBetterSpinnerP = findViewById(R.id.spinnerPrinter);
        tpHourMin = findViewById(R.id.datePicker1);
        tpHourMin.setIs24HourView(true);
        tpHourMin.setOnTimeChangedListener((timePicker, i, i1) -> {
            entries.clear();
            DataSingleton.addMain(new MainObj(TIME_HR,timePicker.getHour()+""),
                    new MainObj(TIME_MN,timePicker.getMinute()+""));
            setUpFields();
            setUpChart();
        });

        ((TextInputEditText)findViewById(R.id.title0)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().isEmpty())
                    weight = 0;
                else
                    weight = Double.parseDouble(charSequence.toString());
                DataSingleton.addMain(new MainObj(WEIGHT,weight+""));
                entries.clear();
                setUpFields();
                setUpChart();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        setup();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume(){
        super.onResume();
        setup();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setup() {
        entries.clear();
        setUpValues();
        setUpSpinner();
        setUpFields();
        setUpChart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUpValues() {
        if(DataSingleton.getMainObj(WEIGHT)!=null)
            ((TextInputEditText)findViewById(R.id.title0)).setText(DataSingleton.getMainObj(WEIGHT)+"");
        if(DataSingleton.getMainObj(TIME_HR)!=null)
            tpHourMin.setHour(Integer.parseInt(DataSingleton.getMainObj(TIME_HR)));
        else
            tpHourMin.setHour(0);
        if(DataSingleton.getMainObj(TIME_MN)!=null) {
            tpHourMin.setMinute(Integer.parseInt(DataSingleton.getMainObj(TIME_MN)));
        } else
            tpHourMin.setMinute(0);
    }

    private void setUpChart() {
        chart = findViewById(R.id.piechart);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
        l.setYOffset(5f);

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(0,0,0,0);
        chart.setHoleRadius(40f);
        chart.setTransparentCircleRadius(48f);
        chart.setTouchEnabled(false);

        PieDataSet dataSet = new PieDataSet(entries, "Part Costs");

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData data = new PieData();
        data.addDataSet(dataSet);
        data.setValueFormatter(new PercentFormatter());
        chart.setData(data);
        chart.invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUpFields() {
        double totalCost = 0;
        String unit = String.format("[%s]", DataSingleton.getCurrency());
        double cost = 0;

        View fc = findViewById(R.id.filament_cost);
        ((TextView) fc.findViewById(R.id.unit)).setText(unit);
        ((TextView) fc.findViewById(R.id.title)).setText(R.string.filament_cost);
        if(material != null)
            cost =  weight / 1000 * material.price.getB();
        else cost = 0;
        entries.add(new PieEntry((float) cost, getResources().getString(R.string.filament_cost)));
        totalCost += cost;
        ((TextView) fc.findViewById(R.id.value)).setText(String.format("%.2f", cost));

        View ec = findViewById(R.id.electricity_cost);
        ((TextView)ec.findViewById(R.id.unit)).setText(unit);
        ((TextView)ec.findViewById(R.id.title)).setText(R.string.electricity_cost);
        if(printer != null)
            cost=printer.energyConsumption.getB()*getTime()*DataSingleton.getGeneral(ENERGY_COSTS);
        else cost = 0;
        entries.add(new PieEntry((float) cost,getResources().getString(R.string.electricity_cost)));
        totalCost+=cost;
        ((TextView)ec.findViewById(R.id.value)).setText(String.format("%.2f", cost));

        View pd = findViewById(R.id.printer_depreciation);
        ((TextView)pd.findViewById(R.id.unit)).setText(unit);
        ((TextView)pd.findViewById(R.id.title)).setText(R.string.printer_depreciation);
        if(printer != null)
            cost=printer.depreciation.getB()*getTime();
        else cost = 0;
        entries.add(new PieEntry((float) cost,getResources().getString(R.string.printer_depreciation)));
        totalCost+=cost;
        ((TextView)pd.findViewById(R.id.value)).setText(String.format("%.2f", cost));

        View prp = findViewById(R.id.preparation_costs);
        cost = 0;
        ((TextView)prp.findViewById(R.id.unit)).setText(unit);
        ((TextView)prp.findViewById(R.id.title)).setText(R.string.preparation_costs);
        for(PreparationObj p : DataSingleton.getPreparationObjs())
            cost+=Double.parseDouble(p.value.toString());
        cost = DataSingleton.getGeneral(LABOR_COSTS)*cost/60.;
        ((TextView)prp.findViewById(R.id.value)).setText(String.format("%.2f", cost));
        entries.add(new PieEntry((float) cost,getResources().getString(R.string.preparation)));
        totalCost+=cost;

        View pp = findViewById(R.id.post_processing_costs);
        ((TextView)pp.findViewById(R.id.unit)).setText(unit);
        ((TextView)pp.findViewById(R.id.title)).setText(R.string.post_processing_costs);
        cost = 0;
        for(PostProcessingsObj p : DataSingleton.getPostProcessingsObjs())
            cost+=Double.parseDouble(p.value.toString());
        cost = DataSingleton.getGeneral(LABOR_COSTS)*cost/60.;
        ((TextView)pp.findViewById(R.id.value)).setText(String.format("%.2f", cost));
        entries.add(new PieEntry((float) cost,getResources().getString(R.string.post_processing)));
        totalCost+=cost;

        View c = findViewById(R.id.consumables_costs);
        ((TextView)c.findViewById(R.id.unit)).setText(unit);
        ((TextView)c.findViewById(R.id.title)).setText(R.string.consumables_costs);
        cost = 0;
        for(ConsumablesObj p : DataSingleton.getConsumablesObjs())
            cost+=Double.parseDouble(p.value.toString());
        ((TextView)c.findViewById(R.id.value)).setText(String.format("%.2f", cost));
        entries.add(new PieEntry((float) cost,getResources().getString(R.string.consumables)));
        totalCost+=cost;

        View q = findViewById(R.id.suggested_price);
        ((TextView)q.findViewById(R.id.unit)).setText(unit);
        ((TextView)q.findViewById(R.id.title)).setText(R.string.suggested_price);
        ((TextView)q.findViewById(R.id.value)).setText(String.format("%.2f", totalCost*(DataSingleton.getGeneral(MARKUP)/100+1)) );
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Double getTime() {
        int h = tpHourMin.getHour();
        int m = tpHourMin.getMinute();
        return h+m/60.;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setUpSpinner() {
        List<String> fillaments = new ArrayList<>();
        for(MaterialObj m : DataSingleton.getMaterialObjs())
            fillaments.add(m.manufacturer.toString());
        if(fillaments.isEmpty()){
            findViewById(R.id.spinnerFillament).setVisibility(View.GONE);
            findViewById(R.id.empty_viewFillament).setVisibility(View.VISIBLE);
        }else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, fillaments);

            materialBetterSpinnerF.setAdapter(adapter);
            materialBetterSpinnerF.setOnItemClickListener((adapterView, view, i, l) -> {
                material = DataSingleton.getMaterialObjs().get(i);
                entries.clear();
                setUpFields();
                setUpChart();
            });
//            materialBetterSpinnerF.setSelection(0);
//            int i = materialBetterSpinnerF.getListSelection();
//            material = DataSingleton.getMaterialObjs().get(i);
        }


        List<String> printers = new ArrayList<>();
        for(PrinterObj m : DataSingleton.getPrinterObjs())
            printers.add(m.name.toString());
        if(printers.isEmpty()){
            findViewById(R.id.spinnerPrinter).setVisibility(View.GONE);
            findViewById(R.id.empty_viewPrinter).setVisibility(View.VISIBLE);
        }else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, printers);

            materialBetterSpinnerP.setAdapter(adapter);
            materialBetterSpinnerP.setOnItemClickListener((adapterView, view, i, l) -> {
                printer = DataSingleton.getPrinterObjs().get(i);
                entries.clear();
                setUpFields();
                setUpChart();
            });
            //TODO set selection
//            materialBetterSpinnerP.setSelection(0);
//            int i = materialBetterSpinnerP.getListSelection();
//            printer = DataSingleton.getPrinterObjs().get(i);
        }
    }
}
