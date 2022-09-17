package com.mtime.bussiness.ticket.cinema.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.widgets.photoview.PhotoView;

/**
 * Created by CSY on 2018/7/23.
 * <营业执照页>
 */
public class LicenseActivity extends BaseActivity  {


    private PhotoView mLicenseIv;

    private String mLicenceImgUrl;

    @Override
    protected void onParseIntent() {
        mLicenceImgUrl = getIntent().getStringExtra(App.getInstance().KEY_LICENCE_URL);
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_license);
        View navbar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navbar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE, getResources().getString(
                R.string.st_mall_license), null);
        mLicenseIv = findViewById(R.id.act_license_icon_iv);
        mLicenceImgUrl = getIntent().getStringExtra(App.getInstance().KEY_LICENCE_URL);
        ImageHelper.with(this, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .load(mLicenceImgUrl)
                .view(mLicenseIv)
                .placeholder(R.drawable.default_image)
                .showload();
    }


    public static void launch(BaseActivity context, String refer, String url) {
        Intent intent = new Intent(context, LicenseActivity.class);
        if(!TextUtils.isEmpty(url)) {
            intent.putExtra(App.getInstance().KEY_LICENCE_URL, url);
        }
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }
}
