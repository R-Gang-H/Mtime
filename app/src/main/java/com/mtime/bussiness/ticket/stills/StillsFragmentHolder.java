package com.mtime.bussiness.ticket.stills;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.beans.Photo;
import com.mtime.util.JumpUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-27
 */
class StillsFragmentHolder extends ContentHolder<ArrayList<Photo>> {

    StillsFragmentHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
    }

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    private StillAdapter mAdapter;

    private int mType;

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.fragment_movie_stills_layout);
        ButterKnife.bind(this, mRootView);

        mAdapter = new StillAdapter();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDataChanged(ArrayList<Photo> data) {
        super.onDataChanged(data);
        mAdapter.notifyDataSetChanged();
    }

    void setStillType(int type) {
        mType = type;
    }

    private class StillAdapter extends RecyclerView.Adapter<StillAdapter.StillHolder> {

        private final int size = (MScreenUtils.getScreenWidth() - MScreenUtils.dp2px(48)) / 3;

        @NonNull
        @Override
        public StillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.item_movie_stills_layout, parent, false);
            return new StillHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull StillHolder holder, int position) {
            Photo photo = mData.get(position);
            ImageHelper.with(getActivity(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT)
                    .override(size, size)
                    .roundedCorners(4, 0)
                    .load(photo.image)
                    .placeholder(R.drawable.default_image)
                    .view(holder.iv)
                    .showload();
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }

        private class StillHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final ImageView iv;

            private StillHolder(View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onStillClick(getAdapterPosition());
            }
        }
    }

    private void onStillClick(int pos) {
        JumpUtil.startPhotoDetailActivity(getActivity(), mType, mData, pos);
    }
}
