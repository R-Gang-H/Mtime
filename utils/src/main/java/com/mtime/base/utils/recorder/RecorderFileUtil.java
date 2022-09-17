package com.mtime.base.utils.recorder;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2018-03-01
 */

public class RecorderFileUtil {

    /**
     * 按字典序列排序文件
     *
     * @param files files
     */
    static void sortFileByDictionary(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    static void closeSafty(Closeable toClose) {
        if (toClose != null) {
            try {
                toClose.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 递归的方式计算文件大小
     *
     * @param file file
     * @return length of bytes
     */
    static long calculateFileLength(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        if (file.isFile()) {
            return file.length();
        }
        int total = 0;
        File[] files = file.listFiles();
        for (File f : files) {
            total += calculateFileLength(f);
        }
        return total;
    }

    /**
     * 递归的方式删除文件
     *
     * @param file file
     */
    static void deleteFiles(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            deleteFiles(f);
        }
    }
}
