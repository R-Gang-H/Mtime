package com.mtime.bussiness.common.adapter.render;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;
import com.mtime.bussiness.common.adapter.FindTopMovieCommonRecyclerAdapter;
import com.mtime.bussiness.common.bean.TopParser;
import com.mtime.util.ImageURLManager;
import com.mtime.widgets.NetworkImageView;

/**
 * 榜单列表页-影人详情
 * Created by yinguanping on 16/7/4.
 */

public class FindTopPersonDetailRender implements BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> {
    private final BaseActivity context;
    private final FindTopMovieCommonRecyclerAdapter adapter;
    private final MRecyclerViewTypeExtraHolder holder;

    public FindTopPersonDetailRender(BaseActivity context, FindTopMovieCommonRecyclerAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        View view = LayoutInflater.from(context).inflate(R.layout.topparsen_item, null);
        holder = new MRecyclerViewTypeExtraHolder(view);
    }

    @Override
    public MRecyclerViewTypeExtraHolder getReusableComponent() {
        return holder;
    }

    @Override
    public void fitEvents() {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getOnRecyclerViewListener().onItemClick(holder.getRealItemPosition());
            }
        });
    }

    @Override
    public void fitDatas(int position) {
        TopParser listBean = adapter.getBean().getPersons().get(position);

        NetworkImageView imgPhoto = holder.obtainView(R.id.topmovie_img, NetworkImageView.class);
        TextView textRemark = holder.obtainView(R.id.text_remark, TextView.class);
        TextView scoreLabel = holder.obtainView(R.id.topmovie_score, TextView.class);
        TextView textSummary = holder.obtainView(R.id.text_summary, TextView.class);
        TextView textEnName = holder.obtainView(R.id.topmovie_nameen, TextView.class);
        TextView textName = holder.obtainView(R.id.topmovie_name, TextView.class);
        TextView textNum = holder.obtainView(R.id.topmovie_num, TextView.class);

        textNum.setText(listBean.getFormatNum());
        float num = (float) (Math.round(listBean.getRating() * 10)) / 10;
        if (num == 10) {
            scoreLabel.setText("10");
        } else if (num > 10 || num < 0) {
            scoreLabel.setVisibility(View.GONE);
        } else {
            scoreLabel.setText(String.valueOf(num));
        }
        scoreLabel.setText(String.valueOf(listBean.getRating()));
        if (listBean.getNameCn() == null || "".equals(listBean.getNameCn().trim())) {
            // 没有中文名，则用英文名替代中文名，英文名位置 不显示内容。
            textName.setText(listBean.getNameEn());
            textEnName.setText("");
        } else {
            textName.setText(listBean.getNameCn());
            textEnName.setText(listBean.getNameEn());
        }
        TopParser parser = listBean;
        StringBuffer sb = new StringBuffer();
        if (parser.getSex() != null && !"".equals(parser.getSex())) {
            sb.append(parser.getSex());
        }
        if (parser.getBirthDay() != null && !"".equals(parser.getBirthDay())) {
            sb.append(",").append(parser.getBirthDay());
        }
        if (parser.getBirthLocation() != null && !"".equals(parser.getBirthLocation())) {
            if (parser.getBirthDay() == null || "".equals(parser.getBirthDay())) {
                sb.append(",");
            }
            sb.append("(").append(parser.getBirthLocation()).append(")");
        }

        textRemark.setText(sb);
        textSummary.setText(listBean.getSummary());

        if (listBean.getRankNum() == 1) {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numa);
        } else if (listBean.getRankNum() == 2) {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numb);
        } else if (listBean.getRankNum() == 3) {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numc);
        } else {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numd);
        }

        context.volleyImageLoader.displayNetworkImage(context.volleyImageLoader, listBean.getPosterUrl(), imgPhoto, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.LARGE, null);

    }
}
