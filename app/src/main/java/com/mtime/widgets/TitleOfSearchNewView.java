package com.mtime.widgets;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mtime.R;

/**
 * @author vivian.wei
 * @date 2020/9/29
 * @desc 搜索页_新搜索组件
 */
public class TitleOfSearchNewView extends BaseTitleView {

    private final Activity mContext;
    private final View mRoot;
    private final ImageButton cancel;
    private EditText searchContent;
    private final View searchPart;
    private boolean isCloseParent;

    public TitleOfSearchNewView(final Activity context, final View root,
                             final ITitleViewLActListener listener) {
        isCloseParent = true;
        mRoot = root;
        mContext = context;

        searchPart = mRoot.findViewById(R.id.search_region);
        searchPart.setVisibility(View.VISIBLE);

        // back
        View back = root.findViewById(R.id.back);
        back.setOnClickListener((view)-> {
            if (isCloseParent) {
                hideInput();
                context.finish();
            }
            if (null != listener) {
                listener.onEvent(ActionType.TYPE_BACK, null);
            }
        });

        // 清空
        cancel = root.findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        cancel.setOnClickListener((v) -> {
            // update UI
            searchContent.setText("");
            cancel.setVisibility(View.GONE);
            //
            if (null != listener) {
                listener.onEvent(ActionType.TYPE_CANCEL, null);
            }
        });

        // 搜索内容
        this.searchContent = root.findViewById(R.id.search_content);
        this.searchContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = s.toString().trim();
                if (TextUtils.isEmpty(key)) {
                    cancel.setVisibility(View.GONE);
                } else {
                    cancel.setVisibility(View.VISIBLE);
                }

                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_CONTENT_CHANGED, key);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //按完回车键才触发
        this.searchContent.setOnEditorActionListener((v, actionId, event) -> {
            if (searchContent.getEditableText().length() > 0 && EditorInfo.IME_ACTION_SEARCH == actionId
                    && null != listener) {
                listener.onEvent(ActionType.TYPE_SEARCH, searchContent.getText().toString());
                return true;
            }
            return false;
        });
    }

    @Override
    public void setAlpha(float alpha) {

    }

    public void setEditHint(final String hint) {
        if (null != searchContent) {
            searchContent.setHint(hint);
        }
    }

    public void setEditTextConent(final String content) {
        if (null != searchContent) {
            this.searchContent.setText(content);
        }
    }

    public String getEditTextConent() {
        return searchContent.getText().toString();
    }

    public void setCloseParent(boolean close) {
        isCloseParent = close;
    }

    public void setVisibile(int visibility) {
        mRoot.setVisibility(visibility);
    }

    public boolean isVisibile() {
        if (mRoot == null) {
            return false;
        }

        return  mRoot.getVisibility() == View.VISIBLE;
    }

    public void setFocus() {
        if (null != this.searchContent) {
            this.searchContent.requestFocus();
            final InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void clearFoucus() {
        if (searchContent != null) {
            searchContent.clearFocus();
        }
    }

    /**
     * 隐藏输入框中右侧的清空按钮
     */
    public void hideClearIcon() {
        if(null != cancel) {
            cancel.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideInput() {
        final InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
    }

}
