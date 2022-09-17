package com.jssdk.beans;

/**
 * Created by Mtime on 17/7/14.
 */

public class ShowVideoPlayerBean {
    public Data data;
    
    public static class Data {
    
        public String videoID; //视频ID
        public String videoType; //视频类型
        public float videoTop; //视频顶部位置
        public float videoLeft; //视频左侧位置;
        public float videoWidth; //视频宽度;
        public float videoHeight; //视频高度;

        @Override
        public String toString() {
            return "Data{" +
                    "videoID='" + videoID + '\'' +
                    ", videoType='" + videoType + '\'' +
                    ", videoTop=" + videoTop +
                    ", videoLeft=" + videoLeft +
                    ", videoWidth=" + videoWidth +
                    ", videoHeight=" + videoHeight +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ShowVideoPlayerBean{" +
                "data=" + data +
                '}';
    }
}
