package arndt.com.a3d_printing_costs.utilities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import arndt.com.a3d_printing_costs.MainActivity;
import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.consumables.ConsumablesActivity;
import arndt.com.a3d_printing_costs.general.GeneralActivity;
import arndt.com.a3d_printing_costs.materials.MaterialsActivity;
import arndt.com.a3d_printing_costs.postprocessings.PostProcessingsActivity;
import arndt.com.a3d_printing_costs.preparations.PreperationsActivity;
import arndt.com.a3d_printing_costs.printers.PrintersActivity;

public class MenuCreator {

    public static Drawer getDrawer(AppCompatActivity context, String title){
        // Handle Toolbar
        androidx.appcompat.widget.Toolbar toolbar = context.findViewById(R.id.toolbar);
        context.setSupportActionBar(toolbar);
        context.getSupportActionBar().setTitle(title);
        Drawer result = new DrawerBuilder()
                .withActivity(context)
                .withToolbar(toolbar)
                .inflateMenu(R.menu.drawer)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if(view==null) return false;
                    if (drawerItem instanceof Nameable) {
                        Class<?> clz = MainActivity.class;
                        switch (view.getId()) {
                            case R.id.menu_1:
                                break;
                            case R.id.menu_2:
                                clz = MaterialsActivity.class;
                                break;
                            case R.id.menu_3:
                                clz = PrintersActivity.class;
                                break;
                            case R.id.menu_4:
                                clz = GeneralActivity.class;
                                break;
                            case R.id.menu_5:
                                clz = ConsumablesActivity.class;
                                break;
                            case R.id.menu_6:
                                clz = PostProcessingsActivity.class;
                                break;
                            case R.id.menu_7:
                                clz = PreperationsActivity.class;
                                break;
                        }
                        if (clz.getName().equals(String.format("%s.%s", context.getPackageName(), context.getLocalClassName())))
                            return false;
//                            Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(context, clz);
//                            myIntent.putExtra("key", value); //Optional parameters
                        context.startActivity(myIntent);
                    }
                    return false;
                }).build();
        return result;
    }
}
