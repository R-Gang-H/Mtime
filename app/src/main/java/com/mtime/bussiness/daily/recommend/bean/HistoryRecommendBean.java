package com.mtime.bussiness.daily.recommend.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-22
 * <p>
 * 历史推荐
 */
public class HistoryRecommendBean extends MBaseBean implements Parcelable {
    public String hasMore; // 1 有更多 2 没有更多

    public List<HistoryMovieListBean> historyMovie;

    public boolean hasMore() {
        return !TextUtils.isEmpty(hasMore) && hasMore.equals("1");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hasMore);
        dest.writeTypedList(this.historyMovie);
    }

    public HistoryRecommendBean() {
    }

    protected HistoryRecommendBean(Parcel in) {
        this.hasMore = in.readString();
        this.historyMovie = in.createTypedArrayList(HistoryMovieListBean.CREATOR);
    }

    public static final Parcelable.Creator<HistoryRecommendBean> CREATOR = new Parcelable.Creator<HistoryRecommendBean>() {
        @Override
        public HistoryRecommendBean createFromParcel(Parcel source) {
            return new HistoryRecommendBean(source);
        }

        @Override
        public HistoryRecommendBean[] newArray(int size) {
            return new HistoryRecommendBean[size];
        }
    };
}
