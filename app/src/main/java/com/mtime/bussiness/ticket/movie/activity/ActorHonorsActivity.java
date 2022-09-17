package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.ActorAwards;
import com.mtime.bussiness.ticket.movie.bean.ActorFestivals;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.bussiness.ticket.movie.bean.ActorNominates;
import com.mtime.bussiness.ticket.movie.bean.ActorWinAwards;
import com.mtime.frame.BaseActivity;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.ScrollListView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.ArrayList;
import java.util.List;

public class ActorHonorsActivity extends BaseActivity {

    class HonorsSubListAdapter extends BaseAdapter {

        private final List<SubItemInfo> infos = new ArrayList<SubItemInfo>();

        private class ViewHolder {
            TextView honorLabel;
            TextView title;
            ImageView header;
            TextView content;
        }

        private final BaseActivity context;

        HonorsSubListAdapter(BaseActivity context, final List<SubItemInfo> infos) {
            this.context = context;
            if (null != infos) {
                this.infos.addAll(infos);
            }
        }

        @Override
        public int getCount() {
            return this.infos.size();
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
                arg1 = context.getLayoutInflater().inflate(R.layout.actor_honors_sublist_item, null);
                holder.honorLabel = arg1.findViewById(R.id.only_honor);
                holder.honorLabel.setVisibility(View.GONE);
                holder.title = arg1.findViewById(R.id.subitem_label);
                holder.header = arg1.findViewById(R.id.subitem_header);
                holder.content = arg1.findViewById(R.id.subitem_content);
                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            final SubItemInfo item = infos.get(arg0);
            if (TextUtils.isEmpty(item.movie) && TextUtils.isEmpty(item.role)) {
                holder.honorLabel.setText(item.label);
                holder.honorLabel.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.GONE);
                holder.header.setVisibility(View.GONE);
                holder.content.setVisibility(View.GONE);

                return arg1;
            }

            holder.honorLabel.setVisibility(View.GONE);
            holder.title.setVisibility(View.VISIBLE);
            holder.header.setVisibility(View.VISIBLE);
            holder.content.setVisibility(View.VISIBLE);

            holder.title.setText(item.label);

            context.volleyImageLoader.displayImage(item.image, holder.header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);
            String value;
            if (TextUtils.isEmpty(item.role)) {
                value = String.format("获奖影片：《%s》", item.movie);
            } else {
                value = String.format("获奖影片：《%s》\n饰演角色：%s", item.movie, item.role);
            }

            holder.content.setText(value);

            return arg1;
        }

    }

    private class HonorsAdapter extends BaseAdapter {

        class ViewHolder {
            View root;
            ImageView header;
            TextView namecn;
            TextView nameen;
            TextView summary;
            ImageView arrow;
            ScrollListView awardsList;
            ScrollListView nomateList;
            TextView nominateHeader;
            TextView awardsHeader;
            View lists_seperate;
        }

        private final BaseActivity context;

        public HonorsAdapter(BaseActivity context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return festivalInfos.size();
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
        public View getView(final int arg0, View arg1, ViewGroup arg2) {
            final ViewHolder holder;
            if (null == arg1) {
                holder = new ViewHolder();
                arg1 = this.context.getLayoutInflater().inflate(R.layout.actor_honors_listview_item, null);
                holder.root = arg1.findViewById(R.id.info);
                holder.arrow = arg1.findViewById(R.id.arrow);
                holder.awardsList = arg1.findViewById(R.id.list_honors);
                holder.header = arg1.findViewById(R.id.header);
                holder.namecn = arg1.findViewById(R.id.name_cn);
                holder.nameen = arg1.findViewById(R.id.name_en);
                holder.summary = arg1.findViewById(R.id.summary);
                holder.nomateList = arg1.findViewById(R.id.list_nomate);

                View header = context.getLayoutInflater().inflate(R.layout.actor_honors_sublist_header, null);
                holder.nominateHeader = header.findViewById(R.id.label);
                holder.nomateList.addHeaderView(header);

                holder.lists_seperate = arg1.findViewById(R.id.honor_nomate_seperate);

                View header1 = context.getLayoutInflater().inflate(R.layout.actor_honors_sublist_header, null);
                holder.awardsHeader = header1.findViewById(R.id.label);
                holder.awardsList.addHeaderView(header1);

                arg1.setTag(holder);
            } else {
                holder = (ViewHolder) arg1.getTag();
            }

            final FestivalInfo item = festivalInfos.get(arg0);
            // header 
            context.volleyImageLoader.displayImage(item.festivalImage, holder.header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.STANDARD, null);

            holder.namecn.setText(item.festivalNameCn);
            holder.nameen.setText(item.festivalNameEn);
            holder.summary.setText(item.festivalSummary);
            // init list view
            initSubListView(holder.awardsList, holder.awardsHeader, "获奖", item.awardsList);
            initSubListView(holder.nomateList, holder.nominateHeader, "提名", item.nominateList);
            holder.awardsList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (0 == arg2) {
                        return;
                    }

                    String id = item.awardsList.get(arg2 - 1).id;
                    if (TextUtils.isEmpty(id)||id.equalsIgnoreCase("0")) {
                        return;
                    }
                    JumpUtil.startMovieInfoActivity(ActorHonorsActivity.this, assemble().toString(), id, 0);
                }
            });

            holder.nomateList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    if (0 == arg2) {
                        return;
                    }
                    String id = item.nominateList.get(arg2 - 1).id;
                    if (TextUtils.isEmpty(id)||id.equalsIgnoreCase("0")) {
                        return;
                    }

                    JumpUtil.startMovieInfoActivity(ActorHonorsActivity.this, assemble().toString(), id, 0);
                }
            });

            updateUI(holder, item);
            holder.root.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    item.isExpanded = !item.isExpanded;
                    updateUI(holder, item);
                }
            });

            return arg1;
        }

        private void updateUI(final ViewHolder holder, final FestivalInfo item) {
            if (item.isExpanded) {
                if (item.winCount > 0) {
                    holder.awardsList.setVisibility(View.VISIBLE);
                    holder.lists_seperate.setVisibility(View.VISIBLE);
                } else {
                    holder.lists_seperate.setVisibility(View.GONE);
                }
                if (item.nominateCount > 0) {
                    holder.nomateList.setVisibility(View.VISIBLE);
                }

                holder.root.setBackgroundResource(R.drawable.actor_honors_background);
                holder.arrow.setBackgroundResource(R.drawable.actor_honors_arrow_up);
            } else {
                holder.root.setBackgroundResource(R.drawable.actor_honors_collpase_background);
                holder.arrow.setBackgroundResource(R.drawable.actor_honors_arrow_down);
                holder.awardsList.setVisibility(View.GONE);
                holder.nomateList.setVisibility(View.GONE);
                holder.lists_seperate.setVisibility(View.GONE);
            }
        }

        private void initSubListView(ScrollListView list, TextView headerView, String headerLabel, List<SubItemInfo> infos) {
            headerView.setText(headerLabel);
            if (null == infos || infos.isEmpty()) {
                list.setVisibility(View.GONE);
                return;
            }

            list.setVisibility(View.VISIBLE);
            HonorsSubListAdapter adapter = new HonorsSubListAdapter(context, infos);
            list.setAdapter(adapter);
        }
    }

    class SubItemInfo {
        String image;
        String label;
        String movie;
        String role;
        String id;
    }

    /*
     * list item used data
     */
    class FestivalInfo {
        boolean isExpanded;
        String festivalNameCn;
        String festivalNameEn;
        String festivalImage;
        String festivalSummary;
        int winCount;
        int nominateCount;

        List<SubItemInfo> awardsList;
        List<SubItemInfo> nominateList;
    }


    private final int DISTANCEX = 80;
    private final int DISTANCEY = 60;

    private int startX = 0;
    private int startY = 0;

    public static ActorInfoBean actorBean;//TODO  这种传值不太合理，需调整

    private String actorId;

    private ITitleViewLActListener listener;
    private final List<FestivalInfo> festivalInfos = new ArrayList<FestivalInfo>();

    private static final String KEY_MOVIE_PERSOM_ID = "movie_person_id"; // 影人（导演/演员）id

    @Override
    protected void onInitEvent() {
    }

    @Override
    protected void onInitVariable() {
        actorId = getIntent().getStringExtra(KEY_MOVIE_PERSOM_ID);

        listener = new ITitleViewLActListener() {

            @Override
            public void onEvent(ActionType type, String content) {

            }
        };

        setPageLabel("honor");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.actor_honors);

        View title = this.findViewById(R.id.honors_title);
        if (null != actorBean) {
            new TitleOfNormalView(this, title, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, actorBean.getNameCn(),
                    listener);
        }
        this.initFestivalInfos();
        ListView list = this.findViewById(R.id.list);

        View header = this.getLayoutInflater().inflate(R.layout.actor_honors_header, null);
        this.initHeader(header);
        list.addHeaderView(header);

        HonorsAdapter adapter = new HonorsAdapter(this);
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
                            finish();
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


    private void initHeader(final View header) {
        TextView headerLabel0 = header.findViewById(R.id.title_label0);
        TextView headerLabel = header.findViewById(R.id.title_label1);
        TextView headerLabel2 = header.findViewById(R.id.title_label2);
        TextView headerLabel3 = header.findViewById(R.id.title_label3);

        if (actorBean.getTotalWinAward() > 0) {
            headerLabel.setText(String.format("%d", actorBean.getTotalWinAward()));
        } else {
            headerLabel0.setText("曾获提名");
            headerLabel.setVisibility(View.GONE);
            headerLabel2.setVisibility(View.GONE);
        }

        if (actorBean.getTotalNominateAward() > 0) {
            headerLabel3.setText(String.format("%d", actorBean.getTotalNominateAward()));
        } else {
            headerLabel2.setVisibility(View.GONE);
            headerLabel3.setVisibility(View.GONE);
        }

    }

    private void initFestivalInfos() {
        if (null == actorBean.getFestivals() || actorBean.getFestivals().isEmpty()) {
            festivalInfos.clear();
            return;
        }
        boolean isExpanded = actorBean.getFestivals().size() <= 6;

        List<ActorFestivals> festivals=new ArrayList<ActorFestivals>();
        festivals.addAll(actorBean.getFestivals());

        if (null != actorBean.getAwards() && !actorBean.getAwards().isEmpty()) {
            for (int i = 0; i < actorBean.getAwards().size(); i++) {
                ActorAwards item = actorBean.getAwards().get(i);
                if (null == item || item.isEmpty()) {
                    continue;
                }
                FestivalInfo info = new FestivalInfo();
                info.isExpanded = isExpanded;
                info.winCount = item.getWinCount();
                info.nominateCount = item.getNominateCount();
                StringBuffer sum = new StringBuffer();
                if (info.winCount > 0) {
                    sum.append(String.format("获奖%d次, ", info.winCount));
                }
                if (info.nominateCount > 0) {
                    sum.append(String.format("提名%d次", info.nominateCount));
                }
                info.festivalSummary = sum.toString();
                for (int j = 0; j < actorBean.getFestivals().size(); j++) {
                    ActorFestivals festival = actorBean.getFestivals().get(j);
                    if (festival.isEmpty()) {
                        break;
                    }
                    if (item.getFestivalId() == festival.getFestivalId()) {
                        festivals.remove(festival);
                        info.festivalImage = festival.getImg();
                        info.festivalNameCn = festival.getNameCn();
                        info.festivalNameEn = festival.getNameEn();
                        break;
                    }
                }
                if (TextUtils.isEmpty(info.festivalImage) && TextUtils.isEmpty(info.festivalNameCn) && TextUtils.isEmpty(info.festivalNameEn)) {
                    return;
                }
                // awards
                if (info.winCount > 0) {
                    for (int x = 0; x < item.getWinAwards().size(); x++) {
                        SubItemInfo subInfo = new SubItemInfo();
                        ActorWinAwards awardsItem = item.getWinAwards().get(x);
                        if (null == awardsItem || awardsItem.isEmpty()) {
                            continue;
                        }
                        if (info.awardsList == null) {
                            info.awardsList = new ArrayList<SubItemInfo>();
                        }
                        subInfo.id = String.valueOf(awardsItem.getMovieId());
                        subInfo.image = awardsItem.getImage();
                        subInfo.role = awardsItem.getRoleName();
                        subInfo.movie = awardsItem.getMovieTitle();
                        // sequence number + year + award name
                        if (TextUtils.isEmpty(awardsItem.getFestivalEventYear()) || awardsItem.getFestivalEventYear().trim().equals("0") || awardsItem.getFestivalEventYear().trim().equalsIgnoreCase("null")) {
                            if (0 == awardsItem.getSequenceNumber()) {
                                subInfo.label = String.format("%s", awardsItem.getAwardName());
                            } else {
                                subInfo.label = String.format("第%d届  -  %s", awardsItem.getSequenceNumber(), awardsItem.getAwardName());
                            }

                        } else {
                            if (0 == awardsItem.getSequenceNumber()) {
                                subInfo.label = String.format("(%s)  -  %s", awardsItem.getFestivalEventYear(), awardsItem.getAwardName());
                            } else {
                                subInfo.label = String.format("第%d届(%s)  -  %s", awardsItem.getSequenceNumber(), awardsItem.getFestivalEventYear(), awardsItem.getAwardName());
                            }

                        }
                        info.awardsList.add(subInfo);
                    }
                }

                //nominate
                if (info.nominateCount > 0) {
                    for (int x = 0; x < item.getNominateAwards().size(); x++) {
                        SubItemInfo subInfo = new SubItemInfo();
                        ActorNominates awardsItem = item.getNominateAwards().get(x);
                        if (null == awardsItem || awardsItem.isEmpty()) {
                            continue;
                        }
                        if (null == info.nominateList) {
                            info.nominateList = new ArrayList<SubItemInfo>();
                        }
                        subInfo.id = String.valueOf(awardsItem.getMovieId());
                        subInfo.image = awardsItem.getImage();
                        subInfo.role = awardsItem.getRoleName();
                        subInfo.movie = awardsItem.getMovieTitle();
                        // sequence number + year + award name
                        if (TextUtils.isEmpty(awardsItem.getFestivalEventYear()) || awardsItem.getFestivalEventYear().trim().equals("0") || awardsItem.getFestivalEventYear().trim().equals("null")) {
                            if (0 == awardsItem.getSequenceNumber()) {
                                subInfo.label = String.format("%s", awardsItem.getAwardName());
                            } else {
                                subInfo.label = String.format("第%d届  -  %s", awardsItem.getSequenceNumber(), awardsItem.getAwardName());
                            }

                        } else {
                            if (0 == awardsItem.getSequenceNumber()) {
                                subInfo.label = String.format("(%s)  -  %s", awardsItem.getFestivalEventYear(), awardsItem.getAwardName());
                            } else {
                                subInfo.label = String.format("第%d届(%s)  -  %s", awardsItem.getSequenceNumber(), awardsItem.getFestivalEventYear(), awardsItem.getAwardName());
                            }

                        }
                        info.nominateList.add(subInfo);
                    }
                }

                this.festivalInfos.add(info);
            }
        }
        if (null != festivals && !festivals.isEmpty()) {
            for (int j = 0; j < festivals.size(); j++) {
                ActorFestivals festival = festivals.get(j);
                if (festival.isEmpty()) {
                    continue;
                }
                FestivalInfo info = new FestivalInfo();
                info.isExpanded = isExpanded;
                info.winCount = 0;
                info.nominateCount = 0;
                info.festivalImage = festival.getImg();
                info.festivalNameCn = festival.getNameCn();
                info.festivalNameEn = festival.getNameEn();
                this.festivalInfos.add(info);
            }

        }

    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     * @param personId
     */
    public static void launch(Context context, String refer, String personId) {
        Intent launcher = new Intent(context, ActorHonorsActivity.class);
        launcher.putExtra(KEY_MOVIE_PERSOM_ID, personId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
