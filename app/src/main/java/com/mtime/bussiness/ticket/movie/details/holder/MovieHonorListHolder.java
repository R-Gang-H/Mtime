package com.mtime.bussiness.ticket.movie.details.holder;

import java.util.List;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieHonorAdapter;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.frame.App;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/6/17
 * @desc 电影获奖页Holder
 */
public class MovieHonorListHolder extends ContentHolder<MovieDetailsBasic.Award> implements OnItemChildClickListener {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_movie_honor_list_recyclerview)
    RecyclerView mRecyclerView;
    // header
    private Group mAwardGroup;
    private TextView mAwardCountTv;
    private TextView mAwardUnitTv;
    private Group mNominateGroup;
    private TextView mNominateCountTv;

    private Unbinder mUnbinder;
    private MovieHonorAdapter mAdapter;

    public MovieHonorListHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_honor_list);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        View headerView = View.inflate(mContext, R.layout.layout_movie_honor_list_header, null);
        mAwardGroup = headerView.findViewById(R.id.layout_movie_honor_list_header_award_group);
        mAwardCountTv = headerView.findViewById(R.id.layout_movie_honor_list_header_award_count_tv);
        mAwardUnitTv = headerView.findViewById(R.id.layout_movie_honor_list_header_award_unit_tv);
        mNominateGroup = headerView.findViewById(R.id.layout_movie_honor_list_header_nominate_group);
        mNominateCountTv = headerView.findViewById(R.id.layout_movie_honor_list_header_nominate_count_tv);

        mAdapter = new MovieHonorAdapter(null);
        mAdapter.addHeaderView(headerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mAdapter.setOnItemChildClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        if (mData == null || CollectionUtils.isEmpty(mData.awardList) || CollectionUtils.isEmpty(mData.festivals)
                || mAdapter == null) {
            return;
        }

        // 显示获奖提名总数
        showHeaderCount();

        mAdapter.setAwardList(mData.awardList);
        mAdapter.addData(mData.festivals);
    }

    // 显示获奖提名总数
    private void showHeaderCount() {
        if(mData.totalWinAward > 0) {
            mAwardCountTv.setText(String.valueOf(mData.totalWinAward));
        } else {
            mAwardGroup.setVisibility(View.GONE);
        }
        if(mData.totalNominateAward > 0) {
            mNominateCountTv.setText(String.valueOf(mData.totalNominateAward));
        } else {
            mNominateGroup.setVisibility(View.GONE);
            mAwardUnitTv.setText(getResource().getString(R.string.movie_honor_list_award_count_unit));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_movie_sub_page_back_iv:
                onHolderEvent(App.MOVIE_SUB_PAGE_EVENT_CODE_BACK, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if(view.getId() == R.id.item_movie_honor_festival_rl) {
            List<MovieDetailsBasic.Festivals> data = adapter.getData();
            MovieDetailsBasic.Festivals festival = data.get(position);
            festival.isExpand = !festival.isExpand;
            View viewParent = (View) view.getParent();
            if(viewParent != null) {
                View expandView = viewParent.findViewById(R.id.item_movie_honor_expand_rl);
                expandView.setVisibility(festival.isExpand ? View.VISIBLE : View.GONE);
                ImageView arrowIv = view.findViewById(R.id.item_movie_honor_festival_arrow_iv);
                arrowIv.setImageResource(festival.isExpand ? R.drawable.movie_honor_up : R.drawable.movie_honor_down);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    // 设置标题
    public void setTitle(String title) {
        if(mTitleTv != null) {
            mTitleTv.setText(TextUtils.isEmpty(title) ? getResource().getString(R.string.movie_honor_list_title) : title);
        }
    }

}
