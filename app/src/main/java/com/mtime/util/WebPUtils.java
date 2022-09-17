package com.mtime.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by yinguanping on 17/2/23.
 */

public class WebPUtils {
    //states
    private static final int NOT_INITIALIZED = -1;
    private static final int SUPPORTED = 1;
    private static final int NOT_SUPPORTED = 0;

    //why not boolean? we need more states for result caching
    private static int isWebPSupportedCache = NOT_INITIALIZED;

    public static boolean isWebPSupported() {
        // did we already try to check?
        if (isWebPSupportedCache == NOT_INITIALIZED) {
            //no - trying to decode
            //webp 1x1 transparent pixel with lossless
            final byte[] webp1x1 = new byte[]{
                    0x52, 0x49, 0x46, 0x46, 0x1A, 0x00, 0x00, 0x00,
                    0x57, 0x45, 0x42, 0x50, 0x56, 0x50, 0x38, 0x4C,
                    0x0D, 0x00, 0x00, 0x00, 0x2F, 0x00, 0x00, 0x00,
                    0x10, 0x07, 0x10, 0x11, 0x11, (byte) 0x88, (byte) 0x88, (byte) 0xFE,
                    0x07, 0x00
            };
            try {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(webp1x1, 0, webp1x1.length);
                if (bitmap != null) {
                    //webp supported
                    isWebPSupportedCache = SUPPORTED;
                    //don't forget to recycle!
                    bitmap.recycle();
                } else {
                    //bitmap is null = not supported
                    isWebPSupportedCache = NOT_SUPPORTED;
                }
            } catch (Exception ex) {
                //we got some exception = not supported
                isWebPSupportedCache = NOT_SUPPORTED;
                ex.printStackTrace();
            }
        }
        return isWebPSupportedCache == SUPPORTED;
    }
}

