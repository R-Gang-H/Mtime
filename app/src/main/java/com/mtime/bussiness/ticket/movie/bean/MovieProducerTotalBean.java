package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieProducerTotalBean extends MBaseBean {
    private List<Company> productionList;
    private List<Company> distributorList;
    private List<Company> specialEffectsList;

    public List<Company> getSpecialEffectsList() {
        return specialEffectsList;
    }

    public void setSpecialEffectsList(List<Company> specialEffectsList) {
        this.specialEffectsList = specialEffectsList;
    }


    public List<Company> getProductionList() {
        return productionList;
    }

    public List<Company> getDistributorList() {
        return distributorList;
    }

    public void setProductionList(List<Company> productionList) {
        this.productionList = productionList;
    }

    public void setDistributorList(List<Company> distributorList) {
        this.distributorList = distributorList;
    }
}
