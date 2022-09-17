package com.mtime.base.utils;

/**
 * Created by LEE on 1/17/17.
 */

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DesOld {
    public static final String ALGORITHM_DES = "DES/ECB/PKCS5Padding";

    public static final byte[] keyBytes = {(byte) 0xf3, (byte) 0x91, (byte) 0xd6, (byte) 0xff,
            (byte) 0x32, (byte) 0x1f, (byte) 0x4a, (byte) 0x02};

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @return 加密后的字符串，一般结合Base64编码使用
     * @throws Exception 异常
     */
    public static String encode(String data) throws Exception {
        //对于长度大于8位的, 无论PKCS5Padding与否，都返回同样的加密串
        //对于data长度小于8的，用0x00在后面补齐8位。小胖会在MobileAPI解密时过滤掉这些0x00
        byte[] newData;
        if (data.length() < 8) {
            newData = new byte[8];
            byte[] realData = data.getBytes();
            for (int i = 0; i < realData.length; i++) {
                newData[i] = realData[i];
            }
            for (int j = realData.length; j < newData.length; j++) {
                newData[j] = (byte) 0x00;
            }
        } else {
            newData = data.getBytes();
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "DESede");//生成加密解密需要的Key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] res = cipher.doFinal(newData);
            return Base64.encodeToString(res, Base64.DEFAULT);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static String decode(byte[] data) throws Exception {
        byte[] datas = Base64.decode(data, Base64.DEFAULT);

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            SecretKeySpec key = new SecretKeySpec(keyBytes, "DESede");//生成加密解密需要的Key
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] res = cipher.doFinal(datas);
            return new String(res);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}