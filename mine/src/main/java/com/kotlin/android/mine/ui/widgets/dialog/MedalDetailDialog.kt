package com.kotlin.android.mine.ui.widgets.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.kotlin.android.app.data.entity.community.medal.MedalDetail
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import org.jetbrains.anko.find
import java.text.SimpleDateFormat

class MedalDetailDialog(private val medalDetail: MedalDetail, private val isAward: Boolean) :
    DialogFragment() {

    companion object {
        const val TAG_MEDAL_DETAIL_DIALOG_FRAGMENT = "tag_medal_detail_dialog_fragment"
    }

    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ViewsBottomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_medal_detail_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        val win: Window = dialog!!.window ?: return
        win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val params: WindowManager.LayoutParams = win.attributes
        params.gravity = Gravity.BOTTOM
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            find<RelativeLayout>(R.id.dialogView).setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            find<ImageView>(R.id.closeIv).setOnClickListener { dismiss() }
            Glide.with(context).load(medalDetail.appLogoUrl).into(find(R.id.medalIv))
            if (!isAward) {
                val filterMatrix = ColorMatrix()
                filterMatrix.setSaturation(0f)
                find<ImageView>(R.id.medalIv).colorFilter = ColorMatrixColorFilter(filterMatrix)
            }
            find<TextView>(R.id.medalName).text = medalDetail.medalName
            find<TextView>(R.id.explainTv).text = medalDetail.factor
            find<TextView>(R.id.awardedTimeTv).apply {
                if (medalDetail.receiveTime != null) text =
                    context.getString(
                        R.string.mine_medal_detail_receive_time,
                        simpleDateFormat.format(simpleDateFormat.parse(medalDetail.receiveTime?.show))
                    )
                visibility =
                    if (isAward) if (medalDetail.receiveTime != null) View.VISIBLE else View.GONE else View.GONE
            }
            find<TextView>(R.id.limitAmountTv).apply {
                text = Html.fromHtml(
                    context.getString(
                        R.string.mine_medal_detail_limit_count, "${medalDetail.receiveCount}"
                    )
                )
                visibility = if (medalDetail.receiveCount != 0L) View.VISIBLE else View.GONE
            }
        }
    }

}