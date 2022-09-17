package com.kotlin.android.film.ui.seat

import android.content.Intent
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.film.seat.Seat
import com.kotlin.android.app.data.entity.film.seat.SeatInfo
import com.kotlin.android.film.databinding.ActSeatBinding
import com.kotlin.android.film.widget.seat.SeatManager
import com.kotlin.android.film.widget.seat.manager.MTimeSeatManager
import com.kotlin.android.mtime.ktx.PriceUtils
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.film.*
import com.kotlin.android.film.KEY_MOVIE_ID
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.date.date
import com.kotlin.android.ktx.ext.date.toStringWithDateFormat
import com.kotlin.android.ktx.ext.date.weekCN3
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.*

/**
 * 选座/座位图页
 *
 * Created on 2022/2/8.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.Film.PAGER_SEAT)
class SeatActivity : BaseVMActivity<SeatViewModel, ActSeatBinding>() {

    private var titleBar: TitleBar? = null
    private var seatInfo: SeatInfo? = null
    private var dId: String = ""
    private var lastOrderId: String? = null
    private var movieId: String? = null
    private var movieName: String? = null
    private var cinemaId: String? = null
    private var cinemaName: String? = null
    private var showtimeDate: String? = null
    private var buyPhone: String? = null

    /**
     * 如： X月X日（周X）X点X分 版本语言片长：如3D,中文
     */
    private val ticketDateInfo: String
        get() = "$showtimeDateDesc (${seatInfo?.hallName}) ${seatInfo?.versionDesc} ${seatInfo?.language}"

    /**
     * 如： X月X日（周X）X点X分
     */
    private val showtimeDateDesc: String?
        get() = seatInfo?.realTime?.toStringWithDateFormat("M月d日 (E)  HH:mm")?.replace("星期", "周")


    private val seatManager by lazy {
        MTimeSeatManager(object : SeatManager.OnClickListener<Seat> {
            /**
             * 选择座席
             *
             * @param info info
             */
            override fun selectedSeat(info: Seat) {
                refreshSeatSelectedView()
            }

            /**
             * 取消选择座席
             *
             * @param info info
             */
            override fun cancelSeat(info: Seat) {
                refreshSeatSelectedView()
            }

            /**
             * 允许访问点击
             *
             * @return true 允许点击座位图，反之。
             */
            override fun allowAccessClick(): Boolean = true // isLogin()

        })
    }

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            dId = getStringExtra(KEY_SEATING_DID).orEmpty()
            lastOrderId = getStringExtra(KEY_SEATING_LAST_ORDER_ID)
            movieId = getStringExtra(KEY_MOVIE_ID)
            movieName = getStringExtra(KEY_MOVIE_NAME)
            cinemaId = getStringExtra(KEY_CINEMA_ID)
            cinemaName = getStringExtra(KEY_CINEMA_NAME)
            showtimeDate = getStringExtra(KEY_SHOWTIME_DATE)
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        titleBar = TitleBarManager.with(this, themeStyle = ThemeStyle.STANDARD_STATUS_BAR)
            .back {
                finish()
            }
            .setTitle(title = cinemaName.orEmpty())
    }

    override fun initView() {
        mBinding?.apply {
            titleView.text = movieName.orEmpty()
            seatView.setSeatManager(seatManager)
            footerLayout.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 10.dpF
            )
            seatSelectView.apply {
                autoClick = {
                    mViewModel?.autoSeat(showtimeId = dId.toLong(), count = it.toLong())
                }
                clickDelete = {
                    seatManager.cancelSeat(it)
                }
            }
            nextView.apply {
                setBackground(
                    colorRes = R.color.color_20a0da,
                    cornerRadius = 20.dpF
                )
                setOnClickListener {
                    next()
                }
            }
        }
    }

    override fun initData() {
        mViewModel?.getSeatInfo(dId = dId)
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it?.apply {
                showProgressDialog(isShow = showLoading)

                success?.apply {
                    seatInfo = this
                    seatManager.setData(this)
                    refreshUI()
                }
            }
        }
        mViewModel?.autoSeatUIState?.observe(this) {
            it?.apply {
                showProgressDialog(isShow = showLoading)

                success?.apply {
                    if (bizCode == 1L) {
                        autoSeatIds?.apply {
                            seatManager.selectedSeatAll(this)
                            mBinding?.seatView?.startResetAnim()
                        }
                    } else {
                        showToast(msg)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding?.seatView?.recycle()
    }

    private fun refreshUI() {
        mBinding?.apply {
            seatInfo?.apply {
                updateTitle(cinemaName.orEmpty())
                val date = realTime.orZero().date.weekCN3
                val timeStart = realTime.orZero().toStringWithDateFormat(format = " MM-dd HH:mm")
                val timeEnd = (realTime.orZero() + (movieLength.orZero() * 60_000L)).toStringWithDateFormat(format = "HH:mm")
                subTitleView.text = "$date$timeStart~$timeEnd ($language$versionDesc)"
                titleView.text = movieName.orEmpty()
            }
        }

        refreshSeatSelectedView()
    }

    private fun updateTitle(title: String) {
        titleBar?.setTitle(title = title)
    }

    private fun refreshSeatSelectedView() {
        mBinding?.seatSelectView?.let {
            it.setData(seatManager)
            val hasTicket = it.ticketCount > 0
            mBinding?.seatLabelView?.isVisible = hasTicket
            mBinding?.seatTipsView?.isVisible = !hasTicket
            if (hasTicket) {
                mBinding?.nextView?.setText(getString(R.string.confirm_selected_seat,
                    "¥${PriceUtils.formatPrice(it.totalPrice.toDouble())}"))
            } else {
                mBinding?.nextView?.setText(R.string.confirm_place_select_seat)
            }
        }
    }

    private val totalPrice: Double
        get() = (mBinding?.seatSelectView?.totalPrice ?: 0) / 100.0

    private val totalServiceFee: Double
        get() = (mBinding?.seatSelectView?.totalServiceFee ?: 0) / 100.0

    private val ticketCount: Int
        get() = mBinding?.seatSelectView?.ticketCount ?: 0

    private val seatIds: String
        get() = mBinding?.seatSelectView?.seatIds.orEmpty()

    private val seatNames: String
        get() = mBinding?.seatSelectView?.seatNames.orEmpty()

    private fun next() {
        if ((mBinding?.seatSelectView?.ticketCount ?: 0) <= 0) {
            showToast("请先选择座位")
            return
        } else {
            openOrderConfirm()
        }
    }

    /**
     * 订单支付页：去支付
     */
    private fun openOrderPay() {
        getProvider(IMainProvider::class.java)?.startOrderPayActivity(
            seatIds = seatIds,
            seatCount = ticketCount,
            totalPrice = totalPrice,
            serviceFee = seatInfo?.serviceFee.orZero() / 100.0,
            ticketDateInfo = ticketDateInfo,
            seatSelectedInfo = seatNames,
            buyPhone = buyPhone,
            orderId = lastOrderId,
            isFromSeatActivity = true,
            dId = dId,
            payEndTime = 0L,
        )
    }

    /**
     * 确认订单页
     */
    private fun openOrderConfirm() {
        getProvider(IMainProvider::class.java)?.startOrderConfirmActivity(
            seatIds = seatIds,
            seatCount = ticketCount,
            totalPrice = totalPrice,
            serviceFee = seatInfo?.serviceFee.orZero() / 100.0,
            ticketDateInfo = ticketDateInfo,
            seatSelectedInfo = seatNames,
            buyPhone = buyPhone,
            orderId = seatInfo?.orderId?.toString(),
            subOrderId = seatInfo?.subOrderId?.toString(),
            isFromSeatActivity = true,
            dId = dId,
            movieId = seatInfo?.movieId?.toString(),
            movieName = seatInfo?.movieName,
            cinemaId = seatInfo?.cinemaId?.toString(),
            cinemaName = seatInfo?.cinemaName,
            showtimeDate = showtimeDate,
            ticketTime = showtimeDateDesc,
            ticketHallName = seatInfo?.hallName,
            ticketVersion = seatInfo?.versionDesc,
            ticketLanguage = seatInfo?.language,
        )
    }

    fun test(): SeatInfo {
//        val json = "{\"error\":\"\",\"mobile\":\"\",\"maxAutoSelectCount\":4,\"maxOrderTicketCount\":4,\"serviceFee\":310,\"cinemaId\":116352,\"cinemaName\":\"勿动----万达平台-北京通州万达影院-张慧\",\"hallName\":\"1号厅VIP1B\",\"hallSpecialDes\":\"关于儿童是否需要购票，以及3D眼镜是否需要自备等特殊性规定，建议您观影前，咨询影院了解\",\"movieId\":32692,\"movieName\":\"阿拉蕾\",\"realTime\":1648694400000,\"movieLength\":95,\"versionDesc\":\"2D\",\"language\":\"汉语普通话\",\"dateMessage\":\"\",\"areaList\":[{\"areaCode\":\"1\",\"areaName\":\"普通分区\",\"areaPrice\":{\"salesPrice\":1510,\"ticketPrice\":1200,\"totalFee\":310},\"seatList\":[{\"id\":\"1-1-1\",\"x\":1,\"y\":1,\"name\":\"1排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":false,\"rowName\":\"2\"},{\"id\":\"1-1-2\",\"x\":2,\"y\":1,\"name\":\"1排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-3\",\"x\":3,\"y\":1,\"name\":\"1排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-4\",\"x\":4,\"y\":1,\"name\":\"1排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-5\",\"x\":5,\"y\":1,\"name\":\"1排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-6\",\"x\":6,\"y\":1,\"name\":\"1排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-7\",\"x\":7,\"y\":1,\"name\":\"1排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-8\",\"x\":8,\"y\":1,\"name\":\"1排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-9\",\"x\":9,\"y\":1,\"name\":\"1排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-10\",\"x\":10,\"y\":1,\"name\":\"1排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-11\",\"x\":11,\"y\":1,\"name\":\"1排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-12\",\"x\":12,\"y\":1,\"name\":\"1排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-13\",\"x\":13,\"y\":1,\"name\":\"1排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-14\",\"x\":14,\"y\":1,\"name\":\"1排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-15\",\"x\":15,\"y\":1,\"name\":\"1排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-16\",\"x\":16,\"y\":1,\"name\":\"1排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"1-1-17\",\"x\":17,\"y\":1,\"name\":\"1排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":false,\"rowName\":\"2\"},{\"id\":\"1-2-1\",\"x\":1,\"y\":2,\"name\":\"2排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-2\",\"x\":2,\"y\":2,\"name\":\"2排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-3\",\"x\":3,\"y\":2,\"name\":\"2排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-4\",\"x\":4,\"y\":2,\"name\":\"2排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-5\",\"x\":5,\"y\":2,\"name\":\"2排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-6\",\"x\":6,\"y\":2,\"name\":\"2排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-7\",\"x\":7,\"y\":2,\"name\":\"2排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-8\",\"x\":8,\"y\":2,\"name\":\"2排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-9\",\"x\":9,\"y\":2,\"name\":\"2排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-10\",\"x\":10,\"y\":2,\"name\":\"2排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-11\",\"x\":11,\"y\":2,\"name\":\"2排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-12\",\"x\":12,\"y\":2,\"name\":\"2排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-13\",\"x\":13,\"y\":2,\"name\":\"2排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-14\",\"x\":14,\"y\":2,\"name\":\"2排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-15\",\"x\":15,\"y\":2,\"name\":\"2排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-16\",\"x\":16,\"y\":2,\"name\":\"2排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"1-2-17\",\"x\":17,\"y\":2,\"name\":\"2排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":false,\"rowName\":\"3\"},{\"id\":\"1-3-1\",\"x\":1,\"y\":3,\"name\":\"3排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-2\",\"x\":2,\"y\":3,\"name\":\"3排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-3\",\"x\":3,\"y\":3,\"name\":\"3排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-4\",\"x\":4,\"y\":3,\"name\":\"3排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-5\",\"x\":5,\"y\":3,\"name\":\"3排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-6\",\"x\":6,\"y\":3,\"name\":\"3排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-7\",\"x\":7,\"y\":3,\"name\":\"3排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-8\",\"x\":8,\"y\":3,\"name\":\"3排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-9\",\"x\":9,\"y\":3,\"name\":\"3排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-10\",\"x\":10,\"y\":3,\"name\":\"3排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-11\",\"x\":11,\"y\":3,\"name\":\"3排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-12\",\"x\":12,\"y\":3,\"name\":\"3排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-13\",\"x\":13,\"y\":3,\"name\":\"3排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-14\",\"x\":14,\"y\":3,\"name\":\"3排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-15\",\"x\":15,\"y\":3,\"name\":\"3排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-16\",\"x\":16,\"y\":3,\"name\":\"3排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"1-3-17\",\"x\":17,\"y\":3,\"name\":\"3排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":false,\"rowName\":\"4\"},{\"id\":\"1-4-1\",\"x\":1,\"y\":4,\"name\":\"4排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-2\",\"x\":2,\"y\":4,\"name\":\"4排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-3\",\"x\":3,\"y\":4,\"name\":\"4排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-4\",\"x\":4,\"y\":4,\"name\":\"4排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-5\",\"x\":5,\"y\":4,\"name\":\"4排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-6\",\"x\":6,\"y\":4,\"name\":\"4排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-7\",\"x\":7,\"y\":4,\"name\":\"4排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-8\",\"x\":8,\"y\":4,\"name\":\"4排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-9\",\"x\":9,\"y\":4,\"name\":\"4排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-10\",\"x\":10,\"y\":4,\"name\":\"4排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-11\",\"x\":11,\"y\":4,\"name\":\"4排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-12\",\"x\":12,\"y\":4,\"name\":\"4排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-13\",\"x\":13,\"y\":4,\"name\":\"4排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-14\",\"x\":14,\"y\":4,\"name\":\"4排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-15\",\"x\":15,\"y\":4,\"name\":\"4排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-16\",\"x\":16,\"y\":4,\"name\":\"4排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"1-4-17\",\"x\":17,\"y\":4,\"name\":\"4排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":false,\"rowName\":\"5\"},{\"id\":\"1-5-1\",\"x\":1,\"y\":5,\"name\":\"5排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-2\",\"x\":2,\"y\":5,\"name\":\"5排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-3\",\"x\":3,\"y\":5,\"name\":\"5排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-4\",\"x\":4,\"y\":5,\"name\":\"5排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-5\",\"x\":5,\"y\":5,\"name\":\"5排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-6\",\"x\":6,\"y\":5,\"name\":\"5排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-7\",\"x\":7,\"y\":5,\"name\":\"5排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-8\",\"x\":8,\"y\":5,\"name\":\"5排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-9\",\"x\":9,\"y\":5,\"name\":\"5排9座\",\"type\":2,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-10\",\"x\":10,\"y\":5,\"name\":\"5排10座\",\"type\":3,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-11\",\"x\":11,\"y\":5,\"name\":\"5排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-12\",\"x\":12,\"y\":5,\"name\":\"5排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-13\",\"x\":13,\"y\":5,\"name\":\"5排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-14\",\"x\":14,\"y\":5,\"name\":\"5排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-15\",\"x\":15,\"y\":5,\"name\":\"5排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-16\",\"x\":16,\"y\":5,\"name\":\"5排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"1-5-17\",\"x\":17,\"y\":5,\"name\":\"5排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":false,\"rowName\":\"6\"},{\"id\":\"1-6-1\",\"x\":1,\"y\":6,\"name\":\"6排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-2\",\"x\":2,\"y\":6,\"name\":\"6排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-3\",\"x\":3,\"y\":6,\"name\":\"6排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-4\",\"x\":4,\"y\":6,\"name\":\"6排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-5\",\"x\":5,\"y\":6,\"name\":\"6排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-6\",\"x\":6,\"y\":6,\"name\":\"6排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-7\",\"x\":7,\"y\":6,\"name\":\"6排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-8\",\"x\":8,\"y\":6,\"name\":\"6排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-9\",\"x\":9,\"y\":6,\"name\":\"6排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-10\",\"x\":10,\"y\":6,\"name\":\"6排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-11\",\"x\":11,\"y\":6,\"name\":\"6排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-12\",\"x\":12,\"y\":6,\"name\":\"6排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-13\",\"x\":13,\"y\":6,\"name\":\"6排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-14\",\"x\":14,\"y\":6,\"name\":\"6排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-15\",\"x\":15,\"y\":6,\"name\":\"6排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-16\",\"x\":16,\"y\":6,\"name\":\"6排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"1-6-17\",\"x\":17,\"y\":6,\"name\":\"6排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":false,\"rowName\":\"7\"},{\"id\":\"1-7-1\",\"x\":1,\"y\":7,\"name\":\"7排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-2\",\"x\":2,\"y\":7,\"name\":\"7排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-3\",\"x\":3,\"y\":7,\"name\":\"7排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-4\",\"x\":4,\"y\":7,\"name\":\"7排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-5\",\"x\":5,\"y\":7,\"name\":\"7排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-6\",\"x\":6,\"y\":7,\"name\":\"7排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-7\",\"x\":7,\"y\":7,\"name\":\"7排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-8\",\"x\":8,\"y\":7,\"name\":\"7排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-9\",\"x\":9,\"y\":7,\"name\":\"7排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-10\",\"x\":10,\"y\":7,\"name\":\"7排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-11\",\"x\":11,\"y\":7,\"name\":\"7排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-12\",\"x\":12,\"y\":7,\"name\":\"7排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-13\",\"x\":13,\"y\":7,\"name\":\"7排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-14\",\"x\":14,\"y\":7,\"name\":\"7排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-15\",\"x\":15,\"y\":7,\"name\":\"7排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-16\",\"x\":16,\"y\":7,\"name\":\"7排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-7-17\",\"x\":17,\"y\":7,\"name\":\"7排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"8\"},{\"id\":\"1-8-1\",\"x\":1,\"y\":8,\"name\":\"8排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-2\",\"x\":2,\"y\":8,\"name\":\"8排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-3\",\"x\":3,\"y\":8,\"name\":\"8排3座\",\"type\":2,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-4\",\"x\":4,\"y\":8,\"name\":\"8排4座\",\"type\":3,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-5\",\"x\":5,\"y\":8,\"name\":\"8排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-6\",\"x\":6,\"y\":8,\"name\":\"8排6座\",\"type\":2,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-7\",\"x\":7,\"y\":8,\"name\":\"8排7座\",\"type\":3,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-8\",\"x\":8,\"y\":8,\"name\":\"8排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-9\",\"x\":9,\"y\":8,\"name\":\"8排9座\",\"type\":2,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-10\",\"x\":10,\"y\":8,\"name\":\"8排10座\",\"type\":3,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-11\",\"x\":11,\"y\":8,\"name\":\"8排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-12\",\"x\":12,\"y\":8,\"name\":\"8排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-13\",\"x\":13,\"y\":8,\"name\":\"8排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-14\",\"x\":14,\"y\":8,\"name\":\"8排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-15\",\"x\":15,\"y\":8,\"name\":\"8排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-16\",\"x\":16,\"y\":8,\"name\":\"8排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-8-17\",\"x\":17,\"y\":8,\"name\":\"8排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"9\"},{\"id\":\"1-9-1\",\"x\":1,\"y\":9,\"name\":\"9排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-2\",\"x\":2,\"y\":9,\"name\":\"9排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-3\",\"x\":3,\"y\":9,\"name\":\"9排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-4\",\"x\":4,\"y\":9,\"name\":\"9排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-5\",\"x\":5,\"y\":9,\"name\":\"9排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-6\",\"x\":6,\"y\":9,\"name\":\"9排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-7\",\"x\":7,\"y\":9,\"name\":\"9排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-8\",\"x\":8,\"y\":9,\"name\":\"9排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-9\",\"x\":9,\"y\":9,\"name\":\"9排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-10\",\"x\":10,\"y\":9,\"name\":\"9排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-11\",\"x\":11,\"y\":9,\"name\":\"9排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-12\",\"x\":12,\"y\":9,\"name\":\"9排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-13\",\"x\":13,\"y\":9,\"name\":\"9排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-14\",\"x\":14,\"y\":9,\"name\":\"9排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-15\",\"x\":15,\"y\":9,\"name\":\"9排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-16\",\"x\":16,\"y\":9,\"name\":\"9排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-9-17\",\"x\":17,\"y\":9,\"name\":\"9排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"10\"},{\"id\":\"1-10-1\",\"x\":1,\"y\":10,\"name\":\"10排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-2\",\"x\":2,\"y\":10,\"name\":\"10排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-3\",\"x\":3,\"y\":10,\"name\":\"10排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-4\",\"x\":4,\"y\":10,\"name\":\"10排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-5\",\"x\":5,\"y\":10,\"name\":\"10排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-6\",\"x\":6,\"y\":10,\"name\":\"10排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-7\",\"x\":7,\"y\":10,\"name\":\"10排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-8\",\"x\":8,\"y\":10,\"name\":\"10排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-9\",\"x\":9,\"y\":10,\"name\":\"10排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-10\",\"x\":10,\"y\":10,\"name\":\"10排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-11\",\"x\":11,\"y\":10,\"name\":\"10排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-12\",\"x\":12,\"y\":10,\"name\":\"10排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-13\",\"x\":13,\"y\":10,\"name\":\"10排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-14\",\"x\":14,\"y\":10,\"name\":\"10排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-15\",\"x\":15,\"y\":10,\"name\":\"10排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-16\",\"x\":16,\"y\":10,\"name\":\"10排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-10-17\",\"x\":17,\"y\":10,\"name\":\"10排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"11\"},{\"id\":\"1-11-1\",\"x\":1,\"y\":11,\"name\":\"11排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-2\",\"x\":2,\"y\":11,\"name\":\"11排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-3\",\"x\":3,\"y\":11,\"name\":\"11排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-4\",\"x\":4,\"y\":11,\"name\":\"11排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-5\",\"x\":5,\"y\":11,\"name\":\"11排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-6\",\"x\":6,\"y\":11,\"name\":\"11排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-7\",\"x\":7,\"y\":11,\"name\":\"11排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-8\",\"x\":8,\"y\":11,\"name\":\"11排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-9\",\"x\":9,\"y\":11,\"name\":\"11排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-10\",\"x\":10,\"y\":11,\"name\":\"11排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-11\",\"x\":11,\"y\":11,\"name\":\"11排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-12\",\"x\":12,\"y\":11,\"name\":\"11排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-13\",\"x\":13,\"y\":11,\"name\":\"11排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-14\",\"x\":14,\"y\":11,\"name\":\"11排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-15\",\"x\":15,\"y\":11,\"name\":\"11排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-16\",\"x\":16,\"y\":11,\"name\":\"11排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-11-17\",\"x\":17,\"y\":11,\"name\":\"11排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"12\"},{\"id\":\"1-12-1\",\"x\":1,\"y\":12,\"name\":\"12排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-2\",\"x\":2,\"y\":12,\"name\":\"12排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-3\",\"x\":3,\"y\":12,\"name\":\"12排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-4\",\"x\":4,\"y\":12,\"name\":\"12排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-5\",\"x\":5,\"y\":12,\"name\":\"12排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-6\",\"x\":6,\"y\":12,\"name\":\"12排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-7\",\"x\":7,\"y\":12,\"name\":\"12排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-8\",\"x\":8,\"y\":12,\"name\":\"12排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-9\",\"x\":9,\"y\":12,\"name\":\"12排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-10\",\"x\":10,\"y\":12,\"name\":\"12排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-11\",\"x\":11,\"y\":12,\"name\":\"12排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-12\",\"x\":12,\"y\":12,\"name\":\"12排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-13\",\"x\":13,\"y\":12,\"name\":\"12排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-14\",\"x\":14,\"y\":12,\"name\":\"12排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-15\",\"x\":15,\"y\":12,\"name\":\"12排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-16\",\"x\":16,\"y\":12,\"name\":\"12排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-12-17\",\"x\":17,\"y\":12,\"name\":\"12排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"13\"},{\"id\":\"1-13-1\",\"x\":1,\"y\":13,\"name\":\"13排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-2\",\"x\":2,\"y\":13,\"name\":\"13排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-3\",\"x\":3,\"y\":13,\"name\":\"13排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-4\",\"x\":4,\"y\":13,\"name\":\"13排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-5\",\"x\":5,\"y\":13,\"name\":\"13排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-6\",\"x\":6,\"y\":13,\"name\":\"13排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-7\",\"x\":7,\"y\":13,\"name\":\"13排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-8\",\"x\":8,\"y\":13,\"name\":\"13排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-9\",\"x\":9,\"y\":13,\"name\":\"13排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-10\",\"x\":10,\"y\":13,\"name\":\"13排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-11\",\"x\":11,\"y\":13,\"name\":\"13排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-12\",\"x\":12,\"y\":13,\"name\":\"13排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-13\",\"x\":13,\"y\":13,\"name\":\"13排13座\",\"type\":0,\"seatNumber\":\"13\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-14\",\"x\":14,\"y\":13,\"name\":\"13排14座\",\"type\":0,\"seatNumber\":\"14\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-15\",\"x\":15,\"y\":13,\"name\":\"13排15座\",\"type\":0,\"seatNumber\":\"15\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-16\",\"x\":16,\"y\":13,\"name\":\"13排16座\",\"type\":0,\"seatNumber\":\"16\",\"status\":true,\"rowName\":\"14\"},{\"id\":\"1-13-17\",\"x\":17,\"y\":13,\"name\":\"13排17座\",\"type\":0,\"seatNumber\":\"17\",\"status\":true,\"rowName\":\"14\"}]}],\"seatColumnCount\":18,\"seatRowCount\":14,\"centerX\":9,\"centerY\":7,\"remainSeat\":214,\"orderMsg\":\"\",\"subOrderId\":0,\"orderId\":0,\"orderExplains\":[],\"isSale\":true,\"isAutoSelected\":true}"

        val json = "{\"error\":\"\",\"mobile\":\"\",\"maxAutoSelectCount\":4,\"maxOrderTicketCount\":4,\"serviceFee\":300,\"cinemaId\":115291,\"cinemaName\":\"测试影院123\",\"hallName\":\"1号奥特曼激光厅\",\"hallSpecialDes\":\"关于儿童是否需要购票，以及3D眼镜是否需要自备等特殊性规定，建议您观影前，咨询影院了解\",\"movieId\":72543,\"movieName\":\"速度与激情速度与激情\",\"realTime\":1653724800000,\"movieLength\":133,\"versionDesc\":\"3D\",\"language\":\"汉语普通话\",\"dateMessage\":\"\",\"areaList\":[{\"areaCode\":\"0\",\"areaName\":\"舒适区\",\"areaPrice\":{\"salesPrice\":500,\"ticketPrice\":100,\"totalFee\":300},\"seatList\":[{\"id\":\"000000020066-1-1\",\"x\":1,\"y\":9,\"name\":\"楼上-1排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"1\"}]},{\"areaCode\":\"1\",\"areaName\":\"优选区\",\"areaPrice\":{\"salesPrice\":600,\"ticketPrice\":100,\"totalFee\":300},\"seatList\":[{\"id\":\"000000020066-1-2\",\"x\":2,\"y\":9,\"name\":\"楼上-1排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"1\"}]},{\"areaCode\":\"2\",\"areaName\":\"豪华区\",\"areaPrice\":{\"salesPrice\":700,\"ticketPrice\":100,\"totalFee\":300},\"seatList\":[{\"id\":\"000000020066-1-3\",\"x\":3,\"y\":9,\"name\":\"楼上-1排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000020066-1-4\",\"x\":4,\"y\":9,\"name\":\"楼上-1排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000020066-2-1\",\"x\":1,\"y\":10,\"name\":\"楼上-2排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000020066-2-2\",\"x\":2,\"y\":10,\"name\":\"楼上-2排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000020066-2-3\",\"x\":3,\"y\":10,\"name\":\"楼上-2排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000020066-2-4\",\"x\":4,\"y\":10,\"name\":\"楼上-2排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000020066-3-1\",\"x\":1,\"y\":11,\"name\":\"楼上-3排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000020066-3-2\",\"x\":2,\"y\":11,\"name\":\"楼上-3排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000020066-3-3\",\"x\":3,\"y\":11,\"name\":\"楼上-3排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000020066-3-4\",\"x\":4,\"y\":11,\"name\":\"楼上-3排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"3\"}]},{\"areaCode\":\"1262\",\"areaName\":\"尊享区\",\"areaPrice\":{\"salesPrice\":400,\"ticketPrice\":100,\"totalFee\":300},\"seatList\":[{\"id\":\"000000015008-1-1\",\"x\":1,\"y\":1,\"name\":\"默认场区1-1排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000015008-1-2\",\"x\":2,\"y\":1,\"name\":\"默认场区1-1排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000015008-1-3\",\"x\":3,\"y\":1,\"name\":\"默认场区1-1排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000015008-1-4\",\"x\":4,\"y\":1,\"name\":\"默认场区1-1排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000015008-1-6\",\"x\":6,\"y\":1,\"name\":\"默认场区1-1排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000015008-1-7\",\"x\":7,\"y\":1,\"name\":\"默认场区1-1排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"000000015008-1-8\",\"x\":8,\"y\":1,\"name\":\"默认场区1-1排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"00000015008-1-11\",\"x\":11,\"y\":1,\"name\":\"默认场区1-1排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"1\"},{\"id\":\"00000015008-1-12\",\"x\":12,\"y\":1,\"name\":\"默认场区1-1排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":false,\"rowName\":\"1\"},{\"id\":\"000000015008-2-1\",\"x\":1,\"y\":2,\"name\":\"默认场区1-2排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-2-2\",\"x\":2,\"y\":2,\"name\":\"默认场区1-2排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-2-3\",\"x\":3,\"y\":2,\"name\":\"默认场区1-2排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-2-4\",\"x\":4,\"y\":2,\"name\":\"默认场区1-2排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-2-6\",\"x\":6,\"y\":2,\"name\":\"默认场区1-2排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-2-7\",\"x\":7,\"y\":2,\"name\":\"默认场区1-2排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-2-8\",\"x\":8,\"y\":2,\"name\":\"默认场区1-2排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"00000015008-2-11\",\"x\":11,\"y\":2,\"name\":\"默认场区1-2排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"00000015008-2-12\",\"x\":12,\"y\":2,\"name\":\"默认场区1-2排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"2\"},{\"id\":\"000000015008-3-1\",\"x\":1,\"y\":3,\"name\":\"默认场区1-3排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-3-2\",\"x\":2,\"y\":3,\"name\":\"默认场区1-3排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-3-3\",\"x\":3,\"y\":3,\"name\":\"默认场区1-3排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-3-4\",\"x\":4,\"y\":3,\"name\":\"默认场区1-3排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-3-6\",\"x\":6,\"y\":3,\"name\":\"默认场区1-3排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-3-7\",\"x\":7,\"y\":3,\"name\":\"默认场区1-3排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-3-8\",\"x\":8,\"y\":3,\"name\":\"默认场区1-3排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"00000015008-3-11\",\"x\":11,\"y\":3,\"name\":\"默认场区1-3排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"00000015008-3-12\",\"x\":12,\"y\":3,\"name\":\"默认场区1-3排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"3\"},{\"id\":\"000000015008-4-1\",\"x\":1,\"y\":4,\"name\":\"默认场区1-4排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"000000015008-4-2\",\"x\":2,\"y\":4,\"name\":\"默认场区1-4排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"000000015008-4-3\",\"x\":3,\"y\":4,\"name\":\"默认场区1-4排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":false,\"rowName\":\"4\"},{\"id\":\"000000015008-4-4\",\"x\":4,\"y\":4,\"name\":\"默认场区1-4排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":false,\"rowName\":\"4\"},{\"id\":\"000000015008-4-6\",\"x\":6,\"y\":4,\"name\":\"默认场区1-4排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"000000015008-4-7\",\"x\":7,\"y\":4,\"name\":\"默认场区1-4排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"000000015008-4-8\",\"x\":8,\"y\":4,\"name\":\"默认场区1-4排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"00000015008-4-11\",\"x\":11,\"y\":4,\"name\":\"默认场区1-4排8座\",\"type\":2,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"4\"},{\"id\":\"00000015008-4-12\",\"x\":12,\"y\":4,\"name\":\"默认场区1-4排9座\",\"type\":3,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"4\"}]},{\"areaCode\":\"1969\",\"areaName\":\"至臻区\",\"areaPrice\":{\"salesPrice\":900,\"ticketPrice\":100,\"totalFee\":300},\"seatList\":[{\"id\":\"000000015008-5-1\",\"x\":1,\"y\":5,\"name\":\"默认场区1-5排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-2\",\"x\":2,\"y\":5,\"name\":\"默认场区1-5排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-3\",\"x\":3,\"y\":5,\"name\":\"默认场区1-5排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-4\",\"x\":4,\"y\":5,\"name\":\"默认场区1-5排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-5\",\"x\":5,\"y\":5,\"name\":\"默认场区1-5排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-6\",\"x\":6,\"y\":5,\"name\":\"默认场区1-5排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-7\",\"x\":7,\"y\":5,\"name\":\"默认场区1-5排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-8\",\"x\":8,\"y\":5,\"name\":\"默认场区1-5排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-5-9\",\"x\":9,\"y\":5,\"name\":\"默认场区1-5排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"00000015008-5-10\",\"x\":10,\"y\":5,\"name\":\"默认场区1-5排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"00000015008-5-11\",\"x\":11,\"y\":5,\"name\":\"默认场区1-5排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"00000015008-5-12\",\"x\":12,\"y\":5,\"name\":\"默认场区1-5排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"5\"},{\"id\":\"000000015008-6-1\",\"x\":1,\"y\":6,\"name\":\"默认场区1-6排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-2\",\"x\":2,\"y\":6,\"name\":\"默认场区1-6排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-3\",\"x\":3,\"y\":6,\"name\":\"默认场区1-6排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-4\",\"x\":4,\"y\":6,\"name\":\"默认场区1-6排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-5\",\"x\":5,\"y\":6,\"name\":\"默认场区1-6排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-6\",\"x\":6,\"y\":6,\"name\":\"默认场区1-6排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-7\",\"x\":7,\"y\":6,\"name\":\"默认场区1-6排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-6-8\",\"x\":8,\"y\":6,\"name\":\"默认场区1-6排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":false,\"rowName\":\"6\"},{\"id\":\"000000015008-6-9\",\"x\":9,\"y\":6,\"name\":\"默认场区1-6排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":false,\"rowName\":\"6\"},{\"id\":\"00000015008-6-10\",\"x\":10,\"y\":6,\"name\":\"默认场区1-6排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"00000015008-6-11\",\"x\":11,\"y\":6,\"name\":\"默认场区1-6排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"00000015008-6-12\",\"x\":12,\"y\":6,\"name\":\"默认场区1-6排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"6\"},{\"id\":\"000000015008-7-1\",\"x\":1,\"y\":7,\"name\":\"默认场区1-7排1座\",\"type\":0,\"seatNumber\":\"1\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-2\",\"x\":2,\"y\":7,\"name\":\"默认场区1-7排2座\",\"type\":0,\"seatNumber\":\"2\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-3\",\"x\":3,\"y\":7,\"name\":\"默认场区1-7排3座\",\"type\":0,\"seatNumber\":\"3\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-4\",\"x\":4,\"y\":7,\"name\":\"默认场区1-7排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-5\",\"x\":5,\"y\":7,\"name\":\"默认场区1-7排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-6\",\"x\":6,\"y\":7,\"name\":\"默认场区1-7排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-7\",\"x\":7,\"y\":7,\"name\":\"默认场区1-7排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-8\",\"x\":8,\"y\":7,\"name\":\"默认场区1-7排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-7-9\",\"x\":9,\"y\":7,\"name\":\"默认场区1-7排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"00000015008-7-10\",\"x\":10,\"y\":7,\"name\":\"默认场区1-7排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"00000015008-7-11\",\"x\":11,\"y\":7,\"name\":\"默认场区1-7排11座\",\"type\":0,\"seatNumber\":\"11\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"00000015008-7-12\",\"x\":12,\"y\":7,\"name\":\"默认场区1-7排12座\",\"type\":0,\"seatNumber\":\"12\",\"status\":true,\"rowName\":\"7\"},{\"id\":\"000000015008-8-6\",\"x\":6,\"y\":8,\"name\":\"默认场区1-8排4座\",\"type\":0,\"seatNumber\":\"4\",\"status\":false,\"rowName\":\"8\"},{\"id\":\"000000015008-8-7\",\"x\":7,\"y\":8,\"name\":\"默认场区1-8排5座\",\"type\":0,\"seatNumber\":\"5\",\"status\":false,\"rowName\":\"8\"},{\"id\":\"000000015008-8-8\",\"x\":8,\"y\":8,\"name\":\"默认场区1-8排6座\",\"type\":0,\"seatNumber\":\"6\",\"status\":false,\"rowName\":\"8\"},{\"id\":\"000000015008-8-9\",\"x\":9,\"y\":8,\"name\":\"默认场区1-8排7座\",\"type\":0,\"seatNumber\":\"7\",\"status\":false,\"rowName\":\"8\"},{\"id\":\"00000015008-8-10\",\"x\":10,\"y\":8,\"name\":\"默认场区1-8排8座\",\"type\":0,\"seatNumber\":\"8\",\"status\":false,\"rowName\":\"8\"},{\"id\":\"00000015008-8-11\",\"x\":11,\"y\":8,\"name\":\"默认场区1-8排9座\",\"type\":0,\"seatNumber\":\"9\",\"status\":false,\"rowName\":\"8\"},{\"id\":\"00000015008-8-12\",\"x\":12,\"y\":8,\"name\":\"默认场区1-8排10座\",\"type\":0,\"seatNumber\":\"10\",\"status\":false,\"rowName\":\"8\"}]}],\"seatColumnCount\":13,\"seatRowCount\":12,\"centerX\":6,\"centerY\":6,\"remainSeat\":79,\"orderMsg\":\"\",\"subOrderId\":0,\"orderId\":0,\"orderExplains\":[],\"isSale\":true,\"isAutoSelected\":true}"
        return Gson().fromJson(json, SeatInfo::class.java)
    }

}
