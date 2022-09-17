package com.mtime.bussiness.mine.adapter;

import android.view.View;

import com.mtime.frame.BaseActivity;
import com.mtime.bussiness.mine.adapter.render.ArticalRankListAdapterRender;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.BaseRecyclerViewRenderAdapter;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;
import com.mtime.bussiness.mine.bean.OfficialAccountListBean;


/**
 * Created by wangdaban on 17/5/25.
 */

public class ArticleRankListAdapter extends BaseRecyclerViewRenderAdapter {
    private final BaseActivity context;
    private final OfficialAccountListBean.ListBeanX bean;

    public ArticleRankListAdapter(BaseActivity context, OfficialAccountListBean.ListBeanX bean, View headerView) {
        super(headerView, null);
        this.context = context;
        this.bean = bean;
    }

    @Override
    public BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> getAdapterTypeRenderExcludeExtraView(int type) {
        return new ArticalRankListAdapterRender(context, bean, getItemCountExcludeExtraView());
    }

    @Override
    public int getItemCountExcludeExtraView() {
        if (bean.getTopList().getRelatedType() == 1) {
            return bean.getTopList().getList().size() + 1;
        } else if (bean.getTopList().getRelatedType() == 2) {
            return bean.getTopList().getList().size() + 1;
        }
        return 0;
    }

    @Override
    public int getItemViewTypeExcludeExtraView(int realItemPosition) {
        return realItemPosition;
    }
}
