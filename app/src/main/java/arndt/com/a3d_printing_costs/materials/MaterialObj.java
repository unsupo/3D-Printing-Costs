package arndt.com.a3d_printing_costs.materials;

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

@Entity(tableName = "materials")
public class MaterialObj implements DataModel {
    @NonNull
    @PrimaryKey
    public String uuid;
    @ColumnInfo
    public StringBuilder manufacturer = new StringBuilder();
    @ColumnInfo
    public Mutable<Double> spoolPrice = new Mutable<>(0d),spoolSize = new Mutable<>(0d),
            density = new Mutable<>(0d),nozzelTemp = new Mutable<>(0d),diameter = new Mutable<>(0d),
            bedTemp = new Mutable<>(0d),lengthPerRoll = new Mutable<>(0d),price = new Mutable<>(0d);
    @Ignore
    HashMap<String, Pair<String,Object>> nameValues = new HashMap<>();
    @Ignore
    HashMap<ClickObj, Object> bindings = new HashMap<>();

    @Ignore
    public MaterialObj() {
        uuid = UUID.randomUUID().toString();
        setUpBindings();
    }

    public MaterialObj(StringBuilder manufacturer, Mutable<Double> spoolPrice, Mutable<Double> spoolSize, Mutable<Double> density, Mutable<Double> nozzelTemp, Mutable<Double> diameter, Mutable<Double> bedTemp, Mutable<Double> lengthPerRoll, Mutable<Double> price) {
        this.manufacturer = manufacturer;
        this.spoolPrice = spoolPrice;
        this.spoolSize = spoolSize;
        this.density = density;
        this.nozzelTemp = nozzelTemp;
        this.diameter = diameter;
        this.bedTemp = bedTemp;
        this.lengthPerRoll = lengthPerRoll;
        this.price = price;
        setUpBindings();
    }

    private void setUpBindings() {
        bindings.put(new ClickObj<>(R.id.name, manufacturer, false),manufacturer);

        bindings.put(new ClickObj<>(R.id.item_1, R.id.value, diameter, false),diameter);
        bindings.put(new ClickObj<>(R.id.item_2, R.id.value, spoolPrice, false),spoolPrice);
        bindings.put(new ClickObj<>(R.id.item_3, R.id.value, spoolSize, false),spoolSize);
        bindings.put(new ClickObj<>(R.id.item_4, R.id.value, density, false),density);
        bindings.put(new ClickObj<>(R.id.item_5, R.id.value, nozzelTemp, false),nozzelTemp);
        bindings.put(new ClickObj<>(R.id.item_6, R.id.value, bedTemp, false),bedTemp);
        bindings.put(new ClickObj<>(R.id.item_7, R.id.value, lengthPerRoll, false),lengthPerRoll);
        bindings.put(new ClickObj<>(R.id.item_8, R.id.value, price, false),price);

        List<Integer> v = Arrays.asList(R.id.item_1, R.id.item_2, R.id.item_3, R.id.item_4, R.id.item_5, R.id.item_6, R.id.item_7, R.id.item_8);
        String u = "Diameter [mm]\tSpool Price [€]\tSpool Size [kg]\tDensity [g/cm³]\tNozzel Temp [°C]\tBed Temp [°C]\tLength per roll [m]\tPrice [€/kg]";
        String[] uu = u.split("\t");
        for (int i = 0; i < v.size(); i++) {
            String s= uu[i].replaceAll(" \\[.*\\]","");
            bindings.put(new ClickObj<>(v.get(i), R.id.title,s , false), s);
        }
        for (int i = 0; i < v.size(); i++) {
            String s= uu[i].replaceAll(".*\\[", "[").replace("€", DataSingleton.getCurrency());
            bindings.put(new ClickObj<>(v.get(i), R.id.unit, s, false), s);
        }

        setUpNameValues();
    }

    private void setUpNameValues() {
        /*
        Material Diameter [mm]	Price [€]	Depreciation Time [h]	Service costs per life [€]	Energy Consumption [kWh/h]	Depreciation [€/h]
         */
        nameValues.put("Manufacturer",new Pair<>("",manufacturer));
        nameValues.put("Diameter",new Pair<>(" [mm]",diameter));
        nameValues.put("Spool Price",new Pair<>(String.format(" [%s]", DataSingleton.getCurrency()),spoolPrice));
        nameValues.put("Spool Size",new Pair<>(" [kg]",spoolSize));
        nameValues.put("Density",new Pair<>(" [g/cm³]",density));
        nameValues.put("Nozzel Temp",new Pair<>(" [°C]",nozzelTemp));
        nameValues.put("Bed Temp",new Pair<>(" [°C]",bedTemp));
        nameValues.put("Length per roll",new Pair<>(" [m]",lengthPerRoll));
        nameValues.put("Price",new Pair<>(String.format(" [%s/kg]", DataSingleton.getCurrency()),price));
    }

    public HashMap<String, Pair<String, Object>> getNameValues() {
        return nameValues;
    }

    @Override
    public HashMap<ClickObj, Object> getBindings() {
        return bindings;
    }


}
