package com.kotlin.android.app.router.provider.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider
import java.util.ArrayList

/**
 * 创建者: zl
 * 创建时间: 2020/6/11 10:11 AM
 * 描述: 宿主app Provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_MAIN)
interface IMainProvider : IBaseProvider {
    fun startCityChangeActivity()
    fun startCityChangeActivity(address: String?)
    fun startLoginActivity(bundle: Bundle?)
    fun startLoginActivityForResult(activity: Activity, bundle: Bundle?, requestCode: Int)
    fun startNormalLoginActivity(bundle: Bundle?)
    fun startNormalLoginActivityForResult(activity: Activity, bundle: Bundle?, requestCode: Int)
    fun startForApplink(applink: String)
    fun startForApplinkH5(applink: String)
    fun startPhotoDetailActivity(urlList: ArrayList<String>, itemClicked: Int)
    fun startPhotoDetailActivityFromAlbum(
        urlList: ArrayList<String>,
        itemClicked: Int,
        albumId: Long,
        albumUserId: Long,
        imageIdList: LongArray
    )

    //    跳转到影人详情， id影人id name 影人名
    fun startActorViewActivity(id: Long, name: String)

    //    跳转到影院详情
    fun startCinemaViewActivity(cinemaId: Long)
//    跳转到订单详情
    /**
     * 跳转到订单详情
     * @param orderId 订单id
     * @param isETicket 是否是电子券
     */
    fun startOrderDetailActivity(
        orderId: Long,
        isETicket: Boolean,
        withoutPay: Boolean = false,
        isFromAccount: Boolean = false
    )

    /**
     * 跳转到主Activity
     * @param tabIndex 第几个tab
     */
    fun startMainActivity(
        flag: PageFlag? = null,
        applink: String? = ""
    )

    fun startGuideViewActivity(context: Context)

    /**
     * 影院排片页
     */
    fun startCinemaShowtimeActivity(
        context: Context,
        cinemaId: String,
        movieId: String,
        newDate: String
    )

    /**
     * 影片预告片列表页
     */
    fun startVideoListActivity(context: Context, movieId: String)


    // JPushMessageReceiver onRegister
    fun pushReceiverOnRegister(context: Context?, registrationID: String?)

    // 通知打开
    fun onNotifyMessageOpened(context: Context?, applinkData: String?)

    // 通知到达
    fun onNotifyMessageArrived(context: Context?, applinkData: String?)

    // 修改昵称与签名错误弹窗
    fun showCustomAlertDlg(context: Context?, message: String)

    //显示日期选择组件
    fun showDatePicker(
        context: Context?, initYear: Int, initMonthOfYear: Int, initDayOfMonth: Int,
        okListener: ((year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit?)? = null
    )

    //显示 toast
    fun showToast(message: String)

    //拨打电话
    fun callNumber(context: Context?, number: String)

    //更改手机号
    fun startChangePhoneNum(context: Context)

    //修改账号密码
    fun modifyPwd(context: Context)

    fun upgradeAppDialog(context: Context)

    fun showCommonAlertDlg(context: Context?, message: String, finishListener: (() -> Unit)? = null)

    /**
     * 订单确认页
     */
    fun startOrderConfirmActivity(
        seatIds: String,
        seatCount: Int,
        totalPrice: Double,
        serviceFee: Double,
        buyPhone: String? = null,
        movieName: String? = null,
        cinemaName: String? = null,
        ticketDateInfo: String? = null,
        seatSelectedInfo: String? = null,
        orderId: String? = null,
        subOrderId: String? = null,
        isFromSeatActivity: Boolean = false,
        // 以下4个数据需要带到支付页，当需要从支付页重新返回选座页时要带回来（更换场次要用到）
        dId: String? = null,
        movieId: String? = null,
        cinemaId: String? = null,
        showtimeDate: String? = null,
        // 在确认订单页面显示 4个
        ticketTime: String? = null,
        ticketHallName: String? = null,
        ticketVersion: String? = null,
        ticketLanguage: String? = null,
        // noVip?
    )

    /**
     * 订单支付页
     */
    fun startOrderPayActivity(
        dId: String,
        orderId: String? = null,
        subOrderId: String? = null,
        isETicket: Boolean = false,
        isFromSeatActivity: Boolean = false,
        totalPrice: Double,
        serviceFee: Double,
        payEndTime: Long,
        buyPhone: String? = null,
        seatIds: String,
        seatCount: Int,
        ticketDateInfo: String? = null,
        seatSelectedInfo: String? = null,
    )
}