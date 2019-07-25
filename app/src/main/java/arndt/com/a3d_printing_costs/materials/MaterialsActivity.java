package arndt.com.a3d_printing_costs.materials;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.printers.PrinterObj;
import arndt.com.a3d_printing_costs.utilities.MenuCreator;
import arndt.com.a3d_printing_costs.utilities.Mutable;
import arndt.com.a3d_printing_costs.utilities.Pair;
import arndt.com.a3d_printing_costs.utilities.Triplet;
import arndt.com.a3d_printing_costs.utilities.Utility;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.ClickObj;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRAdapter;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRAdapterBuilder;
import arndt.com.a3d_printing_costs.utilities.easyrecyclerview.DRViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MaterialsActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view) public RecyclerView mRecyclerView;
    private DRAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        result = MenuCreator.getDrawer(this, getResources().getString(R.string.materials));
        if(result.getCurrentSelection() != R.id.menu_2)
            result.setSelection(R.id.menu_2);
        ButterKnife.bind(this);
        setUpRecycler();
        findViewById(R.id.floating_action_button).setOnClickListener(view -> addMaterial());
    }

    private void addMaterial(MaterialObj... materialObjs) {
        boolean isNewMaterial = true;
        if(materialObjs != null && materialObjs.length > 0 && materialObjs[0] != null)
            isNewMaterial = false;
        final View v = getLayoutInflater().inflate(R.layout.add_item, null);
        MaterialObj p = new MaterialObj();
        if(!isNewMaterial)
            p = materialObjs[0];
        int j = 0;
        for(Map.Entry<String, Pair<String, Object>> values : p.getNameValues().entrySet()) {
            TextInputEditText f = v.findViewById(Utility.getResId("title" + j,R.id.class));
            TextInputLayout ff = v.findViewById(Utility.getResId("textinputlayout" + (j++),R.id.class));
//                f.setHint(values.getKey()+values.getValue().getKey());
            ff.setHint(values.getKey()+values.getValue().getKey());
            if(!isNewMaterial)
                f.setText(values.getValue().getValue().toString());
            if(values.getValue().getValue() instanceof Mutable)
                f.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL );
        }
        MaterialObj finalP = p;
        boolean finalIsNewPrinter = isNewMaterial;
        new MaterialAlertDialogBuilder(this)
                .setTitle(String.format("%s Material", isNewMaterial ? "Add a new":"Edit a"))
                .setView(v)
                .setNegativeButton("CANCEL",(dialogInterface, i) -> {})
                .setPositiveButton("SAVE",(dialogInterface, i) -> {
                    int k = 0;
                    for(Map.Entry<String, Pair<String, Object>> values : finalP.getNameValues().entrySet()) {
                        String s = ((TextInputEditText) v.findViewById(Utility.getResId("title" + (k++),R.id.class)))
                                .getText().toString();
                        if(values.getValue().getValue() instanceof StringBuilder) {
                            ((StringBuilder) values.getValue().getValue()).setLength(0);
                            ((StringBuilder) values.getValue().getValue()).append(s);
                        }else if(values.getValue().getValue() instanceof Mutable)
                            try {
                                ((Mutable) values.getValue().getValue()).setB(
                                        Double.parseDouble(s)
                                );
                            }catch (Exception e){
                                ((Mutable) values.getValue().getValue()).setB(0d);
                            }
                    }
                    if(finalIsNewPrinter)
                        DataSingleton.getMaterialObjs().add(finalP);
                    AsyncTask.execute(() -> {
                        if(finalIsNewPrinter) {
                            DataSingleton.getDAO().insertAll(finalP);
                        }else
                            DataSingleton.getDAO().updateAll(finalP);
                    });
                    mAdapter.setData(DataSingleton.getPrinterObjs());
                    mAdapter.notifyDataSetChanged();
                })
                .show();
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
        clickObjs.add(new ClickObj<>(R.id.delete));
        clickObjs.addAll(new MaterialObj().bindings.keySet());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
        mAdapter = new DRAdapterBuilder<MaterialObj>()
                .setContext(this)
                .setData(DataSingleton.getMaterialObjs())
                .setRecyclerViewId(R.id.recycler_view)
                .setViewId(R.layout.list_items)
                .setEmptyViewId(R.id.empty_view)
                .setClickIds(clickObjs)
                .build();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.getSelfPositionClicks().subscribe(o ->addMaterial((MaterialObj) o));
        mAdapter.getPositionClicks().subscribe(triplet -> {
            Triplet<DRViewHolder, ClickObj, Integer> o = (Triplet<DRViewHolder, ClickObj, Integer>) triplet;
            switch (o.getSecond().getId().intValue()){
                case R.id.delete:
                    MaterialObj v = DataSingleton.getMaterialObjs().get(o.getThird().intValue());
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Delete Material")
                            .setMessage("Are You sure you want to delete this material: "+
                                    v.manufacturer.toString()+"?")
                            .setPositiveButton("YES",(dialogInterface, i) -> {
                                DataSingleton.getMaterialObjs().remove(o.getThird().intValue());
                                mAdapter.setData(DataSingleton.getMaterialObjs());
                                AsyncTask.execute(() -> DataSingleton.getDAO().delete(v));
                            })
                            .setNegativeButton("NO",(dialogInterface, i) -> {})
                            .show();
                    break;
            }
        });
    }
}
