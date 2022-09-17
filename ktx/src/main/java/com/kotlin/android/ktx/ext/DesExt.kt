package com.kotlin.android.ktx.ext

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * 创建者: zl
 * 创建时间: 2020/11/6 11:08 上午
 * 描述:DES算法，加密\解密。从旧框架里移植过来
 */
val ALGORITHM_DES: String? = "DES/ECB/PKCS5Padding"
val ALGORITHM: String? = "DESede"

val keyBytes = byteArrayOf(0xf3.toByte(), 0x91.toByte(), 0xd6.toByte(), 0xff.toByte(),
        0x32.toByte(), 0x1f.toByte(), 0x4a.toByte(), 0x02.toByte())

/**
 * DES算法，加密
 *
 * @param data 待加密字符串
 * @return 加密后的字符串，一般结合Base64编码使用
 * @throws Exception 异常
 */
@Throws(Exception::class)
fun encode(data: String): String {
    //对于长度大于8位的, 无论PKCS5Padding与否，都返回同样的加密串
    //对于data长度小于8的，用0x00在后面补齐8位。小胖会在MobileAPI解密时过滤掉这些0x00
    val newData: ByteArray
    if (data.length < 8) {
        newData = ByteArray(8)
        val realData = data.toByteArray()
        for (i in realData.indices) {
            newData[i] = realData[i]
        }
        for (j in realData.size until newData.size) {
            newData[j] = 0x00.toByte()
        }
    } else {
        newData = data.toByteArray()
    }
    return try {
        val cipher = Cipher.getInstance(ALGORITHM_DES)
        val key = SecretKeySpec(keyBytes, ALGORITHM) //生成加密解密需要的Key
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val res = cipher.doFinal(newData)
        Base64.encodeToString(res, Base64.DEFAULT)
    } catch (e: Exception) {
        throw Exception(e)
    }
}

/**
 * DES算法，解密
 *
 * @param data 待解密字符串
 * @return 解密后的字节数组
 * @throws Exception 异常
 */
@Throws(Exception::class)
fun decode(data: ByteArray?): String {
    val data = Base64.decode(data, Base64.DEFAULT)
    return try {
        val cipher = Cipher.getInstance(ALGORITHM_DES)
        val key = SecretKeySpec(keyBytes, ALGORITHM) //生成加密解密需要的Key
        cipher.init(Cipher.DECRYPT_MODE, key)
        val res = cipher.doFinal(data)
        String(res)
    } catch (e: Exception) {
        throw Exception(e)
    }
}