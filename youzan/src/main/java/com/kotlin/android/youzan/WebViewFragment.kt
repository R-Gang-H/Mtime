package com.kotlin.android.youzan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.youzan.androidsdkx5.YouzanBrowser

/**
 * @des 有赞webview
 * @author zhangjian
 * @date 2021/10/25 16:42
 */
abstract class WebViewFragment : Fragment() {
    private var mWebView: YouzanBrowser? = null
    private var mIsWebViewAvailable = false

    /**
     * Called to instantiate the view. Creates and returns the WebView.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (mWebView != null ) {
            mWebView?.destroy()
        }
        val contentView: View = inflater.inflate(getLayoutId(), container, false)
        mWebView = contentView.findViewById<View>(getWebViewId()) as YouzanBrowser
        mIsWebViewAvailable = true
        return contentView
    }

    /**
     * @return The id of WebView in layout
     */
    @IdRes
    protected abstract fun getWebViewId(): Int

    /**
     * @return the layout id for Fragment
     */
    @LayoutRes
    protected abstract fun getLayoutId(): Int

    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    override fun onPause() {
        super.onPause()
        mWebView?.onPause()
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    override fun onResume() {
        mWebView?.onResume()
        super.onResume()
    }

    /**
     * Called when the WebView has been detached from the fragment.
     * The WebView is no longer available after this time.
     */
    override fun onDestroyView() {
        mIsWebViewAvailable = false
        super.onDestroyView()
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    override fun onDestroy() {
        if (mWebView != null) {
            mWebView!!.destroy()
            mWebView = null
        }
        super.onDestroy()
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     *
     * @return True if the host application wants to handle back press by itself, otherwise return false.
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    /**
     * Gets the WebView.
     */
    fun getWebView(): YouzanBrowser? {
        return if (mIsWebViewAvailable) mWebView else null
    }


}