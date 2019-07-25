package arndt.com.a3d_printing_costs.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import arndt.com.a3d_printing_costs.consumables.ConsumablesObj;
import arndt.com.a3d_printing_costs.general.GeneralObj;
import arndt.com.a3d_printing_costs.materials.MaterialObj;
import arndt.com.a3d_printing_costs.postprocessings.PostProcessingsObj;
import arndt.com.a3d_printing_costs.preparations.PreparationObj;
import arndt.com.a3d_printing_costs.printers.PrinterObj;

@Database(entities = {
        GeneralObj.class, MaterialObj.class, PrinterObj.class,
        ConsumablesObj.class, PreparationObj.class, PostProcessingsObj.class
}, version = 7, exportSchema = false)
@TypeConverters({DataTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDAO schedulerDAO();
}
