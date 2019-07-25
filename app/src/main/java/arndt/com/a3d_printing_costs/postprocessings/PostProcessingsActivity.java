package arndt.com.a3d_printing_costs.postprocessings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

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

public class PostProcessingsActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view) public RecyclerView mRecyclerView;
    private DRAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.post_processing));
        if(result.getCurrentSelection() != R.id.menu_6)
            result.setSelection(R.id.menu_6);
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
        clickObjs.addAll(new PostProcessingsObj().bindings.keySet());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
        mAdapter = new DRAdapterBuilder<PostProcessingsObj>()
                .setContext(this)
                .setData(DataSingleton.getPostProcessingsObjs())
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
        PostProcessingsObj g = new PostProcessingsObj();
        boolean isNew = true;
        if(o != null) {
            g = (PostProcessingsObj) o;
            isNew = false;
        }
        View v = getLayoutInflater().inflate(R.layout.add_optional,null);
        TextInputEditText nameT = v.findViewById(R.id.name),
                            valueT = v.findViewById(R.id.value);
        if(!isNew){
            nameT.setText(g.key);
            valueT.setText(g.value);
        }
        PostProcessingsObj finalG = g;
        boolean finalIsNew = isNew;
        MaterialAlertDialogBuilder b = new MaterialAlertDialogBuilder(this)
                .setMessage(String.format("%s a Post-Processing [min]", isNew ? "Add" : "Edit"))
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
                        DataSingleton.getPostProcessingsObjs().add(finalG);
                    mAdapter.setData(DataSingleton.getPostProcessingsObjs());
                    mAdapter.notifyDataSetChanged();
                });
        if(!isNew)
            b.setNeutralButton("Delete", (dialogInterface, i) -> {
                AsyncTask.execute(() ->DataSingleton.getDAO().delete(finalG));
                DataSingleton.getPostProcessingsObjs().remove(finalG);
                mAdapter.setData(DataSingleton.getPostProcessingsObjs());
            });
        b.show();
    }
}
