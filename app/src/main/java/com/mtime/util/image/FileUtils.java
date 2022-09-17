package com.mtime.util.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;

import com.mtime.base.utils.MD5;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-12-22
 */

public final class FileUtils {

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 按字典序列排序文件
     *
     * @param files files
     */
    public static void sortFileByDictionary(List<File> files) {
        Collections.sort(files, (o1, o2) -> {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o1.getName().compareTo(o2.getName());
        });
    }

    public static boolean checkMD5(String md5, String path) {
        return MD5.checkMD5(md5, new File(path));
    }

    /**
     * 根据 url 获取 文件后缀 包含 .
     * 例如 http://mtime-file.oss-cn-beijing.aliyuncs.com/files/misc/2017/09/13/170913180253028232.zip
     * <p>
     * 返回结果是 .zip
     */
    public static String getSuffix(String url) {
        return getSuffix(url, false);
    }


    /**
     * 根据 url 获取 文件后缀 包含 .
     * 例如 http://mtime-file.oss-cn-beijing.aliyuncs.com/files/misc/2017/09/13/170913180253028232.zip
     * <p>
     * 返回结果是 .zip
     */
    public static String getSuffix(String url, boolean includeDot) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        int index;
        if (!url.startsWith("/")) { // 文件
            url = Uri.parse(url).getPath();
            if (url == null) {
                return "";
            }
        }
        index = url.lastIndexOf(".");
        if (index <= 0) {
            return "";
        }
        if (includeDot) {
            return url.substring(index);
        }
        if (index + 1 >= url.length()) {
            return "";
        }
        return url.substring(index + 1);
    }

    /**
     * 从 src 重命名到 dst
     */
    public static boolean renameFile(String src, String dst) {
        try {
            File source = new File(src);
            File dest = new File(dst);
            return source.renameTo(dest);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取图片宽高信息
     *
     * @param path path
     * @return point
     */
    public static Size getImageSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int degree = getImageDegree(path);
        if (degree == 90 || degree == 270) {
            return new Size(options.outHeight, options.outWidth);
        }
        return new Size(options.outWidth, options.outHeight);
    }

    /**
     * 获取图片在内存的大小
     *
     * @param path path
     * @return bytes size
     */
    public static long getImageMemorySize(String path) {
        Size imageSize = getImageSize(path);
        return getImageMemorySize(imageSize);
    }

    public static long getImageMemorySize(Size imageSize) {
        return imageSize.width * imageSize.height * 4;
    }

    /**
     * 获取图片旋转信息
     *
     * @param path path
     * @return degree
     */
    public static int getImageDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取图片信息
     *
     * @param path path
     * @return ImageInfo
     */
    public static ImageInfo getImageInfo(String path) {
        Size imageSize = getImageSize(path);
        return new ImageInfo(imageSize, getImageMemorySize(imageSize), getImageDegree(path));
    }

    /**
     * 旋转图片
     *
     * @param bitmap  bitmap
     * @param degrees degrees
     * @return Bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || bitmap == null)
            return bitmap;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return bmp;
    }
}
