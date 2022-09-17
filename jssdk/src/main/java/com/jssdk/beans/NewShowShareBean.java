package com.jssdk.beans;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-12-19
 */
public class NewShowShareBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        public String relateId;  // 对应的关联ID
        public String secondRelateId; //二级关联ID
        public String sharetype; //分享类型

        public String getRelateId() {
            return relateId;
        }

        public void setRelateId(String relateId) {
            this.relateId = relateId;
        }

        public String getSecondRelateId() {
            return secondRelateId;
        }

        public void setSecondRelateId(String secondRelateId) {
            this.secondRelateId = secondRelateId;
        }

        public String getSharetype() {
            return sharetype;
        }

        public void setSharetype(String sharetype) {
            this.sharetype = sharetype;
        }
    }
}
