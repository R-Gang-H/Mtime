package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class PhotoAll extends MBaseBean {

    public List<PhotoType> imageTypes;
    public List<Photo> images;

    public void setImageTypes(final List<PhotoType> imageTypes) {
        this.imageTypes = imageTypes;
    }

    public List<PhotoType> getImageTypes() {
        return this.imageTypes;
    }

    public void setImages(final List<Photo> images) {
        this.images = images;
    }

    public List<Photo> getImages() {
        return this.images;
    }

}
