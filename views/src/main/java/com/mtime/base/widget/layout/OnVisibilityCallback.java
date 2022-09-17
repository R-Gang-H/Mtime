package com.mtime.base.widget.layout;

/**
 * Created by Taurus on 2017/11/30.
 */

public abstract class OnVisibilityCallback implements OnVisibilityListener {

    private final Tag mTag;

    public OnVisibilityCallback(Tag tag){
        this.mTag = tag;
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    @Override
    public final void onAttachedToWindow() {
        onShow(mTag);
    }

    @Override
    public final void onDetachedFromWindow() {
        onHidden(mTag);
    }

    protected abstract void onShow(Tag tag);

    protected abstract void onHidden(Tag tag);

    public static class Tag{
        public int type;
        public int position;
        public Object data;

        @Override
        public String toString() {
            return "position : " + position + "," + "data : " + data.toString();
        }
    }
}
