package com.mtime.player.listplay;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

public abstract class BaseListPlayViewHolder extends RecyclerView.ViewHolder {

    public FrameLayout playerContainer;

    public BaseListPlayViewHolder(View itemView) {
        super(itemView);
        playerContainer = itemView.findViewById(getPlayerContainerId());
    }

    protected abstract int getPlayerContainerId();

}
