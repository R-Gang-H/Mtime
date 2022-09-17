package com.kotlin.android.mine.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthMovieTypeBean
import com.kotlin.android.mine.binder.AuthMovieTypeBinder
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import kotlinx.android.synthetic.main.view_authen_movier_production.view.*

/**
 * create by lushan on 2020/9/9
 * description:身份认证-电影人认证 作品控件 只能选中一个
 */
class AuthenProductionView @JvmOverloads constructor(var ctx: Context, var attrs: AttributeSet? = null, var defaultStyle: Int = -1) : LinearLayoutCompat(ctx, attrs, defaultStyle) {

    init {
        orientation = VERTICAL
        initView()
    }

    private var tagBinderList: MutableList<AuthMovieTypeBinder> = mutableListOf()

    private var selectTagId: Long = 0L//选中的标签
    private var adapter: MultiTypeAdapter? = null
     var refreshListener: ((String) -> Unit)? = null

    private fun initView() {
        val view = View.inflate(ctx, R.layout.view_authen_movier_production, null)
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            leftMargin = 15.dp
            topMargin = 20.dp
        }.also {
            addView(view, it)
        }

        adapter = createMultiTypeAdapter(productionRuleRv, FlexboxLayoutManager(ctx))
        adapter?.setOnClickListener { view, binder ->
//            只能选中一个
            if (view.id == R.id.tagTv) {
                if (binder is AuthMovieTypeBinder) {
                    handleSelectTag(binder)
                }
            }
        }
        myProductionET?.doOnTextChanged { text, start, before, count ->
            setInputLineStyle(false,productionDownLine)
            refreshListener?.invoke(text.toString())
        }

    }

    /**
     * 获取作品信息
     */
    fun getProductionName(): String = myProductionET?.text?.toString().orEmpty()

    /**
     * 清空所有选中状态
     */
    private fun handleSelectTag(binder: AuthMovieTypeBinder) {
//        其余选项数据情况
        tagBinderList.filter { it.bean.canClick && it.bean.id != binder.bean.id }.forEach {
            it.bean.isSelected = false
        }
//        当前选项，选中或取消
        tagBinderList.filter { it.bean.id == binder.bean.id }.forEach {
            it.bean.isSelected = it.bean.isSelected.not()
            selectTagId = if (it.bean.isSelected) {
                it.bean.id
            } else {
                0L
            }
        }
        refreshListener?.invoke(binder.bean.tag)
        adapter?.notifyAdapterAdded(tagBinderList)
    }


    /**
     * 获取选中的角色
     */
    fun getSelectRoleName():String{
        val list = tagBinderList.filter { it.bean.id == selectTagId }.toMutableList()
        return if (list.isEmpty().not()){
            list[0].bean.tag
        }else{
            ""
        }

    }

    fun setData(list: MutableList<AuthMovieTypeBean>) {
        this.tagBinderList.clear()
        this.tagBinderList.addAll(list.map { AuthMovieTypeBinder(it) })
        adapter?.notifyAdapterDataSetChanged(this.tagBinderList)
    }

    /**
     * 获取选中的角色类型
     */
    fun getSelectTagId(): Long = selectTagId

    //    作品没有输入，提交时需要下划线红色展示
    fun productionError() {
        setInputLineStyle(true,productionDownLine)
    }

    /**
     * 设置输入框下划线颜色
     */
    private fun setInputLineStyle(isError: Boolean, line: View?) {
        line?.setBackgroundColor(getColor(if (isError) R.color.color_ff5a36 else R.color.color_f3f3f4))
    }
}