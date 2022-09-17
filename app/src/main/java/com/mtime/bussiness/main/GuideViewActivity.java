package com.mtime.bussiness.main;

import java.util.ArrayList;
import java.util.List;

import com.kotlin.android.ktx.ext.statusbar.StatusBarExtKt;
import com.mtime.R;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.location.CityChangeActivity;
import com.mtime.bussiness.main.maindialog.bean.PricacyPolicyBean;
import com.mtime.bussiness.main.maindialog.dialog.PrivacyPolicyDialog;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.constant.Constants;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.util.UIUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 首次进入程序时显示的引导界面
 */
public class GuideViewActivity extends BaseActivity {
    private List<View> views = null;
    private ViewPager leadViewPager = null;
    private BaseApi mApi;

    public static void launch(Context context, int tabIndex, String newRegisterGitUrl) {
        Intent launcher = new Intent(context, GuideViewActivity.class);
        launcher.putExtra(Constants.KEY_MAIN_TAB_INDEX, tabIndex);
        if (!(context instanceof Activity)) {
            launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(launcher);
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
//        StatusBarUtil.setTranslucentStatus(this, true);
        StatusBarExtKt.handleArticleStatusBar(this,false);
        setContentView(R.layout.leadview);
        final int[] imageBgs = new int[]{R.mipmap.lead_bg1, R.mipmap.lead_bg2, R.mipmap.lead_bg3,
                R.mipmap.lead_bg4};
        final int[] imageContents = new int[]{R.mipmap.lead_bg1_iv, R.mipmap.lead_bg2_iv, R.mipmap.lead_bg3_iv,
                R.mipmap.lead_bg4_iv};
        views = new ArrayList<View>();
        for (int i = 0; i < imageBgs.length; i++) {
            final View view = View.inflate(this, R.layout.leadview_item, null);

            ImageView ivLeadviewBg = view.findViewById(R.id.iv_lead_view_bg);
            ivLeadviewBg.setImageResource(imageBgs[i]);
            ImageView ivLeadviewContent = view.findViewById(R.id.iv_lead_view_content);
            ivLeadviewContent.setBackgroundResource(imageContents[i]);
            if (i == (imageBgs.length - 1)) {
                TextView btnLeadView = view.findViewById(R.id.btn_lead_view);
                btnLeadView.setVisibility(View.VISIBLE);
                btnLeadView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // 提前到app启动页，显示隐私条款弹框(工信部的要求)
//                        loadPrivacyPolicy();
                        gotoCityChangeActivity();
                    }
                });
            }
            views.add(view);
        }

        leadViewPager = findViewById(R.id.viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mApi) {
            mApi.cancel();
        }
    }

    private void loadPrivacyPolicy() {
        UIUtil.showLoadingDialog(this);
        if (null == mApi) {
            mApi = new BaseApi() {

                @Override
                protected String host() {
                    return null;
                }
            };
        }
        mApi.get(mApi, ConstantUrl.GET_PRIVACY_POLICY, null, 0, null, new NetworkManager.NetworkListener<PricacyPolicyBean>() {
            @Override
            public void onSuccess(PricacyPolicyBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (null != result) {
                    PrivacyPolicyDialog.show(result, getSupportFragmentManager(), new PrivacyPolicyDialog.OnDismissListner() {
                        @Override
                        public void onDismiss(boolean isOk) {
                            if (isOk) {
                                App.getInstance().getPrefsManager().putBoolean(App.getInstance().MORE_THAN_ONCE, true);
                                App.getInstance().initUserPrivacyInfos();
                                gotoCityChangeActivity();
                            }
                        }
                    });
                } else {
                    MToastUtils.showShortToast(R.string.s_load_data_err);
                }
            }

            @Override
            public void onFailure(NetworkException<PricacyPolicyBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(R.string.s_load_data_err);
            }
        }, false);

        /*String json = "{\n" +
                "\t\t\"content\": \"欢迎使用时光网产品/服务！在我们正式为您提供服务前，为了更好地保障您的个人权益，请务必审慎阅读《Mtime时光网服务条款》与《隐私政策》内的所有条款，本服务条款及隐私政策将向您说明：\\n只有当您阅读并同意后，我们才会为您提供时光网的全部产品/服务内容，为更好的向您提供个性化推荐、购票购物、交流互动等服务，在使用过程中，可能会收集您的位置、设备信息、联系方式等必要信息，以便为您提供更好的服务。您可在使用过程中随时取消授权，但取消授权后相关服务可能会受影响。\\n我们在您使用服务过程中收集的信息：\\n1.\\t第三方服务提供商与业务合作伙伴指定的与您相关的信息：我们可能收集并使用如第三方服务提供商与业务合作伙伴分配的ID。\\n2.\\t与您的应用使用相关的信息： 包括应用基础信息，例如应用ID信息、SDK版本、应用设置（地区、语言、时区、字体），以及应用状态记录（例如下载、安装、更新、删除）。\\n3.\\t您在使用服务时生成的信息： 例如社区/平台中您的用户等级、签到信息、浏览记录；您在使用社区服务时的站内信（仅发送和接收的双方可见）；您在使用推送服务时的推送文本；您在使用广告服务时的行为（如点击、曝光）。\\n4.\\t位置信息（仅适用于特定服务/功能）： 若您使用和位置相关的服务（如使用影院定位，附近的影院，刷新位置），我们可能采集与您设备的精确或模糊位置相关的各类信息。例如地区、国家代码、城市代码、移动网络代码、移动国家代码、经纬度信息、时区设置和语言设置。您可以随时在手机设置（设置-权限）中关闭位置服务。\\n5.\\t日志信息： 与您使用某些功能、应用和网站相关的信息。例如Cookie和其他匿名标识符技术、互联网协议（IP）地址、网络请求信息、临时消息历史、标准系统日志、错误崩溃信息、使用服务产生的日志信息（如注册时间、访问时间、活动时间等），可能应用于个性化推荐算法中，以推荐用户感兴趣的内容。\\n6.\\t其他信息：\\tIMEI，IMSI，Mac地址，环境特征值 (ECV)，即从时光账号、设备标识、连接的Wi-Fi产生的信息和地理位置信息。\\n您点击“同意”的行为即表示您已阅读完毕并同意以上协议的全部内容，包括您同意我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，您的用户权利等条款，约定我们的限制责任、免责条款，以及协议内的其他所有条款。如您对以上协议有任何疑问，可通过客服电话4006059500与我们联系。\\n\",\n" +
                "\t\t\"policyVersion\": \"20191217\",\n" +
                "\t\t\"title\": \"Mtime服务条款与隐私保护概览\"\n" +
                "\t}";
        PricacyPolicyBean result = MJsonUtils.parseString(json, PricacyPolicyBean.class);
        PrivacyPolicyDialog.show(result, getSupportFragmentManager(), new PrivacyPolicyDialog.OnDismissListner() {
            @Override
            public void onDismiss(boolean isOk) {
                if (isOk) {
                    App.getInstance().getPrefsManager().putBoolean(App.getInstance().MORE_THAN_ONCE, true);
                    App.getInstance().initUserPrivacyInfos();
                    gotoCityChangeActivity();
                }
            }
        });*/
    }

    private void gotoCityChangeActivity() {
        final Intent intent = new Intent();
        intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, MainTabActivity.class.getName());
        intent.putExtra(Constants.KEY_MAIN_TAB_INDEX, LoadManager.getAndroidTab());
        intent.putExtra(CityChangeActivity.KEY_IS_FROM_GUIDE, true);
        intent.setClassName(getPackageName(), CityChangeActivity.class.getName());
        startActivity(intent);
        GuideViewActivity.this.finish();
    }

    @Override
    protected void onInitEvent() {
        leadViewPager.setAdapter(new ViewPagerAdapter());
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return null != views ? views.size() : 0;
        }

        @Override
        public Object instantiateItem(final View arg0, final int arg1) {
            // 添加item
            ((ViewPager) arg0).addView(views.get(arg1));

            return views.get(arg1);
        }

        @Override
        public void destroyItem(final View arg0, final int arg1, final Object arg2) {
            // 移除item
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(final View arg0, final Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void finishUpdate(final View arg0) {
        }

        @Override
        public void restoreState(final Parcelable arg0, final ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(final View arg0) {
        }

    }

    @Override
    protected void onInitVariable() {
        setPageLabel("intro");
    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onRequestData() {
    }

    @Override
    protected void onUnloadData() {
    }

}
