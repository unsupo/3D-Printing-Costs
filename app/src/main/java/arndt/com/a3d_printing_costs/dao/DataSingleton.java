package arndt.com.a3d_printing_costs.dao;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import arndt.com.a3d_printing_costs.general.GeneralObj;
import arndt.com.a3d_printing_costs.materials.MaterialObj;
import arndt.com.a3d_printing_costs.printers.PrinterObj;

public class DataSingleton {

    public static final String MONEY_UNIT = "Money unit";
    private AppDatabase db;
    private Activity context;
    private List<GeneralObj> generalObjs = new ArrayList<>();
    private List<MaterialObj> materialObjs = new ArrayList<>();
    private List<PrinterObj> printerObjs = new ArrayList<>();
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
            this.generalObjs.clear();
            this.materialObjs.clear();
            this.printerObjs.clear();
            this.generalObjs.addAll(db.schedulerDAO().getGeneral());
            this.materialObjs.addAll(db.schedulerDAO().getMaterials());
            this.printerObjs.addAll(db.schedulerDAO().getPrinters());

            // Default settings
            if(this.generalObjs.isEmpty()){
                ArrayList<GeneralObj> gos = new ArrayList<>();
            /*
            Energy cost	    0.26	€/kWh
            Labor Costs	    30	    €/h
            Failure rate	10	    %
            Money unit	    €
             */
                gos.add(new GeneralObj("Energy Cost","0.26","€/kWh".replace("€",getCurrency())));
                gos.add(new GeneralObj("Labor Costs","30","€/h".replace("€",getCurrency())));
                gos.add(new GeneralObj("Failure rate","10","%"));
                gos.add(new GeneralObj(MONEY_UNIT,getCurrency(),""));
                this.generalObjs.addAll(gos);
                db.schedulerDAO().insertAll(gos.toArray(new GeneralObj[gos.size()]));
            }
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
