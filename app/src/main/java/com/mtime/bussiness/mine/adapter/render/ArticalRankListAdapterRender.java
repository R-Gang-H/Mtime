package com.mtime.bussiness.mine.adapter.render;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;
import com.mtime.bussiness.mine.bean.OfficialAccountListBean;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.common.utils.Utils;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;

/**
 * Created by cong.zhang on 17/4/13.
 */

public class ArticalRankListAdapterRender implements BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> {
    private final BaseActivity context;
    private final OfficialAccountListBean.ListBeanX bean;
    private final MRecyclerViewTypeExtraHolder holder;
    private final int totalCount;

    public ArticalRankListAdapterRender(BaseActivity context, OfficialAccountListBean.ListBeanX bean, int totalCount) {
        this.context = context;
        this.bean = bean;
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_rank_list, null);
        holder = new MRecyclerViewTypeExtraHolder(view);
        this.totalCount = totalCount;
    }

    @Override
    public void fitDatas(final int position) {
        ImageView imgPoster = holder.obtainView(R.id.img_poster);
        View rlLast = holder.obtainView(R.id.rl_last);
        TextView tvCount = holder.obtainView(R.id.tv_count);
        TextView tvFilter = holder.obtainView(R.id.tv_filter);

        // 最后一条
        if (position == totalCount - 1) {
            tvCount.setText(String.valueOf(bean.getTopList().getTotal()));
            imgPoster.setVisibility(View.GONE);
            rlLast.setVisibility(View.VISIBLE);
            rlLast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转详情页
                    JumpUtil.startArticleActivity(context, null, String.valueOf(bean.getArticleId()), String.valueOf(bean.getArticleType()), ""/*rcmdedId*/, ""/*rcmdedType*/);
                }
            });
        } else {
            imgPoster.setVisibility(View.VISIBLE);
            rlLast.setVisibility(View.GONE);
            String url;
            if (bean.getTopList().getRelatedType() == 1) {
                url = bean.getTopList().getList().get(position).getPosterUrl();
            } else {
                url = bean.getTopList().getList().get(position).getPosterUrl();
            }

            imgPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent();
                    if (bean.getTopList().getRelatedType() == 1) {
                        JumpUtil.startMovieInfoActivity(context, context.assemble().toString(),
                                String.valueOf(bean.getTopList().getList().get(position).getRelatedId()), 0);
                    } else {
                        intent.putExtra(App.getInstance().KEY_MOVIE_PERSOM_ID,
                                String.valueOf(bean.getTopList().getList().get(position).getRelatedId()));
                        context.startActivityForResult(ActorViewActivity.class, intent);
                    }
                }
            });
            context.volleyImageLoader.displayImage(url, imgPoster, R.drawable.default_image, R.drawable.default_image,
                    Utils.dip2px(context, 100), Utils.dip2px(context, 150), ImageURLManager.FIX_WIDTH_OR_HEIGHT, null);
        }
    }

    @Override
    public MRecyclerViewTypeExtraHolder getReusableComponent() {
        return holder;
    }

    @Override
    public void fitEvents() {
    }
}
