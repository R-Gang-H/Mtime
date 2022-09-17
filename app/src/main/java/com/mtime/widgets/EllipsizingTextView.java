package com.mtime.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class EllipsizingTextView extends TextView {

    private static final String ELLIPSIS = "...";

    public interface EllipsizeListener {
        void ellipsizeStateChanged(boolean ellipsized);
    }

    private final List<EllipsizeListener> ellipsizeListeners            = new ArrayList<EllipsizeListener>();

    private boolean                       isEllipsized;
    private boolean                       isStale;
    private boolean                       programmaticChange;
    private String                        fullText;
    private int                           maxLines                      = 2;
    private float                         lineSpacingMultiplier         = 1.0f;
    private float                         lineAdditionalVerticalPadding = 0.0f;

    public EllipsizingTextView(final Context context) {
        super(context);
    }

    public EllipsizingTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public EllipsizingTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addEllipsizeListener(final EllipsizeListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        this.ellipsizeListeners.add(listener);
    }

    public void removeEllipsizeListener(final EllipsizeListener listener) {
        this.ellipsizeListeners.remove(listener);
    }

    public boolean isEllipsized() {
        return this.isEllipsized;
    }

    @Override
    public void setMaxLines(final int maxLines) {
        super.setMaxLines(maxLines);
        this.maxLines = maxLines;
        this.isStale = true;
    }

    /*
     * 根据实际文本行数、设置初始展示行数来回切换
     */
    public void toggleShowLines() {
        final String workingText = this.fullText;
        final Layout layout = this.createWorkingLayout(workingText);

        final int contentLines = layout.getLineCount();

        this.isEllipsized = !this.isEllipsized;
        final int linesCount = this.isEllipsized ? this.maxLines : contentLines;

        super.setMaxLines(linesCount);
        this.resetText(linesCount);
    }

    public int LineCount() {
        int result = 0;
        if ((this.fullText != null) && (this.fullText.length() > 0)) {
            final Layout layout = this.createWorkingLayout(this.fullText);
            result = layout.getLineCount();
        }
        return result;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    @Override
    public void setLineSpacing(final float add, final float mult) {
        this.lineAdditionalVerticalPadding = add;
        this.lineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        if (!this.programmaticChange) {
            this.fullText = text.toString();
            this.isStale = true;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (this.isStale) {
            super.setEllipsize(null);
            final int allCount = this.getMaxLines();
            this.resetText(allCount);
        }
        super.onDraw(canvas);
    }

    @SuppressLint("NewApi")
    private void resetText(final int showLines) {
        final int maxLines = showLines;
        String workingText = this.fullText;
        this.isEllipsized = false;
        if ((maxLines > 0) && (workingText != null) && (workingText.length() > 0)) {
            final Layout layout = this.createWorkingLayout(workingText);
            if (layout.getLineCount() > maxLines) {

                workingText = this.fullText.substring(0, layout.getLineEnd(maxLines - 1) - 1).trim();
                final Layout layout2 = this.createWorkingLayout(workingText + EllipsizingTextView.ELLIPSIS);
                while (layout2.getLineCount() > maxLines) {
                    System.out.println(layout2.getLineCount() + "\t" + maxLines);
                    final int lastSpace = workingText.lastIndexOf(' ');
                    if (lastSpace == -1) {
                        break;
                    }
                    workingText = workingText.substring(0, lastSpace);
                }
                workingText = workingText + EllipsizingTextView.ELLIPSIS;
                this.isEllipsized = true;
            }
        }
        if (!workingText.equals(this.getText())) {
            this.programmaticChange = true;
            try {
                this.setText(workingText);
            }
            finally {
                this.programmaticChange = false;
            }
        }
        this.isStale = false;
        for (final EllipsizeListener listener : this.ellipsizeListeners) {
            listener.ellipsizeStateChanged(this.isEllipsized);
        }
    }

    private Layout createWorkingLayout(final String workingText) {
        return new StaticLayout(workingText, this.getPaint(), this.getWidth() - this.getPaddingLeft()
                - this.getPaddingRight(), Alignment.ALIGN_NORMAL, this.lineSpacingMultiplier,
                this.lineAdditionalVerticalPadding, false);
    }

    @Override
    public void setEllipsize(final TruncateAt where) {

    }
}