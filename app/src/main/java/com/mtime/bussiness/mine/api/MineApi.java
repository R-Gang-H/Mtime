package com.mtime.bussiness.mine.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kotlin.android.app.data.entity.user.User;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.mine.bean.MessageConfigsSetBean;
import com.mtime.bussiness.mine.bean.UnreadMessageBean;
import com.mtime.bussiness.mine.login.bean.AuthorizeLoginBean;
import com.mtime.bussiness.mine.login.bean.AuthorizePageBean;
import com.mtime.bussiness.mine.login.bean.CapchaBean;
import com.mtime.bussiness.mine.login.bean.EmailSuffix;
import com.mtime.bussiness.mine.login.bean.LoginBean;
import com.mtime.bussiness.mine.login.bean.RegisterBean;
import com.mtime.bussiness.mine.login.bean.SmsCodeBean;
import com.mtime.bussiness.mine.login.bean.SmsRegetPasswordVeryCode;
import com.mtime.bussiness.mine.login.bean.ThirdLoginBean;
import com.mtime.bussiness.mine.profile.bean.BindMobileResultBean;
import com.mtime.bussiness.mine.profile.bean.RegetPasswordVeryCodeBean;
import com.mtime.bussiness.mine.profile.bean.RegetPasswordWithLoginBean;
import com.mtime.bussiness.mine.profile.bean.UpdateMemberInfoBean;
import com.mtime.bussiness.ticket.cinema.bean.SyncFavoriteCinemaBean;
import com.mtime.bussiness.ticket.movie.bean.SmsVeryCodeBean;
import com.mtime.network.ConstantUrl;
import com.mtime.util.Des;

import android.text.TextUtils;

/**
 * @author zhuqiguang
 * @date 2019/4/2
 * @blog www.zhuqiguang.cn
 * @desc 类描述
 */
public class MineApi extends BaseApi {

    // 获取我的消息-话题邀请列表  参数cleanUnreadCount值  0不清空  1清空
    public static final int TOPIC_INVITE_LIST_CLEAN_UNREAD_COUNT_PARAM_NO = 0;
    public static final int TOPIC_INVITE_LIST_CLEAN_UNREAD_COUNT_PARAM_YES = 1;

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    @Override
    public void cancel() {
        NetworkManager.getInstance().cancel(this);
    }

    // 登录模块-获取邮箱后缀列表
    public void getEmailExtensions(NetworkManager.NetworkListener<EmailSuffix> listener) {
        get(this, ConstantUrl.MAIL_EXTENSIONS, null, listener);
    }

    // 用户登录接口(包括关联第三方，强制绑定手机号登录)
    public void userLogin(String name, String password, String imgCode, String imgCodeId,
                          String mobile, String smsCode, String smsCodeId, String oauthToken,
                          NetworkManager.NetworkListener<LoginBean> listener) {
        Map<String, String> params = new HashMap<>(8);
        params.put("name", name);                       // 账号（邮箱、手机号）
        params.put("password", Des.encode(password));   // password这里改成aes加密
        params.put("code", imgCode);                    // 图片验证码
        params.put("codeId", imgCodeId);                // 图片验证码id
        params.put("mobile", mobile);                   // 需绑定的手机号
        params.put("vcode", smsCode);                   // 短信验证码
        params.put("vcodeId", smsCodeId);               // 短信验证码id
        params.put("oauthToken", oauthToken);           // 第三方授权
        post(this, ConstantUrl.POST_LOGIN, params, listener);
    }

    // 同步影院收藏
    public void syncFavoriteCinema(String item, NetworkManager.NetworkListener<SyncFavoriteCinemaBean> listener) {
        Map<String, String> params = new HashMap<String, String>(2);
        params.put("addIds", item);
        params.put("deleteFIds", "");
        post(this, ConstantUrl.SYNC_FAVORITE_CINEMA, params, listener);
    }

    // 设置消息通知设置接口
    public void setMessageConfigs(String pushtoken, String jpushid, NetworkManager.NetworkListener<MessageConfigsSetBean> listener) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("deviceToken", pushtoken);
        params.put("jPushRegId", jpushid);
        params.put("setMessageConfigType", "2");
        post(this, ConstantUrl.SET_MESSAGECONFIGS, params, listener);
    }

    // 用户账户信息
    public void getAccountDetail(NetworkManager.NetworkListener<User> listener) {
        get(this, ConstantUrl.ACCOUNT_DETAIL, null, listener);
    }

    /**
     * 用户账户信息-2018新版
     * 仅用于绑定手机号后获取用户信息，其他情况使用上面不带参数的方法
     *
     * @param param    0:默认 1:用户绑定手机号后，跳过缓存重新获取信息
     * @param listener
     */
    public void getAccountDetail(int param, NetworkManager.NetworkListener<User> listener) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("param", String.valueOf(param));
        get(this, ConstantUrl.ACCOUNT_DETAIL, params, listener);
    }

    // 获取图片验证码
    public void getImageVerifyCode(NetworkManager.NetworkListener<CapchaBean> listener) {
        get(this, ConstantUrl.GET_IMAGE_VERITY_CODE, null, listener);
    }

    // 第三方授权登录
    public void userOauthLogin(String token, String expires, String platformId, String code,
                               String mobileToken, String password, String confirmPassword,
                               NetworkManager.NetworkListener<ThirdLoginBean> listener) {
        Map<String, String> params = new HashMap<String, String>(7);
        params.put("accessToken", token);
        params.put("qqExpiresIn", expires);
        params.put("platformId", platformId);
        params.put("code", code);
        params.put("mobileToken", mobileToken);         // 绑定的手机token
        params.put("password", Des.encode(password));               // 密码
        params.put("confirmPassword", Des.encode(confirmPassword)); // 确认密码
        post(this, ConstantUrl.POST_THIRD_LOGIN, params, listener);
    }

    // 第三方登录, h5页面接口
    public void getAuthorizePage(NetworkManager.NetworkListener<List<AuthorizePageBean>> listener) {
        get(this, ConstantUrl.GET_AUTHORIZE_PAGE, null, listener);
    }

    // 第三方授权H5回调登录接口
    public void authWapLogin(String url, NetworkManager.NetworkListener<AuthorizeLoginBean> listener) {
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("url", url);
        post(this, ConstantUrl.POST_THIRD_LOGIN_WITHH5, params, listener);
    }

    // 发送短信验证码（不检查手机号是否已经注册，短信验证码登录页使用）
    public void sendLoginSmsCode(String mobile, String imgCodeId, String imgCode, NetworkManager.NetworkListener<SmsCodeBean> listener) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("mobile", mobile);
        params.put("imgCodeId", imgCodeId);
        params.put("imgCode", imgCode);
        post(this, ConstantUrl.POST_LOGIN_SMS_CODE, params, listener);
    }

    // 检查绑定手机号并发送短信验证码
    public void checkBindMobile(String mobile, int bindtype, String imgCodeId, String imgCode, String oauthToken,
                                int existMobileAllow, NetworkManager.NetworkListener<SmsVeryCodeBean> listener) {
        Map<String, String> params = new HashMap<String, String>(6);
        params.put("mobile", mobile);
        params.put("bindType", String.valueOf(bindtype)); // 默认0:新绑定 1:更换绑定
        params.put("imgCodeId", imgCodeId);
        params.put("imgCode", imgCode);
        params.put("oauthToken", oauthToken); // （接口返回的)第三方授权token
        params.put("existMobileAllow", String.valueOf(existMobileAllow)); // 是否允许已经注册过时光网账号的Mobile号码, 2019新版绑定手机页传 1:允许
        post(this, ConstantUrl.POST_BIND_GET_SMSCODE, params, listener);
    }

    // 绑定的手机号短信验证码校验
    public void verifyBindMobileSmsCode(String mobile, String smsCode, String smsCodeId, String verifyOldMobile, NetworkManager.NetworkListener<SmsVeryCodeBean> listener) {
        Map<String, String> params = new HashMap<>(3);
        params.put("mobile", mobile);
        params.put("code", smsCode);
        params.put("codeId", smsCodeId);
        params.put("verifyOldMobile", verifyOldMobile);
        post(this, ConstantUrl.POST_VERIFY_BIND_MOBILE_WITH_SMS_CODE, params, listener);
    }

    // 绑定手机号(已经注册为时光网账号的用户绑定手机号)
    public void bindMobile(String mobile, String smsCode, String smsCodeId, String oauthToken, NetworkManager.NetworkListener<BindMobileResultBean> listener) {
        Map<String, String> params = new HashMap<String, String>(4);
        params.put("mobile", mobile);
        params.put("code", smsCode);
        params.put("codeId", smsCodeId);
        params.put("oauthToken", oauthToken);
        post(this, ConstantUrl.POST_BIND_MOBILE, params, listener);
    }

    // 手机找回密码发送校验码接口
    public void forgetPwdSendCode(String account, int type, String imgCodeId, String imgCode, NetworkManager.NetworkListener<SmsRegetPasswordVeryCode> listener) {
        Map<String, String> params = new HashMap<String, String>(4);
        params.put("account", account);
        params.put("type", String.valueOf(type)); // 1-手机,0-邮箱
        params.put("imgCodeId", imgCodeId);
        params.put("imgCode", imgCode);
        post(this, ConstantUrl.POST_REGET_PASSWORD_SEND_SMSCODE, params, listener);
    }

    // 忘记密码验证校验码接口
    public void regetPasswordVerycode(String account, String smsCode, String smsCodeId, int type, NetworkManager.NetworkListener<RegetPasswordVeryCodeBean> listener) {
        Map<String, String> params = new HashMap<String, String>(4);
        params.put("account", account);
        params.put("code", smsCode);
        params.put("codeId", smsCodeId);
        params.put("type", String.valueOf(type)); // 1-手机,0-邮箱
        post(this, ConstantUrl.POST_REGET_PASSWORD_VERYCODE, params, listener);
    }

    // 忘记密码保存新密码并自动登录
    public void saveNewPwd(String token, String password, String passwordConfirm, NetworkManager.NetworkListener<RegetPasswordWithLoginBean> listener) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("mobileToken", token);
        params.put("password", Des.encode(password));
        params.put("passwordConfirm", Des.encode(passwordConfirm));
        post(this, ConstantUrl.POST_SAVE_NEW_PASSWORD_OF_FORGET, params, listener);
    }

    // 未注册用户第三方登录设置密码
    public void oauthSetPassword(String oauthToken, String mobileToken, String password, String confirmPassword,
                                 NetworkManager.NetworkListener<RegisterBean> listener) {
        Map<String, String> params = new HashMap<String, String>(4);
        params.put("oauthToken", oauthToken);  // 第三方授权token
        params.put("mobileToken", mobileToken); // 手机校验token
        params.put("password", Des.encode(password)); // 密码
        params.put("confirmPassword", Des.encode(confirmPassword)); // 确认密码
        post(this, ConstantUrl.POST_OUTH_SET_PASSWORD, params, listener);
    }

    // 登录后的第一次绑定手机号后设置密码接口
    public void setPwdAndBindMobile(String mobile, String password, String confirmPassword, NetworkManager.NetworkListener<RegetPasswordVeryCodeBean> listener) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("mobile", mobile);  // 手机号
        params.put("password", Des.encode(password)); // 密码
        params.put("confirmPassword", Des.encode(confirmPassword)); // 确认密码
        post(this, ConstantUrl.POST_SET_MOBILE_PASSWORD, params, listener);
    }

    // 修改密码
    public void modifyPassword(String oldPassword, String password, String passwordConfirm, NetworkManager.NetworkListener<RegetPasswordWithLoginBean> listener) {
        Map<String, String> params = new HashMap<String, String>(3);
        params.put("oldPassword", Des.encode(oldPassword));
        params.put("password", Des.encode(password));
        params.put("passwordConfirm", Des.encode(passwordConfirm));
        post(this, ConstantUrl.POST_MODIFY_PASSWORD, params, listener);
    }

    /**
     * 修改会员基本信息（生日/居住地/签名）
     *
     * @param birthday   生日，格式:2017-3-12 备注：年-月-日
     * @param locationId 居住地id
     * @param userSign   用户签名
     * @param type       1.生日，2居住地 3,用户签名
     * @param listener
     */
    public void updateMemberInfo(String birthday, String locationId, String userSign, String type,
                                 NetworkManager.NetworkListener<UpdateMemberInfoBean> listener) {
        Map<String, String> params = new HashMap<>(2);
        if (!TextUtils.isEmpty(birthday)) {
            params.put("birthday", birthday);
        }
        if (!TextUtils.isEmpty(locationId)) {
            params.put("locationId", locationId);
        }
        if (!TextUtils.isEmpty(userSign)) {
            params.put("userSign", userSign);
        }
        params.put("type", type);
        post(this, ConstantUrl.UPDATE_MEMBERINFO, params, listener);
    }
}
