package com.mtime.bussiness.mine.widget;

import android.app.Activity;
import androidx.collection.ArrayMap;
import android.view.View;

import com.mtime.frame.App;
import com.mtime.bussiness.mine.bean.MemberCenterPopupBean;
import com.mtime.bussiness.mine.bean.MemberGiftDismantleBean;
import com.mtime.network.RequestCallback;
import com.mtime.network.ConstantUrl;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;

import java.util.Map;

/**
 * Created by vivian.wei on 2017/7/6.
 * 会员首页弹窗
 * 有3种类型弹窗：生日礼包、等级礼包、升级提示
 * 优先级如下：生日礼包>等级礼包>升级提示，最多只能弹出1个
 */

public class MemberCenterPopup {

    private final Activity context;
    // 会员礼包类型
    private int giftType;
    // 会员弹窗回调
    private RequestCallback getMemberCenterPopupCallback;
    // 会员拆礼物回调
    private RequestCallback giftDismantleCallback;
    // 拆礼物成功后回调(会员首页、会员权益页）
    private final OnDismantleSuccessListener dismantleSuccessListener;

    private static final String SERVICE_NUMBER = "4006059500";
    private static final String DISMANTLE_FAIL_DLG_TEXT = "礼包领取失败，请拨打时光网客服电话要求补发。";
    private static final String DISMANTLE_FAIL_DLG_OK_TEXT = "联系客服";
    private static final String DISMANTLE_FAIL_DLG_CANCEL_TEXT = "关闭";
    private static final String BIND_MOBILE_DLG_TEXT = "绑定手机号才可以领取礼包哦！先去绑定手机号，然后到个人中心的会员俱乐部领取礼包吧！";
    private static final String BIND_MOBILE_DLG_OK_TEXT = "去绑定手机号";
    private static final String BIND_MOBILE_DLG_CANCEL_TEXT = "放弃领取";

    /**
     * 会员弹窗
     *
     * @param requestPopup
     * true 来自会员首页需要请求接口/member/center/popup.api
     * false-来自h5会员权益页直接拆礼物
     *
     * @param listener
     * 拆礼包成功后页面级回调方法，如：刷新页面数据等
     *
     */
    public MemberCenterPopup(final Activity context, final boolean requestPopup,
                             final OnDismantleSuccessListener listener) {
        this.context = context;
        dismantleSuccessListener = listener;
        initEvent();
        if(requestPopup) {
            requestData();
        }
    }

    private void initEvent() {
        // 会员弹窗回调
        getMemberCenterPopupCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                /**
                 * 会员中心首页弹窗
                 * 有3种类型：生日礼包、等级礼包、升级提示
                 * 弹窗优先级如下：生日礼包>等级礼包>升级提示，最多只能弹出1个
                 * 弹的类型和弹的次数均由api控制
                 */
                MemberCenterPopupBean popupBean = (MemberCenterPopupBean) o;
                if (null == popupBean || App.GIFT_TYPE_NONE == popupBean.getType()) {
                    return;
                }

                if (App.GIFT_TYPE_LEVEL == popupBean.getType()
                        || App.GIFT_TYPE_BIRTH == popupBean.getType()) {
                    // 生日/等级礼包
                    showGiftDlg(popupBean);
                } else if (App.GIFT_TYPE_UPDATE == popupBean.getType()
                        && popupBean.getLevel() > App.USER_LEVER_NORMAL
                        && popupBean.getLevel() <= App.USER_LEVER_DIAMOND) {
                    // 升级提示
                    showUpdateLevelDlg(popupBean);
                }
            }

            @Override
            public void onFail(Exception e) {
            }
        };

        // 会员拆礼物回调
        giftDismantleCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                MemberGiftDismantleBean dismantleBean = (MemberGiftDismantleBean) o;
                if(null == dismantleBean) {
                    return;
                }

                // 0:兑换失败 1:兑换成功 2 需绑定手机号
                switch (dismantleBean.getBizCode()) {
                    case 0: // 拆礼物_兑换失败弹窗
                        showDismantleFailDlg();
                        break;
                    case 1: // 拆礼物_兑换成功弹窗
                        showDismantleSuccessDlg(giftType, dismantleBean);
                        // 拆礼物成功后执行回调刷新调用页面
                        if(null != dismantleSuccessListener) {
                            dismantleSuccessListener.onSuccess();
                        }
                        break;
                    case 2: // 拆礼物_绑定手机号弹窗
                        showBindMobileDlg();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFail(Exception e) {
            }
        };
    }

    private void requestData() {
        // 会员中心首页弹窗
        HttpUtil.get(ConstantUrl.GET_MEMBER_CENTER_POPUP, null, MemberCenterPopupBean.class, getMemberCenterPopupCallback);
    }

    // 生日/等级礼包
    private void showGiftDlg(MemberCenterPopupBean popupBean) {
        final MemberCenterGiftDlg dlg = new MemberCenterGiftDlg(context, popupBean);

        // 拆礼物事件
        final int type = popupBean.getType();
        dlg.setBtnDismantleListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dlg.dismiss();
                giftDismantle(type);
            }
        });

        // 关闭事件
        dlg.setBtnCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                dlg.dismiss();
            }
        });

        dlg.show();
    }

    /*
    *  拆礼包
    *  h5权益详情页点击"立即领取"也会回调这个方法（注意type的定义需要转换）
    *  type: 1等级、2生日
    * */
    public void giftDismantle(final int type) {
        giftType = type;
        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("type", String.valueOf(type));
        HttpUtil.post(ConstantUrl.GET_MEMBER_GIFT_DISMANTLE, parameterList, MemberGiftDismantleBean.class, giftDismantleCallback);
    }

    // 拆礼物_兑换成功弹窗
    private void showDismantleSuccessDlg(int type, MemberGiftDismantleBean dismantleBean) {
        final MemberCenterGiftContentDlg dlg = new MemberCenterGiftContentDlg(context, type, dismantleBean);
        // 点击事件
        dlg.setBtnGotoCouponListListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dlg.dismiss();
                // 个人中心-优惠券
                JumpUtil.startMyVoucherListActivity(context,"", 0);
            }
        });

        // 关闭事件
        dlg.setBtnCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                dlg.dismiss();
            }
        });

        dlg.show();
    }

    // 拆礼物_兑换失败弹窗
    private void showDismantleFailDlg() {
        final CustomAlertDlg dlg = new CustomAlertDlg(context, CustomAlertDlg.TYPE_OK_CANCEL);
        dlg.setBtnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                dlg.dismiss();
            }
        });
        dlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dlg.dismiss();
                // 拨打电话
                JumpUtil.actionCall(context, SERVICE_NUMBER);
            }
        });
        dlg.show();
        dlg.setText(DISMANTLE_FAIL_DLG_TEXT);
        dlg.setBtnOkText(DISMANTLE_FAIL_DLG_OK_TEXT);
        dlg.setBtnCancelText(DISMANTLE_FAIL_DLG_CANCEL_TEXT);
    }

    // 拆礼物_绑定手机号弹窗
    private void showBindMobileDlg() {
        final CustomAlertDlg dlg = new CustomAlertDlg(context, CustomAlertDlg.TYPE_OK_CANCEL);
        dlg.setBtnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                dlg.dismiss();
            }
        });
        dlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dlg.dismiss();
                JumpUtil.startProfileActivity(context,"");
            }
        });
        dlg.show();
        dlg.setText(BIND_MOBILE_DLG_TEXT);
        dlg.setBtnOkText(BIND_MOBILE_DLG_OK_TEXT);
        dlg.setBtnCancelText(BIND_MOBILE_DLG_CANCEL_TEXT);
    }

    // 升级提示弹窗
    private void showUpdateLevelDlg(MemberCenterPopupBean popupBean) {
        final MemberCenterUpdateLevelDlg dlg = new MemberCenterUpdateLevelDlg(context, popupBean);

        dlg.setBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dlg.dismiss();
            }
        });

        // 关闭事件
        dlg.setBtnCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                dlg.dismiss();
            }
        });

        dlg.show();
    }

    // 拆礼物成功后回调接口
    public interface OnDismantleSuccessListener {
        void onSuccess();
    }
}
