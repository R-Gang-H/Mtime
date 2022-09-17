package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class CinemaShowtimeUPHalfBean extends MBaseBean {
    private CinemaShowtimeUPHalfCinemaBean cinema;
    private List<CinemaShowtimeUPHalfMovieBean> movies;
    private List<MovieTimeChildMainBean> showtimes;

    public CinemaShowtimeUPHalfCinemaBean getCinema() {
        return cinema;
    }

    public void setCinema(CinemaShowtimeUPHalfCinemaBean cinema) {
        this.cinema = cinema;
    }

    public List<CinemaShowtimeUPHalfMovieBean> getMovies() {
        return movies;
    }

    public void setMovies(List<CinemaShowtimeUPHalfMovieBean> movies) {
        this.movies = movies;
    }

    public List<MovieTimeChildMainBean> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(List<MovieTimeChildMainBean> showtimes) {
        this.showtimes = showtimes;
    }
}
