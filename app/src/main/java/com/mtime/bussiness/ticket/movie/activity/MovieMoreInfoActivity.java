package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.adapter.MovieMoreInfoWebAdapter;
import com.mtime.bussiness.ticket.movie.bean.MovieMoreInfoBean;
import com.mtime.common.utils.TextUtil;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.ScrollListView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZhangCong on 15/10/20.
 * 更多资料
 */
public class MovieMoreInfoActivity extends BaseActivity {
    private MovieMoreInfoBean moreInfoBean;

    private RequestCallback getMoreInfoCallback;
    private String movieId;
    private TextView length, costs, date, language, nameTitle, lengthTitle, costsTitle, dateTitle, languageTitle, webTitle;
    private ScrollListView web; // 解决ScrollView与ListView滑动冲突
    private LinearLayout namesContainer;

    public static final String KEY_MOVIE_ID = "movie_id";

    @Override
    protected void onInitEvent() {
        getMoreInfoCallback = new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();

                moreInfoBean = (MovieMoreInfoBean) o;

                if (moreInfoBean.getName() != null && !moreInfoBean.getName().isEmpty()) {
                    if (CollectionUtils.isNotEmpty(moreInfoBean.getName())) {
                        for (int i = 0; i < moreInfoBean.getName().size(); i++) {
                            View child = getLayoutInflater().inflate(R.layout.producer_child,
                                    namesContainer, false);

                            TextView nameTv = child.findViewById(R.id.company_name);
                            nameTv.setText(moreInfoBean.getName().get(i));
                            child.findViewById(R.id.right_arrow).setVisibility(View.GONE);
                            if (i == moreInfoBean.getName().size() - 1) {
                                child.findViewById(R.id.gray_line).setVisibility(View.GONE);
                            }
                            namesContainer.addView(child);
                        }

                        namesContainer.setVisibility(View.VISIBLE);
                        nameTitle.setVisibility(View.VISIBLE);
                    }
                }

                if (!TextUtils.isEmpty(moreInfoBean.getLength())) {
                    length.setText(moreInfoBean.getLength());
                    length.setVisibility(View.VISIBLE);
                    lengthTitle.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(moreInfoBean.getCost())) {
                    costs.setText(Html.fromHtml(moreInfoBean.getCost()));
                    costs.setVisibility(View.VISIBLE);
                    costsTitle.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(moreInfoBean.getCaptureDate())) {
                    date.setText(Html.fromHtml(moreInfoBean.getCaptureDate()));
                    date.setVisibility(View.VISIBLE);
                    dateTitle.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(moreInfoBean.getLanguage())) {
                    language.setText(moreInfoBean.getLanguage());
                    language.setVisibility(View.VISIBLE);
                    languageTitle.setVisibility(View.VISIBLE);
                }

                if (moreInfoBean.getOfficialWebsite().size() > 0) {
                    if (!moreInfoBean.getOfficialWebsite().get(0).getText().isEmpty()) {
                        MovieMoreInfoWebAdapter webAdapter = new MovieMoreInfoWebAdapter(MovieMoreInfoActivity.this, moreInfoBean.getOfficialWebsite());
                        web.setAdapter(webAdapter);
                        web.setVisibility(View.VISIBLE);
                        webTitle.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        web.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Uri uri = Uri.parse(moreInfoBean.getOfficialWebsite().get(i).getUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }

        );
    }

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        movieId = getIntent().getStringExtra(KEY_MOVIE_ID);

        setPageLabel("moreMovieInformation");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_more_info);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        namesContainer = findViewById(R.id.names_container);
        length = findViewById(R.id.length);
        costs = findViewById(R.id.costs);
        date = findViewById(R.id.date);
        language = findViewById(R.id.language);
        web = findViewById(R.id.web);

        web.setDividerHeight(0);

        nameTitle = findViewById(R.id.name_title);
        lengthTitle = findViewById(R.id.length_title);
        costsTitle = findViewById(R.id.costs_title);
        dateTitle = findViewById(R.id.date_title);
        languageTitle = findViewById(R.id.language_title);
        webTitle = findViewById(R.id.web_title);
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);
        // Movie/MoreDetail.api?MovieId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("movieId", movieId);
        HttpUtil.get(ConstantUrl.GET_MOVIE_MORE_INFO, param, MovieMoreInfoBean.class, getMoreInfoCallback, 3600);
    }

    public static void launch(Context context, String refer, String movieId) {
        Intent launcher = new Intent(context, MovieMoreInfoActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
