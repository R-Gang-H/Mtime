package com.mtime.util.image;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-12-29
 */

public final class ImageInfo {

    /**
     * 根据 rotation 旋转后的 size
     */
    public final Size size;
    /**
     * 如果加载到内存中 将会占用的 内存大小
     */
    public final long memorySize;
    /**
     *
     */
    public final int rotation;

    public ImageInfo(Size size, long memorySize, int rotation) {
        this.size = size;
        this.memorySize = memorySize;
        this.rotation = rotation;
    }
}
