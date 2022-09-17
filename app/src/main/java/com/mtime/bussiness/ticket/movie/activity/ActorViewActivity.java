package com.mtime.bussiness.ticket.movie.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.IViewHolder;
import com.aspsine.irecyclerview.OnIScrollListener;
import com.aspsine.irecyclerview.OnLoadMoreListener;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.jssdk.JSInterfaceNative;
import com.jssdk.beans.OpenAppLinkBean;
import com.jssdk.listener.JSOpenAppLinkListener;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.common.CommBizCodeResult;
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState;
import com.kotlin.android.app.data.entity.community.praisestate.PraiseStateList;
import com.kotlin.android.app.data.entity.community.record.PostRecord;
import com.kotlin.android.app.data.entity.community.record.RecordId;
import com.kotlin.android.mtime.ktx.KtxMtimeKt;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.liveevent.event.CollectionState;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.provider.ugc.IUgcProvider;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ADDetailBean;
import com.mtime.beans.ADTotalBean;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedItemBean;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedListBean;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.bean.CollectResultBean;
import com.mtime.bussiness.ticket.bean.CommentBean;
import com.mtime.bussiness.ticket.bean.CommentPageBean;
import com.mtime.bussiness.ticket.movie.api.PersonApi;
import com.mtime.bussiness.ticket.movie.bean.ActorInfoBean;
import com.mtime.bussiness.ticket.movie.bean.ActorViewBean;
import com.mtime.bussiness.ticket.movie.bean.FilmographyBean;
import com.mtime.bussiness.ticket.movie.bean.FilmographyListBean;
import com.mtime.bussiness.ticket.movie.bean.PersonDynamicBean;
import com.mtime.bussiness.ticket.movie.bean.PersonRatingResultBean;
import com.mtime.bussiness.ticket.movie.widget.ActorBasicInfoView;
import com.mtime.bussiness.ticket.movie.widget.ActorExperiencesView;
import com.mtime.bussiness.ticket.movie.widget.ActorHonorsView;
import com.mtime.bussiness.ticket.movie.widget.ActorHotPlayingView;
import com.mtime.bussiness.ticket.movie.widget.ActorImagesView;
import com.mtime.bussiness.ticket.movie.widget.ActorMoviesView;
import com.mtime.bussiness.ticket.movie.widget.ActorRelationsView;
import com.mtime.bussiness.ticket.movie.widget.AppraiseOfPerson;
import com.mtime.bussiness.ticket.movie.widget.QuizGameView;
import com.mtime.bussiness.ticket.movie.widget.ShowWholeNameView;
import com.mtime.bussiness.ticket.widget.CommentsInputView;
import com.mtime.bussiness.video.api.PraiseCommentApi;
import com.mtime.common.utils.LogWriter;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.ADWebView;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.network.UAUtils;
import com.mtime.share.ShareExtJava;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticActor;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageLoader;
import com.mtime.util.JumpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;
import com.mtime.util.VolleyError;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kotlin.android.app.data.annotation.AnnotationExtKt.CONTENT_TYPE_FILM_COMMENT;
import static com.kotlin.android.app.data.annotation.AnnotationExtKt.SHORT_COMMENT;
import static com.kotlin.android.router.liveevent.EventKeyExtKt.COLLECTION_OR_CANCEL;

/**
 * actor detailed information
 * 影人资料页
 *
 * @author Lee
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
@Route(path = RouterActivityPath.Main.PAGE_ACTORVIEW)
public class ActorViewActivity extends BaseActivity {


    private class ActorDetailMsgsAdapter extends RecyclerView.Adapter<ActorDetailMsgsAdapter.ViewHolder> {

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


        public ActorDetailMsgsAdapter(BaseActivity context, List<CommentBean> comments) {
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


            final CommentBean bean = comments.get(arg0);
            ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                    .override(MScreenUtils.dp2px(45), MScreenUtils.dp2px(45))
                    .placeholder(R.drawable.profile_default_head_h90)
                    .load(bean.getUserImage())
                    .view(holder.header)
                    .showload();
            holder.name.setText(bean.getNickname());

            String value = KtxMtimeKt.formatPublishTime(Long.parseLong(String.valueOf(bean.getStampTime())));

//            long sendTime = bean.getStampTime() - 8 * 60 * 60L;
//            if (0 == arg0 && 0 == bean.getTweetId()) {
//                sendTime += 8 * 60 * 60L;
//            }
//            long replyTime = (System.currentTimeMillis() / 1000 - sendTime) / 60;
//            if (replyTime < 0) {
//                Calendar now = Calendar.getInstance();
//                TimeZone timeZone = now.getTimeZone();
//                long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
//                long totalSeconds = totalMilliseconds / 1000;
//                replyTime = (totalSeconds - bean.getStampTime()) / 60;
//                if (replyTime < 1) {
//                    replyTime = 1;
//                }
//            }
//
//            if (replyTime < 60) {
//                value = String.format(context.getResources().getString(R.string.st_actor_info_minute), replyTime);
//            } else if (replyTime < (24 * 60)) {
//                value = String.format(context.getResources().getString(R.string.st_actor_info_hour), replyTime / 60);
//            } else {
//                value = DateUtil.getLongToDate(DateUtil.sdf10, bean.getStampTime());
//            }
            holder.time.setText(value);

            holder.content.setText(bean.getContent());
//            holder.content.setMaxLines(5);
//            holder.content.post(new Runnable() {
//
//                @Override
//                public void run() {
//                    holder.content.setEllipsis("...");
//                }
//            });

            final int count = bean.getCommentCount();
            if (count < 1) {
                value = context.getResources().getString(R.string.st_actor_info_comment);
            } else if (count < 1000) {
                value = String.valueOf(count);
            } else {
                value = context.getResources().getString(R.string.st_actor_info_999);
            }

            // 点击"回复"
            holder.reply.setText(value);
            holder.reply.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (0 == arg0 && 0 == comments.get(arg0).getTweetId()) {
                        return;
                    }

                    // if no peply and not login, login now.
                    if (0 == count && !UserManager.Companion.getInstance().isLogin()) {
//                        startActivityForResult(LoginActivity.class, new Intent());
                        UserLoginKt.gotoLoginPage(context, null, 0);
                        return;
                    }

                    lastSelectedPos = arg0;
                    App.getInstance().isPraised = bean.isPraise();
                    App.getInstance().totalPraise = bean.getTotalPraise();

                    int commentId = comments.get(arg0).getTweetId();
                    // 埋点
                    Map<String, String> businessParam = new HashMap<>(2);
                    businessParam.put(StatisticConstant.PERSON_ID, actorId);
                    businessParam.put(StatisticConstant.COMMENT_ID, String.valueOf(commentId));
                    StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "reply", "", "", "", businessParam);
                    StatisticManager.getInstance().submit(statisticBean);


                    // 短评详情页
                    if (mUgcProvider != null) {
                        mUgcProvider.launchDetail(commentId, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,0L,false);
                    }
                }
            });
            // 点击回复Icon
            holder.replyIcon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (0 == arg0 && 0 == comments.get(arg0).getTweetId()) {
                        return;
                    }

                    // if no reply and not login, login now.
                    if (0 == count && !UserManager.Companion.getInstance().isLogin()) {
//                        startActivityForResult(LoginActivity.class, new Intent());
                        UserLoginKt.gotoLoginPage(context, null, 0);
                        return;
                    }

                    lastSelectedPos = arg0;
                    App.getInstance().isPraised = bean.isPraise();
                    App.getInstance().totalPraise = bean.getTotalPraise();

                    int commentId = comments.get(arg0).getTweetId();
                    // 埋点
                    Map<String, String> businessParam = new HashMap<>(2);
                    businessParam.put(StatisticConstant.PERSON_ID, actorId);
                    businessParam.put(StatisticConstant.COMMENT_ID, String.valueOf(commentId));
                    StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "reply", "", "", "", businessParam);
                    StatisticManager.getInstance().submit(statisticBean);


                    // 短评详情页
                    if (mUgcProvider != null) {
                        mUgcProvider.launchDetail(commentId, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,0L,false);
                    }
                }
            });

            final ImageView viewAnim = holder.praise_icon_animation;
            final ImageView viewDef = holder.praise_icon;
            final TextView textView = holder.praise;

            holder.praise_icon_animation.setVisibility(View.INVISIBLE);

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

            // 点击"赞"
            holder.praise.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    lastSelectedPos = arg0;

                    if (!UserManager.Companion.getInstance().isLogin()) {
                        praiseId = String.valueOf(comments.get(arg0).getTopicId());
//                        context.startActivityForResult(LoginActivity.class, new Intent(), 3);
                        UserLoginKt.gotoLoginPage(context, null, 3);
                        return;
                    }

                    if (0 == arg0 && 0 == comments.get(arg0).getTweetId()) {
                        return;
                    }

                    // 埋点
                    Map<String, String> businessParam = new HashMap<>(2);
                    businessParam.put(StatisticConstant.PERSON_ID, actorId);
                    businessParam.put(StatisticConstant.COMMENT_ID, String.valueOf(comments.get(arg0).getTweetId()));

                    final boolean isCancel = bean.isPraise();
                    comments.get(arg0).setPraise(!bean.isPraise());

                    int thumbsUpCount = 0;
                    if (comments.get(arg0).isPraise()) {
                        thumbsUpCount = comments.get(arg0).getTotalPraise() + 1;
                        comments.get(arg0).setTotalPraise(thumbsUpCount);
                        businessParam.put(StatisticConstant.THUMBS_UP_STATE, StatisticEnum.EnumThumbsUpState.THUMBS_UP.getValue());
                    } else {
                        thumbsUpCount = comments.get(arg0).getTotalPraise() - 1;
                        comments.get(arg0).setTotalPraise(thumbsUpCount);
                        businessParam.put(StatisticConstant.THUMBS_UP_STATE, StatisticEnum.EnumThumbsUpState.CANCEL.getValue());
                    }
                    businessParam.put(StatisticConstant.THUMBS_UP_COUNT, String.valueOf(thumbsUpCount));
                    StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "thumbsUp", "", "", "", businessParam);
                    StatisticManager.getInstance().submit(statisticBean);

                    updatePraiseValue(viewAnim, viewDef, textView, comments.get(arg0).getTotalPraise(),
                            comments.get(arg0).isPraise());

                    mPraiseCommentApi.postEditPraise("",
                            String.valueOf(comments.get(arg0).getTweetId()),
                            String.valueOf(CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT),
                            isCancel,
                            new NetworkManager.NetworkListener<CommBizCodeResult>() {
                                @Override
                                public void onSuccess(CommBizCodeResult result, String showMsg) {

                                }
                                @Override
                                public void onFailure(NetworkException<CommBizCodeResult> exception, String showMsg) {
                                }
                            });


//                    UIUtil.showLoadingDialog(context);
//                    Map<String, String> parameterList = new ArrayMap<String, String>(2);
//                    parameterList.put("id", String.valueOf(comments.get(arg0).getTweetId()));
//                    parameterList.put("relatedObjType", String.valueOf(78));
//                    HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, praiseCallback, null);
                }
            });

            holder.praise_icon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    lastSelectedPos = arg0;

                    if (!UserManager.Companion.getInstance().isLogin()) {
                        praiseId = String.valueOf(comments.get(arg0).getTopicId());
//                        context.startActivityForResult(LoginActivity.class, new Intent(), 3);
                        UserLoginKt.gotoLoginPage(context, null, 3);
                        return;
                    }

                    if (0 == arg0 && 0 == comments.get(arg0).getTweetId()) {
                        return;
                    }

                    // 埋点
                    Map<String, String> businessParam = new HashMap<>(2);
                    businessParam.put(StatisticConstant.PERSON_ID, actorId);
                    businessParam.put(StatisticConstant.COMMENT_ID, String.valueOf(comments.get(arg0).getTweetId()));

                    final boolean isCancel = bean.isPraise();
                    comments.get(arg0).setPraise(!bean.isPraise());

                    int thumbsUpCount = 0;
                    if (comments.get(arg0).isPraise()) {
                        thumbsUpCount = comments.get(arg0).getTotalPraise() + 1;
                        comments.get(arg0).setTotalPraise(thumbsUpCount);
                        businessParam.put(StatisticConstant.THUMBS_UP_STATE, StatisticEnum.EnumThumbsUpState.THUMBS_UP.getValue());
                    } else {
                        thumbsUpCount = comments.get(arg0).getTotalPraise() - 1;
                        comments.get(arg0).setTotalPraise(thumbsUpCount);
                        businessParam.put(StatisticConstant.THUMBS_UP_STATE, StatisticEnum.EnumThumbsUpState.CANCEL.getValue());
                    }
                    businessParam.put(StatisticConstant.THUMBS_UP_COUNT, String.valueOf(thumbsUpCount));
                    StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "thumbsUp", "", "", "", businessParam);
                    StatisticManager.getInstance().submit(statisticBean);

                    updatePraiseValue(viewAnim, viewDef, textView, comments.get(arg0).getTotalPraise(),
                            comments.get(arg0).isPraise());

                    mPraiseCommentApi.postEditPraise("",
                            String.valueOf(comments.get(arg0).getTweetId()),
                            String.valueOf(CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT),
                            isCancel,
                            new NetworkManager.NetworkListener<CommBizCodeResult>() {
                                @Override
                                public void onSuccess(CommBizCodeResult result, String showMsg) {

                                }
                                @Override
                                public void onFailure(NetworkException<CommBizCodeResult> exception, String showMsg) {
                                }
                            });

//                    UIUtil.showLoadingDialog(context);
//                    Map<String, String> parameterList = new ArrayMap<String, String>(2);
//                    parameterList.put("id", String.valueOf(comments.get(arg0).getTweetId()));
//                    parameterList.put("relatedObjType", String.valueOf(78));
//                    HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, praiseCallback, null);
                }
            });

            // 点击item
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
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        //
        private void updatePraise(TextView view, int total, boolean isPraised) {
            // set color icon
            String value;
            if (total < 1) {
                value = getString(R.string.st_actor_info_support);
                // set default icon
            } else if (total < 1000) {
                value = String.valueOf(total);
            } else {
                value = getString(R.string.st_actor_info_999);
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

        public void addComment(final CommentBean comment) {
            if (null != comment) {
                this.comments.add(0, comment);
                this.notifyDataSetChanged();
            }
        }

        public List<CommentBean> getComments() {
            return this.comments;
        }

        public void clear() {
            this.comments.clear();
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

    /*
     * 1. request actor information, user's messages, and the account state. 2.
     * request finished, set value and update UI. 3. click effect.
     */
    private int lastSelectedPos = 0;
    private String praiseId;
    // id, structure and requests
    public String actorId;
    public ActorInfoBean actorInfo;

    public boolean isLiking;
    public boolean isHating;

    // request callback
    private RequestCallback personCommentsCallback = null;
    private RequestCallback praiseCallback;
    // UI variables
    private TitleOfNormalView title;
    private int titleHeight;

    //    private ImageView poster_background;
    private IRecyclerView listMsgs;
    private LoadMoreFooterView loadMoreFooterView;

    public ShowWholeNameView showNameView;
    private View guide;
    private RelativeLayout root;
    private View overlay;
    private boolean hasGuide = false;
    // movies
    private ActorDetailMsgsAdapter msgAdapter;
    private ITitleViewLActListener listener;

    public int indexMovies = 1;
    private int indexComments = 1;
    private boolean queryCommentsFinished;
    private boolean queryAgain;

    private View header;
    private int defaultDistance;
    private int flag = 1;// 用于ScrollListener
    private boolean startCal;
    private WebView webView;
    private OnItemClickListener onItemClickListener;

    private ActorBasicInfoView basicView;
    private ActorMoviesView movies;
    private ActorHotPlayingView hotPlaying;
    private ActorExperiencesView experiencesView;
    private ActorImagesView imagesView;
    private ActorRelationsView relationsView;
    private ActorHonorsView honorsView;
    private View newsView;
    private TextView message_title;
    public final String logxParamType = "personID";

    private AppraiseOfPerson comments;
    private CommentsInputView commentsHolder;
    private View scale_cover;
    private PersonApi mPersonApi;

    public static final String MOVIE_PERSOM_ID = "movie_person_id"; // 影人id
    public static final String KEY_MOVIE_PERSOM_NAME = "movie_person_name"; // 影人（导演/演员）name
    private static final String TAG_PRAISE = "person_comment_praise";
    // 收藏动作
    private static final int COLLECT_ACTION_ADD = 1;
    private static final int COLLECT_ACTION_CANCEL = 2;
    // 评分值
    private static final int RATING_LIKE = 8;
    private static final int RATING_DISLIKE = 3;
    // 获取影人作品列表接口参数：排序 0默认
    private static final int PERSON_MOVIE_LIST_ORDER_DEFUALT = 0;

    private PraiseCommentApi mPraiseCommentApi;
    private TicketApi mTicketApi;
    private IUgcProvider mUgcProvider = ProviderExtKt.getProvider(IUgcProvider.class);

    @Override
    protected boolean enableSliding() {
        return true;
    }

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        startCal = false;

        titleHeight = this.getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        queryCommentsFinished = false;
        queryAgain = false;

        actorId = getIntent().getStringExtra(App.getInstance().KEY_MOVIE_PERSOM_ID);

        setPageLabel(StatisticActor.PN_STAR_DETAIL);
        putBaseStatisticParam(StatisticConstant.PERSON_ID, actorId);

        mPersonApi = new PersonApi();
        mPraiseCommentApi = new PraiseCommentApi();
        mTicketApi = new TicketApi();

        listener = new ITitleViewLActListener() {

            @Override
            public void onEvent(ActionType type, String content) {
                switch (type) {
                    case TYPE_FAVORITE:
                        // 埋点
                        String collectState =
                                Boolean.valueOf(content) ? StatisticEnum.EnumCollectState.COLLECT.getValue() : StatisticEnum.EnumCollectState.CANCEL.getValue();
                        Map<String, String> businessParam = new HashMap<>(2);
                        businessParam.put(StatisticConstant.PERSON_ID, actorId);
                        businessParam.put(StatisticConstant.COLLECT_STATE, collectState);
                        StatisticPageBean statisticBean1 = assemble(StatisticActor.STAR_DETAIL_TOP_NAV, "", "collect", "", "", "", businessParam);
                        StatisticManager.getInstance().submit(statisticBean1);

                        if (!UserManager.Companion.getInstance().isLogin()) {
                            title.restoreFavorite();
                            JumpUtil.startLoginActivity(ActorViewActivity.this, statisticBean1.toString());
                            return;
                        }

                        // 收藏/取消收藏
                        collect(Boolean.valueOf(content));

//                        if (Boolean.valueOf(content)) {
//                            addFavorite();
//                        } else {
//                            cancelFavorite();
//                        }

                        break;
                    case TYPE_SHARE:
                        // 埋点
                        StatisticPageBean statisticBean2 = assemble(StatisticActor.STAR_DETAIL_TOP_NAV, "", "shareBtn", "", "", "", getBaseStatisticParam());
                        StatisticManager.getInstance().submit(statisticBean2);

                        ShareExtJava.showShareDialog(
                                ActorViewActivity.this,
                                String.valueOf(CommConstant.SHARE_TYPE_PERSON),
                                actorId,
                                null
                        );

                        break;
                    case TYPE_BACK:
                    default:
                        break;
                }

            }
        };
    }

    private void initHeader() {
        LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        header = inflate.inflate(R.layout.actor_detail_listview_header, null);
        basicView = header.findViewById(R.id.actor_basic);
        movies = header.findViewById(R.id.actor_movies);
        newsView = header.findViewById(R.id.actor_news);
        hotPlaying = header.findViewById(R.id.actor_hot_playing);
        honorsView = header.findViewById(R.id.actor_honors);
        experiencesView = header.findViewById(R.id.actor_experiences);
        imagesView = header.findViewById(R.id.actor_images);
        relationsView = header.findViewById(R.id.actor_relations);
        message_title = header.findViewById(R.id.message_title);

        newsView.setVisibility(View.GONE);
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.actor_detail_listview);
        initHeader();
        root = findViewById(R.id.view_root);
        overlay = findViewById(R.id.preload_layout);

        scale_cover = this.findViewById(R.id.scale_cover);
        scale_cover.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scale_cover.setVisibility(View.GONE);
                commentsHolder.setFocus(false);
                commentsHolder.setVisibility(View.GONE);
            }
        });

        View root = this.findViewById(R.id.navigation);
        title = new TitleOfNormalView(this, root, StructType.TYPE_NORMAL_SHOW_BACK_TITLE_SHARE_FAVORITE, " ", listener);
        title.setAlpha(0);
        title.setTitleTextAlpha(0, 0.0f);

        View showname = this.findViewById(R.id.whole_name);
        this.showNameView = new ShowWholeNameView(showname);
        this.showNameView.setVisibility(View.INVISIBLE);

        listMsgs = this.findViewById(R.id.root_list);
        listMsgs.setLayoutManager(new LinearLayoutManager(this));
        loadMoreFooterView = (LoadMoreFooterView) listMsgs.getLoadMoreFooterView();
        listMsgs.addHeaderView(header);

        msgAdapter = new ActorDetailMsgsAdapter(this, null);
        listMsgs.setIAdapter(msgAdapter);
        initOnItemClickListener();
        msgAdapter.setOnItemClickListener(onItemClickListener);

        initListEvent();

        //软件盘弹起后所占高度阀值
        final int keyHeight = FrameConstant.SCREEN_HEIGHT / 3;
        findViewById(R.id.actor_detail_listview).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
//                    Toast.makeText(ArticleDetailActivity.this, "监听到软键盘弹起...");
                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
//                    Toast.makeText(ArticleDetailActivity.this, "监听到软件盘关闭...");
                    scale_cover.setVisibility(View.GONE);
                    commentsHolder.setFocus(false);
                    commentsHolder.setVisibility(View.GONE);
                }
            }
        });


    }

    private void initOnItemClickListener() {
        onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (0 == pos && 0 == msgAdapter.getComments().get(pos).getTweetId()) {
                    return;
                }
                lastSelectedPos = pos;
                App.getInstance().isPraised = msgAdapter.getComments().get(pos).isPraise();
                App.getInstance().totalPraise = msgAdapter.getComments().get(pos).getTotalPraise();

                int commentId = msgAdapter.getComments().get(pos).getTweetId();
                // 埋点
                Map<String, String> businessParam = new HashMap<>(2);
                businessParam.put(StatisticConstant.PERSON_ID, actorId);
                businessParam.put(StatisticConstant.COMMENT_ID, String.valueOf(commentId));
                StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "details", "", "", "", businessParam);
                StatisticManager.getInstance().submit(statisticBean);

                // 短评详情页
                if (mUgcProvider != null) {
                    mUgcProvider.launchDetail(commentId, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,0L,false);
                }

//                JumpUtil.startTwitterActivity(ActorViewActivity.this, statisticBean.toString(),
//                        commentId, TwitterActivity.ACTIVITY_RESULT_CODE_ACTORCOMMENT, msgAdapter.getComments().get(pos).getNickname(), 5);
            }
        };
    }

    @Override
    protected void onInitEvent() {

        personCommentsCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                startCal = true;

                String value = getResources().getString(R.string.actor_detail_message_label);
                CommentPageBean pageBean = (CommentPageBean) o;
//                listMsgs.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        listMsgs.scrollToPosition(0);
//                    }
//                },100);

                if (null == pageBean) {
                    if (1 == indexComments) {
                        message_title.setText(String.format(value, 0));
                    }
                    queryCommentsFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    return;
                }

                final List<CommentBean> comments = pageBean.getList();
                if (0 == pageBean.getCount() || null == comments || comments.isEmpty()) {
                    queryCommentsFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    if (1 == indexComments) {
                        message_title.setText(String.format(value, 0));
                    }
                    return;
                }

                int count = pageBean.getCount() <= 0 ? 0 : pageBean.getCount();
                message_title.setText(String.format(value, count));
                indexComments++;

                // update list item now...
                if (queryAgain) {
                    msgAdapter.clear();
                    queryAgain = false;
                }

                // use new API to get values.
                requestCommentsPraised(comments);

//                listMsgs.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        listMsgs.scrollToPosition(0);
//                    }
//                },100);
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                overlay.setVisibility(View.GONE);

                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);

                if (1 == indexComments) {
                    String value = getResources().getString(R.string.actor_detail_message_label);
                    message_title.setText(String.format(value, 0));
                }
            }
        };

//        praiseCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//                // AddOrDelPraiseLogBean bean = (AddOrDelPraiseLogBean) o;
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//            }
//        };

    }


    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onUnloadData() {
    }

    @Override
    protected void onRequestData() {
        requestActorDetailInfo();

        // 热门点击
        mTicketApi.postSearchPoplarClick(TicketApi.SEARCH_POPULAR_CLICK_TYPE_PERSON,
                TicketApi.SEARCH_POPULAR_CLICK_SUB_TYPE_PERSON,
                actorId);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (showNameView != null && View.VISIBLE == this.showNameView.getVisibility()) {
                showNameView.setVisibility(View.INVISIBLE);
                return true;
            } else if (hasGuide) {
                hasGuide = false;
                this.root.removeView(this.guide);
                this.root.postInvalidate();
                return true;
            } else if (null != commentsHolder && View.VISIBLE == commentsHolder.getVisibility()) {
                scale_cover.setVisibility(View.GONE);
                commentsHolder.setFocus(false);
                commentsHolder.setVisibility(View.GONE);
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    // 请求获取影人详细信息
    private void requestActorDetailInfo() {
        final RequestCallback actorDetailInfoCallback = new RequestCallback() {

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onSuccess(final Object o) {
                startCal = true;

                ActorViewBean bean = (ActorViewBean) o;
                title.setTitleText(bean.getBackground().getNameCn());
                title.setTitleTextAlpha(0, 0.0f);

                actorInfo = bean.getBackground();

                // 获取影人作品列表
                requestPersonMovies(indexMovies, PERSON_MOVIE_LIST_ORDER_DEFUALT);

//                final Type type = new TypeToken<List<FilmographyBean>>() {
//                }.getType();
//                // Person/Movie.api?personId={0}&pageIndex={1}&orderId={2}
//                final Map<String, String> param = new HashMap<String, String>(3);
//                param.put("personId", actorId);
//                param.put("pageIndex", String.valueOf(indexMovies));
//                param.put("orderId", String.valueOf(0));
//                HttpUtil.get(ConstantUrl.GET_FILMOGRAPHIES, param, null, filmographiesCallback, 180000, type, 0);

                // query other informations.
                if (UserManager.Companion.getInstance().isLogin()) {
                    // 当前用户的收藏，喜欢状态
                    getPersonDynamic();
//                    doGetTargetObjStatus();
                }

                // Comment.api?personId={0}&pageIndex={1}
                Map<String, String> param1 = new HashMap<>(2);
                param1.put("personId", actorId);
                param1.put("pageIndex", String.valueOf(indexComments));
                HttpUtil.get(ConstantUrl.GET_PERSON_COMMENTS, param1, CommentPageBean.class, personCommentsCallback, 180000, null, 0);
                //
                boolean skiped = App.getInstance().getPrefsManager()
                        .getBoolean("actor_guide_skip" + actorId);
                if (null != actorInfo.getStyle() && 1 == actorInfo.getStyle().getIsLeadPage() && !skiped) {
                    // show the guide ui now.
                    guide = getLayoutInflater().inflate(R.layout.actor_detail_guide, null);

                    root.addView(guide);
                    hasGuide = true;

                    webView = guide.findViewById(R.id.webview);
                    final ImageView imageView = guide.findViewById(R.id.header);
                    View reload = guide.findViewById(R.id.reload);
                    reload.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            webView.setVisibility(View.VISIBLE);
                            webView.reload();

                        }
                    });

                    TextView skip = guide.findViewById(R.id.skip);
                    skip.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            App.getInstance().getPrefsManager()
                                    .putBoolean("actor_guide_skip" + actorId, true);
                            root.removeView(guide);
                            hasGuide = false;
                        }
                    });

                    if (!TextUtils.isEmpty(actorInfo.getStyle().getLeadUrl())) {
                        imageView.setVisibility(View.INVISIBLE);
                        webView.setInitialScale(100);
                        UAUtils.changeWebViewUA(webView);
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        webView.setWebChromeClient(new WebChromeClient() {

                            @Override
                            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                                result.confirm();
                                return true;
                            }

                            @Override
                            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                                result.confirm();
                                return true;
                            }

                        });
                        
                        new JSInterfaceNative(webView, "mtime")
                                .getJsCenter().setJsOpenAppLinkListener(new JSOpenAppLinkListener() {
                            @Override
                            public void openAppLinkClient(OpenAppLinkBean bean) {
                                String refer = StatisticManager.getH5Refer(webView.getUrl());
                                ApplinkManager.jump4H5(ActorViewActivity.this, bean.getData().getApplinkData(), refer);
                            }
                        });
                        
                        webView.setWebViewClient(new WebViewClient() {
                            
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                if (ToolsUtils.isVisit(url)) {
//                                    JumpToAdv jumpToAdv = new JumpToAdv();
//                                    jumpToAdv.isOpenH5 = false;
//                                    String refer = StatisticManager.getH5Refer(view.getUrl());
//                                    jumpToAdv.Jump(ActorViewActivity.this, url, -1, webView, requestCode, false, false, null, null, refer);
                                    return super.shouldOverrideUrlLoading(view, url);
                                }
                                return true;
                            }

                            @Override
                            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    if (null != view && null != request && null != request.getUrl()) {
                                        String url = request.getUrl().toString();
                                        if (ToolsUtils.isVisit(url)) {
                                            return super.shouldInterceptRequest(view, request);
                                        }
                                    }
                                }
                                return null;
                            }

                            @Override
                            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                                if (null != view && !TextUtils.isEmpty(url) && ToolsUtils.isVisit(url)) {
                                    return super.shouldInterceptRequest(view, url);
                                }

                                return null;
                            }

                            @Override
                            public void onReceivedError(WebView view, int errorCode, String description,
                                                        String failingUrl) {
                                view.stopLoading();
                                view.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler,
                                                           android.net.http.SslError error) {
                                handler.proceed();
                            }

                        });

                        webView.loadUrl(actorInfo.getStyle().getLeadUrl());
                    } else if (!TextUtils.isEmpty(actorInfo.getStyle().getLeadImg())) {
                        webView.setVisibility(View.INVISIBLE);
                        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                                if (null != response.getBitmap()) {
                                    imageView.setImageBitmap(response.getBitmap());
                                }
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                imageView.setVisibility(View.INVISIBLE);
                            }
                        };
                        volleyImageLoader.displayImage(actorInfo.getStyle().getLeadImg(), imageView, 0, 0, listener);

                    } else {
                        // if get nothing, jump this view
                        root.removeView(guide);
                        hasGuide = false;
                    }
                }

                initViewValues(actorInfo);
                initAD(bean.getAdvertisement());
                // 影人新闻（本期不上，先隐藏）
//                requestNews();

                // query the next page with movies

            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();

                UIUtil.showLoadingFailedLayout(ActorViewActivity.this, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //这里填写原来的click代码
                        onRequestData();
                    }
                });

                startCal = true;
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }
        };

        UIUtil.showLoadingDialog(ActorViewActivity.this);
        LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                if (null != locationInfo) {
                    // Person/Detail.api?personId={0}&locationId={1}
                    // PersonDetailAdvertisement.api?locationId={0}&personId={1}
                    Map<String, String> param = new HashMap<>(2);
                    param.put("cityId", locationInfo.getCityId());
                    param.put("personId", actorId);
                    HttpUtil.get(ConstantUrl.GET_ACTOR_DETAIL_INFO, param, ActorViewBean.class, actorDetailInfoCallback);
                }
            }

            @Override
            public void onLocationFailure(LocationException e) {
                onLocationSuccess(LocationHelper.getDefaultLocationInfo());
            }
        });
    }

    private void initViewValues(final ActorInfoBean actorInfo) {
        this.initQuizGame(actorInfo);
        basicView.onDrawView(this, actorInfo);
        hotPlaying.onDrawView(this, actorInfo);
        experiencesView.onDrawView(this, actorInfo);
        imagesView.onDrawView(this, actorInfo);
        relationsView.onDrawView(this, actorInfo);
        honorsView.onDrawView(this, actorInfo);
    }

    // 猜游戏
    private void initQuizGame(final ActorInfoBean actorInfo) {
        QuizGameView quizGameView = header.findViewById(R.id.quiz_game);
        if (actorInfo.getQuizGame() == null || TextUtils.isEmpty(actorInfo.getQuizGame().getUrl())) {
            quizGameView.setVisibility(View.GONE);
            return;
        }
        quizGameView.onRefreshViev(this, actorId, actorInfo.getQuizGame());
    }

    /**
     * 获取影人新闻
     */
    private void requestNews() {
        mPersonApi.getPersonNewsList(actorId, 1, 1, new NetworkManager.NetworkListener<HomeOriginalFeedListBean>() {
            @Override
            public void onSuccess(HomeOriginalFeedListBean result, String showMsg) {
                if(newsView != null && result != null && CollectionUtils.isNotEmpty(result.getList())) {
                    newsView.setVisibility(View.VISIBLE);
                    HomeOriginalFeedItemBean bean = result.getList().get(0);
                    TextView titleTv = newsView.findViewById(R.id.view_actor_news_title_tv);
                    titleTv.setText(bean.getTitle());
                    ImageView imageView = newsView.findViewById(R.id.view_actor_news_img_iv);
                    String ImageUrl = TextUtils.isEmpty(bean.getImgUrl(0)) ? "" : bean.getImgUrl(0);
                    ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                            .override(MScreenUtils.dp2px(150), MScreenUtils.dp2px(100))
                            .placeholder(R.drawable.default_image)
                            .load(ImageUrl)
                            .view(imageView)
                            .showload();
                } else {
                    newsView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(NetworkException<HomeOriginalFeedListBean> exception, String showMsg) {
                newsView.setVisibility(View.GONE);
            }
        });
    }

    public void loadMoreFilmgraphies() {
        if (-1 == indexMovies) {//没有更多数据了
            return;
        }

        // 获取影人作品列表
        requestPersonMovies(indexMovies, PERSON_MOVIE_LIST_ORDER_DEFUALT);

//        final Type type = new TypeToken<List<FilmographyBean>>() {
//        }.getType();
//        // Person/Movie.api?personId={0}&pageIndex={1}&orderId={2}
//        Map<String, String> param = new HashMap<String, String>(3);
//        param.put("personId", actorId);
//        param.put("pageIndex", String.valueOf(indexMovies));
//        param.put("orderId", String.valueOf(0));
//        HttpUtil.get(ConstantUrl.GET_FILMOGRAPHIES, param, null, filmographiesCallback, 180000, type, 0);
    }

    /**
     * 获取影人作品列表
     * @param pageIndex
     * @param orderId
     */
    private void requestPersonMovies(int pageIndex, int orderId) {
        if (mPersonApi == null) {
            return;
        }
        mPersonApi.getPersonMovies(actorId, pageIndex, orderId, new NetworkManager.NetworkListener<FilmographyListBean>() {
            @Override
            public void onSuccess(FilmographyListBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                overlay.setVisibility(View.GONE);
                if (1 == indexMovies) {
                    //加载第一页，清空影片列表
                    movies.cleanMoviesList();
                    movies.setVisibility(View.VISIBLE);
                }
                indexMovies++;

                if (result == null || CollectionUtils.isEmpty(result.getList())) {
                    indexMovies = -1;
                    return;
                }
                List<FilmographyBean> films = result.getList();
                movies.addFilmography(ActorViewActivity.this, films);
            }

            @Override
            public void onFailure(NetworkException<FilmographyListBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (indexMovies == 1) {
                    //加载第一页，且加载失败
                    movies.cleanMoviesList();
                    movies.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 获取用户对影人的动态信息
     */
    private void getPersonDynamic() {
        if (mPersonApi == null) {
            return;
        }
        mPersonApi.getPersonDynamic(actorId, new NetworkManager.NetworkListener<PersonDynamicBean>() {
            @Override
            public void onSuccess(PersonDynamicBean result, String showMsg) {
                if(result == null) {
                    return;
                }

                title.setFavoriate(result.isFavorite());
                // update like/unlike button state
                float rate = Float.parseFloat(result.getRating());
                isLiking = rate > 7 && rate <= 10;
                isHating = rate > 0 && rate <= 5;
                comments.setState(isLiking, isHating);
            }

            @Override
            public void onFailure(NetworkException<PersonDynamicBean> exception, String showMsg) {
            }
        });
    }


    /**
     * 当前用户的收藏，喜欢状态
     */
//    private void doGetTargetObjStatus() {
//        RequestCallback likedRateCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//
//                final TargetObjStatus status = (TargetObjStatus) o;
//                float rate = status.getRating();
//
//                if (1 == status.getIsFavorite()) {
//                    title.setFavoriate(true);
//                } else {
//                    title.setFavoriate(false);
//                }
//
//                // update like/unlike button state
//                isLiking = (rate > 7 && rate <= 10) ? true : false;
//                isHating = (rate > 0 && rate <= 5) ? true : false;
//                comments.setState(isLiking, isHating);
//                // 喜欢相关
////                basicView.updateLikeState(ActorViewActivity.this);
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//            }
//        };
//
//        // GetRelatedObjStatus.api?relateType={0}&relateId={1}
//        Map<String, String> param = new HashMap<>(2);
//        param.put("relateType", String.valueOf(App.getInstance().TARGET_OBJ_TYPE_ACTOR));
//        param.put("relateId", actorId);
//        HttpUtil.get(ConstantUrl.GET_RELATED_OBJ_STATUS, param, TargetObjStatus.class, likedRateCallback, 0, null, 0);
//    }

    /**
     * 收藏/取消收藏
     */
    private void collect(final boolean isAdd) {
        UIUtil.showLoadingDialog(this);
        mTicketApi.postCollect(isAdd ? COLLECT_ACTION_ADD : COLLECT_ACTION_CANCEL,
                CommConstant.COLLECTION_OBJ_TYPE_PERSON, actorId,
                new NetworkManager.NetworkListener<CollectResultBean>() {
            @Override
            public void onSuccess(CollectResultBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (result.getBizCode() == CollectResultBean.SUCCESS) {
                    // 成功
                    MToastUtils.showShortToast(getString(isAdd ? R.string.common_collect_success : R.string.common_cancel_collect_success));
                    title.setFavoriate(isAdd);
                    setResult(App.STATUS_PERSON_REFRESH);
                } else {
                    // 失败
                    MToastUtils.showShortToast(getString(isAdd ? R.string.common_collect_failed : R.string.common_cancel_collect_failed));
                    title.setFavoriate(!isAdd);
                }
                LiveEventBus.get(COLLECTION_OR_CANCEL).post(new CollectionState(CommConstant.COLLECTION_TYPE_PERSON));
            }

            @Override
            public void onFailure(NetworkException<CollectResultBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                title.setFavoriate(!isAdd);
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    /**
     * 添加收藏
     */
//    private void addFavorite() {
//        RequestCallback favoriteAddCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                final CommResultBean result = (CommResultBean) o;
//                if (result.isSuccess()) {
//                    MToastUtils.showShortToast(getString(R.string.common_collect_success));
//                    title.setFavoriate(true);
//                    setResult(App.STATUS_PERSON_REFRESH);
//                } else {
//                    MToastUtils.showShortToast(getString(R.string.common_collect_failed));
//                    title.setFavoriate(false);
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//
//                title.setFavoriate(false);
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };
//
//        UIUtil.showLoadingDialog(ActorViewActivity.this);
//
//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("id", actorId);
//        parameterList.put("type", "2");// "2"表示影人
//        HttpUtil.post(ConstantUrl.ADD_FAVORITE, parameterList, CommResultBean.class, favoriteAddCallback, null);
//    }

    // 取消收藏
//    private void cancelFavorite() {
//
//        RequestCallback favoriteCancelCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                final CommResultBean result = (CommResultBean) o;
//                if (result.isSuccess()) {
//                    MToastUtils.showShortToast(getString(R.string.common_cancel_collect_success));
//                    title.setFavoriate(false);
//                    ActorViewActivity.this.setResult(App.STATUS_PERSON_REFRESH);
//                } else {
//                    MToastUtils.showShortToast(getString(R.string.common_cancel_collect_failed));
//                    title.setFavoriate(true);
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                title.setFavoriate(true);
//
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };
//
//        UIUtil.showLoadingDialog(ActorViewActivity.this);
//
//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("relateId", actorId);
//        parameterList.put("type", "2");// "2"表示影人
//        HttpUtil.post(ConstantUrl.CANCEL_FAVORITE, parameterList, CommResultBean.class, favoriteCancelCallback, null);
//
//    }

    /**
     * 影人评分：喜欢/不喜欢
     */
    private void sendRating(int rating) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            JumpUtil.startLoginActivity(this, mBaseStatisticHelper.assemble().toString());
            return;
        }

        mPersonApi.postPersonRating(actorId, rating, new NetworkManager.NetworkListener<PersonRatingResultBean>() {
            @Override
            public void onSuccess(PersonRatingResultBean result, String showMsg) {
            }

            @Override
            public void onFailure(NetworkException<PersonRatingResultBean> exception, String showMsg) {

            }
        });
    }

    /**
     * 提交留言
     */
    private void postComment(String body) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            JumpUtil.startLoginActivity(this, mBaseStatisticHelper.assemble().toString());
            return;
        }

        UIUtil.showLoadingDialog(ActorViewActivity.this);

        // 获取新记录IDv2，此ID供未发布内容-保存记录API中recId使用
        mPersonApi.postCommunityRecordId(CONTENT_TYPE_FILM_COMMENT, new NetworkManager.NetworkListener<RecordId>() {
            @Override
            public void onSuccess(RecordId result, String showMsg) {
                if(result != null && result.getRecId() > 0) {
                    // 提交留言（影人短影评）
                    postRecord(result.getRecId(), body);
                } else {
                    MToastUtils.showShortToast("发布失败");
                    UIUtil.dismissLoadingDialog();
                }
            }

            @Override
            public void onFailure(NetworkException<RecordId> exception, String showMsg) {
                MToastUtils.showShortToast("发布失败");
                UIUtil.dismissLoadingDialog();
            }
        });
    }

    /**
     * 提交留言（影人短影评）
     * @param body
     */
    private void postRecord(Long recId, String body) {
        PostRecord record = new PostRecord();
        record.setRecId(recId);
        record.setFcPerson(Long.parseLong(actorId));
        record.setFcType(SHORT_COMMENT);
        record.setType(CONTENT_TYPE_FILM_COMMENT);
        record.setBody(body);

        mPersonApi.postCommunityRecord(record, new NetworkManager.NetworkListener<CommBizCodeResult>() {
            @Override
            public void onSuccess(CommBizCodeResult result, String showMsg) {
                UIUtil.dismissLoadingDialog();

                if(result != null && result.isSuccess()) {
                    commentsHolder.clear();

                    // update comment list now.
                    CommentBean bean = new CommentBean();
                    bean.setTweetId(0);
                    bean.setContent(body);
                    bean.setStampTime((int) (System.currentTimeMillis() / 1000));
                    bean.setNickname(UserManager.Companion.getInstance().getNickname());
                    bean.setUserImage(UserManager.Companion.getInstance().getUserAvatar());
                    msgAdapter.addComment(bean);
                }

            }

            @Override
            public void onFailure(NetworkException<CommBizCodeResult> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
            }
        });

    }

//    /**
//     * 提交短评/评分
//     * @param score
//     * @param comment
//     * @param showdlg
//     */
//    public void sendScore(int score, final String comment, final boolean showdlg) {
//        if (!UserManager.Companion.getInstance().isLogin()) {
//            final Intent intent = new Intent();
//            this.startActivityForResult(LoginActivity.class, intent);
//            return;
//        }
//        if (showdlg) {
//            UIUtil.showLoadingDialog(ActorViewActivity.this);
//        }
//        Map<String, String> parameterList = new ArrayMap<String, String>(6);
//        parameterList.put("personId", actorId);
//        parameterList.put("content", comment);
//        parameterList.put("rating", String.valueOf(score));
//        parameterList.put("longitude", String.valueOf(0));
//        parameterList.put("latitude", String.valueOf(0));
//        parameterList.put("locationName", "");
//        HttpUtil.post(ConstantUrl.COMMENT_ACTOR, parameterList, CommResultBean.class, new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//                if (showdlg) {
//                    UIUtil.dismissLoadingDialog();
//                }
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                commentsHolder.clear();
//
//                if (showdlg) {
//                    UIUtil.dismissLoadingDialog();
//
//                    // update comment list now.
//                    CommentBean bean = new CommentBean();
//                    bean.setTweetId(0);
//                    bean.setContent(comment);
//                    bean.setStampTime((int) (System.currentTimeMillis() / 1000));
//                    bean.setNickname(UserManager.Companion.getInstance().getNickname());
//                    bean.setUserImage(UserManager.Companion.getInstance().getUserAvatar());
//                    msgAdapter.addComment(bean);
//                }
//            }
//        }, null);
//    }

    private void loadAD(ADDetailBean item) {
        if (!ADWebView.show(item)) {
            return;
        }

        final ADWebView ad1 = header.findViewById(R.id.ad1);
        View adView1 = header.findViewById(R.id.ad1_seperate);
        final ADWebView ad2 = header.findViewById(R.id.ad2);
        View adView2 = header.findViewById(R.id.ad2_seperate);

        if (App.getInstance().AD_ACTOR_BANNER1.equalsIgnoreCase(item.getType())) {
            adView1.setVisibility(View.VISIBLE);
            ad1.setVisibility(View.VISIBLE);
            ad1.setFocusable(false);
            ad1.setOnAdItemClickListenner(new ADWebView.OnAdItemClickListenner() {
                @Override
                public void onAdItemClick(ADDetailBean item, String url) {
                    Map<String, String> businessParams1 = new HashMap<>(2);
                    businessParams1.put(StatisticConstant.PERSON_ID, actorId);
                    businessParams1.put(StatisticConstant.URL, url);
                    StatisticPageBean bean1 = assemble(StatisticActor.STAR_DETAIL_AD, "", "up", "", "", "", businessParams1);
                    StatisticManager.getInstance().submit(bean1);

                    ad1.setAdReferer(bean1.toString());
                }
            });

            ad1.load(this, item);
        } else if (App.getInstance().AD_ACTOR_BANNER2.equalsIgnoreCase(item.getType())) {
            adView2.setVisibility(View.VISIBLE);
            ad2.setVisibility(View.VISIBLE);
            ad2.setFocusable(false);
            ad2.setOnAdItemClickListenner(new ADWebView.OnAdItemClickListenner() {
                @Override
                public void onAdItemClick(ADDetailBean item, String url) {
                    Map<String, String> businessParams2 = new HashMap<>(2);
                    businessParams2.put(StatisticConstant.PERSON_ID, actorId);
                    businessParams2.put(StatisticConstant.URL, url);
                    StatisticPageBean bean2 = assemble(StatisticActor.STAR_DETAIL_AD, "", "down", "", "", "", businessParams2);
                    StatisticManager.getInstance().submit(bean2);

                    ad2.setAdReferer(bean2.toString());
                }
            });

            ad2.load(this, item);
        }
    }

    private void initListEvent() {
        commentsHolder = this.findViewById(R.id.comments_input_holder);
        commentsHolder.setListener(new CommentsInputView.CommentsInputViewListener() {
            @Override
            public void onEvent(String content) {
                LogWriter.e("checkComments", "发生评论中");
                commentsHolder.setVisibility(View.GONE);
                commentsHolder.setFocus(false);
                scale_cover.setVisibility(View.GONE);

                // 埋点
                StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "publish", "", "", "", getBaseStatisticParam());
                StatisticManager.getInstance().submit(statisticBean);

                postComment(content);
//                sendScore(0, content, true);
            }
        });

        commentsHolder.setVisibility(View.GONE);

        comments = this.findViewById(R.id.comments_view);
        comments.setVisibility(View.GONE);
        comments.setListener(new AppraiseOfPerson.OnApprasiePersonListener() {
            @Override
            public void onEvent(int state) {
                if (!UserManager.Companion.getInstance().isLogin()) {
                    UserLoginKt.gotoLoginPage(ActorViewActivity.this, null, 0);
//                    startActivityForResult(LoginActivity.class, new Intent());
                    return;
                }

                if (0 == state) {
                    // 埋点
                    StatisticPageBean statisticBean1 = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "commentInput", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(statisticBean1);
                    // 弹出评论框
                    scale_cover.setVisibility(View.VISIBLE);
                    commentsHolder.setVisibility(View.VISIBLE);
                    commentsHolder.setFocus(true);
                } else if (1 == state) {
                    // like
                    if (isLiking) {
                        return;
                    }
                    // 埋点
                    StatisticPageBean statisticBean1 = assemble(StatisticActor.STAR_DETAIL_BASIC_DATA, "", "like", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(statisticBean1);

                    isLiking = true;
                    isHating = false;

                    comments.setState(isLiking, isHating);
                    sendRating(RATING_LIKE);
                } else if (2 == state) {
                    // unlike
                    if (isHating) {
                        return;
                    }
                    // 埋点
                    StatisticPageBean statisticBean2 = assemble(StatisticActor.STAR_DETAIL_BASIC_DATA, "", "disLike", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(statisticBean2);

                    isLiking = false;
                    isHating = true;
                    comments.setState(isLiking, isHating);
                    sendRating(RATING_DISLIKE);
                }
            }
        });

        this.listMsgs.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(View loadMoreView) {
                if (loadMoreFooterView.canLoadMore()) {
                    if (!queryCommentsFinished) {
                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);

                        // 埋点
                        StatisticPageBean statisticBean = assemble(StatisticActor.STAR_DETAIL_COMMENT, "", "more", "", "", "", getBaseStatisticParam());
                        StatisticManager.getInstance().submit(statisticBean);

                        // Comment.api?personId={0}&pageIndex={1}
                        Map<String, String> param = new HashMap<String, String>(2);
                        param.put("personId", actorId);
                        param.put("pageIndex", String.valueOf(indexComments));
                        HttpUtil.get(ConstantUrl.GET_PERSON_COMMENTS, param, CommentPageBean.class, personCommentsCallback, 180000, null, 0);
                        return;
                    }

                    MToastUtils.showShortToast(getString(R.string.st_actor_info_data_loaded));
                }
            }
        });

        listMsgs.setOnIScrollListener(new OnIScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int[] posterLocation = new int[2];
                int[] commentLayoutLocation = new int[2];
                basicView.getPosterBackground().getLocationOnScreen(posterLocation);
                // pos is zero, means reach the bottom of header
                basicView.getPosterBackground().getLocationOnScreen(commentLayoutLocation);
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
                if (Math.abs(defaultDistance - posterLocation[1]) < 10) {
                    comments.setVisibility(View.GONE);
                } else {
                    comments.setVisibility(View.VISIBLE);
                }
                flag = posterLocation[1];
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != scale_cover && View.VISIBLE == scale_cover.getVisibility()) {
            return super.dispatchTouchEvent(ev);
        }

        LogWriter.e("checkComments", "dispatchtouchevent");
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

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
//        修复分享没有回调bug
        if (getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(arg0, arg1, arg2);
            }
        }

        if (5 == arg0) {
            // update praise
            msgAdapter.getComments().get(lastSelectedPos).setTotalPraise(App.getInstance().totalPraise);
            msgAdapter.getComments().get(lastSelectedPos).setPraise(App.getInstance().isPraised);
            msgAdapter.notifyDataSetChanged();

            return;
        }
        if (arg1 == App.STATUS_LOGIN && 3 == arg0) {
            //
            Map<String, String> parameterList = new ArrayMap<String, String>(2);
            parameterList.put("id", praiseId);
            parameterList.put("relatedObjType", String.valueOf(78));
            HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, new RequestCallback() {

                @Override
                public void onFail(Exception e) {
                    MToastUtils.showShortToast(getString(R.string.st_actor_info_support_failed) + e.getLocalizedMessage());
                    msgAdapter.notifyDataSetChanged();
                }

                @Override
                public void onSuccess(Object o) {
                    MToastUtils.showShortToast(getString(R.string.st_actor_info_support_success));
                    msgAdapter.restoreLastPraiseState();
                    msgAdapter.notifyDataSetChanged();
                }
            }, null);

            return;
        }

    }

    private void requestCommentsPraised(final List<CommentBean> beans) {
        final List<CommentBean> data = new ArrayList<CommentBean>();
        data.addAll(beans);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i).getTweetId());
            sb.append(",");
        }

        String ids = sb.toString();
        if (ids.length() < 1) {
            return;
        }

        // 批量查询点赞点踩状态
        mPraiseCommentApi.getPraiseStatList(TAG_PRAISE, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                ids, new NetworkManager.NetworkListener<PraiseStateList>() {
            @Override
            public void onSuccess(PraiseStateList result, String showMsg) {
                if (result == null || CollectionUtils.isEmpty(result.getList())) {
                    msgAdapter.setComments(beans);
                    msgAdapter.notifyDataSetChanged();
                    return;
                }
                List<PraiseState> praises = result.getList();
                for (int i = 0; i < praises.size(); i++) {
                    // 当前用户点赞、点踩状态：1.点赞 2.点踩 null.无  -1.当前用户未登录
                    boolean isPraise = praises.get(i).getCurrentUserPraise() != null
                            && praises.get(i).getCurrentUserPraise() == PraiseState.CURRENT_USER_PRAISE_STAT_PRAISE;
                    int totalPraise = Integer.parseInt(String.valueOf(praises.get(i).getUpCount()));
                    data.get(i).setPraise(isPraise);
                    data.get(i).setTotalPraise(totalPraise);
                }
                msgAdapter.setComments(data);
                msgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(NetworkException<PraiseStateList> exception, String showMsg) {
                msgAdapter.setComments(beans);
                msgAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        if(mPersonApi != null) {
            mPersonApi = null;
        }
        if(mPraiseCommentApi != null) {
            mPraiseCommentApi.cancelAllTags();
        }
        if(mTicketApi != null) {
            mTicketApi = null;
        }
    }

    private void initAD(ADTotalBean bean) {
        if (null == bean || !bean.getSuccess() || null == bean.getAdvList() || bean.getAdvList().size() < 1) {
            return;
        }

        int index = 0;
        int count = bean.getAdvList().size();

        ADDetailBean item = bean.getAdvList().get(index++);
        //
        loadAD(item);

        if (1 == count) {
            return;
        }
        // second
        item = bean.getAdvList().get(index++);
        loadAD(item);
    }


    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     * @param id
     */
    public static void launch(Context context, String refer, String id) {
        launch(context, refer, id, "");
    }

    public static void launch(Context context, String refer, String id, String name) {
        Intent launcher = new Intent(context, ActorViewActivity.class);
        launcher.putExtra(MOVIE_PERSOM_ID, id);
        if (!TextUtils.isEmpty(name)) {
            launcher.putExtra(KEY_MOVIE_PERSOM_NAME, name);
        }
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
