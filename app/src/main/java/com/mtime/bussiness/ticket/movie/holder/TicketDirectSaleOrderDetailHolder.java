package com.mtime.bussiness.ticket.movie.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.bean.SeatItem;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;
import com.mtime.bussiness.ticket.widget.SeatHelper;
import com.mtime.util.Base64ToBitmap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by JiaJunHui on 2018/5/22.
 */
public class TicketDirectSaleOrderDetailHolder extends ContentHolder<TicketDetailBean> {

    public static final String KEY_SERVICE_NUMBER = "service_number";

    public static final int EVENT_GO_MAP_NAVIGATION = 10;
    public static final int EVENT_SAVE_TO_ALBUM = 11;
    public static final int EVENT_INTENT_TO_CALL_PHONE = 12;

    @BindView(R.id.act_ticket_direct_sale_order_detail_state_success_rl)
    View mSuccessLayout;
    @BindView(R.id.act_ticket_direct_sale_order_detail_state_error_rl)
    View mErrorLayout;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_error_title_tv)
    TextView mErrorStateTitle;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_error_tip_tv)
    TextView mErrorDesc;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_one_code_ll)
    View mTicketCodeOneLayout;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_more_code_ll)
    View mTicketCodeMoreLayout;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_ticket_code_tv)
    TextView mTicketCodeTv;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_code_first_tv)
    TextView mTicketCodeMoreFirstTv;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_code_second_tv)
    TextView mTicketCodeMoreSecondTv;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_user_mobile_number)
    TextView mUserMobileNumber;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_success_qr_code_iv)
    ImageView mQrCodeIv;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_error_hot_line_telephone)
    TextView mTopHotLineTelephone;
    @BindView(R.id.layout_ticket_direct_sale_order_platform_service_info_rl)
    View mServiceInfoRl;
    @BindView(R.id.layout_ticket_direct_sale_order_platform_service_label_tv)
    TextView mPlatformServiceLabel;
    @BindView(R.id.layout_ticket_direct_sale_order_platform_service_telephone_tv)
    TextView mPlatformServiceTelephone;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_third_indicator_iv)
    ImageView mDirectSaleThirdIndicatorIv;
    @BindView(R.id.layout_ticket_direct_sale_order_top_state_third_name_tv)
    TextView mDirectSaleThirdNameTv;
    @BindView(R.id.layout_ticket_direct_sale_order_info_order_number_tv)
    TextView mOrderNumberTv;
    @BindView(R.id.layout_ticket_direct_sale_order_info_order_state_tip_tv)
    TextView mOrderStateTipTv;
    @BindView(R.id.layout_ticket_direct_sale_order_info_movie_name_tv)
    TextView mMovieNameTv;
    @BindView(R.id.layout_ticket_direct_sale_order_info_play_time_tv)
    TextView mMoviePlayTimeTv;
    @BindView(R.id.layout_ticket_direct_seat_container_row_first_ll)
    LinearLayout mFirstSeatLinear;
    @BindView(R.id.layout_ticket_direct_seat_container_row_second_ll)
    LinearLayout mSecondSeatLinear;
    @BindView(R.id.layout_ticket_direct_seat_container_row_third_ll)
    LinearLayout mThirdSeatLinear;
    @BindView(R.id.layout_ticket_direct_sale_order_info_price_info_tv)
    TextView mOrderPriceInfoTv;
    @BindView(R.id.layout_ticket_direct_sale_cinema_info_cinema_name_tv)
    TextView mCinemaNameTv;
    @BindView(R.id.layout_ticket_direct_sale_cinema_info_cinema_address_tv)
    TextView mCinemaAddressTv;
    @BindView(R.id.layout_ticket_direct_sale_cinema_info_map_navigation_tv)
    ImageView mCinemaInfoMapNavigationIv;
    @BindView(R.id.layout_ticket_direct_sale_notice_introduction_tv)
    TextView mNoticeInfo;
    @BindView(R.id.act_ticket_direct_sale_order_detail_save_to_album_tv)
    TextView mSaveToAlbum;

    private int mOrderStatus;

    private Unbinder unbinder;

    private SeatHelper mSeatHelper;
    private int mRefundStatus;

    public TicketDirectSaleOrderDetailHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_ticket_direct_sale_order_detail);
        unbinder = ButterKnife.bind(this, mRootView);

        mSeatHelper = new SeatHelper(mContext, new SeatHelper.SeatHelperParams()
                .setFirstLinear(mFirstSeatLinear)
                .setSecondLinear(mSecondSeatLinear)
                .setThirdLinear(mThirdSeatLinear)
                .setItemLayoutId(R.layout.ticket_item_seat)
                .setHallNameLayoutId(R.layout.layout_ticket_direct_seat_hall_name)
                .setItemTextViewId(R.id.ticket_seat_item_tv)
                .setHallNameTextViewId(R.id.layout_ticket_direct_seat_hall_name_tv));

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();

    }

    @Override
    public void refreshView() {
        super.refreshView();
        if(mData==null)
            return;
        updateOrderStatus();
        updateTopState();
        updateTicketMovieInfo();
        updateCinemaInfo();
    }

    /**
     * 主订单状态：
     * 0  新建(此时订单对用户不可见)
     * 10 创建成功(调用FinishOrder时所有子订单创建成功，此后主订单再不允许添加子订单)
     * 20 创建失败(调用了FinishOrder时部分或全部子订单创建失败，此后主订单再不允许添加子订单)
     * 30 成功(已支付，且所有子订单都成功)
     * 40 失败(已支付，但部分或全部子订单失败)
     * 100已取消(用户在支付前主动取消)
     */
    private void updateOrderStatus() {
        //订单状态
        mOrderStatus = mData.getOrderStatus();
        //退款状态
        mRefundStatus = mData.getRefundStatus();
        String orderStatusText = "";
        switch (mOrderStatus){
            case TicketConst.OrderStatus.ORDER_NEW:
                //暂无此状态
                break;
            case TicketConst.OrderStatus.ORDER_CANCEL:
                //暂无此状态
                break;
            case TicketConst.OrderStatus.ORDER_CREATE_SUCCESS:
                //暂无此状态
                break;
            case TicketConst.OrderStatus.ORDER_CREATE_FAILURE:
                //暂无此状态
                break;

            case TicketConst.OrderStatus.ORDER_DEAL_SUCCESS:
                //30
                if(mRefundStatus ==TicketConst.RefundStatus.NONE){
                    //成功
                    orderStatusText = getString(R.string.ticket_order_status_success);
                }else {
                    orderStatusText = getStatusText(mRefundStatus);
                }
                break;
            case TicketConst.OrderStatus.ORDER_DEAL_FAILURE:
                //40
                if(mRefundStatus ==TicketConst.RefundStatus.NONE){
                    //失败
                    orderStatusText = getString(R.string.ticket_order_status_fail);
                }else{
                    orderStatusText = getStatusText(mRefundStatus);
                }
                break;
        }
        //订单状态
        mOrderStateTipTv.setText(orderStatusText);
        //订单号
        mOrderNumberTv.setText(String.format(getString(R.string.ticket_order_number_text),mData.getDsOrderNo()));
    }

    private String getStatusText(int refundStatus){
        if(refundStatus==TicketConst.RefundStatus.APPLY){
            return getString(R.string.ticket_order_status_refunding);
        }else if(refundStatus==TicketConst.RefundStatus.REFUNDED){
            return getString(R.string.ticket_order_status_refunded);
        }else{
            return getString(R.string.ticket_order_status_success);
        }
    }

    private void updateTopState() {
        //订单成功且无退款
        if(mOrderStatus==TicketConst.OrderStatus.ORDER_DEAL_SUCCESS){
            //顶部成功状态
            mErrorLayout.setVisibility(View.GONE);
            mSuccessLayout.setVisibility(View.VISIBLE);

            //取票码
            List<TicketDetailBean.NeoElectronicCodeBean> neoList = mData.getNeoElectronicCode();
            int codeSize = neoList==null?0:neoList.size();
            if(codeSize>0){
                if(codeSize==1){
                    mTicketCodeOneLayout.setVisibility(View.VISIBLE);
                    mTicketCodeMoreLayout.setVisibility(View.GONE);
                    TicketDetailBean.NeoElectronicCodeBean codeBean = neoList.get(0);
                    mTicketCodeTv.setText(codeBean.getValue());
                }else if(codeSize==2){
                    mTicketCodeOneLayout.setVisibility(View.GONE);
                    mTicketCodeMoreLayout.setVisibility(View.VISIBLE);
                    //第一条
                    TicketDetailBean.NeoElectronicCodeBean firstBean = neoList.get(0);
                    ColorText first = new ColorText(mTicketCodeMoreFirstTv);
                    first.addItem(new ColorText.TextItem(0, firstBean.getName() + "： ", "#777777"));
                    first.addItem(new ColorText.TextItem(1, firstBean.getValue(), "#F97D3F"));
                    //第二条
                    TicketDetailBean.NeoElectronicCodeBean secondBean = neoList.get(1);
                    ColorText second = new ColorText(mTicketCodeMoreSecondTv);
                    second.addItem(new ColorText.TextItem(0, secondBean.getName() + "： ", "#777777"));
                    second.addItem(new ColorText.TextItem(1, secondBean.getValue(), "#F97D3F"));
                }
            }
            //手机号
            mUserMobileNumber.setText(String.format(getString(R.string.ticket_order_user_mobile_text), mData.getMobile()));

            //二维码
            String qrCode = mData.getQrcode();
            if(TextUtils.isEmpty(qrCode)||codeSize>=2){
                mQrCodeIv.setVisibility(View.GONE);
            }else{
                Bitmap bitmap = Base64ToBitmap.getQrCodeByBase64String(qrCode);
                if(bitmap!=null)
                    mQrCodeIv.setImageBitmap(bitmap);
            }
        }else{
            //顶部失败状态
            mSuccessLayout.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
            //hot line
            mTopHotLineTelephone.setText(mData.getDsCustomerService());
            if(mRefundStatus==TicketConst.RefundStatus.NONE){
                mErrorStateTitle.setText(R.string.ticket_order_status_fail);
                mErrorDesc.setText(R.string.ticket_order_refund_tip1);
            }else{
                mErrorStateTitle.setText(R.string.ticket_order_status_fail_refund_tip);
                mErrorDesc.setText(R.string.ticket_order_refund_tip2);
            }
        }

        /*int r = MScreenUtils.dp2px(5);
        mDirectSaleThirdIndicatorIv.setRadius(r, r);

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.default_mtime)
                .error(R.drawable.default_mtime)
                .transform(new RoundedCornersTransformation(r, 0));

        Glide.with(mContext)
                .load(mData.getDsPlatformLogo())
                .apply(options)
                .into(mDirectSaleThirdIndicatorIv);*/
        
        ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(MScreenUtils.dp2px(30), MScreenUtils.dp2px(30))
                .roundedCorners(5, 0)
                .placeholder(R.drawable.default_mtime)
                .load(mData.getDsPlatformLogo())
                .view(mDirectSaleThirdIndicatorIv)
                .showload();

        mDirectSaleThirdNameTv.setText(mData.getDsPlatformName());
    }

    private void updateTicketMovieInfo() {
        mMovieNameTv.setText(mData.getMovieTitle());
        //电影放映时间
        ColorText movieTimeText = new ColorText(mMoviePlayTimeTv);
        movieTimeText.addItem(new ColorText.TextItem(0, getString(R.string.ticket_info_text_header_time),"#999999"));
        movieTimeText.addItem(new ColorText.TextItem(1,formatDate(mData.getOriginShowTime()),"#F97D3F"));

        //座位 和 厅信息
        List<SeatItem> items = SeatItem.parseSeat(mData.getSeatName());
        mSeatHelper.setSeatInfo(items, mData.getHallName());

        //总价
        ColorText orderPriceText = new ColorText(mOrderPriceInfoTv);
        orderPriceText.addItem(new ColorText.TextItem(0, getString(R.string.ticket_info_text_header_total_price),"#999999"));
        orderPriceText.addItem(new ColorText.TextItem(1,getString(R.string.ticket_info_text_header_RMB) + mData.getDoubleAmount(),"#F97D3F"));

        //判断是否含有卖品  有则显示小卖icon
        if(mData.getDsWithGoods()==1){
            mOrderPriceInfoTv.setCompoundDrawablePadding(MScreenUtils.dp2px(5));
            Drawable rightDrawable = getResource().getDrawable(R.drawable.ticket_direct_sale_price_icon);
            rightDrawable.setBounds(0,0,rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
            mOrderPriceInfoTv.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
        }

        //客服信息
        if(!TextUtils.isEmpty(mData.getDsCustomerService())){
            mPlatformServiceLabel.setText(mData.getDsCustomerServiceLabel());
            mPlatformServiceTelephone.setText(mData.getDsCustomerService());
        }else{
            mServiceInfoRl.setVisibility(View.GONE);
        }
    }

    private void updateCinemaInfo() {
        //影院名称
        mCinemaNameTv.setText(mData.getCname());
        //影院地址
        mCinemaAddressTv.setText(mData.getcAddress());
        //影院自营购票说明
        mNoticeInfo.setText(mData.getDsNotice());
    }

    @OnClick({
            R.id.layout_ticket_direct_sale_cinema_info_map_navigation_tv,
            R.id.act_ticket_direct_sale_order_detail_save_to_album_tv,
            R.id.layout_ticket_direct_sale_order_platform_service_info_rl,
            R.id.layout_ticket_direct_sale_order_top_state_error_hot_line_telephone
    })
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.layout_ticket_direct_sale_cinema_info_map_navigation_tv:
                onHolderEvent(EVENT_GO_MAP_NAVIGATION, null);
                break;
            case R.id.act_ticket_direct_sale_order_detail_save_to_album_tv:
                onHolderEvent(EVENT_SAVE_TO_ALBUM, null);
                break;
            case R.id.layout_ticket_direct_sale_order_platform_service_info_rl:
            case R.id.layout_ticket_direct_sale_order_top_state_error_hot_line_telephone:
                Bundle bundle = new Bundle();
                bundle.putString(KEY_SERVICE_NUMBER, mData.getDsCustomerService());
                onHolderEvent(EVENT_INTENT_TO_CALL_PHONE, bundle);
                break;
        }
    }

    private String formatDate(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 （EEEE）  HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(time * 1000L));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }



    /**
     * Created by JiaJunHui on 2018/5/22.
     */
    public static class ColorText {

        private final String FONT_LABEL = "<font color=%s>%s</font>";
        private final TextView mTextView;
        private final ItemComparator mComparator;
        private final List<TextItem> mItems;

        public ColorText(TextView view){
            this.mTextView = view;
            mComparator = new ItemComparator();
            mItems = new ArrayList<>();
        }

        public void addItem(TextItem newItem){
            mItems.add(newItem);
            Collections.sort(mItems, mComparator);
            StringBuilder sb = new StringBuilder();
            for(TextItem item:mItems){
                sb.append(String.format(FONT_LABEL, item.color, item.text));
            }
            mTextView.setText(Html.fromHtml(sb.toString()));
        }

        public static class TextItem{
            public int index;
            public String text;
            public String color;

            public TextItem(int index, String text, String color) {
                this.index = index;
                this.text = text;
                this.color = color;
            }
        }

        private static class ItemComparator implements Comparator<TextItem> {
            @Override
            public int compare(TextItem o1, TextItem o2) {
                int x = o1.index;
                int y = o2.index;
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        }

    }

    /**
     * Created by JiaJunHui on 2018/5/23.
     */
    public interface TicketConst {

        /**
         * 主订单状态：
         * 0  新建(此时订单对用户不可见)
         * 10 创建成功(调用FinishOrder时所有子订单创建成功，此后主订单再不允许添加子订单)
         * 20 创建失败(调用了FinishOrder时部分或全部子订单创建失败，此后主订单再不允许添加子订单)
         * 30 成功(已支付，且所有子订单都成功)
         * 40 失败(已支付，但部分或全部子订单失败)
         * 100已取消(用户在支付前主动取消)
         */
        interface OrderStatus{
            int ORDER_NEW = 0;
            int ORDER_CREATE_SUCCESS = 10;
            int ORDER_CREATE_FAILURE = 20;
            int ORDER_DEAL_SUCCESS = 30;
            int ORDER_DEAL_FAILURE = 40;
            int ORDER_CANCEL = 100;
        }

        //退款状态，0-未退款，1-申请退款，2-已退款
        interface RefundStatus{
            int NONE = 0;
            int APPLY = 1;
            int REFUNDED = 2;
        }

    }
}
