package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 弹幕的bean
 * Created by wangdaban on 17/8/15.
 */

public class BarrageBean  extends MBaseBean {

    private int count;//查询时间段总弹幕数量
    private List<ListBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public boolean hasListData(){
        return this.list!=null && this.list.size()>0;
    }

    public static class ListBean extends MBaseBean{

        private long pointTime;//播放时间点发布，单位秒
        private String content;//内容
        private boolean isSelf;//是否当前用户评论
        private boolean hasShow;//是否已经显示过

        public long getPointTime() {
            return pointTime;
        }

        public void setPointTime(long pointTime) {
            this.pointTime = pointTime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isIsSelf() {
            return isSelf;
        }

        public void setIsSelf(boolean isSelf) {
            this.isSelf = isSelf;
        }

        public boolean isHasShow() {
            return hasShow;
        }

        public void setHasShow(boolean hasShow) {
            this.hasShow = hasShow;
        }
    }

}
