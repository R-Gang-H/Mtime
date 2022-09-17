package com.mtime.bussiness.ticket.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.mtime.R;

import java.util.List;
import java.util.Map;

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/18
 * 描述: 购票tab-预告片推荐Adapter
 */
public class TabTicketVideoAdapter extends BaseQuickAdapter<Map<String, String>, BaseViewHolder> {

    private final int PADDING_FIRST_END = DimensionExtKt.getDp(10);
    private final int PADDING_OTHER = DimensionExtKt.getDp(5);
    private final int IMG_WIDTH = DimensionExtKt.getDp(156);
    private final int IMG_HEIGHT = DimensionExtKt.getDp(88);
    private final float CORNER_RADIUS = DimensionExtKt.getDpF(4);

    public TabTicketVideoAdapter(@NonNull List<Map<String, String>> data) {
        super(R.layout.item_tab_ticket_video, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Map<String, String> bean) {
        int position = holder.getLayoutPosition();
        holder.itemView.getRootView().setPadding(
                position == 0 ? PADDING_FIRST_END : PADDING_OTHER,
                0,
                position == getItemCount() - 1 ? PADDING_FIRST_END : PADDING_OTHER,
                0
        );
        holder.setText(R.id.videoTitleTv, bean.get("title"));
        holder.setText(R.id.videoTitleSmallTv, bean.get("titleSmall"));
        AppCompatImageView imgView = holder.getView(R.id.videoIv);
        CoilCompat.INSTANCE.loadGifImage(
                imgView,
                bean.get("imageBigApp"),
                IMG_WIDTH,
                IMG_HEIGHT,
                true,
                true,
                false,
                R.drawable.default_image,
                R.drawable.default_image,
                CORNER_RADIUS
        );
    }

}
