package com.mtime.bussiness.mine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;

/**
 * Created by wangdaban on 17/4/14.
 */

public class ArticleBottomItemView extends FrameLayout implements View.OnClickListener {
    private LinearLayout llUser;
    private TextView tvReply;
    private TextView tvPraise;
    private RelativeLayout imgMenu;
    private BottomItemActListener listener;

    public ArticleBottomItemView(Context context) {
        this(context, null);
    }

    public ArticleBottomItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_artical_bottom_item, this);
        llUser = findViewById(R.id.ll_user);
        tvReply = findViewById(R.id.tv_reply);
        tvPraise = findViewById(R.id.tv_praise);
        imgMenu = findViewById(R.id.img_menu);

        tvPraise.setOnClickListener(this);
        llUser.setOnClickListener(this);
        tvReply.setOnClickListener(this);
        imgMenu.setOnClickListener(this);

    }

    public ArticleBottomItemView setActListener(BottomItemActListener listener) {
        this.listener = listener;
        return this;
    }

    public ArticleBottomItemView setReplyCount(int replyCount) {
        if ( replyCount == 0) {
            tvReply.setVisibility(GONE);
        } else {
            tvReply.setText(getPrice(String.valueOf(replyCount)));
            tvReply.setVisibility(VISIBLE);
        }
        return this;
    }

    public ArticleBottomItemView setPraiseCount(int praiseCount) {
        if ( praiseCount == 0) {
            tvPraise.setVisibility(GONE);
        } else {
            tvPraise.setText(getPrice(String.valueOf(praiseCount)));
            tvPraise.setVisibility(VISIBLE);
        }
        return this;
    }


    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_user:
                listener.onEvent(ActionType.TYPE_USER);
                break;
            case R.id.tv_reply:
                listener.onEvent(ActionType.TYPE_REPLY);
                break;
            case R.id.tv_praise:
                listener.onEvent(ActionType.TYPE_PRAISE);
                break;
            case R.id.img_menu:
                listener.onEvent(ActionType.TYPE_MENU);
                break;
            default:
                break;
        }
    }

    public interface BottomItemActListener {
        /**
         * @param type 点击事件的类型，如下ActionType所示
         */
        void onEvent(final ActionType type);
    }

    public enum ActionType {
        TYPE_USER,
        TYPE_REPLY,
        TYPE_PRAISE,
        TYPE_MENU,
    }


    public static String getPrice(String priceStr) {
        if (priceStr == null) {
            return null;
        }
        priceStr = priceStr.trim();
        if (priceStr.indexOf(".") > -1) {
            priceStr = priceStr.substring(0, priceStr.indexOf("."));
        }
        int len = priceStr.length();
        StringBuffer priceBuffer = new StringBuffer();
        if (len > 8) {
            priceBuffer.append(priceStr.substring(0, len - 8));
            if (len % 4 > 0) {
                priceBuffer.append(".");
                priceBuffer.append(priceStr.substring(0, len % 4));
            }
            priceBuffer.append("亿");
        } else if (len > 4) {
            priceBuffer.append(priceStr.substring(0, len - 4));
            if (len % 4 > 0) {
                priceBuffer.append(".");
                priceBuffer.append(priceStr.charAt(len - 4));
            }
            priceBuffer.append("万");
        } else {
            priceBuffer.append(priceStr);
        }

        return priceBuffer.toString();
    }
}
