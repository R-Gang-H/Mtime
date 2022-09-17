package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.frame.BaseStatisticHelper;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-19
 *
 * 影片详情-列表底部区域的广告位
 * 影片资料页广告图尺寸比例 750x210
 */
public class MovieDetailsAdBinder extends MovieDetailsBaseBinder<CommonAdListBean> {
    private int height;

    public MovieDetailsAdBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        super(callback, helper);
    }

    @NonNull
    @Override
    protected BaseViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_movie_details_ad, parent, false);
        ViewGroup.LayoutParams params = view.findViewById(R.id.common_layout_ad_item_fl).getLayoutParams();
        if (null == params)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = height = MScreenUtils.getScreenWidth() * 210 / 750;
        view.setLayoutParams(params);
        return new BaseViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull BaseViewHolder holder, @NonNull CommonAdListBean item) {
        if (item.hasDatas()) {
            CommonAdListBean.AdBean adBean = item.getAdList().get(0);
            if (null != adBean && adBean.hasAdData()) {
                ImageView imageView =
                        holder.setGone(R.id.advtag, TextUtils.isEmpty(adBean.getAdTitle()))
                        .setText(R.id.advtag, adBean.getAdTitle())
                        .getView(R.id.imageview);
                ImageHelper.with(imageView.getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                        .override(MScreenUtils.getScreenWidth(), height)
                        .load(adBean.getImagePropertiesList().get(0).getImgUrl())
                        .placeholder(R.drawable.default_image)
                        .view(imageView)
                        .showload();
                holder.getView(R.id.common_layout_ad_item_fl).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 埋点上报
                        String refer = mBaseStatisticHelper.assemble1(
                                "adBanner", null,
                                "down", null,
                                null, null, null).submit();

                        if (!TextUtils.isEmpty(adBean.getAdLink())) {
                            // 广告applink跳转
                            ApplinkManager.jump(imageView.getContext(), adBean.getAdLink(), refer);
                        } else {
                            MToastUtils.showShortToast("跳转链接为空~！");
                        }
                    }
                });
            }
        }
    }
}
