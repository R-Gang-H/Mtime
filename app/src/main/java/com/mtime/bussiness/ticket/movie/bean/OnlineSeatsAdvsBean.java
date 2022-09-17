package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by wangdaban on 2017/1/12.
 */

public class OnlineSeatsAdvsBean extends MBaseBean {

        private String title;
        private String img1;
        private String url1;
        private String img2;
        private boolean isOpenH5;
        /**显示广告字样，为空不显示 20180728版新增!*/
        public String advTag;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImg1() {
            return img1;
        }

        public void setImg1(String img1) {
            this.img1 = img1;
        }

        public String getUrl1() {
            return url1;
        }

        public void setUrl1(String url1) {
            this.url1 = url1;
        }

        public String getImg2() {
            return img2;
        }

        public void setImg2(String img2) {
            this.img2 = img2;
        }

        public boolean isIsOpenH5() {
            return isOpenH5;
        }

        public void setIsOpenH5(boolean isOpenH5) {
            this.isOpenH5 = isOpenH5;
        }

}

