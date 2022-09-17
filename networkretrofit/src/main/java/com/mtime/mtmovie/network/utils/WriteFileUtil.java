package com.mtime.mtmovie.network.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.mtime.mtmovie.network.interfaces.Fail;
import com.mtime.mtmovie.network.interfaces.Progress;
import com.mtime.mtmovie.network.interfaces.Success;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by yinguanping on 17/1/13.
 */

public class WriteFileUtil {

    private Progress progress;
    private Success mSuccessCallBack;
    private Fail mErrorCallBack;

//    private Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    ProgressInfo info = (ProgressInfo) msg.obj;
//                    progress.progress((float) info.read/info.total, info.read, info.total);
//                    break;
//                case 2:
//                    mSuccessCallBack.Success(msg.obj);
//                    break;
//                case 3:
//                    mErrorCallBack.Fail(msg.obj);
//                    break;
//                default:
//                    mErrorCallBack.Fail(msg.obj);
//                    break;
//            }
//        }
//    };

    public void writeFile(final ResponseBody body, final String path, final Progress progress,
        final Success mSuccessCallBack, final Fail mErrorCallBack) {
        this.progress = progress;
        this.mSuccessCallBack = mSuccessCallBack;
        this.mErrorCallBack = mErrorCallBack;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String directory = path.substring(0, path.lastIndexOf("/"));
                File dirFile = new File(directory);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                File futureStudioIconFile = new File(path);
                InputStream inputStream = null;
                OutputStream outputStream = null;
                final ProgressInfo progressInfo = new ProgressInfo();
                try {
                    byte[] fileReader = new byte[4096];
                    progressInfo.total = body.contentLength();
                    progressInfo.read = 0;
                    inputStream = body.byteStream();
                    outputStream = new FileOutputStream(futureStudioIconFile);
                    while (true) {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream.write(fileReader, 0, read);
                        progressInfo.read += read;
//                        mHandler.obtainMessage(1, progressInfo).sendToTarget();
                        progress.progress((float) progressInfo.read/progressInfo.total, progressInfo.read, progressInfo.total);
                    }
//                    mHandler.obtainMessage(2, path).sendToTarget();
                    mSuccessCallBack.Success(path);

                    outputStream.flush();
                } catch (final IOException e) {
//                    mHandler.obtainMessage(3, e).sendToTarget();
                    mErrorCallBack.Fail(e);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }).start();
    }

    static class ProgressInfo {
        public long read = 0;
        public long total = 0;
    }
}
