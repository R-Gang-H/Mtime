package com.mtime.bussiness.ticket.movie.widget;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.adapter.ChangeSceneAdapter;
import com.mtime.bussiness.ticket.movie.activity.SeatSelectActivity;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeUIBean;
import com.mtime.constant.FrameConstant;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.statistic.large.ticket.StatisticTicket;

import java.util.List;

/**
 * 选座页 - 点击“更换场次”时显示
 */
public class SeatSelectChangeDialog extends Dialog {

    private final SeatSelectActivity context;
    private final List<ShowTimeUIBean> filteredShowtimes;
    private ListView listView;
    private final String date;

    public SeatSelectChangeDialog(SeatSelectActivity context, int theme, List<ShowTimeUIBean> filteredShowtimes, final String date) {
        super(context, theme);
        this.context = context;
        this.filteredShowtimes = filteredShowtimes;
        this.date = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seat_select_changedialog);

        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = (int) (FrameConstant.SCREEN_HEIGHT * 0.6);
        p.width = FrameConstant.SCREEN_WIDTH;
        getWindow().setAttributes(p);
        ImageButton close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StatisticPageBean bean5 = context.assemble(StatisticTicket.TICKET_CHANGE_TIME, null, "cancel", null, null, null, null);
                StatisticManager.getInstance().submit(bean5);
                dismiss();
            }
        });
        listView = findViewById(R.id.dialog_listview);
        listView.setAdapter(new ChangeSceneAdapter(context, filteredShowtimes, date));

    }

    public ListView getListView() {
        return listView;
    }

    public void showActionSheet() {
        if (this != null && null != context && context.canShowDlg) {
            WindowManager m = context.getWindowManager();
            DisplayMetrics metric = new DisplayMetrics();
            m.getDefaultDisplay().getMetrics(metric);

            Window window = getWindow();
            // 设置显示动画
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = metric.heightPixels;

            // 设置显示位置
            this.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            this.setCanceledOnTouchOutside(true);
            this.show();
        }
    }
}
