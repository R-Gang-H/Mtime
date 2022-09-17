package com.mtime.adapter.render.base;

import android.view.View;

/**
 * Created by yinguanping on 16/3/25.
 */
@Deprecated
public class MRecyclerViewTypeExtraHolder extends BaseRecyclerViewHolder {

    public MRecyclerViewTypeExtraHolder(View itemView) {
        super(itemView);
    }

    /**
     * 保存当前position（list index，不包括headerView和footerView）
     */
    private int realItemPosition;

    public int getRealItemPosition() {
        return realItemPosition;
    }

    protected void setRealItemPosition(int realItemPosition) {
        this.realItemPosition = realItemPosition;
    }

}