package com.kotlin.android.film.widget.seat

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.film.R
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.FileEnv
import com.kotlin.android.mtime.ktx.getDrawable

/**
 *
 * Created on 2022/2/10.
 *
 * @author o.s
 */
internal class SeatIcon(
    val width: Int = 20.dp,
    val height: Int = 20.dp ,
    val labelWidth: Int = 20.dp,
    val labelHeight: Int = 20.dp,
) {
    /**
     * 可选座席/区域可选座席
     */
    val optional: Bitmap?
        get() {
            if (needInit(_optional)) {
                _optional = getBitmap(R.drawable.pic_seat_optional_1)
            }
            return _optional
        }

    val optional1: Bitmap?
        get() {
            if (needInit(_optional1)) {
                _optional1 = getBitmap(R.drawable.pic_seat_optional_1)
            }
            return _optional1
        }

    val optional2: Bitmap?
        get() {
            if (needInit(_optional2)) {
                _optional2 = getBitmap(R.drawable.pic_seat_optional_2)
            }
            return _optional2
        }

    val optional3: Bitmap?
        get() {
            if (needInit(_optional3)) {
                _optional3 = getBitmap(R.drawable.pic_seat_optional_3)
            }
            return _optional3
        }

    val optional4: Bitmap?
        get() {
            if (needInit(_optional4)) {
                _optional4 = getBitmap(R.drawable.pic_seat_optional_4)
            }
            return _optional4
        }

    val optional5: Bitmap?
        get() {
            if (needInit(_optional5)) {
                _optional5 = getBitmap(R.drawable.pic_seat_optional_5)
            }
            return _optional5
        }

    /**
     * 已选座席/区域已选座席
     */
    val selected: Bitmap?
        get() {
            if (needInit(_selected)) {
                _selected = getBitmap(R.drawable.pic_seat_selected_1)
            }
            return _selected
        }

    val selected1: Bitmap?
        get() {
            if (needInit(_selected1)) {
                _selected1 = getBitmap(R.drawable.pic_seat_selected_1)
            }
            return _selected1
        }

    val selected2: Bitmap?
        get() {
            if (needInit(_selected2)) {
                _selected2 = getBitmap(R.drawable.pic_seat_selected_2)
            }
            return _selected2
        }

    val selected3: Bitmap?
        get() {
            if (needInit(_selected3)) {
                _selected3 = getBitmap(R.drawable.pic_seat_selected_3)
            }
            return _selected3
        }

    val selected4: Bitmap?
        get() {
            if (needInit(_selected4)) {
                _selected4 = getBitmap(R.drawable.pic_seat_selected_4)
            }
            return _selected4
        }

    val selected5: Bitmap?
        get() {
            if (needInit(_selected5)) {
                _selected5 = getBitmap(R.drawable.pic_seat_selected_5)
            }
            return _selected5
        }

    /**
     * 已售座席
     */
    val disabled: Bitmap?
        get() {
            if (needInit(_disabled)) {
                _disabled = getBitmap(R.drawable.pic_seat_sold)
            }
            return _disabled
        }

    /**
     * 待维修座席
     */
    val repair: Bitmap?
        get() {
            if (needInit(_repair)) {
                _repair = getBitmap(R.drawable.pic_seat_repair)
            }
            return _repair
        }

    /**
     * 情侣座席
     */
    val coupleOptionalLeft: Bitmap?
        get() {
            if (needInit(_coupleOptionalLeft)) {
                _coupleOptionalLeft = getBitmap(R.drawable.pic_copseat_optional_1_l)
            }
            return _coupleOptionalLeft
        }

    val coupleOptionalRight: Bitmap?
        get() {
            if (needInit(_coupleOptionalRight)) {
                _coupleOptionalRight = getBitmap(R.drawable.pic_copseat_optional_1_r)
            }
            return _coupleOptionalRight
        }

    val copOptional1L: Bitmap?
        get() {
            if (needInit(_copOptional1L)) {
                _copOptional1L = getBitmap(R.drawable.pic_copseat_optional_1_l)
            }
            return _copOptional1L
        }

    val copOptional2L: Bitmap?
        get() {
            if (needInit(_copOptional2L)) {
                _copOptional2L = getBitmap(R.drawable.pic_copseat_optional_2_l)
            }
            return _copOptional2L
        }

    val copOptional3L: Bitmap?
        get() {
            if (needInit(_copOptional3L)) {
                _copOptional3L = getBitmap(R.drawable.pic_copseat_optional_3_l)
            }
            return _copOptional3L
        }

    val copOptional4L: Bitmap?
        get() {
            if (needInit(_copOptional4L)) {
                _copOptional4L = getBitmap(R.drawable.pic_copseat_optional_4_l)
            }
            return _copOptional4L
        }

    val copOptional5L: Bitmap?
        get() {
            if (needInit(_copOptional5L)) {
                _copOptional5L = getBitmap(R.drawable.pic_copseat_optional_5_l)
            }
            return _copOptional5L
        }

    val copOptional1R: Bitmap?
        get() {
            if (needInit(_copOptional1R)) {
                _copOptional1R = getBitmap(R.drawable.pic_copseat_optional_1_r)
            }
            return _copOptional1R
        }

    val copOptional2R: Bitmap?
        get() {
            if (needInit(_copOptional2R)) {
                _copOptional2R = getBitmap(R.drawable.pic_copseat_optional_2_r)
            }
            return _copOptional2R
        }

    val copOptional3R: Bitmap?
        get() {
            if (needInit(_copOptional3R)) {
                _copOptional3R = getBitmap(R.drawable.pic_copseat_optional_3_r)
            }
            return _copOptional3R
        }

    val copOptional4R: Bitmap?
        get() {
            if (needInit(_copOptional4R)) {
                _copOptional4R = getBitmap(R.drawable.pic_copseat_optional_4_r)
            }
            return _copOptional4R
        }

    val copOptional5R: Bitmap?
        get() {
            if (needInit(_copOptional5R)) {
                _copOptional5R = getBitmap(R.drawable.pic_copseat_optional_5_r)
            }
            return _copOptional5R
        }

    val coupleSelectedLeft: Bitmap?
        get() {
            if (needInit(_coupleSelectedLeft)) {
                _coupleSelectedLeft = getBitmap(R.drawable.pic_copseat_selected_1_l)
            }
            return _coupleSelectedLeft
        }

    val coupleSelectedRight: Bitmap?
        get() {
            if (needInit(_coupleSelectedRight)) {
                _coupleSelectedRight = getBitmap(R.drawable.pic_copseat_selected_1_r)
            }
            return _coupleSelectedRight
        }

    val copSelected1L: Bitmap?
        get() {
            if (needInit(_copSelected1L)) {
                _copSelected1L = getBitmap(R.drawable.pic_copseat_selected_1_l)
            }
            return _copSelected1L
        }

    val copSelected2L: Bitmap?
        get() {
            if (needInit(_copSelected2L)) {
                _copSelected2L = getBitmap(R.drawable.pic_copseat_selected_2_l)
            }
            return _copSelected2L
        }

    val copSelected3L: Bitmap?
        get() {
            if (needInit(_copSelected3L)) {
                _copSelected3L = getBitmap(R.drawable.pic_copseat_selected_3_l)
            }
            return _copSelected3L
        }

    val copSelected4L: Bitmap?
        get() {
            if (needInit(_copSelected4L)) {
                _copSelected4L = getBitmap(R.drawable.pic_copseat_selected_4_l)
            }
            return _copSelected4L
        }

    val copSelected5L: Bitmap?
        get() {
            if (needInit(_copSelected5L)) {
                _copSelected5L = getBitmap(R.drawable.pic_copseat_selected_5_l)
            }
            return _copSelected5L
        }

    val copSelected1R: Bitmap?
        get() {
            if (needInit(_copSelected1R)) {
                _copSelected1R = getBitmap(R.drawable.pic_copseat_selected_1_r)
            }
            return _copSelected1R
        }

    val copSelected2R: Bitmap?
        get() {
            if (needInit(_copSelected2R)) {
                _copSelected2R = getBitmap(R.drawable.pic_copseat_selected_2_r)
            }
            return _copSelected2R
        }

    val copSelected3R: Bitmap?
        get() {
            if (needInit(_copSelected3R)) {
                _copSelected3R = getBitmap(R.drawable.pic_copseat_selected_3_r)
            }
            return _copSelected3R
        }

    val copSelected4R: Bitmap?
        get() {
            if (needInit(_copSelected4R)) {
                _copSelected4R = getBitmap(R.drawable.pic_copseat_selected_4_r)
            }
            return _copSelected4R
        }

    val copSelected5R: Bitmap?
        get() {
            if (needInit(_copSelected5R)) {
                _copSelected5R = getBitmap(R.drawable.pic_copseat_selected_5_r)
            }
            return _copSelected5R
        }

    val coupleDisabledLeft: Bitmap?
        get() {
            if (needInit(_coupleDisabledLeft)) {
                _coupleDisabledLeft = getBitmap(R.drawable.pic_copseat_sold_l)
            }
            return _coupleDisabledLeft
        }

    val coupleDisabledRight: Bitmap?
        get() {
            if (needInit(_coupleDisabledRight)) {
                _coupleDisabledRight = getBitmap(R.drawable.pic_copseat_sold_r)
            }
            return _coupleDisabledRight
        }

    /**
     * 残疾人座席可选
     */
    val disabilityOptional: Bitmap?
        get() {
            if (needInit(_disabilityOptional)) {
                _disabilityOptional = getBitmap(R.drawable.pic_seat_optional_disability)
            }
            return _disabilityOptional
        }

    /**
     * 残疾人座席已选
     */
    val disabilitySelected: Bitmap?
        get() {
            if (needInit(_disabilitySelected)) {
                _disabilitySelected = getBitmap(R.drawable.pic_seat_selected_disability)
            }
            return _disabilitySelected
        }

    /**
     * 标签：可选座席
     */
    val labelOptional: Bitmap?
        get() {
            if (needInit(_labelOptional)) {
                _labelOptional = getLabelBitmap(R.drawable.pic_seat_optional_1)
            }
            return _labelOptional
        }

    val labelOptional1: Bitmap?
        get() {
            if (needInit(_labelOptional1)) {
                _labelOptional1 = getLabelBitmap(R.drawable.pic_seat_optional_1)
            }
            return _labelOptional1
        }

    val labelOptional2: Bitmap?
        get() {
            if (needInit(_labelOptional2)) {
                _labelOptional2 = getLabelBitmap(R.drawable.pic_seat_optional_2)
            }
            return _labelOptional2
        }

    val labelOptional3: Bitmap?
        get() {
            if (needInit(_labelOptional3)) {
                _labelOptional3 = getLabelBitmap(R.drawable.pic_seat_optional_3)
            }
            return _labelOptional3
        }

    val labelOptional4: Bitmap?
        get() {
            if (needInit(_labelOptional4)) {
                _labelOptional4 = getLabelBitmap(R.drawable.pic_seat_optional_4)
            }
            return _labelOptional4
        }

    val labelOptional5: Bitmap?
        get() {
            if (needInit(_labelOptional5)) {
                _labelOptional5 = getLabelBitmap(R.drawable.pic_seat_optional_5)
            }
            return _labelOptional5
        }

    val labelSelected: Bitmap?
        get() {
            if (needInit(_labelSelected)) {
                _labelSelected = getLabelBitmap(R.drawable.pic_seat_selected_1)
            }
            return _labelSelected
        }

    val labelDisabled: Bitmap?
        get() {
            if (needInit(_labelDisabled)) {
                _labelDisabled = getLabelBitmap(R.drawable.pic_seat_sold)
            }
            return _labelDisabled
        }

    val labelRepair: Bitmap?
        get() {
            if (needInit(_labelRepair)) {
                _labelRepair = getLabelBitmap(R.drawable.pic_seat_repair)
            }
            return _labelRepair
        }

    val labelDisabilityOptional: Bitmap?
        get() {
            if (needInit(_labelDisabilityOptional)) {
                _labelDisabilityOptional = getLabelBitmap(R.drawable.pic_seat_optional_disability)
            }
            return _labelDisabilityOptional
        }

    val labelCoupleOptionalLeft: Bitmap?
        get() {
            if (needInit(_labelCoupleOptionalLeft)) {
                _labelCoupleOptionalLeft = getLabelBitmap(R.drawable.pic_copseat_optional_1_l)
            }
            return _labelCoupleOptionalLeft
        }

    val labelCoupleOptionalRight: Bitmap?
        get() {
            if (needInit(_labelCoupleOptionalRight)) {
                _labelCoupleOptionalRight = getLabelBitmap(R.drawable.pic_copseat_optional_1_r)
            }
            return _labelCoupleOptionalRight
        }

    private fun needInit(bitmap: Bitmap?): Boolean {
        return (bitmap?.isRecycled != false)
    }

    private var _optional: Bitmap? = null
    private var _optional1: Bitmap? = null
    private var _optional2: Bitmap? = null
    private var _optional3: Bitmap? = null
    private var _optional4: Bitmap? = null
    private var _optional5: Bitmap? = null
    private var _selected: Bitmap? = null
    private var _selected1: Bitmap? = null
    private var _selected2: Bitmap? = null
    private var _selected3: Bitmap? = null
    private var _selected4: Bitmap? = null
    private var _selected5: Bitmap? = null
    private var _disabled: Bitmap? = null // 已售座席
    private var _repair: Bitmap? = null  // 待维修座席
    private var _coupleOptionalLeft: Bitmap? = null
    private var _coupleOptionalRight: Bitmap? = null
    private var _copOptional1L: Bitmap? = null
    private var _copOptional2L: Bitmap? = null
    private var _copOptional3L: Bitmap? = null
    private var _copOptional4L: Bitmap? = null
    private var _copOptional5L: Bitmap? = null
    private var _copOptional1R: Bitmap? = null
    private var _copOptional2R: Bitmap? = null
    private var _copOptional3R: Bitmap? = null
    private var _copOptional4R: Bitmap? = null
    private var _copOptional5R: Bitmap? = null
    private var _coupleSelectedLeft: Bitmap? = null
    private var _coupleSelectedRight: Bitmap? = null
    private var _copSelected1L: Bitmap? = null
    private var _copSelected2L: Bitmap? = null
    private var _copSelected3L: Bitmap? = null
    private var _copSelected4L: Bitmap? = null
    private var _copSelected5L: Bitmap? = null
    private var _copSelected1R: Bitmap? = null
    private var _copSelected2R: Bitmap? = null
    private var _copSelected3R: Bitmap? = null
    private var _copSelected4R: Bitmap? = null
    private var _copSelected5R: Bitmap? = null
    private var _coupleDisabledLeft: Bitmap? = null
    private var _coupleDisabledRight: Bitmap? = null
    private var _disabilityOptional: Bitmap? = null // 残疾人座席可选
    private var _disabilitySelected: Bitmap? = null // 残疾人座席已选

    private var _labelOptional: Bitmap? = null // 标签：可选座席
    private var _labelOptional1: Bitmap? = null
    private var _labelOptional2: Bitmap? = null
    private var _labelOptional3: Bitmap? = null
    private var _labelOptional4: Bitmap? = null
    private var _labelOptional5: Bitmap? = null
    private var _labelSelected: Bitmap? = null // 标签：已选座席
    private var _labelDisabled: Bitmap? = null // 标签：已售座席
    private var _labelRepair: Bitmap? = null  // 标签：待维修座席
    private var _labelDisabilityOptional: Bitmap? = null // 标签：残疾人座席可选
    private var _labelCoupleOptionalLeft: Bitmap? = null // 标签：残疾人座席可选
    private var _labelCoupleOptionalRight: Bitmap? = null // 标签：残疾人座席可选

    private fun getBitmap(@DrawableRes resId: Int): Bitmap? {
        return getDrawable(resId)?.toBitmap(width, height)
    }

    private fun getLabelBitmap(@DrawableRes resId: Int): Bitmap? {
        return getDrawable(resId)?.toBitmap(labelWidth, labelHeight)
    }

    fun recycle() {
        _optional?.recycle()
        _optional1?.recycle()
        _optional2?.recycle()
        _optional3?.recycle()
        _optional4?.recycle()
        _optional5?.recycle()
        _selected?.recycle()
        _selected1?.recycle()
        _selected2?.recycle()
        _selected3?.recycle()
        _selected4?.recycle()
        _selected5?.recycle()
        _disabled?.recycle()
        _repair?.recycle()
        _coupleOptionalLeft?.recycle()
        _coupleOptionalRight?.recycle()
        _copOptional1L?.recycle()
        _copOptional2L?.recycle()
        _copOptional3L?.recycle()
        _copOptional4L?.recycle()
        _copOptional5L?.recycle()
        _copOptional1R?.recycle()
        _copOptional2R?.recycle()
        _copOptional3R?.recycle()
        _copOptional4R?.recycle()
        _copOptional5R?.recycle()
        _coupleSelectedLeft?.recycle()
        _coupleSelectedRight?.recycle()
        _copSelected1L?.recycle()
        _copSelected2L?.recycle()
        _copSelected3L?.recycle()
        _copSelected4L?.recycle()
        _copSelected5L?.recycle()
        _copSelected1R?.recycle()
        _copSelected2R?.recycle()
        _copSelected3R?.recycle()
        _copSelected4R?.recycle()
        _copSelected5R?.recycle()
        _coupleDisabledLeft?.recycle()
        _coupleDisabledRight?.recycle()
        _disabilityOptional?.recycle()
        _disabilitySelected?.recycle()

        _labelOptional?.recycle()
        _labelOptional1?.recycle()
        _labelOptional2?.recycle()
        _labelOptional3?.recycle()
        _labelOptional4?.recycle()
        _labelOptional5?.recycle()
        _labelSelected?.recycle()
        _labelDisabled?.recycle()
        _labelRepair?.recycle()
        _labelDisabilityOptional?.recycle()
        _labelCoupleOptionalLeft?.recycle()
        _labelCoupleOptionalRight?.recycle()
    }

    /**
     * 动态获取可选座位图(根据行列号)
     */
    fun getOptionalBitmap(row: Int, col: Int): Bitmap? {
        return null
    }

    /**
     * 动态获取已选座位图(根据行列号)
     */
    fun getSelectedBitmap(row: Int, col: Int): Bitmap? {
        return null
    }

    /**
     * 动态获取不可选/已售座位图(根据行列号)
     */
    fun getDisabledBitmap(row: Int, col: Int): Bitmap? {
        return null
    }
}

val seatIconsFilePath by lazy { FileEnv.seatIconsDownloadDir }

data class OptionalSeat(
    var iconPath: String,
    var iconUrl: String? = null,
    var mediaPath: String? = null,
    var mediaUrl: String? = null,
    var bitmap: Bitmap? = null
) {
    companion object {
        fun obtain(item: SeatItem, bitmap: (String) -> Bitmap?): OptionalSeat {
            val iconPath = if (item.iconFileName?.isNotEmpty() == true) {
                seatIconsFilePath.plus(item.iconFileName)
            } else {
                ""
            }
            val mediaPath = if (item.mediaFileName?.isNotEmpty() == true) {
                seatIconsFilePath.plus(item.mediaFileName)
            } else {
                ""
            }
            return OptionalSeat(
                iconPath,
                item.unsedIconUrl,
                mediaPath,
                item.attachmentUrl,
                bitmap.invoke(iconPath)
            )
        }
    }
}

data class SelectedSeat(
    var iconPath: String,
    var iconUrl: String? = null,
    var tipsPath: String? = null,
    var tipsUrl: String? = null,
    var mediaPath: String? = null,
    var mediaUrl: String? = null,
    var subtitles: String? = null,
    var bitmap: Bitmap? = null,
    var tipsBitmap: Bitmap? = null
) {
    companion object {
        fun obtain(
            selected: SelectedImage,
            bitmap: (String) -> Bitmap?,
            tipsBitmap: (String) -> Bitmap?
        ): SelectedSeat {
            val iconPath = if (selected.iconFileName?.isNotEmpty() == true) {
                seatIconsFilePath.plus(selected.iconFileName)
            } else {
                ""
            }
            val tipsPath = if (selected.tipsFileName?.isNotEmpty() == true) {
                seatIconsFilePath.plus(selected.tipsFileName)
            } else {
                ""
            }
            val mediaPath = if (selected.mediaFileName?.isNotEmpty() == true) {
                seatIconsFilePath.plus(selected.mediaFileName)
            } else {
                ""
            }
            return SelectedSeat(
                iconPath,
                selected.iconUrl,
                tipsPath,
                selected.ampleIconUrl,
                mediaPath,
                selected.attachmentFileUrl,
                selected.text,
                bitmap.invoke(iconPath),
                tipsBitmap.invoke(tipsPath)
            )
        }
    }
}

data class SoldSeat(
    val iconPath: String,
    val iconUrl: String? = null,
    var bitmap: Bitmap? = null
) {

    companion object {
        fun obtain(sold: SoldImage, bitmap: (String) -> Bitmap?): SoldSeat {
            val iconPath = if (sold.iconFileName?.isNotEmpty() == true) {
                seatIconsFilePath.plus(sold.iconFileName)
            } else {
                ""
            }
            return SoldSeat(
                iconPath,
                sold.soldImageUrl,
                bitmap.invoke(iconPath)
            )
        }
    }
}

/**
 * 座位图信息
 *
 * Created on 2022/1/8.
 *
 * @author o.s
 */
data class SeatItem(
    var attachmentName: String? = null,
    var attachmentUrl: String? = null,
    var createTime: Double = 0.0,
    var endValidTime: Double = 0.0,
    var iconType: Int = 0,
    var id: Int = 0,
    var name: String? = null,
    var startValidTime: Double = 0.0,
    var subjectCode: String? = null,
    var typeValue: Int = 0,
    var unsedIconName: String? = null,
    var unsedIconUrl: String? = null,
    var selectedImageLists: List<SelectedImage>? = null,
    var soldImageLists: List<SoldImage>? = null,

    var iconFileName: String? = null,
    var mediaFileName: String? = null,
    var iconSuccess: Boolean = false,
    var mediaSuccess: Boolean = false,
    // labelName
    var optionalName: String = "可选",
    var selectedName: String = "已选",
    var soldName: String = "已售"
) : ProguardRule

/**
 * 可选座位图信息
 *
 * Created on 2022/1/8.
 *
 * @author o.s
 */
data class SelectedImage(
    var attachmentFileName: String? = null,
    var attachmentFileUrl: String? = null,
    var ampleIconUrl: String? = null,
    var iconName: String? = null,
    var iconText: String? = null,
    var iconUrl: String? = null,
    var sortSeq: Int = 1,

    var iconFileName: String? = null,
    var tipsFileName: String? = null,
    var mediaFileName: String? = null,
    var text: String = "",
    var time: Long = 0,
    var iconSuccess: Boolean = false,
    var tipsSuccess: Boolean = false,
    var mediaSuccess: Boolean = false
) : ProguardRule

/**
 * 已售座位图信息
 *
 * Created on 2022/1/8.
 *
 * @author o.s
 */
data class SoldImage(
    var soldImageName: String? = null,
    var soldImageUrl: String? = null,
    var sortSeq: Int = 1,

    var iconFileName: String? = null,
    var iconSuccess: Boolean = false
) : ProguardRule