package com.kotlin.chat_component.inner.modules.conversation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kotlin.chat_component.R;
import com.kotlin.chat_component.inner.interfaces.OnItemClickListener;
import com.kotlin.chat_component.inner.modules.conversation.interfaces.OnConversationChangeListener;
import com.kotlin.chat_component.inner.modules.conversation.interfaces.OnConversationLoadListener;
import com.kotlin.chat_component.inner.modules.conversation.model.EaseConversationInfo;
import com.kotlin.chat_component.inner.modules.menu.EasePopupMenuHelper;
import com.kotlin.chat_component.inner.modules.menu.OnPopupMenuItemClickListener;
import com.kotlin.chat_component.inner.modules.menu.OnPopupMenuPreShowListener;
import com.kotlin.chat_component.inner.ui.base.EaseBaseFragment;
import com.hyphenate.util.EMLog;

import java.util.List;

public class EaseConversationListFragment extends EaseBaseFragment implements OnItemClickListener, OnPopupMenuItemClickListener, OnPopupMenuPreShowListener, SwipeRefreshLayout.OnRefreshListener, OnConversationLoadListener, OnConversationChangeListener {
    private static final String TAG = EaseConversationListFragment.class.getSimpleName();
    public LinearLayout llRoot;
    public EaseConversationListLayout conversationListLayout;
    public SwipeRefreshLayout srlRefresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(savedInstanceState);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public int getLayoutId() {
        return R.layout.ease_fragment_conversations;
    }

    public void initView(Bundle savedInstanceState) {
        llRoot = findViewById(R.id.ll_root);
        srlRefresh = findViewById(R.id.srl_refresh);
        conversationListLayout = findViewById(R.id.list_conversation);
        conversationListLayout.init();
    }

    public void initListener() {
        conversationListLayout.setOnItemClickListener(this);
        conversationListLayout.setOnPopupMenuItemClickListener(this);
        conversationListLayout.setOnPopupMenuPreShowListener(this);
        conversationListLayout.setOnConversationLoadListener(this);
        conversationListLayout.setOnConversationChangeListener(this);
        srlRefresh.setOnRefreshListener(this);
    }

    public void initData() {
        conversationListLayout.loadDefaultData();
    }

    /**
     * ????????????????????????
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {

    }

    /**
     * ????????????????????????????????????
     * @param item
     * @param position
     */
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EMLog.i(TAG, "click menu position = "+position);
        return false;
    }

    /**
     * ??????????????????????????????????????????????????????PopupMenu????????????{@link EaseConversationListLayout#addItemMenu(int, int, int, String)}???
     * ????????????????????????{@link EaseConversationListLayout#findItemVisible(int, boolean)}
     * @param menuHelper
     * @param position
     */
    @Override
    public void onMenuPreShow(EasePopupMenuHelper menuHelper, int position) {

    }

    @Override
    public void onRefresh() {
        conversationListLayout.loadDefaultData();
    }

    @Override
    public void loadDataFinish(List<EaseConversationInfo> data) {
        finishRefresh();
    }

    @Override
    public void loadDataFail(String message) {
        finishRefresh();
    }

    /**
     * ????????????
     */
    public void finishRefresh() {
        if(!mContext.isFinishing() && srlRefresh != null) {
            runOnUiThread(()->srlRefresh.setRefreshing(false));
        }
    }

    @Override
    public void notifyItemChange(int position) {

    }

    @Override
    public void notifyAllChange() {

    }

    @Override
    public void notifyItemRemove(int position) {

    }
}

