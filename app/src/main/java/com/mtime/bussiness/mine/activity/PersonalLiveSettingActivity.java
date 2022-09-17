package com.mtime.bussiness.mine.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.applink.ApplinkConstants;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.frame.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalLiveSettingActivity extends BaseActivity {
    @BindView(R.id.act_personal_live_setting_ed1)
    EditText actEd1Params;
    @BindView(R.id.act_personal_live_setting_ed2)
    EditText actEd2Params;
    @BindView(R.id.back)
    RelativeLayout back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.act_personal_live_jump_bt1)
    Button mJumpBt1;
    @BindView(R.id.act_personal_live_jump_bt2)
    Button mJumpBt2;
    private String mParams1;

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, PersonalLiveSettingActivity.class);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.act_personal_live_setting);
        ButterKnife.bind(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalLiveSettingActivity.this.finish();
            }
        });
        title.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
    }

    /**
     * 加入
     */
    public void jump(View v) {
        switch (v.getId()) {
            case R.id.act_personal_live_jump_bt1:
//                mParams1 = ApplinkConstants.PAGE_TYPE_guessMovie;
                appendParam(actEd1Params.getEditableText().toString());
                break;
            case R.id.act_personal_live_jump_bt2:
//                mParams1 = ApplinkConstants.PAGE_TYPE_liveHQHome;
                appendParam(actEd2Params.getEditableText().toString());
                break;
            default:
                break;
        }
    }

    private void appendParam(String id) {
        if (TextUtils.isEmpty(id)) {
            MToastUtils.showShortToast("id不能为空");
            return;
        }
        String params2 = "\"liveId\":\"".concat(id).concat("\"");
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(" \"handleType\" : \"jumpPage\",").append(" \"pageType\" : \"" + mParams1 + "\"");
        if (!TextUtils.isEmpty(params2)) {
            sb.append(",").append(params2);
        }
        sb.append("}");
        ApplinkManager.jump(this, sb.toString(), null);
    }
}
