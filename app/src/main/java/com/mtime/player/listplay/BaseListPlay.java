package com.mtime.player.listplay;

import android.content.Context;
import android.content.res.Configuration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

public abstract class BaseListPlay<T> {

    protected Context mContext;
    protected RecyclerView mRecycler;
    protected T mData;

    protected int mScreenH;
    private boolean isLandScape;

    public BaseListPlay(Context context, RecyclerView recycler){
        this.mContext = context;
        this.mRecycler = recycler;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mScreenH = displayMetrics.heightPixels;
    }

    public void setData(T data){
        this.mData = data;
    }

    public abstract void play(int position);

    public void onConfigChanged(Configuration configuration){
        isLandScape = configuration.orientation== Configuration.ORIENTATION_LANDSCAPE;
    }

    protected final boolean isLandScape(){
        return isLandScape;
    }

    protected abstract void attachItemHolder(BaseListPlayViewHolder itemHolder);

    protected abstract void playItemHolder(int position);

    /**
     * 获取Item中渲染视图的可见高度
     * @param position
     * @return
     */
    protected int getItemVisibleRectHeight(int position){
        BaseListPlayViewHolder itemHolder = getItemHolder(position);
        if(itemHolder==null)
            return 0;
        int[] location = new int[2];
        itemHolder.playerContainer.getLocationOnScreen(location);
        int height = itemHolder.playerContainer.getHeight();

        int visibleRect;
        if(location[1] <= getRecyclerViewLocationTopStartOffset()){
            visibleRect = location[1] - getRecyclerViewLocationTopStartOffset() + height;
        }else{
            if(location[1] + height >= mScreenH){
                visibleRect = mScreenH - location[1];
            }else{
                visibleRect = height;
            }
        }
        return visibleRect;
    }

    /**
     * 获取两个索引条目中渲染视图可见高度最大的条目
     * @param position1
     * @param position2
     * @return
     */
    protected int getVisibleRectMaxPosition(int position1, int position2){
        BaseListPlayViewHolder itemHolder1 = getItemHolder(position1);
        BaseListPlayViewHolder itemHolder2 = getItemHolder(position2);
        if(itemHolder1==null && itemHolder2==null){
            return RecyclerView.NO_POSITION;
        }
        if(itemHolder1==null){
            return position2;
        }
        if(itemHolder2==null){
            return position1;
        }
        int visibleRect1 = getItemVisibleRectHeight(position1);
        int visibleRect2 = getItemVisibleRectHeight(position2);
        return visibleRect1 >= visibleRect2?position1:position2;
    }

    /**
     * 可见高度超出一半认为是可播放状态
     * @param position
     * @return
     */
    protected boolean isVisibleRectAvailablePlay(int position){
        BaseListPlayViewHolder itemHolder = getItemHolder(position);
        if(itemHolder==null)
            return false;
        int height = itemHolder.playerContainer.getHeight();
        return getItemVisibleRectHeight(position) > availablePlayHeight(height);
    }

    protected int availablePlayHeight(int itemHeight){
        return itemHeight/2;
    }

    protected boolean isCompleteVisibleRect(int position){
        BaseListPlayViewHolder itemHolder = getItemHolder(position);
        if(itemHolder==null)
            return false;
        int height = itemHolder.playerContainer.getHeight();
        return getItemVisibleRectHeight(position) == height;
    }

    protected boolean isPositionVisibleInList(int position){
        return position >= getFirstVisiblePosition() && position <= getLastVisiblePosition();
    }

    protected final RecyclerView.LayoutManager getLayoutManager(){
        return mRecycler.getLayoutManager();
    }

    private LinearLayoutManager getLinearLayoutManager(){
        return (LinearLayoutManager) getLayoutManager();
    }

    protected int getFirstCompleteVisiblePosition(){
        return getLinearLayoutManager().findFirstCompletelyVisibleItemPosition();
    }

    protected int getLastCompleteVisiblePosition() {
        return getLinearLayoutManager().findLastCompletelyVisibleItemPosition();
    }

    protected int getFirstVisiblePosition(){
        return getLinearLayoutManager().findFirstVisibleItemPosition();
    }

    protected int getLastVisiblePosition(){
        return getLinearLayoutManager().findLastVisibleItemPosition();
    }

    protected abstract int getRecyclerViewLocationTopStartOffset();

    protected BaseListPlayViewHolder getItemHolder(int position){
        RecyclerView.ViewHolder viewHolder = mRecycler.findViewHolderForLayoutPosition(position);
        if(viewHolder!=null){
            return (BaseListPlayViewHolder) viewHolder;
        }
        return null;
    }

    protected final boolean isLegalPosition(int position){
        int size = getItemCount();
        return position >= 0 && position <= size - 1;
    }

    protected abstract int getItemCount();

    public abstract void destroy();

}
