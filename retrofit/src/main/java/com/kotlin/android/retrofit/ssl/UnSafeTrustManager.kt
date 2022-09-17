package com.kotlin.android.retrofit.ssl

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class UnSafeTrustManager : X509TrustManager {
    /**
     * Given the partial or complete certificate chain provided by the
     * peer, build a certificate path to a trusted root and return if
     * it can be validated and is trusted for client SSL
     * authentication based on the authentication type.
     *
     *
     * The authentication type is determined by the actual certificate
     * used. For instance, if RSAPublicKey is used, the authType
     * should be "RSA". Checking is case-sensitive.
     *
     * @param chain the peer certificate chain
     * @param authType the authentication type based on the client certificate
     * @throws IllegalArgumentException if null or zero-length chain
     * is passed in for the chain parameter or if null or zero-length
     * string is passed in for the  authType parameter
     * @throws CertificateException if the certificate chain is not trusted
     * by this TrustManager.
     */
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    /**
     * Given the partial or complete certificate chain provided by the
     * peer, build a certificate path to a trusted root and return if
     * it can be validated and is trusted for server SSL
     * authentication based on the authentication type.
     *
     *
     * The authentication type is the key exchange algorithm portion
     * of the cipher suites represented as a String, such as "RSA",
     * "DHE_DSS". Note: for some exportable cipher suites, the key
     * exchange algorithm is determined at run time during the
     * handshake. For instance, for TLS_RSA_EXPORT_WITH_RC4_40_MD5,
     * the authType should be RSA_EXPORT when an ephemeral RSA key is
     * used for the key exchange, and RSA when the key from the server
     * certificate is used. Checking is case-sensitive.
     *
     * @param chain the peer certificate chain
     * @param authType the key exchange algorithm used
     * @throws IllegalArgumentException if null or zero-length chain
     * is passed in for the chain parameter or if null or zero-length
     * string is passed in for the  authType parameter
     * @throws CertificateException if the certificate chain is not trusted
     * by this TrustManager.
     */
    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
    }

    /**
     * Return an array of certificate authority certificates
     * which are trusted for authenticating peers.
     *
     * @return a non-null (possibly empty) array of acceptable
     * CA issuer certificates.
     */
    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }
}