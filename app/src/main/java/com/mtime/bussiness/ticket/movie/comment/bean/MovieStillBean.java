package com.mtime.bussiness.ticket.movie.comment.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-25
 */
public class MovieStillBean implements IObfuscateKeepAll, Parcelable {
    public String stillUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stillUrl);
    }

    public MovieStillBean() {
    }

    protected MovieStillBean(Parcel in) {
        this.stillUrl = in.readString();
    }

    public static final Parcelable.Creator<MovieStillBean> CREATOR = new Parcelable.Creator<MovieStillBean>() {
        @Override
        public MovieStillBean createFromParcel(Parcel source) {
            return new MovieStillBean(source);
        }

        @Override
        public MovieStillBean[] newArray(int size) {
            return new MovieStillBean[size];
        }
    };
}
