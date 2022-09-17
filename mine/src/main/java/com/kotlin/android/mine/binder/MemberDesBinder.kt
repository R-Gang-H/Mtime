package com.kotlin.android.mine.binder

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.ext.openCommunity
import com.kotlin.android.app.router.ext.openFilm
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.MemberDesViewBean
import com.kotlin.android.mine.databinding.ItemMemberDesBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.user.IAppUserProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/10/24
 * description: 会员中心下方获取M豆列表
 */
class MemberDesBinder(var bean: MemberDesViewBean) : MultiTypeBinder<ItemMemberDesBinding>() {
    private val userProvider = getProvider(IAppUserProvider::class.java)
    override fun layoutId(): Int {
        return R.layout.item_member_des
    }

    override fun onBindViewHolder(binding: ItemMemberDesBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        描述方法背景
//        ShapeExt.setShapeColorAndCorner(binding.beanDesCL, R.color.color_ffffff, 5)
//        按钮背景
        ShapeExt.setShapeColorAndCorner(binding.gotoBtn, R.color.color_20a0da, 13.5f.dp)

//        显示
        showDesView()

    }

    private fun showDesView() {
        binding?.desLL?.removeAllViews()
        bean.desList.forEachIndexed { index, s ->
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 5.dp, 0)
            }.also {
                binding?.root?.context?.apply {
                    val desItemTv = getDesItemTv(this, s)
                    desItemTv?.layoutParams = it
                    binding?.desLL?.addView(desItemTv)
                }
            }
        }

    }

    private fun getDesItemTv(context: Context, content: String): AppCompatTextView {
        return AppCompatTextView(context).apply {
            text = content
            setTextColor(getColor(R.color.color_8798af))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTypeface(null, Typeface.BOLD)
            setPadding(5.dp, 0, 5.dp, 0)
            ShapeExt.setShapeCorner2Color2Stroke(this, strokeColor = R.color.color_8798af, strokeWidth = 0.5f.dp)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.gotoBtn -> {//跳转按钮
                when (bean.type) {
                    MemberDesViewBean.TYPE_BUY -> {//去购票
                        binding?.root?.context?.apply {
                            openFilm()
                        }
                    }
                    MemberDesViewBean.TYPE_COMMUNITY -> {//去社区
                        binding?.root?.context?.apply {
                            openCommunity()
                        }
                    }
                    MemberDesViewBean.TYPE_GAME -> {//去游戏
                        val instance = getProvider(ICardMonopolyProvider::class.java)
                        instance?.startCardMainActivity(binding?.root?.context as Activity)
                    }
                    else -> super.onClick(view)
                }

            }
            R.id.moreTv -> {//更多方法
                (binding?.root?.context as? Activity)?.apply {
                    getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                            BrowserEntity(title = "", url = bean.mBeanUrl))
                }
            }
            else -> {
                super.onClick(view)
            }
        }

    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MemberDesBinder && other.bean != bean
    }
}