package com.mtime.bussiness.main.maindialog.api;

import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;

import com.kotlin.android.user.UserManager;
import com.mtime.base.dialog.BaseMDialog;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.main.maindialog.bean.UnusedTicketItemBean;
import com.mtime.bussiness.main.maindialog.MainDialogApi;
import com.mtime.bussiness.main.maindialog.bean.DialogDataBean;
import com.mtime.bussiness.main.maindialog.bean.UnusedTicketListBean;
import com.mtime.bussiness.main.maindialog.dialog.UnusedTicketDialog;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 【在线选座（未使用的影票）】
 * 24小时内，只在第一次开启app时首页弹出一次（超过24小时清除记录，重新触发弹出）
 * 只弹出一次，用户在该弹窗内完成点击算一次（点击影票信息 或【我知道了】按钮均算完成点击）
 * 点击进入影票详情页返回首页弹窗消失
 */
public class UnusedTicketApi extends BaseApi implements MainDialogApi.Api<UnusedTicketListBean> {
    
    private DialogDataBean<UnusedTicketListBean> item;
    private BaseMDialog mDialog;
    
    @Override
    protected String host() {
        return null;
    }
    
    
    /*
     //旧的数据拼装
     if(null != eTicketsAndTickets) {
                        List<UnusedTicketItemBean> ticketList = eTicketsAndTickets.getTicketList();
                        if (null != ticketList && !ticketList.isEmpty() && null != info) {
                            double shortDis = 0;
                            String cinemaId = "";
                            String titleString = "";
                            for (final UnusedTicketItemBean b : ticketList) {
                                final double dis = MtimeUtils.gps2m(info.getLatitude(), info.getLongitude(), b.getBaiduLatitude(), b.getBaiduLongitude());
                                b.setDistance(dis);
                                // 过滤得到最小的距离和cinemaid
                                if (dis < shortDis) {
                                    shortDis = dis;
                                    cinemaId = b.getCinemaId();
                                    titleString = b.getCinemaName();
                                }
                            }
                            // 对票进行排序
                            Collections.sort(ticketList, new Comparator<UnusedTicketItemBean>() {
                                @Override
                                public int compare(final UnusedTicketItemBean ticket1,
                                final UnusedTicketItemBean ticket2) {
                                    if (ticket1.getShowtime() > ticket2.getShowtime()) {
                                        return 1;
                                    }
                                    return 0;
                                }
        
                            });
                            // 是否有未使用的票（因为没有用到城市数据，去掉了城市判断）
                            if (shortDis < 1000) {
                                UnusedTicketListBean bean = new UnusedTicketListBean();
                                bean.ticketList = new ArrayList<>();
                                bean.titleString = titleString;
                                for (final UnusedTicketItemBean b : ticketList) {
                                    if (b.getCinemaId().trim().equals(cinemaId.trim())) {
                                        b.setShowOutDate(true);
                                        bean.ticketList.add(b);
                                    }
                                }
                                if(!bean.ticketList.isEmpty()) {
                                    item = DialogDataBean.get(DialogDataBean.TYPE_OF_UNUSED_TICKET,
                                            false, false, false, bean);
                                }
                            }
                        }
                    }
                    if(null != listener) {
                        listener.onFinish(item);
                    }
     */
    
    @Override
    public void onRequest(LocationInfo info, ApiRequestListener listener) {
        final long remindTickets = App.getInstance().getPrefsManager().getLong("RemindTickets");
        if (UserManager.Companion.getInstance().isLogin() && (System.currentTimeMillis() - remindTickets) > (1000 * 60 * 60 * 24)) {
            get(this, ConstantUrl.GET_ALL_ETICKET_AND_TICKET, null, new NetworkManager.NetworkListener<UnusedTicketListBean>() {
                @Override
                public void onSuccess(UnusedTicketListBean listBean, String s) {
                    if(null != listBean && listBean.hasDatas()) {
                        // 对票进行排序
                        Collections.sort(listBean.ticketList, new Comparator<UnusedTicketItemBean>() {
                            @Override
                            public int compare(final UnusedTicketItemBean ticket1,
                                               final UnusedTicketItemBean ticket2) {
                                if (ticket1.getShowtime() > ticket2.getShowtime()) {
                                    return 1;
                                }
                                return 0;
                            }
        
                        });
    
                        item = DialogDataBean.get(DialogDataBean.TYPE_OF_UNUSED_TICKET,
                                false, false, false, listBean);
                    }
                    if(null != listener) {
                        listener.onFinish(item);
                    }
                }
            
                @Override
                public void onFailure(NetworkException<UnusedTicketListBean> networkException, String s) {
                    if(null != listener) {
                        listener.onFinish(item);
                    }
                }
            });
        } else {
            if(null != listener) {
                listener.onFinish(item);
            }
        }
    }
    
    @Override
    public boolean onShow(AppCompatActivity activity, ApiShowListener listener) {
        if(null == activity || activity.isFinishing())
            return false;
    
        App.getInstance().getPrefsManager().putLong("RemindTickets", System.currentTimeMillis());
        
        mDialog = UnusedTicketDialog.newInstance(new ArrayList<>(item.data.ticketList))
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(null != listener) {
                            listener.onDismiss(item);
                        }
                    }
                })
                .setMargin(15)
                .setOutCancel(false)
                .show(activity.getSupportFragmentManager());
        return true;
                
    }
    
    @Override
    public DialogDataBean<UnusedTicketListBean> getData() {
        return item;
    }
    
    @Override
    public void onDestroy() {
        cancel();
        item = null;
        if(null != mDialog && !mDialog.isDetached()) {
            mDialog.dismissAllowingStateLoss();
        }
        mDialog = null;
    }
}
