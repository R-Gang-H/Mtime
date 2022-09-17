package com.mtime.bussiness.daily.recommend.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-22
 * <p>
 * 基础类
 */
public class DailyRecommendBean extends MBaseBean implements Parcelable {

    public long rcmdId; // 每日弹窗id
    public String movieId; // 电影id
    public String poster; // 海报
    public String rcmdQuote; // 推荐语录
    public String desc; // 副推荐语，后台配置的 （显示在推荐语下面）
    public String dailyMovieTime; // "6-21"
    public String playStatus; // 播放状态（是否可播放 1：不可播放  2可播放）
    public String movieDetailURL; // 二维码 url

    public boolean canPlay() {
        return !TextUtils.isEmpty(playStatus) && playStatus.equals("2");
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(movieId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.rcmdId);
        dest.writeString(this.movieId);
        dest.writeString(this.poster);
        dest.writeString(this.rcmdQuote);
        dest.writeString(this.desc);
        dest.writeString(this.dailyMovieTime);
        dest.writeString(this.playStatus);
        dest.writeString(this.movieDetailURL);
    }

    public DailyRecommendBean() {
    }

    protected DailyRecommendBean(Parcel in) {
        this.rcmdId = in.readLong();
        this.movieId = in.readString();
        this.poster = in.readString();
        this.rcmdQuote = in.readString();
        this.desc = in.readString();
        this.dailyMovieTime = in.readString();
        this.playStatus = in.readString();
        this.movieDetailURL = in.readString();
    }

    public static final Parcelable.Creator<DailyRecommendBean> CREATOR = new Parcelable.Creator<DailyRecommendBean>() {
        @Override
        public DailyRecommendBean createFromParcel(Parcel source) {
            return new DailyRecommendBean(source);
        }

        @Override
        public DailyRecommendBean[] newArray(int size) {
            return new DailyRecommendBean[size];
        }
    };
}
