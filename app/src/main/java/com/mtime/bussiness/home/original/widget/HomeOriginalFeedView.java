package com.mtime.bussiness.home.original.widget;

import android.app.Activity;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.views.ForegroundImageView;
import com.mtime.base.widget.OnViewClickListener;
import com.mtime.base.widget.layout.OnVisibilityCallback;
import com.mtime.base.widget.layout.VisibilityStateLayout;
import com.mtime.bussiness.home.bean.HomeFeedItemBean;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedItemBean;
import com.mtime.bussiness.home.recommend.bean.HomeRecommendFeedItemBean;
import com.mtime.bussiness.home.recommend.bean.HomeRecommendFeedLogxBean;
import com.mtime.bussiness.mine.history.ReadHistoryUtil;

/**
 * @author vivian.wei
 * @date 2019/6/11
 * @desc 首页_时光原创feed流_Item组件
 *
 * 目前只有在MovieOriginalListActivity中使用到
 */
public class HomeOriginalFeedView extends VisibilityStateLayout {

    public static final int FEED_ITEM_TYPE_ORIGINAL = 1;    // 时光原创
    public static final int FEED_ITEM_TYPE_RECOMMEND = 2;   // 首页_推荐

    private final Context mContext;
    private HomeFeedItemBean mData;
    private Activity mActivity;
    private int mPosition = 0;

    private int mSmallPicWidth;
    private int mSmallPicHeight;
    private int mLargePicWidth;
    private int mlargePicHeight;
    private OnFeedItemClickListener mOnFeedItemClickListener;

    private View mRootView;
    private View mDividerView;
    private TextView mTitleTv;
    private TextView mAuthorTv;
    private ImageView mCloseIv;
    private TextView mAdTagTv;
    private TextView mStickTagTv;

    private final int[] mImageViews = {
            R.id.item_home_feed_list_three_pic_pic1_iv,
            R.id.item_home_feed_list_three_pic_pic2_iv,
            R.id.item_home_feed_list_three_pic_pic3_iv
    };

    public HomeOriginalFeedView(Context context) {
        super(context);
        mContext = context;
        initValue();
    }

    public HomeOriginalFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initValue();
    }

    private void initValue() {
        int width = MScreenUtils.getScreenWidth();
        //减去左右padding
        width -= mContext.getResources().getDimensionPixelOffset(R.dimen.common_list_left_or_right_margin) * 2;

        mLargePicWidth = width;
        mlargePicHeight = mLargePicWidth * 9 / 16; //按16：9计算

        //减去小图片之间的空隙
        width -= MScreenUtils.dp2px(9);
        mSmallPicWidth = width / 3;
        mSmallPicHeight = mSmallPicWidth * 2 / 3; //按16：9计算
    }

    /* 设置数据
       FEED_ITEM_TYPE_RECOMMEND: 带删除图标X
     */
    public void setData(Activity activity, HomeFeedItemBean data, int position) {
        if (null == data || data.styleType < 1 || data.styleType > 3) {
            return;
        }

        // 添加布局
        addLayout(data);

        mActivity = activity;
        mData = data;
        mPosition = position;
        mRootView = findViewById(R.id.item_root);
        //1 小图样式、2 大图样式、3 三图样式
        switch (mData.styleType) {
            case 1:
                showSinglePicSmall();
                break;
            case 2:
                showSinglePicLarge();
                break;
            case 3:
                showThreePic();
                break;
            default:
                break;
        }

        addOnVisibilityCallback();
    }

    // 添加布局
    private void addLayout(HomeFeedItemBean data) {
        if (mData == null || mData.styleType != data.styleType) {
            removeAllViews();
            switch (data.styleType) {
                case 1:
                    LayoutInflater.from(mContext).inflate(R.layout.item_home_feed_list_single_pic_small, this);
                    break;
                case 2:
                    LayoutInflater.from(mContext).inflate(R.layout.item_home_feed_list_single_pic_large, this);
                    break;
                case 3:
                    LayoutInflater.from(mContext).inflate(R.layout.item_home_feed_list_three_pic, this);
                    break;
                default:
                    break;
            }
            mRootView = findViewById(R.id.item_root);
            mDividerView = findViewById(R.id.home_feed_item_divider_v);
        }
    }

    public void hideDivider() {
        if (mDividerView != null) {
            mDividerView.setVisibility(GONE);
        }
    }

    // 加载布局_一张小图
    private void showSinglePicSmall() {
        mCloseIv = findViewById(R.id.item_home_feed_list_single_pic_small_close_iv);
        mAdTagTv = findViewById(R.id.item_home_feed_list_single_pic_small_ad_tag_tv);
        mStickTagTv = findViewById(R.id.item_home_feed_list_single_pic_small_stick_tag_tv);
        mTitleTv = findViewById(R.id.item_home_feed_list_single_pic_small_title_tv);
        mAuthorTv = findViewById(R.id.item_home_feed_list_single_pic_small_author_tv);

        // 图片
        ForegroundImageView imageView = findViewById(R.id.item_home_feed_list_single_pic_small_pic_iv);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = mSmallPicWidth;
        params.height = mSmallPicHeight;
        loadSmallImage(mData.getImgUrl(0), imageView);

        // 显示通用View
        showCommonView();
    }

    // 加载布局_一张大图
    private void showSinglePicLarge() {
        mCloseIv = findViewById(R.id.item_home_feed_list_single_pic_large_close_iv);
        mAdTagTv = findViewById(R.id.item_home_feed_list_single_pic_large_ad_tag_tv);
        mStickTagTv = findViewById(R.id.item_home_feed_list_single_pic_large_stick_tag_tv);
        mTitleTv = findViewById(R.id.item_home_feed_list_single_pic_large_title_tv);
        mAuthorTv = findViewById(R.id.item_home_feed_list_single_pic_large_author_tv);

        // 图片
        ForegroundImageView imageView = findViewById(R.id.item_home_feed_list_single_pic_large_pic_iv);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.height = mlargePicHeight;
        ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT)
                .override(mLargePicWidth, mlargePicHeight)
                .view(imageView)
                .load(mData.getImgUrl(0))
                .placeholder(R.drawable.default_image)
                .showload();
        // 设置播放icon
        if (mData.isShowPlayIcon(0)) {
            imageView.setForegroundResource(R.drawable.common_icon_play_large);
        } else {
            imageView.setForeground(null);
        }

        // 显示通用View
        showCommonView();
    }

    // 加载布局_三张小图
    private void showThreePic() {
        mCloseIv = findViewById(R.id.item_home_feed_list_three_pic_close_iv);
        mAdTagTv = findViewById(R.id.item_home_feed_list_three_pic_ad_tag_tv);
        mStickTagTv = findViewById(R.id.item_home_feed_list_three_pic_stick_tag_tv);
        mTitleTv = findViewById(R.id.item_home_feed_list_three_pic_title_tv);
        mAuthorTv = findViewById(R.id.item_home_feed_list_three_pic_author_tv);

        // 三张小图
        ForegroundImageView imageView;
        for (int i = 0; i < mImageViews.length; i++) {
            imageView = findViewById(mImageViews[i]);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.width = mSmallPicWidth;
            params.height = mSmallPicHeight;
            //设置图片
            loadSmallImage(mData.getImgUrl(i), imageView);
        }

        // 显示通用View
        showCommonView();
    }

    // 加载小图
    private void loadSmallImage(String url, ForegroundImageView imageView) {
        ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT)
                .override(mSmallPicWidth, mSmallPicHeight)
                .view(imageView)
                .load(url)
                .placeholder(R.drawable.default_image)
                .showload();

        // 设置播放icon
        if (mData.isShowPlayIcon(0)) {
            imageView.setForegroundResource(R.drawable.common_icon_play_large);
        } else {
            imageView.setForeground(null);
        }
    }

    // 显示通用View
    private void showCommonView() {
        // 初始化 隐藏通用view
        mCloseIv.setVisibility(GONE);
        mAdTagTv.setVisibility(GONE);
        mStickTagTv.setVisibility(GONE);

        // 设置title、作者
        mTitleTv.setText(mData.title);
        mAuthorTv.setText(mData.getPublicName());

        String tag = "";
        String brandName = "";
        // 读过置灰
        if (mData instanceof HomeOriginalFeedItemBean) {
            // 时光原创
            HomeOriginalFeedItemBean originalBean = (HomeOriginalFeedItemBean) mData;
            mTitleTv.setTextColor(ContextCompat.getColor(mContext,
                    ReadHistoryUtil.hasReadHistory(originalBean.relatedId) ? R.color.color_999999 : R.color.color_303A47));
            tag = originalBean.tag;
            if (originalBean.contentType == 3 && null != originalBean.adv) {
                brandName = originalBean.adv.brandName;
            }
        } else if (mData instanceof HomeRecommendFeedItemBean) {
            // 推荐
            HomeRecommendFeedItemBean recommendBean = (HomeRecommendFeedItemBean) mData;
            mTitleTv.setTextColor(ContextCompat.getColor(mContext,
                    ReadHistoryUtil.hasReadHistory(recommendBean.feedId) ? R.color.color_999999 : R.color.color_303A47));
            tag = recommendBean.tag;
            if (recommendBean.contentType == HomeFeedItemBean.CONTENT_TYPE_AD && null != recommendBean.adv) {
                brandName = recommendBean.adv.brandName;
            }

            //显示关闭按钮(非广告类型)
            if (recommendBean.contentType != HomeFeedItemBean.CONTENT_TYPE_AD && !recommendBean.isStickType) {
                mCloseIv.setVisibility(VISIBLE);
                mCloseIv.setOnClickListener(new OnViewClickListener() {
                    @Override
                    public void onClicked(View v) {
                        onDeleteDislikeRcmd(v, recommendBean, mPosition);
                    }
                });
            }
        }

        //设置标签
        if (!TextUtils.isEmpty(tag)) {
            if (mData.contentType == HomeFeedItemBean.CONTENT_TYPE_AD) {
                //设置广告标签
                mAdTagTv.setText(tag);
                mAdTagTv.setVisibility(VISIBLE);
                mAuthorTv.setText(brandName);
            } else {
                // 设置置顶标签
                mStickTagTv.setText(tag);
                mStickTagTv.setVisibility(VISIBLE);
            }
        }

        //item点击监听
        mRootView.setOnClickListener(new OnViewClickListener() {
            @Override
            public void onClicked(View v) {
                if (mData.contentType == HomeFeedItemBean.CONTENT_TYPE_ARTICLES ||
                        mData.contentType == HomeFeedItemBean.CONTENT_TYPE_VIDEO ||
                        mData.contentType == HomeFeedItemBean.CONTENT_TYPE_RANK) {
                    // 标题置灰
                    mTitleTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_999999));
                }
                onItemClick(mData, mPosition);
            }
        });
    }

    // 根布局显示|隐藏
    private void addOnVisibilityCallback() {
        if (mRootView instanceof VisibilityStateLayout && mOnFeedItemClickListener != null) {
            OnVisibilityCallback.Tag tag = new OnVisibilityCallback.Tag();
            tag.data = mData;
            tag.type = mData.contentType;
            tag.position = mPosition;
            ((VisibilityStateLayout) mRootView).setOnVisibilityListener(new OnVisibilityCallback(tag) {
                @Override
                protected void onShow(Tag tag) {
                    mOnFeedItemClickListener.onFeedItemVisibleChange(true, tag);
                }

                @Override
                protected void onHidden(Tag tag) {
                    mOnFeedItemClickListener.onFeedItemVisibleChange(false, tag);
                }
            });
        }
    }

    //删除不喜欢的推荐
    private void onDeleteDislikeRcmd(View anchorView, final HomeRecommendFeedItemBean bean, final int position) {
//        DislikePopupWrapper.show(mActivity, anchorView, bean.recommendID, bean.logx, new DislikePopupWrapper.DeleteDislikeListener() {
//            @Override
//            public void onDeletedDislike(HomeRecommendFeedLogxBean reasonBean) {
//                if (null != mOnFeedItemClickListener) {
//                    mOnFeedItemClickListener.onDeleteDislikeRcmd(bean, reasonBean, position);
//                }
//            }
//        });
    }

    // item点击事件
    private void onItemClick(HomeFeedItemBean bean, int position) {
        if (null != mOnFeedItemClickListener) {
            switch (bean.contentType) {
                case HomeFeedItemBean.CONTENT_TYPE_AD:
                    mOnFeedItemClickListener.onFeedItemClick2Ad(bean, position);
                    break;
                case HomeFeedItemBean.CONTENT_TYPE_RANK:
                case HomeFeedItemBean.CONTENT_TYPE_ARTICLES:
                    mOnFeedItemClickListener.onFeedItemClick2Articles(bean, position);
                    break;
                case HomeFeedItemBean.CONTENT_TYPE_VIDEO:
                    mOnFeedItemClickListener.onFeedItemClick2Video(bean, position);
                    break;
                default:
                    break;
            }
        }
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener listener) {
        mOnFeedItemClickListener = listener;
    }

    public interface OnFeedItemClickListener {
        void onFeedItemClick2Articles(HomeFeedItemBean item, int position);

        void onFeedItemClick2Video(HomeFeedItemBean item, int position);

        void onFeedItemClick2Ad(HomeFeedItemBean item, int position);

        void onFeedItemVisibleChange(boolean show, OnVisibilityCallback.Tag tag);

        // 仅供推荐Item调用
        void onDeleteDislikeRcmd(HomeRecommendFeedItemBean item, HomeRecommendFeedLogxBean reasonBean, int position);
    }
}
