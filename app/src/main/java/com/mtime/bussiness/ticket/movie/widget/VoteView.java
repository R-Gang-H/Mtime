package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.movie.bean.VoteDataItemBean;
import com.mtime.bussiness.ticket.movie.bean.VoteDataOptionBean;
import com.mtime.widgets.ScrollListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guangshun on 16-6-23.
 * 投票控件
 */
public class VoteView extends LinearLayout {
    public interface OnOptionClickListener {
        void onOptionClick(int topicId, VoteDataOptionBean optionBean);
    }

    private OnOptionClickListener optionClickListener;
    private final Context context;
    private TextView label;//标签名字（投票）
    private TextView title;//投票的主题内容&显示多选还是单选
    private TextView voterNum;//已投票的人数
    private ScrollListView list;//投票的选项
    private ImageView line;//分割线（只有影片详情和影片短评显示）


    private VoteDataItemBean voteData;
    private OptionAdapter adapter;

    public VoteView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public VoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();

    }

    public final void setOptionClickListener(OnOptionClickListener l) {
        optionClickListener = l;
    }

    public void setLabel(String labelTxt, boolean showSeperate) {
        if (!TextUtils.isEmpty(labelTxt)) {
            label.setText(labelTxt);
            label.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            label.setTextColor(getResources().getColor(R.color.color_0075c4));
        }
        line.setVisibility(showSeperate ? VISIBLE : GONE);
    }

    public void addVoteData(VoteDataItemBean data) {
        this.voteData = data;
        if (null != voteData) {
            setVisibility(VISIBLE);
            adapter.setOptions(voteData.getOptions());
            refreshTitle();
            refreshVoterNum();
        } else {
            setVisibility(GONE);
        }
    }


    private void refreshTitle() {
        if (null == title || null == voteData) {
            return;
        }
        String titleTxt = TextUtils.isEmpty(voteData.getTitle()) ? "" : voteData.getTitle();
        String rule = "[单选]";
        if (voteData.getIsMult()) {//多选
            rule = String.format("[多选,最多%d选择]", voteData.getMaxChooseCount());
        }
        title.setText(String.format("%1$s%2$s", titleTxt, rule));
        SpannableStringBuilder builder = new SpannableStringBuilder(title.getText().toString());
        ForegroundColorSpan ruleSpan = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.color_999999));
        builder.setSpan(ruleSpan, titleTxt.length(), title.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(builder);
    }

    private void refreshVoterNum() {
        if (null == voterNum || null == voteData || 1 > voteData.getMaxChooseCount()) {
            voterNum.setVisibility(GONE);
            return;
        }
        voterNum.setVisibility(VISIBLE);
        voterNum.setText(String.format("%d人已参与投票", voteData.getMaxChooseCount()));
        SpannableStringBuilder builder = new SpannableStringBuilder(voterNum.getText().toString());
        ForegroundColorSpan ruleSpan = new ForegroundColorSpan(ContextCompat.getColor(context,R.color.color_0075c4));
        builder.setSpan(ruleSpan, 0, voterNum.getText().toString().indexOf("人") + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        voterNum.setText(builder);
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.vote, this);
        label = findViewById(R.id.label);
        title = findViewById(R.id.title);
        voterNum = findViewById(R.id.voter_num);
        list = findViewById(R.id.list);
        line = findViewById(R.id.line);
        adapter = new OptionAdapter();
        list.setAdapter(adapter);
        setVisibility(GONE);
    }

    private class OptionAdapter extends BaseAdapter {
        private List<VoteDataOptionBean> options = new ArrayList<>();
        private int voterNum;

        public OptionAdapter() {
        }

        public void setOptions(List<VoteDataOptionBean> options) {
            this.options = options;
            this.voterNum = (null != voteData) ? voteData.getVoteCount() : 0;
            notifyDataSetChanged();
        }

        public String showMaxChooseMsg() {
            String msg = "";
            if (null != options && null != voteData) {
                int optionSelNum = 0;
                for (VoteDataOptionBean bean : options) {
                    if (bean.getIsSelf()) {
                        optionSelNum++;
                    }
                    if (optionSelNum >= voteData.getMaxChooseCount()) {
                        msg = String.format("最多可选%d项", voteData.getMaxChooseCount());
                        MToastUtils.showShortToast(msg);
                    }
                }
            }
            return msg;
        }

        @Override
        public int getCount() {
            return (null == options) ? 0 : options.size();
        }

        @Override
        public VoteDataOptionBean getItem(int i) {
            return (null == options) ? null : options.get(i);
        }

        @Override
        public long getItemId(int i) {
            return (null == options) ? 0 : options.get(i).getTopicOptionId();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (null == view) {
                view = new VoteItem(context);
                holder = new ViewHolder();
                holder.progressBar = view.findViewById(R.id.progress);
                holder.icon = view.findViewById(R.id.icon);
                holder.percent = view.findViewById(R.id.percent);
                holder.content = view.findViewById(R.id.content);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != optionClickListener && null != voteData) {
                            if (!TextUtils.isEmpty(showMaxChooseMsg())) {//如果投票数已经超过最大限制，在app端就不让请求投票接口了
                                return;
                            }
                            optionClickListener.onOptionClick(voteData.getTopicId(), ((VoteItem) view).getOptionBean());
                        }
                    }
                });
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            VoteDataOptionBean optionBean = getItem(i);
            if (null == optionBean) {
                return view;
            }
            ((VoteItem) view).setOptionBean(optionBean);
            holder.content.setText(optionBean.getContent());
            if (0 < voterNum) {
                holder.percent.setVisibility(VISIBLE);
                holder.percent.setText(String.format("%s％", optionBean.getVoteCount() * 100 / voterNum));
                holder.progressBar.setProgress(optionBean.getVoteCount() * 100 / voterNum);
            } else {
                holder.progressBar.setProgress(0);
                holder.percent.setVisibility(INVISIBLE);
            }
            showOption(optionBean, view, holder);
            holder.progressBar.getLayoutParams().height = ((VoteItem) view).getItemHeight();
            return view;
        }

        private void showOption(VoteDataOptionBean optionBean, View view, ViewHolder holder) {
            if (null != optionBean && null != holder) {

                if (optionBean.getIsSelf()) {//已经投的选项
                    view.setBackgroundResource(R.drawable.vote_item_border_blue);
                    holder.content.setTextColor(ContextCompat.getColor(context,R.color.color_005d98));
                    holder.percent.setTextColor(ContextCompat.getColor(context,R.color.color_0075c4));
                    holder.icon.setImageResource(R.drawable.vote_item_icon_blue);
                    holder.progressBar.setProgressDrawable(getDrawable(R.drawable.progressbar_drawable_vote_66acdc));
                } else {
                    view.setBackgroundResource(R.drawable.vote_item_border_white);
                    holder.content.setTextColor(ContextCompat.getColor(context,R.color.color_333333));
                    holder.percent.setTextColor(ContextCompat.getColor(context,R.color.color_777777));
                    holder.icon.setImageResource(R.drawable.vote_item_icon_white);
                    holder.progressBar.setProgressDrawable(getDrawable(R.drawable.progressbar_drawable_vote_cce3f3));
                }
            }

        }

        private Drawable getDrawable(int id) {
            final int version = Build.VERSION.SDK_INT;
            if (version >= 21) {
                return ContextCompat.getDrawable(context, id);
            } else {
                return ContextCompat.getDrawable(context,id);
            }
        }

        final class ViewHolder {
            ProgressBar progressBar;
            ImageView icon;
            TextView percent;
            TextView content;
        }
    }
}
