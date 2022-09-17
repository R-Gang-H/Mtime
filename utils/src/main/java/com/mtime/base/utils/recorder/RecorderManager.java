package com.mtime.base.utils.recorder;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-08-22
 */

public final class RecorderManager {
    /**
     * 日志文件缓存路径
     */
    static String RECORDER_LOG_DIR;
    static Context sContext;


    public static void init(Context context) {
        sContext = context.getApplicationContext();
        RECORDER_LOG_DIR = sContext.getExternalFilesDir(null) + "/recorder/";
    }

    private RecorderManager() {
    }

    private static final Map<String, Recorder> sRecorders = new HashMap<>();
    private static final Handler sRecorderHandler;
    private static final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
    // 每个目录最大限制 512 K bytes
    private static final int DIRECTORY_MAX_SIZE = 512 * 1024;

    static {
        HandlerThread t = new HandlerThread("RecorderThread");
        t.start();
        sRecorderHandler = new Handler(t.getLooper());
    }

    public static void clearCache() {
        File recorderDir = new File(RECORDER_LOG_DIR);
        File[] files = recorderDir.listFiles();
        if (files == null || files.length == 0) return;
        for (File item : files) {
            if (item.isFile()) {
                RecorderFileUtil.deleteFiles(item);
            } else {
                File[] logs = item.listFiles();
                if (logs == null || logs.length == 0) continue;
                List<File> fileList = Arrays.asList(logs);
                RecorderFileUtil.sortFileByDictionary(fileList);
                int N = fileList.size();
                for (int i = 0; i < N - 1; ++i) {
                    RecorderFileUtil.deleteFiles(fileList.get(i));
                }
            }
        }
    }

    public static long getLogFileSize() {
        return RecorderFileUtil.calculateFileLength(new File(RECORDER_LOG_DIR));
    }

    /**
     * 获取一个 Recorder 实例
     *
     * @param fileName log 目录名称，不包含路径
     * @return Recorder
     */
    public static Recorder get(String fileName) {
        Recorder recorder = sRecorders.get(fileName);
        if (recorder == null) {
            recorder = new SimpleRecorder(fileName);
            sRecorders.put(fileName, recorder);
        }
        return recorder;
    }

    private static class SimpleRecorder implements Recorder {

        private final String mFileName;

        private boolean mOpen;

        private Sink mSink;
        private BufferedSink mBufferedSink;

        private SimpleRecorder(String fileName) {
            mFileName = fileName;
        }

        @Override
        public void open() {
            open("");
        }

        /**
         * 打开一个文件
         *
         * @param suffix 文件后缀
         */
        @Override
        public void open(final String suffix) {
            if (mOpen) {
                return;
            }
            sRecorderHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        String append = TextUtils.isEmpty(suffix) ? "" : "_" + suffix;
                        String fileName = sFormat.format(new Date()) + append + ".txt";
                        File outDir = new File(RECORDER_LOG_DIR, mFileName);
                        File outFile = new File(outDir, fileName);
                        if ((outDir.exists() || outDir.mkdirs()) && (outFile.exists() || outFile.createNewFile())) {
                            mSink = Okio.sink(outFile);
                            mBufferedSink = Okio.buffer(mSink);
                            mOpen = true;
                        }
                        if (outDir.exists()) {
                            long length = RecorderFileUtil.calculateFileLength(outDir);
                            if (length > DIRECTORY_MAX_SIZE) {
                                deleteHalfFiles(outDir);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void record(String msg) {
            if (!mOpen) {
                Log.w("Recorder", "you should open first!");
                return;
            }
            final String message = sFormat.format(new Date()) + ": " + msg + "\n";
            sRecorderHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!mOpen) {
                        return;
                    }
                    try {
                        mBufferedSink.writeUtf8(message);
                        mBufferedSink.flush();
                        mSink.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void close() {
            sRecorders.remove(mFileName);
            sRecorderHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mOpen) {
                        mOpen = false;
                        RecorderFileUtil.closeSafty(mBufferedSink);
                        RecorderFileUtil.closeSafty(mSink);
                    }
                }
            });
        }

    }

    /**
     * 按字典序排序文件并删除前一半的文件
     *
     * @param dir dir
     */
    private static void deleteHalfFiles(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length < 2) {
            return;
        }
        List<File> fileList = Arrays.asList(files);
        RecorderFileUtil.sortFileByDictionary(fileList);
        int N = fileList.size() / 2;
        for (int i = 0; i < N; ++i) {
            RecorderFileUtil.deleteFiles(fileList.get(i));
        }
    }

}
