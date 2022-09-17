package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieDetailAwards extends MBaseBean {
    private int festivalId;
    private int winCount;
    private int nominateCount;

    private List<MovieDetailWinAwards> winAwards;//获奖列表
    private List<MovieDetailNominates> nominateAwards; //提名列表

    public int getFestivalId() {
        return festivalId;
    }
    public void setFestivalId(int festivalId) {
        this.festivalId = festivalId;
    }
    public int getWinCount() {
        return winCount;
    }
    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }
    public int getNominateCount() {
        return nominateCount;
    }
    public void setNominateCount(int nominateCount) {
        this.nominateCount = nominateCount;
    }
    public List<MovieDetailWinAwards> getWinAwards() {
        return winAwards;
    }
    public void setWinAwards(List<MovieDetailWinAwards> winAwards) {
        this.winAwards = winAwards;
    }
    public List<MovieDetailNominates> getNominateAwards() {
        return nominateAwards;
    }
    public void setNominateAwards(List<MovieDetailNominates> nominateAwards) {
        this.nominateAwards = nominateAwards;
    }


}
