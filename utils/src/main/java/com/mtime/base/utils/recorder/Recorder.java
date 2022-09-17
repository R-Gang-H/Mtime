package com.mtime.base.utils.recorder;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-08-23
 */

public interface Recorder {

    /**
     * 打开文件
     */
    void open();

    /**
     * 打开文件
     *
     * @param suffix 文件后缀
     */
    void open(String suffix);

    /**
     * 记录一条信息
     *
     * @param msg msg
     */
    void record(String msg);

    /**
     * 关闭文件
     */
    void close();

}
