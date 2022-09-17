package com.mtime.mtmovie.widgets;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.beans.Relation;
import com.mtime.bussiness.ticket.movie.activity.ActorViewActivity;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;

import java.util.List;

/**
 * 发现-新闻- 点击“相关电影/影人”时显示
 * 
 */
public class FindNewsAboutMoviePersonDialog extends Dialog {
    
    private final BaseActivity        context;
    private final List<Relation>      beans;

    public FindNewsAboutMoviePersonDialog(BaseActivity context, int theme, List<Relation> beans) {
        super(context, theme);
        this.context = context;
        this.beans = beans;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_news_aboutmovieperson_dialog);
        getWindow().setLayout(FrameConstant.SCREEN_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageButton close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        LinearLayout layout = findViewById(R.id.layout_aboutmovieperson);
        layout.removeAllViews();
        for (int i = 0; i < beans.size(); i++) {
            View itemView = View.inflate(context, R.layout.about_movie_item, null);
            layout.addView(itemView);
            TextView textName = itemView.findViewById(R.id.news_name);
            TextView textYear = itemView.findViewById(R.id.news_year);
            ScoreView scoreLabel = itemView.findViewById(R.id.news_score);
            ImageView imageTitle = itemView.findViewById(R.id.news_img);
            final Relation relation = beans.get(i);
            textName.setText(relation.getName());
            
            if (relation.getType() == 2) {// 影人不显示评分
                scoreLabel.setVisibility(View.GONE);
            }
            else {
                if (relation.getRating() > 0) {
                    scoreLabel.setVisibility(View.VISIBLE);
                    scoreLabel.setText(String.valueOf(relation.getRating()));
                }
                else {
                    scoreLabel.setVisibility(View.GONE);
                }
            }
            if (relation.getType() == 1 && !TextUtils.isEmpty(relation.getReleaseDate())) {// 影片显示年代
                if (relation.getReleaseDate().length() >= 4) {
                    textYear.setText(String.format("(%s)", relation.getReleaseDate().substring(0, 4)));
                }
            }

            context.volleyImageLoader.displayImage(relation.getImage(), imageTitle, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    if (relation.getType() == 2) {
                        final Intent intent = new Intent();
                        intent.putExtra(App.getInstance().KEY_MOVIE_PERSOM_ID, String.valueOf(relation.getId()));
                        context.startActivity(ActorViewActivity.class, intent);
                    }
                    else {
                        JumpUtil.startMovieInfoActivity(context, context.assemble().toString(), String.valueOf(relation.getId()), 0);
                    }
                }
            });
        }
        
    }
    
    public void showActionSheet() {
        if (this != null) {
            Window window = getWindow();
            // 设置显示动画
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            DisplayMetrics metric = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metric);
            wl.y = metric.heightPixels;
            
            // 设置显示位置
            this.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            this.setCanceledOnTouchOutside(true);
            this.show();
        }
    }
    
}
