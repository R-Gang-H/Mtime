package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class ActorRelationShips extends MBaseBean {

    private int rPersonId;
    private String relation;
    private String rNameCn;
    private String rNameEn;
    private String rCover;
    
    
    public int getrPersonId() {
        return rPersonId;
    }
    public void setrPersonId(int rPersonId) {
        this.rPersonId = rPersonId;
    }
    public String getRelation() {
        return relation;
    }
    public void setRelation(String relation) {
        this.relation = relation;
    }
    public String getrNameCn() {
        return rNameCn;
    }
    public void setrNameCn(String rNameCn) {
        this.rNameCn = rNameCn;
    }
    public String getrNameEn() {
        return rNameEn;
    }
    public void setrNameEn(String rNameEn) {
        this.rNameEn = rNameEn;
    }
    public String getrCover() {
        return rCover;
    }
    public void setrCover(String rCover) {
        this.rCover = rCover;
    }
    
}
