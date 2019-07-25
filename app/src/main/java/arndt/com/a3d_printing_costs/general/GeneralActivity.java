package arndt.com.a3d_printing_costs.general;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.utilities.MenuCreator;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.ClickObj;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRAdapter;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRAdapterBuilder;
import butterknife.BindView;
import butterknife.ButterKnife;

import static arndt.com.a3d_printing_costs.dao.DataSingleton.MONEY_UNIT;

public class GeneralActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view) public RecyclerView mRecyclerView;
    private DRAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.general));
        if(result.getCurrentSelection() != R.id.menu_4)
            result.setSelection(R.id.menu_4);
        ButterKnife.bind(this);
        setUpRecycler();
        //Can't add general_list_item items
        findViewById(R.id.floating_action_button).setVisibility(View.GONE); //.setOnClickListener(view -> addPrinter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpRecycler();
    }

    private void setUpRecycler() {
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        List<ClickObj> clickObjs = new ArrayList<>();
//        clickObjs.add(new ClickObj<>(R.id.delete));
        clickObjs.addAll(new GeneralObj().bindings.keySet());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
        mAdapter = new DRAdapterBuilder<GeneralObj>()
                .setContext(this)
                .setData(DataSingleton.getGeneralObjs())
                .setRecyclerViewId(R.id.recycler_view)
                .setViewId(R.layout.general_list_item)
                .setEmptyViewId(R.id.empty_view)
                .setClickIds(clickObjs)
                .build();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.getSelfPositionClicks().subscribe(o ->{
            GeneralObj g = (GeneralObj) o;
            EditText txt = new EditText(this);
            txt.setMaxLines(1);
            txt.setText(g.value.toString());
            if(!g.key.toString().equals(MONEY_UNIT))
                txt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            new MaterialAlertDialogBuilder(this)
                    .setMessage(String.format("%s [%s]", g.key,g.unit).replace("[]",""))
                    .setView(txt)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> { })
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        g.value.setLength(0);
                        g.value.append(txt.getText().toString());
                        if (g.key.toString().equals(MONEY_UNIT))
                            DataSingleton.setCurrency(g.value.toString());
                        AsyncTask.execute(() ->DataSingleton.getDAO().updateAll(g));
                        mAdapter.notifyDataSetChanged();
                    })
                    .show();
        });
        mAdapter.getPositionClicks().subscribe(triplet -> {/*No buttons in settings*/});
    }
}
