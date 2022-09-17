package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieProducerType extends MBaseBean {
    private String typeName;
    private List<Company> companys;

    public String getTypeName() {
        return typeName;
    }

    public List<Company> getCompanys() {
        return companys;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setCompanys(List<Company> companys) {
        this.companys = companys;
    }
}
