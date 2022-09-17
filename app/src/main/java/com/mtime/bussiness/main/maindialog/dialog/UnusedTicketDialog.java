package com.mtime.bussiness.main.maindialog.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mtime.R;
import com.mtime.base.dialog.BaseMDialog;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.recyclerview.CommonRecyclerAdapter;
import com.mtime.base.recyclerview.CommonViewHolder;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.main.maindialog.bean.UnusedTicketItemBean;
import com.mtime.bussiness.ticket.movie.activity.OrderDetailActivity;
import com.mtime.frame.App;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ZhouSuQiang on 2018/7/6
 * 【在线选座（未使用的影票）】
 */
public class UnusedTicketDialog extends BaseMDialog {
    private static final String KEY_TICKET_LIST = "key_ticket_list";

    public static UnusedTicketDialog newInstance(ArrayList<UnusedTicketItemBean> tickets) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_TICKET_LIST, tickets);
        UnusedTicketDialog dialog = new UnusedTicketDialog();
        dialog.setArguments(bundle);
        return dialog;
    }

    private ArrayList<UnusedTicketItemBean> mUnusedTicketItems;
    private DialogInterface.OnDismissListener mOnDismissListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnusedTicketItems = getArguments().getParcelableArrayList(KEY_TICKET_LIST);
    }

    @Override
    public int intLayoutId() {
        return R.layout.dialog_unused_ticket;
    }

    public UnusedTicketDialog setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
        if (null != getDialog()) {
            getDialog().setOnDismissListener(mOnDismissListener);
        }
        return this;
    }

    @Override
    public LayoutInflater onGetLayoutInflater(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = super.onGetLayoutInflater(savedInstanceState);
        if (null != getDialog()) {
            getDialog().setOnDismissListener(mOnDismissListener);
        }
        return layoutInflater;
    }

    @Override
    public void convertView(CommonViewHolder commonViewHolder, BaseMDialog baseMDialog) {
        RecyclerView recyclerView = commonViewHolder.setOnClickListener(R.id.dialog_unused_ticket_btn_close_tv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseMDialog.dismissAllowingStateLoss();
            }
        }).getView(R.id.dialog_unused_ticket_rv_list);

        CommonRecyclerAdapter<UnusedTicketItemBean> adapter = new CommonRecyclerAdapter<UnusedTicketItemBean>(recyclerView.getContext()) {

            @Override
            public int getItemLayoutId(int i) {
                return R.layout.item_dialog_unused_ticket_list;
            }

            private String formatTime(long time) {
                // 减去 8 小时时差
                time -= 8 * 60 * 60;
                time *= 1000L;
                Calendar c = Calendar.getInstance();
                c.add(GregorianCalendar.DAY_OF_MONTH, 1);
                if (MTimeUtils.isToday(new Date(time))) {
                    return MTimeUtils.format("MM月dd日 今天  HH:mm", time);
                }
                if (MTimeUtils.isTheSameDay(new Date(time), c.getTime())) {
                    return MTimeUtils.format("MM月dd日 明天  HH:mm", time);
                } else {
                    return MTimeUtils.format("yyyy年MM月dd日 HH:mm", time);
                }
            }

            @Override
            public void onBindItemHolder(CommonViewHolder commonViewHolder, UnusedTicketItemBean bean, int position) {
                commonViewHolder.setText(R.id.item_dialog_unused_ticket_list_movie_name_tv, bean.getMovieName())
                        .setText(R.id.item_dialog_unused_ticket_list_cinema_name_tv, bean.getCinemaName())
                        .setText(R.id.item_dialog_unused_ticket_list_ticket_count_tv, String.format(
                                getResources().getString(R.string.remind_ticket_count_unit), bean.getQuantity()))
                        .setText(R.id.item_dialog_unused_ticket_list_show_date_tv, formatTime(bean.getShowtime()))
                        .setVisibility(R.id.item_dialog_unused_ticket_list_bottom_line_iv,
                                position < getItemCount() - 1 ? View.VISIBLE : View.INVISIBLE);

                ImageHelper.with(baseMDialog, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.dp2px(75), MScreenUtils.dp2px(105))
                        .view(commonViewHolder.getView(R.id.item_dialog_unused_ticket_list_cover_iv))
                        .placeholder(R.drawable.default_image)
                        .load(bean.getImageUrl())
                        .showload();

                commonViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                        intent.putExtra(App.getInstance().KEY_SEATING_ORDER_ID, bean.getOrderId());
                        intent.putExtra(App.getInstance().PAY_ETICKET, false);
                        startActivity(intent);
                        dismissAllowingStateLoss();
                    }
                });
            }
        };
        adapter.setDatas(mUnusedTicketItems);
        recyclerView.setAdapter(adapter);

        if (null != mUnusedTicketItems && mUnusedTicketItems.size() > 2) {
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            params.height = MScreenUtils.dp2px(335);
            recyclerView.setLayoutParams(params);
        }
    }
}
