package com.kotlin.android.core

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.cdma.CdmaCellLocation
import android.telephony.gsm.GsmCellLocation
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.core.telephonyManager
import java.net.NetworkInterface
import java.util.*

/**
 *
 * Created on 2020/8/6.
 *
 * @author o.s
 */
class Phone private constructor() {

    companion object {
        val instance by lazy { Phone() }
    }

    /**
     * 本地语言
     */
    val language: String = Locale.getDefault().language

    /**
     * IMSI 移动用户识别码
     */
    val imsi: String
        get() {
            return CoreApp.instance.telephonyManager?.run {
                networkOperator
            } ?: ""
        }

    /**
     * IMEI 移动设备识别码
     */
    val imei: String
        get() {
            return CoreApp.instance.telephonyManager?.run {
                try {
                    if (ActivityCompat.checkSelfPermission(CoreApp.instance, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            imei
                        } else {
                            deviceId
                        }
                    }
                    androidID
                } catch (e: Exception) {
//                    e.printStackTrace()
                    androidID
                }
            } ?: androidID
        }

    /**
     * 移动设备MAC地址
     */
    val mac: String
        get() {
            try {
                val list: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (n in list) {
                    if (!n.name.equals("wlan0", ignoreCase = true)) continue
                    val macBytes = n.hardwareAddress ?: return ""
                    val address = StringBuilder()
                    macBytes.forEachIndexed { index, b ->
                        if (index == 0) {
                            address.append(String.format("%02X", b))
                        } else {
                            address.append(String.format(":%02X", b))

                        }
                    }
                    return address.toString()
                }
                return ""
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

    /**
     * 小区号
     */
    val cid: String
        get() {
            return ""
        }

    /**
     * 【基站编号】基站信号覆盖区域的编号ID，用于定位
     */
    val cellId: String
        get() {
            return CoreApp.instance.telephonyManager?.run {
                return if (ActivityCompat.checkSelfPermission(CoreApp.instance, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(CoreApp.instance, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        (cellLocation as? GsmCellLocation)?.cid?.toString()
                                ?: (cellLocation as? CdmaCellLocation)?.baseStationId?.toString()
                                ?: ""
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ""
                    }
                } else {
                    ""
                }
            } ?: ""
        }

    /**
     * Android ID
     */
    val androidID: String
        get() {
            var androidID = Settings.System.getString(CoreApp.instance.contentResolver, Settings.Secure.ANDROID_ID)
            if (TextUtils.isEmpty(androidID)) {
                androidID = Settings.Secure.getString(CoreApp.instance.contentResolver, Settings.Secure.ANDROID_ID)
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) androidID else androidID + Build.SERIAL
        }

    val genId: String
        get() {
            val sb = StringBuffer()
            sb.append("35")
            sb.append(Build.BOARD.length % 10)
            sb.append(Build.BRAND.length % 10)
            sb.append(Build.CPU_ABI.length % 10)
            sb.append(Build.DEVICE.length % 10)
            sb.append(Build.DISPLAY.length % 10)
            sb.append(Build.HOST.length % 10)
            sb.append(Build.ID.length % 10)
            sb.append(Build.MANUFACTURER.length % 10)
            sb.append(Build.MODEL.length % 10)
            sb.append(Build.PRODUCT.length % 10)
            sb.append(Build.TAGS.length % 10)
            sb.append(Build.TYPE.length % 10)
            sb.append(Build.USER.length % 10)
            //13 digits
            sb.e()

            val sb2 = StringBuffer()
            sb2.append("\nBOARD:")
            sb2.append(Build.BOARD)
            sb2.append("\nBRAND:")
            sb2.append(Build.BRAND)
            sb2.append("\nCPU_ABI:")
            sb2.append(Build.CPU_ABI)
            sb2.append("\nDEVICE:")
            sb2.append(Build.DEVICE)
            sb2.append("\nDISPLAY:")
            sb2.append(Build.DISPLAY)
            sb2.append("\nHOST:")
            sb2.append(Build.HOST)
            sb2.append("\nID:")
            sb2.append(Build.ID)
            sb2.append("\nMANUFACTURER:")
            sb2.append(Build.MANUFACTURER)
            sb2.append("\nMODEL:")
            sb2.append(Build.MODEL)
            sb2.append("\nPRODUCT:")
            sb2.append(Build.PRODUCT)
            sb2.append("\nTAGS:")
            sb2.append(Build.TAGS)
            sb2.append("\nTYPE:")
            sb2.append(Build.TYPE)
            sb2.append("\nUSER:")
            sb2.append(Build.USER)
            sb2.e()

            return sb.toString()
        }
}