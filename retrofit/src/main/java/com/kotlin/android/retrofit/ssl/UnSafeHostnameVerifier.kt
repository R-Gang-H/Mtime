package com.kotlin.android.retrofit.ssl

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class UnSafeHostnameVerifier : HostnameVerifier {
    /**
     * Verify that the host name is an acceptable match with
     * the server's authentication scheme.
     *
     * @param hostname the host name
     * @param session SSLSession used on the connection to host
     * @return true if the host name is acceptable
     */
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return true
    }
}