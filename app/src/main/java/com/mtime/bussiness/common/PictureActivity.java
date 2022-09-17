package com.mtime.bussiness.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotlin.android.image.coil.ext.CoilCompat;
import com.mtime.R;
import com.mtime.beans.ImageBean;
import com.mtime.frame.BaseActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangshun on 15-6-24.
 */
public class PictureActivity extends BaseActivity implements View.OnClickListener {
    public static final String KEY_CURRENT_ID = "ID";
    public static final String KEY_IMAGES_EXTRA = "imagesExtra";
    
    public static void launch(Context context, int currentId, List<ImageBean> images){
        Intent launcher = new Intent(context, PictureActivity.class);
        launcher.putExtra(KEY_CURRENT_ID, currentId);
        launcher.putExtra(KEY_IMAGES_EXTRA, (Serializable)images);
        context.startActivity(launcher);
    }

    public static void launch(Activity context, String refer, int currentId, List<ImageBean> images, int requestcode) {
        Intent launcher = new Intent(context, PictureActivity.class);
        launcher.putExtra(KEY_CURRENT_ID, currentId);
        launcher.putExtra(KEY_IMAGES_EXTRA, (Serializable)images);
        dealRefer(context, refer, launcher);
        context.startActivityForResult(launcher, requestcode);
    }
    
    private List<ImageBean> images = new ArrayList<ImageBean>();
    private ViewPager pager;
    private PageAdapter adapter;
    private int currentId = 0;
    private TextView title;

    @Override
    protected void onInitVariable() {
        images = (List<ImageBean>) getIntent().getSerializableExtra(KEY_IMAGES_EXTRA);
        currentId = getIntent().getIntExtra(KEY_CURRENT_ID, 0);
        setPageLabel("pictureThumbs");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_picture);
        pager = findViewById(R.id.viewpager);
        pager.setOnPageChangeListener(pageChangeListener);
        TextView del = findViewById(R.id.button_del);
        del.setOnClickListener(this);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(this);
        title = findViewById(R.id.title);
        adapter = new PageAdapter(images);
        pager.setOnPageChangeListener(pageChangeListener);
        pager.setAdapter(adapter);

        pager.setCurrentItem(currentId);
        setTitle();
    }

    public void setTitle() {
        if (title != null) {
            title.setText((currentId + 1) + "/" + (images == null ? 1 : images.size()));
        }

    }

    @Override
    protected void onInitEvent() {

    }

    @Override
    protected void onRequestData() {

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();
        } else if (view.getId() == R.id.button_del) {
            images.remove(currentId);
            Intent intent = new Intent();
            intent.putExtra(PictureSelectActivity.IMAGESEXTRA, (Serializable) images);
            intent.putExtra(PictureSelectActivity.INDEX_EXTRA, currentId);
            setResult(PictureSelectActivity.ACTIVITY_RESULT_CODE_PICTURESELECT, intent);
            finish();
        }
    }

    class PageAdapter extends PagerAdapter {

        private final List<ImageBean> imageList;

        private final int size;

        public PageAdapter(List<ImageBean> imageList) {
            this.imageList = imageList;
            size = imageList == null ? 0 : imageList.size();
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public void finishUpdate(ViewGroup container) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageBean ib = imageList.get(position);

            final ImageView img = new AppCompatImageView(PictureActivity.this);
            img.setBackgroundColor(0xff000000);
            img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            File file = null;
            if (!TextUtils.isEmpty(ib.path)) {
                file = new File(ib.path);
            }
            if (null == file) {
                img.setImageResource(R.drawable.default_image);
            } else {
                CoilCompat.INSTANCE.loadGifImage(
                        img,
                        file,
                        0,
                        0,
                        false,
                        true,
                        false,
                        R.drawable.default_image,
                        null,
                        0F
                );
            }

            container.addView(img);
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

    private final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            currentId = arg0;
            setTitle();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void clearMemory() {
        super.clearMemory();
//        NativeImageLoader.getInstance().cleanBitmap();
    }

}
