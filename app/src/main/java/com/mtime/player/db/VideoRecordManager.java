package com.mtime.player.db;

import com.mtime.frame.App;

/**
 * Created by mtime on 2017/11/8.
 */

public class VideoRecordManager {

    private final VideoRecordDao videoRecordDao;

    private VideoRecordManager(){
        videoRecordDao = new VideoRecordDao(App.getInstance().getApplicationContext());
    }

    private static VideoRecordManager instance;

    public static VideoRecordManager getInstance(){
        if(null==instance){
            synchronized (VideoRecordManager.class){
                if(null==instance){
                    instance = new VideoRecordManager();
                }
            }
        }
        return instance;
    }

    public boolean savePlayRecord(VideoRecordInfo info){
        if(getPlayRecord(info.getVid())!=null){
            return updatePlayRecord(info);
        }
        return videoRecordDao.savePlayRecord(info);
    }

    public boolean updatePlayRecord(VideoRecordInfo info){
        return videoRecordDao.updatePlayRecord(info);
    }

    public boolean resetPlayRecord(String vid){
        return videoRecordDao.resetPlayRecord(vid);
    }

    public VideoRecordInfo getPlayRecord(String vid){
        return videoRecordDao.getPlayRecord(vid);
    }

}
