package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class ShowTimeDataMainBean extends MBaseBean {
    private List<ShowtimeDate> showtimeDates;
    private MovieBaseItem movie;

    public List<ShowtimeDate> getShowtimeDate() {
	return showtimeDates;
    }

    public void setShowtimeDate(final List<ShowtimeDate> showtimeDate) {
	showtimeDates = showtimeDate;
    }

    public MovieBaseItem getMovie() {
        return movie;
    }

    public void setMovie(MovieBaseItem movie) {
        this.movie = movie;
    }
}
