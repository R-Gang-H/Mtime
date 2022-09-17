package com.mtime.bussiness.mine.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.mine.adapter.FeedBackListAdapter;
import com.mtime.bussiness.mine.bean.FeedBackMainBean;
import com.mtime.bussiness.mine.bean.FeedbackListBean;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.List;

public class FeedBackListActivity extends BaseActivity
{
    private ListView feedBackListView;
    private List<FeedbackListBean> feedbackListBeans;
    @Override
    protected void onInitEvent()
    {
    }

    @Override
    protected void onInitVariable()
    {
        App.getInstance().getPrefsManager().putLong("feedback_time", MTimeUtils.getLastDiffServerTime());
        FeedBackMainBean feedBackMainBean=(FeedBackMainBean) getIntent().getSerializableExtra(App.getInstance().KEY_FEEDBACK_MAIN);
        feedbackListBeans=feedBackMainBean.getMessages();
        setPageLabel("nativeFeedbackList");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_feedback_list);    
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE,"我的留言记录", null);
        feedBackListView= findViewById(R.id.feedback_list);
        feedBackListView.setDivider(null);
        if (feedbackListBeans!=null)
        {
            FeedBackListAdapter feedBackListAdapter=new FeedBackListAdapter(FeedBackListActivity.this, feedbackListBeans);
            feedBackListView.setAdapter(feedBackListAdapter);
        }
    }

    @Override
    protected void onLoadData()
    {
        
    }

    @Override
    protected void onRequestData()
    {
        
        
    }

    @Override
    protected void onUnloadData()
    {
        
        
    }

}
