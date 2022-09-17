package com.mtime.bussiness.information.adapter;

import android.view.View;

import com.mtime.frame.BaseActivity;
import com.mtime.bussiness.information.bean.NewsCommentItemBean;
import com.mtime.bussiness.information.adapter.render.NewsCommentItemRender;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.BaseRecyclerViewRenderAdapter;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;

import java.util.List;

/**
 * 新闻评论列表页
 * Created by yinguanping on 16/7/5.
 */
public class NewsCommentListAdapter extends BaseRecyclerViewRenderAdapter {
    public interface OnRecyclerViewListener {//这里声明一个recycleView的item点击接口，以实现类似listview的onItemClick

        void onClickReply(String commentId);

    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public OnRecyclerViewListener getOnRecyclerViewListener() {
        return onRecyclerViewListener;
    }

    private final BaseActivity context;
    private final List<NewsCommentItemBean> list;

    public List<NewsCommentItemBean> getList() {
        return list;
    }

    public NewsCommentListAdapter(BaseActivity context, List<NewsCommentItemBean> list, View headerView, View footerView) {
        super(headerView, footerView);
        this.context = context;
        this.list = list;
    }

    @Override
    public BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> getAdapterTypeRenderExcludeExtraView(int type) {
        BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> render;
        render = new NewsCommentItemRender(context, this);
        return render;
    }

    @Override
    public int getItemCountExcludeExtraView() {
        return list.size();
    }

    @Override
    public int getItemViewTypeExcludeExtraView(int realItemPosition) {
        return realItemPosition;
    }


}
