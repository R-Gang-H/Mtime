//package com.kotlin.android.widget.titlebar.item
//
//import android.content.Context
//import android.graphics.Typeface
//import android.util.AttributeSet
//import android.widget.CompoundButton
//import android.widget.FrameLayout
//import android.widget.RadioButton
//import android.widget.RadioGroup
//import androidx.annotation.StringRes
//import com.kotlin.android.ktx.ext.core.getShapeDrawable
//import com.kotlin.android.ktx.ext.core.getString
//import com.kotlin.android.ktx.ext.core.setBackground
//import com.kotlin.android.ktx.ext.dimension.dpF
//import com.kotlin.android.ktx.ext.statelist.getColorStateList
//import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
//import com.kotlin.android.widget.R
//
///**
// * 影片/影院标题选择器
// *
// * Created on 2021/12/14.
// *
// * @author o.s
// */
//
//class SwitchTitleView : FrameLayout, CompoundButton.OnCheckedChangeListener {
//    private var switchView: RadioGroup? = null
//    private var startLabel: RadioButton? = null
//    private var endLabel: RadioButton? = null
//
//    private val cornerRadius = 15.dpF
//    private val itemCornerRadius = 13.dpF
//
//    var switchChange: ((position: Int) -> Unit)? = null
//
//    fun setLabel(
//        @StringRes startLabelRes: Int = R.string.btn_none,
//        @StringRes endLabelRes: Int = R.string.btn_none,
//        startLabel: CharSequence? = null,
//        endLabel: CharSequence? = null,
//    ) {
//        this.startLabel?.text = startLabel ?: getString(startLabelRes)
//        this.endLabel?.text = endLabel ?: getString(endLabelRes)
//    }
//
//    /**
//     * 选择位置
//     */
//    fun selectPosition(position: Int) {
//        startLabel?.isChecked = position != 1
//        endLabel?.isChecked = position == 1
//    }
//
//    constructor(context: Context) : super(context) {
//        initView()
//    }
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        initView()
//    }
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
//        context,
//        attrs,
//        defStyleAttr
//    ) {
//        initView()
//    }
//
//    constructor(
//        context: Context,
//        attrs: AttributeSet?,
//        defStyleAttr: Int,
//        defStyleRes: Int
//    ) : super(context, attrs, defStyleAttr, defStyleRes) {
//        initView()
//    }
//
//    private fun initView() {
//        inflate(context, R.layout.view_switch, this)
//        switchView = findViewById(R.id.switchView)
//        startLabel = findViewById(R.id.startLabel)
//        endLabel = findViewById(R.id.endLabel)
//
//        switchView?.setBackground(
//            colorRes = R.color.color_ffffff,
//            cornerRadius = cornerRadius,
//        )
//
//        startLabel?.apply {
//            setOnCheckedChangeListener(this@SwitchTitleView)
//
//            background = getDrawableStateList(
//                normal = getShapeDrawable(),
//                checked = getShapeDrawable(
//                    colorRes = R.color.color_f5f7f9,
//                    cornerRadius = itemCornerRadius,
//                )
//            )
//            setTextColor(
//                getColorStateList(
//                    normalColorRes = R.color.color_283748,
//                    pressColorRes = R.color.color_004696,
//                    checkedColorRes = R.color.color_004696,
//                    activatedColorRes = R.color.color_004696,
//                    selectedColorRes = R.color.color_004696,
//                )
//            )
//        }
//
//        endLabel?.apply {
//            setOnCheckedChangeListener(this@SwitchTitleView)
//
//            background = getDrawableStateList(
//                normal = getShapeDrawable(),
//                checked = getShapeDrawable(
//                    colorRes = R.color.color_f5f7f9,
//                    cornerRadius = itemCornerRadius,
//                )
//            )
//            setTextColor(
//                getColorStateList(
//                    normalColorRes = R.color.color_283748,
//                    pressColorRes = R.color.color_004696,
//                    checkedColorRes = R.color.color_004696,
//                    activatedColorRes = R.color.color_004696,
//                    selectedColorRes = R.color.color_004696,
//                )
//            )
//        }
//    }
//
//    /**
//     * Called when the checked state of a compound button has changed.
//     *
//     * @param buttonView The compound button view whose state has changed.
//     * @param isChecked  The new checked state of buttonView.
//     */
//    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//        buttonView?.typeface = if (isChecked) {
//            Typeface.defaultFromStyle(Typeface.BOLD)
//        } else {
//            Typeface.defaultFromStyle(Typeface.NORMAL)
//        }
//        if (isChecked) {
//            val position = when (buttonView) {
//                startLabel -> 0
//                else -> 1
//            }
//            switchChange?.invoke(position)
//        }
//    }
//
//}