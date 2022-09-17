package com.mtime.frame;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.mtime.frame.BaseActivity;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.frame.App;
import com.kk.taurus.uiframe.i.HolderData;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.util.RegGiftDlg;
@Deprecated
public abstract class BaseFragment<T extends HolderData, H extends ContentHolder<T>> extends BaseFrameUIFragment<T,H> {

    public AppCompatActivity context;
    private View contentView;
    public boolean isInitFinish = false;
    /**
     * 标志位，标志已经初始化完成
     */
    protected boolean isPrepared;
    
    @Deprecated
    protected void onInitVariable() {
        
    }
    
    @Deprecated
    protected void onInitView(final Bundle savedInstanceState) {
        
    }
    
    @Deprecated
    protected void onInitEvent() {
        
    }
    
    @Deprecated
    protected void onRequestData() {
        
    }
    
    @Deprecated
    protected View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (AppCompatActivity)this.getActivity();
        isInitFinish = false;
    }

//    protected void setLastPageRefer(String refer){
//        mFragmentRefer = refer;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = this.setContentView(inflater, container, savedInstanceState);
        }
        //兼容新框架
        if(contentView == null) {
            contentView = super.onCreateView(inflater, container, savedInstanceState);
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        isPrepared = true;
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.gc();

        if (!isInitFinish) {
            this.onInitVariable();
            this.onInitView(savedInstanceState);
            this.onInitEvent();
            this.onRequestData();
        }
        isInitFinish = true;

        //弹注册新手礼包图片
        showRegisterNewGiftDlg();
    }

    //弹注册新手礼包图片
    private void showRegisterNewGiftDlg() {
        // 注册成功后弹出的礼包图片; 购票购商品过程中的注册，不弹，但是会记录下来，主标签页要弹1次，退出app再进入不再弹
        if (!TextUtils.isEmpty(App.getInstance().REGISTER_DLG_NEWGIFT_IMG)) {
            RegGiftDlg giftDlg = new RegGiftDlg(context, App.getInstance().REGISTER_DLG_NEWGIFT_IMG);
            giftDlg.show();

            App.getInstance().REGISTER_DLG_NEWGIFT_IMG = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mIsSubmit) {
//            mStartTime = System.currentTimeMillis();
//            // open 上报
//            StatisticPageBean bean =context.assemble(StatisticConstant.OPEN, "", "", "", "", "", null);
//            bean.pageName = mFragmentPageLabel;
//            bean.refer =mFragmentRefer;
//            StatisticManager.getInstance().submitOpen(bean);
//        }
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mIsSubmit) {
//            //页面在进入不可见时就上传
//            Map<String, String> params = new HashMap<>();
//            params.put(StatisticConstant.DURATION, String.valueOf(System.currentTimeMillis() - mStartTime));
//            StatisticPageBean timingBean=context.assemble(StatisticConstant.TIMING, "", "", "", "", "", params);
//            timingBean.pageName = mFragmentPageLabel;
//            timingBean.refer =mFragmentRefer;
//            StatisticManager.getInstance().submitTiming(timingBean);
//            // close 上报
//            StatisticPageBean closeBean = context.assemble(StatisticConstant.CLOSE, "", "", "", "", "", null);
//            closeBean.pageName = mFragmentPageLabel;
//            closeBean.refer =mFragmentRefer;
//            StatisticManager.getInstance().submitClose(closeBean);
//        }
//    }

//    @Override
//    public void destroy() {
//        super.destroy();
//    }

}
