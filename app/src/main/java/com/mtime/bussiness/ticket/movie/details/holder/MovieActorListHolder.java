package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.bean.MovieStarsTotalBean;
import com.mtime.bussiness.ticket.movie.bean.MovieStarsType;
import com.mtime.bussiness.ticket.movie.bean.Person;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieActorAdapter;
import com.mtime.bussiness.ticket.movie.details.widget.MovieActorFloatingItemDecoration;
import com.mtime.frame.App;
import com.mtime.util.JumpUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/5/21
 * @desc 影片演职员页Holder
 */
public class MovieActorListHolder extends ContentHolder<MovieStarsTotalBean> implements OnItemClickListener {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_movie_actor_list_new_recyclerview)
    RecyclerView mRecyclerView;

    private Unbinder mUnbinder;
    private MovieActorAdapter mAdapter;
    private MovieActorFloatingItemDecoration mFloatingItemDecoration;

    public MovieActorListHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_actor_list_new);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mTitleTv.setText(getResource().getString(R.string.st_movie_actor));

        mAdapter = new MovieActorAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        // 列表分组
        mFloatingItemDecoration = new MovieActorFloatingItemDecoration(mContext);
        mFloatingItemDecoration.setTitleHeight(MScreenUtils.dp2px(30));
        mFloatingItemDecoration.setTitleViewmTitleViewPaddingLeft(MScreenUtils.dp2px(15));
        mFloatingItemDecoration.setTtitleBackground(ContextCompat.getColor(mContext, R.color.color_F2F3F6_alpha_92));
        mFloatingItemDecoration.setTitleTextColor(ContextCompat.getColor(mContext, R.color.color_8798AF));
        mFloatingItemDecoration.setTitleTextSize(MScreenUtils.sp2px( 12));
        mRecyclerView.addItemDecoration(mFloatingItemDecoration);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        List<MovieStarsType> types = mData.getMovieStarsTypes();
        if (CollectionUtils.isEmpty(types)) {
            return;
        }

        // 数据分组
        MovieStarsType type;
        int titleKey = 0;
        for (int i = 0, size = types.size(); i < size; i++) {
            type = types.get(i);
            if(type!= null && CollectionUtils.isNotEmpty(type.getPersons())) {
                titleKey = mAdapter.getItemCount();
                if(null != mFloatingItemDecoration) {
                    mFloatingItemDecoration.appendTitles(titleKey, String.format("%s %s", type.getTypeName(), type.getTypeNameEn()));
                }
                int personSize = type.getPersons().size();
                type.getPersons().get(personSize - 1).setGroupEnd(true);
                mAdapter.addData(type.getPersons());
            }
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // 无埋点
        List<Person> personList = adapter.getData();
        Person person = personList.get(position);
        JumpUtil.startActorDetail(mContext, "", String.valueOf(person.getId()),
                TextUtils.isEmpty(person.getName()) ? person.getNameEn() : person.getName());
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

}
