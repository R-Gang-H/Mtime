package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mtime on 2017/10/18.
 */

public class PlayUrlInfoBean extends MBaseBean implements Serializable {

    private ArrayList<UrlItem> playUrlAndSizeList;

    public ArrayList<UrlItem> getPlayUrlAndSizeList() {
        return playUrlAndSizeList;
    }

    public void setPlayUrlAndSizeList(ArrayList<UrlItem> playUrlAndSizeList) {
        this.playUrlAndSizeList = playUrlAndSizeList;
    }

    public static class UrlItem extends MBaseBean {

        private long fileSize;
        private String name;
        private String url;

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
