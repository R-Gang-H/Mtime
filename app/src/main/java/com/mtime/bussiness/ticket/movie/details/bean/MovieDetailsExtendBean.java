package com.mtime.bussiness.ticket.movie.details.bean;

import android.text.TextUtils;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-27
 *
 * 影片详情-扩展接口实体
 */
public class MovieDetailsExtendBean implements IObfuscateKeepAll {
    public long movieId; //影片ID，非服务器返回
    public String movieName; //影片名称，非服务器返回

    public MovieDetailsArticle timeOriginal; // 时光原创
    public MovieDetailsArticle timeTalks; // 时光对话
    public MovieDetailsEvents events; // 相关事件
    public MovieDetailsDataBank dataBankEntry; // 资料库
    public List<MovieDetailsRelatedMovie> relelatedMovielist; //关联电影

    public boolean hasData() {
        return null != timeOriginal || null != timeTalks || null != events
                || null != dataBankEntry || !CollectionUtils.isEmpty(relelatedMovielist);
    }

    public boolean hasTimeTalks() {
        return null != timeTalks && timeTalks.relatedId > 0;
    }

    public MovieDetailsArticle getTimeTalks() {
        if (hasTimeTalks()) {
            timeTalks.movieId = movieId;
            timeTalks.movieName = movieName;
            timeTalks.titleResId = R.string.movie_details_mtime_dialogue_title;
            return timeTalks;
        }
        return null;
    }

    public boolean hasTimeOriginal() {
        return null != timeOriginal && timeOriginal.relatedId > 0;
    }

    public MovieDetailsArticle getTimeOriginal() {
        if (hasTimeOriginal()) {
            timeOriginal.movieId = movieId;
            timeOriginal.movieName = movieName;
            timeOriginal.titleResId = R.string.movie_details_mtime_original_title;
            return timeOriginal;
        }
        return null;
    }

    public boolean hasEvents() {
        return null != events && !CollectionUtils.isEmpty(events.list);
    }

    public MovieDetailsEvents getEvents() {
        if (hasEvents()) {
            events.movieId = movieId;
            return events;
        }
        return null;
    }

    /**
     * 是否存在【扩展资料】数据
     * @return
     */
    public boolean hasDataBankEntry() {
        return null != dataBankEntry && dataBankEntry.hasExtendInfoDatas();
    }

    /**
     * 更多资料
     *
     * @return
     */
    public MovieDetailsDataBank getDataBankEntry() {
        if (hasDataBankEntry()) {
            dataBankEntry.movieId = movieId;
            return dataBankEntry;
        }
        return null;
    }

    public boolean hasClassicLines() {
        return null != dataBankEntry && dataBankEntry.isClassicLines && !TextUtils.isEmpty(dataBankEntry.classicLine);
    }

    /**
     * 经典台词 实体
     *
     * @return
     */
    public MovieDetailsDataBank.ClassicLines getClassicLines() {
        if (hasClassicLines()) {
            return dataBankEntry.getClassicLines();
        }
        return null;
    }

    public boolean hasAssociatedMovies() {
        return !CollectionUtils.isEmpty(relelatedMovielist);
    }

    /**
     * 关联的电影
     * @return
     */
    private final AssociatedMovies mAssociatedMovies = new AssociatedMovies();
    public AssociatedMovies getAssociatedMovies() {
        if (hasAssociatedMovies()) {
            List<MovieDetailsRelatedMovie.Movie> list = new ArrayList<>();
            for (MovieDetailsRelatedMovie relatedMovie : relelatedMovielist) {
                if (!CollectionUtils.isEmpty(relatedMovie.movies)) {
                    relatedMovie.movies.get(0).typeName = relatedMovie.typeName;
                    list.addAll(relatedMovie.movies);
                }
            }
            mAssociatedMovies.movieId = movieId;
            mAssociatedMovies.list = list;
            return mAssociatedMovies;
        }
        return null;
    }

    /**
     * 关联的电影封装类（非服务器返回的数据实体）
     */
    public static class AssociatedMovies implements IObfuscateKeepAll {
        public long movieId;
        public List<MovieDetailsRelatedMovie.Movie> list; //关联电影

        public int getCount() {
            if (CollectionUtils.isEmpty(list))
                return 0;
            return list.size();
        }
    }
}
