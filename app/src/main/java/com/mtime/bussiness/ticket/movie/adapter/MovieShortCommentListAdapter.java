package com.mtime.bussiness.ticket.movie.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;

import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.common.CommBizCodeResult;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.bussiness.ticket.movie.activity.MovieShortCommentsActivity;
import com.mtime.bussiness.ticket.movie.activity.TwitterActivity;
import com.mtime.bussiness.ticket.movie.bean.V2_MovieCommentBean;
import com.mtime.bussiness.video.api.PraiseCommentApi;
import com.mtime.common.utils.DateUtil;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.MyTextView;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * 影片短评列表adapter
 */
public class MovieShortCommentListAdapter extends RecyclerView.Adapter<MovieShortCommentListAdapter.ViewHolder> implements OnClickListener {
    private final BaseActivity mCtx;
    // TODO 这里根本就没有用到这个对象，还给值做什么？？
    private int mCommentCount; // 左边竖轴的评论数
    private List<V2_MovieCommentBean> mComments;
    private final List<V2_MovieCommentBean> mHotComments = new ArrayList<V2_MovieCommentBean>();
    private final android.view.animation.Animation animation;
    private List<ImageBean> images;
    private final String title;
    private boolean showTypeTag = true;//是否显示类型页签（影片详情里面不需要，影片短评列表需要）
    private OnItemClickListener onItemClickListener;
    private PraiseCommentApi mPraiseCommentApi = new PraiseCommentApi();

    public MovieShortCommentListAdapter(final BaseActivity ctx, final String title) {
        mCtx = ctx;
        animation = AnimationUtils.loadAnimation(mCtx, R.anim.zoomin);
        this.title = title;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void showTypeView(boolean isShow) {
        showTypeTag = isShow;
    }

    public void setCommentList(final List<V2_MovieCommentBean> comments) {
        mComments = comments;
        images = null;
        notifyDataSetChanged();
    }

    public void setCommentCount(final int commentCount) {
        mCommentCount = commentCount;
    }

    public void addCommentList(final List<V2_MovieCommentBean> comments) {
        if (mComments == null) {
            mComments = new ArrayList<V2_MovieCommentBean>();
        }
        List<V2_MovieCommentBean> removeComments = new ArrayList<V2_MovieCommentBean>();
        mComments.removeAll(mHotComments);
        for (V2_MovieCommentBean comment : comments) {
            if (comment.getHot()) {
                mHotComments.add(comment);
                removeComments.add(comment);//需要把需要删除的保存下来，不能在此直接删除
            }
        }
        comments.removeAll(removeComments);
        mComments.addAll(0, mHotComments);
        mComments.addAll(comments);
        notifyDataSetChanged();
    }

    /**
     * 将一条评论信息添加到评论列表的第一个位置，并刷新listview
     */
    public void addCommentToFirst(final V2_MovieCommentBean comment) {
        images = null;
        if (mComments != null) {
            mComments.add(mHotComments.size(), comment);
            notifyDataSetChanged();
        }
    }

    /**
     * 将一条评论信息添加到评论列表的第一个位置，并刷新listview
     */
    public void addCommentToFirst(final V2_MovieCommentBean comment, List<ImageBean> images) {
        if (mComments != null) {
            mComments.add(0, comment);
            this.images = images;
            notifyDataSetChanged();
        }
    }

    /**
     * 清除所有数据（下拉刷新时需要清除所有数据后再重新加载新数据）
     */
    public void clearCommentList() {
        if (mComments != null) {
            mComments.clear();
            notifyDataSetChanged();
        }
    }

    public List<V2_MovieCommentBean> getCommentList() {
        return mComments;
    }

    public int getCount() {
        if (mComments != null) {
            return mComments.size();
        } else {
            return 0;
        }
    }

    public Object getItem(final int position) {
        return mComments == null ? null : mComments.get(position);
    }

    public long getItemId(final int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mComments != null) {
            return mComments.size();
        }
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.movie_comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MovieShortCommentsActivity.lastSelectedPos = position;
        final V2_MovieCommentBean comment = mComments.get(position);
        if (comment == null) {
            return;
        }
        comment.setPosition(position);
        if (mComments.get(0) != null && mComments.get(0).getTweetId() == comment.getTweetId() && position == 0) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }
        if (showTypeTag) {
            if (mHotComments.size() > 0 && mHotComments.size() < mComments.size()) {//有最热评论
                if (mComments.get(0).getTweetId() == comment.getTweetId()) {//显示最热评论标题名字
                    holder.typeName.setTextColor(mCtx.getResources().getColor(R.color.color_ff670b));
                    holder.typeLine.setBackgroundColor(mCtx.getResources().getColor(R.color.color_ff670b));
                    holder.typeName.setText(mCtx.getResources().getString(R.string.comment_type_hot));
                    holder.type.setVisibility(View.VISIBLE);
                } else if (mComments.get(mHotComments.size()).getTweetId() == comment.getTweetId() && position == 0) {//显示最新短评标题
                    holder.typeName.setTextColor(mCtx.getResources().getColor(R.color.color_0075C4));
                    holder.typeLine.setBackgroundColor(mCtx.getResources().getColor(R.color.color_0075C4));
                    holder.typeName.setText(mCtx.getResources().getString(R.string.comment_type_default));
                    holder.type.setVisibility(View.VISIBLE);
                } else {
                    holder.type.setVisibility(View.GONE);
                }
            } else if (mComments.get(0).getTweetId() == comment.getTweetId() && position == 0) {//没有最热短评，那么第一条数据加上 最新短评 的页签（产品测试投票的时候调整的，记录下bugID 4312）
                holder.typeName.setTextColor(mCtx.getResources().getColor(R.color.color_0075C4));
                holder.typeLine.setBackgroundColor(mCtx.getResources().getColor(R.color.color_0075C4));
                holder.typeName.setText(mCtx.getResources().getString(R.string.comment_type_default));
                holder.type.setVisibility(View.VISIBLE);
            } else {
                holder.type.setVisibility(View.GONE);
            }
        } else {
            holder.type.setVisibility(View.GONE);
        }
        holder.praiseAnimIcon.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(comment.getCeimg())) {
            holder.commentImg.setVisibility(View.VISIBLE);
            holder.commentImg.setTag(R.id.comment_img, comment.getCeimg());

            ImageHelper.with(mCtx, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.SCALE_TO_FIT)
                    .override(460, 270)
                    .load(comment.getCeimg())
                    .view(holder.commentImg)
                    .placeholder(R.drawable.default_image)
                    .showload();
        } else if (images != null && images.size() > 0 && position == 0 && comment.getTweetId() == 0) {
            holder.commentImg.setVisibility(View.VISIBLE);
            ImageBean ib = images.get(0);
            ImageHelper.with(mCtx, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                    .load(ib.path)
                    .view(holder.commentImg)
                    .placeholder(R.drawable.default_image)
                    .showload();
        } else {
            holder.commentImg.setVisibility(View.GONE);
        }

        holder.reply.setTag(comment);
        holder.replyNum.setTag(comment);
        holder.reply.setOnClickListener(this);
        holder.replyNum.setOnClickListener(this);

        holder.praiseView.setTag(comment);
        holder.praiseView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!UserManager.Companion.getInstance().isLogin()) {
//                    Intent intent = new Intent();
//                    ((BaseActivity) (arg0.getContext())).startActivityForResult(LoginActivity.class, intent);
                    UserLoginKt.gotoLoginPage(arg0.getContext(), null, 0);
                    return;
                }

                final V2_MovieCommentBean tagComment = (V2_MovieCommentBean) (arg0.getTag());
                if (0 == tagComment.getTweetId()) {
                    return;
                }
                boolean isPraise = tagComment.getIsPraise();
                isPraise = !isPraise;
                tagComment.setIsPraise(isPraise);
                int mTotalPraise = tagComment.getTotalPraise();
                mTotalPraise += isPraise ? 1 : -1;
                tagComment.setTotalPraise(mTotalPraise);
                updatePraise(isPraise, mTotalPraise, holder.praiseAnimIcon, holder.praiseIcon, holder.praiseValue);
                holder.praiseIcon.startAnimation(animation);

                mPraiseCommentApi.postEditPraise("",
                        String.valueOf(comment.getTweetId()),
                        String.valueOf(CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT),
                        !isPraise,
                        new NetworkManager.NetworkListener<CommBizCodeResult>() {
                            @Override
                            public void onSuccess(CommBizCodeResult result, String showMsg) {
                                if(!result.isSuccess()) {
                                    setAddOrDelPraiseFail(tagComment, holder);
                                }
                            }
                            @Override
                            public void onFailure(NetworkException<CommBizCodeResult> exception, String showMsg) {
                                setAddOrDelPraiseFail(tagComment, holder);
                            }
                        });

                // TODO: 2020/10/21 页面数据由于迁移接口字段没有兼容，显示不出来，还未联调，联调后再删除旧代码

//                Map<String, String> parameterList = new HashMap<String, String>();
//                parameterList.put("id", String.valueOf(comment.getTweetId()));
//                parameterList.put("relatedObjType", String.valueOf(78));
//                HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, new RequestCallback() {
//
//                    @Override
//                    public void onFail(Exception e) {
//                        setAddOrDelPraiseFail(tagComment, holder);
//                    }
//
//                    @Override
//                    public void onSuccess(Object o) {
//                        AddOrDelPraiseLogBean bean = (AddOrDelPraiseLogBean) o;
//                        if (bean.isSuccess()) {
//                            int mTotalPraise = bean.getTotalCount();
//                            boolean isPraise = bean.isAdd();
//                            tagComment.setTotalPraise(mTotalPraise);
//                            tagComment.setIsPraise(bean.isAdd());
//                            updatePraise(isPraise, mTotalPraise, holder.praiseAnimIcon, holder.praiseIcon,
//                                    holder.praiseValue);
//                        } else {
//                            setAddOrDelPraiseFail(tagComment, holder);
//                        }
//
//                    }
//
//                });
            }
        });
        updatePraise(comment.getIsPraise(), comment.getTotalPraise(), holder.praiseAnimIcon, holder.praiseIcon,
                holder.praiseValue);

        holder.name.setText(comment.getCa());
        holder.content.setMaxLines(5);
        holder.content.setEllipsis("...");
        holder.content.setText(comment.getCe());
        if (comment.getCommentCount() > 0) {
            holder.replyNum.setText("" + (comment.getCommentCount() >= 1000 ? "999+" : comment.getCommentCount()));
        } else {
            holder.replyNum.setText("回复");
        }

        if (comment.getCr() > 0) {
            holder.score.setVisibility(View.VISIBLE);
            holder.score.setText(String.format("%.1f", comment.getCr()));
            if (comment.getCr() >= 10) {
                holder.score.setText("10");
            }
        } else {
            holder.score.setVisibility(View.INVISIBLE);
        }
        String value;

        long sendTime = comment.getCd() - 8 * 60 * 60L;
        if (0 == comment.getTweetId()) {
            sendTime += 8 * 60 * 60L;
        }

        long replyTime = (System.currentTimeMillis() / 1000 - sendTime) / 60;
        if (replyTime < 0) {
            Calendar now = Calendar.getInstance();
            TimeZone timeZone = now.getTimeZone();
            long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();
            long totalSeconds = totalMilliseconds / 1000;
            replyTime = (totalSeconds - comment.getCd()) / 60;
            if (replyTime < 1) {
                replyTime = 1;
            }
        }

        if (replyTime < 60) {
            value = String.format("%d分钟前", replyTime);
        } else if (replyTime < (24 * 60)) {
            value = String.format("%d小时前", replyTime / 60);
        } else {
            value = DateUtil.getLongToDate(DateUtil.sdf1, comment.getCd());
        }

        holder.time.setText(value + " - 评");
        if (comment.getCr() <= 0) {
            holder.time.setText(value);
        }

        ImageHelper.with(mCtx, ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .load(comment.getCaimg())
                .cropCircle()
                .view(holder.userImageView)
                .error(R.drawable.profile_default_head_h90)
                .placeholder(R.drawable.profile_default_head_h90)
                .showload();

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

    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    private void setAddOrDelPraiseFail(V2_MovieCommentBean tagComment, final ViewHolder holder) {
        boolean isPraise = tagComment.getIsPraise();
        isPraise = !isPraise;
        tagComment.setIsPraise(isPraise);
        int mTotalPraise = tagComment.getTotalPraise();
        mTotalPraise += isPraise ? 1 : -1;
        tagComment.setTotalPraise(mTotalPraise);
        mTotalPraise += isPraise ? 1 : -1;
        updatePraise(isPraise, mTotalPraise, holder.praiseAnimIcon, holder.praiseIcon, holder.praiseValue);
        MToastUtils.showShortToast(tagComment.getIsPraise() ? "取消赞失败" : "点赞失败");
    }

    private void updatePraise(boolean isPraise, int mTotalPraise, ImageView praiseAnimIcon, ImageView praiseIcon,
                              TextView praiseValue) {
        if (isPraise) {
            praiseAnimIcon.setImageResource(R.drawable.assist2);
            praiseIcon.setImageResource(R.drawable.assist2);
            praiseValue.setTextColor(mCtx.getResources().getColor(R.color.orange));
        } else {
            praiseValue.setTextColor(mCtx.getResources().getColor(R.color.color_777777));
            praiseAnimIcon.setImageResource(R.drawable.assist1);
            praiseIcon.setImageResource(R.drawable.assist1);
        }

        String value;
        if (mTotalPraise < 1) {
            value = "赞";
        } else if (mTotalPraise < 1000) {
            value = String.valueOf(mTotalPraise);
        } else {
            value = "999+";
        }
        praiseValue.setText(value);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tweet_head_triangle:
            case R.id.comment_reply_num:

                final V2_MovieCommentBean tagComment = (V2_MovieCommentBean) (v.getTag());
                if (0 == tagComment.getTweetId()) {
                    return;
                }

                // if no peply and not login, login now.
                if (0 == tagComment.getCommentCount() && !UserManager.Companion.getInstance().isLogin()) {
//                    mCtx.startActivityForResult(LoginActivity.class, new Intent());
                    UserLoginKt.gotoLoginPage(mCtx, null, 0);
                    return;
                }

                if (tagComment.getCommentCount() == 0) {
                    if (!UserManager.Companion.getInstance().isLogin()) {
//                        mCtx.twitterExtra = tagComment;//TODO换一种方法
//                        Intent intent = new Intent();
//                        mCtx.startActivityForResult(LoginActivity.class, intent, 3);
                        UserLoginKt.gotoLoginPage(mCtx, null, 3);
                        return;
                    }
                }

                App.getInstance().isMoviePraised = tagComment.getIsPraise();
                App.getInstance().totalMoviePraise = tagComment.getTotalPraise();

                Intent intent = new Intent();
                intent.putExtra("tweetId", tagComment.getTweetId());
                intent.putExtra("title", TextUtils.isEmpty(title) ? "" : title);
                intent.putExtra("assist_num", tagComment.getTotalPraise());
                intent.putExtra("reply_num", tagComment.getCommentCount());
                intent.putExtra("isassist", tagComment.getIsPraise());
                intent.putExtra("twitter_type", TwitterActivity.TWITTERTYPE_MOIVE);
                intent.putExtra(TwitterActivity.EXTRAS_KEY_SHOWINPUT, tagComment.getCommentCount() == 0);
                mCtx.startActivityForResult(TwitterActivity.class, intent);
                break;

            default:
                break;
        }
    }

    public class ViewHolder extends IViewHolder {
        ImageView userImageView;
        ImageView reply;
        // ImageView assist, animationAssist;
        View praiseView;
        TextView name;
        MyTextView content;
        TextView replyNum;
        TextView praiseValue;
        ImageView praiseIcon;
        ImageView praiseAnimIcon;
        TextView score;
        TextView time;
        View line;
        ImageView commentImg;
        LinearLayout type;
        TextView typeName;
        View typeLine;

        public ViewHolder(View itemView) {
            super(itemView);
            reply = itemView.findViewById(R.id.tweet_head_triangle);
            praiseAnimIcon = itemView.findViewById(R.id.praise_icon_animation);
            praiseIcon = itemView.findViewById(R.id.praise_icon);
            praiseValue = itemView.findViewById(R.id.praise);
            praiseView = itemView.findViewById(R.id.praise_region);

            userImageView = itemView.findViewById(R.id.comment_photo);
            BitmapDrawable bd = (BitmapDrawable) ContextCompat.getDrawable(mCtx, R.drawable.profile_default_head_h90);
            Bitmap bm = bd.getBitmap();
            userImageView.setImageBitmap(bm);
            name = itemView.findViewById(R.id.comment_name);
            content = itemView.findViewById(R.id.twitter_head_content);

            replyNum = itemView.findViewById(R.id.comment_reply_num);
            // assistNum = (TextView) itemView
            // .findViewById(R.id.comment_assist_num);
            score = itemView.findViewById(R.id.twitter_head_score);
            time = itemView.findViewById(R.id.twitter_head_comment);
            line = itemView.findViewById(R.id.gray_line);
            commentImg = itemView.findViewById(R.id.comment_img);
            commentImg.setVisibility(View.GONE);
            commentImg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String ceimg = (String) view.getTag(R.id.comment_img);
                    if (!TextUtils.isEmpty(ceimg)) {
//                        Intent intent = new Intent();
//                        intent.putExtra(CommentImageDetailActivity.KEY_IMAGE_PATH, ceimg);
//                        mCtx.startActivity(CommentImageDetailActivity.class, intent);
                        JumpUtil.startCommentImageDetailActivity(mCtx, ceimg);
                    }
                }
            });
            itemView.findViewById(R.id.reply_one).setVisibility(View.GONE);
            itemView.findViewById(R.id.reply_two).setVisibility(View.VISIBLE);
            type = itemView.findViewById(R.id.type);
            typeName = itemView.findViewById(R.id.type_name);
            typeLine = itemView.findViewById(R.id.type_line);
//            itemView.setTag(holder);
            type.setVisibility(View.GONE);
        }
    }

}
