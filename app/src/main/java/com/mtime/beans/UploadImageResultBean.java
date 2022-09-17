package com.mtime.beans;

import com.google.gson.annotations.SerializedName;
import com.mtime.base.bean.MSyncBaseBean;

import java.util.List;

public class UploadImageResultBean extends MSyncBaseBean {

    @SerializedName("List")
    public List<ResultList> resultList;

}
