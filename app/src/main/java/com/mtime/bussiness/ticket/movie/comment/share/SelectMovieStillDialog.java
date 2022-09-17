package com.mtime.bussiness.ticket.movie.comment.share;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.comment.bean.MovieStillBean;
import com.mtime.frame.BaseFrameUIDialogFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-25
 */
public class SelectMovieStillDialog extends BaseFrameUIDialogFragment {

    private ArrayList<MovieStillBean> mMovieStills;
    private String mSelectedUrl;

    @Override
    public int getTheme() {
        return R.style.ViewsBottomDialog;
    }

    void setStills(ArrayList<MovieStillBean> movieStills, String selectedUrl) {
        Bundle args = new Bundle();
        args.putParcelableArrayList("movieStills", movieStills);
        args.putString("selectedUrl", selectedUrl);
        setArguments(args);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_select_movie_still_layout;
    }

    @BindView(R.id.movie_stills)
    RecyclerView mRecyclerView;

    private StillAdapter mAdapter;
    private int oldPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieStills = getArguments().getParcelableArrayList("movieStills");
        mSelectedUrl = getArguments().getString("selectedUrl", "");
    }

    @Override
    protected float getDimAmount() {
        return 0.2f;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOnStillChanged = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.addItemDecoration(new ItemPlace());
        mRecyclerView.setItemAnimator(null);
        mAdapter = new StillAdapter(requireContext());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean scrolling = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    scrolling = true;
                } else {
                    if (scrolling) {
                        if (mOnStillChanged != null) {
                            mOnStillChanged.onStillScrolled();
                        }
                    }
                    scrolling = false;
                }
            }
        });
    }

    private void onStillClick(int position) {
        MovieStillBean bean = mMovieStills.get(position);
        if (TextUtils.equals(bean.stillUrl, mSelectedUrl)) {
            return;
        }
        mSelectedUrl = bean.stillUrl;
        mAdapter.notifyItemChanged(oldPosition);
        mAdapter.notifyItemChanged(position);
        if (mOnStillChanged != null) {
            mOnStillChanged.onStillChanged(mSelectedUrl, position);
        }
    }

    private OnStillChanged mOnStillChanged;

    public void setOnStillChanged(OnStillChanged onStillChanged) {
        mOnStillChanged = onStillChanged;
    }

    interface OnStillChanged {
        void onStillChanged(String url, int pos);

        void onStillScrolled();
    }

    class StillAdapter extends RecyclerView.Adapter<StillAdapter.StillHolder> {

        private final Context mContext;
        private final LayoutInflater mInflater;

        public StillAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public StillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.dialog_select_movie_still_item_layout, parent, false);
            return new StillHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StillHolder holder, int position) {
            MovieStillBean bean = mMovieStills.get(position);
            ImageHelper.with(mContext, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                    .load(bean.stillUrl)
                    .view(holder.stillIv)
                    .showload();
            if (TextUtils.equals(bean.stillUrl, mSelectedUrl)) {
                oldPosition = position;
                holder.check.setVisibility(View.VISIBLE);
            } else {
                holder.check.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mMovieStills.size();
        }

        class StillHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.movie_still_iv)
            ImageView stillIv;
            @BindView(R.id.checked_iv)
            View check;

            StillHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onStillClick(getAdapterPosition());
            }
        }
    }

    @OnClick(R.id.ok)
    void onOkClick() {
        dismiss();
    }

    private static class ItemPlace extends RecyclerView.ItemDecoration {

        private final int dp10 = MScreenUtils.dp2px(10);

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int count = parent.getAdapter().getItemCount();
            int position = parent.getChildAdapterPosition(view);
            if (position < count - 1) {
                outRect.right = dp10;
            }
        }
    }
}
