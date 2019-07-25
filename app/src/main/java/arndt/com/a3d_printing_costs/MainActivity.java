package arndt.com.a3d_printing_costs;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mikepenz.materialdrawer.Drawer;

import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.utilities.MenuCreator;

public class MainActivity extends AppCompatActivity {
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataSingleton.getInstance(this);
        setContentView(R.layout.activity_main);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.quote));
        if(result.getCurrentSelection() != R.id.menu_1)
            result.setSelection(R.id.menu_1);
    }
}
