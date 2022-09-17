package com.mtime.player.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mtime on 2017/11/8.
 */

public class VideoRecordSqliteHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "video_record.db";

    public static final String CREATE_VIDEO_RECORD = "create table " + VideoRecordInfo.TABLE_NAME + " ("

            + "id integer primary key autoincrement, "

            + VideoRecordInfo.COLUMN_VIDEO_ID + " text, "

            + VideoRecordInfo.COLUMN_VIDEO_CURRENT_POSITION + " integer, "

            + VideoRecordInfo.COLUMN_VIDEO_DURATION + " integer)";

    public VideoRecordSqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VIDEO_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
