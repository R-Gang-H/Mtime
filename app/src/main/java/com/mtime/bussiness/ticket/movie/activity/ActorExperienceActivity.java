package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.BaseActivity;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;
/**
 * 演艺经历
 */
public class ActorExperienceActivity extends BaseActivity implements ITitleViewLActListener {
    
    private class ExperienceAdapter extends BaseAdapter {
        
        private class ViewHolder {
            public TextView  year;
            public ImageView header;
            public TextView  title;
            public TextView  content;
        }
        
        private final BaseActivity        context;

        public ExperienceAdapter(BaseActivity context) {
            this.context = context;
        }
        
        @Override
        public int getCount() {
            return actorInfo.getExpriences().size();
        }
        
        @Override
        public Object getItem(int arg0) {
            return arg0;
        }
        
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }
        
        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            ViewHolder holder;
            if (null == arg1) {
                holder = new ViewHolder();
                arg1 = context.getLayoutInflater().inflate(R.layout.actor_experiences_list_item, null);
                holder.year = arg1.findViewById(R.id.year);
                holder.header = arg1.findViewById(R.id.header);
                holder.title = arg1.findViewById(R.id.title);
                holder.content = arg1.findViewById(R.id.content);
                arg1.setTag(holder);
            }
            else {
                holder = (ViewHolder) arg1.getTag();
            }

            if (0 == actorInfo.getExpriences().get(arg0).getYear()) {
                holder.year.setVisibility(View.INVISIBLE);
            } else {
                holder.year.setVisibility(View.VISIBLE);
                holder.year.setText(String.valueOf(actorInfo.getExpriences().get(arg0).getYear()));
            }

            context.volleyImageLoader.displayImage(actorInfo.getExpriences().get(arg0).getImg(), holder.header, R.drawable.default_image, R.drawable.default_image, FrameConstant.SCREEN_WIDTH,
                    FrameConstant.SCREEN_WIDTH * 2 / 3, null);
            
            holder.title.setText(actorInfo.getExpriences().get(arg0).getTitle());
            holder.content.setText(actorInfo.getExpriences().get(arg0).getContent());
            
            return arg1;
        }
        
    }
    
    private final int           DISTANCEX = 80;
    private final int           DISTANCEY = 60;
    
    private int                 startX    = 0;
    private int                 startY    = 0;
    
    public static ActorInfoBean actorInfo;
    
    private String              actorId;

    private static final String KEY_MOVIE_PERSOM_ID = "movie_person_id"; // 影人（导演/演员）id
    
    @Override
    protected void onInitEvent() {
    }
    
    @Override
    protected void onInitVariable() {
        actorId = getIntent().getStringExtra(KEY_MOVIE_PERSOM_ID);
        setPageLabel("performingExperience");
    }
    
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.actor_experiences);
        
        View root = this.findViewById(R.id.experience_title);
        new TitleOfNormalView(this, root, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, actorInfo.getNameCn(), this);
        
        ExperienceAdapter adapter = new ExperienceAdapter(this);
        ListView list = this.findViewById(R.id.list);
        list.setAdapter(adapter);
        
        list.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                final int action = arg1.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (0 == startX) {
                            startX = (int) arg1.getX();
                            startY = (int) arg1.getY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (0 == startX) {
                            startX = (int) arg1.getX();
                            startY = (int) arg1.getY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        final int endX = (int) arg1.getX();
                        final int endY = (int) arg1.getY();
                        if (DISTANCEX < (endX - startX) && DISTANCEY > Math.abs(endY - startY)) {
                            ActorExperienceActivity.this.finish();
                        }
                        
                        startX = 0;
                        startY = 0;
                        break;
                    default:
                        break;
                }
                
                return false;
            }
        });
    }
    
    @Override
    protected void onLoadData() {
    }
    
    @Override
    protected void onRequestData() {
    }
    
    @Override
    protected void onUnloadData() {
    }
    
    @Override
    public void onEvent(ActionType type, String content) {

    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     * @param personId
     */
    public static void launch(Context context, String refer, String personId) {
        Intent launcher = new Intent(context, ActorExperienceActivity.class);
        launcher.putExtra(KEY_MOVIE_PERSOM_ID, personId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
    
}
