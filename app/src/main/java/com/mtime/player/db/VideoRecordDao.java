package com.mtime.player.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mtime on 2017/11/8.
 */

public class VideoRecordDao {

    private final VideoRecordSqliteHelper videoRecordSqliteHelper;
    private SQLiteDatabase db;

    public VideoRecordDao(Context context){
        videoRecordSqliteHelper = new VideoRecordSqliteHelper(context);
    }

    public boolean savePlayRecord(VideoRecordInfo info){
        int row = 0;
        try {
            if(info.getCurrent() >= info.getDuration())
                return false;
            db = videoRecordSqliteHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VideoRecordInfo.COLUMN_VIDEO_ID,info.getVid());
            cv.put(VideoRecordInfo.COLUMN_VIDEO_CURRENT_POSITION,info.getCurrent());
            cv.put(VideoRecordInfo.COLUMN_VIDEO_DURATION,info.getDuration());
            row = (int) db.insert(VideoRecordInfo.TABLE_NAME,null,cv);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db!=null){
                db.close();
            }
        }
        return row > 0;
    }

    public boolean updatePlayRecord(VideoRecordInfo info){
        int row = 0;
        try {
            db = videoRecordSqliteHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VideoRecordInfo.COLUMN_VIDEO_ID,info.getVid());
            cv.put(VideoRecordInfo.COLUMN_VIDEO_CURRENT_POSITION,info.getCurrent());
            cv.put(VideoRecordInfo.COLUMN_VIDEO_DURATION,info.getDuration());
            row = db.update(VideoRecordInfo.TABLE_NAME,cv, VideoRecordInfo.COLUMN_VIDEO_ID + "=?",new String[]{info.getVid()});
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db!=null){
                db.close();
            }
        }
        return row > 0;
    }

    public boolean resetPlayRecord(String vid){
        int row = 0;
        try {
            db = videoRecordSqliteHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(VideoRecordInfo.COLUMN_VIDEO_ID,vid);
            cv.put(VideoRecordInfo.COLUMN_VIDEO_CURRENT_POSITION ,0);
            cv.put(VideoRecordInfo.COLUMN_VIDEO_DURATION ,0);
            row = db.update(VideoRecordInfo.TABLE_NAME,cv, VideoRecordInfo.COLUMN_VIDEO_ID + "=?",new String[]{vid});
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(db!=null){
                db.close();
            }
        }
        return row > 0;
    }

    public VideoRecordInfo getPlayRecord(String vid){
        VideoRecordInfo info = null;
        try {
            db = videoRecordSqliteHelper.getReadableDatabase();
            StringBuffer sb = new StringBuffer("select ");
            sb.append(VideoRecordInfo.COLUMN_VIDEO_ID).append(",");
            sb.append(VideoRecordInfo.COLUMN_VIDEO_CURRENT_POSITION).append(",");
            sb.append(VideoRecordInfo.COLUMN_VIDEO_DURATION);
            sb.append(" from ").append(VideoRecordInfo.TABLE_NAME);
            sb.append(" where ").append(VideoRecordInfo.COLUMN_VIDEO_ID).append("=").append(vid);
            Cursor cursor = db.rawQuery(sb.toString(), new String[]{});
            if(cursor.moveToNext()){
                info = new VideoRecordInfo();
                info.setVid(cursor.getString(0));
                info.setCurrent(cursor.getInt(1));
                info.setDuration(cursor.getInt(2));
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return info;
    }

}
