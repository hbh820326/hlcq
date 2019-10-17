package com.jcodecraeer.xrecyclerview;

import android.content.Context;
//import android.support.design.widget.AppBarLayout;
//import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

public class XRecyclerView extends RecyclerView {
    // private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;
    //private int mPageCount = 0;
    private boolean isLoadingData,isNoMore;
    private int mRefreshProgressStyle = ProgressStyle.SysProgress;
    private int mLoadingMoreProgressStyle = ProgressStyle.SysProgress;

    private WrapAdapter mWrapAdapter;
    private float mLastY = -1;
    private static final float DRAG_RATE = 3;
    private LoadingListener mLoadingListener;

    private boolean pullRefreshEnabled = true;//刷新
    private boolean loadingMoreEnabled = true;//加载更多

    //adapter没有数据的时候显示,类似于listView的emptyView
    private View mEmptyView;
    private ArrowRefreshHeader mRefreshHeader;
    private LoadingMoreFooter mFootView;

    //下面的ItemViewType是保留值(ReservedItemViewType),如果用户的adapter与它们重复将会强制抛出异常。不过为了简化,我们检测到重复时对用户的提示是ItemViewType必须小于10000
    private static final int TYPE_REFRESH_HEADER = 10000;//设置一个很大的数字,尽可能避免和用户的adapter冲突
    private static final int TYPE_FOOTER = 10001;
    private static final int HEADER_INIT_INDEX = 10002;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private static List<Integer> sHeaderTypes = new ArrayList<>();//每个header必须有不同的type,不然滚动的时候顺序会变化
    private static final int FOOT_INIT_INDEX = 9999;
    private ArrayList<View> mFootViews = new ArrayList<>();
    private static List<Integer> sFootTypes = new ArrayList<>();//每个header必须有不同的type,不然滚动的时候顺序会变化


    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @param listener 上拉和下拉监听
     */
    public void setLoadingListener(LoadingListener listener) {
        mLoadingListener = listener;
    }

    /**
     * 刷新，加载 回调
     */
    public interface LoadingListener {
        void onRefresh();//刷新
        void onLoadMore();//加载
    }

    /**
     * 初始化头视图和尾视图
     */
    private void init() {
            mRefreshHeader = new ArrowRefreshHeader(getContext());//拉刷新头
            mRefreshHeader.setProgressStyle(mRefreshProgressStyle);//拉刷新动画样式
            mFootView = new LoadingMoreFooter(getContext());//下拉加载尾
            mFootView.setProgressStyle(mLoadingMoreProgressStyle);//下拉加载动画样式
            mFootView.setVisibility(GONE);
    }

    /**
     * 加载完毕
     */
    public void loadMoreComplete() {
        isLoadingData = false;
        mFootView.setState(LoadingMoreFooter.STATE_COMPLETE);

    }

    /**
     * @param noMore 不再上拉加载 开关
     */
    public void setNoMore(boolean noMore) {
        isLoadingData = false;
        isNoMore = noMore;
        mFootView.setState(isNoMore ? LoadingMoreFooter.STATE_NOMORE : LoadingMoreFooter.STATE_COMPLETE);
    }

    /**
     * 刷新完成
     */
    public void refreshComplete() {
        mRefreshHeader.refreshComplete();//刷新完成
        setNoMore(false);//关闭   不再加载
    }

    /**
     * 复位
     */
    public void reset() {
        setNoMore(false);//关闭   不再加载
        loadMoreComplete();//加载完成
        refreshComplete();//刷新完成
    }

    /**
     * @param enabled 刷新开关
     */
    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    /**
     * @param enabled 加载开关
     */
    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
    }

    /**
     * @param style 刷新动画样式
     */
    public void setRefreshProgressStyle(int style) {
        mRefreshProgressStyle = style;
        if (mRefreshHeader != null) {
            mRefreshHeader.setProgressStyle(style);
        }
    }

    /**
     * @param style 加载动画样式
     */
    public void setLoadingMoreProgressStyle(int style) {
        mLoadingMoreProgressStyle = style;

        mFootView.setProgressStyle(style);

    }

    /**
     * @param resId 设置刷新箭头图片
     */
    public void setArrowImageView(int resId) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setArrowImageView(resId);
        }
    }

    /**
     * @param refreshing 代码控制刷新
     */
    public void setRefreshing(boolean refreshing) {
        if (refreshing && pullRefreshEnabled && mLoadingListener != null) {
            mRefreshHeader.setState(ArrowRefreshHeader.STATE_REFRESHING);
            mRefreshHeader.onMove(mRefreshHeader.getMeasuredHeight());
            mLoadingListener.onRefresh();
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount() && !isNoMore && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                isLoadingData = true;

                mFootView.setState(LoadingMoreFooter.STATE_LOADING);

                mLoadingListener.onLoadMore();
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled //&& appbarState == AppBarStateChangeListener.State.EXPANDED
                        ) {
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    if (mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled// && appbarState == AppBarStateChangeListener.State.EXPANDED
                        ) {
                    if (mRefreshHeader.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener.onRefresh();
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max=0;
        for (int value : lastPositions) {
            if (value > max)max = value;
        }
        return max;
    }
    private boolean isOnTop() {
        return mRefreshHeader.getParent() != null;
    }



    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {//视图观察者
        /**
         * 视图变化监听   （主要逻辑是监听是否为空，替换空视图）
         */
        @Override
        public void onChanged() {
            showEmpty();
        }

        /**
         *  范围插入监听
         * @param positionStart
         * @param itemCount
         */
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            showEmpty();
        }

        /**
         *  范围改变监听
         * @param positionStart
         * @param itemCount
         */
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            showEmpty();
        }

        /**
         * 范围改变监听
         * @param positionStart
         * @param itemCount
         * @param payload
         */
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            showEmpty();
        }

        /**
         * 范围删除监听
         * @param positionStart
         * @param itemCount
         */
        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            showEmpty();
        }

        /**
         * 范围移动监听
         * @param fromPosition
         * @param toPosition
         * @param itemCount
         */
        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            showEmpty();
        }
    };
    public void showEmpty() {
        if (mWrapAdapter != null && mEmptyView != null) {
            if (mWrapAdapter.adapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
                XRecyclerView.this.setVisibility(View.GONE);
            } else {
                if (mEmptyView.getVisibility() == View.VISIBLE) {
                    mEmptyView.setVisibility(View.GONE);
                    XRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    /**
     * @param emptyView 空视图
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
        mDataObserver.onChanged();
    }

    /**
     * @return 取空视图
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * 拦截适配器，用工厂模式改造
     *
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    /**
     * 添加头视图
     *
     * @param view
     */
    public void addHeaderView(View view) {
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size());
        mHeaderViews.add(view);
    }

    /**
     * 添加尾视图
     *
     * @param view
     */
    public void addFootView(View view) {
        sFootTypes.add(FOOT_INIT_INDEX - mFootViews.size());
        mFootViews.add(view);
    }

    /**
     * 判断一个type是否为HeaderType
     */
    private boolean isHeaderType(int itemViewType) {
        return mHeaderViews.size() > 0 && sHeaderTypes.contains(itemViewType);
    }

    /**
     * 判断一个type是否为FootType
     *
     * @param itemViewType
     * @return
     */
    private boolean isFootType(int itemViewType) {
        return mFootViews.size() > 0 && sFootTypes.contains(itemViewType);
    }

    /**
     * 根据header的ViewType取头视图
     */

    private View getHeaderViewByType(int itemType) {
        if (!isHeaderType(itemType)) {
            return null;
        }
        return mHeaderViews.get(itemType - HEADER_INIT_INDEX);
    }

    /**
     * 根据foot的ViewType取尾视图
     */
    private View getFootViewByType(int itemType) {
        if (isFootType(itemType)) {
            return mFootViews.get(FOOT_INIT_INDEX - itemType);
        }
        return null;
    }


    /**
     * 工厂模式 改造适配器
     */
    public class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public boolean isHeader(int position) {
            return position >= 1 && position < mHeaderViews.size() + 1;
        }

        public boolean isFootView(int position) {
            if (loadingMoreEnabled) {
                return position < getItemCount() - 1 && position > getItemCount() - 2 - getFootCount();
            } else {
                return position < getItemCount() && position > getItemCount() - getFootCount() - 1;
            }
        }

        public boolean isFooter(int position) {

            return loadingMoreEnabled && position == getItemCount() - 1;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootCount() {
            return mFootViews.size();
        }
//ok
        @Override
        public int getItemViewType(int position) {

            if (isRefreshHeader(position)) {

                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {

                position = position - 1;
                return sHeaderTypes.get(position);
            }
            if (isFootView(position)) {

                position = position - 1 - getHeadersCount() - adapter.getItemCount();
                return sFootTypes.get(position);
            }
            if (isFooter(position)) {

                return TYPE_FOOTER;
            }
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mRefreshHeader);
            } else if (isHeaderType(viewType)) {
                return new SimpleViewHolder(getHeaderViewByType(viewType));
            } else if (isFootType(viewType)) {
                return new SimpleViewHolder(getFootViewByType(viewType));
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootView);
            } else return adapter.onCreateViewHolder(parent, viewType);
        }
//ok
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (adapter != null) {
                int adjPosition = position - (getHeadersCount() + 1);
                if (adjPosition >= 0 && adjPosition < adapter.getItemCount()) {

                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }
//ok
        @Override
        public int getItemCount() {
            if (adapter != null) {
                if (loadingMoreEnabled) {

                    return getHeadersCount() + adapter.getItemCount() + getFootCount() + 2;
                } else {
                    return getHeadersCount() + getFootCount() + adapter.getItemCount() + 1;
                }
            }
            return 0;
        }


        /**ok
         * LayoutManager为GridLayoutManager时控制点的格数
         *
         * @param recyclerView
         */
        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {//占的格数，gridManager.getSpanCount()：一整行，1：1，2：2
                        return (isHeader(position) || isFooter(position) || isRefreshHeader(position) || isFootView(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        /**
         * LayoutManager为StaggeredGridLayoutManager时控制点的格数
         *
         * @param holder
         */
        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFootView(holder.getLayoutPosition()) || isRefreshHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);//占一整行
            }
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            adapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
            return adapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            adapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            adapter.registerAdapterDataObserver(observer);
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        //解决和CollapsingToolbarLayout冲突的问题
//        AppBarLayout appBarLayout = null;
//        ViewParent p = getParent();
//        while (p != null) {
//            if (p instanceof CoordinatorLayout) {
//                break;
//            }
//            p = p.getParent();
//        }
//        if(p instanceof CoordinatorLayout) {
//            CoordinatorLayout coordinatorLayout = (CoordinatorLayout)p;
//            final int childCount = coordinatorLayout.getChildCount();
//            for (int i = childCount - 1; i >= 0; i--) {
//                final View child = coordinatorLayout.getChildAt(i);
//                if(child instanceof AppBarLayout) {
//                    appBarLayout = (AppBarLayout)child;
//                    break;
//                }
//            }
//            if(appBarLayout != null) {
//                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
//                    @Override
//                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
//                        appbarState = state;
//                    }
//                });
//            }
//        }
//    }

}