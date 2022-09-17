package com.mtime.bussiness.mine.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.aspsine.irecyclerview.OnRefreshListener;
import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.BaseTitleBarHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.mine.history.adapter.ReadHistoryAdapter;
import com.mtime.bussiness.mine.history.bean.ReadHistoryListBean;
import com.mtime.bussiness.mine.history.dao.HistoryDao;
import com.mtime.bussiness.mine.history.holder.ReadHistoryHolder;
import com.mtime.frame.BaseActivity;
import com.mtime.frame.holder.DefaultTitleBarHolder;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.mine.StatisticMine;

import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vivian.wei on 2018/8/2.
 * 个人中心-阅读历史
 */

public class ReadHistoryActivity extends BaseActivity<ReadHistoryListBean, ReadHistoryHolder>
    implements OnRefreshListener, View.OnClickListener, ReadHistoryAdapter.OnItemDelClickListener {

    private DefaultTitleBarHolder mDefaultTitleBarHolder;
    private final ReadHistoryListBean mHistoryData = new ReadHistoryListBean();
    private boolean mIsEdit = false;

    /**
     * 自己定义refer
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, ReadHistoryActivity.class);
        dealRefer(context,refer, launcher);
        context.startActivity(launcher);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new ReadHistoryHolder(this);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        setPageLabel(StatisticMine.PN_MY_HISTORY);

        // 标题
        BaseTitleBarHolder titleBarHolder = getUserHolder().titleBarHolder;
        if(null != titleBarHolder) {
            mDefaultTitleBarHolder = (DefaultTitleBarHolder)titleBarHolder;
            mDefaultTitleBarHolder.setRightButtonMode(DefaultTitleBarHolder.RIGHT_BUTTON_MODE_TEXT);
            mDefaultTitleBarHolder.setTextButtonText(getResources().getString(R.string.my_read_history_edit));
        }
        // 获取本地阅读历史数据
        getReadHistoryData();
        getUserContentHolder().setFreshListener(this);
        getUserContentHolder().setClickListener(this, this);
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode){
            case DefaultTitleBarHolder.TITLE_BAR_EVENT_TEXT_BUTTON_CLICK: // 编辑/取消
                // 埋点
                StatisticPageBean statisticBean1 = assemble(StatisticMine.MY_HISTORY_TOP_NAV, "",
                        mIsEdit ? "cancel" : "edit", "",
                        "", "",
                        null);
                StatisticManager.getInstance().submit(statisticBean1);

                mIsEdit = !mIsEdit;
                if(null != mDefaultTitleBarHolder) {
                    mDefaultTitleBarHolder.setTextButtonText(getResources().getString(mIsEdit ? R.string.my_read_history_cancel : R.string.my_read_history_edit));
                }
                getUserContentHolder().setIsEdit(mIsEdit);
                break;
            case ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK: // 返回箭头
                // 埋点
                StatisticPageBean statisticBean2 = assemble(StatisticMine.MY_HISTORY_TOP_NAV, "",
                        "back", "",
                        "", "",
                        null);
                StatisticManager.getInstance().submit(statisticBean2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_my_read_history_del_tv: // 点击"删除"按钮
                // 埋点
                StatisticPageBean statisticBean = assemble(StatisticMine.MY_HISTORY_EDIT_STATE, "",
                        "delete", "",
                        "", "",
                        null);
                StatisticManager.getInstance().submit(statisticBean);

                List<String> selectPositionList = getUserContentHolder().getSelectPositionList();
                delete(selectPositionList);
                break;
            default:
                break;
        }
    }

    // 点击一条中的右侧删除按钮
    @Override
    public void onItemDelClick(View view, int position) {
        if(null != mHistoryData && CollectionUtils.isNotEmpty(mHistoryData.getList())
                && position > -1 && position < mHistoryData.getList().size()) {
            HistoryDao bean = mHistoryData.getList().get(position);

            // 埋点
            Map<String, String> businessParam = new HashMap<>();
            businessParam.put("articleID", String.valueOf(bean.getRelatedId()));
            StatisticPageBean statisticBean = assemble(StatisticMine.MY_HISTORY_CONTENT, "",
                    bean.getDateGroup(), "",
                    "delete", String.valueOf(bean.getDatePosition()),
                    businessParam);
            StatisticManager.getInstance().submit(statisticBean);

            List<String> selectPositionList = new ArrayList<>();
            selectPositionList.add(String.valueOf(position));
            delete(selectPositionList);
        }
    }

    @Override
    public void onRefresh() {
        // 埋点
        StatisticPageBean statisticBean = assemble(StatisticMine.MY_HISTORY_CONTENT, "",
                "refresh", "",
                "", "",
                null);
        StatisticManager.getInstance().submit(statisticBean);

        getReadHistoryData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 获取本地阅读历史数据
    private void getReadHistoryData() {
        HistoryDao.getLocalDatas(new FindMultiCallback<HistoryDao>() {
            @Override
            public void onFinish(List<HistoryDao> list) {
                getUserContentHolder().setRefreshState(false);

                // 不论是否为空都需要更新界面
                // 更新列表
                mHistoryData.setList(list);
                setData(mHistoryData);
                // 更新标题栏
                updateTitleBar();
            }
        });
    }

    // 更新标题栏
    private void updateTitleBar() {
        if(null != mDefaultTitleBarHolder) {
            if (null == mHistoryData || CollectionUtils.isEmpty(mHistoryData.getList())) {
                mDefaultTitleBarHolder.setTextButtonText("");
            } else {
                mDefaultTitleBarHolder.setTextButtonText(getResources().getString(mIsEdit ? R.string.my_read_history_cancel : R.string.my_read_history_edit));
            }
        }
    }

    // 批量删除
    private void delete(List<String> selectPositionList) {
        if(null != mHistoryData && CollectionUtils.isNotEmpty(mHistoryData.getList())
                && CollectionUtils.isNotEmpty(selectPositionList)) {
            if(selectPositionList.size() > 1) {
                // 选中postion倒序排
                Collections.sort(selectPositionList, (obj1, obj2) -> {
                    int a = Integer.parseInt(obj1);
                    int b = Integer.parseInt(obj2);
                    if (a > b) {
                        return -1;
                    } else if (a < b) {
                        return 1;
                    } else {
                        return 0;
                    }
                });
            }
            // 移除数据
            List<HistoryDao> dataList = mHistoryData.getList();
            List<HistoryDao> removeDataList = new ArrayList<>();
            int position = -1;
            for(int i = 0, size = selectPositionList.size(); i < size; i++) {
                position = Integer.parseInt(selectPositionList.get(i));
                if(position > -1 && position < dataList.size()) {
                    removeDataList.add(dataList.get(position));
                    dataList.remove(position);
                }
            }
            // 更新列表
            mHistoryData.setList(dataList);
            setData(mHistoryData);
            // 取消编辑状态
            mIsEdit = false;
            // 更新标题栏
            updateTitleBar();
            // 更新界面
            getUserContentHolder().setIsEdit(mIsEdit);
            // 保存到本地数据库
            HistoryDao.remove(removeDataList);
        }
    }

}
