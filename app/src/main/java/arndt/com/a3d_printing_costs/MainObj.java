package arndt.com.a3d_printing_costs;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "main")
public class MainObj {
    @NonNull
    @PrimaryKey
    public StringBuilder key = new StringBuilder();
    @ColumnInfo
    public StringBuilder value = new StringBuilder();

    public MainObj(StringBuilder key, StringBuilder value) {
        this.key = key;
        this.value = value;
    }
    public MainObj(String key, String value) {
        this.key = new StringBuilder(key);
        this.value = new StringBuilder(value);
    }

    public String getKey() {
        return key.toString();
    }

    public void setKey(StringBuilder key) {
        this.key.setLength(0);
        this.key.append(key);
    }

    public String getValue() {
        return value.toString();
    }

    public void setValue(String value) {
        this.value.setLength(0);
        this.value.append(value);
    }
}
