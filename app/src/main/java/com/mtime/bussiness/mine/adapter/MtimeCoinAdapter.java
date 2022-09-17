package com.mtime.bussiness.mine.adapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.MtimeCoinBean;
import com.mtime.common.utils.Utils;
import com.mtime.util.ImageURLManager;
import com.mtime.util.MtimeUtils;
import com.mtime.widgets.NetworkImageView;

import java.util.List;

/**
 * Created by zhulinping on 2017/6/13.
 */

public class MtimeCoinAdapter extends RecyclerView.Adapter<MtimeCoinAdapter.ViewHolder> {

    BaseActivity mContext;
    List<MtimeCoinBean> mCoinList;

    public interface ExchangeCoinListener {
        void goExchange(int couponId, int couponType, String couponName, long num);
    }

    private ExchangeCoinListener mExchangeCoinListener;

    public MtimeCoinAdapter(BaseActivity context, List<MtimeCoinBean> list) {
        mContext = context;
        mCoinList = list;
    }

    public void setmExchangeCoinListener(ExchangeCoinListener listener) {
        mExchangeCoinListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_mtime_coin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MtimeCoinBean bean = mCoinList.get(position);

        if (position == mCoinList.size() - 1) {
            holder.iv_end.setVisibility(View.VISIBLE);
        } else {
            holder.iv_end.setVisibility(View.GONE);
        }


        holder.nameTv.setText(bean.getTitle());
        String money = MtimeUtils.formatPrice(bean.getAmount() / 100);
        if (bean.getStatus() == 1) {
            holder.exchangeLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.coin_red_bg));
            holder.exchangeTv.setText(mContext.getResources().getString(R.string.exchange_imediate_str));
        } else {
            holder.exchangeLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.coin_gray_bg));
            holder.exchangeTv.setText(mContext.getResources().getString(R.string.exchange_empty));
        }

        if (money.length() >2) {
            holder.moneyTv.setTextSize(24);
        } else {
            holder.moneyTv.setTextSize(36);
        }

        holder.moneyTv.setText(money);
        holder.moneyOffTv.setText(bean.getRule());
        holder.coinNumTv.setText(String.format(mContext.getResources().getString(R.string.exchange_coin_num), bean.getQuota()));


        if (TextUtils.isEmpty(bean.getImage())) {
            holder.coinImv.setImageResource(R.drawable.default_image);
        } else {
            mContext.volleyImageLoader.displayNetworkImage(mContext.volleyImageLoader, bean.getImage(), holder.coinImv, R.drawable.default_image, R.drawable.default_image,
                    Utils.dip2px(mContext, 70), Utils.dip2px(mContext, 70), ImageURLManager.SCALE_TO_FIT, null);
        }

        holder.holeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.getStatus() == 1) {
                    mExchangeCoinListener.goExchange(bean.getCouponId(), bean.getCouponType(), bean.getTitle(), bean.getQuota());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCoinList.size();
    }

    public class ViewHolder extends IViewHolder {
        NetworkImageView coinImv;
        TextView nameTv;
        TextView moneyOffTv;
        TextView coinNumTv;
        TextView exchangeTv;
        RelativeLayout exchangeLayout;
        TextView moneyTv;
        LinearLayout holeLayout;
        ImageView iv_end;

        public ViewHolder(View itemView) {
            super(itemView);
            holeLayout = itemView.findViewById(R.id.hole_layout);
            coinImv = itemView.findViewById(R.id.coin_imv);
            nameTv = itemView.findViewById(R.id.coin_name_tv);
            moneyOffTv = itemView.findViewById(R.id.money_off_tv);
            coinNumTv = itemView.findViewById(R.id.coin_num_tv);
            exchangeLayout = itemView.findViewById(R.id.exchange_layout);
            exchangeTv = itemView.findViewById(R.id.exchange_tv);
            moneyTv = itemView.findViewById(R.id.money_tv);
            iv_end = itemView.findViewById(R.id.iv_end);
        }
    }
}
