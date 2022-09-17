package com.kotlin.android.mine.ui.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.doOnTextChanged
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean.Companion.TYPE_MOVIE_PERSON
import com.kotlin.android.user.UserManager
import kotlinx.android.synthetic.main.view_authen_input.view.*

/**
 * create by lushan on 2020/9/8
 * description: 身份验证输入框内容，包含：用户名、真实姓名、身份证号、手机号、邮箱
 * 影评人：用户名、真实姓名、身份证号
 *      必填：用户名、真实姓名、身份证号
 * 电影人： 用户名、真实姓名、身份证号、手机号、邮箱
 *      必填：用户名、真实姓名、身份证号、手机号
 * 机构认证：用户名、真实姓名、身份证号
 *      必填：用户名、真实姓名、身份证号
 */
class AuthenInputView @JvmOverloads constructor(var ctx: Context, var attrs: AttributeSet? = null, var defaultStyle: Int = -1) : LinearLayoutCompat(ctx, attrs, defaultStyle) {

    private var authenType = 0L//认证类型

    init {
        orientation = VERTICAL
        initView()
    }

    private var listener: ((Boolean) -> Unit)? = null//回调输入框是否可以提交

    private fun initView() {
        val inflate = View.inflate(context, R.layout.view_authen_input, null)

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            leftMargin = 15.dp
            rightMargin = 15.dp
        }

        addView(inflate, layoutParams)
    }

    fun setListener(listener: ((Boolean) -> Unit)?) {
        this.listener = listener
    }

    private fun initListener(){
        //        真实姓名
        realNameET?.doOnTextChanged { text, start, before, count ->
            callBackCanSubmit()
            setInputLineStyle(false,realNameLine)
        }
//身份证号
        idCardET?.doOnTextChanged { text, start, before, count ->
            callBackCanSubmit()
            setInputLineStyle(false,idCardLine)
        }

//        手机号
        phoneNumET?.doOnTextChanged { text, start, before, count ->
            callBackCanSubmit()
            setInputLineStyle(false,phoneNumLine)
        }
//邮箱
        postBoxET?.doOnTextChanged { text, start, before, count ->
            callBackCanSubmit()
            setInputLineStyle(false,postBoxLine)
        }

    }


    private fun callBackCanSubmit() {
        listener?.invoke(canSubmit())
    }

    /**
     * 是否可以提交
     */
    fun canSubmit(): Boolean {
        return when (authenType) {
            TYPE_MOVIE_PERSON -> {//电影人认证
                TextUtils.isEmpty(getRealName()).not() && TextUtils.isEmpty(getIdCard()).not() && TextUtils.isEmpty(getPhoneNum()).not()
            }
            else -> {
                TextUtils.isEmpty(getRealName()).not() && TextUtils.isEmpty(getIdCard()).not()
            }
        }
    }

    /**
     * @param type 认证类型
     */
    fun setInputType(type: Long) {
        this.authenType = type
        setPhoneAndPostState(type == TYPE_MOVIE_PERSON)//如果是电影人认证需要手机号和邮箱
        userNameTv?.text = UserManager.instance.user?.nickname.orEmpty()
        initListener()
    }


    /**
     * 真实姓名输入错误
     */
    fun realNameError() {
        setInputLineStyle(true, realNameLine)
    }

    /**
     * 身份证号输入错误
     */
    fun idCardError(){
        setInputLineStyle(true,idCardLine)
    }

    /**
     * 手机号输入错误
     */
    fun phoneError(){
        setInputLineStyle(true,phoneNumLine)
    }

    /**
     * 邮箱输入错误
     */
    fun postError(){
        setInputLineStyle(true,postBoxLine)
    }
    /**
     * 设置输入框下划线颜色
     */
    private fun setInputLineStyle(isError: Boolean, line: View?) {
        line?.setBackgroundColor(getColor(if (isError) R.color.color_ff5a36 else R.color.color_f3f3f4))
    }

    /**
     * 获取真实姓名
     */
    fun getRealName(): String = realNameET?.text?.toString()?.trim().orEmpty()

    /**
     * 获取身份证号
     */
    fun getIdCard(): String = idCardET?.text?.toString()?.trim().orEmpty()

    /**
     * 获取手机号
     */
    fun getPhoneNum(): String = phoneNumET?.text?.toString()?.trim().orEmpty()

    /**
     * 获取邮箱
     */
    fun getPostNum(): String = postBoxET?.text?.toString()?.trim().orEmpty()


    /**
     * 设置邮箱和手机号的显示和隐藏
     */
    private fun setPhoneAndPostState(show: Boolean) {
        postBoxLine?.visible(show)
        postBoxET?.visible(show)
        postBoxTipsTv?.visible(false)//邮箱都不是必填
        postBoxTitleTv?.visible(show)
        phoneNumLine?.visible(show)
        phoneNumET?.visible(show)
        phoneNumTips?.visible(show)
        phoneNumTipsTv?.visible(show)
        phoneNumTitleTv?.visible(show)
    }
}