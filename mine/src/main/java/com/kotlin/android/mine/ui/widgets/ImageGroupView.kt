package com.kotlin.android.mine.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.kotlin.android.image.bindadapter.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mine.R

/**
 * @desc 展示图片,最多4张
 * @author zhangjian
 * @date 2020/10/15 16:16
 */
class ImageGroupView : LinearLayout {

    var imgArr: ArrayList<String> = arrayListOf()

    //图片的数量
    private val COUNT_1 = 1
    private val COUNT_2 = 2
    private val COUNT_3 = 3
    private val COUNT_4 = 4
    private val IMG_WIDTH = 70.dp
    private val IMG_HEIGHT = 70.dp

    private var ivFirst: ImageView? = null
    private var ivSecond: ImageView? = null
    private var ivThird: ImageView? = null
    private var ivFourth: ImageView? = null
    private var rl: RelativeLayout? = null
    private var tvCount: TextView? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, @Nullable attr: AttributeSet?, def: Int) : super(context, attr, def) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_img_group_show, null)
        ivFirst = view.findViewById(R.id.ivFirst)
        ivSecond = view.findViewById(R.id.ivSecond)
        ivThird = view.findViewById(R.id.ivThird)
        ivFourth = view.findViewById(R.id.ivFourth)
        tvCount = view.findViewById(R.id.tvCount)
        rl = view.findViewById(R.id.rlFourth)
        val param = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        param.marginStart = 10.dp
        param.marginEnd = 10.dp
        view.layoutParams = param
        addView(view)
    }

    /**
     * 通过数组数量展示图片
     */
    private fun showImageView() {
        if (imgArr.isEmpty()) {
            return
        }
        when (imgArr.size) {
            //一张图
            COUNT_1 -> {
                showOneImage()
            }
            COUNT_2 -> {
                showTwoImage()
            }
            COUNT_3 -> {
                showThreeImage()
            }
            COUNT_4 -> {
                showFourImage()
            }
            else -> {
                showMostImage()
            }
        }
    }

    /**
     * 展示第一张图片
     */
    private fun showFirstImageView() {
        ivFirst?.visibility = View.VISIBLE
        loadImage(ivFirst!!, imgArr[0], IMG_WIDTH, IMG_HEIGHT, circleCrop = false)
    }

    /**
     * 展示第二张图片
     */
    private fun showSecondImageView() {
        ivSecond?.visibility = View.VISIBLE
        loadImage(ivSecond!!, imgArr[1], IMG_WIDTH, IMG_HEIGHT, circleCrop = false)
    }

    /**
     * 展示第三张图片
     */
    private fun showThirdImageView() {
        ivThird?.visibility = View.VISIBLE
        loadImage(ivThird!!, imgArr[2], IMG_WIDTH, IMG_HEIGHT, circleCrop = false)
    }

    /**
     * 展示第四张图片
     */
    private fun showFourthImageView() {
        rl?.visibility = View.VISIBLE
        ivFourth?.visibility = View.VISIBLE
        loadImage(ivFourth!!, imgArr[3], IMG_WIDTH, IMG_HEIGHT, circleCrop = false)
    }

    /**
     * 展示多于第四张的图片
     */
    private fun showMostImageView() {
        tvCount?.visibility = View.VISIBLE
        tvCount?.text = "+${imgArr.size - 1}"
    }


    private fun showOneImage() {
        showFirstImageView()
    }


    private fun showTwoImage() {
        showFirstImageView()
        showSecondImageView()
    }


    private fun showThreeImage() {
        showFirstImageView()
        showSecondImageView()
        showThirdImageView()
    }


    private fun showFourImage() {
        showFirstImageView()
        showSecondImageView()
        showThirdImageView()
        showFourthImageView()
    }

    private fun showMostImage() {
        showFirstImageView()
        showSecondImageView()
        showThirdImageView()
        showFourthImageView()
        showMostImageView()
    }


    fun setImageArrData(list: ArrayList<String>) {
        imgArr.clear()
        imgArr.addAll(list)
        showImageView()
    }
}