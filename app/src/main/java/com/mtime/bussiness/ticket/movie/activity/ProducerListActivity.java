package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.view.View;
import android.widget.ExpandableListView;

import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.adapter.MovieProducerListAdapter;
import com.mtime.bussiness.ticket.movie.bean.Company;
import com.mtime.bussiness.ticket.movie.bean.MovieProducerTotalBean;
import com.mtime.bussiness.ticket.movie.bean.MovieProducerType;
import com.mtime.bussiness.ticket.movie.widget.MovieStarsListView;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangCong on 15/10/19.
 * 制作发行
 */
public class ProducerListActivity extends BaseActivity {
    private MovieStarsListView movie_producer_listview;//复用演员列表的ExpandableListView
    private MovieProducerTotalBean fullProducers;
    private final List<MovieProducerType> types = new ArrayList<MovieProducerType>();
    private List<Company> producers;
    private List<Company> distributor;
    private List<Company> specialEffects;

    private String movieId;

    List<Map<String, String>> groups = new ArrayList<>();
    List<List<Map<String, String>>> childs = new ArrayList<>();

    public static final String KEY_MOVIE_ID = "movie_id";

    @Override
    protected void onInitEvent() {
        movie_producer_listview = findViewById(R.id.movie_actor_listview);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        movie_producer_listview.setGroupIndicator(null);
        // 设置group点击无效
        movie_producer_listview.setOnGroupClickListener((parent, v, groupPosition, id) -> true);
    }

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        movieId = getIntent().getStringExtra(KEY_MOVIE_ID);

        setPageLabel("productionReleaseList");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_movie_actor_list);
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

        RequestCallback getFullProducersCallback = new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();

                fullProducers = (MovieProducerTotalBean) o;
                producers = fullProducers.getProductionList();
                distributor = fullProducers.getDistributorList();
                specialEffects = fullProducers.getSpecialEffectsList();

                if (null == producers && null == distributor && specialEffects == null) {
                    return;
                }
                // 这样写死非常不好，浪费代码，但是接口组说现有接口结构上不能更改，so...
                if (!producers.isEmpty()) {
                    MovieProducerType a = new MovieProducerType();
                    a.setTypeName(ProducerListActivity.this.getResources().getString(R.string.str_movie_producer));
                    a.setCompanys(producers);
                    types.add(a);
                }

                if (!distributor.isEmpty()) {
                    MovieProducerType a = new MovieProducerType();
                    a.setTypeName(ProducerListActivity.this.getResources().getString(R.string.str_movie_distributor));
                    a.setCompanys(distributor);
                    types.add(a);
                }

//                if (!specialEffects.isEmpty()) {
//                    MovieProducerType a = new MovieProducerType();
//                    a.setTypeName(ProducerListActivity.this.getResources().getString(R.string.str_movie_specialEffects));
//                    a.setCompanys(specialEffects);
//                    types.add(a);
//                }


                for (int i = 0; i < types.size(); i++) {
                    final MovieProducerType type = types.get(i);
                    final Map<String, String> group = new ArrayMap<String, String>(1);
                    group.put("typeName", type.getTypeName());
                    groups.add(group);

                    final List<Map<String, String>> child = new ArrayList<Map<String, String>>();
                    final List<Company> clist = type.getCompanys();

                    for (int j = 0; j < clist.size(); j++) {
                        final Company c = clist.get(j);
                        final Map<String, String> childdata = new ArrayMap<String, String>(1);

                        childdata.put("cName", c.getName());
                        child.add(childdata);
                    }
                    childs.add(child);
                }

                movie_producer_listview.setDivider(null);
                movie_producer_listview.setHeaderView(ProducerListActivity.this.getLayoutInflater().inflate(
                        R.layout.moviestars_group_header, movie_producer_listview, false));

                final MovieProducerListAdapter adapter = new MovieProducerListAdapter(ProducerListActivity.this, types,
                        movie_producer_listview, groups, R.layout.v2_moviestars_group, new String[]{"typeName"},
                        new int[]{R.id.groupto}, childs, R.layout.producer_child, new String[]{"cName"},
                        new int[]{R.id.company_name});

                movie_producer_listview.setAdapter(adapter);
                final int groupCount = movie_producer_listview.getCount();
                for (int i = 0; i < groupCount; i++) {
                    movie_producer_listview.expandGroup(i);
                }

                //TODO 等待下一个Activity有了之后进行修改
                movie_producer_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                        final Company c = types.get(groupPosition).getCompanys().get(childPosition);
                        JumpUtil.startCompanyDetailActivity(ProducerListActivity.this, String.valueOf(c.getId()), c.getName());
                        return false;
                    }
                });
            }
        };

        // Movie/CompanyList.api?MovieId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("movieId", movieId);
        HttpUtil.get(ConstantUrl.GET_FULL_PRODUCERS, param, MovieProducerTotalBean.class, getFullProducersCallback, 180000);
    }

    public static void launch(Context context, String refer, String movieId) {
        Intent launcher = new Intent(context, ProducerListActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
