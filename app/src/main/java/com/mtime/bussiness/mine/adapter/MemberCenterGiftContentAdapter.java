package com.mtime.bussiness.mine.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.bussiness.mine.bean.MemberGiftBean;

import java.util.List;

/**
 * Created by vivian.wei on 2017/7/20.
 * 会员中心首页弹窗_生日/等级礼包内容Adapter
 */

public class MemberCenterGiftContentAdapter extends RecyclerView.Adapter<MemberCenterGiftContentAdapter.ViewHolder> {

    private final Activity context;
    private final List<MemberGiftBean> list;

    private static final String QTY = "x%d";

    public MemberCenterGiftContentAdapter(Activity context, List<MemberGiftBean> list) {
        this.context = context;
        this.list = list;
    }

    public MemberGiftBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_member_center_gift_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MemberGiftBean bean = getItem(position);
        // 1 购物券 2 购票券
        switch(bean.getType()) {
            case 1:
                holder.ivIcon.setBackgroundResource(R.drawable.dialog_member_center_gift_content_goods);
                break;
            case 2:
                holder.ivIcon.setBackgroundResource(R.drawable.dialog_member_center_gift_content_ticket);
                break;
            default:
                break;
        }
        holder.tvName.setText(bean.getName());
        holder.tvCount.setText(String.format(QTY, bean.getQty()));
    }

    public class ViewHolder extends IViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCount = itemView.findViewById(R.id.tv_count);
        }
    }
}
