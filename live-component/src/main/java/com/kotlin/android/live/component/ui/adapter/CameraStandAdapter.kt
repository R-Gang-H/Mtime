package com.kotlin.android.live.component.ui.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.viewbean.CameraStandViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDimension
import com.kotlin.android.mtime.ktx.getDrawable
import kotlinx.android.synthetic.main.item_camera_stand_new.view.*

/**
 * create by lushan on 2021/5/17
 * description:
 */
class CameraStandAdapter(
    private var context: Context,
    private var list: MutableList<CameraStandViewBean> = mutableListOf(),
    private var isPortrait: Boolean = true,
    private var clickListener: ((CameraStandViewBean) -> Unit)? = null
) : RecyclerView.Adapter<CameraStandAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(bean: CameraStandViewBean) {
            with(bean) {
                ShapeExt.setGradientColorWithColor(
                    itemView.cameraBgView,
                    startColor = getColor(R.color.color_00000000),
                    endColor = getColor(R.color.color_80000000),
                    corner = 0
                )
//                设置item左边距和大小
                val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.width =
                    itemView.resources.getDimensionPixelOffset(if (isPortrait) R.dimen.offset_132px else R.dimen.offset_206px)
                layoutParams.height =
                    itemView.resources.getDimensionPixelOffset(if (isPortrait) R.dimen.offset_78px else R.dimen.offset_122px)
                layoutParams.leftMargin =
                    itemView.resources.getDimensionPixelOffset(if (isPortrait) R.dimen.offset_20px else R.dimen.offset_30px)
                itemView.layoutParams = layoutParams

                itemView.cameraCv?.radius =
                    if (isPortrait) getDimension(R.dimen.offset_8px) else getDimension(R.dimen.offset_12px)

                val w = if (isPortrait) 66.dp else 103.dp
                val h = if (isPortrait) 39.dp else 61.dp
                itemView.cameraIv?.loadImage(
                    data = img,
                    width = w,
                    height = h
                )

                itemView.titleTv?.apply {
                    text = title
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isPortrait) 12f else 17f)

                    if (bean.isSelected) {
                        val drawable = getDrawable(R.drawable.ic_live_play)?.apply {
                            setBounds(
                                0,
                                0,
                                if (isPortrait) 8.dp else 10.dp,
                                if (isPortrait) 8.dp else 11.dp
                            )
                        }
                        this.setCompoundDrawables(drawable, null, null, null)
                    } else {
                        this.setCompoundDrawables(null, null, null, null)
                    }
                }

                if (isSelected) {
                    ShapeExt.setShapeCorner2Color2Stroke(
                        itemView.maskView,
                        R.color.color_3300000000,
                        if (isPortrait) 4 else 6,
                        R.color.color_feb12a,
                        1
                    )
                } else {
                    itemView.maskView?.background = null
                }

                itemView.onClick {
                    clickListener?.invoke(bean)
                }

            }
        }
    }

    fun setPortrait(isPortrait: Boolean) {
        this.isPortrait = isPortrait
        notifyDataSetChanged()
    }

    fun setData(list: MutableList<CameraStandViewBean>,isPortrait: Boolean) {
        this.isPortrait = isPortrait
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.item_camera_stand_new, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size
}