package com.mtime.bussiness.ticket.movie.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.bussiness.ticket.movie.bean.ShareImagesBean;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.mtmovie.widgets.CircleImageView;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageLoader;
import com.mtime.util.ImageURLManager;
import com.mtime.util.VolleyError;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



/**
 * Created by guangshun on 17/2/27.
 * 分享影片评分评论
 */

public class ShareCommentView extends RelativeLayout implements View.OnClickListener {
    /**
     * 图片圆角规则
     */
    public enum HalfType {
        LEFT, // 左上角 + 左下角
        RIGHT, // 右上角 + 右下角
        TOP, // 左上角 + 右上角
        BOTTOM, // 左下角 + 右下角
        ALL // 四角
    }

    private final Context context;
    private ImageView largeImage;
    private TextView nextPicture;
    private TextView movieNameCN;
    private TextView movieNameEN;
    private RatingBar rbscore;
    private TextView score;
    private TextView comment;
    private ImageView blur;
    private CircleImageView headImage;
    private RelativeLayout title;//标题区域，截图时要把它去掉
    private TextView boottomTXT;//底部的文字，只有截图上出现，正常页面上不显示
    private LinearLayout llShare;//分享界面
    private ShareImagesBean shareImagesBean;
    private ShareData shareData = new ShareData();
    private final Random random = new Random();
    private final DecimalFormat scoreFormat = new DecimalFormat("0.0");

    private final ImageLoader volleyImageLoader = new ImageLoader();
//    private ShareView shareView;
    private int curIndex = -1;

    private Activity activity;

    public ShareCommentView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ShareCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setActivity(final Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        boottomTXT.setVisibility(GONE);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_share_comment, this);

        shareData = new ShareData();
        largeImage = findViewById(R.id.iv_image);
        movieNameCN = findViewById(R.id.tv_name_cn);
        movieNameEN = findViewById(R.id.tv_name_en);
        comment = findViewById(R.id.tv_comment);
        blur = findViewById(R.id.iv_blur);
        nextPicture = findViewById(R.id.bt_next_picture);
        headImage = findViewById(R.id.iv_head);
        rbscore = findViewById(R.id.rb_score);
        score = findViewById(R.id.tv_score);
        title = findViewById(R.id.rl_tilte);
        llShare = findViewById(R.id.ll_share_part);
        findViewById(R.id.share_weichat_world).setOnClickListener(this);
        findViewById(R.id.share_qq_friend).setOnClickListener(this);
        findViewById(R.id.share_weixin_friend).setOnClickListener(this);
        findViewById(R.id.share_weibo_sina).setOnClickListener(this);
        boottomTXT = findViewById(R.id.tv_bottom_txt);
        boottomTXT.setVisibility(GONE);

        String userAvatar = UserManager.Companion.getInstance().getUserAvatar();
        if (!TextUtils.isEmpty(userAvatar)) {
            volleyImageLoader.displayImage(userAvatar, null, Utils.dip2px(context, 50), Utils.dip2px(context, 50), ImageURLManager.SCALE_TO_FIT, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (null != response.getBitmap()) {
                        headImage.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

        }

        nextPicture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPicture();
            }
        });
        findViewById(R.id.iv_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(GONE);
            }
        });
    }

    //获取截图
    private Bitmap getScreenshotBitmap() {
        llShare.setVisibility(GONE);
        boottomTXT.setVisibility(VISIBLE);
        nextPicture.setVisibility(GONE);
        title.setVisibility(GONE);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        layout(getLeft(), getTop(), getRight(),
                getBottom());
        draw(canvas);
        if (shareData.getImages().size() < 2) {
            nextPicture.setVisibility(GONE);
        } else {
            nextPicture.setVisibility(VISIBLE);
        }
        title.setVisibility(VISIBLE);
        boottomTXT.setVisibility(GONE);
        llShare.setVisibility(VISIBLE);
        return bitmap;
    }

    //设置影片ID，影片中英文数据
    public void setBasicData(final String movieId, String nameCN, String nameEN) {
        if (null == shareData) {
            shareData = new ShareData();
        }
//        if (null == shareView && context instanceof Activity) {
//            shareView = new ShareView(activity);
//            LocationHelper.location(getContext().getApplicationContext(), new OnLocationCallback() {
//                @Override
//                public void onLocationSuccess(LocationInfo locationInfo) {
//                    if(null != locationInfo) {
//                        shareView.setValues(movieId, ShareView.SHARE_TYPE_MOVIE_RATE, locationInfo.getCityId(), null, null);
//                    }
//                }
//
//                @Override
//                public void onLocationFailure(LocationException e) {
//                    onLocationSuccess(LocationHelper.getDefaultLocationInfo());
//                }
//            });
//        }
        shareData.setMovieData(movieId, nameCN, nameEN);
        if (!TextUtils.isEmpty(shareData.getMovieNameCN())) {
            movieNameCN.setText(shareData.getMovieNameCN());
        }
        if (!TextUtils.isEmpty(shareData.getMovieNameEN())) {
            movieNameEN.setText(shareData.getMovieNameEN());
        }
    }

    public void setCommentData(float scoreValue, String commentValue) {
        if (null == shareData) {
            shareData = new ShareData();
        }
        shareData.setScore(scoreValue);
        shareData.setComment(commentValue);
        rbscore.setRating(shareData.getScore() / 2);
        comment.setText(shareData.getComment());
        if (shareData.getScore() < 0.1) {
            findViewById(R.id.ll_content_score).setVisibility(GONE);
            return;
        } else {
            findViewById(R.id.ll_content_score).setVisibility(VISIBLE);
        }
        score.setText(String.format("评%s分", scoreFormat.format(new BigDecimal(String.valueOf(shareData.getScore())))));
    }

    private void nextPicture() {
        if (shareData.getImages().size() > 0) {
            int width = largeImage.getMeasuredWidth() != 0 ? largeImage.getMeasuredWidth() : FrameConstant.SCREEN_WIDTH - Utils.dip2px(context, 44);
            int height = largeImage.getMeasuredHeight() != 0 ? largeImage.getMeasuredHeight() : Utils.dip2px(context, 250);
            int index = random.nextInt(shareData.getImages().size());
            if (index == curIndex) {
                nextPicture();
            } else {
                curIndex = index;
                if (!TextUtils.isEmpty(shareData.getImages().get(curIndex).getStillUrl())) {
//                    volleyImageLoader.displayImage(shareData.getImages().get(curIndex).getStillUrl(), largeImage, width, height, ImageURLManager.SCALE_TO_FIT, null);
                    ImageHelper.with(context, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                            .override(width, height)
                            .view(largeImage)
                            .roundedCorners(30, 0)
                            .load(shareData.getImages().get(curIndex).getStillUrl())
                            .showload();
                    volleyImageLoader.displayBlurImg(context, shareData.getImages().get(curIndex).getStillUrl(), blur, 25, 10, R.drawable.default_image, R.drawable.default_image, null);
                }
            }
        }
    }

    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            if (0 == shareData.getImages().size() && !shareData.isEmptyValue()) {
                requestMovieImages();
            }
        }
    }

    public void requestMovieImages() {
        Map<String, String> param1 = new HashMap<>(1);
        param1.put("movieId", shareData.getMovieId());
        HttpUtil.get(ConstantUrl.GET_SHARE_IMAGES, param1, ShareImagesBean.class, new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                volleyImageLoader.displayBlurImg(context, R.drawable.default_image, blur, 25, 10, R.drawable.default_image, R.drawable.default_image, null);
            }

            @Override
            public void onSuccess(Object o) {
                ShareImagesBean shareImagesBean = (ShareImagesBean) o;
                if (null != shareImagesBean) {
                    shareData.setMovieNameCN(shareImagesBean.getName());
                    shareData.setMovieNameEN(shareImagesBean.getNameEn());
                    shareData.setShareImagesBean(shareImagesBean);
                    if (!TextUtils.isEmpty(shareData.getMovieNameCN())) {
                        movieNameCN.setText(shareData.getMovieNameCN());
                    }
                    if (!TextUtils.isEmpty(shareData.getMovieNameEN())) {
                        movieNameEN.setText(shareData.getMovieNameEN());
                    }
                    nextPicture();
                    if (shareData.getImages().size() < 2) {
                        nextPicture.setVisibility(GONE);
                    } else {
                        nextPicture.setVisibility(VISIBLE);
                    }

                }

            }
        }, 180000);
    }


    private class ShareData {
        private String movieId;
        private String movieNameCN;
        private String movieNameEN;
        private float score;
        private String comment;
        private ShareImagesBean shareImagesBean;

        public void setMovieData(String movieId, String movieNameCN, String movieNameEN) {
            this.movieId = movieId;
            this.movieNameCN = movieNameCN;
            this.movieNameEN = movieNameEN;
        }

        public void setMovieId(String movieId) {
            this.movieId = movieId;
        }

        public void setMovieNameCN(String movieNameCN) {
            this.movieNameCN = movieNameCN;
        }

        public void setMovieNameEN(String movieNameEN) {
            this.movieNameEN = movieNameEN;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getMovieId() {
            return movieId;
        }

        public String getMovieNameCN() {
            return movieNameCN;
        }

        public String getMovieNameEN() {
            return movieNameEN;
        }

        public String getComment() {
            return comment;
        }

        public float getScore() {
            return score;
        }

        public boolean isEmptyValue() {
            return TextUtils.isEmpty(movieId);
        }

        public void setShareImagesBean(ShareImagesBean shareImagesBean) {
            this.shareImagesBean = shareImagesBean;
        }

        public ShareImagesBean getShareImagesBean() {
            return shareImagesBean;
        }

        public List<ShareImagesBean.MovieStillsBean> getImages() {
            if (null != shareImagesBean && null != shareImagesBean.getMovieStills()) {
                return shareImagesBean.getMovieStills();
            }
            return new ArrayList<>();
        }
    }

    //将图片的四角圆弧化,这块的功能已经迁移到XCRoundRectImageView类了
    private Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels, HalfType half) {
        if (null == bitmap) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap roundConcerImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建一个和原始图片一样大小的位图
        Canvas canvas = new Canvas(roundConcerImage);//创建位图画布
        Paint paint = new Paint();//创建画笔

        Rect rect = new Rect(0, 0, width, height);//创建一个和原始图片一样大小的矩形
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);// 抗锯齿

        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);//画一个基于前面创建的矩形大小的圆角矩形
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置相交模式
        canvas.drawBitmap(bitmap, null, rect, paint);//把图片画到矩形去

        switch (half) {
            case LEFT:
                return Bitmap.createBitmap(roundConcerImage, 0, 0, width - roundPixels, height);
            case RIGHT:
                return Bitmap.createBitmap(roundConcerImage, width - roundPixels, 0, width - roundPixels, height);
            case TOP: // 上半部分圆角化 “- roundPixels”实际上为了保证底部没有圆角，采用截掉一部分的方式，就是截掉和弧度一样大小的长度
                return Bitmap.createBitmap(roundConcerImage, 0, 0, width, height - roundPixels);
            case BOTTOM:
                return Bitmap.createBitmap(roundConcerImage, 0, height - roundPixels, width, height - roundPixels);
            case ALL:
                return roundConcerImage;
            default:
                return roundConcerImage;
        }
    }


    @Override
    public void onClick(View view) {
//        if (null != shareView) {
//            shareView.setScreenshotBitmap(getScreenshotBitmap());
//            shareView.onClick(view);
//        }

    }
}
