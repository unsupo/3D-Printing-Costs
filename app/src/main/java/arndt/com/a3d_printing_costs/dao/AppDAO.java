package arndt.com.a3d_printing_costs.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import arndt.com.a3d_printing_costs.MainObj;
import arndt.com.a3d_printing_costs.consumables.ConsumablesObj;
import arndt.com.a3d_printing_costs.general.GeneralObj;
import arndt.com.a3d_printing_costs.materials.MaterialObj;
import arndt.com.a3d_printing_costs.postprocessings.PostProcessingsObj;
import arndt.com.a3d_printing_costs.preparations.PreparationObj;
import arndt.com.a3d_printing_costs.printers.PrinterObj;

@Dao
public interface AppDAO {
    @Query("select * from general_list_item")
    List<GeneralObj> getGeneral();
    @Query("select * from materials")
    List<MaterialObj> getMaterials();
    @Query("select * from printers")
    List<PrinterObj> getPrinters();
    @Query("select * from consumables")
    List<ConsumablesObj> getConsumables();
    @Query("select * from postprocessings")
    List<PostProcessingsObj> getPostprocessings();
    @Query("select * from preparation")
    List<PreparationObj> getPreparation();
    @Query("select * from main")
    List<MainObj> getMain();

    @Delete
    void delete(GeneralObj general);
    @Delete
    void delete(MaterialObj materials);
    @Delete
    void delete(PrinterObj printers);
    @Delete
    void delete(ConsumablesObj general);
    @Delete
    void delete(PostProcessingsObj materials);
    @Delete
    void delete(PreparationObj printers);
    @Delete
    void delete(MainObj printers);

    @Insert
    void insertAll(GeneralObj... general);
    @Insert
    void insertAll(MaterialObj... materials);
    @Insert
    void insertAll(PrinterObj... printers);
    @Insert
    void insertAll(ConsumablesObj... general);
    @Insert
    void insertAll(PostProcessingsObj... materials);
    @Insert
    void insertAll(PreparationObj... printers);
    @Insert
    void insertAll(MainObj... printers);

    @Update
    void updateAll(GeneralObj... general);
    @Update
    void updateAll(MaterialObj... materials);
    @Update
    void updateAll(PrinterObj... printers);
    @Update
    void updateAll(ConsumablesObj... general);
    @Update
    void updateAll(PostProcessingsObj... materials);
    @Update
    void updateAll(PreparationObj... printers);
    @Update
    void updateAll(MainObj... printers);

}
