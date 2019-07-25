package arndt.com.a3d_printing_costs.printers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.utilities.MenuCreator;
import arndt.com.a3d_printing_costs.utilities.Mutable;
import arndt.com.a3d_printing_costs.utilities.Pair;
import arndt.com.a3d_printing_costs.utilities.Triplet;
import arndt.com.a3d_printing_costs.utilities.Utility;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.ClickObj;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRAdapter;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRAdapterBuilder;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrintersActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view) public RecyclerView mRecyclerView;
    private DRAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.printers));
        if(result.getCurrentSelection() != R.id.menu_3)
            result.setSelection(R.id.menu_3);
        ButterKnife.bind(this);
        findViewById(R.id.floating_action_button).setOnClickListener(view -> addPrinter());
        setUpRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRecycler();
    }

    private void addPrinter(PrinterObj... printerObj) {
        boolean isNewPrinter = true;
        if(printerObj != null && printerObj.length > 0 && printerObj[0] != null)
            isNewPrinter = false;
        final View v = getLayoutInflater().inflate(R.layout.add_item, null);
        PrinterObj p = new PrinterObj();
        if(!isNewPrinter)
            p = printerObj[0];
        int j = 0;
        for(Map.Entry<String, Pair<String, Object>> values : p.getNameValues().entrySet()) {
            TextInputEditText f = v.findViewById(Utility.getResId("title" + j,R.id.class));
            TextInputLayout ff = v.findViewById(Utility.getResId("textinputlayout" + (j++),R.id.class));
//                f.setHint(values.getKey()+values.getValue().getKey());
            ff.setHint(values.getKey()+values.getValue().getKey());
            if(!isNewPrinter)
                f.setText(values.getValue().getValue().toString());
            if(values.getValue().getValue() instanceof Mutable)
                f.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
        }
        //printers have 6 items not 8
        v.findViewById(R.id.textinputlayout7).setVisibility(View.GONE);
        v.findViewById(R.id.textinputlayout8).setVisibility(View.GONE);
        PrinterObj finalP = p;
        boolean finalIsNewPrinter = isNewPrinter;
        new MaterialAlertDialogBuilder(this)
                .setTitle(String.format("%s Printer", isNewPrinter ? "Add a new":"Edit a"))
                .setView(v)
                .setNegativeButton("CANCEL",(dialogInterface, i) -> {})
                .setPositiveButton("SAVE",(dialogInterface, i) -> {
                    int k = 0;
                    for(Map.Entry<String, Pair<String, Object>> values : finalP.getNameValues().entrySet()) {
                        String s = ((TextInputEditText) v.findViewById(Utility.getResId("title" + (k++),R.id.class)))
                                .getText().toString();
                        if(values.getValue().getValue() instanceof StringBuilder) {
                            ((StringBuilder) values.getValue().getValue()).setLength(0);
                            ((StringBuilder) values.getValue().getValue()).append(s);
                        }else if(values.getValue().getValue() instanceof Mutable)
                            try {
                                ((Mutable) values.getValue().getValue()).setB(
                                        Double.parseDouble(s)
                                );
                            }catch (Exception e){
                                ((Mutable) values.getValue().getValue()).setB(0d);
                            }
                    }
                    if(finalIsNewPrinter)
                        DataSingleton.getPrinterObjs().add(finalP);
                    AsyncTask.execute(() -> {
                        if(finalIsNewPrinter)
                            DataSingleton.getDAO().insertAll(finalP);
                        else
                            DataSingleton.getDAO().updateAll(finalP);
                    });
                    mAdapter.setData(DataSingleton.getPrinterObjs());
                })
                .show();
    }

    private void setUpRecycler() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        List<ClickObj> clickObjs = new ArrayList<>();
        clickObjs.add(new ClickObj<>(R.id.delete));
        clickObjs.addAll(new PrinterObj().bindings.keySet());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
        mAdapter = new DRAdapterBuilder<PrinterObj>()
                .setContext(this)
                .setData(DataSingleton.getPrinterObjs())
                .setRecyclerViewId(R.id.recycler_view)
                .setViewId(R.layout.list_items)
                .setEmptyViewId(R.id.empty_view)
                .setClickIds(clickObjs)
                .build();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.getSelfPositionClicks().subscribe(o ->addPrinter((PrinterObj)o));
        mAdapter.getPositionClicks().subscribe(triplet -> {
            Triplet<DRViewHolder, ClickObj, Integer> o = (Triplet<DRViewHolder, ClickObj, Integer>) triplet;
            switch (o.getSecond().getId().intValue()){
                case R.id.delete:
                    PrinterObj v = DataSingleton.getPrinterObjs().get(o.getThird().intValue());
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Delete Printer")
                            .setMessage("Are You sure you want to delete this printer: "+
                                    v.name.toString()+"?")
                            .setPositiveButton("YES",(dialogInterface, i) -> {
                                DataSingleton.getPrinterObjs().remove(o.getThird().intValue());
                                mAdapter.setData(DataSingleton.getPrinterObjs());
                                AsyncTask.execute(() -> {
                                    DataSingleton.getDAO().delete(v);
                                });
                            })
                            .setNegativeButton("NO",(dialogInterface, i) -> {})
                            .show();
                    break;
            }
        });
    }
}
