package com.mtime.bussiness.mine.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.CompanyMovieBean;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.util.ImageURLManager;

import java.util.List;

/**
 * Created by ZhangCong on 15/10/26.
 */
public class CompanyDetailAdapter extends RecyclerView.Adapter<CompanyDetailAdapter.ViewHolder> {
    BaseActivity context;
    private final List<CompanyMovieBean> movies;
    private OnItemClickListener onItemClickListener;

    public CompanyDetailAdapter(final BaseActivity context, final List<CompanyMovieBean> movies) {
        this.context = context;
        this.movies = movies;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.company_movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.textName.setText(movies.get(position).getName());
        holder.textNameEn.setText(movies.get(position).getNameEn());

        final String director = movies.get(position).getDirector();
        final String actor = movies.get(position).getActor1() + " " + movies.get(position).getActor2();
        final String releaseDate = movies.get(position).getReleaseDate();
        holder.textDirector.setText("导演：" + ConvertHelper.toString(director, "--"));
        holder.textStarring.setText("主演：" + ConvertHelper.toString(actor, "--"));
        holder.textTime.setText("上映日期：" + ConvertHelper.toString(releaseDate, "--") + " ");
        holder.textLocation.setText(movies.get(position).getReleaseArea());

        double ratingD = 0;
        if(!TextUtils.isEmpty(movies.get(position).getRating())) {
            ratingD = Double.parseDouble(movies.get(position).getRating());
        }
        if (ratingD > 0) {
            float num = (float) (Math.round(ratingD * 10)) / 10;
            if (num == 10) {
                holder.scoreLabel.setText("10");
            } else if (num > 10 || num < 0) {
                holder.scoreLabel.setVisibility(View.GONE);
            } else {
                holder.scoreLabel.setText(String.valueOf(num));
            }
            holder.scoreLabel.setVisibility(View.VISIBLE);
        } else {
            holder.scoreLabel.setText("");
            holder.scoreLabel.setVisibility(View.GONE);
        }

        context.volleyImageLoader.displayImage(movies.get(position).getImg(), holder.imgItem, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.LARGE, null);
        if(position == 0){
            holder.textYear.setText(String.valueOf(movies.get(0).getYear()));
            holder.company_top.setVisibility(View.VISIBLE);
        } else if(movies.get(position).getYear() != movies.get(position-1).getYear()){
            holder.textYear.setText(String.valueOf(movies.get(position).getYear()));
            holder.company_top.setVisibility(View.VISIBLE);
        } else {
            holder.company_top.setVisibility(View.GONE);
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                    onItemClickListener.onItemClick(holder.itemView, holder.getIAdapterPosition());
                }
            });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends IViewHolder {
        TextView textYear;
        LinearLayout company_top;
        TextView textName;
        TextView textNameEn;
        TextView textDirector;
        TextView textStarring;
        TextView textTime;
        TextView textLocation;
        TextView scoreLabel;
        ImageView imgItem;

        public ViewHolder(View itemView) {
            super(itemView);
            textYear = itemView.findViewById(R.id.company_movie_year);
            textName = itemView.findViewById(R.id.company_movie_name);
            textNameEn = itemView.findViewById(R.id.company_movie_nameen);
            textDirector = itemView.findViewById(R.id.company_movie_director);
            textStarring = itemView.findViewById(R.id.company_movie_starring);
            textTime = itemView.findViewById(R.id.company_movie_time);
            textLocation = itemView.findViewById(R.id.company_movie_location);
            scoreLabel = itemView.findViewById(R.id.company_movie_score);
            imgItem = itemView.findViewById(R.id.company_movie_img);
            company_top = itemView.findViewById(R.id.company_movie_top);
        }
    }
}
