package com.kotlin.android.home.ui.findmovie.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kotlin.android.home.R
import com.kotlin.android.home.ui.findmovie.bean.SearchBean
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.mtime.base.dialog.BaseDialogFragment
import kotlinx.android.synthetic.main.item_filter_condition_contain_layout.*


/**
 * 找电影条件筛选
 * @author: WangWei
 * @date: 2022/4/14
 */
class FilterConditionDialogFragment :
    BaseDialogFragment() {
    var list: List<MultiTypeBinder<*>>? = arrayListOf()
    var adapter: MultiTypeAdapter? = null
    var action: ((data: SearchBean) -> Unit)? = null//选中结果回调
    var searchBean: SearchBean = SearchBean()

    fun setData(
        searchBean: SearchBean,
        list: List<MultiTypeBinder<*>>?,
        action: ((data: SearchBean) -> Unit)?
    ) {
        this.searchBean = searchBean
        this.list = list
        this.action = action
        adapter?.notifyAdapterDataSetChanged(list)
    }


    companion object {
        fun newInstance() = FilterConditionDialogFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ViewsBottomDialog)
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)

    }

    override fun onStart() {
        super.onStart()
        val win: Window = dialog!!.window ?: return
//        win.setBackgroundDrawable(ColorDrawable(getColor(R.color.color_99000000)))

        val params: WindowManager.LayoutParams = win.attributes
        params.gravity = Gravity.TOP
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params

    }

    override fun getLayoutRes(): Int {
       return R.layout.item_filter_condition_contain_layout
    }

    override fun bindView(v: View?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = createMultiTypeAdapter(
            rv,
            GridLayoutManager(this.requireContext(), 4)
        ).apply {
            setOnClickListener(::onBinderClick)
            notifyAdapterDataSetChanged(list)
        }
    }

     fun initView() {

    }

    /**
     * 需要回调再实现
     * 关注、订阅之类
     */
    private  fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        if (binder is ItemFilmFilterConditionBinder) {
            if (binder != null) {
                when (searchBean.type) {
                    0 -> {//类型
                        searchBean.genreTypes = binder.bean.value.orZero()
                    }
                    1 -> {//地区
                        searchBean.area = binder.bean.value.orZero()
                    }
                    2 -> {//排序
                        searchBean.sortType = binder.bean.value.orZero()
                    }
                    3 -> {//年代
                        searchBean.year =
                            binder.bean.from.orZero().toString() + ":" + binder.bean.to.orZero()
                    }
                    else -> {
                    }
                }
            }
            //把选中的数据回调
            action?.invoke(searchBean)

            dismiss()
        }

    }


}