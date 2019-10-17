package com.jcodecraeer.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class BaseRecyclerHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views = new SparseArray<>();//默认大小为10
    public BaseRecyclerHolder(View itemView) {
        super(itemView);
}
public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
        view = itemView.findViewById(viewId);
        views.put(viewId, view);
        }
        return (T) view;
        }
        }