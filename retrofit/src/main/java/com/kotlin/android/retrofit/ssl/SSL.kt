package com.kotlin.android.retrofit.ssl

import com.kotlin.android.ktx.ext.io.safeClose
import okhttp3.internal.platform.Platform
import okhttp3.internal.readFieldOrNull
import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import javax.net.ssl.*
import kotlin.Exception

/**
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */

class SSL private constructor() {

    var ssl: SSLParam? = null
    var ignoreSSL: SSLParam? = null

    companion object {
        val instance by lazy { SSL() }
    }

    init {
//        initSSLSimple(CoreApp.instance.assets.open(ApiConfig.CERTIFICATE_FILE_NAME_CRT))
//        initSSL(arrayOf(CoreApp.instance.assets.open(ApiConfig.CERTIFICATE_FILE_NAME_CRT)))
        initIgnoreSSL()
    }

    /**
     * 初始化忽略所有证书的SSL配置
     */
    private fun initIgnoreSSL() {
        val trustAllCrts = arrayOf(IgnoreX509TrustManager())
        val ssl = SSLContext.getInstance("TLS")
        ssl.init(null, trustAllCrts, SecureRandom())
        ignoreSSL = SSLParam(ssl.socketFactory, trustAllCrts[0])
    }

    private fun initSSLSimple(certificate: InputStream) {
        try {
            //访问安全网站 https
            //1.创建SSL上下文对象设置信任管理器
            val sslContext = SSLContext.getInstance("TLS") //这个参数是请求协议，参考服务器的配置sslProtocol

            //2.获取信任管理工厂
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

            //2.1清空默认证书信息，设置自己的证书
            val ks = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null)
            }
            val cf = CertificateFactory.getInstance("X.509") //证书文件的编码方式X.509
            val cert = cf.generateCertificate(certificate) //生成证书对象
            ks.setCertificateEntry("wandafilm", cert)

            //3.初始化工厂
            tmf.init(ks)
            val tms = tmf.trustManagers
            val tm = if (tms.isNullOrEmpty()) {
                UnSafeTrustManager()
            } else {
                MyTrustManager(choseTrustManager(tms))
            }
            sslContext.init(null, Array<TrustManager>(1) { tm }, null)
            ssl = SSLParam(sslContext.socketFactory, tm)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            certificate.safeClose()
        }
    }

    /**
     * 初始化证书SSL配置
     */
    private fun initSSL(
        certificates: Array<InputStream>,
        bksFile: InputStream? = null,
        password: String? = null
    ) {
        try {
            val tms = prepareTrustManager(certificates)
            val kms = prepareKeyManager(bksFile, password)
            val sslContext = SSLContext.getInstance("TLS")
            val tm = if (tms.isNullOrEmpty()) {
                UnSafeTrustManager()
            } else {
                MyTrustManager(choseTrustManager(tms))
            }
            sslContext.init(kms, Array<TrustManager>(1) { tm }, null)
            ssl = SSLParam(sslContext.socketFactory, tm)
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        } catch (e: KeyStoreException) {
            throw AssertionError(e)
        }
    }

    private fun prepareTrustManager(
        certificates: Array<InputStream>?
    ): Array<TrustManager>? {
        if (certificates.isNullOrEmpty()) {
            return null
        }
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null)
            }
            certificates.forEachIndexed { index, cer ->
                keyStore.setCertificateEntry(
                    index.toString(),
                    certificateFactory.generateCertificate(cer)
                )
            }
            return TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                .apply {
                    init(keyStore)
                }.trustManagers
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            certificates.forEach {
                it.safeClose()
            }
        }
        return null
    }

    private fun prepareKeyManager(
        bksFile: InputStream? = null,
        password: String? = null
    ): Array<KeyManager>? {
        if (bksFile == null || password == null) {
            return null
        }
        try {
            val pwd = password.toCharArray()
            val keyStore = KeyStore.getInstance("BKS").apply {
                load(bksFile, pwd)
            }
            return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
                init(keyStore, pwd)
            }.keyManagers
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun choseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        trustManagers.forEach {
            if (it is X509TrustManager) {
                return it
            }
        }
        return null
    }

    class SSLParam(
        var sslSocketFactory: SSLSocketFactory,
        var trustManager: X509TrustManager
    )

    /**
     * 忽略所有证书（不稳定）
     */
    fun initIgnoreSSLOther() {
        Platform.get().trustManager(HttpsURLConnection.getDefaultSSLSocketFactory())?.run {
            ignoreSSL = SSLParam(HttpsURLConnection.getDefaultSSLSocketFactory(), this)
        }
    }
}

fun Platform.trustManager(sslSocketFactory: SSLSocketFactory): X509TrustManager? {
    return try {
        // Attempt to get the trust manager from an OpenJDK socket factory. We attempt this on all
        // platforms in order to support Robolectric, which mixes classes from both Android and the
        // Oracle JDK. Note that we don't support HTTP/2 or other nice features on Robolectric.
        val sslContextClass = Class.forName("sun.security.ssl.SSLContextImpl")
        val context = readFieldOrNull(sslSocketFactory, sslContextClass, "context") ?: return null
        readFieldOrNull(context, X509TrustManager::class.java, "trustManager")
    } catch (e: ClassNotFoundException) {
        null
    }
}