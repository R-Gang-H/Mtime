package com.mtime.bussiness.video.event;

/**
 * Created by mtime on 2017/12/14.
 */

public class FollowStateEditEvent {

    private long publicId;
    private boolean follow;

    public FollowStateEditEvent(long publicId, boolean follow) {
        this.publicId = publicId;
        this.follow = follow;
    }

    public long getPublicId() {
        return publicId;
    }

    public void setPublicId(long publicId) {
        this.publicId = publicId;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
