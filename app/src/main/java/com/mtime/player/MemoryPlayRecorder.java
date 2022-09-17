package com.mtime.player;

import android.util.SparseIntArray;

import com.kk.taurus.playerbase.log.PLog;

/**
 * Created by mtime on 2018/1/6.
 */

public class MemoryPlayRecorder {

    static final String TAG = "MemoryPlayRecorder";

    private static final SparseIntArray mRecorders = new SparseIntArray();

    private static int mRecorderPlayIndex;

    public static void recordPlayTime(int id, int time){
        mRecorders.put(id, time);
        PLog.d(TAG,"recordPlayTime : id = " + id + ", time = " + time);
    }

    public static int getRecordPlayTime(int id){
        PLog.d(TAG,"getRecordPlayTime : id = " + id + ", record = " + mRecorders.get(id));
        return mRecorders.get(id);
    }

    public static void recordPlayIndex(int index){
        mRecorderPlayIndex = index;
    }

    public static int getRecorderPlayIndex(){
        return mRecorderPlayIndex;
    }

}
