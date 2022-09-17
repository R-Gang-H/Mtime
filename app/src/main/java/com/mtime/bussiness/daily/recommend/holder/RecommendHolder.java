package com.mtime.bussiness.daily.recommend.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.image.ImageCallback;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.share.ShareType;
import com.kotlin.android.share.entity.ShareEntity;
import com.kotlin.android.share.ext.ShareExtKt;
import com.kotlin.android.share.ui.ShareFragment;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.daily.recommend.adapter.DailyRecmdAdapter;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.daily.share.DailyRecmdCapture;
import com.mtime.bussiness.daily.widget.RcmdItemDecoration;
import com.mtime.bussiness.daily.widget.RecmdSnapHelper;
import com.mtime.bussiness.daily.widget.RecommendLayout;
import com.mtime.bussiness.daily.widget.VerticalTextView;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.splash.bean.PullRefreshFilmWord;
import com.mtime.util.FileUtils;
import com.mtime.util.MtimeUtils;
import com.mtime.util.UIUtil;
import com.mtime.util.ViewCapture;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-20
 */
public class RecommendHolder extends ContentHolder<List<DailyRecommendBean>>
        implements RecommendLayout.OpenPercentCallback, DailyRecmdAdapter.OnItemClickListener
        , ViewCapture.CaptureCallback<DailyRecmdCapture> {

    public static final int JUMP_HISTORY_RECOMMEND = 1;
    public static final int SHARE_RECMD = 2;
    public static final int DOWNLOAD_RECMD = 3;
    public static final int POSTER_SHOW = 4;
    public static final int POSTER_CLICK = 5;

    public RecommendHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
    }

    private Unbinder mUnbinder;

    @BindView(R.id.rl_recommend_root)
    RecommendLayout mRecommendRoot;
    @BindView(R.id.iv_recommend_bg)
    ImageView mBlurView;
    @BindView(R.id.ll_recommend_text_panel)
    View mRecommendTextPanel;
    @BindView(R.id.vtv_recommend_movie_content)
    VerticalTextView mVRecMovieWord;
    @BindView(R.id.vtv_recommend_movie_name)
    VerticalTextView mVRecMovieName;
    @BindView(R.id.recommend_content_rv)
    RecyclerView mRecContent;
    //    @BindView(R.id.recommend_close_iv)
//    View mClose;
    @BindView(R.id.ll_h_rec_text_panel)
    View mHRecTextPanel;
    @BindView(R.id.htv_recommend_movie_content)
    TextView mRecmdText;
    @BindView(R.id.htv_recommend_movie_name)
    TextView mSubRecmdText;
    @BindView(R.id.ll_bottom_btns_panel)
    View mBottomBtnsPanel;
    @BindView(R.id.iv_recommend_download)
    ImageView mCalendar;

    private LinearLayoutManager layoutManager;
    private DailyRecmdAdapter mAdapter;
    private int mLastPosition = -1;
    private List<PullRefreshFilmWord> mMovieAdvlist;
    private int mMovieAdvIndex = 0;

    private DailyRecmdCapture mCapture;
    private boolean mIsCalendar = false;
    private Bundle mArgs;

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.act_daily_recommend);
        List<PullRefreshFilmWord> movieAdvlist = LoadManager.getMovieAdvlist();
        if (movieAdvlist == null || movieAdvlist.isEmpty()) {
            mMovieAdvlist = new ArrayList<>();
            PullRefreshFilmWord word = new PullRefreshFilmWord();
            word.setWord("让电影遇见生活");
            word.setMovieName("时光网");
            mMovieAdvlist.add(word);
        } else {
            mMovieAdvlist = movieAdvlist;
        }
        mUnbinder = ButterKnife.bind(this, mRootView);

        int dp15 = (int) convertToPx(UNIT_DIP, 15);
        int mPosterHeight = (int) (MScreenUtils.getScreenWidth() * 770f / 720);
        int mPosterWidth = (int) (mPosterHeight * 620f / 770);
        int padding = (MScreenUtils.getScreenWidth() - mPosterWidth - dp15) / 2;

        mRecContent.setPadding(padding, 0, padding, 0);

        mAdapter = new DailyRecmdAdapter(mContext, mPosterWidth, mPosterHeight);
        mRecommendRoot.setOpenPercentCallback(this);
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        mRecContent.setLayoutManager(layoutManager);
        final RecmdSnapHelper helper = new RecmdSnapHelper();
        helper.attachToRecyclerView(mRecContent);
        RcmdItemDecoration decoration = new RcmdItemDecoration();
        decoration.setItemOffsets(dp15);
        mRecContent.addItemDecoration(decoration);
        mRecContent.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View snapView = helper.findSnapView(layoutManager);
                    if (snapView == null) return;
                    int position = layoutManager.getPosition(snapView);
                    if (mLastPosition != position) {
                        onItemSelected(position);
                    }
                    mHRecTextPanel.setAlpha(1);
                    mBottomBtnsPanel.setAlpha(1);
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mHRecTextPanel.setAlpha(0);
                    mBottomBtnsPanel.setAlpha(0);
                }
            }
        });
        mRecContent.setAdapter(mAdapter);
        mRecommendRoot.setRecommendEnable(false);
        mAdapter.setOnItemClickListener(this);

        mCapture = new DailyRecmdCapture(mRecommendRoot);
        mCapture.setCaptureCallback(this);
        showFilmWord();
    }

    private void showFilmWord() {
        mMovieAdvIndex = mMovieAdvIndex % mMovieAdvlist.size();
        PullRefreshFilmWord filmWord = mMovieAdvlist.get(mMovieAdvIndex);
        mVRecMovieWord.setText(filmWord.getWord());
        mVRecMovieName.setText(filmWord.getMovieName());
    }

    private void onItemSelected(int position) {
        if (mLastPosition == position) return;
        mLastPosition = position;
        DailyRecommendBean bean = mData.get(position);
        CoilCompat.INSTANCE.loadBlurImage(
                mBlurView,
                bean.poster,
                0,
                0,
                false,
                0,
                20F,
                4F
        );
        mRecmdText.setText(bean.rcmdQuote);
        mSubRecmdText.setText(bean.desc);
        if (bean.canPlay()) {
            Drawable play = getResource().getDrawable(R.drawable.icon_recmd_can_play);
            play.setBounds(0, 0, play.getIntrinsicWidth(), play.getIntrinsicHeight());
            mSubRecmdText.setCompoundDrawables(null, null, play, null);
        } else {
            mSubRecmdText.setCompoundDrawables(null, null, null, null);
        }
        Bundle args = getArgs();
        args.putString("movieId", bean.movieId);
        sendHolderEvent(POSTER_SHOW, args);
    }

    private Bundle getArgs() {
        if (mArgs == null) {
            mArgs = new Bundle();
        }
        mArgs.clear();
        return mArgs;
    }

    @Override
    public void refreshView() {
        super.refreshView();
        mAdapter.setData(mData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @OnClick({
            R.id.recommend_close_iv,
            R.id.iv_recommend_download,
            R.id.iv_recommend_share
    })
    void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.recommend_close_iv:
                finish();
                break;
            case R.id.iv_recommend_download:
                downloadPic();
                break;
            case R.id.iv_recommend_share:
                gotoShare();
                break;
        }
    }

    private void downloadPic() {
        if (!mRecommendRoot.isClosed()) {
            return;
        }
        if (mBottomBtnsPanel.getAlpha() != 1) {
            return;
        }
        if (mIsCalendar) {
            sendHolderEvent(JUMP_HISTORY_RECOMMEND, null);
        } else {
            UIUtil.showLoadingDialog(mContext);
            mCapture.qrShow(false);
            mCapture.fillData(mData.get(mLastPosition), new ImageCallback() {
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
        }
    }

    private void gotoShare() {
        if (!mRecommendRoot.isClosed()) {
            return;
        }
        if (mBottomBtnsPanel.getAlpha() != 1) {
            return;
        }
        UIUtil.showLoadingDialog(mContext);
        mCapture.qrShow(true);
        mCapture.fillData(mData.get(mLastPosition), new ImageCallback() {
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
    }

    @Override
    public void onCaptured(DailyRecmdCapture capture, final Bitmap b) {
        if (capture.isQrShow()) { // share
//            ShareView sv = new ShareView(getActivity());
//            sv.setScreenshotBitmap(b);
//            sv.setValues("", ShareView.SHARE_TYPE_MOVIE_RATE, null, null, null);
//            sv.showActionSheet();
//            sv.setListener(new ShareView.IShareViewEventListener() {
//                @Override
//                public void onEvent(ShareView.ShareViewEventType type) {
//                    b.recycle();
//                }
//            });
            onShareScreenshotBitmap(b);
            sendHolderEvent(SHARE_RECMD, null);
        } else { // download
            String path = MtimeUtils.saveBitmapToInternal(mContext, b);
            b.recycle();
            if (FileUtils.savePhotoToLocal(getActivity(), path)) {
                MToastUtils.showLongToast(R.string.daily_recommend_pic_saved);
            } else {
                MToastUtils.showLongToast(R.string.daily_recommend_pic_save_failed);
            }
            sendHolderEvent(DOWNLOAD_RECMD, null);
        }
    }

    /**
     * 分享截屏图片
     * @param b
     */
    private void onShareScreenshotBitmap(final Bitmap b) {
        ShareEntity entity = new ShareEntity();
        entity.setShareType(ShareType.SHARE_IMAGE);
        entity.setImage(b);
        ShareExtKt.showShareDialog(
                getActivity(),
                entity,
                ShareFragment.LaunchMode.STANDARD,
                true,
                null);
    }

    private void gotoMovieDetail(String movieId) {
        if (!mRecommendRoot.isClosed()) {
            return;
        }
        Bundle args = getArgs();
        args.putString("movieId", movieId);
        sendHolderEvent(POSTER_CLICK, args);
    }

    @Override
    public void openPercent(float percent) {
        mRecommendTextPanel.setAlpha(percent);
//        mHRecTextPanel.setAlpha(1 - percent);
//        mBottomBtnsPanel.setAlpha(1 - percent);
        if (percent == 0) {
            mMovieAdvIndex++;
            showFilmWord();
        }
    }

    public void locatePosition(int pos) {
        if (pos < 0) pos = 0;
        if (pos >= mData.size()) pos = mData.size() - 1;
//        layoutManager.scrollToPosition(pos);
        onItemSelected(pos);
    }

    @Override
    public void onItemClick(int position) {
        String movieId = mData.get(position).movieId;
        gotoMovieDetail(movieId);
    }

    public void showCalendar() {
        mIsCalendar = true;
        mRecommendRoot.setRecommendEnable(true);
        mCalendar.setImageResource(R.drawable.ic_daily_calendar);
    }

    public boolean isCalendar() {
        return mIsCalendar;
    }
}
