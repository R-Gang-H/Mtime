package com.mtime.bussiness.daily.dialog;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kotlin.android.image.ImageCallback;
import com.kotlin.android.share.ShareType;
import com.kotlin.android.share.entity.ShareEntity;
import com.kotlin.android.share.ext.ShareExtKt;
import com.kotlin.android.share.ui.ShareFragment;
import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.daily.recommend.api.RecmdApi;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.daily.share.DailyRecmdCapture;
import com.mtime.frame.BaseFrameUIDialogFragment;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.dailyrecmd.StatisticDailyRecmd;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;
import com.mtime.util.ViewCapture;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-25
 * 每日推荐弹框
 */
public class RecmdDialog extends BaseFrameUIDialogFragment implements ViewCapture.CaptureCallback<DailyRecmdCapture>, StatisticDailyRecmd {

    private static final String KEY_OF_DATA = "key_of_data";

    private final RecmdApi mApi = new RecmdApi();

    @Override
    public int getLayoutRes() {
        return R.layout.dlg_daily_recmd;
    }

    @Override
    public int getTheme() {
        return R.style.DailyRecmdDialog;
    }

    @BindView(R.id.iv_dlg_daily_poster)
    ImageView mPoster;
    @BindView(R.id.dlg_recmd_share_title_tv)
    TextView mTitle;
    @BindView(R.id.dlg_recmd_share_subtitle_tv)
    TextView mSubTitle;
    @BindView(R.id.dlg_daily_recmd_dlg_sign_tv)
    TextView mSignView;

    private DailyRecommendBean mRecommendBean;
    private DailyRecmdCapture mCapture;
    private String mMovieId;

    private int mPosterW;
    private int mPosterH;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseStatisticHelper.setPageLabel(DAILY_RCMD_POPUP);
        mBaseStatisticHelper.setSubmit(true);
        // R.style.DailyRecmdDialog 中定义了 宽度为 80% 所以这里 用 0.8
        int dialogW = (int) (MScreenUtils.getScreenWidth() * 0.8f);
        // 10 是因为布局里 左右各有 5 的间距
        mPosterW = dialogW - MScreenUtils.dp2px(10);
        mPosterH = (int) (mPosterW * 385f / 310);
    }

    public void fillData(DailyRecommendBean bean) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_OF_DATA, bean);
        setArguments(args);
    }

    @Override
    public void bindView(View view) {
        ButterKnife.bind(this, view);
        DailyRecommendBean bean = getArguments().getParcelable(KEY_OF_DATA);
        mRecommendBean = bean;
        mMovieId = bean.movieId;

        mCapture = new DailyRecmdCapture((ViewGroup) view);
        mCapture.setCaptureCallback(this);
        ImageHelper.with(getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(mPosterW, mPosterH)
                .load(bean.poster)
                .view(mPoster)
                .showload();
        mTitle.setText(bean.rcmdQuote);
        mSubTitle.setText(bean.desc);

        if (bean.canPlay()) {
            Drawable play = getContext().getResources().getDrawable(R.drawable.icon_recmd_can_play);
            play.setBounds(0, 0, play.getIntrinsicWidth(), play.getIntrinsicHeight());
            mSubTitle.setCompoundDrawables(null, null, play, null);
        } else {
            mSubTitle.setCompoundDrawables(null, null, null, null);
        }
        Map<String, String> param = new HashMap<>(1);
        param.put(MOVIE_ID, mMovieId);
        StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, SHOW,
                null, null, null, param);
        StatisticManager.getInstance().submit(assemble);

        //当前用户签到上报
        if (UserManager.Companion.getInstance().isLogin()) {
            mSignView.setVisibility(View.VISIBLE);
            mApi.userSign();
        }
    }

    @Override
    public void onCaptured(DailyRecmdCapture capture, final Bitmap b) {
//        ShareView sv = new ShareView(getActivity());
//        sv.setScreenshotBitmap(b);
//        sv.setValues("", ShareView.SHARE_TYPE_MOVIE_RATE, null, null, null);
//        sv.showActionSheet();
//        sv.setListener(new ShareView.IShareViewEventListener() {
//            @Override
//            public void onEvent(ShareView.ShareViewEventType type) {
//                b.recycle();
//            }
//        });
        StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, SHARE,
                null, null, null, null);
        StatisticManager.getInstance().submit(assemble);

        if (null != getActivity()) {
            ShareEntity shareEntity = new ShareEntity();
            shareEntity.setShareType(ShareType.SHARE_IMAGE);
            shareEntity.setImage(b);
            ShareExtKt.showShareDialog(
                    getActivity(),
                    shareEntity,
                    ShareFragment.LaunchMode.STANDARD,
                    true, null);
        }
    }

    @OnClick({
            R.id.iv_daily_recmd_dlg_share,
            R.id.iv_daily_recmd_dlg_calendar,
            R.id.iv_dialy_recmd_close,
            R.id.iv_dlg_daily_poster
    })
    void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.iv_dlg_daily_poster: {
                Map<String, String> param = new HashMap<>(1);
                param.put(MOVIE_ID, mMovieId);
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, SHOW,
                        null, null, null, param);
                StatisticManager.getInstance().submit(assemble);
                JumpUtil.startMovieInfoActivity(getContext(), assemble.toString(), mMovieId, 0);
                dismiss();
                break;
            }
            case R.id.iv_daily_recmd_dlg_share:
                UIUtil.showLoadingDialog(getActivity());
                mCapture.fillData(mRecommendBean, new ImageCallback() {
                    @Override
                    public void onStart(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onError(@Nullable Drawable error) {
                        UIUtil.dismissLoadingDialog();
                    }

                    @Override
                    public void onSuccess(@Nullable Drawable drawable) {
                        UIUtil.dismissLoadingDialog();
                    }
                });
                mCapture.capture();
                break;
            case R.id.iv_daily_recmd_dlg_calendar: {
                StatisticPageBean assemble = assemble(DAILY_RCMD_MOVIE, null, CALENDAR,
                        null, null, null, null);
                StatisticManager.getInstance().submit(assemble);
                JumpUtil.jumpHistoryRecommend(getContext(), assemble.toString());
            }
            case R.id.iv_dialy_recmd_close:
                dismiss();
                break;
        }
    }

    @Override
    public boolean shouldCancelOnTouchOutside() {
        return false;
    }
}
