package com.mtime.bussiness.common.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;

import java.util.List;

/**
 * Created by CSY on 2018/5/10.
 * 广告实体
 */
public class CommonAdListBean implements IObfuscateKeepAll {
    public static final String AD_POSITION_CODE_STARTUP_FULLSCREEN = "ad_mtime_startup_fullscreen_countdown_1"; //splash页面，广告类型
    public static final String AD_POSITION_CODE_FILM_DETAIL = "ad_mobile_notic_bar_film_detail"; //影片详情页面，广告类型
    public static final String AD_POSITION_CODE_ARTICLE_DETAIL = "ad_mobile_notic_bar_article_detail"; //文章详情页面，广告类型


    private List<AdBean> adList;

    public List<AdBean> getAdList() {
        return adList;
    }

    public void setAdList(List<AdBean> adList) {
        this.adList = adList;
    }

    public boolean hasDatas() {
        return !CollectionUtils.isEmpty(adList) && adList.get(0).hasAdData();
    }

    public static class AdBean implements IObfuscateKeepAll {
        /**
         * adId : 6
         * adLink : mtlf://scheme?XXX
         * adTitle : 侃侃猩Test
         * countdown : 5
         * isShowLogo : false
         * imagePropertiesList : [{"imgType":1,"imgUrl":"http://img5.test.cn/mg/2017/12/28/144246.47161345.jpg","label":"adapterCommonScreen"},{"imgUrl":"http://img5.test.cn/mg/2017/12/28/144246.32424232.jpg","label":"adapterFullScreen"}]
         * positionCode : ad_feed_home_1
         * showTimeList : [{"endTime":"2018-05-12 23:59:59","startTime":"2018-05-10 00:00:00"},{"endTime":"2018-05-28 23:59:59","startTime":"2018-05-28 00:00:00"}]
         */

        private int adId;
        private String adLink;
        private String adTitle;
        private int countdown;
        private boolean isShowLogo;
        private String positionCode;
        private String positionType;
        private List<ImagePropertiesListBean> imagePropertiesList;
        private List<ShowTimeListBean> showTimeList;

        public boolean hasAdData() {
            return null != imagePropertiesList && !imagePropertiesList.isEmpty();
        }

        public int getAdId() {
            return adId;
        }

        public void setAdId(int adId) {
            this.adId = adId;
        }

        public String getAdLink() {
            return adLink;
        }

        public void setAdLink(String adLink) {
            this.adLink = adLink;
        }

        public String getAdTitle() {
            return adTitle;
        }

        public void setAdTitle(String adTitle) {
            this.adTitle = adTitle;
        }

        public int getCountdown() {
            return countdown;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }

        public boolean isIsShowLogo() {
            return isShowLogo;
        }

        public void setIsShowLogo(boolean isShowLogo) {
            this.isShowLogo = isShowLogo;
        }

        public String getPositionType() {
            return positionType;
        }

        public void setPositionType(String positionType) {
            this.positionType = positionType;
        }

        public String getPositionCode() {
            return positionCode;
        }

        public void setPositionCode(String positionCode) {
            this.positionCode = positionCode;
        }

        public List<ImagePropertiesListBean> getImagePropertiesList() {
            return imagePropertiesList;
        }

        public void setImagePropertiesList(List<ImagePropertiesListBean> imagePropertiesList) {
            this.imagePropertiesList = imagePropertiesList;
        }

        public List<ShowTimeListBean> getShowTimeList() {
            return showTimeList;
        }

        public void setShowTimeList(List<ShowTimeListBean> showTimeList) {
            this.showTimeList = showTimeList;
        }

        public static class ImagePropertiesListBean implements IObfuscateKeepAll {
            /**
             * imgType : 1
             * imgUrl : http://img5.test.cn/mg/2017/12/28/144246.47161345.jpg
             * label : adapterCommonScreen
             */

            private int imgType;
            private String imgUrl;
            private String label;

            public int getImgType() {
                return imgType;
            }

            public void setImgType(int imgType) {
                this.imgType = imgType;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getLabel() {
                return label;
            }

            public void setLabel(String label) {
                this.label = label;
            }
        }

        public static class ShowTimeListBean implements IObfuscateKeepAll {
            /**
             * endTime : 2018-05-12 23:59:59
             * startTime : 2018-05-10 00:00:00
             */

            private String endTime;
            private String startTime;

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }
        }
    }
}


//    /**
//     * adId : 6
//     * adLink : mtlf://scheme?XXX
//     * adTitle : 侃侃猩Test
//     * countdown : 5
//     * isShowLogo : false
//     * imagePropertiesList : [{"imgType":1,"imgUrl":"http://img5.test.cn/mg/2017/12/28/144246.47161345.jpg","label":"adapterCommonScreen"},{"imgType":1,"imgUrl":"http://img5.test.cn/mg/2017/12/28/144246.32424232.jpg","label":"adapterFullScreen"}]
//     * positionCode : ad_feed_home_1
//     * showTimeList : [{"endTime":"2018-05-12 23:59:59","startTime":"2018-05-10 00:00:00"},{"endTime":"2018-05-28 23:59:59","startTime":"2018-05-28 00:00:00"}]
//     */
//
//    private int adId;
//    private String adLink;
//    private String adTitle;
//    private int countdown;
//    private boolean isShowLogo;
//    private String positionCode;
//    private List<ImagePropertiesListBean> imagePropertiesList;
//    private List<ShowTimeListBean> showTimeList;
//
//    public boolean hasAdData() {
//        return null != imagePropertiesList && !imagePropertiesList.isEmpty();
//    }
//
//    public int getAdId() {
//        return adId;
//    }
//
//    public void setAdId(int adId) {
//        this.adId = adId;
//    }
//
//    public String getAdLink() {
//        return adLink;
//    }
//
//    public void setAdLink(String adLink) {
//        this.adLink = adLink;
//    }
//
//    public String getAdTitle() {
//        return adTitle;
//    }
//
//    public void setAdTitle(String adTitle) {
//        this.adTitle = adTitle;
//    }
//
//    public int getCountdown() {
//        return countdown;
//    }
//
//    public void setCountdown(int countdown) {
//        this.countdown = countdown;
//    }
//
//    public boolean isIsShowLogo() {
//        return isShowLogo;
//    }
//
//    public void setIsShowLogo(boolean isShowLogo) {
//        this.isShowLogo = isShowLogo;
//    }
//
//    public String getPositionCode() {
//        return positionCode;
//    }
//
//    public void setPositionCode(String positionCode) {
//        this.positionCode = positionCode;
//    }
//
//    public List<ImagePropertiesListBean> getImagePropertiesList() {
//        return imagePropertiesList;
//    }
//
//    public void setImagePropertiesList(List<ImagePropertiesListBean> imagePropertiesList) {
//        this.imagePropertiesList = imagePropertiesList;
//    }
//
//    public List<ShowTimeListBean> getShowTimeList() {
//        return showTimeList;
//    }
//
//    public void setShowTimeList(List<ShowTimeListBean> showTimeList) {
//        this.showTimeList = showTimeList;
//    }
//
//    public static class ImagePropertiesListBean {
//        /**
//         * imgType : 1
//         * imgUrl : http://img5.test.cn/mg/2017/12/28/144246.47161345.jpg
//         * label : adapterCommonScreen
//         */
//
//        private int imgType;
//        private String imgUrl;
//        private String label;
//
//        public int getImgType() {
//            return imgType;
//        }
//
//        public void setImgType(int imgType) {
//            this.imgType = imgType;
//        }
//
//        public String getImgUrl() {
//            return imgUrl;
//        }
//
//        public void setImgUrl(String imgUrl) {
//            this.imgUrl = imgUrl;
//        }
//
//        public String getLabel() {
//            return label;
//        }
//
//        public void setLabel(String label) {
//            this.label = label;
//        }
//    }
//
//    public static class ShowTimeListBean {
//        /**
//         * endTime : 2018-05-12 23:59:59
//         * startTime : 2018-05-10 00:00:00
//         */
//
//        private String endTime;
//        private String startTime;
//
//        public String getEndTime() {
//            return endTime;
//        }
//
//        public void setEndTime(String endTime) {
//            this.endTime = endTime;
//        }
//
//        public String getStartTime() {
//            return startTime;
//        }
//
//        public void setStartTime(String startTime) {
//            this.startTime = startTime;
//        }
//    }
//
//

