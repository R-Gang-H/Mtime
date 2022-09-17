package com.mtime.mtmovie.widgets.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.RefreshTrigger;
import com.mtime.R;

/**
 * 下拉刷新的布局
 */
public class PullRefreshHeaderView extends RelativeLayout implements RefreshTrigger {
    private final ImageView ivArrow;
    private final TextView tvRefresh;
    private final ProgressBar progressBar;
    private final TextView mAdName;
    private final TextView mAdWord;
    private final Animation rotateUp;
    private final Animation rotateDown;
    private boolean rotated = false;
    private int mHeight;

    public PullRefreshHeaderView(Context context) {
        this(context, null);
    }

    public PullRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_irecyclerview_pull_refresh_header_view, this);
        tvRefresh = findViewById(R.id.tvRefresh);
        ivArrow = findViewById(R.id.ivArrow);
        progressBar = findViewById(R.id.progressbar);
        mAdName = findViewById(R.id.pull_to_refresh_ad_name);
        mAdWord = findViewById(R.id.pull_to_refresh_ad_word);
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.rotate_up);
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.rotate_down);
    }

    @Override
    public void onStart(boolean automatic, int headerHeight, int finalHeight) {
        this.mHeight = headerHeight;
        progressBar.setIndeterminate(false);
        //临时先去掉电影、名言
        /*if (App.getInstance().pullAdvwordList != null && App.getInstance().pullAdvwordList.size() > 0) {
            int n = 0;
            java.util.Random r = new java.util.Random();
            n = r.nextInt(App.getInstance().pullAdvwordList.size());
            PullRefreshFilmWord adv = App.getInstance().pullAdvwordList.get(n);
            setAD(adv.getMovieName(), adv.getWord());
        }*/
    }

    private void setAD(String movieName, String word) {
        if (null != mAdName && null != movieName && !"".equals(movieName)) {
            mAdName.setVisibility(GONE);
            mAdName.setText(movieName);
        }
        if (null != mAdWord && null != word && !"".equals(word)) {
            mAdWord.setVisibility(GONE);
            mAdWord.setText(word);
        }
    }

    @Override
    public void onMove(boolean isComplete, boolean automatic, int moved) {
        if (!isComplete) {
            ivArrow.setVisibility(VISIBLE);
            progressBar.setVisibility(GONE);
            if (moved <= mHeight) {
                if (rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateDown);
                    rotated = false;
                }
                tvRefresh.setText("下拉刷新");
            } else {
                tvRefresh.setText("释放刷新");
                if (!rotated) {
                    ivArrow.clearAnimation();
                    ivArrow.startAnimation(rotateUp);
                    rotated = true;
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        ivArrow.clearAnimation();
        ivArrow.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        tvRefresh.setText("正在载入...");
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        rotated = false;
        ivArrow.clearAnimation();
        ivArrow.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        tvRefresh.setText("刷新成功");
    }

    @Override
    public void onReset() {
        rotated = false;
        ivArrow.clearAnimation();
        ivArrow.setVisibility(GONE);
        progressBar.setVisibility(GONE);
    }
}
