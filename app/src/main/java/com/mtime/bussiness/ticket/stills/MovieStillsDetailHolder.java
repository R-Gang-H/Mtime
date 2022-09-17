package com.mtime.bussiness.ticket.stills;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.image.ImageCallback;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.Photo;
import com.mtime.bussiness.ticket.stills.utils.PhotoManager;
import com.mtime.mtmovie.widgets.UnTouchViewPager;
import com.mtime.util.MtimeUtils;
import com.mtime.widgets.photoview.PhotoView;
import com.mtime.widgets.photoview.PhotoViewAttacher;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-27
 */
class MovieStillsDetailHolder extends ContentHolder<ArrayList<Photo>> implements ViewPager.OnPageChangeListener {

    MovieStillsDetailHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
    }

    @BindView(R.id.pager)
    UnTouchViewPager mViewPager;

    @BindView(R.id.cur_pos_tv)
    TextView mCurPosTv;

    private ArrayList<Photo> mPhotos;
    private int mTargetType;

    private int mCurPos;

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_still_detail_layout);
        ButterKnife.bind(this, mRootView);

//        mPhotos = getIntent().getParcelableArrayListExtra(MovieStillsDetailActivity.KEY_PHOTO_LIST_PHOTOS);
        mPhotos = PhotoManager.getPhotoList();
        int pos = getIntent().getIntExtra(MovieStillsDetailActivity.KEY_PHOTO_LIST_POSITION, 0);
        mTargetType = getIntent().getIntExtra(MovieStillsDetailActivity.KEY_PHOTO_LIST_TARGET_TYPE, -1);

        DetailAdapter adapter = new DetailAdapter();
        mViewPager.setAdapter(adapter);


        int realSize = mPhotos == null ? 0 : mPhotos.size();
        if (realSize == 1) {

            mViewPager.setCurrentItem(0);
        } else {
            int max = Integer.MAX_VALUE;
            int half = max / 2 - 1;
            int realPos = realSize == 0 ? 0 : half % realSize;
            int diff = pos - realPos;

            mViewPager.setCurrentItem(half + diff);
        }

        mViewPager.addOnPageChangeListener(this);

        updatePos(pos);
    }

    private void updatePos(int cur) {
        mCurPos = cur;
        mCurPosTv.setText(String.format(Locale.getDefault(), "%d/%d", cur + 1, mPhotos.size()));
    }

    @OnClick({
            R.id.still_download_iv
//            ,
//            R.id.still_share_iv
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.still_download_iv:
                downloadPhoto();
                break;
//            case R.id.still_share_iv:
//                sharePhoto();
//                break;
        }
    }

    /*private void sharePhoto() {
        Photo photo = mPhotos.get(mCurPos);
        String strTargetType;
        if (mTargetType == 0) {
            strTargetType = ShareView.SHARE_TYPE_PERSON_IMAGE;
        } else if (mTargetType == 1) {
            strTargetType = ShareView.SHARE_TYPE_MOVIE_IMAGE;
        } else {
            strTargetType = ShareView.SHARE_TYPE_CINEMA_IMAGE;
        }

        String id = "0";
        if (null != photo && ConvertHelper.toInt(photo.getId()) > 0) {
            id = photo.getId();
        }

        ShareView view = new ShareView(getActivity());
        view.setValues(id, strTargetType, null, null, null);
        view.showActionSheet();
    }*/

    private void downloadPhoto() {
        Photo photo = mPhotos.get(mCurPos);
        Acp.getInstance(getActivity()).request(new AcpOptions.Builder()
                        .setPermissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ).build(),
                new AcpListener() {
                    @Override
                    public void onGranted() {
                        Acp.getInstance(getActivity()).onDestroy();
                        MtimeUtils.downLoadImgFromNet(getActivity(), photo.getImage());
                    }

                    @Override
                    public void onDenied(List<String> permissions) {
                        Acp.getInstance(getActivity()).onDestroy();
                        MToastUtils.showShortToast("读取SD卡权限拒绝");
                    }
                });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        int realPos = position % mPhotos.size();
        updatePos(realPos);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    class DetailAdapter extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener {

        @Override
        public int getCount() {
            return CollectionUtils.size(mPhotos) == 1 ? 1 : Integer.MAX_VALUE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int realPos = position % mPhotos.size();
            Photo photo = mPhotos.get(realPos);

            View v = getLayoutInflater().inflate(R.layout.photo_list_detail_viewpager_item,
                    container, false);

            PhotoView imageView = v.findViewById(R.id.image);
            ProgressBar progressBar = v.findViewById(R.id.loading);

            imageView.setOnViewTapListener(this);

            CoilCompat.INSTANCE.loadGifImageCallback(
                    photo.image,
                    0,
                    0,
                    false,
                    true,
                    new ImageCallback() {
                        @Override
                        public void onStart(@Nullable Drawable placeholder) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(@Nullable Drawable error) {
                            imageView.setImageResource(R.drawable.default_image);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onSuccess(@Nullable Drawable drawable) {
                            imageView.setImageDrawable(drawable);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );
            container.addView(v);
            return v;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public void onViewTap(View view, float v, float v1) {
            finish();
        }
    }
}
