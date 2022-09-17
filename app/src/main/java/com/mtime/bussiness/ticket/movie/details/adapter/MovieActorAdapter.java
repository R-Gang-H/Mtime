package com.mtime.bussiness.ticket.movie.details.adapter;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.bean.Person;
import com.mtime.mtmovie.widgets.RoundAngleImageView;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/5/22
 * @desc 影片演职员页Adapter
 */
public class MovieActorAdapter extends BaseQuickAdapter<Person, BaseViewHolder> {

    public MovieActorAdapter(@Nullable List<Person> data) {
        super(R.layout.item_movie_actor, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Person bean) {
        // 图片
        RoundAngleImageView imageView = holder.getView(R.id.item_movie_actor_head_iv);
        ImageHelper.with(getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT)
                .override(MScreenUtils.dp2px(65), MScreenUtils.dp2px(97))
                .view(imageView)
                .load(bean.getImage())
                .placeholder(R.drawable.default_image)
                .showload();
        // 中英文名
        if(TextUtils.isEmpty(bean.getName())) {
            holder.setText(R.id.item_movie_actor_name_tv, bean.getNameEn());
            holder.setGone(R.id.item_movie_actor_name_en_tv, true);
        } else {
            holder.setText(R.id.item_movie_actor_name_tv, bean.getName());
            holder.setText(R.id.item_movie_actor_name_en_tv, bean.getNameEn());
            holder.setGone(R.id.item_movie_actor_name_en_tv, false);
        }
        // 角色
        if(TextUtils.isEmpty(bean.getPersonate())) {
            holder.setGone(R.id.item_movie_actor_personate_tv, true);
        } else {
            holder.setGone(R.id.item_movie_actor_personate_tv, false);
            holder.setText(R.id.item_movie_actor_personate_tv, getContext().getResources().getString(R.string.actor_detail_hot_playing_act, bean.getPersonate()));
        }
        // 分隔线
        holder.setVisible(R.id.item_movie_actor_line_view, !bean.isGroupEnd());
    }
}
