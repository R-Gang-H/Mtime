package com.mtime.bussiness.ticket.movie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.MovieBehindBean;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangCong on 15/10/16.
 * 幕后制作
 */
public class MovieBehindActivity extends BaseActivity {
    private String mMovieId;
    private TextView mBehindTextView;

    private RequestCallback mMovieBehindCallBack;

    public static final String KEY_MOVIE_ID = "movie_id";

    @Override
    protected void onInitEvent() {
        mMovieBehindCallBack = new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
            }

            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();

                final MovieBehindBean mbBean = (MovieBehindBean) o;

                String Detail = mbBean.getText();
                mBehindTextView.setText(Html.fromHtml(Detail));
                mBehindTextView.setMovementMethod(LinkMovementMethod.getInstance());// 能够跳转html的链接
            }
        };
    }

    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);

        // Movie/BehindMake.api?MovieId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("MovieId", mMovieId);
        HttpUtil.get(ConstantUrl.GET_MOVIE_BEHIND, param, MovieBehindBean.class, mMovieBehindCallBack, 3600);
    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_behind_the_scenes);
        View navBar = findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "幕后制作", null);

        mBehindTextView = findViewById(R.id.behind_detail);
    }

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        mMovieId = getIntent().getStringExtra(KEY_MOVIE_ID);
        setPageLabel("behindStory");
    }

    public static void launch(BaseActivity context, String refer, String movieId) {
        Intent launcher = new Intent(context, MovieBehindActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
