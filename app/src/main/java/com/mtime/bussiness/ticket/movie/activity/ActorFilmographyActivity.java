package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.google.gson.reflect.TypeToken;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.adapter.ActorFilmographyAdapter;
import com.mtime.bussiness.ticket.movie.adapter.ActorFilmographyYearListAdapter;
import com.mtime.bussiness.ticket.movie.api.PersonApi;
import com.mtime.bussiness.ticket.movie.bean.FilmographyBean;
import com.mtime.bussiness.ticket.movie.bean.FilmographyListBean;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 影人-作品年表
 * 
 * @author ye
 */
public class ActorFilmographyActivity extends BaseActivity implements OnClickListener {

    enum FilterType {
        TYPE_UNKNOWN, TYPE_DESC_BY_YEAR, TYPE_ASC_BY_YEAR, TYPE_HOT,
    }
    
    // 影人Id
    private String                          actorId;
    private String                          actorName;
    private int                             actorMoviesCount;             // 作品数
    private ActorFilmographyAdapter         adapter;
    
    private int                             queryIndex;                   // 请求分页数据的索引
    private int                             queryYearIndex;                   // 请求分页数据的索引

    // 影人代表作-排序类型
    private final int                       SORT_TYPE_YEARS_DESC   = 2;   // 降序（年代从近到远）
    private final int                       SORT_TYPE_YEARS_ASC    = 3;   // 升序（年代从远到近）-（默认？）
    private final int                       NORMAL_DESC            = 4;   // 默认排序（按评分人数倒序排列，人数相同按评分倒序）
    private int                             sortType               = 0;   // 当前列表数据的排序类型-默认
    private int                             subSortType            = 0;
    
    // new views
    private TitleOfNormalView               title;
    private TextView                        hotView;
    private TextView                        yearView;
    private ImageView                       orderView;
    
    private FilterType                      filterType;
    private FilterType                      yearType;
    // FOR HOT PLAYING.......
    private IRecyclerView moviesList;
    private LoadMoreFooterView movieLoadMoreFooterView;
    private IRecyclerView moviesYearList;
    private LoadMoreFooterView yearLoadMoreFooterView;
    private ActorFilmographyYearListAdapter expandAdapter;
    private PersonApi mPersonApi;

    private static final String KEY_MOVIE_PERSOM_ID = "movie_person_id"; // 影人（导演/演员）id
    private static final String KEY_MOVIE_PERSOM_NAME = "movie_person_name"; // 影人（导演/演员）name
    private static final String KEY_MOVIE_PERSOM_WORK_COUNT = "movie_person_work_count"; // 影人（导演/演员）作品数

    @Override
    protected boolean  enableSliding(){
        return true;
    }

    @Override
    protected void onInitVariable() {
        actorId = getIntent().getStringExtra(KEY_MOVIE_PERSOM_ID);
        actorName = getIntent().getStringExtra(KEY_MOVIE_PERSOM_NAME);
        actorMoviesCount = getIntent().getIntExtra(KEY_MOVIE_PERSOM_WORK_COUNT, 0);
        
        this.setSwipeBack(true);
        
        this.filterType = FilterType.TYPE_HOT;
        this.yearType = FilterType.TYPE_UNKNOWN;
        
        this.queryIndex = 1;
        this.queryYearIndex = 1;
        this.sortType = NORMAL_DESC; // 记录当前列表数据的排序类型
        setPageLabel("starMovies");
        mPersonApi = new PersonApi();
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_actor_filmography);
        
        View root = this.findViewById(R.id.graph_title);
        this.title = new TitleOfNormalView(this, root, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, actorName,
                new ITitleViewLActListener() {
                    
                    @Override
                    public void onEvent(ActionType type, String content) {
                        finish();
                    }
                });
        title.setTopTitleSize(17);
        title.setBottomTitleSize(12);
        this.title.setTitles(actorName, String.format("%d部作品", actorMoviesCount));
        
        View viewHot = this.findViewById(R.id.hot_view);
        viewHot.setOnClickListener(this);
        this.hotView = this.findViewById(R.id.hot_label);
        
        View viewAssec = this.findViewById(R.id.order_year_des_view);
        viewAssec.setOnClickListener(this);
        this.yearView = this.findViewById(R.id.year_des);
        
        this.orderView = this.findViewById(R.id.order_icon);
        
        this.moviesList = this.findViewById(R.id.movies_list);
        this.moviesList.setLayoutManager(new LinearLayoutManager(this));
        this.movieLoadMoreFooterView = (LoadMoreFooterView) this.moviesList.getLoadMoreFooterView();

        this.moviesList.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(View loadMoreView) {
                if (movieLoadMoreFooterView.canLoadMore()) {
                    movieLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    requestFilmographies(++queryIndex, sortType);
                }
            }
        });

        this.adapter = new ActorFilmographyAdapter(this, null);
        this.moviesList.setIAdapter(adapter);
        this.adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String movieId = String.valueOf(adapter.getFilmographyList().get(position).getId());
                JumpUtil.startMovieInfoActivity(ActorFilmographyActivity.this, assemble().toString(), movieId, 0);
            }
        });
        
        this.moviesYearList = this.findViewById(R.id.movies_year_list);
        this.moviesYearList.setLayoutManager(new LinearLayoutManager(this));
        yearLoadMoreFooterView = (LoadMoreFooterView) moviesYearList.getLoadMoreFooterView();
        moviesYearList.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(View loadMoreView) {
                if (yearLoadMoreFooterView.canLoadMore()) {
                    yearLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    requestFilmographies(++queryYearIndex, sortType);
                }
            }
        });
        expandAdapter = new ActorFilmographyYearListAdapter(this, null);
        this.moviesYearList.setIAdapter(expandAdapter);
        expandAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String movieId = String.valueOf(expandAdapter.getFilmographyList().get(position).getId());
                JumpUtil.startMovieInfoActivity(ActorFilmographyActivity.this, assemble().toString(), movieId, 0);
            }
        });
        
        showView(filterType, yearType);
    }
    
    @Override
    protected void onInitEvent() {

    }
    
    @Override
    protected void onLoadData() {
        
    }
    
    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);
        
        requestFilmographies(queryIndex, sortType); // 请求第一页数据，排序方式-默认
    }
    
    @Override
    protected void onUnloadData() {
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPersonApi != null) {
            mPersonApi = null;
        }
    }
    
    // 分页加载影人代表作数据
    private void requestFilmographies(final int pageIndex, final int sortType) {
        if (mPersonApi == null) {
            return;
        }
        // 获取影人作品列表
        mPersonApi.getPersonMovies(actorId, pageIndex, sortType, new NetworkManager.NetworkListener<FilmographyListBean>() {
            @Override
            public void onSuccess(FilmographyListBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if(result == null) {
                    return;
                }
                List<FilmographyBean> filmOgraphyList = result.getList();
                if (FilterType.TYPE_HOT == filterType) {
                    if (1 == queryIndex) {
                        adapter.setFilmographyList(filmOgraphyList);
                    } else {
                        adapter.addFilmographyList(filmOgraphyList);
                    }
                    if (adapter.getItemCount() < actorMoviesCount) {
                        movieLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    } else {
                        movieLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    }
                } else {
                    yearLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    if (null == filmOgraphyList || filmOgraphyList.size() == 0) {
                        MToastUtils.showShortToast("已经加载完全部数据");
                        yearLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                        return;
                    }
                    if (1 == queryYearIndex) {
                        expandAdapter.setFilmographyList(filmOgraphyList);
                    } else {
                        expandAdapter.addFilmographyList(filmOgraphyList);
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<FilmographyListBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (FilterType.TYPE_HOT == filterType) {
                    movieLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                } else {
                    yearLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                }
            }
        });

//        final Type type = new TypeToken<List<FilmographyBean>>() {
//        }.getType();
//        // Person/Movie.api?personId={0}&pageIndex={1}&orderId={2}
//        Map<String, String> param = new HashMap<String, String>(3);
//        param.put("personId", actorId);
//        param.put("pageIndex", String.valueOf(pageIndex));
//        param.put("orderId", String.valueOf(sortType));
//        HttpUtil.get(ConstantUrl.GET_FILMOGRAPHIES, param, null, mFilmographiesCallback, 180000, type, 0);
    }
    
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.hot_view:
                filterType = FilterType.TYPE_HOT;
                sortType = NORMAL_DESC; // 记录当前列表数据的排序类型
                // show hot list and hide the expand list
                moviesYearList.setVisibility(View.GONE);
                moviesList.setVisibility(View.VISIBLE);
                break;
            case R.id.order_year_des_view:
                // default;
                moviesYearList.setVisibility(View.VISIBLE);
                moviesList.setVisibility(View.GONE);
                if (filterType == FilterType.TYPE_HOT) {
                    // hide the hot play and show the expand list
                    if (FilterType.TYPE_UNKNOWN == yearType) {
                        yearType = FilterType.TYPE_DESC_BY_YEAR;
                        sortType = SORT_TYPE_YEARS_DESC;
                        subSortType = SORT_TYPE_YEARS_DESC;
                        filterType = FilterType.TYPE_DESC_BY_YEAR;
                    }
                    else {
                        filterType = yearType;
                        sortType = subSortType;
                    }
                    if (expandAdapter.getFilmographyList() == null || expandAdapter.getFilmographyList().size() == 0) {
                        requestFilmographies(queryYearIndex, sortType);
                    }
                }
                else {
                    if (FilterType.TYPE_DESC_BY_YEAR == yearType) {
                        yearType = FilterType.TYPE_ASC_BY_YEAR;
                        sortType = SORT_TYPE_YEARS_ASC;
                        filterType = FilterType.TYPE_ASC_BY_YEAR;
                        subSortType = SORT_TYPE_YEARS_DESC;
                    }
                    else {
                        yearType = FilterType.TYPE_DESC_BY_YEAR;
                        sortType = SORT_TYPE_YEARS_DESC;
                        filterType = FilterType.TYPE_DESC_BY_YEAR;
                        subSortType = SORT_TYPE_YEARS_DESC;
                    }
                    queryYearIndex = 1;
                    requestFilmographies(queryYearIndex, sortType);

                }
                
                break;
            default:
                break;
        }
        
        showView(filterType, yearType);
        
        // update list view
//        this.queryIndex = 1;
//        this.isQueryOver = false;
//
//        UIUtil.showLoadingDialog(this);
//
//        requestFilmographies(queryIndex, sortType);
    }
    
    private void restoreAll() {
        this.hotView.setTextColor(getResources().getColor(R.color.color_999999));
        this.yearView.setTextColor(getResources().getColor(R.color.color_999999));
    }
    
    private void showView(FilterType type, FilterType subType) {
        this.restoreAll();
        
        if (FilterType.TYPE_HOT == type) {
            this.hotView.setTextColor(getResources().getColor(R.color.white));
        }
        else {
            this.yearView.setTextColor(getResources().getColor(R.color.white));
            switch (subType) {
                case TYPE_ASC_BY_YEAR:
                    this.orderView.setImageResource(R.drawable.price_sort_up);
                    break;
                case TYPE_DESC_BY_YEAR:
                    this.orderView.setImageResource(R.drawable.price_sort_down);
                    break;
                default:
                    break;
            }
        }
        
    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     * @param personId
     */
    public static void launch(Context context, String refer, String personId, String personName, int workCount) {
        Intent launcher = new Intent(context, ActorFilmographyActivity.class);
        launcher.putExtra(KEY_MOVIE_PERSOM_ID, personId);
        launcher.putExtra(KEY_MOVIE_PERSOM_NAME, personName);
        launcher.putExtra(KEY_MOVIE_PERSOM_WORK_COUNT, workCount);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}