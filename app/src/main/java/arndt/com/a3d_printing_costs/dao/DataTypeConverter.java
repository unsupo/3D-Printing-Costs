package arndt.com.a3d_printing_costs.dao;

import androidx.room.TypeConverter;

import arndt.com.a3d_printing_costs.utilities.Mutable;

class DataTypeConverter {
    @TypeConverter
    public static String toString(StringBuilder stringBuilder){
        return stringBuilder.toString();
    }
    @TypeConverter
    public static StringBuilder fromString(String stringBuilder){
        return new StringBuilder(stringBuilder);
    }
    @TypeConverter
    public static Double toDouble(Mutable<Double> v){
        return v.getB();
    }
    @TypeConverter
    public static Mutable<Double> fromDouble(double v){
        return new Mutable<>(v);
    }
}
