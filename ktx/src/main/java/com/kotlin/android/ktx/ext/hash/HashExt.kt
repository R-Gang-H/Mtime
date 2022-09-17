package com.kotlin.android.ktx.ext.hash

import java.nio.charset.Charset
import java.security.MessageDigest

/**
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 */

private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')


/**
 * 使用指定的Hash算法加密ByteArray
 */
private fun hash(data: ByteArray, algorithm: Hash): ByteArray {
    val md = MessageDigest.getInstance(algorithm.name)
    md.update(data)
    return md.digest()
}

/**
 * 使用指定的Hash算法加密字符串
 */
fun String.hash(algorithm: Hash, charset: Charset = Charset.forName("utf-8")): String = this.trim { it <= ' '}.toByteArray(charset).hash(algorithm)
fun ByteArray.hash(algorithm: Hash): String = hash(this, algorithm).toHexString()

/**
 * 使用MD5加密字符串
 */
fun String.md5(charset: Charset = Charset.forName("utf-8")): String = this.trim { it <= ' '}.toByteArray(charset).md5()
fun ByteArray.md5(): String = hash(this, Hash.MD5).toHexString()

fun ByteArray.toHexString(): String {
    val sb = StringBuilder(size * 2)
    for (b in this){
        sb.append(HEX_DIGITS[(b.toInt().and(0xf0)).ushr(4)])
        sb.append(HEX_DIGITS[b.toInt().and(0x0f)])
    }
    return sb.toString()
}

/**
 * Hash算法类型
 */
enum class Hash {
    MD5,
    SHA1,
    SHA224,
    SHA256,
    SHA384,
    SHA512,
}