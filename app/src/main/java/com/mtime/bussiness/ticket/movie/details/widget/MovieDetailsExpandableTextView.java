package com.mtime.bussiness.ticket.movie.details.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-29
 */
public class MovieDetailsExpandableTextView extends LinearLayout {
    /** 显示内容的文本控件 */
    private TextView mContentTextView;
    /** 全文/收起 按钮 */
    private TextView mExpansionButton;

    /** 最大显示行数（默认 6 行） */
    private int mMaxLine = 3;
    /** 显示的内容 */
    private CharSequence mContent;

    /** 当前是否已是 "全文" 状态 */
    private boolean mIsExpansion;

    private OnToggleExpansionStatusListener mOnToggleExpansionStatusListener;

    public MovieDetailsExpandableTextView(Context context) {
        super(context);
        init(context, null);
    }

    public MovieDetailsExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MovieDetailsExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);

        LayoutInflater.from(context).inflate(R.layout.layout_movie_details_expandable_text_view, this);

        mContentTextView = findViewById(R.id.movie_details_intro_content_tv);
        mExpansionButton = findViewById(R.id.movie_details_intro_expand_tv);
        mExpansionButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        mExpansionButton.getPaint().setAntiAlias(true);//抗锯齿

        // 监听文本控件的布局绘制
        mContentTextView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mContentTextView.getWidth() == 0) {
                            return;
                        }
                        mContentTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // 文本控件绘制成功后, 更新文本
                        setText(mContent);
                    }
                }
        );

        // "全文/收起" 按钮点击监听
        mExpansionButton.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleExpansionStatus();
                    }
                }
        );
        // 默认隐藏 "全文/收起" 按钮
        mExpansionButton.setVisibility(GONE);
    }

    /**
     * 切换 全文/收起 状态
     */
    private void toggleExpansionStatus() {
        // 切换状态
        mIsExpansion = !mIsExpansion;

        // 更新内容和切换按钮的显示
        if (mIsExpansion) {
            mExpansionButton.setText("收起");                       // 全文状态, 按钮显示 "收起"
            mContentTextView.setMaxLines(Integer.MAX_VALUE);        // 全文状态, 行数设置为最大
        } else {
            mExpansionButton.setText("展开");                       // 收起状态, 按钮显示 "展开"
            mContentTextView.setMaxLines(mMaxLine);                 // 收起状态, 最大显示指定的行数
        }

        if (null != mOnToggleExpansionStatusListener) {
            mOnToggleExpansionStatusListener.onToggleExpansionStatusChanged(mIsExpansion);
        }
    }

    public void setOnToggleExpansionStatusListener(OnToggleExpansionStatusListener onToggleExpansionStatusListener) {
        mOnToggleExpansionStatusListener = onToggleExpansionStatusListener;
    }

    /**
     * 设置收起状态最大允许显示的行数（超过该行数显示 "收起" 按钮）
     */
    public void setMaxLine(int maxLine) {
        this.mMaxLine = maxLine;
        setText(mContent);                                      // 更新状态, 重新显示文本
    }

    /**
     * 设置需要显示的文本
     */
    public void setText(CharSequence text) {
        mContent = text;

        // 文本控件有宽度时（绘制成功后）才能获取到文本显示的所需要的行数,
        // 如果控件还没有被绘制, 等监听到绘制成功后再设置文本
        if (mContentTextView.getWidth() == 0) {
            return;
        }

        mContentTextView.setMaxLines(Integer.MAX_VALUE);        // 默认先设置最大行数为最大值（即不限制行数）
        mContentTextView.setText(mContent);                     // 设置文本

        int lineCount = mContentTextView.getLineCount();        // 设置完文本后, 获取显示该文本所需要的行数

        if (lineCount > mMaxLine) {
            // 行数超过显示, 显示 "展开" 按钮
            mExpansionButton.setVisibility(VISIBLE);
            mExpansionButton.setText("展开");

            mContentTextView.setMaxLines(mMaxLine);             // 设置文本控件的最大允许显示行数
            mIsExpansion = false;

        } else {
            // 行数没有超过限制, 不需要显示 "全文/收起" 按钮
            mExpansionButton.setVisibility(GONE);
        }
    }

    public interface OnToggleExpansionStatusListener {
        void onToggleExpansionStatusChanged(boolean isExpansion);
    }

}
