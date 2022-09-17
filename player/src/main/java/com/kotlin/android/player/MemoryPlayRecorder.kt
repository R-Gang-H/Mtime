package com.kotlin.android.player

import android.util.SparseIntArray
import com.kk.taurus.playerbase.log.PLog

/**
 * create by lushan on 2020/8/31
 * description:
 */
class MemoryPlayRecorder {
    companion object{
        const val TAG = "MemoryPlayRecorder"

        private val mRecorders = SparseIntArray()

        private  var mRecorderPlayIndex = 0
        fun recordPlayTime(id: Int, time: Int) {
           mRecorders.put(id, time)
            PLog.d(TAG, "recordPlayTime : id = $id, time = $time")
        }

        fun getRecordPlayTime(id: Int): Int {
            PLog.d(TAG, "getRecordPlayTime : id = " + id + ", record = " + mRecorders.get(id))
            return mRecorders.get(id)
        }

        fun recordPlayIndex(index: Int) {
            mRecorderPlayIndex = index
        }

        fun getRecorderPlayIndex(): Int {
            return mRecorderPlayIndex
        }

    }
}