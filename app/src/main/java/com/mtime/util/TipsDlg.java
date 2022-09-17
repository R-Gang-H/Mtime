package com.mtime.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mtime.R;

public class TipsDlg extends Dialog {
    private TextView textView = null;
    private ProgressBar progressBar = null;
    private ImageView img = null;
    private int mDuration = 0;

    public TipsDlg(final Context context) {
        super(context);
    }

    public TipsDlg(final Context context, final int duration) {
        super(context, R.style.upomp_bypay_MyDialog);
        mDuration = duration;
    }

    /**
     * @return the textView
     */
    public TextView getTextView() {
        return textView;
    }

    /**
     * @return the progressBar
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * @return the img
     */
    public ImageView getImg() {
        return img;
    }

    public String getText() {
        if (textView != null) {
            return textView.getText().toString();
        }
        return null;
    }

    public void setText(final String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setImage(final Bitmap bmp) {
        if (img != null) {
            img.setImageBitmap(bmp);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_tips);

        textView = findViewById(R.id.textView1);
        progressBar = findViewById(R.id.progressBar1);
        img = findViewById(R.id.imageView1);
    }

    public void setDuration(final int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    @Override
    public void show() {
        if (mDuration > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        dismiss();
                    } catch (Exception e) {

                    }
                }
            }, mDuration);
        }
        super.show();
    }

    /**
     * 显示提示对话框（默认时间1000毫秒）
     *
     * @param ctx
     * @param msg
     * @param msg 显示内容
     * @param showImage       是否显示“勾”图片
     */
    public static void showTipsDlg(final Context ctx, final String msg,
                                   final boolean showImage) {
        TipsDlg.showTipsDlg(ctx, msg, 1000, showImage);
    }

    /**
     * 显示提示对话框
     *
     * @param ctx
     * @param msg
     * @param duration        持续时间
     * @param msg 显示内容
     * @param showImage       是否显示“勾”图片
     */
    public static TipsDlg showTipsDlg(final Context ctx, final String msg,
                                      final int duration, final boolean showImage) {
        final TipsDlg tipsDlg = new TipsDlg(ctx, duration);
        tipsDlg.show();

        tipsDlg.getProgressBar().setVisibility(View.GONE);
        if (showImage) {
            tipsDlg.getImg().setVisibility(View.VISIBLE);
        } else {
            tipsDlg.getImg().setVisibility(View.GONE);
        }
        tipsDlg.setText(msg);

        return tipsDlg;
    }

    public static TipsDlg showTipsDlgWithProgress(final Context ctx,
                                                  final String msg) {
        final TipsDlg tipsDlg = new TipsDlg(ctx);
        tipsDlg.show();
        tipsDlg.getProgressBar().setVisibility(View.VISIBLE);

        tipsDlg.getImg().setVisibility(View.GONE);
        tipsDlg.setText(msg);

        return tipsDlg;
    }
}
