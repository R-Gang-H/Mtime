/**
 * 
 */
package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * @author wangjin
 * 
 */
public class SaveSeenRecommendBean extends MBaseBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String Id;
    private String type;

    public String getId() {
	return Id;
    }

    public void setId(final String id) {
	Id = id;
    }

    public String getType() {
	return type;
    }

    public void setType(final String type) {
	this.type = type;
    }

}
