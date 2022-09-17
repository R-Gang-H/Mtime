package com.mtime.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.mtmovie.widgets.MessageBtn;

public class TitleOfNormalView extends BaseTitleView {

    private final LinearLayout ll_sign_in;
    private final Activity context;
    private final View rootView;
    private final View wholeView;
    private boolean isFavorited;
    private final ImageButton favorite;
    private final TextView title;

    private final View twoTitles;
    private final TextView topTitle;
    private final TextView bottomTitle;
    private final TextView feedBackList;
    private final TextView timer;
    private final ImageButton share;
    private final ImageView backBtn;
    private final TextView attention;
    private boolean isAttention;

    public ImageView getIvhead() {
        return ivhead;
    }

    private final ImageView ivhead;
    private boolean isCloseParent;


    public TitleOfNormalView(final Activity context, final View root, final StructType type, final String label, final ITitleViewLActListener listener) {
        isCloseParent = true;
        this.context = context;
        this.wholeView = root;
        this.rootView = root.findViewById(R.id.background);

        title = root.findViewById(R.id.title);
        if (!TextUtils.isEmpty(label)) {
            title.setText(label);
        }

        twoTitles = root.findViewById(R.id.two_tilte_region);
        twoTitles.setVisibility(View.GONE);
        topTitle = root.findViewById(R.id.top_title);
        bottomTitle = root.findViewById(R.id.bottom_title);
        feedBackList = root.findViewById(R.id.feedbacklist);
        timer = root.findViewById(R.id.title_timer);
        TextView packetRule = root.findViewById(R.id.title_red_packet);
        View back = root.findViewById(R.id.back);
        backBtn = root.findViewById(R.id.back_icon);
        share = root.findViewById(R.id.share);
        favorite = root.findViewById(R.id.favorite);
        attention = root.findViewById(R.id.attention);
        ivhead = root.findViewById(R.id.ivhead);
        ll_sign_in = root.findViewById(R.id.ll_sign_in);

        MessageBtn msg = root.findViewById(R.id.message);

        switch (type) {
            case TYPE_NORMAL_SHOW_ALL:
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_ONLY:
                title.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_SHARE_FAVORITE:
                title.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_FAVORITE:
                title.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_SHARE:
                title.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_MESSAGE_ONLY:
                back.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_TITLE_ONLY:
                back.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_FEEDBACKLIST:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.VISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_SHARE_FAVORITE:
                title.setVisibility(View.VISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_FAVORITE:
                title.setVisibility(View.VISIBLE);
                msg.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_TIMER:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                timer.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_PACKET_RULE:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                packetRule.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_TITLE_DRAEABLELEFT_ATTENTION:
                title.setVisibility(View.VISIBLE);
                ivhead.setVisibility(View.VISIBLE);
                attention.setVisibility(View.VISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                packetRule.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_TITLE_SIGN_IN:
                title.setVisibility(View.VISIBLE);
                ivhead.setVisibility(View.GONE);
                attention.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                packetRule.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_BACK, null);
                }

                if (isCloseParent) {
                    context.finish();
                }

            }
        });

        packetRule.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_PACKET, null);
                }
            }
        });
        share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SHARE, null);
                }

            }
        });

        favorite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isFavorited = !isFavorited;
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_FAVORITE, String.valueOf(isFavorited));
                }
            }
        });
        feedBackList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_FEEDBACKLIST, null);
                }
            }
        });
        title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_TITLE, null);
                }
            }
        });
        ivhead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_TITLE, null);
                }
            }
        });
        attention.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_ATTENTION, String.valueOf(isAttention));
                }
            }
        });

        ll_sign_in.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SIGN_IN, null);
                }
            }
        });

    }


    public void setIsFavorited() {
        isFavorited = !isFavorited;
    }

    /*public TitleOfNormalView(final Activity context, final View root, final StructType type, final String label, final ITitleViewLActListener listener) {
        try {
            this.context = context;
        } catch (ClassCastException e) {
            this.context = null;
        }
        this.wholeView = root;
        this.rootView = root.findViewById(R.id.background);

        title = (TextView) root.findViewById(R.id.title);
        if (!TextUtils.isEmpty(label)) {
            title.setText(label);
        }

        twoTitles = root.findViewById(R.id.two_tilte_region);
        twoTitles.setVisibility(View.GONE);
        topTitle = (TextView) root.findViewById(R.id.top_title);
        bottomTitle = (TextView) root.findViewById(R.id.bottom_title);
        feedBackList = (TextView) root.findViewById(R.id.feedbacklist);
        timer = (TextView) root.findViewById(R.id.title_timer);
        View back = root.findViewById(R.id.back);
        backBtn = (ImageView) root.findViewById(R.id.back_icon);
        share = (ImageButton) root.findViewById(R.id.share);
        favorite = (ImageButton) root.findViewById(R.id.favorite);
        attention = (TextView) root.findViewById(R.id.attention);
        ll_sign_in = (LinearLayout) root.findViewById(R.id.ll_sign_in);
        ivhead = (ImageView) root.findViewById(R.id.ivhead);
        MessageBtn msg = (MessageBtn) root.findViewById(R.id.message);

        switch (type) {
            case TYPE_NORMAL_SHOW_ALL:
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_ONLY:
                title.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_SHARE_FAVORITE:
                title.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_SHARE:
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_MESSAGE_ONLY:
                back.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_TITLE_ONLY:
                back.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_FEEDBACKLIST:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_SHARE_FAVORITE:
                title.setVisibility(View.VISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_NORMAL_SHOW_BACK_TITLE_TIMER:
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                timer.setVisibility(View.VISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_TITLE_DRAEABLELEFT_ATTENTION:
                title.setVisibility(View.VISIBLE);
                ivhead.setVisibility(View.VISIBLE);
                attention.setVisibility(View.VISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.INVISIBLE);
                break;
            case TYPE_TITLE_SIGN_IN:
                title.setVisibility(View.VISIBLE);
                ivhead.setVisibility(View.GONE);
                attention.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                favorite.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.INVISIBLE);
                feedBackList.setVisibility(View.INVISIBLE);
                ll_sign_in.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                context.finish();

                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_BACK, null);
                }

            }
        });

        share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SHARE, null);
                }

            }
        });

        favorite.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isFavorited = !isFavorited;
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_FAVORITE, String.valueOf(isFavorited));
                }
            }
        });
        feedBackList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_FEEDBACKLIST, null);
                }
            }
        });
        title.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_TITLE, null);
                }
            }
        });
        ivhead.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_TITLE, null);
                }
            }
        });
        attention.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_ATTENTION, String.valueOf(isAttention));
                }
            }
        });
        ll_sign_in.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SIGN_IN, null);
                }
            }
        });
    }*/

    @SuppressLint("NewApi")
    public void setTitleTextAlpha(float alpha, float minAlpha) {
        if (android.os.Build.VERSION.SDK_INT < 11 || title == null || TextUtils.isEmpty(title.getText().toString())) {
            return;
        }
        if (alpha < minAlpha) {
            alpha = 0.0f;
        }
        title.setAlpha(alpha);
    }

    @SuppressLint("NewApi")
    @Override
    public void setAlpha(float alpha) {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            return;
        }

        if (this.rootView.getAlpha() == alpha) {
            return;
        }

        float a = alpha < MIN_ALPHA ? 0 : alpha;
        this.rootView.setAlpha(a > 1 ? 1 : a);
    }

    @SuppressLint("NewApi")
    public void setAlpha(float alpha, float minAlpha) {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            return;
        }
        if (this.rootView.getAlpha() == alpha) {
            return;
        }
        float a = alpha < minAlpha ? 0 : alpha;
        this.rootView.setAlpha(a > 1 ? 1 : a);
    }

    public void setTitleMaxEms(int maxEms) {
        if (title != null) {
            title.setMaxEms(maxEms);
        }
    }

    public void setFavoriate(final boolean value) {
        isFavorited = value;
        favorite();
    }

    public boolean getFavoriate() {
        return this.isFavorited;
    }

    private void favorite() {
        if (isFavorited) {
            favorite.setBackgroundResource(R.drawable.title_favorite_on);
        } else {
            favorite.setBackgroundResource(R.drawable.title_bar_favorite);
        }
    }

    /**
     * 设置标题文字
     */
    public void setTitleText(final String label) {
        if (!TextUtils.isEmpty(label)) {
            title.setText(label);
        }

        twoTitles.setVisibility(View.INVISIBLE);
    }

    public void setTitleColor(final int color) {
        rootView.setBackgroundResource(color);
    }

    public void setTitleSize(float textSize) {
        title.setTextSize(textSize);
    }

    public void setTopTitleSize(float textSize) {
        topTitle.setTextSize(textSize);
    }

    public void setBottomTitleSize(float textSize) {
        bottomTitle.setTextSize(textSize);
    }

    public void setTitleStyle(Typeface typeface) {
        title.setTypeface(typeface);
    }

    public void setRightButtonText(final String label) {
        if (!TextUtils.isEmpty(label)) {
            feedBackList.setText(label);
        } else {
            feedBackList.setVisibility(View.INVISIBLE);
        }
    }

    public void setShareBackground(int resid) {
        share.setBackgroundResource(resid);
    }

    public void setTitles(final String topLabel, final String bottomLabel) {
        this.title.setVisibility(View.INVISIBLE);

        this.twoTitles.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(topLabel)) {
            this.topTitle.setVisibility(View.GONE);
        }
        this.topTitle.setText(topLabel);

        if (TextUtils.isEmpty(bottomLabel)) {
            this.bottomTitle.setVisibility(View.GONE);
        }
        this.bottomTitle.setText(bottomLabel);
    }

    public void restoreFavorite() {
        this.isFavorited = !this.isFavorited;
    }

    public void setShareVisibility(int visibile) {
        share.setVisibility(visibile);
    }

    public void setRootViewVisibility(int visibile) {
        this.wholeView.setVisibility(visibile);
    }

    public void setTimerViewVisibility(int visibile) {
        timer.setVisibility(visibile);
    }

    public void setTimerText(String text) {
        timer.setText(text);
    }

    public void setTimerTextColor(int textColor) {
        timer.setTextColor(textColor);
    }

    public void setCloseParent(boolean close) {
        isCloseParent = close;
    }

    public void showFavorite(final boolean show) {
        if (null != this.favorite) {
            this.favorite.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void showShare(final boolean show) {
        if (null != this.share) {
            this.share.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setCloseViewResource(int resid) {
        if (null != backBtn) {
            backBtn.setBackgroundResource(resid);
        }
    }

    public boolean isAttention() {
        return isAttention;
    }

    public void setAttention(boolean attention) {
        this.isAttention = attention;
        this.attention.setSelected(isAttention);
        if (isAttention) {
            this.attention.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_common_followed, 0, 0, 0);
//            this.attention.setText("已关注");
        } else {
            this.attention.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
//            this.attention.setCompoundDrawablePadding(context.getApplicationContext().getResources().getDimensionPixelSize(R.dimen.offset_5dp));
//            this.attention.setText("关注");
        }
    }

    /**
     * 设置标题栏是否显示
     *
     * @param isShow
     */
    public void setTitleVisibility(boolean isShow) {
        if (null != title) {
            title.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置背景图片
     */
    public void setBackGroundImg(int id) {
        if (null != rootView) {
            rootView.setBackgroundResource(id);
        }
    }

}
