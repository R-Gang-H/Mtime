package com.mtime.bussiness.common.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kk.taurus.uiframe.d.BaseTitleBarParams;
import com.kk.taurus.uiframe.v.BaseTitleBarHolder;
import com.mtime.R;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-25
 */
public class AbsWebViewTitleBarHolder extends BaseTitleBarHolder {

    private final ActionCallback mCallback;
    private final BaseTitleBarParams mTitleBarParams;
    private LinearLayout mLeftBtnContainer;
    private LinearLayout mRightBtnContainer;
    private TextView mTitleTv;
    private View mBackBtn;
    private View mShareBtn;
    private View mCloseBtn;

    private LinearLayout.LayoutParams mBtnParams;
    private RelativeLayout.LayoutParams mContainerParams;

    public AbsWebViewTitleBarHolder(Context context, ActionCallback callback) {
        super(context);
        mCallback = callback;
        mTitleBarParams = new BaseTitleBarParams();
    }

    @Override
    public BaseTitleBarParams getTitleBarParams() {
        mTitleBarParams.titleBarHeight = mCallback.onGetTitleHeight();
        return mTitleBarParams;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_base_web_view_title_bar);
        mLeftBtnContainer = getViewById(R.id.common_base_web_view_left_btn_container);
        mRightBtnContainer = getViewById(R.id.common_base_web_view_right_btn_container);
        mTitleTv = getViewById(R.id.common_base_web_view_title);
        mBackBtn = getViewById(R.id.common_base_web_view_back);
        mShareBtn = getViewById(R.id.common_base_web_view_share);
        mCloseBtn = getViewById(R.id.common_base_web_view_close);

        mBackBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);
    }

    /**
     * 设置 返回按钮是否显示
     *
     * @param show show
     */
    public void setBackShow(boolean show) {
        handleShow(mBackBtn, show);
    }
    
    /**
     * 返回按钮是否显示
     *
     * @return
     */
    public boolean isBackShow() {
        return null != mBackBtn && mBackBtn.getVisibility() != View.GONE;
    }

    /**
     * 设置 关闭按钮是否显示
     *
     * @param show show
     */
    public void setCloseShow(boolean show) {
        handleShow(mCloseBtn, show);
    }

    /**
     * 设置 分享按钮是否显示
     *
     * @param show show
     */
    public void setShareShow(boolean show) {
        handleShow(mShareBtn, show);
    }

    /**
     * 设置 指定 id 的 view 是否显示
     *
     * @param show show
     * @throws NullPointerException 指定 id view 未找到
     */
    public void setViewShow(int id, boolean show) {
        View view = mRootView.findViewById(id);
        if (view == null) {
            throw new NullPointerException("id of View not found");
        }
        handleShow(view, show);
    }

    /**
     * 为 title 左边 添加 按钮
     *
     * @param btn  要添加的 view
     * @param head 是否添加到现有 按钮的 左边
     */
    public void addLeftBtn(View btn, boolean head) {
        addBtn(mLeftBtnContainer, btn, head ? 0 : -1);
    }

    /**
     * 为 title 右边 添加 按钮
     *
     * @param btn  要添加的 view
     * @param head 是否添加到现有 按钮的 左边
     */
    public void addRightBtn(View btn, boolean head) {
        addBtn(mRightBtnContainer, btn, head ? 0 : -1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_base_web_view_back:
                mCallback.onBackClicked();
                break;
            case R.id.common_base_web_view_share:
                mCallback.onShareClicked();
                break;
            case R.id.common_base_web_view_close:
                mCallback.onCloseClicked();
                break;
            default:
                mCallback.onTitleAction(v.getId());
                break;
        }
    }

    private void addBtn(ViewGroup parentContainer, View btn, int position) {
        checkViewId(btn);
        RelativeLayout container = createContainer();
        int id = btn.getId();
        btn.setId(View.NO_ID);
        container.setId(id);
        container.addView(btn, mContainerParams);
        container.setOnClickListener(this);
        parentContainer.addView(container, position, mBtnParams);
    }

    private RelativeLayout createContainer() {
        if (mBtnParams == null) {
            mBtnParams = new LinearLayout.LayoutParams(
                    (int) getDimension(R.dimen.title_bar_normal_back_view_width),
                    (int) getDimension(R.dimen.title_bar_height)
            );
        }
        if (mContainerParams == null) {
            mContainerParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            mContainerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        RelativeLayout container = new RelativeLayout(mContext);
        container.setFocusable(true);
        container.setClickable(true);
        return container;
    }

    private void checkViewId(View view) {
        int id = view.getId();
        if (id == View.NO_ID) {
            throw new RuntimeException("View need an id");
        }
        View find = mRootView.findViewById(id);
        if (find != null) {
            throw new RuntimeException("id of View already exists");
        }
    }

    private void handleShow(View view, boolean show) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleTv.setText(title);
    }

    /**
     * 设置 整个 title bar 是否显示
     *
     * @param showTitle show
     */
    public void setTitleBarShow(boolean showTitle) {
        setVisibility(showTitle ? View.VISIBLE : View.GONE);
    }
    
    /**
     * 整个 title bar 是否显示
     * @return
     */
    public boolean isTitleBarShow() {
        return null != mRootView && mRootView.getVisibility() != View.GONE;
    }

    public interface ActionCallback {
        /**
         * @return title 高度
         */
        int onGetTitleHeight();

        /**
         * 除 关闭，返回，分享意外的 按钮点击事件回调
         *
         * @param id view id
         */
        void onTitleAction(int id);

        void onBackClicked();

        void onCloseClicked();

        void onShareClicked();
    }
}
