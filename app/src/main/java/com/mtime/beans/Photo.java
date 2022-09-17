package com.mtime.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class Photo extends MBaseBean implements Serializable, Parcelable {

    private static final long serialVersionUID = -3354764498594138703L;

    public String id; // 影片图片id/影人图片id/电视剧id
    public String image; // 图片地址
    public int type; // 类型：
    // 影片：海报和剧照
    // 影人：写真和生活照

    public Photo() {
    }

    public Photo(final String id, final String image, final int type) {
        this.id = id;
        this.image = image;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.image);
        dest.writeInt(this.type);
    }

    protected Photo(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
