package com.mtime.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * Created by guangshun on 15-6-23.
 */
public class ImageBean extends MBaseBean implements Serializable, Parcelable {

    public String parentName;
    public long size;
    public String displayName;
    public String path;
    public boolean isChecked;

    public ImageBean() {
        super();
    }

    public ImageBean(String path) {
        super();
        this.path = path;
    }

    public ImageBean(String parentName, long size, String displayName,
                     String path, boolean isChecked) {
        super();
        this.parentName = parentName;
        this.size = size;
        this.displayName = displayName;
        this.path = path;
        this.isChecked = isChecked;
    }

    /**
     * 图片的路径相同就认为是同一张图片
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageBean)) {
            return false;
        }
        ImageBean other = (ImageBean) o;
        return this.path.equals(other.path);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.parentName);
        dest.writeLong(this.size);
        dest.writeString(this.displayName);
        dest.writeString(this.path);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected ImageBean(Parcel in) {
        this.parentName = in.readString();
        this.size = in.readLong();
        this.displayName = in.readString();
        this.path = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };
}
