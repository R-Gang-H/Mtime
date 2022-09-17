package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class SubscribeListBean  extends MBaseBean {
	private int interruptionFreeStart;
	private int interruptionFreeEnd;
	private boolean isMessage;
	private boolean  isBroadcast;
	private boolean  isRemindNewMovie;
	private boolean  isUpdateVersion;
	private boolean isSwitchCity;
	private String version;
	
	private List<SubscribeBean> subscribeBroadcastList;

	public int getinterruptionFreeStart() {
		return interruptionFreeStart;
	}

	public void setinterruptionFreeStart(int interruptionFreeStart) {
		this.interruptionFreeStart = interruptionFreeStart;
	}
	
	public int getinterruptionFreeEnd() {
        return interruptionFreeEnd;
    }

    public void setinterruptionFreeEnd(int interruptionFreeEnd) {
        this.interruptionFreeEnd = interruptionFreeEnd;
    }
    public boolean getisMessage() {
        return isMessage;
    }

    public void setisMessage(boolean isMessage) {
        this.isMessage = isMessage;
    }
    
    public boolean getisBroadcast() {
        return isBroadcast;
    }

    public void setisBroadcast(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }
    
    public boolean getisRemindNewMovie() {
        return isRemindNewMovie;
    }

    public void setisRemindNewMovie(boolean isRemindNewMovie) {
        this.isRemindNewMovie = isRemindNewMovie;
    }
    public boolean getisUpdateVersion() {
        return isUpdateVersion;
    }

    public void setisUpdateVersion(boolean isUpdateVersion) {
        this.isUpdateVersion = isUpdateVersion;
    }
    
    public boolean getisSwitchCity() {
        return isSwitchCity;
    }

    public void setisSwitchCity(boolean isSwitchCity) {
        this.isSwitchCity = isSwitchCity;
    }
	
    public String getversion() {
        return version;
    }

    public void setversion(String version) {
        this.version = version;
    }
	
	public List<SubscribeBean> getsubscribeBroadcastList() {
		return subscribeBroadcastList;
	}

	public void setsubscribeBroadcastList(List<SubscribeBean> subscribeBroadcastList) {
		this.subscribeBroadcastList = subscribeBroadcastList;
	}
}
