package com.kotlin.android.mine.binder

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.GoodsViewBean
import com.kotlin.android.mine.bean.MemberInfoViewBean
import com.kotlin.android.mine.databinding.ItemMemberInfoBinding
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.app.router.provider.user.IAppUserProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * create by lushan on 2020/10/24
 * description:会员中心上方信息
 */
class MemberInfoBinder(var bean: MemberInfoViewBean,var list: MutableList<GoodsViewBean>) : MultiTypeBinder<ItemMemberInfoBinding>() {
    private val userProvider = getProvider(IAppUserProvider::class.java)
    override fun layoutId(): Int {
        return R.layout.item_member_info
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MemberInfoBinder && other.bean == bean && other.list == list
    }

    override fun onBindViewHolder(binding: ItemMemberInfoBinding, position: Int) {
        super.onBindViewHolder(binding, position)

//      设置M豆总数量
        binding.mBeanTv?.text = getMBeanSpannable()
        var linearLayoutManager = LinearLayoutManager(binding.root.context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        createMultiTypeAdapter(binding.goodsRv,linearLayoutManager).apply {
            notifyAdapterDataSetChanged(list.map { MemberGoodsBinder(it) }, false)
            setOnClickListener { view, binder ->
                super.onClick(view)
            }
        }
    }

    private fun getMBeanSpannable(): SpannableString {
        val content = formatCount(bean.totalMBeanNum)
        val format = getString(R.string.mine_member_m_bean_format).format(content)
        return SpannableString(format).apply {
//            设置数量样式
            setSpan(StyleSpan(Typeface.BOLD_ITALIC), 0, content.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
//            设置数量字体大小
            setSpan(AbsoluteSizeSpan(27, true), 0, content.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)

        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.howToUpdateTv -> {//如何升级
                gotoH5(bean.vipDesUrl)
            }
            R.id.levelPackageTv -> {//等级礼包
                gotoH5(bean.vipDesUrl)
            }
            R.id.birthdayPrivilegeTv -> {//生日特权
                gotoH5(bean.vipDesUrl)
            }
            R.id.movieCoinPrivilegeTv -> {//游戏金币
                gotoH5(bean.vipDesUrl)
            }

            R.id.movieGoodsPrivilegeTv -> {//电影周边
                gotoH5(bean.vipDesUrl)
            }

            R.id.customerServicePrivilegeTv -> {//客服特权
                gotoH5(bean.vipDesUrl)
            }
            R.id.vipLeveIv,R.id.vipTv->{//会员等级名称及图标
                gotoH5(bean.vipDesUrl)
            }
            R.id.whatsMBeanTv -> {//什么是m豆
                gotoH5(bean.mBeanUrl)
            }

            else -> super.onClick(view)
        }

    }

    private fun gotoH5(url: String){
        getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                BrowserEntity(title = "", url = url))
    }

    fun isShowExchangeView():Boolean{
        return list.isEmpty().not()
    }

}