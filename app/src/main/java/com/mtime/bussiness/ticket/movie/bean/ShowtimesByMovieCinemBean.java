package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class ShowtimesByMovieCinemBean extends MBaseBean {
    
    private List<String>               showDates;
    private V2_ShowtimesMovieInfoBean movie;
    private V2_ShowtimesCinemaInfoBean cinema;
    private List<V2_ShowTimesDateBean> dateShowtimes = new ArrayList<V2_ShowTimesDateBean>();
    
    public void setShowDates(final List<String> showDates) {
        this.showDates = showDates;
    }
    
    public List<String> getShowDates() {
        return this.showDates;
    }
    
    public void setMovie(final V2_ShowtimesMovieInfoBean movie) {
        this.movie = movie;
    }
    
    public V2_ShowtimesMovieInfoBean getMovie() {
        return this.movie;
    }
    
    public void setCinema(final V2_ShowtimesCinemaInfoBean cinema) {
        this.cinema = cinema;
    }
    
    public V2_ShowtimesCinemaInfoBean getCinema() {
        return this.cinema;
    }
    
    public void setDateShowtimes(final List<V2_ShowTimesDateBean> dateShowtimes) {
        this.dateShowtimes = dateShowtimes;
    }
    
    public List<V2_ShowTimesDateBean> getDateShowtimes() {
        return this.dateShowtimes;
    }
}
