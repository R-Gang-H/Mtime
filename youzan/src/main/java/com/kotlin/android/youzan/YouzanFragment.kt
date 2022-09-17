package com.kotlin.android.youzan

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.youzan.androidsdk.YouzanSDK
import com.youzan.androidsdk.YouzanToken
import com.youzan.androidsdk.YzLoginCallback
import com.youzan.androidsdk.event.*
import com.youzan.androidsdk.model.goods.GoodsShareModel
import com.youzan.androidsdk.model.trade.TradePayFinishedModel
import com.youzan.androidsdkx5.YouzanBrowser

/**
 * @author zhangjian
 * @date 2021/10/25 17:06
 */
class YouzanFragment : WebViewFragment(), SwipeRefreshLayout.OnRefreshListener {
    private var mView: YouzanBrowser? = null
    private var mRefreshLayout: SwipeRefreshLayout? = null
    private var mToolbar: Toolbar? = null
    private val CODE_REQUEST_LOGIN = 0x1000

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupYouzan()
        val url = arguments?.getString(YouzanActivity.KEY_URL)
        mView?.loadUrl(url)
        //加载H5时，开启默认loading
        //设置自定义loading图片
//        mView.setLoadingImage(R.mipmap.ic_launcher);
    }


    private fun setupViews(contentView: View) {
        //WebView
        mView = getWebView()
        mToolbar = contentView.findViewById<View>(R.id.toolbar) as Toolbar
        mRefreshLayout = contentView.findViewById<View>(R.id.swipe) as SwipeRefreshLayout

        //分享按钮
        mToolbar?.setTitle(R.string.loading_page)
        mToolbar?.inflateMenu(R.menu.menu_youzan_share)
        mToolbar?.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_share -> {
                    mView?.sharePage()
                    true
                }
                else -> false
            }
        }

        //刷新
        mRefreshLayout!!.setOnRefreshListener(this)
        mRefreshLayout!!.setColorSchemeColors(Color.BLUE, Color.RED)
        mRefreshLayout!!.isEnabled = false
    }

    private fun setupYouzan() {
        mView!!.subscribe(object : AbsCheckAuthMobileEvent() {})
        //认证事件, 回调表示: 需要需要新的认证信息传入
        mView!!.subscribe(object : AbsAuthEvent() {
            override fun call(context: Context, needLogin: Boolean) {
                /**
                 * 建议实现逻辑:
                 *
                 * 判断App内的用户是否登录?
                 * => 已登录: 请求带用户角色的认证信息(login接口);
                 * => 未登录: needLogin为true, 唤起App内登录界面, 请求带用户角色的认证信息(login接口);
                 * => 未登录: needLogin为false, 请求不带用户角色的认证信息(initToken接口).
                 *
                 * 服务端接入文档: https://www.youzanyun.com/docs/guide/appsdk/683
                 */
                //TODO 自行编码实现. 具体可参考开发文档中的伪代码实现
                //TODO 手机号自己填入
                YouzanSDK.yzlogin(
                    "6630418",
                    "https://cdn.daddylab.com/Upload/android/20210113/021119/au9j4d6aed5xfweg.jpeg?w=1080&h=1080",
                    "",
                    "一百亿养乐多",
                    "0",
                    object : YzLoginCallback {
                        override fun onSuccess(youzanToken: YouzanToken) {
                            mView!!.post { mView!!.sync(youzanToken) }
                        }

                        override fun onFail(s: String) {}
                    })
            }
        })
        mView!!.subscribe(object : AbsCheckAuthMobileEvent() {})
        //文件选择事件, 回调表示: 发起文件选择. (如果app内使用的是系统默认的文件选择器, 该事件可以直接删除)
        mView!!.subscribe(object : AbsChooserEvent() {
            @Throws(ActivityNotFoundException::class)
            override fun call(context: Context, intent: Intent, requestCode: Int) {
                startActivityForResult(intent, requestCode)
            }
        })

        //页面状态事件, 回调表示: 页面加载完成
        mView!!.subscribe(object : AbsStateEvent() {
            override fun call(context: Context) {
                mToolbar?.title = mView?.title

                //停止刷新
                mRefreshLayout!!.isRefreshing = false
                mRefreshLayout!!.isEnabled = true
            }
        })
        //分享事件, 回调表示: 获取到当前页面的分享信息数据
        mView!!.subscribe(object : AbsShareEvent() {
            override fun call(context: Context, data: GoodsShareModel) {
                /**
                 * 在获取数据后, 可以使用其他分享SDK来提高分享体验.
                 * 这里调用系统分享来简单演示分享的过程.
                 */
                val content = data.desc + data.link
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, content)
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, data.title)
                sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
        })
        mView!!.subscribe(object : AbsPaymentFinishedEvent() {
            override fun call(context: Context, tradePayFinishedModel: TradePayFinishedModel) {}
        })
    }


    override fun getWebViewId(): Int {
        //YouzanBrowser在布局文件中的id
        return R.id.view
    }


    override fun getLayoutId(): Int {
        //布局文件
        return R.layout.fragment_youzan
    }

    override fun onBackPressed(): Boolean {
        //页面回退
        return getWebView()!!.pageGoBack()
    }

    override fun onRefresh() {
        //重新加载页面
        mView!!.reload()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (CODE_REQUEST_LOGIN == requestCode) { // 如果是登录事件返回
            if (resultCode == Activity.RESULT_OK) {
                // 登录成功设置token
            } else {
                // 登录失败
                mView!!.syncNot()
            }
        } else {
            // 文件选择事件处理。
            mView!!.receiveFile(requestCode, data)
        }
    }
}