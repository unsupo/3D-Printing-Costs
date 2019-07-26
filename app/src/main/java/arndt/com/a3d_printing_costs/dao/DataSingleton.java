package arndt.com.a3d_printing_costs.dao;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import arndt.com.a3d_printing_costs.MainObj;
import arndt.com.a3d_printing_costs.consumables.ConsumablesObj;
import arndt.com.a3d_printing_costs.general.GeneralObj;
import arndt.com.a3d_printing_costs.materials.MaterialObj;
import arndt.com.a3d_printing_costs.postprocessings.PostProcessingsObj;
import arndt.com.a3d_printing_costs.preparations.PreparationObj;
import arndt.com.a3d_printing_costs.printers.PrinterObj;

public class DataSingleton {

    public static final String MONEY_UNIT = "Money unit";
    public static final String MARKUP = "Markup";
    public static final String ENERGY_COSTS = "Energy Cost";
    public static final String LABOR_COSTS = "Labor Costs";
    public static final String FAILURE_RATE = "Failure rate";
    private AppDatabase db;
    private Activity context;
    private List<GeneralObj> generalObjs = new ArrayList<>();
    private List<MaterialObj> materialObjs = new ArrayList<>();
    private List<PrinterObj> printerObjs = new ArrayList<>();
    private List<ConsumablesObj> consumablesObjs = new ArrayList<>();
    private List<PostProcessingsObj> postProcessingsObjs = new ArrayList<>();
    private List<PreparationObj> preparationObjs = new ArrayList<>();
    private List<MainObj> main = new ArrayList<>();
    private static DataSingleton instance;
    private StringBuilder currency = new StringBuilder("$");

    public static DataSingleton getInstance(Activity... context){
        if(instance == null)
            instance = new DataSingleton(context[0]);
        return instance;
    }
    public static void updateData(){
        getInstance().getData();
    }

    public static List<GeneralObj> getGeneralObjs() {
        return getInstance().generalObjs;
    }

    public static List<MaterialObj> getMaterialObjs() {
        return getInstance().materialObjs;
    }

    public static List<ConsumablesObj> getConsumablesObjs() {
        return getInstance().consumablesObjs;
    }
    public static String getMainObj(String key){
        for(MainObj mainObj : instance.main)
            if(mainObj.key.toString().equals(key))
                return mainObj.value.toString();
        return null;
    }

    public static List<PostProcessingsObj> getPostProcessingsObjs() {
        return getInstance().postProcessingsObjs;
    }

    public static List<PreparationObj> getPreparationObjs() {
        return getInstance().preparationObjs;
    }

    public static List<PrinterObj> getPrinterObjs() {
        if(getInstance().printerObjs != null)
            return getInstance().printerObjs;
        else
            return new ArrayList<>();
    }

    private DataSingleton(Activity context) {
        this.context = context;
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "printingcosts-userdata.sqllite")
                .fallbackToDestructiveMigration()
                .build();
        getData();
    }

    public static String getCurrency() {
        return instance.currency.toString();
    }

    public static AppDAO getDAO() {
        return instance.db.schedulerDAO();
    }

    public static void insertAll(Object p) {
        AsyncTask.execute(() -> {
            if(p instanceof PrinterObj)
                getDAO().insertAll((PrinterObj) p);
            else if(p instanceof MaterialObj)
                getDAO().insertAll((MaterialObj) p);
            else if(p instanceof GeneralObj)
                getDAO().insertAll((GeneralObj) p);
            else
                throw new IllegalArgumentException("Uknown type of object: "+p);
            updateData();
        });
    }

    public static void setCurrency(String v) {
        instance._setCurrency(v);
    }

    public static Double getGeneral(String key) {
        for(GeneralObj o : getGeneralObjs())
            if(o.key.toString().equals(key))
                return Double.parseDouble(o.value.toString());
        return null;
    }
    public static String getGeneralStr(String key) {
        for(GeneralObj o : getGeneralObjs())
            if(o.key.toString().equals(key))
                return o.value.toString();
        return null;
    }

    public static void addMain(MainObj... mainObjs) {
        for(MainObj mainObj : mainObjs)
            if(getMainObj(mainObj.key.toString()) == null) {
                AsyncTask.execute(() -> getDAO().insertAll(mainObj));
                instance.main.add(mainObj);
            }else {
                AsyncTask.execute(() -> getDAO().updateAll(mainObj));
                for(MainObj m : instance.main)
                    if(m.key.toString().equals(mainObj.key.toString())){
                        m.setValue(mainObj.getValue());
                        break;
                    }
            }
    }

    private void _setCurrency(String v) {
        currency.setLength(0);
        currency.append(v);
        for (int i = 0; i < this.generalObjs.size(); i++) {
            switch (this.generalObjs.get(i).key.toString()){
                case "Energy Cost":
                    this.generalObjs.get(i).unit.setLength(0);
                    this.generalObjs.get(i).unit.append(String.format("%s/kWh", v));
                    break;
                case "Labor Costs":
                    this.generalObjs.get(i).unit.setLength(0);
                    this.generalObjs.get(i).unit.append(String.format("%s/h", v));
                    break;
            }
        }
    }

    private void getData() {
        AsyncTask.execute(() -> {
            this.main = db.schedulerDAO().getMain();

            this.generalObjs.clear();
            this.materialObjs.clear();
            this.printerObjs.clear();
            this.consumablesObjs.clear();
            this.postProcessingsObjs.clear();
            this.preparationObjs.clear();
            this.generalObjs.addAll(db.schedulerDAO().getGeneral());
            this.materialObjs.addAll(db.schedulerDAO().getMaterials());
            this.printerObjs.addAll(db.schedulerDAO().getPrinters());
            this.consumablesObjs.addAll(db.schedulerDAO().getConsumables());
            this.postProcessingsObjs.addAll(db.schedulerDAO().getPostprocessings());
            this.preparationObjs.addAll(db.schedulerDAO().getPreparation());

            // Default settings
            ArrayList<GeneralObj> gos = new ArrayList<>();
            if(getGeneralStr(ENERGY_COSTS) == null)
                gos.add(new GeneralObj(ENERGY_COSTS,"0.26","€/kWh".replace("€",getCurrency())));
            if(getGeneralStr(LABOR_COSTS) == null)
                gos.add(new GeneralObj(LABOR_COSTS,"30","€/h".replace("€",getCurrency())));
            if(getGeneralStr(FAILURE_RATE) == null)
                gos.add(new GeneralObj(FAILURE_RATE,"10","%"));
            if(getGeneralStr(MARKUP) == null)
                gos.add(new GeneralObj(MARKUP,"10","%"));
            if(getGeneralStr(MONEY_UNIT) == null)
                gos.add(new GeneralObj(MONEY_UNIT,getCurrency(),""));

            this.generalObjs.addAll(gos);
            db.schedulerDAO().insertAll(gos.toArray(new GeneralObj[gos.size()]));

            for(GeneralObj g : this.generalObjs)
                if(g.key.equals(MONEY_UNIT)){
                    this._setCurrency(g.value.toString());
                    break;
                }
            for(GeneralObj g : this.generalObjs)
                switch (g.key.toString()){
                    case  "Energy Cost":
                        g.unit.setLength(0);
                        g.unit.append("€/kWh".replace("€",getCurrency()));
                        break;
                    case  "Labor Costs":
                        g.unit.setLength(0);
                        g.unit.append("€/h".replace("€",getCurrency()));
                        break;
                }
        });
    }
}
