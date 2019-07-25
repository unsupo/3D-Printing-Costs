package arndt.com.a3d_printing_costs.printers;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.utilities.Mutable;
import arndt.com.a3d_printing_costs.utilities.Pair;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.ClickObj;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DataModel;

@Entity(tableName = "printers")
public class PrinterObj implements DataModel {
    @NonNull
    @PrimaryKey
    public String uuid;
    @ColumnInfo
    public StringBuilder name = new StringBuilder();
    @ColumnInfo
    public Mutable<Double> materialDiameter = new Mutable<>(0d),price = new Mutable<>(0d),
            depreciationTime = new Mutable<>(0d),serviceCostsPerLife = new Mutable<>(0d),
            energyConsumption = new Mutable<>(0d),depreciation = new Mutable<>(0d);

    @Ignore
    HashMap<String, Pair<String,Object>> nameValues = new HashMap<>();
    @Ignore
    HashMap<ClickObj, Object> bindings = new HashMap<>();
    @Ignore
    public PrinterObj() {
        this.uuid = UUID.randomUUID().toString();
        setUpBindings();
    }


    public PrinterObj(String name, double materialDiameter, double price, double depreciationTime,
                      double serviceCostsPerLife, double energyConsumption, double depreciation) {
        this.name = new StringBuilder(name);
        this.materialDiameter = new Mutable<>(materialDiameter);
        this.price = new Mutable<>(price);
        this.depreciationTime = new Mutable<>(depreciationTime);
        this.serviceCostsPerLife = new Mutable<>(serviceCostsPerLife);
        this.energyConsumption = new Mutable<>(energyConsumption);
        this.depreciation = new Mutable<>(depreciation);
        setUpBindings();
    }

    public PrinterObj(StringBuilder name, Mutable<Double> materialDiameter, Mutable<Double> price, Mutable<Double> depreciationTime, Mutable<Double> serviceCostsPerLife, Mutable<Double> energyConsumption, Mutable<Double> depreciation) {
        this.name = name;
        this.materialDiameter = materialDiameter;
        this.price = price;
        this.depreciationTime = depreciationTime;
        this.serviceCostsPerLife = serviceCostsPerLife;
        this.energyConsumption = energyConsumption;
        this.depreciation = depreciation;
        setUpBindings();
    }

    private void setUpBindings() {
        bindings.put(new ClickObj<>(R.id.name, name, false),name);

        bindings.put(new ClickObj<>(R.id.item_1, R.id.value, materialDiameter, false),materialDiameter);
        bindings.put(new ClickObj<>(R.id.item_2, R.id.value, price, false),price);
        bindings.put(new ClickObj<>(R.id.item_3, R.id.value, depreciationTime, false),depreciationTime);
        bindings.put(new ClickObj<>(R.id.item_4, R.id.value, serviceCostsPerLife, false),serviceCostsPerLife);
        bindings.put(new ClickObj<>(R.id.item_5, R.id.value, energyConsumption, false),energyConsumption);
        bindings.put(new ClickObj<>(R.id.item_6, R.id.value, depreciation, false),depreciation);

        List<Integer> v = Arrays.asList(R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4, R.id.item_5, R.id.item_6);
        String u = "Material Diameter [mm]\tPrice [€]\tDepreciation Time [h]\tService costs per life [€]\tEnergy Consumption [kWh/h]\tDepreciation [€/h]";
        String[] uu = u.split("\t");
        for (int i = 0; i < v.size(); i++) {
            String s= uu[i].replaceAll(" \\[.*\\]","");
            bindings.put(new ClickObj<>(v.get(i), R.id.title,s , false), s);
        }
        for (int i = 0; i < v.size(); i++) {
            String s= uu[i].replaceAll(".*\\[", "[").replace("€", DataSingleton.getCurrency());
            bindings.put(new ClickObj<>(v.get(i), R.id.unit, s, false), s);
        }

        bindings.put(new ClickObj<>(R.id.item_7, R.id.value, null, false),null);
        bindings.put(new ClickObj<>(R.id.item_8, R.id.value, null, false),null);

        setUpNameValues();
    }

    private void setUpNameValues() {
        /*
        Material Diameter [mm]	Price [€]	Depreciation Time [h]	Service costs per life [€]	Energy Consumption [kWh/h]	Depreciation [€/h]
         */
        nameValues.put("Name",new Pair<>("",name));
        nameValues.put("Material Diameter",new Pair<>(" [mm]",materialDiameter));
        nameValues.put("Price",new Pair<>(String.format(" [%s]", DataSingleton.getCurrency()),price));
        nameValues.put("Depreciation Time",new Pair<>(" [h]",depreciationTime));
        nameValues.put("Service costs per life",new Pair<>(String.format(" [%s]", DataSingleton.getCurrency()),serviceCostsPerLife));
        nameValues.put("Energy Consumption",new Pair<>(" [kWh/h]",energyConsumption));
        nameValues.put("Depreciation",new Pair<>(String.format(" [%s/h]", DataSingleton.getCurrency()),depreciation));
    }

    public HashMap<String, Pair<String, Object>> getNameValues() {
        return nameValues;
    }

    @Override
    public HashMap<ClickObj, Object> getBindings() {
        return bindings;
    }
}
