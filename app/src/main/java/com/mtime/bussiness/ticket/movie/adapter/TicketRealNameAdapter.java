package com.mtime.bussiness.ticket.movie.adapter;

import android.text.TextUtils;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.TicketRealNameBean;
import com.mtime.constant.Constants;
import com.mtime.util.AESutil;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2020/7/28
 * @desc 订单详情页_实名预约购票信息Adapter
 */
public class TicketRealNameAdapter extends BaseQuickAdapter<TicketRealNameBean, BaseViewHolder> {

    public TicketRealNameAdapter(@Nullable List<TicketRealNameBean> data) {
        super(R.layout.item_ticket_real_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TicketRealNameBean bean) {
        String realName = "";
        if(!TextUtils.isEmpty(bean.getRealName())) {
            try {
                realName = AESutil.decrypt(Constants.AES_KEY, bean.getRealName());
            } catch (Exception e) {
            }
        }
        helper.setText(R.id.item_ticket_real_name_name_tv, realName);
        // 证件名称：需要解密
        String typeName = "";
        if(!TextUtils.isEmpty(bean.getIdTypeName())) {
            try {
                typeName = AESutil.decrypt(Constants.AES_KEY, bean.getIdTypeName());
            } catch (Exception e) {
            }
        }
        helper.setText(R.id.item_ticket_real_name_id_type_tv, typeName);
        // 证件号码：需要解密，前3位和后4位正常显示，中间显示******
        String num = "";
        if(!TextUtils.isEmpty(bean.getIdNum())) {
            try {
                num = AESutil.decrypt(Constants.AES_KEY, bean.getIdNum());
            } catch (Exception e) {
            }
        }
        helper.setText(R.id.item_ticket_real_name_id_num_tv, num);

        if(helper.getAdapterPosition() == getItemCount() - 1) {
            // 需要占位 invisible
            helper.setVisible(R.id.item_ticket_real_name_bottom_line_view, false);
        }
    }

    // 获取类似101****1111之类的字符串
    private String getShowNum(String num) {
        if (!TextUtils.isEmpty(num) && num.length() > 7) {
            StringBuffer buffer = new StringBuffer();
            int start = 3;
            int end = num.length() - 4;
            buffer.append(num.substring(0, start));
            for(int i = start; i < end; i++) {
                buffer.append("*");
            }
            buffer.append(num.substring(end));
            return buffer.toString();
        }
        return num;
    }

}
