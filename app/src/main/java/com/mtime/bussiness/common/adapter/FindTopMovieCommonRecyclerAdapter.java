package com.mtime.bussiness.common.adapter;

import android.view.View;

import com.mtime.frame.BaseActivity;
import com.mtime.bussiness.common.adapter.render.FindTopMovieDetailRender;
import com.mtime.bussiness.common.adapter.render.FindTopPersonDetailRender;
import com.mtime.bussiness.common.bean.TopMovieDetailBean;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.BaseRecyclerViewRenderAdapter;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;

/**
 * 榜单列表页common adapter
 * Created by yinguanping on 16/7/4.
 */

public class FindTopMovieCommonRecyclerAdapter extends BaseRecyclerViewRenderAdapter {
    public interface OnRecyclerViewListener {//这里声明一个recycleView的item点击接口，以实现类似listview的onItemClick

        //,其他各item种的事件单独在每个item的render中添加
        void onItemClick(int position);

    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public OnRecyclerViewListener getOnRecyclerViewListener() {
        return onRecyclerViewListener;
    }

    private final BaseActivity context;
    private final TopMovieDetailBean bean;

    public TopMovieDetailBean getBean() {
        return bean;
    }

    public FindTopMovieCommonRecyclerAdapter(BaseActivity context, TopMovieDetailBean bean, View headerView, View footerView) {
        super(headerView, footerView);
        this.context = context;
        this.bean = bean;
    }

    @Override
    public BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> getAdapterTypeRenderExcludeExtraView(int type) {
        BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> render = null;
        switch (type) {
            case 1:
                render = new FindTopMovieDetailRender(context, this);
                break;
            case 2:
                render = new FindTopPersonDetailRender(context, this);
                break;
            default:
                break;
        }
        return render;
    }

    @Override
    public int getItemCountExcludeExtraView() {
        int size = 0;
        switch (bean.getType()) {
            case 1:
                size = bean.getMovies().size();
                break;
            case 2:
                size = bean.getPersons().size();
                break;
            default:
                break;
        }
        return size;
    }

    @Override
    public int getItemViewTypeExcludeExtraView(int realItemPosition) {
        return bean.getType();
    }
}
