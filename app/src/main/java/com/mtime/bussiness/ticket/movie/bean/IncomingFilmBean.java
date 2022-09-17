package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 最受关注影片
 * Created by lsmtime on 17/4/5.
 */

public class IncomingFilmBean extends MBaseBean {
    private List<RecommendsBean> recommends;
    private List<MoviecomingsBean> moviecomings;

    public List<RecommendsBean> getRecommends() {
        return recommends;
    }

    public void setRecommends(List<RecommendsBean> recommends) {
        this.recommends = recommends;
    }

    public List<MoviecomingsBean> getMoviecomings() {
        return moviecomings;
    }

    public void setMoviecomings(List<MoviecomingsBean> moviecomings) {
        this.moviecomings = moviecomings;
    }

    public static class RecommendsBean extends MBaseBean {
        private String recommendTitle;
        private List<MoviecomingsBean> movies;

        public String getRecommendTitle() {
            return recommendTitle;
        }

        public void setRecommendTitle(String recommendTitle) {
            this.recommendTitle = recommendTitle;
        }

        public List<MoviecomingsBean> getMovies() {
            return movies;
        }

        public void setMovies(List<MoviecomingsBean> movies) {
            this.movies = movies;
        }

    }

    public static class MoviecomingsBean extends MBaseBean {
        private int movieId;
        private String title;
        private String imgUrl;
        private String releaseDateStr;
        private int releaseYear;
        private int releaseMonth;
        private int releaseDay;
        private String type;
        private String actors;
        private boolean isTicket;
        private int wantedCount;
        private boolean isVideo;
        private  boolean isFilter;//是否屏蔽海报

        private boolean isWantSee; //是否已想看；此字段在首页用到

        private boolean isHeader;//本地
        private String dateString = "";//本地组装数据
        private int position;
        private long date;

        public boolean isFilter() {
            return isFilter;
        }

        public void setFilter(boolean filter) {
            isFilter = filter;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public boolean isHeader() {
            return isHeader;
        }

        public void setHeader(boolean header) {
            isHeader = header;
        }

        public boolean isWantSee() {
            return isWantSee;
        }

        public void setWantSee(boolean wanted) {
            isWantSee = wanted;
        }

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getReleaseDateStr() {
            return releaseDateStr;
        }

        public void setReleaseDateStr(String releaseDateStr) {
            this.releaseDateStr = releaseDateStr;
        }

        public int getReleaseYear() {
            return releaseYear;
        }

        public void setReleaseYear(int releaseYear) {
            this.releaseYear = releaseYear;
        }

        public int getReleaseMonth() {
            return releaseMonth;
        }

        public void setReleaseMonth(int releaseMonth) {
            this.releaseMonth = releaseMonth;
        }

        public int getReleaseDay() {
            return releaseDay;
        }

        public void setReleaseDay(int releaseDay) {
            this.releaseDay = releaseDay;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getActors() {
            return actors;
        }

        public void setActors(String actors) {
            this.actors = actors;
        }

        public boolean isIsTicket() {
            return isTicket;
        }

        public void setIsTicket(boolean isTicket) {
            this.isTicket = isTicket;
        }

        public int getWantedCount() {
            return wantedCount;
        }

        public void setWantedCount(int wantedCount) {
            this.wantedCount = wantedCount;
        }

        public boolean isIsVideo() {
            return isVideo;
        }

        public void setIsVideo(boolean isVideo) {
            this.isVideo = isVideo;
        }
    }
}
