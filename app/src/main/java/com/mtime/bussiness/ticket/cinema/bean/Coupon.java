package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 电影院-优惠信息
 * 
 * @author ye
 * 
 */
public class Coupon extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -2761349995891434773L;

    private String fId; // //优惠信息Id,分享时用到
    private String start;
    private String end;
    private String content;

    /** 优惠信息Id,分享时用到 */
    public String getfId() {
	return fId;
    }

    public void setfId(final String fId) {
	this.fId = fId;
    }

    public String getStart() {
	return start;
    }

    public void setStart(final String start) {
	this.start = start;
    }

    public String getEnd() {
	return end;
    }

    public void setEnd(final String end) {
	this.end = end;
    }

    /**
     * 返回有效期
     * 
     * start和end同时为空字符串时表示不限期（start和end只会同时为空）
     * 
     * @return
     */
    public String getValidity() {
	String validity;
	if ((start != null) && !"".equals(start) && (end != null)
		&& !"".equals(end)) {
	    final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年M月d日");

	    try {
		validity = sdf2.format(sdf1.parse(start)) + " - "
			+ sdf2.format(sdf1.parse(end));
	    } catch (final ParseException e) {
		e.printStackTrace();
		validity = start + " - " + end;
	    }
	} else {
	    validity = "长期有效";
	}
	return validity;
    }

    public String getContent() {
	return content;
    }

    public void setContent(final String content) {
	this.content = content;
    }
}