package com.mtime.frame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;


import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.base.utils.MLogWriter;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.network.NetworkConstant;
import com.mtime.network.UAUtils;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.util.ImageLoader;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.SlidingLayout;

/**
 * 框架页面的基类，封装了基本的页面功能，与配置文件相配合
 *
 * @author MTime
 */
@Deprecated
public abstract class BaseActivity<T, H extends ContentHolder<T>> extends BaseFrameUIActivity<T,H> { // implements
                                                           // ActivityCompat.OnRequestPermissionsResultCallback
    
    @Override
    public ContentHolder onBindContentHolder() {
        return null;
    }
    
    // path
    public String pageClickPath = "";

    // 是否滑动返回设置
    private boolean swipeBack = false;

    protected int requestCode = -1;

    // 沉浸式状态栏开关
    private final boolean isShowStatus = false;

    // 图片加载器
    public ImageLoader volleyImageLoader = new ImageLoader();

    /**
     * 加载必要的数据和参数
     */
    private void loadViriable() {

        if (null != getIntent()) {
        
//            mRequestRefer = getIntent().getStringExtra("request_refer");
//            if (TextUtils.isEmpty(mRequestRefer)) {
//                // 这里只有启动和从 push过来的才不会有request_refer对象，所以需要单独处理一下。
//                // 如果有其他协议进来，再说。理论上都应该从启动页进来后再跳转会合适一些。
//                // 新系统中据说支持了模块跳转，得出来后看具体情况
//                String pushrf = getIntent().getStringExtra("push_rf");
//                if (TextUtils.isEmpty(pushrf)) {
//                    mRequestRefer = "app_";
//                } else {
//                    mRequestRefer = "appPush_";
//                }
//            }
//            HttpUtil.setLastPageRefer(mRequestRefer);
//            NetworkManager.getInstance().setLastPageRefer(mRequestRefer);
        }
    }

    /**
     * 是否启动滑动关闭
     *
     * @return
     */
    protected boolean enableSliding() {
        return false;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // LY：右边滑动关闭页面 note:requestWindowFeature必须放在前面执行
        if (enableSliding()) {
            SlidingLayout rootView = new SlidingLayout(this);
            rootView.bindActivity(this);
        }

        this.loadViriable();
        this.onInitVariable();// 用于子类初始化设置或者变量
        super.onCreate(savedInstanceState);

        this.onInitView(savedInstanceState);

        if (TextUtils.isEmpty(FrameConstant.push_token)) {
            FrameConstant.push_token = ToolsUtils.getToken(getApplicationContext());
        }
        if (TextUtils.isEmpty(FrameConstant.jpush_id)) {
            FrameConstant.jpush_id = ToolsUtils.getJPushId(getApplicationContext());
        }

        checkLogin();

        this.onInitEvent();
        this.onRequestData();
    }

    @Override
    public void setContentView(final int resID) {
        if (isShowStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            // window.setFlags(
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // super.setContentView(R.layout.base_activity_ground);
            // base = (LinearLayout) findViewById(R.id.base_activity);
            // base.setPadding(0, getStatusBarHeight(), 0, 0);
            // setStatusBarColor(-1);
            // base.addView(getLayoutInflater().inflate(resID, null));
        } else {
            super.setContentView(resID);
        }

    }

    /**
     * 显示网络错误页面
     */
    protected void onNetErrorShowPage() {
        if (!canShowDlg) {
            return;
        }
        // 这里使用一个dialog来实现重试，并且只是重新加载数据，不会涉及到布局的修改
        new AlertDialog.Builder(this).setTitle("加载数据失败，请稍候重试...")
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface arg0, final int arg1) {
                    arg0.dismiss();
                    onRequestData();
                }
            }).show();
    }

    protected void onDestroy() {
        this.clearMemory();
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onStart() {
        checkLogin();
//        LogWriter.e("checkHard", Build.HARDWARE);
        super.onStart();
    }

    @Override
    protected void onRestart() {
        // 再次设置一下，防止回退后被修改
//        HttpUtil.setLastPageRefer(mRequestRefer);
        super.onRestart();
    }

    /**
     * 初始化变量
     */
    @Deprecated
    protected void onInitVariable(){

    }

    /**
     * 初始化UI
     *
     * @param savedInstanceState
     */
    @Deprecated
    protected void onInitView(final Bundle savedInstanceState){

    }

    /**
     * 初始化UI 对应的Event事件
     */
    @Deprecated
    protected void onInitEvent(){

    }

    @Deprecated
    protected void onRequestData(){

    }

    /**
     * 数据加载
     */
    @Deprecated
    protected void onLoadData(){

    }

    /**
     * 卸载数据
     */
    @Deprecated
    protected void onUnloadData(){

    }

    protected void clearMemory() {

    }

    protected void onPause() {
        this.onUnloadData();
        super.onPause();
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected void onResume() {
        super.onResume();
        final NetworkInfo netInfo = Utils.getNetworkInfo(this);
        if (netInfo == null && canShowDlg) {
            try {
                new AlertDialog.Builder(this).setTitle("网络错误")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface arg0, final int arg1) {
                            arg0.dismiss();
                            // onNetErrorShowPage();
                        }
                    }).show();
            } catch (Exception e) {
            }
        } else {
            this.onLoadData();
        }

    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    protected final int DOUBLE_CLICK_DELAY = 300;
    protected long mLastClickTime = 0;
    protected boolean isDoubleClick() {
        MLogWriter.e("BaseActivity", this.getClass().getSimpleName() + " mLastClickTime="+mLastClickTime);
        long curr = System.currentTimeMillis();
        if (curr - mLastClickTime > DOUBLE_CLICK_DELAY) {
            mLastClickTime = curr;
            return false;
        } else {
            return true;
        }
    }
    
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//    }
//
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
//        if(this.isDoubleClick())
//            return;
//        super.startActivityForResult(intent, requestCode, options);
//    }
    
    // --------------------------------新版-startActivity---------------------------------------------------------
    /************* 非Activity里执行的跳转，例如adapter里，需要把context强转为baseactivity ******/
    
    public void startActivity(Class<?> clazz) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void startActivity(Class<?> clazz, Intent intent) {
        if (intent != null) {
            intent.setClass(this, clazz);
            this.startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void startActivityForResult(final Class<?> className, final Intent intent) {
        intent.setClass(this, className);
        this.startActivityForResult(intent, 0);// 请求code默认 0
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void startActivityForResult(final Class<?> className, final int requestCode) {
        final Intent i = new Intent();
        i.setClass(this, className);
        this.startActivityForResult(i, requestCode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void startActivityForResult(Class<?> clazz, Intent intent, int requestCode) {
        if (intent != null) {
            intent.setClass(this, clazz);
            intent.putExtra("RequestCode", requestCode);
        }
        this.startActivityForResult(intent, requestCode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    // --------------------------------加入滑动返回---------------------------------------------------------

    public void setSwipeBack(boolean b) {
        this.swipeBack = b;
    }

    private void checkLogin() {
        /*// 这里进行初步的判读是否登录，只考虑状态和cookie的_mi_字段是否存在，不考虑expires设置。
        // 具体的设置在回到前台时通过接口进行判断。
        if (!UserManager.Companion.getInstance().isLogin()) {
            final AccountDetailBean obj = AccountManager.getAccountInfo();
            if (null != obj) {
                UserManager.Companion.getInstance().isLogin() = true;
                App.getInstance().userInfo = obj;
            }
        }
        // 如果登陆了同时usertoken为空，则获取一下。否则不需要重复获取
        if (UserManager.Companion.getInstance().isLogin() && TextUtils.isEmpty(FrameConstant.userToken)) {
            FrameConstant.userToken = getUserToken();
        } else {
            FrameConstant.userToken = "";
        }*/
    }

    // 给目的页面的请求使用的数据。
    private String getRequestRefer() {
        // pagelabel理论上都会有值的，这里是给下一个页面使用的。所以不需要判断pagelabel.
        if (TextUtils.isEmpty(pageClickPath)) {
            pageClickPath = "";
        }

        return String.format("%s_%s", getPageLabel(), pageClickPath);
    }

    public String setClickParams(final String firstRegion, final String firstRgionMark, final String secRegion,
        final String secRegionMark, final String thrRegion, final String thrRegionMark) {
        StringBuffer sb = new StringBuffer();
        sb.append(firstRegion).append("_");
        sb.append(firstRgionMark).append("_");
        sb.append(secRegion).append("_");
        sb.append(secRegionMark).append("_");
        sb.append(thrRegion).append("_");
        sb.append(thrRegionMark);
        return (pageClickPath = sb.toString());
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            // 点击的是输入框区域，保留点击EditText的事件
            return !(event.getX() > left) || !(event.getX() < right) || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }
}
