package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieDetailWinAwards extends MBaseBean {

    private int sequenceNumber;
    private String festivalEventYear;
    private String awardName;
    private List<WinAwardsPerson> persons;

    public void setPersons(List<WinAwardsPerson> persons) {
        this.persons = persons;
    }
    public List<WinAwardsPerson> getPersons(){
        return persons;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getFestivalEventYear() {
        return festivalEventYear;
    }

    public void setFestivalEventYear(String festivalEventYear) {
        this.festivalEventYear = festivalEventYear;
    }

    public String getAwardName() {
        return awardName;
    }

    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }

}
