package com.mtime.base.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Log工具类
 * <p/>
 * Created by Mars on 15/6/30.
 */
public class MLogWriter {

    /**
     * tag
     */
    private String TAG = "MTIME_LOG";

    /**
     * log level
     */
    private  int level = Level.INFO;

    /**
     * model tag
     */
    private String modelTag = "";

    /**
     * a switch control printing, default open
     */
    private static boolean toggle = true;

    private static final MLogWriter sMLogWriter = new MLogWriter();


    public MLogWriter() {
    }

    public MLogWriter(String tag) {
        this.TAG = tag;
    }

    /**
     * set model tag
     * 
     * @param modelTag
     */
    public void setModelTag(String modelTag) {
        this.modelTag = modelTag;
    }

    /**
     * set the current level
     * 
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 开关
     * @param to
     */
    public static void setToggle(boolean to) {
        toggle = to;
    }
    public static void v(String tag, String msg) {
        sMLogWriter.write(Level.VERBOSE, tag, msg);
    }

    public static void d(String tag, String msg) {
        sMLogWriter.write(Level.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        sMLogWriter.write(Level.INFO, tag, msg);
    }

    public static void w(String tag, String msg) {
        sMLogWriter.write(Level.WARN, tag, msg);
    }

    public static void e(String tag, String msg) {
        sMLogWriter.write(Level.ERROR, tag, msg);
    }
    /**
     * error logging
     *
     * @param msg
     */
    public void e(String msg) {
        if (level <= Level.ERROR) {
            write(Level.ERROR, TAG, msg);
        }
    }

    /**
     * warn logging
     *
     * @param msg
     */
    public void w(String msg) {
        if (level <= Level.WARN) {
            write(Level.WARN, TAG, msg);
        }
    }

    /**
     * info logging
     *
     * @param msg
     */
    public void i(String msg) {
        if (level <= Level.INFO) {
            write(Level.INFO, TAG, msg);
        }
    }

    /**
     * debug logging
     *
     * @param msg
     */
    public void d(String msg) {
        if (level <= Level.DEBUG) {
            write(Level.DEBUG, TAG, msg);
        }
    }

    /**
     * verbose logging
     *
     * @param msg
     */
    public void v(String msg) {
        if (level <= Level.VERBOSE) {
            write(Level.VERBOSE, TAG, msg);
        }
    }


    /**
     * mark tag
     */
    private  String markTag(String tag) {
        if (TextUtils.isEmpty(tag)){
            tag = TAG;
        }

        StringBuilder tagSb = new StringBuilder();
        if (!TextUtils.isEmpty(modelTag)) {
            tagSb.append("[");
            tagSb.append(this.modelTag);
            tagSb.append("]");
        }
        tagSb.append("[");
        tagSb.append(tag);
        tagSb.append("]");
        return tagSb.toString();
    }

    /**
     * local print
     * @param level
     * @param tag
     * @param msg
     */
    private void callSysLog(int level, String tag, String msg) {
        if (tag == null) {
            tag = "";
        }
        if (msg == null) {
            msg = "";
        }

        switch (level) {
            case Level.ERROR:
                Log.e(tag, msg);
                break;
            case Level.WARN:
                Log.w(tag, msg);
                break;
            case Level.INFO:
                Log.i(tag, msg);
                break;
            case Level.DEBUG:
                Log.d(tag, msg);
                break;
            case Level.VERBOSE:
                Log.v(tag, msg);
                break;
            default:
                break;
        }
    }

    private void write(int level, String tag, String msg) {
        if (!toggle) {
            return;
        }
        if (level >= level) {
            callSysLog(level, markTag(tag), msg);
        }
    }

    /**
     * Log Level
     * <p/>
     * Created by Mars on 15/6/30.
     */
    public static class Level {

        /**
         * lowest level, turn on all logging
         */
        public static final int ALL = 0x1;

        /**
         * verbose level
         */
        public static final int VERBOSE = 0x2;

        /**
         * devug level
         */
        public static final int DEBUG = 0x3;

        /**
         * info level
         */
        public static final int INFO = 0x4;

        /**
         * warn level
         */
        public static final int WARN = 0x5;

        /**
         * error level
         */
        public static final int ERROR = 0x6;

        /**
         * highest level, turn off loading
         */
        public static final int OFF = 0x7;

    }

}
