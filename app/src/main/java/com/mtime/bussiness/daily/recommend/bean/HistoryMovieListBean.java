package com.mtime.bussiness.daily.recommend.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-22
 * <p>
 * 每日推荐
 */
public class HistoryMovieListBean extends MBaseBean implements Parcelable {

    public String month; // 数据的月份
    public String monthShow; // 数据的月份，中文拼接显示
    public List<DailyRecommendBean> rcmdList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.month);
        dest.writeString(this.monthShow);
        dest.writeTypedList(this.rcmdList);
    }

    public HistoryMovieListBean() {
    }

    protected HistoryMovieListBean(Parcel in) {
        this.month = in.readString();
        this.monthShow = in.readString();
        this.rcmdList = in.createTypedArrayList(DailyRecommendBean.CREATOR);
    }

    public static final Parcelable.Creator<HistoryMovieListBean> CREATOR = new Parcelable.Creator<HistoryMovieListBean>() {
        @Override
        public HistoryMovieListBean createFromParcel(Parcel source) {
            return new HistoryMovieListBean(source);
        }

        @Override
        public HistoryMovieListBean[] newArray(int size) {
            return new HistoryMovieListBean[size];
        }
    };
}
