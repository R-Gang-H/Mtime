package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * 影人列表控件
 * 
 * @author x 2013-2-6 下午3:12:43
 */
@Deprecated
public class MovieStarsListView extends ExpandableListView implements OnScrollListener, OnGroupClickListener {
    private boolean canCloseChild = false;
    
    public MovieStarsListView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        registerListener();
    }
    
    public MovieStarsListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        registerListener();
    }
    
    public MovieStarsListView(final Context context) {
        super(context);
        registerListener();
    }
    
    public void setCanCloseChild(final boolean canclosechild) {
        canCloseChild = true;
    }
    
    /**
     * HomeHeadHotShowingFilmListAdapter 接口 . 列表必须实现此接口 .
     */
    public interface HeaderAdapter {
        int PINNED_HEADER_GONE      = 0;
        int PINNED_HEADER_VISIBLE   = 1;
        int PINNED_HEADER_PUSHED_UP = 2;
        
        /**
         * 获取 Header 的状态
         * 
         * @param groupPosition
         * @param childPosition
         * @return 
         *         PINNED_HEADER_GONE,PINNED_HEADER_VISIBLE,PINNED_HEADER_PUSHED_UP
         *         其中之一
         */
        int getHeaderState(int groupPosition, int childPosition);
        
        /**
         * 配置 Header, 让 Header 知道显示的内容
         * 
         * @param header
         * @param groupPosition
         * @param childPosition
         * @param alpha
         */
        void configureHeader(View header, int groupPosition, int childPosition, int alpha);
        
        /**
         * 设置组按下的状态
         * 
         * @param groupPosition
         * @param status
         */
        void setGroupClickStatus(int groupPosition, int status);
        
        /**
         * 获取组按下的状态
         * 
         * @param groupPosition
         * @return
         */
        int getGroupClickStatus(int groupPosition);
        
    }
    
    private static final int MAX_ALPHA = 255;
    
    private HeaderAdapter    mAdapter;
    
    /**
     * 用于在列表头显示的 View,mHeaderViewVisible 为 true 才可见
     */
    private View             mHeaderView;
    
    /**
     * 列表头是否可见
     */
    private boolean          mHeaderViewVisible;
    
    private int              mHeaderViewWidth;
    
    private int              mHeaderViewHeight;
    
    public void setHeaderView(final View view) {
        mHeaderView = view;
        final AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        
        requestLayout();
    }
    
    private void registerListener() {
        setOnScrollListener(this);
    }
    
    private float mDownX;
    private float mDownY;
    
    /**
     * 如果 HeaderView 是可见的 , 此函数用于判断是否点击了 HeaderView, 并对做相应的处理 , 因为 HeaderView
     * 是画上去的 , 所以设置事件监听是无效的 , 只有自行控制 .
     */
    /**
     * 
     * 点击 HeaderView 触发的事件
     */
    
    private void headerViewClick() {
        
        final long packedPosition = getExpandableListPosition(getFirstVisiblePosition());
        
        final int groupPosition = ExpandableListView
        
        .getPackedPositionGroup(packedPosition);
        
        collapseGroup(groupPosition);
        
        mAdapter.setGroupClickStatus(groupPosition, 0);
        
        // 没设置这个会出现一些奇怪的问题 , 暂时不知道为什么
        
        setSelectedGroup(groupPosition);
        
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        if (mHeaderViewVisible) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    if ((mDownX <= mHeaderViewWidth) && (mDownY <= mHeaderViewHeight)) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    final float x = ev.getX();
                    final float y = ev.getY();
                    final float offsetX = Math.abs(x - mDownX);
                    final float offsetY = Math.abs(y - mDownY);
                    // 如果 HeaderView 是可见的 , 点击在 HeaderView 内 , 那么触发
                    // headerClick()
                    if ((x <= mHeaderViewWidth) && (y <= mHeaderViewHeight) && (offsetX <= mHeaderViewWidth)
                            && (offsetY <= mHeaderViewHeight)) {
                        
                        if ((mHeaderView != null) && canCloseChild) {
                            headerViewClick();
                        }
                        
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        
        return super.onTouchEvent(ev);
        
    }
    
    @Override
    public void setAdapter(final ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (HeaderAdapter) adapter;
    }
    
    /**
     * 
     * 点击了 Group 触发的事件
     */
    
    @Override
    public boolean onGroupClick(final ExpandableListView parent, final View v, final int groupPosition, final long id) {
        // 返回 true 才可以弹回第一行 , 不知道为什么
        return true;
        
    }
    
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }
    
    private int mOldState = -1;
    
    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
        final int groupPos = ExpandableListView.getPackedPositionGroup(flatPostion);
        final int childPos = ExpandableListView.getPackedPositionChild(flatPostion);
        if (mAdapter == null) {
            return;
        }
        final int state = mAdapter.getHeaderState(groupPos, childPos);
        if ((mHeaderView != null) && (mAdapter != null) && (state != mOldState)) {
            mOldState = state;
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
        }
        
        configureHeaderView(groupPos, childPos);
    }
    
    public void configureHeaderView(final int groupPosition, final int childPosition) {
        if ((mHeaderView == null) || (mAdapter == null) || (((ExpandableListAdapter) mAdapter).getGroupCount() == 0)) {
            return;
        }
        
        final int state = mAdapter.getHeaderState(groupPosition, childPosition);
        
        switch (state) {
            case HeaderAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }
            
            case HeaderAdapter.PINNED_HEADER_VISIBLE: {
                mAdapter.configureHeader(mHeaderView, groupPosition, childPosition, MovieStarsListView.MAX_ALPHA);
                
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                
                mHeaderViewVisible = true;
                
                break;
            }
            
            case HeaderAdapter.PINNED_HEADER_PUSHED_UP: {
                final View firstView = getChildAt(0);
                final int bottom = firstView.getBottom();
                
                // intitemHeight = firstView.getHeight();
                final int headerHeight = mHeaderView.getHeight();
                
                int y;
                
                int alpha;
                
                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                    alpha = (MovieStarsListView.MAX_ALPHA * (headerHeight + y)) / headerHeight;
                }
                else {
                    y = 0;
                    alpha = MovieStarsListView.MAX_ALPHA;
                }
                
                mAdapter.configureHeader(mHeaderView, groupPosition, childPosition, alpha);
                
                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }
                
                mHeaderViewVisible = true;
                break;
            }
        }
    }
    
    /**
     * 列表界面更新时调用该方法(如滚动时)
     */
    @Override
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            // 分组栏是直接绘制到界面中，而不是加入到ViewGroup中
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }
    
    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
            final int totalItemCount) {
        final long flatPos = getExpandableListPosition(firstVisibleItem);
        final int groupPosition = ExpandableListView.getPackedPositionGroup(flatPos);
        final int childPosition = ExpandableListView.getPackedPositionChild(flatPos);
        
        configureHeaderView(groupPosition, childPosition);
    }
    
    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
    }
}
