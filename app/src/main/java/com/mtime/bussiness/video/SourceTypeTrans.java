package com.mtime.bussiness.video;


import com.mtime.bussiness.video.bean.VideoSource;

/**
 * Created by mtime on 2017/10/26.
 * <p>
 * 统计中的video_type
 * video_type：视频类型，当前主客中的视频来源较多，有老资料库「lab」，有自媒体「mp」，有新媒资「vrs」，有直播回放「live」
 * <p>
 * <p>
 * 接口中的sourceType
 * 视频来源，
 * 1:预告片 ，2:自媒体视频，3:媒资视频
 * MOVIEVIDEO(1, "预告片"),
 * SMAVIDEO(2, "自媒体视频"),
 * MEDIAVIDEO(3, "媒资视频");
 * <p>
 * <p>
 * 老资料库---》对应预告片
 * 自媒体----》对应自媒体视频
 * 新媒资----》对应媒资视频
 */

public class SourceTypeTrans {

    public static String getStatisticsVType(int sourceType) {
        if (VideoSource.MOVIEVIDEO.getTypeValue() == sourceType) {
            return VideoSource.MOVIEVIDEO.getStatisticsVType();
        } else if (VideoSource.SMAVIDEO.getTypeValue() == sourceType) {
            return VideoSource.SMAVIDEO.getStatisticsVType();
        } else if (VideoSource.MEDIAVIDEO.getTypeValue() == sourceType) {
            return VideoSource.MEDIAVIDEO.getStatisticsVType();
        }
        return VideoSource.MOVIEVIDEO.getStatisticsVType();
    }

}
