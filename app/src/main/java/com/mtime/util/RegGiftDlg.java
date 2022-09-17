package com.mtime.util;

import android.app.Dialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.R;

/**
 * Created by vivian.wei on 15/8/6.
 * 手机号注册成功－新人大礼包弹框
 */
public class RegGiftDlg extends Dialog {

    private final AppCompatActivity context;
    private final String imageUrl;
    private View.OnClickListener okListener = null;

    public RegGiftDlg(AppCompatActivity context, String imageUrl) {
        super(context, R.style.Main_Dialog_New_Gift);
        this.context = context;
        this.imageUrl = imageUrl;
    }

    public void setBtnCloseListener(final View.OnClickListener click) {
        okListener = click;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_home_registration_gift);

        //加载图片
        ImageView imageView = findViewById(R.id.img);
        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .load(imageUrl)
                .view(imageView)
                .placeholder(R.drawable.default_image)
                .showload();
        View closeView = findViewById(R.id.close_rl);

        //点击关闭图标消失
        if(okListener!=null){
            closeView.setOnClickListener(okListener);
        }else {
            closeView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    dismiss();
                }

            });
        }
    }

}