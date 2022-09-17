package com.mtime.bussiness.ticket.cinema.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.holder.ActivitiesInstructionsHolder;
import com.mtime.frame.BaseFrameUIActivity;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-21
 * <p>
 * 活动说明页
 */
public class ActivitiesInstructionsActivity extends BaseFrameUIActivity<String, ActivitiesInstructionsHolder> {

    private static final String KEY_OF_DETAIL = "bundle_key_of_detail";

    @Override
    public ContentHolder onBindContentHolder() {
        return new ActivitiesInstructionsHolder(this);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        // activity content view 加载完成
        setTitle(R.string.activities_instruction_txt);
        String detail = getIntent().getStringExtra(KEY_OF_DETAIL);
        setData(detail);
    }

    public static void launch(Context context, String detail) {
        Intent intent = new Intent(context, ActivitiesInstructionsActivity.class);
        intent.putExtra(KEY_OF_DETAIL, detail);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

}
