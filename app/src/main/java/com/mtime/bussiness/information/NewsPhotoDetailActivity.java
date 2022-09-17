package com.mtime.bussiness.information;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.app.data.entity.community.album.AlbumUpdate;
import com.kotlin.android.image.ImageCallback;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.kotlin.android.app.router.liveevent.event.DeleteCommentState;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.widget.dialog.BaseDialog;
import com.mtime.R;
import com.mtime.base.imageload.ImageLoadOptions;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.api.CommonApi;
import com.mtime.frame.BaseActivity;
import com.mtime.util.MtimeUtils;
import com.mtime.widgets.photoview.CustomClickListener;
import com.mtime.widgets.photoview.PhotoView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.DELETE_ALBUM_IMAGE;

/**
 * 新闻图片详情页面
 */
@Route(path = RouterActivityPath.Main.PAGE_PHOTO_DETAIL)
public class NewsPhotoDetailActivity extends BaseActivity {

    public static final String INTENT_PHOTO_LIST_DATA = "intent_photo_list_data";
    private static final String INTENT_NEWSID = "intent_news_id";
    public static final String INTENT_PHOTO_LIST_POSITION_CLICKED = "intent_photo_list_data_postion_clicked";
    public static final String INTENT_FROM = "intent_from";
    private static final String INTENT_FROM_REVIEW = "intent_from_review";

    public static final String INTENT_ALBUM_ID = "intent_album_id";
    public static final String INTENT_ALBUM_USER_ID = "intent_album_user_id";
    public static final String INTENT_IMAGEID_LIST = "intent_album_image_id_list";


    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer, ArrayList<String> urlList, String newsId, int itemClicked, int from, boolean isFromReview) {
        Intent launcher = new Intent(context, NewsPhotoDetailActivity.class);
        launcher.putExtra(INTENT_PHOTO_LIST_DATA, urlList);
        launcher.putExtra(INTENT_NEWSID, newsId);
        launcher.putExtra(INTENT_PHOTO_LIST_POSITION_CLICKED, itemClicked);
        launcher.putExtra(INTENT_FROM, from);
        launcher.putExtra(INTENT_FROM_REVIEW, isFromReview);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }


    private ViewPager viewPager;
    private ImageView imgDownload;
    private ImageView imgShare;
    private ImageView imgDelete;
    private TextView titleTv;
    private ArrayList<String> urlList;
    private int item;
    private int listSize;
    private String newsId;
    private boolean isFromReview = false;
    private int from = 2;//1:文章 2:新闻

    private boolean mFailed = false;
    private boolean mGif = false;
    private final CommonApi commonApi = new CommonApi();

    private long albumId = 0L;//相册id
    private long albumUserId = 0L;//相册所有人id
    private ArrayList<Long> imageIdList;//相册中图片id


    @Override
    protected void onInitVariable() {
        urlList = getIntent().getStringArrayListExtra(INTENT_PHOTO_LIST_DATA);
        newsId = getIntent().getStringExtra(INTENT_NEWSID);
        item = getIntent().getIntExtra(INTENT_PHOTO_LIST_POSITION_CLICKED, 0);
        from = getIntent().getIntExtra(INTENT_FROM, 2);
        isFromReview = getIntent().getBooleanExtra(INTENT_FROM_REVIEW, false);
        listSize = urlList.size();
        albumId = getIntent().getLongExtra(INTENT_ALBUM_ID, 0L);
        albumUserId = getIntent().getLongExtra(INTENT_ALBUM_USER_ID, 0L);
        long[] imageIdArray = getIntent().getLongArrayExtra(INTENT_IMAGEID_LIST);
        imageIdList = new ArrayList<>();
        if (imageIdArray != null) {//只有相册进入会用到
            for (long l : imageIdArray) {
                imageIdList.add(l);
            }
        }
//        this.mPageLabel = "newsPhotoDetail";

    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_photo_detal);
//        View navBar = findViewById(R.id.navigationbar);


        viewPager = findViewById(R.id.pager);
        imgDownload = findViewById(R.id.downloadIv);
        imgShare = findViewById(R.id.shareIv);
        imgDelete = findViewById(R.id.deleteIv);
        titleTv = findViewById(R.id.titleTv);
        imgDelete.setVisibility((albumId != 0L && albumUserId != 0 && UserManager.Companion.getInstance().getUserId() == albumUserId) ? View.VISIBLE : View.GONE);

        //暂时先把分享功能去掉
        imgShare.setVisibility(View.GONE);
        /*imgShare.setVisibility(from == 1L ? View.GONE : View.VISIBLE);
        imgShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urlList == null || urlList.size() == 0) {
                    return;
                }

                String strTargetType;
                if (isFromReview) {
                    strTargetType = ShareView.SHARE_TYPE_MOVIE_COMMENT;
                } else {
                    strTargetType = ShareView.SHARE_TYPE_NEWS;
                }

                String imgUrl = null;
                if ((urlList != null) && (urlList.size() > item)) {
                    imgUrl = urlList.get(item);
                }

                ShareView shareView = new ShareView(NewsPhotoDetailActivity.this);
                shareView.setValues(newsId, strTargetType, null, null, imgUrl);
                shareView.showActionSheet();

            }
        });*/

//        从相册中删除图片
        imgDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem >= imageIdList.size()) {
                    return;
                }

                new BaseDialog.Builder(NewsPhotoDetailActivity.this).setContent(R.string.delete_image_confirm).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteImage();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

            }
        });
    }

    private void deleteImage() {
        int currentItem = viewPager.getCurrentItem();
        commonApi.postDeleteImageFromAlbum(imageIdList.get(currentItem), albumId, new NetworkManager.NetworkListener<AlbumUpdate>() {
            @Override
            public void onSuccess(AlbumUpdate result, String showMsg) {
                if (result.getResult()) {//删除成功
                    LiveEventBus.get(DELETE_ALBUM_IMAGE).post(new DeleteCommentState(imageIdList.get(currentItem), false));
                    urlList.remove(currentItem);
                    imageIdList.remove(currentItem);
                    viewPager.getAdapter().notifyDataSetChanged();
                    viewPager.setCurrentItem(currentItem + 1);
                    int totalCount = viewPager.getAdapter().getCount();
                    titleTv.setText((currentItem + 1 >= totalCount ? totalCount : currentItem + 1) + "/" + totalCount);
                    listSize = totalCount;
                    if (totalCount <=0){
                        MToastUtils.showShortToast("图片删除成功");
                        finish();
                    }
                } else {
                    MToastUtils.showShortToast("图片删除失败");
                }
            }

            @Override
            public void onFailure(NetworkException<AlbumUpdate> exception, String showMsg) {
                MToastUtils.showShortToast("图片删除失败");
            }
        });
    }

    @Override
    protected void onInitEvent() {

        titleTv.setText(item + 1 + "/" + listSize);
        viewPager.setAdapter(new myPagerView());
        viewPager.setCurrentItem(item);

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(final int arg0) {

                item = arg0;
                titleTv.setText(item + 1 + "/" + listSize);
            }

            @Override
            public void onPageScrolled(final int arg0, final float arg1, final int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(final int arg0) {

            }
        });
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (urlList == null || urlList.isEmpty()) {
                    return;
                }
                if (mFailed) {
                    MToastUtils.showShortToast("图片下载失败");
                    return;
                }
                String url = urlList.get(item);
                // 危险权限：运行时请求
                Acp.getInstance(NewsPhotoDetailActivity.this).request(new AcpOptions.Builder()
                                .setPermissions(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ).build(),
                        new AcpListener() {
                            @Override
                            public void onGranted() {
                                Acp.getInstance(getApplicationContext()).onDestroy();
                                MtimeUtils.downLoadImgFromNet(NewsPhotoDetailActivity.this, url, mGif);
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                Acp.getInstance(getApplicationContext()).onDestroy();
                                MToastUtils.showShortToast("读取SD卡权限拒绝");
                            }
                        });
            }
        };

        imgDownload.setOnClickListener(clickListener);

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {

    }

    @Override
    protected void onUnloadData() {

    }

    class myPagerView extends PagerAdapter {

        private final LayoutInflater inflater;

        myPagerView() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            if (urlList != null) {
                return urlList.size();
            }
            return 0;
        }

        private String getUrl(int index) {
            if (urlList != null) {
                return urlList.get(index);
            }
            return null;
        }

        @Override
        public boolean isViewFromObject(final View arg0, final Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(final Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(final View container, final int position, final Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public Object instantiateItem(final View view, final int position) {
            final View imageLayout = inflater.inflate(R.layout.photo_list_detail_viewpager_item, null);
            final PhotoView imageView = imageLayout.findViewById(R.id.image);
            final ProgressBar progressBar = imageLayout.findViewById(R.id.loading);

            progressBar.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.default_image);

            String url = ImageProxyUrl.createProxyUrl(getUrl(position), new ImageLoadOptions.ImageSize(DimensionExtKt.getScreenWidth(), DimensionExtKt.getScreenHeight()),
                    ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT);
            CoilCompat.INSTANCE.loadGifImageCallback(url, 0, 0, false, true, new ImageCallback() {
                @Override
                public void onStart(@Nullable Drawable placeholder) {

                }

                @Override
                public void onSuccess(@org.jetbrains.annotations.Nullable Drawable drawable) {
                    mGif = drawable instanceof GifDrawable;
                    progressBar.setVisibility(View.GONE);
                    imageView.setImageDrawable(drawable);
                }

                @Override
                public void onError(@org.jetbrains.annotations.Nullable Drawable error) {
                    progressBar.setVisibility(View.GONE);
                    mFailed = true;
                }
            });

            imageView.setCustomClickListener(new CustomClickListener() {

                @Override
                public void onEvent() {
                    finish();
                }
            });

            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

    }

}
