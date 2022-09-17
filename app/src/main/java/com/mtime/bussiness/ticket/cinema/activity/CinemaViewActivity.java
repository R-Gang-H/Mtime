package com.mtime.bussiness.ticket.cinema.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.IViewHolder;
import com.aspsine.irecyclerview.OnIScrollListener;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.mine.UserCollectQuery;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.app.router.liveevent.event.CollectionState;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.bean.CollectResultBean;
import com.mtime.bussiness.ticket.bean.CommentBean;
import com.mtime.bussiness.ticket.bean.CommentPageBean;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaViewFeatureAdapter;
import com.mtime.bussiness.ticket.cinema.bean.CinemaDetailBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaViewFeature;
import com.mtime.bussiness.ticket.cinema.bean.CinemaViewJsonBean;
import com.mtime.bussiness.ticket.movie.widget.ShowWholeNameView;
import com.mtime.common.utils.DateUtil;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.payment.WapPayActivity;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.SaveOffenGo;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.COLLECTION_OR_CANCEL;

/**
 * 影院详情页
 * 改为recycler view
 */
@SuppressLint("NewApi")
@Route(path = RouterActivityPath.Main.PAGE_CINEMAVIEW)
public class CinemaViewActivity extends BaseActivity implements OnClickListener {
    private class CinemaDetailMsgsAdapter extends RecyclerView.Adapter<CinemaDetailMsgsAdapter.ViewHolder> {

        public class ViewHolder extends IViewHolder {
            ImageView header;
            TextView name;
            TextView time;
            TextView content;
            TextView praise;
            TextView reply;
            View replyIcon;
            ImageView praise_icon;
            ImageView praise_icon_animation;
            View line;

            public ViewHolder(View itemView) {
                super(itemView);
                header = itemView.findViewById(R.id.comment_photo);
                header.setImageResource(R.drawable.profile_default_head_h90);
                name = itemView.findViewById(R.id.comment_name);
                time = itemView.findViewById(R.id.twitter_head_time);
                content = itemView.findViewById(R.id.twitter_head_content);
                praise = itemView.findViewById(R.id.praise);
                reply = itemView.findViewById(R.id.comment_reply_num);
                replyIcon = itemView.findViewById(R.id.tweet_head_triangle);
                praise_icon = itemView.findViewById(R.id.praise_icon);
                praise_icon_animation = itemView.findViewById(R.id.praise_icon_animation);
                line = itemView.findViewById(R.id.gray_line);
                itemView.findViewById(R.id.reply_two).setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.twitter_head_score).setVisibility(View.INVISIBLE);
                itemView.findViewById(R.id.reply_one).setVisibility(View.GONE);
                itemView.findViewById(R.id.twitter_head_comment).setVisibility(View.GONE);
            }
        }

        private final Animation animation;
        private final BaseActivity context;
        private final List<CommentBean> comments = new ArrayList<CommentBean>();
        private OnItemClickListener onItemClickListener;

        public CinemaDetailMsgsAdapter(BaseActivity context, List<CommentBean> comments) {
            this.context = context;
            if (null != comments) {
                this.comments.addAll(comments);
            }

            animation = AnimationUtils.loadAnimation(context, R.anim.zoomin);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.v2_twitter_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int arg0) {
            holder.line.setVisibility(View.VISIBLE);
            if (0 == arg0) {
                holder.line.setVisibility(View.INVISIBLE);
            }

            holder.praise_icon_animation.setVisibility(View.INVISIBLE);

            final CommentBean bean = comments.get(arg0);
            context.volleyImageLoader.displayImage(bean.getUserImage(), holder.header, R.drawable.default_image, R.drawable.default_image, ImageURLManager.ImageStyle.THUMB, null);
            holder.name.setText(bean.getNickname());

            String value = null;
            long sendTime = bean.getEnterTime() - 8 * 60 * 60L;
            if (0 == arg0 && 0 == bean.getTopicId()) {
                sendTime += 8 * 60 * 60L; // 如果是加评论，不需要减去8小时
            }
            long replyTime = (System.currentTimeMillis() / 1000 - sendTime) / 60;
            if (replyTime < 0) {
                Calendar now = Calendar.getInstance();
                TimeZone timeZone = now.getTimeZone();
                long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
                long totalSeconds = totalMilliseconds / 1000;
                replyTime = (totalSeconds - bean.getEnterTime()) / 60;
                if (replyTime < 1) {
                    replyTime = 1;
                }
            }

            if (replyTime < 60) {
                value = String.format("%d分钟前", replyTime);
            } else if (replyTime < (24 * 60)) {
                value = String.format("%d小时前", replyTime / 60);
            } else {
                value = DateUtil.getLongToDate(DateUtil.sdf10, bean.getEnterTime());
            }

            holder.time.setText(value);
            holder.content.setText(bean.getContent());
            holder.content.setMaxLines(5);

            final int count = bean.getReplyCount();
            if (count < 1) {
                value = "回复";
            } else if (count < 1000) {
                value = String.valueOf(count);
            } else {
                value = "999+";
            }

            holder.reply.setText(value);
            holder.reply.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (0 == arg0 && 0 == bean.getTopicId()) {
                        return;
                    }

                    // if no peply and not login, login now.
                    if (0 == count && !UserManager.Companion.getInstance().isLogin()) {
//                        startActivityForResult(LoginActivity.class, new Intent());
                        UserLoginKt.gotoLoginPage(context, null, 0);
                        return;
                    }
                    lastSelectedPos = arg0;
                    isPraised = bean.isPraise();
                    totalPraise = bean.getTotalPraise();

//                    Intent intent = new Intent();
//                    intent.putExtra("topicId", adapter.getComments().get(arg0).getTopicId());
//                    intent.putExtra("title", mCinemaBean.getName());
//                    startActivityForResult(TwitterCinemaActivity.class, intent, 5);
                    JumpUtil.startTwitterCinemaActivity(CinemaViewActivity.this
                            , adapter.getComments().get(arg0).getTopicId(), mCinemaBean.getName(), 5);
                }
            });

            holder.replyIcon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (0 == arg0 && 0 == bean.getTopicId()) {
                        return;
                    }

                    // if no peply and not login, login now.
                    if (0 == count && !UserManager.Companion.getInstance().isLogin()) {
//                        startActivityForResult(LoginActivity.class, new Intent());
                        UserLoginKt.gotoLoginPage(context, null, 0);
                        return;
                    }

                    lastSelectedPos = arg0;
                    isPraised = bean.isPraise();
                    totalPraise = bean.getTotalPraise();

//                    Intent intent = new Intent();
//                    intent.putExtra("topicId", adapter.getComments().get(arg0).getTopicId());
//                    intent.putExtra("title", mCinemaBean.getName());
//                    startActivityForResult(TwitterCinemaActivity.class, intent, 5);
                    JumpUtil.startTwitterCinemaActivity(CinemaViewActivity.this
                            , adapter.getComments().get(arg0).getTopicId(), mCinemaBean.getName(), 5);
                }
            });

            final ImageView viewAnim = holder.praise_icon_animation;
            final ImageView viewDef = holder.praise_icon;
            final TextView textView = holder.praise;

            if (bean.isPraise()) {
                holder.praise_icon.setImageResource(R.drawable.assist2);
                holder.praise_icon_animation.setImageResource(R.drawable.assist2);
                holder.praise.setTextColor(ContextCompat.getColor(context, R.color.orange));
            } else {
                holder.praise_icon.setImageResource(R.drawable.assist1);
                holder.praise_icon_animation.setImageResource(R.drawable.assist1);
                holder.praise.setTextColor(ContextCompat.getColor(context, R.color.color_777777));
            }

            updatePraise(holder.praise, comments.get(arg0).getTotalPraise(), bean.isPraise());

            holder.praise.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    lastSelectedPos = arg0;
                    if (!UserManager.Companion.getInstance().isLogin()) {
                        praiseId = String.valueOf(comments.get(arg0).getTopicId());
//                        Intent intent = new Intent();
//                        startActivityForResult(LoginActivity.class, intent, 3);
                        UserLoginKt.gotoLoginPage(context, null, 3);
                        return;
                    }

                    if (0 == arg0 && 0 == bean.getTopicId()) {
                        return;
                    }

                    comments.get(arg0).setPraise(!bean.isPraise());
                    if (comments.get(arg0).isPraise()) {
                        comments.get(arg0).setTotalPraise(comments.get(arg0).getTotalPraise() + 1);
                    } else {
                        comments.get(arg0).setTotalPraise(comments.get(arg0).getTotalPraise() - 1);
                    }

                    updatePraiseValue(viewAnim, viewDef, textView, comments.get(arg0).getTotalPraise(),
                            comments.get(arg0).isPraise());

                    Map<String, String> parameterList = new ArrayMap<String, String>(2);
                    parameterList.put("id", String.valueOf(comments.get(arg0).getTopicId()));
                    parameterList.put("relatedObjType", String.valueOf(86));
                    HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, null);
                }
            });

            holder.praise_icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    lastSelectedPos = arg0;

                    if (!UserManager.Companion.getInstance().isLogin()) {
                        praiseId = String.valueOf(comments.get(arg0).getTopicId());
//                        Intent intent = new Intent();
//                        startActivityForResult(LoginActivity.class, intent, 3);
                        UserLoginKt.gotoLoginPage(context, null, 3);
                        return;
                    }

                    if (0 == arg0 && 0 == bean.getTopicId()) {
                        return;
                    }

                    comments.get(arg0).setPraise(!bean.isPraise());
                    if (comments.get(arg0).isPraise()) {
                        comments.get(arg0).setTotalPraise(comments.get(arg0).getTotalPraise() + 1);
                    } else {
                        comments.get(arg0).setTotalPraise(comments.get(arg0).getTotalPraise() - 1);
                    }

                    updatePraiseValue(viewAnim, viewDef, textView, comments.get(arg0).getTotalPraise(),
                            comments.get(arg0).isPraise());

                    Map<String, String> parameterList = new ArrayMap<String, String>(2);
                    parameterList.put("id", String.valueOf(comments.get(arg0).getTopicId()));
                    parameterList.put("relatedObjType", String.valueOf(86));
                    HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, null);
                }
            });
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
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private void updatePraise(TextView view, int total, boolean isPraised) {
            String value;
            if (total < 1) {
                value = "赞";
            } else if (total < 1000) {
                value = String.valueOf(total);
            } else {
                value = "999+";
            }
            view.setText(value);
            if (isPraised) {
                view.setTextColor(ContextCompat.getColor(context, R.color.orange));
            } else {
                view.setTextColor(ContextCompat.getColor(context, R.color.color_777777));
            }
        }

        private void updatePraiseValue(ImageView viewAnim, ImageView viewDef, TextView view, int total,
                                       boolean isPraised) {
            if (isPraised) {
                viewAnim.setImageResource(R.drawable.assist2);
                viewDef.setImageResource(R.drawable.assist2);
            } else {
                viewAnim.setImageResource(R.drawable.assist1);
                viewDef.setImageResource(R.drawable.assist1);
            }

            viewDef.startAnimation(animation);
            updatePraise(view, total, isPraised);
        }

        public void setComments(final List<CommentBean> comments) {
            if (null != comments) {
                this.comments.addAll(comments);
                this.notifyDataSetChanged();
            }
        }

        public void addComment(final CommentBean bean) {
            if (null != bean) {
                this.comments.add(0, bean);
                this.notifyDataSetChanged();
            }
        }

        public List<CommentBean> getComments() {
            return this.comments;
        }

        public void restoreLastPraiseState() {
            comments.get(lastSelectedPos).setPraise(!comments.get(lastSelectedPos).isPraise());
            if (comments.get(lastSelectedPos).isPraise()) {
                comments.get(lastSelectedPos).setTotalPraise(comments.get(lastSelectedPos).getTotalPraise() + 1);
            } else {
                comments.get(lastSelectedPos).setTotalPraise(comments.get(lastSelectedPos).getTotalPraise() - 1);
            }
        }
    }

    // 收藏动作
    private static final int COLLECT_ACTION_ADD = 1;
    private static final int COLLECT_ACTION_CANCEL = 2;

    private int lastSelectedPos = 0;
    public static boolean isPraised = false;
    public static int totalPraise = 0;

    //    private View showHideFeaturesRegion;
//    private View showFeatures;
//    private View showFeatureSeperate;
    private RecyclerView mFeatureRv;
    private CinemaViewFeatureAdapter mFeatureAdapter;

    private View header;
    private TextView message_title;
    private TextView like_rate;
    private View noMsg;
    private IRecyclerView commentsList;
    private LoadMoreFooterView loadMoreFooterView;
    private CinemaDetailMsgsAdapter adapter;
    private View overlay;
    private TitleOfNormalView title;
    private int titleHeight;
    private TextView name_china;
    private ShowWholeNameView showNameView;
//    private CinemaRateView rateView;
//    private AppraiseOfCinema bottom;
//    private CommentsInputView commentsHolder;
//    private ImageView poster_header;

    private CustomAlertDlg mCustomDlg;

    private CinemaViewJsonBean mCinemaBean;
    private String mCinemaFirstTel;
    private int totalComments = 0;
    private int queryIndex = 1;
    private boolean queryFinished = false;

    private String mCinemaId;
//    private OnClickListener eticketItemClickListener;

    private String praiseId;
//    private View scale_cover;

    private int defaultDistance;
    private boolean startCal;

    private OnItemClickListener onItemClickListener;
    private int flag = 1;// 用于ScrollListener

    private String mCityId;
    // 报错用
//    private String mCityName;
    private TicketApi mTicketApi;

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        setPageLabel(StatisticTicket.PN_CINEMA_DETAIL);
        mCinemaId = getIntent().getStringExtra(App.getInstance().KEY_CINEMA_ID);
        putBaseStatisticParam(StatisticConstant.CINEMA_ID, String.valueOf(mCinemaId));
        titleHeight = this.getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        queryFinished = false;
        queryIndex = 1;
        defaultDistance = 0;
        startCal = false;
        mTicketApi = new TicketApi();

//        eticketItemClickListener = new OnClickListener() {
//
//            @Override
//            public void onClick(final View v) {
//                final int position = v.getId();
//                final List<CinemaViewEticket> etickets = mCinemaBean.getEtickets();
//                final CinemaViewEticket eticket = etickets.get(position);
//                final Intent intent = new Intent();
//                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaBean.getName());
//                intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//                intent.putExtra(App.getInstance().KEY_ETICKET_ID, String.valueOf(eticket.getEticketId()));
//                intent.putExtra(App.getInstance().KEY_ETICKET_BEAN, eticket);
//                // 判断是否登录
//                if (UserManager.Companion.getInstance().isLogin()) {
//                    startActivity(ETicketConfirmActivity.class, intent);
//                } else {
//                    // TODO 临时注释掉,等完整的需求确定后再跳转.
////                    intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, ETicketConfirmActivity.class.getName());
//                    startActivity(LoginActivity.class, intent);
//                }
//            }
//        };
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        setContentView(R.layout.cinema_detail_ui_list);

        commentsList = this.findViewById(R.id.listview_msg);
        overlay = findViewById(R.id.preload_layout);

        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        header = inflate.inflate(R.layout.cinema_detail_ui_list_header, null);
        name_china = header.findViewById(R.id.name_china);
        like_rate = header.findViewById(R.id.like_rate);
        like_rate.setVisibility(View.GONE);
//        showHideFeaturesRegion = header.findViewById(R.id.show_hide_view);
//        showHideFeaturesRegion.setVisibility(View.GONE);
        // feature
        mFeatureRv = header.findViewById(R.id.special_part_layout_rv);
        // 自动换行
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mFeatureRv.setLayoutManager(layoutManager);
        mFeatureAdapter = new CinemaViewFeatureAdapter(null);
        mFeatureRv.setAdapter(mFeatureAdapter);

        commentsList.setLayoutManager(new LinearLayoutManager(this));
        loadMoreFooterView = (LoadMoreFooterView) commentsList.getLoadMoreFooterView();

//        poster_header = (ImageView) header.findViewById(R.id.poster_header);
        // initialize title,bottom and rate view

        View showname = this.findViewById(R.id.whole_name);
        this.showNameView = new ShowWholeNameView(showname);
        this.showNameView.setVisibility(View.INVISIBLE);

        this.initTitle();
//        this.initBottom();
//        this.initRate();
        this.initUserMessags();

        this.initListEvent();

        //软件盘弹起后所占高度阀值
        final int keyHeight = FrameConstant.SCREEN_HEIGHT / 3;
        findViewById(R.id.cinema_detail_ui_list).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
//                    Toast.makeText(ArticleDetailActivity.this, "监听到软键盘弹起...");
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
//                    Toast.makeText(ArticleDetailActivity.this, "监听到软件盘关闭...");
//                    scale_cover.setVisibility(View.GONE);
//                    commentsHolder.setFocus(false);
//                    commentsHolder.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onRequestData() {
        LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                if (null != locationInfo) {
                    mCityId = locationInfo.getCityId();
//                    mCityName = locationInfo.getCityName();
                } else {
                    mCityId = String.valueOf(GlobalDimensionExt.CITY_ID);
//                    mCityName = GlobalDimensionExt.CITY_NAME;
                }
                requestCinemaDetail();
            }

            @Override
            public void onLocationFailure(LocationException e) {
                onLocationSuccess(LocationHelper.getDefaultLocationInfo());
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (null != showNameView && View.VISIBLE == this.showNameView.getVisibility()) {
                // 收起影院完整名称
                showNameView.setVisibility(View.INVISIBLE);
                return true;
            }
//            else if (null != commentsHolder && View.VISIBLE == commentsHolder.getVisibility()) {
//                scale_cover.setVisibility(View.GONE);
//                commentsHolder.setFocus(false);
//                commentsHolder.setVisibility(View.GONE);
//                return true;
//            }

        }
        return super.onKeyUp(keyCode, event);
    }

    // 请求影院详情数据
    private void requestCinemaDetail() {
        if (mTicketApi == null) {
            return;
        }
        UIUtil.showLoadingDialog(this);
        mTicketApi.getCinemaDetail(mCinemaId, new NetworkManager.NetworkListener<CinemaDetailBean>() {
            @Override
            public void onSuccess(CinemaDetailBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                overlay.setVisibility(View.GONE);
                startCal = true;
                title.setAlpha(0);

                if (result == null || result.getBaseInfo() == null) {
                    MToastUtils.showShortToast("没有获取到影院基本信息");
                    return;
                }

                mCinemaBean = result.getBaseInfo();

                if (UserManager.Companion.getInstance().isLogin()) {
                    // 获取收藏状态
                    getStatus();
                } else {
//                    cinema_praise.setText(R.string.st_cinema_rate_btn_label);
                    // mCinemaId 本地存储的常去影院Id
                    title.setFavoriate(SaveOffenGo.getInstance().contains(mCinemaId));
                }

                // 初始化基本信息
                initBaseInfo();

                // 初始化特色设施
                initFeature(result.getBaseInfo());
//                initInstallations(result.getBaseInfo());

                // 影院信息
//                initCinemaInfo();

                // 报错
//                initCinemaErrors();

                // 广告
//                parseAD(result.getAdvertisement());

                // 重要：不要删掉，二期会添加回来
//                requestCinemaComments(queryIndex++, false);
            }

            @Override
            public void onFailure(NetworkException<CinemaDetailBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                startCal = true;
                MToastUtils.showShortToast("加载数据失败:" + showMsg);
                //
                View loading_failed_layout = findViewById(R.id.loading_failed_layout);
                TextView retryErrorTv = findViewById(R.id.retryErrorTv);
                if (loading_failed_layout == null) {
                    return;
                }
                loading_failed_layout.setVisibility(View.VISIBLE);
                if (retryErrorTv != null) {
                    retryErrorTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loading_failed_layout.setVisibility(View.GONE);
                            onRequestData();
                        }
                    });
                }

//                UIUtil.showLoadingFailedLayout(CinemaViewActivity.this, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //这里填写原来的click代码
//                        onRequestData();
//                    }
//                });
            }
        });

//        RequestCallback mCinemaDetailCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                startCal = true;
//                CinemaDetailBean bean = (CinemaDetailBean) o;
//                mCinemaBean = bean.getBaseInfo();
//
//                if (UserManager.Companion.getInstance().isLogin()) {
//                    getStatus();
//                } else {
////                    cinema_praise.setText(R.string.st_cinema_rate_btn_label);
//                    // mCinemaId
//                    if (SaveOffenGo.getInstance().contains(mCinemaId)) {
//                        title.setFavoriate(true);
//                    } else {
//                        title.setFavoriate(false);
//                    }
//                }
//
//                title.setAlpha(0);
//                initBaseInfo();
//                initInstallations(bean.getBaseInfo());
//                // 影院信息
////                initCinemaInfo();
//                // 报错
////                initCinemaErrors();
//                parseAD(bean.getAdvertisement());
//
//                requestCinemaComments(queryIndex++, false);
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                startCal = true;
//                MToastUtils.showShortToast("加载数据失败:" + e.getLocalizedMessage());
//
//                UIUtil.dismissLoadingDialog();
//                UIUtil.showLoadingFailedLayout(CinemaViewActivity.this, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //这里填写原来的click代码
//                        onRequestData();
//                    }
//                });
//            }
//        };

//        UIUtil.showLoadingDialog(this);
//        // Cinema/Detail.api?cinemaId={0}
//        Map<String, String> param = new HashMap<>(2);
//        param.put("locationId", mCityId);
//        param.put("cinemaId", mCinemaId);
//        HttpUtil.get(ConstantUrl.GET_CINEMA_DETAIL, param, CinemaDetailBean.class, mCinemaDetailCallback, Constants.REQUEST_CHACHE_TIME_10MINS);
    }

    /**
     * 获取收藏状态
     */
    private void getStatus() {
        if (mTicketApi == null) {
            return;
        }
        mTicketApi.userCollectQuery(CommConstant.COLLECTION_OBJ_TYPE_CINEMA, mCinemaId, new NetworkManager.NetworkListener<UserCollectQuery>() {
            @Override
            public void onSuccess(UserCollectQuery result, String showMsg) {
                // 收藏
                boolean isCollect = result != null && result.isCollect() != null && result.isCollect();
                title.setFavoriate(isCollect);

                // 评价影院的功能
                // 调用接口 /Showtime/GetRelatedObjStatus.api  (迁移接口没有该定义)
//                float rate = (int) status.getReffect() / 2 + (int) status.getRservice() / 2;
//                if (rate <= 0) {
//                    bottom.setAppraise(null);
//                } else if (rate < 5) {
//                    bottom.setAppraise(getResources().getString(R.string.st_rate_result_hardwork));
//                } else if (rate <= 6) {
//                    bottom.setAppraise(getResources().getString(R.string.str_normal));
//                } else {
//                    bottom.setAppraise(getResources().getString(R.string.st_rate_result_good));
//                }
//                rateView.setValues((int) status.getReffect() / 2, (int) status.getRservice() / 2);
            }

            @Override
            public void onFailure(NetworkException<UserCollectQuery> exception, String showMsg) {

            }
        });
    }

    /**
     * 收藏/取消收藏
     */
    private void collect(int action) {
        UIUtil.showLoadingDialog(this);
        mTicketApi.postCollect(action, CommConstant.COLLECTION_OBJ_TYPE_CINEMA, mCinemaId, new NetworkManager.NetworkListener<CollectResultBean>() {
            @Override
            public void onSuccess(CollectResultBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (result.getBizCode() == CollectResultBean.SUCCESS) {
                    // 成功
                    MToastUtils.showShortToast(action == COLLECT_ACTION_ADD ? "已添加到我的收藏" : "已取消收藏");
                    title.setFavoriate(action == COLLECT_ACTION_ADD);
                } else {
                    // 失败
                    MToastUtils.showShortToast((action == COLLECT_ACTION_ADD ? "收藏失败" : "取消收藏失败:") + showMsg);
                    title.setFavoriate(action == COLLECT_ACTION_CANCEL);
                }
                LiveEventBus.get(COLLECTION_OR_CANCEL).post(new CollectionState(CommConstant.COLLECTION_TYPE_CINEMA));
            }

            @Override
            public void onFailure(NetworkException<CollectResultBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast((action == COLLECT_ACTION_ADD ? "收藏失败" : "取消收藏失败:") + showMsg);
                title.setFavoriate(action == COLLECT_ACTION_CANCEL);
            }
        });
    }

    // 添加收藏
//    private void addFavorite() {
//        RequestCallback mAddFavoriteCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                final CommResultBean result = (CommResultBean) o;
//                if (result.isSuccess()) {
//                    MToastUtils.showShortToast("已添加到我的收藏");
//                    title.setFavoriate(true);
//                } else {
//                    MToastUtils.showShortToast("收藏失败:" + result.getError());
//                    title.setFavoriate(false);
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };
//
//        UIUtil.showLoadingDialog(this);
//
//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("id", mCinemaId);
//        parameterList.put("type", "33");
//        HttpUtil.post(ConstantUrl.ADD_FAVORITE, parameterList, CommResultBean.class, mAddFavoriteCallback);
//    }

    // 取消收藏
//    private void cancelFavorite() {
//        RequestCallback mCancelFavoriteCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                final CommResultBean result = (CommResultBean) o;
//                if (result.isSuccess()) {
//                    MToastUtils.showShortToast("已取消收藏");
//                    title.setFavoriate(false);
//                } else {
//                    MToastUtils.showShortToast("取消收藏失败");
//                    title.setFavoriate(true);
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };
//
//        UIUtil.showLoadingDialog(this);
//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("relateId", mCinemaId);
//        parameterList.put("type", "33");
//        HttpUtil.post(ConstantUrl.CANCEL_FAVORITE, parameterList, CommResultBean.class, mCancelFavoriteCallback);
//    }

    @Override
    public void onClick(final View v) {
        final Intent intent = new Intent();
        switch (v.getId()) {
            //
            case R.id.name_china:
                if (ToolsUtils.isTextOverFlowed(name_china)) {
                    showNameView.setVisibility(View.VISIBLE);
                }
                break;
//            case R.id.scale_cover:
//                scale_cover.setVisibility(View.GONE);
//                if (null != commentsHolder && View.VISIBLE == commentsHolder.getVisibility()) {
//                    commentsHolder.setFocus(false);
//                    commentsHolder.setVisibility(View.GONE);
//                } else {
//                    rateView.closeView();
//                }
//                break;
            case R.id.cinema_detail_address_map_layout:
                // 地图
//                intent.putExtra(App.getInstance().KEY_MAP_LONGITUDE, mCinemaBean.getBaiduLongitude());
//                intent.putExtra(App.getInstance().KEY_MAP_LATITUDE, mCinemaBean.getBaiduLatitude());
//                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaBean.getName());
//                intent.putExtra(App.getInstance().KEY_CINEMA_ADRESS, mCinemaBean.getAddress());
//                intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//                startActivity(MapViewActivity.class, intent);
                JumpUtil.startMapViewActivity(this, mCinemaBean.getBaiduLongitude(), mCinemaBean.getBaiduLatitude(),
                        mCinemaId, mCinemaBean.getName(), mCinemaBean.getAddress(), "");
                break;
            case R.id.in_phone:
                showCallPhoneDlg();
                break;
            //营业执照跳转
            case R.id.license_rl:
                LicenseActivity.launch(this, assemble().toString(), mCinemaBean.getLicenceImgUrl());
                break;
//            case R.id.business_show_all:
//                this.showFeatures.setVisibility(View.GONE);
//                this.showFeatureSeperate.setVisibility(View.GONE);
//                this.showHideFeaturesRegion.setVisibility(View.VISIBLE);
//                break;
//            case R.id.show_hide_view:
//                this.showFeatures.setVisibility(View.VISIBLE);
//                this.showFeatureSeperate.setVisibility(View.VISIBLE);
//                this.showHideFeaturesRegion.setVisibility(View.GONE);
//                break;
//            case R.id.money_off:
//                intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//                intent.putExtra(App.getInstance().KEY_CINEMA_NAME, mCinemaBean.getName());
//                startActivity(CinemaPreferentialActivity.class, intent);
//                break;
//            case R.id.cinema_pictures:
//                intent.putExtra(App.getInstance().KEY_PHOTO_LIST_TARGET_TYPE, PhotoListFragment.TARGET_TYPE_CINEMA); // 图片类型为电影
//                intent.putExtra(App.getInstance().KEY_PHOTO_LIST_TARGET_ID, mCinemaId);
//                intent.putExtra(App.getInstance().KEY_PHOTO_LIST_TITLE, mCinemaBean.getName());
//                startActivity(PhotoListActivity.class, intent);
//                JumpUtil.startPhotoListActivity(CinemaViewActivity.this, assemble().toString(),
//                        MovieStillsActivity.TARGET_TYPE_CINEMA, mCinemaId, mCinemaBean.getName(), null);
//                break;
//            case R.id.cinema_subshop:
//                CinemaShopsActivity.shops.clear();
//                // 过滤下没有名字的影院
//                for (int i = 0; i < mCinemaBean.getBranchCinemas().size(); i++) {
//                    if (null == mCinemaBean.getBranchCinemas().get(i) ||
//                            TextUtils.isEmpty(mCinemaBean.getBranchCinemas().get(i).getName())) {
//                        continue;
//                    }
//                    CinemaShopsActivity.shops.add(mCinemaBean.getBranchCinemas().get(i));
//                }
//                startActivityForResult(CinemaShopsActivity.class, intent);
//                break;
//            case R.id.cinema_error_tip:
//                // 跳转到用户反馈列表,携带影院信息过去
//                StringBuffer sb = new StringBuffer();
//                if (!TextUtils.isEmpty(mCityName)) {
//                    sb.append(mCityName);
//                    sb.append(getResources().getString(R.string.str_city_postfix));
//                }
//                sb.append(mCinemaBean.getName());
//                intent.putExtra("cinema_info", sb.toString());
//                startActivity(FeedBackActivity.class, intent);
//                break;
            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showCallPhoneDlg() {
        if (TextUtils.isEmpty(mCinemaFirstTel)) {
            return;
        }

        mCustomDlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        mCustomDlg.show();
        mCustomDlg.setBtnOKListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // 危险权限：运行时请求
                Acp.getInstance(CinemaViewActivity.this).request(new AcpOptions.Builder()
                                .setPermissions(Manifest.permission.CALL_PHONE,
                                        Manifest.permission.READ_PHONE_STATE)
                                .build(),
                        new AcpListener() {
                            @Override
                            public void onGranted() {
                                Acp.getInstance(getApplicationContext()).onDestroy();
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
                                    final Uri telUri = Uri.parse("tel:" + mCinemaFirstTel);
                                    final Intent intent = new Intent(Intent.ACTION_CALL, telUri);
                                    startActivity(intent);
                                }
                                if (null != mCustomDlg) {
                                    mCustomDlg.dismiss();
                                    mCustomDlg = null;
                                }
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                Acp.getInstance(getApplicationContext()).onDestroy();
                                MToastUtils.showShortToast(permissions.toString() + "权限拒绝");
                            }
                        });
            }
        });
        mCustomDlg.setBtnCancelListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != mCustomDlg) {
                    mCustomDlg.dismiss();
                    mCustomDlg = null;
                }
            }
        });

        mCustomDlg.getTextView().setText("呼叫" + mCinemaFirstTel);
    }

    /*
     * 分页加载影院评论数据, 初始化时加载第一页评论数据
     */
    private void requestCinemaComments(final int pageIndex, final boolean showDlg) {
        RequestCallback mCinemaCommentsCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                startCal = true;
                UIUtil.dismissLoadingDialog();
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                overlay.setVisibility(View.GONE);

                String value = getResources().getString(R.string.actor_detail_message_label);
                if (!(o instanceof CommentPageBean)) {
                    message_title.setText("用户留言");
                    noMsg.setVisibility(View.VISIBLE);
                    queryFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    return;
                }

                CommentPageBean addBean = (CommentPageBean) o;
                if (addBean.getCount() < 1) {
                    message_title.setText("用户留言");
                    noMsg.setVisibility(View.VISIBLE);
                    queryFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    return;
                }

                if (null == addBean.getList() || addBean.getList().size() < 1) {
                    queryFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    return;
                }

                // update title
                noMsg.setVisibility(View.GONE);
                totalComments = addBean.getCount();
                value = String.format(value, totalComments);
                message_title.setText(value);

                // query the praise value then update the list view
                // update adapter
                requestCommentsPraised(addBean.getList());
            }

            @Override
            public void onFail(final Exception e) {

                final View load_failed = findViewById(R.id.load_failed);
                load_failed.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        load_failed.setVisibility(View.INVISIBLE);
                        onRequestData();
                    }
                });
                startCal = true;

                UIUtil.dismissLoadingDialog();
                overlay.setVisibility(View.GONE);

                if (1 == pageIndex) {
                    message_title.setText("用户留言");
                    noMsg.setVisibility(View.VISIBLE);
                } else {
                    noMsg.setVisibility(View.GONE);
                }
            }
        };

        if (showDlg) {
            UIUtil.showLoadingDialog(this);
        }

        // Cinema/Comment.api?cinemaId={0}&pageIndex={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("cinemaId", mCinemaId);
        param.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(ConstantUrl.GET_CINEMA_COMMENT, param, CommentPageBean.class, mCinemaCommentsCallback, 180000);
    }

    private void requestCommentsPraised(final List<CommentBean> beans) {
        final List<CommentBean> data = new ArrayList<CommentBean>();
        data.addAll(beans);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i).getTopicId());
            sb.append(",");
        }

        String ids = sb.toString();
        if (ids.length() < 1) {
            return;
        }

        // 备注： 产品说留言列表后期会加回来，所以逻辑代码先不删，只注释。对应新接口 /community/praise_stat_list.api

//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("ids", ids);
//        parameterList.put("relatedObjType", "86");
//        HttpUtil.post(ConstantUrl.PARISE_INFOS_BY_RELATEDIDS, parameterList, PariseInfosByRelatedIdsBean.class, new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//                adapter.setComments(beans);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                PariseInfosByRelatedIdsBean bean = (PariseInfosByRelatedIdsBean) o;
//                List<ReviewParis> paris = bean.getReviewParises();
//                if (null == paris) {
//                    adapter.setComments(beans);
//                    adapter.notifyDataSetChanged();
//                    return;
//                }
//
//                for (int i = 0; i < paris.size(); i++) {
//                    data.get(i).setPraise(paris.get(i).getIsPraise());
//                    data.get(i).setTotalPraise(paris.get(i).getTotalPraise());
//                }
//
//                adapter.setComments(data);
//                adapter.notifyDataSetChanged();
//            }
//        });

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != App.STATUS_LOGIN) {
            if (3 == requestCode) {
                adapter.notifyDataSetChanged();
            } else if (requestCode == 5) {
                // update the click item now.
                if (lastSelectedPos > adapter.getComments().size()) {
                    adapter.getComments().get(lastSelectedPos)
                            .setTotalPraise(CinemaViewActivity.totalPraise);
                    adapter.getComments().get(lastSelectedPos).setPraise(CinemaViewActivity.isPraised);
                    adapter.notifyDataSetChanged();
                }
            }
            return;
        }
        if (requestCode == 0) {
            adapter.notifyDataSetChanged();
            return;
        } else if (requestCode == 2) {
            requestWithLogin(mCinemaBean.getCreateMembershipCardUrl());
        } else if (requestCode == 3) {
            Map<String, String> parameterList = new ArrayMap<String, String>(2);
            parameterList.put("id", praiseId);
            parameterList.put("relatedObjType", String.valueOf(86));
            HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, new RequestCallback() {

                @Override
                public void onFail(Exception e) {
                    MToastUtils.showShortToast("赞失败 " + e.getLocalizedMessage());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onSuccess(Object o) {
                    MToastUtils.showShortToast("赞成功");
                    adapter.restoreLastPraiseState();
                    adapter.notifyDataSetChanged();
                }
            });
        } else if (requestCode == 5) {
            // update the click item now.
            if (lastSelectedPos > adapter.getComments().size()) {
                adapter.getComments().get(lastSelectedPos)
                        .setTotalPraise(CinemaViewActivity.totalPraise);
                adapter.getComments().get(lastSelectedPos).setPraise(CinemaViewActivity.isPraised);
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == 4) {
            getStatus();
        } else if (requestCode == 101) {
            // 收藏/取消收藏
            collect(title.getFavoriate() ? COLLECT_ACTION_ADD : COLLECT_ACTION_CANCEL);
        } else {
//            this.rateView.setVisibility(View.VISIBLE);
        }
    }

    public void requestWithLogin(String url) {
        UIUtil.showLoadingDialog(this);

        Map<String, String> parameterList = new ArrayMap<String, String>(1);
        parameterList.put("url", url);
        HttpUtil.post(ConstantUrl.GET_COUPON_URL_WITH_LOGIN, parameterList, SuccessBean.class, new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();

                SuccessBean bean = (SuccessBean) o;
                if (bean.getSuccess() != null) {
                    if (bean.getSuccess().equalsIgnoreCase("true")) {
                        if (UserManager.Companion.getInstance().isLogin()) {
                            String url = bean.getNewUrl();
                            Intent intent = new Intent();
                            intent.putExtra(App.getInstance().WAP_PAY_URL, url);
                            intent.putExtra(App.getInstance().KEY_WEBVIEW_TITLE_NAME, "开卡/绑定");
                            intent.putExtra(App.getInstance().MOVIE_CARD_PAY, true);
                            startActivity(WapPayActivity.class, intent);
                        }

                    } else {
                        MToastUtils.showShortToast("登录失败，请重新登录后重试！");
                    }
                } else {
                    MToastUtils.showShortToast("登录失败，请重新登录后重试!！");
                }

            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();

                MToastUtils.showShortToast("请求数据失败:" + e.getLocalizedMessage());
            }
        });
    }

    private void initTitle() {
        View root = this.findViewById(R.id.cinema_detail_title);
        title = new TitleOfNormalView(this, root, StructType.TYPE_NORMAL_SHOW_BACK_TITLE_FAVORITE, "影院详情",
                new ITitleViewLActListener() {

                    @Override
                    public void onEvent(final ActionType type, final String content) {
                        switch (type) {
                            case TYPE_FAVORITE:
                                if (!UserManager.Companion.getInstance().isLogin()) {
//                                    CinemaViewActivity.this.startActivityForResult(LoginActivity.class, new Intent(), 101);
                                    UserLoginKt.gotoLoginPage(CinemaViewActivity.this, null, 101);
                                    return;
                                }

//                                if (Boolean.valueOf(content)) {
//                                    addFavorite();
//                                } else {
//                                    cancelFavorite();
//                                }
                                // 收藏/取消收藏
                                collect(Boolean.valueOf(content) ? COLLECT_ACTION_ADD : COLLECT_ACTION_CANCEL);
                                break;
                            default:
                                break;
                        }

                    }
                });
        title.setAlpha(0);
        title.setTitleTextAlpha(0, 0.0f);
    }

//    private void initBottom() {
//
//        commentsHolder = (CommentsInputView) this.findViewById(R.id.comments_input_holder);
//        commentsHolder.setListener(new CommentsInputView.CommentsInputViewListener() {
//            @Override
//            public void onEvent(String content) {
//                commentsHolder.setVisibility(View.GONE);
//                commentsHolder.setFocus(false);
//                scale_cover.setVisibility(View.GONE);
//                sendScore(content, rateView.getWatchQuality(), rateView.getServiceQuality(), false);
//            }
//        });
//
//        commentsHolder.setVisibility(View.GONE);
//        bottom = (AppraiseOfCinema) this.findViewById(R.id.cinema_detail_bottom);
//        bottom.setListener(new AppraiseOfCinema.OnAppraiseViewClickListener() {
//            @Override
//            public void onEvent(int type) {
//                if (!UserManager.Companion.getInstance().isLogin()) {
//                    final Intent intent = new Intent();
//                    startActivityForResult(LoginActivity.class, intent, 4);
//                    return;
//                }
//
//                setSwipeBack(false);
//                scale_cover.setVisibility(View.VISIBLE);
//
//                if (0 == type) {
//                    commentsHolder.setVisibility(View.VISIBLE);
//                    commentsHolder.setFocus(true);
//                } else {
//                    rateView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        bottom.setVisibility(View.GONE);
//    }

    // 初始化评分
//    private void initRate() {
//        scale_cover = this.findViewById(R.id.scale_cover);
//        scale_cover.setOnClickListener(this);
//
//        // load rate view
//        View root = this.findViewById(R.id.cinema_rate);
//        rateView = new CinemaRateView(this, root, new ICinemaRateViewListener() {
//
//            @Override
//            public void onEvent(CinemaRateViewEventType type, int rate) {
//                scale_cover.setVisibility(View.INVISIBLE);
//
//                if (CinemaRateViewEventType.TYPE_OK == type) {
//                    // update button
//                    // 评价影院的功能
//                    if (rate <= 0) {
//                        bottom.setAppraise(null);
//                    } else if (rate < 5) {
//                        bottom.setAppraise(getResources().getString(R.string.st_rate_result_hardwork));
//                    } else if (rate <= 6) {
//                        bottom.setAppraise(getResources().getString(R.string.str_normal));
//                    } else {
//                        bottom.setAppraise(getResources().getString(R.string.st_rate_result_good));
//                    }
//
//                    // send the rate value to server
//                    sendScore(null, rateView.getWatchQuality(), rateView.getServiceQuality(), true);
//                }
//
//                setSwipeBack(true);
//            }
//        });
//
//        rateView.setVisibility(View.GONE);
//    }

    /**
     * 初始化基本信息
     */
    private void initBaseInfo() {
//        ImageHelper.with(this, ImageProxyUrl.SizeType.RATIO_1_1, ImageProxyUrl.ClipType.FIX_WIDTH_OR_HEIGHT)
//                .load(mCinemaBean.getImage())
//                .view(poster_header)
//                .placeholder(R.drawable.default_image)
//                .showload();


        if (mCinemaBean.getRating() > 0) {
            if (header != null) {
                StringBuffer sb = new StringBuffer();
                sb.append("喜爱度 ").append((int) (mCinemaBean.getRating() * 10)).append("%");
                like_rate.setText(sb.toString());
                like_rate.setVisibility(View.VISIBLE);
            }
        }

        name_china.setText(mCinemaBean.getName());
        name_china.setOnClickListener(this);
        showNameView.setLabels(mCinemaBean.getName(), null);

        TextView halls = header.findViewById(R.id.halls);
        StringBuffer sb = new StringBuffer();
        sb.append(mCinemaBean.getHallCount()).append(this.getResources().getString(R.string.cinema_detail_halls_label));
        halls.setText(sb.toString());

        // 观影效果
//        TextView halls_effect = (TextView) header.findViewById(R.id.halls_effect);
//        int rate = (int) mCinemaBean.getQualityRating() / 2;
//        String effect;
//        if (0 == rate) {
//            effect = "暂无";
//        } else if (1 == rate) {
//            effect = "很差";
//        } else if (2 == rate) {
//            effect = "差";
//        } else if (3 == rate) {
//            effect = "一般";
//        } else if (4 == rate) {
//            effect = "好";
//        } else {
//            effect = "很好";
//        }
//        halls_effect.setText(effect);

        // 服务质量
//        TextView serviceView = (TextView) header.findViewById(R.id.halls_effect2);
//        rate = (int) mCinemaBean.getServiceRating() / 2;
//        if (0 == rate) {
//            effect = "暂无";
//        } else if (1 == rate) {
//            effect = "很差";
//        } else if (2 == rate) {
//            effect = "差";
//        } else if (3 == rate) {
//            effect = "一般";
//        } else if (4 == rate) {
//            effect = "好";
//        } else {
//            effect = "很好";
//        }
//        serviceView.setText(effect);

        //
//        rateView.setValues(0, 0);

        View addressMapLayout = header.findViewById(R.id.cinema_detail_address_map_layout);
        addressMapLayout.setOnClickListener(this);
        TextView address = header.findViewById(R.id.address);
        address.setText(!TextUtils.isEmpty(mCinemaBean.getAddress()) ? mCinemaBean.getAddress().trim() : "");

        View phone = header.findViewById(R.id.in_phone);
        phone.setOnClickListener(this);
        TextView phone_number = header.findViewById(R.id.phone_number);
        mCinemaFirstTel = mCinemaBean.getFirstPhoneNumber();

        phone_number.setText(TextUtils.isEmpty(mCinemaFirstTel) ? "--" : mCinemaFirstTel);
        //营业执照
        LinearLayout licenceRl = header.findViewById(R.id.license_rl);
        licenceRl.setOnClickListener(this);
        if (TextUtils.isEmpty(mCinemaBean.getLicenceImgUrl())) {
            licenceRl.setVisibility(View.GONE);
        } else {
            licenceRl.setVisibility(View.VISIBLE);
            TextView licenceTv = header.findViewById(R.id.license_tv);
            licenceTv.setText(mCinemaBean.getLicenceButtonLabel());
        }
    }

    /**
     * 初始化特色设施
     *
     * @param bean
     */
    private void initFeature(CinemaViewJsonBean bean) {
        // 灰色分隔条
        View cinema_special_seperate = header.findViewById(R.id.cinema_special_id);
        View rootView = header.findViewById(R.id.special_part_layout_root);
        CinemaViewFeature feature = bean.getFeature();
        if (feature == null) {
            cinema_special_seperate.setVisibility(View.GONE);
            rootView.setVisibility(View.GONE);
            return;
        }

        // TODO: 2020/10/5 需要按新排序顺序返回 加排序字段
        List<String> features = new ArrayList();
        if (feature.getHasPark() == 1) {
            // 可停车
            features.add("可停车");
        }
        if (1 == feature.getHasFood() || 1 == feature.getHasLeisure()) {
            // 周边休闲
            features.add("周边休闲");
        }
        if (feature.getHasServiceTicket() == 1) {
            // 餐饮
            features.add("餐饮");
        }
        if (feature.getHas3D() == 1) {
            // 3D
            features.add("3D");
        }
        if (feature.getHasIMAX() == 1) {
            // IMAX
            features.add("IMAX");
        }
        if (feature.getHas4D() == 1) {
            // 4D
            features.add("4D");
        }
        if (feature.getHasWifi() == 1) {
            // WIFI
            features.add("WIFI");
        }
        if (feature.getHasVIP() == 1) {
            // VIP
            features.add("VIP");
        }
        if (feature.getHasLoveseat() == 1) {
            // 情侣座
            features.add("情侣座");
        }
        if (feature.getHasGame() == 1) {
            // 游戏厅
            features.add("游戏厅");
        }
        if (feature.getHasCardPay() == 1) {
            // 可刷卡
            features.add("可刷卡");
        }

        if (CollectionUtils.isEmpty(features)) {
            cinema_special_seperate.setVisibility(View.GONE);
            rootView.setVisibility(View.GONE);
            return;
        }

        cinema_special_seperate.setVisibility(View.VISIBLE);
        rootView.setVisibility(View.VISIBLE);
        mFeatureAdapter.getData().clear();
        mFeatureAdapter.addData(features);
    }

//    /**
//     * 初始化特色设施
//     * @param bean
//     */
//    private void initInstallations(CinemaViewJsonBean bean) {
//        View installations = header.findViewById(R.id.installations);
//        // 灰色分隔条
//        View cinema_special_seperate = header.findViewById(R.id.cinema_special_id);
//        CinemaViewFeature feature = bean.getFeature();
//        if (null == feature) {//
//            cinema_special_seperate.setVisibility(View.GONE);
//            installations.setVisibility(View.GONE);
//            return;
//        }
//
//        int showIndex = 0;
//        int notShowIndex = 0;
//        String value = null;
//        View parks = header.findViewById(R.id.parks);
//        if (0 == feature.getHasPark()) {
//            parks.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            TextView parks_tips = (TextView) header.findViewById(R.id.parks_tips);
//            value = feature.getFeatureParkContent();
//            parks_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            showIndex++;
//        }
//
//
//        View facility = header.findViewById(R.id.facility);
//        if (0 == feature.getHasFood() && 0 == feature.getHasLeisure()) {
//            facility.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            value = feature.getFeatureLeisureContent();
//            TextView facility_tips = (TextView) header.findViewById(R.id.facility_tips);
//            facility_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            showIndex++;
//        }
//
//        View ticket_machine = header.findViewById(R.id.food_machine);
//        if (0 == feature.getHasServiceTicket()) {
//            ticket_machine.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            TextView food_machine_tips = (TextView) header.findViewById(R.id.food_machine_tips);
//            value = feature.getFeatureFoodContent();
//            food_machine_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            showIndex++;
//        }
//
//
//        // hide/show the
//        int subShowIndex = 0;
//        int subNotShowIndex = 0;
//        //Show unexpanded
//        this.showFeatureSeperate = header.findViewById(R.id.show_all_seperate);
//        this.showFeatures = header.findViewById(R.id.business_show_all);
//        //Not show expanded
//
//        if (0 == feature.getHas3D()) {
//            View icon_3d = header.findViewById(R.id.icon_3d);
//            icon_3d.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//
//        if (0 == feature.getHasIMAX()) {
//            View icon_imax = header.findViewById(R.id.icon_imax);
//            icon_imax.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//
//        if (0 == feature.getHas4D()) {
//            View icon_4d = header.findViewById(R.id.icon_4d);
//            icon_4d.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//        if (0 == feature.getHasWifi()) {
//            View icon_wififree = header.findViewById(R.id.icon_wififree);
//            icon_wififree.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//        if (0 == feature.getHasVIP()) {
//            View icon_vip = header.findViewById(R.id.icon_vip);
//            icon_vip.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//        if (0 == feature.getHasLoveseat()) {
//            View icon_coupleseat = header.findViewById(R.id.icon_coupleseat);
//            icon_coupleseat.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//        if (0 == feature.getHasGame()) {
//            View icon_gamezone = header.findViewById(R.id.icon_gamezone);
//            icon_gamezone.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//        if (0 == feature.getHasCardPay()) {
//            View icon_creditcard = header.findViewById(R.id.icon_creditcard);
//            icon_creditcard.setVisibility(View.GONE);
//            subNotShowIndex++;
//        } else {
//            subShowIndex++;
//        }
//
//        if (12 == notShowIndex + subNotShowIndex) {
//            cinema_special_seperate.setVisibility(View.GONE);
//            installations.setVisibility(View.GONE);
//            return;
//        }
//
//        if (9 == subNotShowIndex) {
//            // hide the view now
//            this.showFeatures.setVisibility(View.GONE);
//            this.showFeatureSeperate.setVisibility(View.GONE);
//            return;
//        }
//
//        boolean showAll = (showIndex + subShowIndex > 5) ? false : true;
//        int temp = 0;
//        // show/hide others.
//        if (0 == feature.getHas3D()) {
//            View c_3d = header.findViewById(R.id.c_3d);
//            c_3d.setVisibility(View.GONE);
//        } else {
//            value = feature.getFeature3DContent();
//            TextView c_3d_tips = (TextView) header.findViewById(R.id.c_3d_tips);
//            c_3d_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//        }
//        if (0 == feature.getHasIMAX()) {
//            View icon_imax = header.findViewById(R.id.imax);
//            icon_imax.setVisibility(View.GONE);
//        } else {
//            value = feature.getFeatureIMAXContent();
//            TextView imax_tips = (TextView) header.findViewById(R.id.imax_tips);
//            imax_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.imax_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//        if (0 == feature.getHas4D()) {
//            View icon_imax = header.findViewById(R.id.imax_4d);
//            icon_imax.setVisibility(View.GONE);
//        } else {
//            value = feature.getFeature4DContent();
//            TextView imax_4d_tips = (TextView) header.findViewById(R.id.imax_4d_tips);
//            imax_4d_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.imax_4d_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//        if (0 == feature.getHasWifi()) {
//            View icon_wififree = header.findViewById(R.id.wifi);
//            icon_wififree.setVisibility(View.GONE);
//        } else {
//            value = feature.getWifiContent();
//            TextView wifi_tips = (TextView) header.findViewById(R.id.wifi_tips);
//            if (TextUtils.isEmpty(value)) {
//                wifi_tips.setText("--");
//            } else {
//                wifi_tips.setText(value);
//            }
//
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.wifi_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//        if (0 == feature.getHasVIP()) {
//            View icon_vip = header.findViewById(R.id.vip);
//            icon_vip.setVisibility(View.GONE);
//        } else {
//            value = feature.getFeatureVIPContent();
//            TextView vip = (TextView) header.findViewById(R.id.vip_tips);
//            vip.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.vip_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//        if (0 == feature.getHasLoveseat()) {
//            View icon_coupleseat = header.findViewById(R.id.coupleseat);
//            icon_coupleseat.setVisibility(View.GONE);
//        } else {
//            value = feature.getLoveseatContent();
//            TextView coupleseat = (TextView) header.findViewById(R.id.coupleseat_tips);
//            coupleseat.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.coupleseat_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//
//        if (0 == feature.getHasGame()) {
//            View icon_gamezone = header.findViewById(R.id.games);
//            icon_gamezone.setVisibility(View.GONE);
//        } else {
//            value = feature.getFeatureGameContent();
//            TextView gamezone_tips = (TextView) header.findViewById(R.id.gamezone_tips);
//            gamezone_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.gamezone_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//
//        if (0 == feature.getHasCardPay()) {
//            View icon_creditcard = header.findViewById(R.id.creditcard);
//            icon_creditcard.setVisibility(View.GONE);
//        } else {
//            value = feature.getCardPayContent();
//            TextView creditcard_tips = (TextView) header.findViewById(R.id.creditcard_tips);
//            creditcard_tips.setText(TextUtils.isEmpty(value) ? "--" : value);
//            temp++;
//            if (!showAll && temp == subShowIndex) {
//                header.findViewById(R.id.creditcard_arrow_icon).setVisibility(View.VISIBLE);
//            }
//        }
//        if (showAll) {
//            this.showFeatures.setVisibility(View.GONE);
//            this.showFeatureSeperate.setVisibility(View.GONE);
//            this.showHideFeaturesRegion.setVisibility(View.VISIBLE);
//        } else {
//            this.showFeatures.setOnClickListener(this);
//            this.showHideFeaturesRegion.setOnClickListener(this);
//        }
//    }

    // 影院信息
//    private void initCinemaInfo() {
//
//        int notShowIndex = 0;
//
//        View lineSeperate = null;
//        LinearLayout eticketContainer = (LinearLayout) header.findViewById(R.id.item_eticket_list_ll_container);
//        final List<CinemaViewEticket> etickets = mCinemaBean.getEtickets();
//        if ((null == etickets || 0 == etickets.size()) && TextUtils.isEmpty(mCinemaBean.getCreateMembershipCardUrl())) {
//            eticketContainer.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            final LayoutInflater inflater = LayoutInflater.from(CinemaViewActivity.this);
//            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(0, 0, 0, 0);
//
//            if (!TextUtils.isEmpty(mCinemaBean.getCreateMembershipCardUrl())) {
//                final View eticketItemView = inflater.inflate(R.layout.cinema_info_eticket_list_item_2, null);
//                final TextView tv__name = (TextView) eticketItemView.findViewById(R.id.ticket_tips);
//                final TextView tv_price = (TextView) eticketItemView.findViewById(R.id.ticket_value);
//                tv_price.setText("开卡/绑定");
//
//                tv__name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_vip, 0, 0, 0);
//                tv__name.setText("影院会员充值卡");
//                final View line = eticketItemView.findViewById(R.id.ticket_seperate_line);
//                eticketItemView.setLayoutParams(lp);
//                if ((etickets != null) && (etickets.size() > 0)) {
//                    line.setVisibility(View.VISIBLE);
//                } else {
//                    line.setVisibility(View.GONE);
//                }
//
//                eticketItemView.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        if (UserManager.Companion.getInstance().isLogin()) {
//                            requestWithLogin(mCinemaBean.getCreateMembershipCardUrl());
//                        } else {
//                            startActivityForResult(LoginActivity.class, 2);
//                        }
//
//                    }
//                });
//                eticketContainer.addView(eticketItemView);
//            }
//
//            if (null != etickets && etickets.size() > 0) {
//                for (int i = 0; i < etickets.size(); i++) {
//                    if (TextUtils.isEmpty(etickets.get(i).getName())) {
//                        continue;
//                    }
//
//                    final View eticketItemView = inflater.inflate(R.layout.cinema_info_eticket_list_item, null);
//                    final TextView tv__name = (TextView) eticketItemView.findViewById(R.id.ticket_tips);
//                    final TextView tv_price = (TextView) eticketItemView.findViewById(R.id.ticket_value);
//                    tv__name.setText(etickets.get(i).getName());
//                    if (i == (etickets.size() - 1)) {
//                        final View line = eticketItemView.findViewById(R.id.ticket_seperate_line);
//                        line.setVisibility(View.GONE);
//                        lineSeperate = line;
//                    }
//
//                    String value = getResources().getString(R.string.actor_detail_hot_playing_mark);
//                    StringBuffer sb = new StringBuffer();
//                    sb.append(value);
//                    int start = sb.length();
//                    sb.append(MtimeUtils.formatPrice(etickets.get(i).getPrice() / 100));
//                    int end = sb.length();
//
//                    SpannableString sp = new SpannableString(sb.toString());
//                    sp.setSpan(new AbsoluteSizeSpan(55), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    tv_price.setText(sp);
//
//                    eticketItemView.setLayoutParams(lp);
//                    eticketItemView.setId(i);
//                    eticketItemView.setOnClickListener(eticketItemClickListener);
//                    eticketContainer.addView(eticketItemView);
//                }
//            }
//        }
//
//        // money off
//        Coupon coupon = mCinemaBean.getCoupon();
//        View money_off = header.findViewById(R.id.money_off);
//        money_off.setOnClickListener(this);
//        if (null == coupon || TextUtils.isEmpty(coupon.getContent())) {
//            money_off.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            TextView moneyoff_tips = (TextView) header.findViewById(R.id.moneyoff_tips);
//            moneyoff_tips.setText(coupon.getContent());
//            if (null != lineSeperate) {
//                lineSeperate.setVisibility(View.VISIBLE);
//            }
//        }
//
//        String value = null;
//        // sub shop
//        View cinema_subshop = header.findViewById(R.id.cinema_subshop);
//        cinema_subshop.setOnClickListener(this);
//
//        List<BranchCinemasBean> branchs = mCinemaBean.getBranchCinemas();
//        if (null == branchs || 0 == branchs.size()) {
//            cinema_subshop.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            TextView subshop_tips = (TextView) header.findViewById(R.id.subshop_tips);
//            value = this.getResources().getString(R.string.cinema_detail_subshops_label);
//            int count = 0;
//            for (int i = 0; i < branchs.size(); i++) {
//                if (null == branchs.get(i) || TextUtils.isEmpty(branchs.get(i).getName())) {
//                    continue;
//                }
//                count++;
//            }
//            if (count > 0) {
//                value = String.format(value, count);
//                subshop_tips.setText(value);
//            } else {
//                cinema_subshop.setVisibility(View.GONE);
//                notShowIndex++;
//            }
//        }
//
//        View cinema_pictures = header.findViewById(R.id.cinema_pictures);
//        cinema_pictures.setOnClickListener(this);
//
//        // 去掉了gallerylist,需要在之类的显示调整一下判断
//        int imgCount = mCinemaBean.getGalleryTotalCount();
//        if (0 == imgCount) {
//            cinema_pictures.setVisibility(View.GONE);
//            notShowIndex++;
//        } else {
//            TextView pictures_tips = (TextView) header.findViewById(R.id.pictures_tips);
//            value = this.getResources().getString(R.string.cinema_detail_pictures_label);
//            value = String.format(value, imgCount);
//            pictures_tips.setText(value);
//        }
//
//        if (4 == notShowIndex) {
//            View cinema_info = header.findViewById(R.id.cinema_info);
//            View cinema_info_seperate = header.findViewById(R.id.cinema_info_seperate_id);
//            cinema_info_seperate.setVisibility(View.GONE);
//            cinema_info.setVisibility(View.GONE);
//        }
//    }

    // 报错
//    private void initCinemaErrors() {
//        TextView cinema_error_tip = (TextView) header.findViewById(R.id.cinema_error_tip);
////        if (TextUtils.isEmpty(mCinemaBean.getRectifyMessage())) {//TODO 产品我要报错文案直接写死,不从接口读取
////            cinema_error_tip.setVisibility(View.GONE);
////            return;
////        }
////        cinema_error_tip.setText(mCinemaBean.getRectifyMessage());
//        cinema_error_tip.setOnClickListener(this);
//    }

    // 用户留言列表
    private void initUserMessags() {
        message_title = header.findViewById(R.id.message_title);
        message_title.setVisibility(View.GONE);
        // 暂无留言
        noMsg = header.findViewById(R.id.no_message);
        noMsg.setVisibility(View.GONE);
//        noMsg.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!UserManager.Companion.getInstance().isLogin()) {
//                    final Intent intent = new Intent();
//                    startActivityForResult(LoginActivity.class, intent, 4);
//                    return;
//                }
//
//                setSwipeBack(false);
//                scale_cover.setVisibility(View.VISIBLE);
//
//                commentsHolder.setVisibility(View.VISIBLE);
//                commentsHolder.setFocus(true);
//            }
//        });

        // add footer first to show the last item avoid the comment bar hide it.
        adapter = new CinemaDetailMsgsAdapter(this, null);

        commentsList.addHeaderView(header);

        commentsList.setIAdapter(adapter);
        initOnItemClickListener();
        adapter.setOnItemClickListener(onItemClickListener);
    }

    private void initOnItemClickListener() {
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (0 == pos && 0 == adapter.getComments().get(pos).getTopicId()) {
                    return;
                }

                lastSelectedPos = pos;
                isPraised = adapter.getComments().get(pos).isPraise();
                totalPraise = adapter.getComments().get(pos).getTotalPraise();

//                Intent intent = new Intent();
//                intent.putExtra("topicId", adapter.getComments().get(pos).getTopicId());
//                intent.putExtra("title", mCinemaBean.getName());
//                startActivityForResult(TwitterCinemaActivity.class, intent, 5);
                JumpUtil.startTwitterCinemaActivity(CinemaViewActivity.this
                        , adapter.getComments().get(pos).getTopicId(), mCinemaBean.getName(), 5);
            }
        };
    }

    // 发布评分
//    private void sendScore(final String content, final int watchQuality, final int serviceQuality, final boolean isRate) {
//
//        RequestCallback callback = new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//                String label = "评论失败 ";
//                if (isRate) {
//                    label = "评分失败 ";
//                }
//                MToastUtils.showShortToast(label + e.getLocalizedMessage());
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                String label = "评论成功 ";
//                if (isRate) {
//                    label = "评分成功 ";
//                }
//
//                commentsHolder.clear();
//
//                MToastUtils.showShortToast(label);
//
//                // add
//                if (!isRate) {
//                    CommentBean bean = new CommentBean();
//                    bean.setTopicId(0);
//                    bean.setContent(content);
//                    bean.setEnterTime((int) (System.currentTimeMillis() / 1000));
//                    bean.setNickname(UserManager.Companion.getInstance().getNickname());
//                    bean.setUserImage(UserManager.Companion.getInstance().getUserAvatar());
//                    adapter.addComment(bean);
//                }
//            }
//        };
//
//        Map<String, String> parameterList = new ArrayMap<String, String>(4);
//        parameterList.put("cinemaId", mCinemaId);
//        parameterList.put("content", content);
//        parameterList.put("qualityRating", String.valueOf(watchQuality));
//        parameterList.put("serviceRating", String.valueOf(serviceQuality));
//        HttpUtil.post(ConstantUrl.COMMENT_CINEMA, parameterList, CommResultBean.class, callback);
//    }

//    private void parseAD(final ADTotalBean bean) {
//        if (null == bean || !bean.getSuccess() || null == bean.getAdvList()) {
//            return;
//        }
//
//        int index = 0;
//        int count = bean.getAdvList().size();
//        if (count < 1) {
//            return;
//        }
//
//        ADDetailBean item = bean.getAdvList().get(index++);
//        //
//        loadAD(item);
//
//        if (1 == count) {
//            return;
//        }
//        // second
//        item = bean.getAdvList().get(index++);
//        loadAD(item);
//    }

//    private void loadAD(ADDetailBean item) {
//        //需要时再加载
//        ADWebView ad1 = (ADWebView) header.findViewById(R.id.ad1);
//        View adView1 = header.findViewById(R.id.ad1_seperate);
//        View adView2 = header.findViewById(R.id.ad2_seperate);
//        ADWebView ad2 = (ADWebView) header.findViewById(R.id.ad2);
//        ad1.setFocusable(false);
//        ad2.setFocusable(false);
//
//        if (!ADWebView.show(item)) {
//            return;
//        }
//
//        if (App.getInstance().AD_CINEMA_BANNER1.equalsIgnoreCase(item.getType())) {
//            adView1.setVisibility(View.VISIBLE);
//            ad1.setVisibility(View.VISIBLE);
//            ad1.load(this, item);
//        } else {
//            adView2.setVisibility(View.VISIBLE);
//            ad2.setVisibility(View.VISIBLE);
//            ad2.load(this, item);
//        }
//    }

    private void initListEvent() {
        // 不要删掉
//        commentsList.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(View loadMoreView) {
//                if (loadMoreFooterView.canLoadMore()) {
//                    if (!queryFinished) {
//                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
//                        requestCinemaComments(queryIndex++, true);
//                    }
//                }
//            }
//        });

        // 不要删掉
        commentsList.setOnIScrollListener(new OnIScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                int[] posterLocation = new int[2];
                int[] commentLayoutLocation = new int[2];

//                poster_header.getLocationOnScreen(posterLocation);
                name_china.getLocationOnScreen(posterLocation);
                // pos is zero, means reach the bottom of header
                message_title.getLocationOnScreen(commentLayoutLocation);

                if (!startCal) {
                    defaultDistance = posterLocation[1];
                    return;
                }
                // change tilte's alpha
                float alpha = Math.abs(1f * (posterLocation[1] - defaultDistance) / titleHeight);
                alpha = alpha > 1 ? 1 : alpha;
                if (posterLocation[1] == 0 && flag == 0) {
                    alpha = 1;
                }

                title.setAlpha(alpha);
                title.setTitleTextAlpha(alpha, 0.0f);
                // 新需求，只要一滚动就显示。
//                if (Math.abs(defaultDistance - posterLocation[1]) < 10) {
//                    bottom.setVisibility(View.GONE);
//                } else {
//                    bottom.setVisibility(View.VISIBLE);
//                }
                flag = posterLocation[1];

            }

        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (null != scale_cover && View.VISIBLE == scale_cover.getVisibility()) {
//            return super.dispatchTouchEvent(ev);
//        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public static void launch(Context context, String refer, String cinemaId) {
        if (TextUtils.isEmpty(cinemaId)) {
            return;
        }
        Intent launcher = new Intent(context, CinemaViewActivity.class);
        if (!TextUtils.isEmpty(cinemaId)) {
            launcher.putExtra(App.getInstance().KEY_CINEMA_ID, cinemaId);
        }
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTicketApi != null) {
            mTicketApi = null;
        }
    }

}
