package com.mtime.bussiness.ticket.cinema.holder;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-21
 */
public class ActivitiesInstructionsHolder extends ContentHolder<String> {

    public ActivitiesInstructionsHolder(Context context) {
        super(context);
    }

    @BindView(R.id.act_activities_instructions_content)
    TextView mContent;

    private Unbinder mUnBinder;


    @Override
    public void onCreate() {
        setContentView(R.layout.act_activities_instructions);
        mUnBinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    public void onDataChanged(String detail) {
        super.onDataChanged(detail);
        detail = detail.replace("\n", "<br/><br/>");
        mContent.setText(Html.fromHtml(detail));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }
}
