package com.mtime.bussiness.mine.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.mine.profile.bean.ChangeSexBean;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;

import java.util.Map;

/**
 * Created by vivian.wei on 15/8/20.
 * 我的Tab_性别选择弹框
 */
public class SexDlg extends Dialog {

    private RequestCallback save_callback;

    public SexDlg( Context context ) {
        super(context, R.style.upomp_bypay_MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_sex);

        ImageView maleIcon = findViewById(R.id.dlg_sex_male_icon);
        ImageView femaleIcon = findViewById(R.id.dlg_sex_female_icon);
        TextView nochoose = findViewById(R.id.dlg_sex_nochoose);
        View line = findViewById(R.id.dlg_sex_line);

        //是否强制填写性别
        if (LoadManager.getIsEditGender()) {
            line.setVisibility(View.GONE);
            nochoose.setVisibility(View.GONE);
        }

        //点击事件
        sexClickListener sexlistener = new sexClickListener();
        maleIcon.setOnClickListener(sexlistener);
        femaleIcon.setOnClickListener(sexlistener);
        if (!LoadManager.getIsEditGender()) {
            nochoose.setOnClickListener(sexlistener);
        }

        //修改性别回调
        save_callback = new RequestCallback() {

            @Override
            public void onSuccess(final Object result) {

                final ChangeSexBean r = (ChangeSexBean) result;
                if (r.getSuccess()) {
                    UserManager.Companion.getInstance().setUserSex(App.getInstance().female);
                }
            }

            @Override
            public void onFail(final Exception e) {

            }

        };

    }

    //点击性别
    private class sexClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.dlg_sex_male_icon:
                case R.id.dlg_sex_nochoose:
                    setSex(App.getInstance().male);
                    break;
                case R.id.dlg_sex_female_icon:
                    setSex(App.getInstance().female);
                    break;
                default:
                    break;
            }

        }

        private void setSex(int sex) {

            if(sex == App.getInstance().female) {
                //修改用户性别为女
                Map<String, String> parameterList = new ArrayMap<String, String>(1);
                parameterList.put("sex", String.valueOf(sex));
                HttpUtil.post(ConstantUrl.CHANGE_SEX, parameterList, ChangeSexBean.class, save_callback);
            }

            //关闭弹框
            SexDlg.this.dismiss();

        }

    }
}
