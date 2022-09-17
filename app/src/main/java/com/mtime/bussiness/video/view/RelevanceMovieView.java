package com.mtime.bussiness.video.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageLoadOptions;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.information.bean.ArticleRelatedMoviesBean;
import com.mtime.frame.App;
import com.mtime.mtmovie.widgets.PosterFilterView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * @author zhuqiguang
 * @date 2018/7/4
 * website www.zhuqiguang.cn
 * 关联电影view
 */
public class RelevanceMovieView extends RelativeLayout {
    @BindView(R.id.layout_player_article_relevance_cover_iv)
    PosterFilterView mCoverIv;
    @BindView(R.id.layout_player_article_relevance_name_tv)
    TextView mNameTv;
    @BindView(R.id.layout_player_article_relevance_play_tv)
    TextView mCanPlayTv;
    @BindView(R.id.layout_player_article_relevance_type_tv)
    TextView mTypeTv;
    @BindView(R.id.layout_player_article_relevance_time_tv)
    TextView mTimeTv;
    @BindView(R.id.layout_player_article_relevance_sale_tv)
    TextView mSaleTv;
    private ArticleRelatedMoviesBean mRelatedMovieBean;

    public RelevanceMovieView(Context context) {
        super(context);
    }

    public RelevanceMovieView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelevanceMovieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_player_article_relevance_movie, this);
        ButterKnife.bind(this, view);
    }

    public void refreshRelatedMovies(ArticleRelatedMoviesBean relatedMovie) {
        if (onRelevanceCallback != null) {
            onRelevanceCallback.onRelatedMovieShow();
        }
        mRelatedMovieBean = relatedMovie;
        mCoverIv.setPosterFilter(relatedMovie.isFilter());
        ImageHelper.with(getContext(),
                ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .load(relatedMovie.getImg())
                .override(MScreenUtils.dp2px(80), MScreenUtils.dp2px(120))
                .placeholder(R.drawable.default_image)
                .view(mCoverIv)
                .showload();
        if (TextUtils.isEmpty(relatedMovie.getName())) {
            mNameTv.setText(relatedMovie.getNameEn());
        } else {
            mNameTv.setText(relatedMovie.getName());
        }
        if (!TextUtils.isEmpty(relatedMovie.getMovieType())) {
            mTypeTv.setText(relatedMovie.getMovieType());
        }
        if (TextUtils.equals(relatedMovie.getIsPlay(), ArticleRelatedMoviesBean.CAN_PLAY)) {
            mCanPlayTv.setVisibility(View.VISIBLE);
        } else if (TextUtils.equals(relatedMovie.getIsPlay(), ArticleRelatedMoviesBean.NOT_PLAY)) {
            mCanPlayTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(relatedMovie.getReleaseDate())) {
            mTimeTv.setText(relatedMovie.getReleaseDate());
        }
        switch (relatedMovie.getBuyTicketStatus()) {
            case ArticleRelatedMoviesBean.TICKET_STATE_NOT:
                mSaleTv.setVisibility(View.GONE);
                break;
            case ArticleRelatedMoviesBean.TICKET_STATE_NORMAL:
                mSaleTv.setBackgroundResource(R.drawable.bg_ff8600);
                mSaleTv.setText(getContext().getResources().getString(R.string.st_buy));
                break;
            case ArticleRelatedMoviesBean.TICKET_STATE_PRESALE:
                mSaleTv.setText(getContext().getResources().getString(R.string.str_home_presell));
                break;
        }
    }

    @Optional
    @OnClick({
            R.id.layout_player_article_relevance_play_tv,
            R.id.layout_player_article_relevance_sale_tv,
            R.id.layout_player_article_movie_root_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_player_article_relevance_play_tv:
                if (onRelevanceCallback != null) {
                    onRelevanceCallback.onRelatedMovieIconClick();
                    onRelevanceCallback.onRelatedMovieAllClick();
                }
                break;
            case R.id.layout_player_article_relevance_sale_tv:
                if (onRelevanceCallback == null) {
                    return;
                }
                onRelevanceCallback.onRelatedMovieAllClick();
                if (mRelatedMovieBean.getBuyTicketStatus() == ArticleRelatedMoviesBean.TICKET_STATE_NORMAL) {
                    onRelevanceCallback.onRelatedMovieTicketClick();
                } else if (mRelatedMovieBean.getBuyTicketStatus() == ArticleRelatedMoviesBean.TICKET_STATE_PRESALE) {
                    onRelevanceCallback.onRelatedMovieReservationClick();
                }
                break;
            case R.id.layout_player_article_movie_root_rl:
                if (onRelevanceCallback != null) {
                    onRelevanceCallback.onRelatedMovieAllClick();
                }
                break;
            default:
                break;
        }
    }

    private OnRelevanceCallback onRelevanceCallback;

    public void setOnItemCallback(OnRelevanceCallback onRelevanceCallback) {
        this.onRelevanceCallback = onRelevanceCallback;
    }

    public interface OnRelevanceCallback {

        void onRelatedMovieAllClick();

        void onRelatedMovieIconClick();

        void onRelatedMovieTicketClick();

        void onRelatedMovieReservationClick();

        void onRelatedMovieShow();
    }
}
