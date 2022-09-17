// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class SmallPayBean extends MBaseBean implements Serializable {
	private boolean success;
	private String error;
	private boolean isMembershipCard;
	private List<CommodityList> commodityList;
	private String tips;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<CommodityList> getCommodityList() {
		return commodityList;
	}

	public void setCommodityList(List<CommodityList> commodityList) {
		this.commodityList = commodityList;
	}

    public boolean isMembershipCard() {
        return isMembershipCard;
    }

    public void setMembershipCard(boolean isMembershipCard) {
        this.isMembershipCard = isMembershipCard;
    }


	public String getTips() {
		if (tips == null) {
			return "";
		}
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

}
