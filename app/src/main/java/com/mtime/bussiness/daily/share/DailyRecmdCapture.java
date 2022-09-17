package com.mtime.bussiness.daily.share;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotlin.android.image.ImageCallback;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.util.QRCUtils;
import com.mtime.util.ViewCapture;

import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-25
 */
public class DailyRecmdCapture extends ViewCapture {

    public DailyRecmdCapture(ViewGroup parent) {
        super(parent, R.layout.capture_recmd_share_layout);
    }

    @BindView(R.id.recmd_share_poster_iv)
    ImageView mCapPoster;
    @BindView(R.id.recmd_share_title_tv)
    TextView mCapTitle;
    @BindView(R.id.recmd_share_subtitle_tv)
    TextView mCapSubtitle;
    @BindView(R.id.recmd_share_qr_iv)
    ImageView mQr;

    private boolean mQrShow = true;

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        ButterKnife.bind(this, view);
    }

    public boolean isQrShow() {
        return mQrShow;
    }

    public void qrShow(boolean show) {
        mQrShow = show;
        if (show) {
            mQr.setVisibility(View.VISIBLE);
        } else {
            mQr.setVisibility(View.GONE);
        }
    }

    public void fillData(DailyRecommendBean bean, ImageCallback callback) {
        CoilCompat.INSTANCE.loadGifImageCallback(
                bean.poster,
                DimensionExtKt.getDp(330),
                DimensionExtKt.getDp(410),
                true,
                false,
                new ImageCallback() {
                    @Override
                    public void onStart(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onError(@Nullable Drawable error) {
                        MToastUtils.showShortToast("图片加载失败");
                        if (null != callback) {
                            callback.onError(error);
                        }
                    }

                    @Override
                    public void onSuccess(@Nullable Drawable drawable) {
                        if (null != callback) {
                            callback.onSuccess(drawable);
                        }
                        mCapPoster.setImageDrawable(drawable);
                        doCapture();
                    }
                }
        );
        mCapTitle.setText(bean.rcmdQuote);
        mCapSubtitle.setText(bean.desc);

        if (TextUtils.isEmpty(bean.movieDetailURL)) {
            mQr.setImageResource(R.drawable.icon_download_qr_code);
        } else {
            int size = MScreenUtils.dp2px(65);
            Bitmap bitmap = QRCUtils.createQRImage(bean.movieDetailURL, size, size, Color.BLACK, Color.WHITE);
            mQr.setImageBitmap(bitmap);
        }
    }
}
