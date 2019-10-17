package com.jcodecraeer.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    public abstract void convert(BaseRecyclerHolder holder, T item);
    View.OnClickListener click;
    View.OnLongClickListener logClick;
    private List<T> list;//数据源
    public int itemLayoutId;//布局id
    public BaseRecyclerAdapter(List<T> list, int itemLayoutId,View.OnClickListener click, View.OnLongClickListener logClick) {
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        this.click=click;
        this.logClick=logClick;
    }
    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(parent.getContext(), itemLayoutId, null);
        if(click!=null)itemView.setOnClickListener(click);
        if(logClick!=null)itemView.setOnLongClickListener(logClick);
        return new BaseRecyclerHolder(itemView);
    }
    @Override
    public void onBindViewHolder(BaseRecyclerHolder holder, int position) {
        convert(holder, list.get(position));
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }


}