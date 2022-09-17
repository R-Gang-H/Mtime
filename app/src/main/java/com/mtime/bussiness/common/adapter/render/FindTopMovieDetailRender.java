package com.mtime.bussiness.common.adapter.render;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.adapter.render.base.BaseAdapterTypeRender;
import com.mtime.adapter.render.base.MRecyclerViewTypeExtraHolder;
import com.mtime.bussiness.common.adapter.FindTopMovieCommonRecyclerAdapter;
import com.mtime.bussiness.common.bean.TopMovy;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.common.utils.TextUtil;
import com.mtime.util.ImageURLManager;
import com.mtime.widgets.ExpandableTextView;
import com.mtime.widgets.NetworkImageView;

/**
 * 榜单列表页
 * Created by yinguanping on 16/7/4.
 */

public class FindTopMovieDetailRender implements BaseAdapterTypeRender<MRecyclerViewTypeExtraHolder> {
    private final BaseActivity context;
    private final FindTopMovieCommonRecyclerAdapter adapter;
    private final MRecyclerViewTypeExtraHolder holder;
    private final SparseBooleanArray mConvertTextCollapsedStatus = new SparseBooleanArray();

    public FindTopMovieDetailRender(BaseActivity context, FindTopMovieCommonRecyclerAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        View view = LayoutInflater.from(context).inflate(R.layout.topmovie_item, null);
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
        TopMovy listBean = adapter.getBean().getMovies().get(position);
        TextView textNum = holder.obtainView(R.id.topmovie_num, TextView.class);
        TextView textName = holder.obtainView(R.id.topmovie_name, TextView.class);
        TextView textNameEn = holder.obtainView(R.id.topmovie_nameen, TextView.class);
        TextView textDirector = holder.obtainView(R.id.text_director, TextView.class);
        TextView textStaring = holder.obtainView(R.id.text_staring, TextView.class);
        TextView scoreLabel = holder.obtainView(R.id.topmovie_score, TextView.class);
        TextView texttime = holder.obtainView(R.id.text_time, TextView.class);
        ExpandableTextView textInfo = holder.obtainView(R.id.text_info, ExpandableTextView.class);
        TextView textlocation = holder.obtainView(R.id.text_location, TextView.class);
        NetworkImageView imgItem = holder.obtainView(R.id.topmovie_img, NetworkImageView.class);
        LinearLayout monnyLin = holder.obtainView(R.id.monny_lin, LinearLayout.class);
        TextView textweekMonny = holder.obtainView(R.id.weekmonny_text, TextView.class);
        TextView textTotalMonny = holder.obtainView(R.id.totalmonny_text, TextView.class);

        textNum.setText(listBean.getFormatNum());
        textName.setText(listBean.getName());
        textNameEn.setText(listBean.getNameEn());

        final String director = listBean.getDirector();
        final String actor = listBean.getActor();
        final String releaseDate = listBean.getReleaseDate();
        textDirector.setText("导演：" + ConvertHelper.toString(director, "--"));
        textStaring.setText("主演：" + ConvertHelper.toString(actor, "--"));
        texttime.setText("上映日期：" + ConvertHelper.toString(releaseDate, "--") + " ");
        if (listBean.getWeekBoxOffice() != null && !listBean.getWeekBoxOffice().equals("")
                && listBean.getTotalBoxOffice() != null && !listBean.getTotalBoxOffice().equals("")) {
            monnyLin.setVisibility(View.VISIBLE);
            textInfo.setVisibility(View.GONE);
            textweekMonny.setText(listBean.getWeekBoxOffice().replace("\n", " "));
            textTotalMonny.setText(listBean.getTotalBoxOffice().replace("\n", " "));
        }
        else {
            textInfo.setVisibility(View.VISIBLE);
            monnyLin.setVisibility(View.GONE);
            textInfo.setConvertText(mConvertTextCollapsedStatus,position,listBean.getRemark());
        }

        if (listBean.getRating() > 0) {
            float num = (float) (Math.round(listBean.getRating() * 10)) / 10;
            if (num == 10) {
                scoreLabel.setText("10");
            }
            else if (num > 10 || num < 0) {
                scoreLabel.setVisibility(View.GONE);
            }
            else {
                scoreLabel.setText(String.valueOf(num));
            }
            scoreLabel.setVisibility(View.VISIBLE);
        }
        else {
            scoreLabel.setText("");
            scoreLabel.setVisibility(View.GONE);
        }
        if (TextUtil.stringIsNotNull(listBean.getReleaseLocation())) {
            textlocation.setText(listBean.getReleaseLocation());
        }
        context.volleyImageLoader.displayNetworkImage(context.volleyImageLoader, listBean.getPosterUrl(), imgItem, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.LARGE, null);

        if (listBean.getRankNum() == 1) {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numa);
        }
        else if (listBean.getRankNum() == 2) {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numb);
        }
        else if (listBean.getRankNum() == 3) {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numc);
        }
        else {
            textNum.setBackgroundResource(R.drawable.topmovie_list_numd);
        }
    }
}
