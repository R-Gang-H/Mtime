package com.kotlin.android.film.widget.seat

import java.util.ArrayList

/**
 * 座位选择器接口
 *
 * [ITEM] Seat
 * [DATA] SeatInfo
 *
 * Created on 2022/2/9.
 *
 * @author o.s
 */
interface SeatManager<ITEM, DATA> {

    /**
     * 屏幕名称
     */
    fun getScreenName(): String

    /**
     * 给管理器设置视图Vie
     */
    fun setSeatView(view: SeatView)

    /**
     * 是否分区
     */
    fun isArea(): Boolean

    /**
     * 获取最大行数
     *
     * @return 行数
     */
    fun getMaxRow(): Int

    /**
     * 获取最大列数
     *
     * @return 列数
     */
    fun getMaxColumn(): Int

    /**
     * 获取最大选座的数量
     *
     * @return 最大选座的数量
     */
    fun getMaxSelectCount(): Int

    /**
     * 获取最大自动选座的数量
     *
     * @return 最大自动选座的数量
     */
    fun getMaxAutoSelectCount(): Int

    /**
     * 根据座位图规则获取中间行
     *
     * @return 中间行，靠后画中线
     */
    fun getCenterRow(): Int

    /**
     * 根据座位图规则获取中间列
     *
     * @return 中间列，靠后画中线
     */
    fun getCenterCol(): Int

    /**
     * 是否可用座位
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isValidSeat(row: Int, column: Int): Boolean

    /**
     * 是否已售
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isSold(row: Int, column: Int): Boolean

    /**
     * 是否已经选择
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isChecked(row: Int, column: Int): Boolean

    /**
     * 选择座位
     *
     * @param row row
     * @param column column
     */
    fun checked(row: Int, column: Int)

    /**
     * 取消选择
     *
     * @param row row
     * @param column column
     */
    fun unCheck(row: Int, column: Int)

    /**
     * 情侣座席左
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isCoupleLeftSeat(row: Int, column: Int): Boolean

    /**
     * 情侣座席右
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isCoupleRightSeat(row: Int, column: Int): Boolean

    /**
     * 残疾人座席
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isDisabilitySeat(row: Int, column: Int): Boolean

    /**
     * 待修理座席
     *
     * @param row row
     * @param column column
     *
     * @return true：待修理座位
     */
    fun isRepairSeat(row: Int, column: Int): Boolean
//
//    /**
//     * 活动座席
//     *
//     * @param row row
//     * @param column column
//     *
//     * @return boolean
//     */
//    fun isActivitySeat(row: Int, column: Int): Boolean

//    /**
//     * 获取选中后座位上显示的文字
//     *
//     * @param row row
//     * @param column column
//     *
//     * @return 返回2个元素的数组, 第一个元素是第一行的文字, 第二个元素是第二行文字, 如果只返回一个元素则会绘制到座位图的中间位置
//     */
//    fun checkedSeatTxt(row: Int, column: Int): Array<String>?

    /**
     * 获取遮罩效果的座位信息
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isCovered(row: Int, column: Int): Boolean

    /**
     * 获取座位矩阵数据
     *
     * @return ITEM[][]
     */
    fun getMatrix(): Array<Array<ITEM?>>

    /**
     * 选座规则
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isCheckRule(row: Int, column: Int): Boolean

    /**
     * 取消选座的规则
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    fun isUnCheckRule(row: Int, column: Int): Boolean

    /**
     * 取消情侣座规则
     *
     * @param row row
     * @param column column
     *
     * @return 返回可用的另一半情侣座位置
     */
    fun cancelCoupleRule(row: Int, column: Int): IntArray?

    /**
     * 设置行号
     */
    fun getIndexes(): List<String>

    /**
     * 获取座位 icon 类型
     *
     * @param row row
     * @param column column
     *
     * @return 座位 icon 类型
     */
    fun getSeatType(row: Int, column: Int): SeatType

//    /**
//     * 获取特殊座位类型
//     *
//     * @return SpecialSeatType
//     */
//    fun getSpecialSeatType(): SpecialSeatType

    /**
     * 获取座位样式
     *
     * @return SeatStyle
     */
    fun getSeatStyle(): SeatStyle

    /**
     * 设置座位样式
     */
    fun setSeatStyle(seatStyle: SeatStyle)

//    /**
//     * 有广告位
//     *
//     * @return boolean
//     */
//    fun hasAdvertisingSpace(): Boolean
//
//    /**
//     * 获取活动Icon路径
//     *
//     * @return String
//     */
//    fun getActivityIconPath(): Array<String>?
//
//    /**
//     * 获取活动座位图iconId
//     *
//     * @return 活动座位图iconId
//     */
//    fun getActivityIconId(): Int
//
//    fun getIconId(): Int
//
//    fun getIconId(row: Int, column: Int): Int
//
//    fun isMultiIconIdSeat(): Boolean

//    /**
//     * 获取活动文案
//     *
//     * @return String
//     */
//    fun getActivityText(): String
//
//    /**
//     * 获取已选座位图路径列表
//     *
//     * @return List<String>
//     */
//    fun getSelectedIconPath(): ArrayList<String>?
//
//    /**
//     * 获取已售座位图路径列表
//     *
//     * @return List<String>
//     */
//    fun getDisabledIconPath(): ArrayList<String>?

//    /**
//     * 播放音频文件字幕
//     *
//     * @param audioPath 音频文件路径
//     * @param subtitles 字幕
//     */
//    fun playAudio(audioPath: String?, subtitles: String?)
//
//    fun stopMedia()
//
//    fun getMediaSource(): SeatPlugins.MediaSource?
//
//    fun setMediaSource(media: SeatPlugins.MediaSource)

    /**
     * 设置数据源
     *
     * @param data 设置数据源
     */
    fun setData(data: DATA)

    /**
     * 获取数据源
     *
     * @return DATA
     */
    fun getData(): DATA

    /**
     * 通知数据发生改变，刷新view
     */
    fun notifyDataSetChanged()

    /**
     * 获取选择的座位列表
     *
     * @return ArrayList
     */
    fun getSelectList(): ArrayList<ITEM>

    /**
     * 取消选择
     *
     * @param data data
     */
    fun cancelSeat(data: ITEM)

    /**
     * 取消选择指定座位列表
     *
     * @param seats seats
     */
    fun cancelSeatAll(seats: List<ITEM>)

    /**
     * 选择座位
     * @param data data
     */
    fun selectedSeat(data: ITEM)

    /**
     * 选择指定座位列表
     *
     * @param seatIds seatIds
     */
    fun selectedSeatAll(seatIds: List<String>)

    /**
     * 检查选中的座位列表是否符合选座规则
     *
     * @return boolean
     */
    fun checkSelectedListSeat(): Boolean

    /**
     * 获取分区列表
     *
     * @return 分区列表
     */
    fun getArea(): List<AreaFlag>

//    /**
//     * 设置是否有专享座席
//     *
//     * @param hasExclusiveSeat true：有专享座席；false：反之。
//     */
//    fun setExclusiveSeat(hasExclusiveSeat: Boolean)
//
//    /**
//     * 有活动专享座席
//     *
//     * @return true 有；反之。
//     */
//    fun hasExclusiveSeat(): Boolean

    /**
     * 有情侣座席
     *
     * @return true 有；反之。
     */
    fun hasCoupleSeat(): Boolean

    /**
     * 有残疾人座席
     *
     * @return true 有；反之。
     */
    fun hasDisabilitySeat(): Boolean

    /**
     * 有待修座席
     *
     * @return true 有；反之。
     */
    fun hasRepairSeat(): Boolean

//    /**
//     * 是否有营销活动
//     *
//     * @return true 有；反之。
//     */
//    fun hasActivity(): Boolean

//    /**
//     * 通知活动广告空间布局
//     *
//     * @param rect 布局rect
//     */
//    fun notifyAdvertisingSpaceLayout(rect: Rect)

    /**
     * 回调接口
     * @param <ITEM>
     */
    interface OnClickListener<in ITEM> {

        /**
         * 选择座席
         *
         * @param info info
         */
        fun selectedSeat(info: ITEM)

        /**
         * 取消选择座席
         *
         * @param info info
         */
        fun cancelSeat(info: ITEM)

//        /**
//         * 选择活动座席
//         *
//         * @param info info
//         */
//        fun selectedActivitySeat(info: ITEM)

        /**
         * 允许访问点击
         *
         * @return true 允许点击座位图，反之。
         */
        fun allowAccessClick(): Boolean

//        /**
//         * 布局活动广告空间
//         *
//         * @param rect 布局rect
//         */
//        fun layoutAdvertisingSpace(rect: Rect)
    }
}