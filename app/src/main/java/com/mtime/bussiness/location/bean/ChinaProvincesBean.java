package com.mtime.bussiness.location.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 中国所有的省（包括直辖市）
 * 
 * @author ye 2012-09-29
 * 
 */
public class ChinaProvincesBean extends MBaseBean {
    // 省（包括直辖市）列表
    private List<ProvinceBean> p;
    
    public List<ProvinceBean> getProvinces() {
        return p;
    }
    
    public void setP(final List<ProvinceBean> p) {
        this.p = p;
    }
}
