package com.mtime.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public class FileUtils {

    /**
     * 拷贝文件
     *
     * @param src src
     * @param dst dst
     * @return true
     */
    public static boolean copyFile(String src, String dst) {
        File srcFile = new File(src);
        File dstFile = new File(dst);
        if (!srcFile.exists()) return false;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (!dstFile.createNewFile()) return false;
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(dstFile);
            byte[] read = new byte[1024];
            int len;
            while ((len = fis.read(read)) != -1) {
                fos.write(read, 0, len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeSafty(fos);
            closeSafty(fis);
        }
        return true;
    }

    public static void closeSafty(Closeable toClose) {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片到系统相册中
     *
     * @param context  context
     * @param imagPath 文件路径
     */
    public static boolean savePhotoToLocal(Context context, String imagPath) {
        String dstDir = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/mtime";
        File out = new File(dstDir);
        if (!out.exists() && !out.mkdirs()) return false;
        String name = "mtime_save" + System.currentTimeMillis() + ".png";
        String file = dstDir + "/" + name;
        if (!FileUtils.copyFile(imagPath, file)) return false;

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file, name, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(file)));
        context.sendBroadcast(intent);
        return true;
    }
}
