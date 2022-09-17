package com.kotlin.android.retrofit.cookie

import android.text.TextUtils
import com.kotlin.android.ktx.ext.io.safeClose
import okhttp3.Cookie
import okio.IOException
import java.io.*

/**
 * Cookie序列化
 *
 * Created on 2020/8/12.
 *
 * @author o.s
 */
class SerializableCookie : Serializable {

    @Transient
    var cookie: Cookie? = null

    @Throws(IOException::class)
    private fun writeObject(out: ObjectOutputStream) {
        cookie?.apply {
            out.writeObject(name)
            out.writeObject(value)
            out.writeLong(if (persistent) { expiresAt } else { INVALID_EXPIRES_AT } )
            out.writeObject(domain)
            out.writeObject(path)
            out.writeBoolean(secure)
            out.writeBoolean(httpOnly)
            out.writeBoolean(hostOnly)
            out.writeBoolean(persistent)
        }
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(ins: ObjectInputStream) {
        val builder = Cookie.Builder()
        builder.name((ins.readObject() as String))
        builder.value((ins.readObject() as String))
        val expiresAt = ins.readLong()
        if (expiresAt != -INVALID_EXPIRES_AT) {
            builder.expiresAt(expiresAt)
        }
        val domain = ins.readObject() as String
        builder.domain(domain)
        builder.path((ins.readObject() as String))
        if (ins.readBoolean()) builder.secure()
        if (ins.readBoolean()) builder.httpOnly()
        if (ins.readBoolean()) builder.hostOnlyDomain(domain)
        cookie = builder.build()
    }

    fun encode(cookie: Cookie?): String? {
        this.cookie = cookie
        val bos = ByteArrayOutputStream()
        var oos: ObjectOutputStream? = null
        try {
            oos = ObjectOutputStream(bos)
            oos.writeObject(this)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            return null
        } finally {
            oos.safeClose()
        }
        return bos.toByteArray().byteArrayToHexString()
    }

    fun decode(encodedCookie: String?): Cookie? {
        if (TextUtils.isEmpty(encodedCookie)) return null
        val bytes = encodedCookie?.hexStringToByteArray()
        val bis = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        var ois: ObjectInputStream? = null
        try {
            ois = ObjectInputStream(bis)
            cookie = (ois.readObject() as? SerializableCookie)?.cookie
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            ois.safeClose()
        }
        return cookie
    }

    companion object {
        private const val serialVersionUID = -8594045714036645534L
        private const val INVALID_EXPIRES_AT = -1L
    }
}