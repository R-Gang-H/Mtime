package com.mtime

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import com.kotlin.android.app.router.ext.parseAppLink
import com.kotlin.android.ktx.ext.log.d
import org.json.JSONException
import org.json.JSONObject


/**
 * 创建者: zl
 * 创建时间: 2021/6/21 3:59 下午
 * 描述:https://docs.jiguang.cn/jpush/client/Android/huawei-Push-guide/#activity
 */
class OpenClickActivity : AppCompatActivity() {

    private val TAG = "jiguang";

    /**消息Id */
    private val KEY_MSGID = "msg_id"

    /**该通知的下发通道 */
    private val KEY_WHICH_PUSH_SDK = "rom_type"

    /**通知标题 */
    private val KEY_TITLE = "n_title"

    /**通知内容 */
    private val KEY_CONTENT = "n_content"

    private lateinit var mTextView: TextView

    /**通知附加字段 */
    private val KEY_EXTRAS = "n_extras"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "#### OpenClickActivity 启动".d()
        mTextView = TextView(this)
        setContentView(mTextView)
        handleOpenClick()

    }

    /**
     * 处理点击事件，当前启动配置的Activity都是使用
     * Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
     * 方式启动，只需要在onCreat中调用此方法进行处理
     */
    private fun handleOpenClick() {
        Log.d(TAG, "handleOpenClick用户点击打开了通知")
        var data: String? = null
        //获取华为平台附带的jpush信息
        if (intent.data != null) {
            data = intent.data.toString()
        }

        //获取fcm、oppo、vivo、华硕、小米平台附带的jpush信息
        if (TextUtils.isEmpty(data) && intent.extras != null) {
            data = intent.extras?.getString("JMessageExtra")
        }
        Log.d(TAG, "msg data is " + data.toString())
        if (TextUtils.isEmpty(data)) return
        try {
            val jsonObject = JSONObject(data)
            val msgId = jsonObject.optString(KEY_MSGID)
            val whichPushSDK = jsonObject.optInt(KEY_WHICH_PUSH_SDK).toByte()
            val title = jsonObject.optString(KEY_TITLE)
            val content = jsonObject.optString(KEY_CONTENT)
            val extras = jsonObject.optString(KEY_EXTRAS)
//            val extra = Gson().fromJson(extras, Extra::class.java)
            Log.d(TAG,"jgPushBean-> $extras")
            val sb = StringBuilder()
            sb.append("msgId:")
            sb.append(msgId.toString())
            sb.append("\n")
            sb.append("title:")
            sb.append(title.toString())
            sb.append("\n")
            sb.append("content:")
            sb.append(content.toString())
            sb.append("\n")
            sb.append("extras:")
            sb.append(extras.toString())
            sb.append("\n")
            sb.append("platform:")
            sb.append(whichPushSDK)
//            mTextView.text = sb.toString()
            Log.d(TAG, "sb is " + sb.toString())
//            sensorsPush(msgId, bean?.jumpTo)
            //上报点击事件
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK)
            val `object` = JSONObject(extras)
            val appLinkData = `object`.getString("applinkData")
            Log.d(TAG,"appLinkData-> $appLinkData")
            parseAppLink(this@OpenClickActivity, appLinkData)

//            RouterManager.instance.getProvider(IMainProvider::class.java)?.jump(msgId, extras)
            finish()
        } catch (e: JSONException) {
            Log.e(TAG, "parse notification error")
        }
    }
}