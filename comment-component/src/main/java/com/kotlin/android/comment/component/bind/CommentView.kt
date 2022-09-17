package com.kotlin.android.comment.component.bind

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.bind.adapter.MultiTypeAdapter
import com.kotlin.android.bind.event.Action
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bind.binder.CommentAllBinder
import com.kotlin.android.comment.component.bind.binder.CommentEmptyBinder
import com.kotlin.android.comment.component.bind.binder.CommentItemBinder
import com.kotlin.android.comment.component.bind.binder.CommentTitleBinder
import com.kotlin.android.app.data.entity.comment.CommentAll
import com.kotlin.android.app.data.entity.comment.CommentEmpty
import com.kotlin.android.app.data.entity.comment.CommentTitle

/**
 * 评论视图
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
class CommentView : RecyclerView {

    private var mCommentTitle = CommentTitle()
    private var mCommentAll: CommentAll? = null
    private var mCommentEmpty = CommentEmpty()
    private val items by lazy { ArrayList<Any>() }
    var isCanScroll = true
    var isNew = false

    val titleBinder by lazy { CommentTitleBinder() }
    val itemBinder by lazy { CommentItemBinder() }

    var actionTitle: ((action: Action<CommentTitleBinder.Holder>) -> Unit)? = null
        set(value) {
            field = value
            titleBinder.withClick(value)
        }

    var actionItem: ((action: Action<CommentItemBinder.Holder>) -> Unit)? = null
        set(value) {
            field = value
            itemBinder.withClick(value)
        }

    private val mAdapter by lazy {
        MultiTypeAdapter().apply {
            // 注册多类型
            register(titleBinder)
            register(itemBinder)
            register(CommentEmptyBinder())
            register(CommentAllBinder())
        }
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        layoutManager = LinearLayoutManager(context)

        adapter = mAdapter
    }

    fun setData(data: List<CommentViewBean>) {
        items.clear()
        items.add(mCommentTitle)
        if (data.isNullOrEmpty()) {
            items.add(mCommentEmpty)
        } else {
            items.addAll(data)
        }
        mCommentAll?.apply {
            items.add(this)
        }
        mAdapter.items = items
        mAdapter.notifyDataSetChanged()
    }

    fun setTitle(count: Long) {
        mCommentTitle.count = count
        val first = items.firstOrNull()
        if (first is CommentTitle) {
            first.count = mCommentTitle.count
        }
    }

    fun setAll(all: CommentAll?) {
        mCommentAll = all
        if (items.lastOrNull() is CommentAll) {
            items.removeLastOrNull()
        }
        mCommentAll?.apply {
            items.add(this)
        }
    }

    /**
     * 通知删除Item
     */
    fun notifyItemRemoved(position: Int) {
        mAdapter.items.removeAt(position)
        mAdapter.notifyItemRemoved(position)
    }

    /**
     * 通知更新Item，
     * 请使用 ViewHolder notifyItemChanged
     */
    fun notifyItemChanged(position: Int) {
        mAdapter.notifyItemChanged(position)
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return isCanScroll
    }
}