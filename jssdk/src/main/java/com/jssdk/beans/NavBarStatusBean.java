package com.jssdk.beans;

/**
 * Created by Mtime on 17/7/14.
 */

public class NavBarStatusBean {
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public static class Data {
        private boolean isShowNavBar;
        private String navBarMainTitle;
        private boolean isShowRightShareBtn;
        private ShareContentBean shareContent;

        public boolean isIsShowNavBar() {
            return isShowNavBar;
        }

        public void setIsShowNavBar(boolean isShowNavBar) {
            this.isShowNavBar = isShowNavBar;
        }

        public String getNavBarMainTitle() {
            return navBarMainTitle;
        }

        public void setNavBarMainTitle(String navBarMainTitle) {
            this.navBarMainTitle = navBarMainTitle;
        }

        public boolean isIsShowRightShareBtn() {
            return isShowRightShareBtn;
        }

        public void setIsShowRightShareBtn(boolean isShowRightShareBtn) {
            this.isShowRightShareBtn = isShowRightShareBtn;
        }

        public ShareContentBean getShareContent() {
            return shareContent;
        }

        public void setShareContent(ShareContentBean shareContent) {
            this.shareContent = shareContent;
        }

        public static class ShareContentBean {
            private String summary;
            private String title;
            private String url;
            private String pic;
            private String sharetype;

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public String getSharetype() {
                return sharetype;
            }

            public void setSharetype(String sharetype) {
                this.sharetype = sharetype;
            }
        }

    }

}