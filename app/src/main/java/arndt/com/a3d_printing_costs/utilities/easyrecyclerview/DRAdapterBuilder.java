package arndt.com.a3d_printing_costs.utilities.easyrecyclerview;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import arndt.com.a3d_printing_costs.R;

public class DRAdapterBuilder<T extends DataModel> {
    private List<T> data = new ArrayList<>();
    private Integer viewId,recyclerViewId = R.id.recycler_view,emptyViewId = R.id.empty_view;
    private List<ClickObj> clickIds = new ArrayList<>();
    private Activity context;
    private View contextV;

    public List<T> getData() {
        return data;
    }

    public DRAdapterBuilder setData(List<T> data) {
        this.data = data;
        return this;
    }

    public int getViewId() {
        return viewId;
    }

    public DRAdapterBuilder setViewId(int viewId) {
        this.viewId = viewId;
        return this;
    }

    public int getRecyclerViewId() {
        return recyclerViewId;
    }

    public DRAdapterBuilder setRecyclerViewId(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
        return this;
    }

    public int getEmptyViewId() {
        return emptyViewId;
    }

    public DRAdapterBuilder setEmptyViewId(int emptyViewId) {
        this.emptyViewId = emptyViewId;
        return this;
    }

    public List<ClickObj> getClickIds() {
        return clickIds;
    }

    public DRAdapterBuilder setClickIds(List<ClickObj> clickIds) {
        this.clickIds = clickIds;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public DRAdapterBuilder setContext(View context) {
        this.contextV = context;
        return this;
    }
    public DRAdapterBuilder setContext(Activity context) {
        this.context = context;
        return this;
    }

    public DRAdapter<T> build(){
        if((context == null && contextV == null) || viewId == null)
            throw new IllegalArgumentException("context and viewId are required");
        if(context == null)
            return new DRAdapter<>(contextV, recyclerViewId.intValue(), data, viewId.intValue(), clickIds, emptyViewId.intValue());
        else
            return new DRAdapter<>(context, recyclerViewId.intValue(), data, viewId.intValue(), clickIds, emptyViewId.intValue());
    }
}
