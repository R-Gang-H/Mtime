package com.mtime.bussiness.video.event;

/**
 * Created by JiaJunHui on 2018/6/23.
 */
public class PlayEvent {

    public int categoryType;
    public int index;

    public PlayEvent() {
    }

    public PlayEvent(int categoryType, int index) {
        this.categoryType = categoryType;
        this.index = index;
    }
}
