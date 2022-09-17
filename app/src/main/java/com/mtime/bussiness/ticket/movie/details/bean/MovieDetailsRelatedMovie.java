package com.mtime.bussiness.ticket.movie.details.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsBaseBinder;

import java.util.List;

/**
 * 影片详情-相关影片
 */
public class MovieDetailsRelatedMovie implements IObfuscateKeepAll {

    public String typeName; // "前传"
    public String typeEn; // "moviesFollows"
    public List<Movie> movies;

    public static class Movie implements IObfuscateKeepAll, MultiItemEntity {

        public String typeName; //title,如"前传" 非服务器返回

        public int movieID; // 42060
        public String title; // "测试"
        public String nameEn; // "The Rules of Deception"
        public String rating; // "0.0"
        public String releaseArea; // ""
        public String img; // "http://img31.test.cn/mt/60/42060/42060_96X128.jpg"
        public String year; // "2011"
        public int actor1Id; // 1214741
        public String actor1; // "D. Gillian Truster"
        public int actor2Id; // 1214900
        public String actor2; // "Cliff T.E. Roseman"
        public String releaseDate; // ""
        public boolean isFilter; //是否过滤恐怖海报

        @Override
        public int getItemType() {
            return movieID == MovieDetailsBaseBinder.allId ? 1 : 0;
        }
    }
}
