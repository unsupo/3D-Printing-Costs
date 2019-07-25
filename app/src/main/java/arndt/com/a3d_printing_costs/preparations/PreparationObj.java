package arndt.com.a3d_printing_costs.preparations;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.utilities.Pair;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.ClickObj;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DataModel;

@Entity(tableName = "preparation")
public class PreparationObj implements DataModel {
    @NonNull
    @PrimaryKey
    public String uuid;
    @ColumnInfo
    public StringBuilder key = new StringBuilder(), value = new StringBuilder(), unit = new StringBuilder("min");

    @Ignore
    HashMap<String, Pair<String,Object>> nameValues = new HashMap<>();
    @Ignore
    HashMap<ClickObj, Object> bindings = new HashMap<>();
    @Ignore
    public PreparationObj() {
        this.uuid = UUID.randomUUID().toString();
        setUpBindings();
    }

    public PreparationObj(String key, String value, String unit) {
        this.uuid = UUID.randomUUID().toString();
        this.key = new StringBuilder(key);
        this.unit = new StringBuilder(unit);
        this.value = new StringBuilder(value);
        setUpBindings();
    }

    public PreparationObj(@NonNull String uuid, StringBuilder key, StringBuilder unit, StringBuilder value) {
        this.uuid = uuid;
        this.key = key;
        this.unit = unit;
        this.value = value;
        setUpBindings();
    }

    private void setUpBindings() {
        bindings.put(new ClickObj<>(R.id.name, key, false),key);
        bindings.put(new ClickObj<>(R.id.value, value, false),value);
        bindings.put(new ClickObj<>(R.id.unit, unit, false),unit);
    }

    @Override
    public HashMap<ClickObj, Object> getBindings() {
        return bindings;
    }

    public HashMap<String, Pair<String, Object>> getNameValues() {
        return nameValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreparationObj that = (PreparationObj) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
