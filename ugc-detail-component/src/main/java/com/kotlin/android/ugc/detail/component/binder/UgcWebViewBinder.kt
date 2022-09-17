package com.kotlin.android.ugc.detail.component.binder

import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.js.sdk.entity.JsEntity
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcWebViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcDetailWebviewBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by lushan on 2020/8/6
 * 承载html
 */
open class UgcWebViewBinder(var bean: UgcWebViewBean, var recyclerView: RecyclerView?, var videoListener: ((JsEntity.VideoPlayerEntity.DataBean?) -> Unit)? = null,
                             var loadFinishListener:((Any?)->Unit)? = null) : MultiTypeBinder<ItemUgcDetailWebviewBinding>() {
    private var localRect = Rect()
    private var mWebViewHeight = 0
    override fun layoutId(): Int = R.layout.item_ugc_detail_webview


    override fun onBindViewHolder(binding: ItemUgcDetailWebviewBinding, position: Int) {

        recyclerView?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.webView.getWebView()?.apply {
                    getLocalVisibleRect(localRect)
                    val articleHeight = measuredHeight
                    val currentArticleHeight = if (localRect.bottom > articleHeight) articleHeight else localRect.bottom
                    if (articleHeight > 0) {
                        if (mWebViewHeight != articleHeight && currentArticleHeight != articleHeight) {
                            mWebViewHeight = articleHeight
                            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mWebViewHeight)
                        }
//                        mListener.onArticleDetailWebViewItemProgressStatistic(articlesHeight, curArticlesHeight)
                    }
                }

            }
            false
        }
        binding.webView.setData(bean.content,bean.webType,videoListener,loadFinishListener)
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcWebViewBinder && other.bean == bean
    }
}