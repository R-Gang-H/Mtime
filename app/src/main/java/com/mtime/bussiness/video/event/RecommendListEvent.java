package com.mtime.bussiness.video.event;

/**
 * Created by JiaJunHui on 2018/4/3.
 * 请求显示相关视频列表
 */
public class RecommendListEvent {

    private final boolean show;

    public RecommendListEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }
}
