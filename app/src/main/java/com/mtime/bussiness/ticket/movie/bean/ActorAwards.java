package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class ActorAwards extends MBaseBean {
    /**
     * is the list item for parent node
     */
    private int festivalId;
    private int winCount;
    private int nominateCount;

    private List<ActorWinAwards> winAwards;
    private List<ActorNominates> nominateAwards;

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

    public List<ActorWinAwards> getWinAwards() {
        return winAwards;
    }

    public void setWinAwards(List<ActorWinAwards> winAwards) {
        this.winAwards = winAwards;
    }

    public List<ActorNominates> getNominateAwards() {
        return nominateAwards;
    }

    public void setNominateAwards(List<ActorNominates> nominateAwards) {
        this.nominateAwards = nominateAwards;
    }

    public boolean isEmpty() {
        return 0 == festivalId && 0 == winCount && 0 == nominateCount && (null == winAwards || winAwards.isEmpty()) && (null == nominateAwards || nominateAwards.isEmpty());
    }

}
