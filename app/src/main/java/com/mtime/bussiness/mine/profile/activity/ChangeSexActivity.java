package com.mtime.bussiness.mine.profile.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.collection.ArrayMap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kotlin.android.user.UserManager;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.profile.bean.ChangeSexBean;
import com.mtime.util.HttpUtil;
import com.mtime.network.RequestCallback;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.bussiness.mine.widget.TitleOfLoginView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.UIUtil;

import java.util.Map;

/**
 * 我的--个人资料--修改性别
 * 
 */
public class ChangeSexActivity extends BaseActivity implements ITitleViewLActListener {
    
    private int              currentSex  = 3; // default to male
    private int              originalSex = 3;
    
    private LinearLayout     ll_male;
    private LinearLayout     ll_female;
    private TextView         tv_male;
    private TextView         tv_female;
    
    private RequestCallback save_callback;
    
    private TitleOfLoginView navigationbar;
    
    @Override
    protected void onInitVariable() {
        setPageLabel("changeSex");
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_profile_change_sex);
        View navbar = this.findViewById(R.id.navigationbar);
        navigationbar = new TitleOfLoginView(this, navbar, StructType.TYPE_LOGIN_SHOW_TITLE_SKIP, getResources()
                .getString(R.string.str_edit_sex), this);
        navigationbar.setRightText("保存",false);
        
        ll_male = findViewById(R.id.ll_male);
        ll_female = findViewById(R.id.ll_female);
        tv_male = findViewById(R.id.tv_male);
        tv_female = findViewById(R.id.tv_female);

        int userSex = UserManager.Companion.getInstance().getUserSex();
        // 和修改昵称一样
        if (userSex == App.getInstance().female) {
            originalSex = App.getInstance().female;
            updateSelectItemStatus(tv_female, R.drawable.oval_selected_height_50);
            updateSelectItemStatus(tv_male, R.drawable.oval_normal_height_50);
        } else if (userSex == App.getInstance().male) {
            originalSex = App.getInstance().male;
            updateSelectItemStatus(tv_male, R.drawable.oval_selected_height_50);
            updateSelectItemStatus(tv_female, R.drawable.oval_normal_height_50);
        } else {
            originalSex = 3;
            updateSelectItemStatus(tv_female, R.drawable.oval_normal_height_50);
            updateSelectItemStatus(tv_male, R.drawable.oval_normal_height_50);
        }
    }
    
    @Override
    protected void onInitEvent() {
        save_callback = new RequestCallback() {
            @Override
            public void onSuccess(final Object result) {
                UIUtil.dismissLoadingDialog();
                
                final ChangeSexBean r = (ChangeSexBean) result;
                if (r.getSuccess()) {
                    originalSex = currentSex;
                    navigationbar.setRightText("保存", false);

                    UserManager.Companion.getInstance().setUserSex(currentSex);

                    MToastUtils.showShortToast("用户性别修改成功");

                    finish();
                }
                else {
                    MToastUtils.showShortToast(r.getErrorMessage());
                }
            }
            
            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("修改性别失败:" + e.getLocalizedMessage());
            }
        };

        OnClickListener ll_male_click = new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSex = App.getInstance().male;
                navigationbar.setRightText("保存", currentSex != originalSex);
                
                updateSelectItemStatus(tv_male, R.drawable.oval_selected_height_50);
                updateSelectItemStatus(tv_female, R.drawable.oval_normal_height_50);
            }
        };
        ll_male.setOnClickListener(ll_male_click);

        OnClickListener ll_female_click = new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSex = App.getInstance().female;
                navigationbar.setRightText("保存", currentSex != originalSex);

                updateSelectItemStatus(tv_female, R.drawable.oval_selected_height_50);
                updateSelectItemStatus(tv_male, R.drawable.oval_normal_height_50);
            }
        };
        ll_female.setOnClickListener(ll_female_click);
    }
    
    private void updateSelectItemStatus(TextView tv, int id) {
        Drawable drawable = ContextCompat.getDrawable(this, id);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv.setCompoundDrawables(drawable, null, null, null);
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
    
    @Override
    public void onEvent(ActionType type, String content) {
        switch (type) {
            case TYPE_SKIP:
                UIUtil.showLoadingDialog(ChangeSexActivity.this);
                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                parameterList.put("sex", String.valueOf(currentSex));
                HttpUtil.post(ConstantUrl.CHANGE_SEX, parameterList, ChangeSexBean.class, save_callback);
                break;
            
            default:
                this.finish();
                break;
        }
    }
}
