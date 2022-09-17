package com.mtime.bussiness.ticket.movie.adapter;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.Award;
import com.mtime.bussiness.ticket.movie.bean.FilmographyBean;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.util.ImageURLManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 演员-作品年表Adapter
 * 
 */
public class ActorFilmographyYearListAdapter extends RecyclerView.Adapter<ActorFilmographyYearListAdapter.ViewHolder> {

    private final int             MIN_AWARD_LINES = 2;                               // 获奖作品显示的最大行数
    private final int             MAX_AWARD_LINES = 100;

    private final BaseActivity          context;
    private final List<FilmographyBean> filmOgraphyList = new ArrayList<FilmographyBean>();
    private OnItemClickListener onItemClickListener;

    public ActorFilmographyYearListAdapter(final BaseActivity context, final List<FilmographyBean> filmOgraphys) {
        this.context = context;

        if (null != filmOgraphys) {
            this.filmOgraphyList.addAll(filmOgraphys);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFilmographyList(final List<FilmographyBean> filmographys) {
        filmOgraphyList.clear();
        filmOgraphyList.addAll(filmographys);
        notifyDataSetChanged();
    }

    public void addFilmographyList(final List<FilmographyBean> filmographys) {
        filmOgraphyList.addAll(filmographys);
        notifyDataSetChanged();
    }

    private void mergeDatas(List<FilmographyBean> filmographys){

    }

    public List<FilmographyBean> getFilmographyList() {
        return filmOgraphyList;
    }

    public int getCount() {
        return filmOgraphyList.size();
    }

    public Object getItem(final int position) {
        return 0 == filmOgraphyList.size() ? null : filmOgraphyList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_actor_filmography, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FilmographyBean filmography = filmOgraphyList.get(position);
        holder.name.setText(filmography.getName());

        if (position == 0) {
            holder.item_actor_filmgraphy_year.setVisibility(View.VISIBLE);
        } else if (position > 0) {
            if (filmography.getYear().equalsIgnoreCase(filmOgraphyList.get(position - 1).getYear())) {
                holder.item_actor_filmgraphy_year.setVisibility(View.GONE);
            } else {
                holder.item_actor_filmgraphy_year.setVisibility(View.VISIBLE);
            }
        }
        holder.item_actor_filmgraphy_year_text.setText(filmography.getYear());

        // 评分
        if (TextUtils.isEmpty(filmography.getRating())) {
            holder.score.setVisibility(View.INVISIBLE);
        }
        else {
            holder.score.setVisibility(View.VISIBLE);
            holder.score.setText(filmography.getRating());
        }

        // 职责（导演/编剧/演员）
        StringBuffer sb = new StringBuffer();
        String office = filmography.getOfficesString();
        if (!TextUtils.isEmpty(office)) {
            sb.append(office);
            sb.append("\n");
        }

        String personate = filmography.getPersonateString();
        if (!TextUtils.isEmpty(personate)) {
            sb.append(context.getResources().getString(R.string.s_actor));
            sb.append(personate);
        }

        if (sb.length() > 0) {
            holder.actors.setText(sb.toString());
            holder.actors.setVisibility(View.VISIBLE);
        }
        else {
            holder.actors.setVisibility(View.GONE);
        }

        // 获奖记录，果没有“获奖记录”，则不显示
        List<Award> awards = filmography.getAwards();
        if (null == awards || 0 == awards.size()) {
            holder.awardsHolder.setVisibility(View.GONE);
        }
        else {
            holder.awardsHolder.setVisibility(View.VISIBLE);

            final StringBuilder sb1 = new StringBuilder();
            sb1.append(context.getString(R.string.st_awards_record));

            for (int i = 0; i < awards.size() - 1; i++) {
                Award award = awards.get(i);
                sb1.append(award.getEventName());
                sb1.append("（");
                sb1.append(award.getYear());
                sb1.append("）");
                sb1.append(" - ");
                sb1.append(award.getAwardName());
                sb1.append("\n");
                sb1.append("\n");
            }

            Award item = awards.get(awards.size() - 1);
            sb1.append(item.getEventName());
            sb1.append("（");
            sb1.append(item.getYear());
            sb1.append("）");
            sb1.append(" - ");
            sb1.append(item.getAwardName());

            final String str = sb1.toString();
            holder.awards.setText(str);

            holder.awards.post(new Runnable() {

                @Override
                public void run() {

                    if (holder.awards.getLineCount() > MIN_AWARD_LINES) {
                        holder.awards.setMaxLines(MIN_AWARD_LINES);
                        holder.expand.setVisibility(View.VISIBLE);
                        holder.expand_mark.setVisibility(View.VISIBLE);
                        holder.awardsHolder.setClickable(true);

                        holder.awardsHolder.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                if ("全部".equalsIgnoreCase(holder.expand.getText().toString())) {
                                    Drawable nav_up = ContextCompat.getDrawable(context,
                                            R.drawable.actor_film_ography_unexpand_icon);
                                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                                    holder.expand.setCompoundDrawables(null, null, nav_up, null);

                                    holder.awards.setMaxLines(MAX_AWARD_LINES); // 无穷大
                                    holder.expand.setText("收起");
                                }
                                else {
                                    Drawable nav_up = ContextCompat.getDrawable(context,
                                            R.drawable.actor_film_ography_expand_icon);
                                    nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
                                    holder.expand.setCompoundDrawables(null, null, nav_up, null);
                                    holder.awards.setMaxLines(MIN_AWARD_LINES);
                                    holder.expand.setText("全部");
                                }
                            }
                        });
                    }
                    else {
                        holder.awards.setMaxLines(MIN_AWARD_LINES);
                        holder.expand.setVisibility(View.GONE);
                        holder.expand_mark.setVisibility(View.GONE);
                        holder.awardsHolder.setClickable(false);
                    }
                }
            });
        }

        context.volleyImageLoader.displayImage(filmography.getImage(), holder.header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);

        if (position == this.filmOgraphyList.size() - 1) {
            holder.line.setVisibility(View.INVISIBLE);
        }
        else {
            holder.line.setVisibility(View.VISIBLE);
        }
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱\
                    onItemClickListener.onItemClick(holder.itemView, holder.getIAdapterPosition());
                }
            });
        }
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return filmOgraphyList.size();
    }

    public class ViewHolder extends IViewHolder {
        ImageView header;      // 作品图片
        TextView  name;        // 作品名
        TextView  score;       // 作品评分
        TextView  actors;      // 饰演
        TextView  awards;      // 获奖记录
        TextView  expand;      // 下箭头
        View      expand_mark;
        View      awardsHolder; // 控制获奖记录的显示与否
        View      line;
        View      item_actor_filmgraphy_year;
        TextView  item_actor_filmgraphy_year_text;

        public ViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header);
            name = itemView.findViewById(R.id.movie_label);
            score = itemView.findViewById(R.id.movie_score);
            actors = itemView.findViewById(R.id.movie_actor);
            awards = itemView.findViewById(R.id.movie_awards);
            expand = itemView.findViewById(R.id.awards_expand);
            awardsHolder = itemView.findViewById(R.id.awards_show_view);
            expand_mark = itemView.findViewById(R.id.expand_mark);
            line = itemView.findViewById(R.id.seperate_line);
            item_actor_filmgraphy_year = itemView.findViewById(R.id.item_actor_filmgraphy_year);
            item_actor_filmgraphy_year_text = itemView.findViewById(R.id.groupto);
        }
    }
    
}
