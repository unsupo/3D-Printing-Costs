package arndt.com.a3d_printing_costs.preparations;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
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

public class PreperationsActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view) public RecyclerView mRecyclerView;
    private DRAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.preparation));
        if(result.getCurrentSelection() != R.id.menu_7)
            result.setSelection(R.id.menu_7);
        ButterKnife.bind(this);
        setUpRecycler();
        //Can't add general_list_item items
        findViewById(R.id.floating_action_button).setOnClickListener(view -> addPreperation(null));
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
        clickObjs.addAll(new PreparationObj().bindings.keySet());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
        mAdapter = new DRAdapterBuilder<PreparationObj>()
                .setContext(this)
                .setData(DataSingleton.getPreparationObjs())
                .setRecyclerViewId(R.id.recycler_view)
                .setViewId(R.layout.general_list_item)
                .setEmptyViewId(R.id.empty_view)
                .setClickIds(clickObjs)
                .build();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.getSelfPositionClicks().subscribe(o ->addPreperation(o));
        mAdapter.getPositionClicks().subscribe(triplet -> {/*No buttons in settings*/});
    }

    private void addPreperation(Object o) {
        PreparationObj g = new PreparationObj();
        boolean isNew = true;
        if(o != null) {
            g = (PreparationObj) o;
            isNew = false;
        }
        View v = getLayoutInflater().inflate(R.layout.add_optional,null);
        TextInputEditText nameT = v.findViewById(R.id.name),
                            valueT = v.findViewById(R.id.value);
        if(!isNew){
            nameT.setText(g.key);
            valueT.setText(g.value);
        }
        PreparationObj finalG = g;
        boolean finalIsNew = isNew;
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this)
                .setMessage(String.format("%s a Preparation [min]", isNew ? "Add" : "Edit"))
                .setView(v)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                })
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    finalG.key.setLength(0);
                    finalG.key.append(nameT.getText().toString());
                    finalG.value.setLength(0);
                    finalG.value.append(valueT.getText().toString());
                    AsyncTask.execute(() ->{
                        if(finalIsNew)
                            DataSingleton.getDAO().insertAll(finalG);
                        else
                            DataSingleton.getDAO().updateAll(finalG);
                    });
                    if (finalIsNew)
                        DataSingleton.getPreparationObjs().add(finalG);
                    mAdapter.setData(DataSingleton.getPreparationObjs());
                });
        if(!isNew)
                b.setNeutralButton("Delete", (dialogInterface, i) -> {
                    AsyncTask.execute(() ->DataSingleton.getDAO().delete(finalG));
                    DataSingleton.getPreparationObjs().remove(finalG);
                    mAdapter.setData(DataSingleton.getPreparationObjs());
                });
        b.show();
    }
}
