package com.kotlin.android.film.widget.seat.manager

import com.kotlin.android.app.data.entity.film.seat.Area
import com.kotlin.android.app.data.entity.film.seat.Seat
import com.kotlin.android.app.data.entity.film.seat.SeatInfo
import com.kotlin.android.film.widget.seat.*
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.showToast
import java.util.ArrayList

/**
 *
 * Created on 2022/2/17.
 *
 * @author o.s
 */
class MTimeSeatManager(
    private val listener: SeatManager.OnClickListener<Seat>
) : SeatManager<Seat, SeatInfo> {

    companion object {
        // 已售 座位状态 NOTSET(0,"未设置"),SALE(1,"可售"),NOTSALE(2,"不可售");
        const val STATUS_DISABLE = 2
        // 可选
        const val STATUS_OPTIONAL = 1
        // 已选
        const val STATUS_SELECTED = 3
        // 0普通座;1残疾人座;2情侣左座;3情侣右座
        const val TYPE_COMM = 0
        const val TYPE_DISABILITY = 1
        const val TYPE_COUPLE_L = 2
        const val TYPE_COUPLE_R = 3
        const val TYPE_REPAIR = 4
    }

    private var data: SeatInfo = SeatInfo()
    private var seatView: SeatView? = null

    /**
     * 屏幕名称
     */
    override fun getScreenName(): String {
        return data.hallName.orEmpty()
    }

    /**
     * 给管理器设置视图Vie
     */
    override fun setSeatView(view: SeatView) {
        this.seatView = view
    }

    /**
     * 是否分区
     */
    override fun isArea(): Boolean = getSeatStyle() == SeatStyle.AREA

    /**
     * 获取最大行数
     *
     * @return 行数
     */
    override fun getMaxRow(): Int = data.seatRowCount ?: 0

    /**
     * 获取最大列数
     *
     * @return 列数
     */
    override fun getMaxColumn(): Int = data.seatColumnCount ?: 0

    /**
     * 获取最大选座的数量
     *
     * @return 最大选座的数量
     */
    override fun getMaxSelectCount(): Int = data.maxOrderTicketCount

    /**
     * 获取最大自动选座的数量
     *
     * @return 最大自动选座的数量
     */
    override fun getMaxAutoSelectCount(): Int = data.maxAutoSelectCount

    /**
     * 根据座位图规则获取中间行
     *
     * @return 中间行，靠后画中线
     */
    override fun getCenterRow(): Int = (data.centerY ?: -1) + 1

    /**
     * 根据座位图规则获取中间列
     *
     * @return 中间列，靠后画中线
     */
    override fun getCenterCol(): Int = (data.centerX ?: -1) + 1

    /**
     * 是否可用座位
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isValidSeat(row: Int, column: Int): Boolean = dataMatrix[row][column] != null

    /**
     * 是否已售
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isSold(row: Int, column: Int): Boolean = dataMatrix[row][column]?.localStatus == STATUS_DISABLE

    /**
     * 是否已经选择
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isChecked(row: Int, column: Int): Boolean = dataMatrix[row][column]?.localStatus == STATUS_SELECTED

    /**
     * 选择座位
     *
     * @param row row
     * @param column column
     */
    override fun checked(row: Int, column: Int) {
        dataMatrix[row][column]?.apply {
            selected = true
            selectList.forEach {
                if (it.isSame(this)) {
                    return
                }
            }
            selectList.add(this)
            listener.selectedSeat(this)
        }
    }

    /**
     * 取消选择
     *
     * @param row row
     * @param column column
     */
    override fun unCheck(row: Int, column: Int) {
        dataMatrix[row][column]?.apply {
            selected = false
            selectList.remove(this)
            listener.cancelSeat(this)
        }
    }

    /**
     * 情侣座席左
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isCoupleLeftSeat(row: Int, column: Int): Boolean = dataMatrix[row][column]?.type == TYPE_COUPLE_L

    /**
     * 情侣座席右
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isCoupleRightSeat(row: Int, column: Int): Boolean = dataMatrix[row][column]?.type == TYPE_COUPLE_R

    /**
     * 残疾人座席
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isDisabilitySeat(row: Int, column: Int): Boolean = dataMatrix[row][column]?.type == TYPE_DISABILITY

    /**
     * 待修理座席
     *
     * @param row row
     * @param column column
     *
     * @return true：待修理座位
     */
    override fun isRepairSeat(row: Int, column: Int): Boolean = dataMatrix[row][column]?.type == TYPE_REPAIR

    /**
     * 获取遮罩效果的座位信息
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isCovered(row: Int, column: Int): Boolean = false

    /**
     * 获取座位矩阵数据
     *
     * @return ITEM[][]
     */
    override fun getMatrix(): Array<Array<Seat?>> = dataMatrix

    /**
     * 选座规则
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isCheckRule(row: Int, column: Int): Boolean {
        if (listener.allowAccessClick().not()) {
            return false
        }
        var isRule = false
        val arr = dataMatrix[row]
        val len = arr.size
        val c = arr[column] ?: return false

        // 情侣座规则拦截
        if (isSelectCoupleRule(row, column, c)) {
            return true
        }

        if (column == len - 1 || column == 0) {
            isRule = true
        } else if (column < len - 1) {
            val left = arr[column - 1]
            val right = arr[column + 1]
            if (left == null || right == null) {
                return true
            }
            val leftStatus = left.localStatus
            val rightStatus = right.localStatus
            if (leftStatus != STATUS_OPTIONAL && rightStatus == STATUS_OPTIONAL) {
                // 左不可选，右可选
                isRule = true
            } else if (leftStatus == STATUS_OPTIONAL && rightStatus != STATUS_OPTIONAL) {
                // 右不可选，左可选
                isRule = true
            } else if (leftStatus == STATUS_OPTIONAL) {
                // 两边都可选
                if (getLeft2(arr, column)) {
                    isRule = getRight2(arr, column)
                }
            } else {
                // 两边都不可选
                isRule = true
            }
        }

        if (!isRule) {
            if (selectList.size > 1) {
                showToast("请连续选择座位，不可留下单独空座")
            } else {
                showToast("座位旁边不要留空位")
            }
        }
        return isRule
    }

    /**
     * 取消选座的规则
     *
     * @param row row
     * @param column column
     *
     * @return boolean
     */
    override fun isUnCheckRule(row: Int, column: Int): Boolean = true

    /**
     * 取消情侣座规则
     *
     * @param row row
     * @param column column
     *
     * @return 返回可用的另一半情侣座位置
     */
    override fun cancelCoupleRule(row: Int, column: Int): IntArray? {
        val type = dataMatrix[row][column]?.type
        if (TYPE_COUPLE_L == type) {
            return cancelCoupleOther(row, column + 1, TYPE_COUPLE_R)
        } else if (TYPE_COUPLE_R == type) {
            return cancelCoupleOther(row, column - 1, TYPE_COUPLE_L)
        }
        return null
    }

    /**
     * 设置行号
     */
    override fun getIndexes(): List<String> = indexes

    /**
     * 获取座位 icon 类型
     *
     * @param row row
     * @param column column
     *
     * @return 座位 icon 类型
     */
    override fun getSeatType(row: Int, column: Int): SeatType {
        if (row < 0 || row > getMaxRow() || column < 0 || column > getMaxColumn()) {
            return SeatType.SEAT_BLANK
        }
        val data = dataMatrix[row][column] ?: return SeatType.SEAT_BLANK
        return if (SeatStyle.AREA === seatStyle) { // 分区选座
            getSeatTypeByArea(data.type.orZero(), data.localStatus, data.areaLevel)
        } else { // 一般选座
            getSeatTypeBySeat(data.type.orZero(), data.localStatus)
        }
    }

    /**
     * 获取座位样式
     *
     * @return SeatStyle
     */
    override fun getSeatStyle(): SeatStyle = seatStyle

    /**
     * 设置座位样式
     */
    override fun setSeatStyle(seatStyle: SeatStyle) {
        this.seatStyle = seatStyle
    }

    /**
     * 设置数据源
     *
     * @param data 设置数据源
     */
    override fun setData(data: SeatInfo) {
        this.data = data
        notifyDataSetChanged()
    }

    /**
     * 获取数据源
     *
     * @return DATA
     */
    override fun getData(): SeatInfo = data

    /**
     * 通知数据发生改变，刷新view
     */
    override fun notifyDataSetChanged() {
        reset()
        initData()
        seatView?.notifyDataChanged()
    }

    /**
     * 获取选择的座位列表
     *
     * @return ArrayList
     */
    override fun getSelectList(): ArrayList<Seat> = selectList

    /**
     * 取消选择
     *
     * @param data data
     */
    override fun cancelSeat(data: Seat) {
        seatView?.unCheckedSeat(data.y, data.x)
    }

    /**
     * 取消选择指定座位列表
     *
     * @param seats seats
     */
    override fun cancelSeatAll(seats: List<Seat>) {
        seats.forEach {
            seatView?.unCheckedSeat(it.y, it.x)
        }
    }

    /**
     * 选择座位
     * @param data data
     */
    override fun selectedSeat(data: Seat) {
        seatView?.checkedSeat(data.y, data.x)
    }

    /**
     * 选择指定座位列表
     *
     * @param seatIds seatIds
     */
    override fun selectedSeatAll(seatIds: List<String>) {
        seatIds.forEach { id ->
            seats.forEach { seat ->
                if (id == seat.id) {
                    seatView?.checkedSeat(seat.y, seat.x)
                }
            }
        }
    }

    /**
     * 检查选中的座位列表是否符合选座规则
     *
     * @return boolean
     */
    override fun checkSelectedListSeat(): Boolean {
        if (selectList.size <= 0) {
            showToast("请选择座位")
            return false
        }
        selectList.forEach {
            if (!isSelectedRule(it.y, it.x)) {
                return false
            }
        }
        return true
    }

    /**
     * 获取分区列表
     *
     * @return 分区列表
     */
    override fun getArea(): List<AreaFlag> = areaList

    /**
     * 有情侣座席
     *
     * @return true 有；反之。
     */
    override fun hasCoupleSeat(): Boolean = hasCoupleSeat
    /**
     * 有残疾人座席
     *
     * @return true 有；反之。
     */
    override fun hasDisabilitySeat(): Boolean = hasDisabilitySeat

    /**
     * 有待修座席
     *
     * @return true 有；反之。
     */
    override fun hasRepairSeat(): Boolean = hasRepairSeat

    private var dataMatrix: Array<Array<Seat?>> = Array(0) { arrayOfNulls<Seat?>(0) }
    private var areas: MutableList<Area> = ArrayList()
    private var seats: MutableList<Seat> = ArrayList()
    private var areaList: MutableList<AreaFlag> = ArrayList()
    private var indexes: MutableList<String> = ArrayList()
    private val selectList = ArrayList<Seat>()
    private var seatStyle = SeatStyle.NORMAL // 座位图样式
    
    private var hasCoupleSeat: Boolean = false // 情侣座席
    private var hasDisabilitySeat: Boolean = false // 残疾人座席
    private var hasRepairSeat: Boolean = false // 待维修座席

    private val minAreaLimit = 2 // 最小分区边界限制
    
    private fun reset() {
        hasCoupleSeat = false
        hasDisabilitySeat = false
        hasRepairSeat = false

        if (selectList.size > 0) {
            cancelSeatAll(selectList) // 清空已选列表
        }
    }

    private fun initData() {
        areas.clear()
        areaList.clear() // 初始化分区标记： ¥ 60
        seats.clear() // 初始化座位列表
        indexes.clear()

        data.areaList?.forEach { 
            areas.add(it)
        }
        if (areas.size >= minAreaLimit) {
            setSeatStyle(SeatStyle.AREA)
        } else {
            setSeatStyle(SeatStyle.NORMAL)
        }

        areas.forEach {
            areaList.add(AreaFlag.create(it))
        }
//        areaList.sortWith { o1, o2 -> o1.salesPrice - o2.salesPrice }
        areaList.indices.forEach {
            val flag = areaList[it]
            handleAreaColorByPosition(flag, it)
        }

        areas.forEach { area ->
            val areaFlag = areaList.firstOrNull { area.areaCode == it.areaId }
            val areaLevel = areaFlag?.areaLevel ?: AreaLevel.AREA_LEVEL_1

            area.seatList?.forEach { seat ->
                seat.areaId = area.areaCode.orEmpty()
                seat.areaLevel = areaLevel.value
                seat.showPrice = areaFlag?.salesPrice ?: 0
                seat.serviceFee = areaFlag?.totalFee ?: 0

                seats.add(seat)

                val type = seat.type
                if (!hasCoupleSeat && (type == 2 || type == 3)) {
                    hasCoupleSeat = true
                } else if (!hasDisabilitySeat && type == 1) {
                    hasDisabilitySeat = true
                }
            }
        }

        // 填充座位图矩阵数据
        val row = getMaxRow()
        val col = getMaxColumn()
        dataMatrix = Array(row) { arrayOfNulls<Seat?>(col) }
        seats.forEach {
            val rowIndex = it.y
            val colIndex = it.x
            if (rowIndex in 0 until row && colIndex in 0 until col) {
                dataMatrix[rowIndex][colIndex] = it
            }
        }

        dataMatrix.forEach {
            var rowName = ""
            it.forEach { seat ->
                val rName = seat?.rowName
                if (rName != null && rName.isNotEmpty()) {
                    rowName = rName
                    return@forEach
                }
            }
            indexes.add(rowName)
        }
    }

    private fun handleAreaColorByPosition(flag: AreaFlag, position: Int) {
        when (position) {
            0 -> flag.areaLevel = AreaLevel.AREA_LEVEL_1
            1 -> flag.areaLevel = AreaLevel.AREA_LEVEL_2
            2 -> flag.areaLevel = AreaLevel.AREA_LEVEL_3
            3 -> flag.areaLevel = AreaLevel.AREA_LEVEL_4
            4 -> flag.areaLevel = AreaLevel.AREA_LEVEL_5
            else -> flag.areaLevel = AreaLevel.AREA_LEVEL_1
        }
    }

    private fun isSelectedRule(row: Int, column: Int): Boolean {
        if (!listener.allowAccessClick()) {
            return false
        }
        var isRule = false
        val arr = dataMatrix[row]
        val len = arr.size
        val c = arr[column]

        if (null == c) {
            showToast("请选择座位")
            return false
        }

        if (column == len - 1 || column == 0) {
            isRule = true
        } else if (column < len - 1) {
            val left = arr[column - 1]
            val right = arr[column + 1]
            val leftStatus = left?.localStatus
            val rightStatus = right?.localStatus
            if (leftStatus != STATUS_OPTIONAL && rightStatus == STATUS_OPTIONAL) {
                // 左不可选，右可选
                isRule = true
            } else if (leftStatus == STATUS_OPTIONAL && rightStatus != STATUS_OPTIONAL) {
                // 右不可选，左可选
                isRule = true
            } else if (leftStatus == STATUS_OPTIONAL) {
                // 两边都可选
                if (getLeft2(arr, column)) {
                    isRule = getRight2(arr, column)
                }
            } else {
                // 两边都不可选
                isRule = true
            }
        }

        if (!isRule) {
            if (selectList.size > 1) {
                showToast("请连续选择座位，不可留下单独空座")
            } else {
                showToast("座位旁边不要留空位")
            }
        }

        return isRule
    }

    /**
     * 情侣座选择规则
     *
     * @param row row
     * @param column column
     * @param seat seat
     *
     * @return true：表示是情侣座；false：反之。
     */
    private fun isSelectCoupleRule(row: Int, column: Int, seat: Seat): Boolean {
        val type = seat.type
        if (TYPE_COUPLE_L == type) {
            if (selectCoupleOther(row, column + 1, TYPE_COUPLE_R)) {
                return true
            }
        } else if (TYPE_COUPLE_R == type) {
            if (selectCoupleOther(row, column - 1, TYPE_COUPLE_L)) {
                return true
            }
        }
        return false
    }

    /**
     * 连选情侣座的另一个座位
     *
     * @param row row
     * @param column column
     * @param targetType 另一个情侣座位类型
     *
     * @return true：表示选择另一个情侣座；false：反之。
     */
    private fun selectCoupleOther(row: Int, column: Int, targetType: Int): Boolean {
        return dataMatrix[row][column]?.let {
            if (targetType == it.type && STATUS_OPTIONAL == it.localStatus) {
                selectedSeat(it)
                true
            } else {
                false
            }
        } ?: false
    }

    /**
     * 连续取消情侣座的另一个座位
     *
     * @param row row
     * @param column column
     * @param targetType 另一个情侣座位类型
     *
     * @return true：表示取消另一个情侣座；false：反之。
     */
    private fun cancelCoupleOther(row: Int, column: Int, targetType: Int): IntArray? {
        return dataMatrix[row][column]?.let {
            if (targetType == it.type && STATUS_SELECTED == it.localStatus) {
                val pos = IntArray(2)
                pos[0] = row
                pos[1] = column
                pos
            } else {
                null
            }
        }
    }

    /**
     * 获取左边第二个座位，判断是否可用
     *
     * @param arr arr
     * @param column column
     *
     * @return true 表示可选；反之。
     */
    private fun getLeft2(arr: Array<Seat?>, column: Int): Boolean {
        if (column > 1) {
            val left2 = arr[column - 2]
            return when (left2?.localStatus) {
                STATUS_OPTIONAL -> true
                STATUS_SELECTED -> false
                else -> false
            }
        }
        return false
    }

    /**
     * 获取右边第二个座位信息，判断是否可用
     *
     * @param arr arr
     * @param column column
     *
     * @return true 表示可选；反之。
     */
    private fun getRight2(arr: Array<Seat?>, column: Int): Boolean {
        val len = arr.size
        if (column < len - 2) {
            val right2 = arr[column + 2]
            return when (right2?.localStatus) {
                STATUS_OPTIONAL -> true
                STATUS_SELECTED -> false
                else -> false
            }
        }
        return false
    }

    /**
     * 获取一般座位图座位 item 类型
     *
     * @param type 座位类型 NOTSET(0,"未设置"),COMM(1,"普通"),DISABLE(2,"残疾"),LEFTLOVER(3,"情侣左"),RIGHTLOVER(4,"情侣右"),(5,"维修");
     * @param status 座位状态 NOTSET(0,"未设置"),SALE(1,"可售"),NOTSALE(2,"不可售");
     *
     * @return 一般座位图座位 item 类型
     */
    private fun getSeatTypeBySeat(type: Int, status: Int): SeatType {
        return when (type) {
            TYPE_COMM -> when (status) {
                STATUS_OPTIONAL -> SeatType.SEAT_OPTIONAL // (1,"可选")
                STATUS_DISABLE -> SeatType.SEAT_DISABLED // (2,"已售")
                STATUS_SELECTED -> SeatType.SEAT_SELECTED // (3,"已选")
                else -> SeatType.SEAT_BLANK
            }
            TYPE_DISABILITY -> when (status) {
                STATUS_OPTIONAL -> SeatType.SEAT_DISABILITY_OPTIONAL
                STATUS_DISABLE -> SeatType.SEAT_DISABLED
                STATUS_SELECTED -> SeatType.SEAT_DISABILITY_SELECTED
                else -> SeatType.SEAT_BLANK
            }
            TYPE_COUPLE_L -> when (status) {
                STATUS_OPTIONAL -> SeatType.SEAT_COUPLE_OPTIONAL_L
                STATUS_DISABLE -> SeatType.SEAT_COUPLE_DISABLED_L
                STATUS_SELECTED -> SeatType.SEAT_COUPLE_SELECTED_L
                else -> SeatType.SEAT_BLANK
            }
            TYPE_COUPLE_R -> when (status) {
                STATUS_OPTIONAL -> SeatType.SEAT_COUPLE_OPTIONAL_R
                STATUS_DISABLE -> SeatType.SEAT_COUPLE_DISABLED_R
                STATUS_SELECTED -> SeatType.SEAT_COUPLE_SELECTED_R
                else -> SeatType.SEAT_BLANK
            }
            TYPE_REPAIR -> SeatType.SEAT_REPAIR
            else -> SeatType.SEAT_BLANK
        }
    }

    /**
     * 获取分区座位图座位 item 类型
     *
     * @param type 座位类型 NOTSET(0,"未设置"),COMM(1,"普通"),DISABLE(2,"残疾"),LEFTLOVER(3,"情侣左"),RIGHTLOVER(4,"情侣右"),(5,"维修");
     * @param status 座位状态 NOTSET(0,"未设置"),SALE(1,"可售"),NOTSALE(2,"不可售");
     * @param areaLevel 座位区域标识 [Y, O, P, B, G] (E:专享座席)
     *
     * @return 分区座位图座位 item 类型
     */
    private fun getSeatTypeByArea(type: Int, status: Int, areaLevel: Int): SeatType {
        return when (type) {
            TYPE_COMM -> when (status) {
                STATUS_OPTIONAL -> getSeatTypeByAreaOptional(areaLevel)
                STATUS_DISABLE -> SeatType.SEAT_DISABLED
                STATUS_SELECTED -> getSeatTypeByAreaSelected(areaLevel)
                else -> SeatType.SEAT_BLANK
            }
            TYPE_DISABILITY -> when (status) {
                STATUS_OPTIONAL -> SeatType.SEAT_DISABILITY_OPTIONAL
                STATUS_DISABLE -> SeatType.SEAT_DISABLED
                STATUS_SELECTED -> SeatType.SEAT_DISABILITY_SELECTED
                else -> SeatType.SEAT_BLANK
            }
            TYPE_COUPLE_L -> when (status) {
                STATUS_OPTIONAL -> getSeatTypeByAreaCopOptionalL(areaLevel) // SeatType.SEAT_COUPLE_OPTIONAL_L
                STATUS_DISABLE -> SeatType.SEAT_COUPLE_DISABLED_L
                STATUS_SELECTED -> getSeatTypeByAreaCopSelectedL(areaLevel) // SeatType.SEAT_COUPLE_SELECTED_L
                else -> SeatType.SEAT_BLANK
            }
            TYPE_COUPLE_R -> when (status) {
                STATUS_OPTIONAL -> getSeatTypeByAreaCopOptionalR(areaLevel) // SeatType.SEAT_COUPLE_OPTIONAL_R
                STATUS_DISABLE -> SeatType.SEAT_COUPLE_DISABLED_R
                STATUS_SELECTED -> getSeatTypeByAreaCopSelectedR(areaLevel) // SeatType.SEAT_COUPLE_SELECTED_R
                else -> SeatType.SEAT_BLANK
            }
            TYPE_REPAIR -> SeatType.SEAT_REPAIR
            else -> SeatType.SEAT_BLANK
        }
    }


    /**
     * 获取分区座位图座位 item 可选类型
     *
     * @param areaLevel 座位区域标识 [Y, B, G, O, P] (E:专享座席)
     * @return 分区座位图座位 item 可选类型
     */
    private fun getSeatTypeByAreaOptional(areaLevel: Int): SeatType = when (areaLevel) {
        AreaLevel.AREA_LEVEL_1.value -> SeatType.AREA_OPTIONAL_1
        AreaLevel.AREA_LEVEL_2.value -> SeatType.AREA_OPTIONAL_2
        AreaLevel.AREA_LEVEL_3.value -> SeatType.AREA_OPTIONAL_3
        AreaLevel.AREA_LEVEL_4.value -> SeatType.AREA_OPTIONAL_4
        AreaLevel.AREA_LEVEL_5.value -> SeatType.AREA_OPTIONAL_5
        else -> SeatType.AREA_OPTIONAL_1
    }

    /**
     * 获取分区座位图座位 item 已选类型
     *
     * @param areaLevel 座位区域标识 [Y, B, G, O, P] (E:专享座席)
     * @return 分区座位图座位 item 已选类型
     */
    private fun getSeatTypeByAreaSelected(areaLevel: Int): SeatType = when (areaLevel) {
        AreaLevel.AREA_LEVEL_1.value -> SeatType.AREA_SELECTED_1
        AreaLevel.AREA_LEVEL_2.value -> SeatType.AREA_SELECTED_2
        AreaLevel.AREA_LEVEL_3.value -> SeatType.AREA_SELECTED_3
        AreaLevel.AREA_LEVEL_4.value -> SeatType.AREA_SELECTED_4
        AreaLevel.AREA_LEVEL_5.value -> SeatType.AREA_SELECTED_5
        else -> SeatType.AREA_SELECTED_1
    }

    /**
     * 获取分区座位图座位 item 可选类型
     *
     * @param areaLevel 座位区域标识 [Y, B, G, O, P] (E:专享座席)
     * @return 分区座位图座位 item 可选类型
     */
    private fun getSeatTypeByAreaCopOptionalL(areaLevel: Int): SeatType = when (areaLevel) {
        AreaLevel.AREA_LEVEL_1.value -> SeatType.AREA_COP_OPTIONAL_1_L
        AreaLevel.AREA_LEVEL_2.value -> SeatType.AREA_COP_OPTIONAL_2_L
        AreaLevel.AREA_LEVEL_3.value -> SeatType.AREA_COP_OPTIONAL_3_L
        AreaLevel.AREA_LEVEL_4.value -> SeatType.AREA_COP_OPTIONAL_4_L
        AreaLevel.AREA_LEVEL_5.value -> SeatType.AREA_COP_OPTIONAL_5_L
        else -> SeatType.AREA_COP_OPTIONAL_1_L
    }

    /**
     * 获取分区座位图座位 item 可选类型
     *
     * @param areaLevel 座位区域标识 [Y, B, G, O, P] (E:专享座席)
     * @return 分区座位图座位 item 可选类型
     */
    private fun getSeatTypeByAreaCopOptionalR(areaLevel: Int): SeatType = when (areaLevel) {
        AreaLevel.AREA_LEVEL_1.value -> SeatType.AREA_COP_OPTIONAL_1_R
        AreaLevel.AREA_LEVEL_2.value -> SeatType.AREA_COP_OPTIONAL_2_R
        AreaLevel.AREA_LEVEL_3.value -> SeatType.AREA_COP_OPTIONAL_3_R
        AreaLevel.AREA_LEVEL_4.value -> SeatType.AREA_COP_OPTIONAL_4_R
        AreaLevel.AREA_LEVEL_5.value -> SeatType.AREA_COP_OPTIONAL_5_R
        else -> SeatType.AREA_COP_OPTIONAL_1_R
    }

    /**
     * 获取分区座位图座位 item 已选类型
     *
     * @param areaLevel 座位区域标识 [Y, B, G, O, P] (E:专享座席)
     * @return 分区座位图座位 item 已选类型
     */
    private fun getSeatTypeByAreaCopSelectedL(areaLevel: Int): SeatType = when (areaLevel) {
        AreaLevel.AREA_LEVEL_1.value -> SeatType.AREA_COP_SELECTED_1_L
        AreaLevel.AREA_LEVEL_2.value -> SeatType.AREA_COP_SELECTED_2_L
        AreaLevel.AREA_LEVEL_3.value -> SeatType.AREA_COP_SELECTED_3_L
        AreaLevel.AREA_LEVEL_4.value -> SeatType.AREA_COP_SELECTED_4_L
        AreaLevel.AREA_LEVEL_5.value -> SeatType.AREA_COP_SELECTED_5_L
        else -> SeatType.AREA_COP_SELECTED_1_L
    }

    /**
     * 获取分区座位图座位 item 已选类型
     *
     * @param areaLevel 座位区域标识 [Y, B, G, O, P] (E:专享座席)
     * @return 分区座位图座位 item 已选类型
     */
    private fun getSeatTypeByAreaCopSelectedR(areaLevel: Int): SeatType = when (areaLevel) {
        AreaLevel.AREA_LEVEL_1.value -> SeatType.AREA_COP_SELECTED_1_R
        AreaLevel.AREA_LEVEL_2.value -> SeatType.AREA_COP_SELECTED_2_R
        AreaLevel.AREA_LEVEL_3.value -> SeatType.AREA_COP_SELECTED_3_R
        AreaLevel.AREA_LEVEL_4.value -> SeatType.AREA_COP_SELECTED_4_R
        AreaLevel.AREA_LEVEL_5.value -> SeatType.AREA_COP_SELECTED_5_R
        else -> SeatType.AREA_COP_SELECTED_1_R
    }

}