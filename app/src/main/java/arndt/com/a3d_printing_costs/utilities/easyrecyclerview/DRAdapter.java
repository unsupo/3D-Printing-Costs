package arndt.com.a3d_printing_costs.utilities.easyrecyclerview;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import arndt.com.a3d_printing_costs.R;
import arndt.com.a3d_printing_costs.dao.DataSingleton;
import arndt.com.a3d_printing_costs.general.GeneralObj;
import arndt.com.a3d_printing_costs.utilities.Mutable;
import arndt.com.a3d_printing_costs.utilities.Triplet;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static arndt.com.a3d_printing_costs.dao.DataSingleton.MONEY_UNIT;

public class DRAdapter<T extends DataModel> extends RecyclerView.Adapter<DRViewHolder> {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private List<T> data = new ArrayList<>();
    private int viewId,recyclerViewId,emptyViewId;
    private List<ClickObj> clickIds;
    private final PublishSubject<T> onSelfClickSubject = PublishSubject.create();
    private final PublishSubject<Triplet<DRViewHolder, ClickObj, Integer>> onClickSubjects = PublishSubject.create();
    DRViewHolder holder;
    private Activity context;
    private View contextV;

    /**
     * Use this if you want to create a quick listview
     * @param context this is the activity running this whole ting
     * @param data this is the list of data with each item having a list of sub data
     * @param viewId this is the view id of the list item to be inflated
     * @param clickIds this is a list of view ids that you want to subscribe to the click events
     */
    public DRAdapter(Activity context, int recyclerViewId, List<T> data, int viewId, List<ClickObj> clickIds, int emptyViewId) {
        this.context = context;
        init(recyclerViewId,data,viewId,clickIds,emptyViewId);
    }public DRAdapter(View context, int recyclerViewId, List<T> data, int viewId, List<ClickObj> clickIds, int emptyViewId) {
        this.contextV = context;
        init(recyclerViewId,data,viewId,clickIds,emptyViewId);
    }
    private void init(int recyclerViewId, List<T> data, int viewId, List<ClickObj> clickIds, int emptyViewId){
        this.viewId = viewId;
        this.clickIds = clickIds;
        this.recyclerViewId = recyclerViewId;
        this.emptyViewId = emptyViewId;
        if(context == null) {
            this.recyclerView = contextV.findViewById(recyclerViewId);
            this.emptyView = contextV.findViewById(emptyViewId);
            if(this.recyclerView == null)
                throw new IllegalArgumentException("Can't find recycler view: "+recyclerViewId);
            if(this.emptyView == null)
                throw new IllegalArgumentException("Can't find empty view: "+ emptyViewId);
        }else {
            this.recyclerView = context.findViewById(recyclerViewId);
            this.emptyView = context.findViewById(emptyViewId);
            if(this.recyclerView == null)
                throw new IllegalArgumentException("Can't find recycler view: "+ recyclerViewId);
            if(this.emptyView == null)
                throw new IllegalArgumentException("Can't find empty view: "+emptyViewId);
        }
        setData(data);
    }

    public DRViewHolder getHolder() {
        return holder;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
        if(!this.data.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public DRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(context == null)
            view = LayoutInflater.from(contextV.getContext())
                    .inflate(viewId,parent,false);
        else
            view = LayoutInflater.from(context)
                    .inflate(viewId,parent,false);
        holder = new DRViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DRViewHolder holder, int position) {
        final T dm = data.get(position);
        holder.itemView.setOnClickListener(v -> onSelfClickSubject.onNext(dm));
        for(ClickObj clickObj : clickIds) {
            int id = clickObj.getId().intValue();
            ClickObj cobj = new ClickObj(id);
            View view = holder.itemView.findViewById(id);
            if(clickObj.getSubId() != null) {
                view = view.findViewById(clickObj.getSubId().intValue());
                cobj = new ClickObj(id,clickObj.getSubId());
            }
            if(clickObj.isClickable())
                view.setOnClickListener(v -> onClickSubjects.onNext(new Triplet<>(holder, clickObj, position)));
            if(!dm.getBindings().containsKey(cobj))
                continue; //this id will not get bound
            if(dm.getBindings().get(cobj) == null) {
                ((LinearLayoutCompat)view.getParent()).setVisibility(View.GONE);
                continue;
            }if(view instanceof SwitchMaterial)
                ((SwitchMaterial)view).setChecked(((Mutable<Boolean>)dm.getBindings().get(cobj)).getB());
//            else if(view instanceof MaterialButton)
//                if(context == null)
//                    view.setBackgroundTintList(ContextCompat
//                            .getColorStateList(contextV.getContext(), ((Mutable<Boolean>)dm.getBindings().get(id)).getB() ? R.color.myCustomColor: R.color.default_day));
//                else
//                    view.setBackgroundTintList(ContextCompat
//                            .getColorStateList(context, ((Mutable<Boolean>)dm.getBindings().get(id)).getB() ? R.color.myCustomColor: R.color.default_day));
            else if(view instanceof TextView)
                ((TextView)view).setText(dm.getBindings().get(cobj).toString());
        }
    }

    /**
     * This is the object to subscribe to for clicks on each individual reyclerview item
     * @return observable of item clicked returning it's data model
     */
    public Observable<T> getSelfPositionClicks(){
        return onSelfClickSubject.hide();
    }

    /**
     * This is the object to subscribe to for clicks on each item you passed into the
     * constructor as clickIds
     * @return A triplet of the view holder, the view id, the data model's position in the list
     */
    public Observable<Triplet<DRViewHolder, ClickObj, Integer>> getPositionClicks(){
        return onClickSubjects.hide();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        onClickSubjects.onComplete();
        onSelfClickSubject.onComplete();
    }
}
