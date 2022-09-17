package com.mtime.bussiness.common.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * create by lushan on 2020/10/12
 * description:
 */
public class DeleteImage  extends MBaseBean {
    private long imageId;
    private long albumId;

    public DeleteImage(long imageId, long albumId) {
        this.imageId = imageId;
        this.albumId = albumId;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
