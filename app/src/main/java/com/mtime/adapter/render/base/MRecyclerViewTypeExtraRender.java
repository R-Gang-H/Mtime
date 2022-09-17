package com.mtime.adapter.render.base;

import android.view.View;

/**
 * 带有header或者footer的view
 * Created by yinguanping on 16/3/25.
 */
@Deprecated
public class MRecyclerViewTypeExtraRender implements BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> {

    protected MRecyclerViewTypeExtraHolder holder;

    protected MRecyclerViewTypeExtraRender(View extraView) {
        holder = new MRecyclerViewTypeExtraHolder(extraView);
    }

    @Override
    public MRecyclerViewTypeExtraHolder getReusableComponent() {
        return holder;
    }

    @Override
    public void fitEvents() {

    }

    @Override
    public void fitDatas(int position) {

    }
}