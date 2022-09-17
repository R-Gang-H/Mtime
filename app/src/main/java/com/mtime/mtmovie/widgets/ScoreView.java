package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * 分数控件
 *
 * @category 分数是10的不显示，其他都显示2位数，使用时可省略做非空判断
 */
public class ScoreView extends AppCompatTextView {
    private final float SCALE_X = 1.0f; // x轴缩放比例
    private String formatStr;

    public ScoreView(Context context) {
        super(context);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFormatStr(String formatStr) {
        this.formatStr = formatStr;
    }

    public void setScore(String numStr) {
        if (numStr != null && !"".equals(numStr)) {
            try {
                setScore(Double.parseDouble(numStr));
            } catch (NumberFormatException e) {
                setVisibility(View.GONE);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public void setScore(Double scoreNum) {
        float num = (float) (Math.round(scoreNum * 10)) / 10;
        if (num == 10) {
            setTexts("10");
            setVisibility(View.VISIBLE);
        } else if (num > 10 || num <= 0) {
            setVisibility(View.GONE);
        } else {
            setTexts(String.valueOf(num));
            setVisibility(View.VISIBLE);
        }
    }

    public void setScore(float scoreNum) {
        float num = (float) (Math.round(scoreNum * 10)) / 10;
        if (num == 10) {
            setTexts("10");
            setVisibility(View.VISIBLE);
        } else if (num > 10 || num <= 0) {
            setVisibility(View.GONE);
        } else {
            setTexts(String.valueOf(num));
            setVisibility(View.VISIBLE);
        }
    }

    public void setTexts(String text) {
        if (!TextUtils.isEmpty(formatStr))
            text = String.format(formatStr, text);
        applyLastLetterSpacing(text);
    }

    private void applyLastLetterSpacing(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String c = "" + text.charAt(i);
            builder.append(c);
        }
        // 末端新增一个空格
        builder.append("\u00A0");
        SpannableString finalText = new SpannableString(builder.toString());
        finalText.setSpan(new ScaleXSpan(SCALE_X),
                builder.toString().length() - 1, builder.toString().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        super.setText(finalText, BufferType.SPANNABLE);
    }

}
