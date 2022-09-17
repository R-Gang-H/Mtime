package com.mtime.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

/**
 * Created by JiaJunHui on 2018/6/1.
 */
public class Base64ToBitmap {

    /**
     * 根据base64字符串转化成图片
     */
    public static Bitmap getQrCodeByBase64String(String content){
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(content)) {
            try {
                byte[] bytes = Base64.decode(content, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
