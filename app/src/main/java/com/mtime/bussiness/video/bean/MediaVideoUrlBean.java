package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 根据Vid获取媒资视频地址
 * Created by lys on 17/8/15.
 */

public class MediaVideoUrlBean extends MBaseBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean extends MBaseBean {

        private String templateName;
        private String url;

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
