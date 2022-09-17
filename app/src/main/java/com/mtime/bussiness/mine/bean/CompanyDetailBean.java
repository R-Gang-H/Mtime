package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class CompanyDetailBean extends MBaseBean {
    private int productionTotalCount;
    private int distributorTotalCount;
    private int otherTotalCount;
    private int currentPageCount;
    List <CompanyMovieBean> movies;

    public int getProductionTotalCount() {
        return productionTotalCount;
    }

    public int getDistributorTotalCount() {
        return distributorTotalCount;
    }

    public int getOtherTotalCount() {
        return otherTotalCount;
    }

    public int getCurrentPageCount() {
        return currentPageCount;
    }

    public List<CompanyMovieBean> getMovies() {
        return movies;
    }

    public void setProductionTotalCount(int productionTotalCount) {
        this.productionTotalCount = productionTotalCount;
    }

    public void setDistributorTotalCount(int distributorTotalCount) {
        this.distributorTotalCount = distributorTotalCount;
    }

    public void setOtherTotalCount(int otherTotalCount) {
        this.otherTotalCount = otherTotalCount;
    }

    public void setCurrentPageCount(int currentPageCount) {
        this.currentPageCount = currentPageCount;
    }

    public void setMovies(List<CompanyMovieBean> movies) {
        this.movies = movies;
    }
}
