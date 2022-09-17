package com.mtime.bussiness.mine.history.holder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.mine.history.adapter.ReadHistoryAdapter;
import com.mtime.bussiness.mine.history.bean.ReadHistoryListBean;
import com.mtime.bussiness.mine.history.dao.HistoryDao;
import com.mtime.bussiness.mine.history.ReadHistoryUtil;
import com.mtime.bussiness.mine.history.widget.ReadHistoryFloatingItemDecoration;
import com.mtime.bussiness.mine.history.widget.SwipeLayout;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.mine.StatisticMine;
import com.mtime.util.JumpUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by vivian.wei on 2018/8/2.
 * 个人中心-阅读历史Holder
 */

public class ReadHistoryHolder extends ContentHolder<ReadHistoryListBean>
        implements ReadHistoryAdapter.OnItemContentClickListener {

    private static final int DEL_BTN_SHOW_MAX_COUNT = 999;
    // IRecyclerView的position有2的偏移量
    private static final int RECYCLERVIEW_POSITION_OFFSET = 2;

    @BindView(R.id.act_my_read_history_irecyclerview)
    IRecyclerView mRecyclerView;

    // 底部按钮区
    @BindView(R.id.act_my_read_history_bottom_split_view)
    View mBottomSplitView;
    @BindView(R.id.act_my_read_history_bottom_ll)
    View mBottomll;
    @BindView(R.id.act_my_read_history_select_all_tv)
    TextView mSelectAllTv;
    @BindView(R.id.act_my_read_history_del_tv)
    TextView mDelTv;
    // 空提示
    @BindView(R.id.act_my_read_history_empty_ll)
    View mEmptyll;

    private Unbinder mUnBinder;
    private LoadMoreFooterView mLoadMoreFooterView;
    private ReadHistoryAdapter mAdapter;
    private ReadHistoryFloatingItemDecoration mFloatingItemDecoration;
    private final List<String> mSelectPositionList = new ArrayList<>();
    private boolean mIsSelectAll = false;

    public ReadHistoryHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_my_read_history);
        mUnBinder = ButterKnife.bind(this, mRootView);

        mBottomll.setVisibility(View.GONE);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mLoadMoreFooterView = (LoadMoreFooterView) mRecyclerView.getLoadMoreFooterView();
        mLoadMoreFooterView.setIsShowTheEnd(true);
        mAdapter = new ReadHistoryAdapter((BaseActivity) mContext);
        mRecyclerView.setIAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        // 列表分组日期
        mFloatingItemDecoration = new ReadHistoryFloatingItemDecoration(mContext);
        mFloatingItemDecoration.setLeftOffset(MScreenUtils.dp2px(15));
        mFloatingItemDecoration.setTitleHeight(MScreenUtils.dp2px(30));
        mFloatingItemDecoration.setTitleTextColor(ContextCompat.getColor(mContext, R.color.color_333333));
        mFloatingItemDecoration.setTitleTextSize(MScreenUtils.sp2px( 14));
        mFloatingItemDecoration.setTtitleBackground(ContextCompat.getColor(mContext, R.color.white));
        mRecyclerView.addItemDecoration(mFloatingItemDecoration);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
    }

    @Override
    public void refreshView() {
        super.refreshView();

        if(null == mData || CollectionUtils.isEmpty(mData.getList())) {
            // 显示空提示
            if(null != mEmptyll) {
                mEmptyll.setVisibility(View.VISIBLE);
            }
        } else {
            if(null != mEmptyll) {
                mEmptyll.setVisibility(View.GONE);
            }
            if(null != mRecyclerView && null != mAdapter) {
                mAdapter.clearList();
                if(null != mFloatingItemDecoration) {
                    mFloatingItemDecoration.clearTitles();
                }

                List<HistoryDao> todayBeans = new ArrayList<>();
                List<HistoryDao> weekBeans = new ArrayList<>();
                List<HistoryDao> otherBeans = new ArrayList<>();
                HistoryDao bean;
                // 时间
                Calendar calendar = new GregorianCalendar();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date today = calendar.getTime();
                long todayTime = today.getTime();
                Date lastWeek = new Date(todayTime - (long)7 * 24 * 60 * 60 * 1000);
                long lastWeekTime = lastWeek.getTime();

                for(int i = 0, size = mData.getList().size(); i < size; i++) {
                    bean = mData.getList().get(i);
                    if(bean.getReadTime() > todayTime) {
                        bean.setDateGroup(ReadHistoryUtil.DATE_GROUP_TODAY);
                        bean.setDatePosition(todayBeans.size() + 1);
                        todayBeans.add(bean);
                    } else if(bean.getReadTime() > lastWeekTime) {
                        bean.setDateGroup(ReadHistoryUtil.DATE_GROUP_WEEK);
                        bean.setDatePosition(weekBeans.size() + 1);
                        weekBeans.add(bean);
                    } else {
                        bean.setDateGroup(ReadHistoryUtil.DATE_GROUP_OTHER);
                        bean.setDatePosition(otherBeans.size() + 1);
                        otherBeans.add(bean);
                    }
                }

                int titleKey = 0;
                if(CollectionUtils.isNotEmpty(todayBeans)) {
                    titleKey = mAdapter.getItemCount() + RECYCLERVIEW_POSITION_OFFSET;
                    if(null != mFloatingItemDecoration) {
                        mFloatingItemDecoration.appendTitles(titleKey, "今日");
                    }
                    mAdapter.addList(todayBeans);
                }
                if(CollectionUtils.isNotEmpty(weekBeans)) {
                    titleKey = mAdapter.getItemCount() + RECYCLERVIEW_POSITION_OFFSET;
                    if(null != mFloatingItemDecoration) {
                        mFloatingItemDecoration.appendTitles(titleKey, "一周内");
                    }
                    mAdapter.addList(weekBeans);
                }
                if(CollectionUtils.isNotEmpty(otherBeans)) {
                    titleKey = mAdapter.getItemCount() + RECYCLERVIEW_POSITION_OFFSET;
                    if(null != mFloatingItemDecoration) {
                        mFloatingItemDecoration.appendTitles(titleKey, "更早");
                    }
                    mAdapter.addList(otherBeans);
                }
                mAdapter.notifyDataSetChanged();
                mLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
            }
        }
    }

    @OnClick({R.id.act_my_read_history_empty_ll,
            R.id.act_my_read_history_select_all_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.act_my_read_history_empty_ll: // 空布局
                finish();
                break;
            case R.id.act_my_read_history_select_all_tv: // 全选/取消全选
                // 埋点
                StatisticPageBean statisticBean1 = ((BaseActivity) mContext).assemble(StatisticMine.MY_HISTORY_EDIT_STATE, "",
                        mIsSelectAll ? "cancel" : "all", "",
                        "", "",
                        null);
                StatisticManager.getInstance().submit(statisticBean1);

                mIsSelectAll = !mIsSelectAll;
                updateSelectAllBtn();
                // 更新列表
                updateDataListSelect(mIsSelectAll);
                mAdapter.notifyDataSetChanged();
                // 更新已选列表
                mSelectPositionList.clear();
                if(mIsSelectAll && null != mData && CollectionUtils.isNotEmpty(mData.getList())) {
                    for(int i = 0, size = mData.getList().size(); i < size; i++) {
                        mSelectPositionList.add(String.valueOf(i));
                    }
                }
                // 更新删除按钮
                updateDelBtn();
                break;
            default:
                break;
        }
    }

    // 编辑状态：选中/取消选中一条中的内容区
    @Override
    public void onItemContentSelectClick(View view, int position, boolean isSelect) {
        if(null != mData && CollectionUtils.isNotEmpty(mData.getList()) && position > -1 && position < mData.getList().size()) {
            HistoryDao bean = mData.getList().get(position);

            // 埋点
            Map<String, String> businessParam = new HashMap<>();
            businessParam.put("articleID", String.valueOf(bean.getRelatedId()));
            StatisticPageBean statisticBean = ((BaseActivity) mContext).assemble(StatisticMine.MY_HISTORY_CONTENT, "",
                    bean.getDateGroup(), "",
                    "checkBox", String.valueOf(bean.getDatePosition()),
                    businessParam);
            StatisticManager.getInstance().submit(statisticBean);

            // 更新一条
            mData.getList().get(position).setSelect(isSelect);
            mAdapter.notifyItemChanged(position);

            String posStr = String.valueOf(position);
            if(isSelect) {
                // 添加到选中List中
                if(!mSelectPositionList.contains(posStr)) {
                    mSelectPositionList.add(posStr);
                }
                // 全部选中
                if(mSelectPositionList.size() == mData.getList().size()) {
                    mIsSelectAll = true;
                    updateSelectAllBtn();
                }
            } else if(mSelectPositionList.contains(posStr)) {
                // 从选中List中移除
                mSelectPositionList.remove(posStr);
                // 修改全选状态
                if(mIsSelectAll) {
                    mIsSelectAll = false;
                    updateSelectAllBtn();
                }
            }
            updateDelBtn();
        }
    }

    // 非编辑状态：点击一条跳转
    @Override
    public void onItemContentJumpClick(View view, int position) {
        if(null != mData && CollectionUtils.isNotEmpty(mData.getList()) && position > -1 && position < mData.getList().size()) {
            HistoryDao bean = mData.getList().get(position);

            // 埋点
            Map<String, String> businessParam = new HashMap<>();
            businessParam.put("articleID", String.valueOf(bean.getRelatedId()));
            StatisticPageBean statisticBean = ((BaseActivity)mContext).assemble(StatisticMine.MY_HISTORY_CONTENT, "",
                    bean.getDateGroup(), "",
                    "click", String.valueOf(bean.getDatePosition()),
                    businessParam);
            StatisticManager.getInstance().submit(statisticBean);

            // 更新阅读历史
            ReadHistoryUtil.saveReadHistory2Local(bean.getContentType(), bean.getRelatedId(),
                    bean.getTitle(), bean.getPublicName(), bean.getImg(), bean.isShowVideoIcon(),
                    bean.getVideoId(), bean.getVideoSourceType());

            if (bean.getContentType() == ReadHistoryUtil.CONTENT_TYPE_ARTICLES || bean.getContentType() == ReadHistoryUtil.CONTENT_TYPE_RANK) {
                String articleType;
                articleType = bean.getContentType() == 4 ?  "2"  : "1";
                //跳转至文章内容详情页
                JumpUtil.startArticleActivity(mContext, "", String.valueOf(bean.getRelatedId()), articleType, "", "");
            } else if (bean.getContentType() == ReadHistoryUtil.CONTENT_TYPE_VIDEO) {
                //跳转至视频内容详情页
                JumpUtil.startMediaVideoDetailActivity(mContext, String.valueOf(bean.getRelatedId()),
                        bean.getVideoId(), bean.getVideoSourceType(), "", "", "");
            }
        }
    }

    // 非编辑状态：侧滑显示删除按钮
    @Override
    public void onItemContentSwipeOpen(SwipeLayout mSwipeLayout, int position) {
        if(null != mData && CollectionUtils.isNotEmpty(mData.getList()) && position > -1 && position < mData.getList().size()) {
            HistoryDao bean = mData.getList().get(position);

            // 埋点
            Map<String, String> businessParam = new HashMap<>();
            businessParam.put("articleID", String.valueOf(bean.getRelatedId()));
            StatisticPageBean statisticBean = ((BaseActivity) mContext).assemble(StatisticMine.MY_HISTORY_CONTENT, "",
                    bean.getDateGroup(), "",
                    "leftSlide", String.valueOf(bean.getDatePosition()),
                    businessParam);
            StatisticManager.getInstance().submit(statisticBean);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 解绑要放到最后
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    // 设置编辑状态
    public void setIsEdit(boolean isEdit) {
        if(null != mBottomSplitView) {
            mBottomSplitView.setVisibility(isEdit ? View.VISIBLE: View.GONE);
        }
        if(null != mBottomll) {
            mBottomll.setVisibility(isEdit ? View.VISIBLE: View.GONE);
        }
        if(null != mRecyclerView) {
            mRecyclerView.setRefreshEnabled(!isEdit);
        }
        if(!isEdit) {
            // 取消编辑状态
            mIsSelectAll = false;
            updateDataListSelect(false);
            mSelectPositionList.clear();
            updateSelectAllBtn();
            updateDelBtn();
        }
        if(null != mAdapter) {
            mAdapter.setIsEdit(isEdit);
            mAdapter.notifyDataSetChanged();
        }
    }

    // 设置刷新监听
    public void setFreshListener(OnRefreshListener refreshListener) {
        if(mRecyclerView != null) {
            mRecyclerView.setOnRefreshListener(refreshListener);
        }
    }

    // 设置click监听
    public void setClickListener(View.OnClickListener onClickListener, ReadHistoryAdapter.OnItemDelClickListener onItemDelClickListener) {
        if(mDelTv != null) {
            mDelTv.setOnClickListener(onClickListener);
        }
        if(mAdapter != null) {
            mAdapter.setOnItemDelClickListener(onItemDelClickListener);
        }
    }

    // 更新下拉刷新状态
    public void setRefreshState(boolean refreshing) {
        if(null != mRecyclerView) {
            mRecyclerView.setRefreshing(refreshing);
        }
    }

    // 获取选中项position
    public List<String> getSelectPositionList() {
        return mSelectPositionList;
    }

    // 更新"全选"按钮文字
    private void updateSelectAllBtn() {
        if(null != mSelectAllTv) {
            mSelectAllTv.setText(getResource().getString(mIsSelectAll ? R.string.my_read_history_unselect_all : R.string.my_read_history_select_all));
        }
    }

    // 更新删除按钮样式
    private void updateDelBtn() {
        if(null == mDelTv) {
            return;
        }
        if(mSelectPositionList.size() > 0) {
            mDelTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_f15353));
            mDelTv.setText(String.format(getResource().getString(R.string.my_read_history_select_del), getShowCount(mSelectPositionList.size())));
        } else {
            mDelTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_f15353_alpha_40));
            mDelTv.setText(getResource().getString(R.string.my_read_history_del));
        }
    }

    // 更新列表数据的选中状态
    private void updateDataListSelect(boolean isSelect) {
        if(null == mData || CollectionUtils.isEmpty(mData.getList())) {
            return;
        }
        HistoryDao bean;
        for(int i = 0, size = mData.getList().size(); i < size; i++) {
            bean = mData.getList().get(i);
            bean.setSelect(isSelect);
        }
    }

    // 格式化数字显示
    private String getShowCount(int num) {
        String numValue = num > DEL_BTN_SHOW_MAX_COUNT ? "999+" : ConvertHelper.toString(num);
        return numValue;
    }
}
