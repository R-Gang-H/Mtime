package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

public class FeedBackTypeItem  extends MBaseBean {
    private final int ID;
    private String Value = "";

    public FeedBackTypeItem() {
	ID = 0;
	Value = "";
    }

    public FeedBackTypeItem(final int _ID, final String _Value) {
	ID = _ID;
	Value = _Value;
    }

    @Override
    public String toString() {
	return Value;
    }

    public int GetID() {
	return ID;
    }

    public String GetValue() {
	return Value;
    }
}
