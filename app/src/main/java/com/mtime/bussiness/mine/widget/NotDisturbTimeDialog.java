package com.mtime.bussiness.mine.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.common.utils.PrefsManager;
import com.mtime.constant.FrameConstant;
import com.mtime.widgets.spinnerwheel.AbstractWheel;
import com.mtime.widgets.spinnerwheel.OnWheelScrollListener;
import com.mtime.widgets.spinnerwheel.adapters.AbstractWheelTextAdapter;

import java.util.ArrayList;

/**
 * 免打扰时间
 */
public class NotDisturbTimeDialog extends Dialog {

    private final Activity context;
    private IOkClickListener okClickListener;

    public NotDisturbTimeDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_not_disturb_time);

        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = FrameConstant.SCREEN_WIDTH;

        getWindow().setAttributes(p);

        findViewById(R.id.dialog_not_disturb_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final AbstractWheel startWheel = findViewById(R.id.start_wheel);
        startWheel.setVisibleItems(7);
        startWheel.setViewAdapter(new WheelAdapter(context, 0, 23));

        final AbstractWheel endWheel = findViewById(R.id.end_wheel);
        endWheel.setVisibleItems(7);
        endWheel.setViewAdapter(new WheelAdapter(context, 1, 24));

        // 设置选定值
        PrefsManager prefsManager = App.getInstance().getPrefsManager();
        // 注意给设置默认值，此时默认值为0，意味着没设置的时候startTime为0点，而endTime为不限，出现bug
        int startTime = prefsManager.getInt(App.getInstance().KEY_NOTDISTURB_TIME_START, -1);
        int endTime = prefsManager.getInt(App.getInstance().KEY_NOTDISTURB_TIME_END);
        if (startTime > -1) {
            startWheel.setCurrentItem(startTime + 1);
        }
        if (endTime > -1) {
            endWheel.setCurrentItem(endTime);
        }

        startWheel.addScrollingListener(new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            public void onScrollingFinished(AbstractWheel wheel) {
                if (startWheel.getCurrentItem() == 0) {
                    endWheel.setCurrentItem(0, true);
                } else {
                    if (endWheel.getCurrentItem() == 0) {
                        endWheel.setCurrentItem(1, true);
                    }
                }
            }
        });
        endWheel.addScrollingListener(new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            public void onScrollingFinished(AbstractWheel wheel) {
                if (endWheel.getCurrentItem() == 0) {
                    startWheel.setCurrentItem(0, true);
                } else {
                    if (startWheel.getCurrentItem() == 0) {
                        startWheel.setCurrentItem(1, true);
                    }
                }
            }
        });

        findViewById(R.id.dialog_not_disturb_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (okClickListener != null) {
                    okClickListener.onEvent(startWheel.getCurrentItem(), endWheel.getCurrentItem());
                }
            }
        });
    }

    public void showActionSheet() {
        if (this != null) {
            Window window = getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();

            DisplayMetrics metric = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(metric);

            wl.x = 0;
            wl.y = metric.heightPixels;

            this.onWindowAttributesChanged(wl);
            this.setCanceledOnTouchOutside(true);
            this.show();
        }
    }

    public void setOkClickListener(IOkClickListener okClickListener) {
        this.okClickListener = okClickListener;
    }

    private class WheelAdapter extends AbstractWheelTextAdapter {
        private final ArrayList<String> wheelStrArrayList = new ArrayList<String>();

        protected WheelAdapter(Context context, int startIndex, int endIndex) {
            super(context, R.layout.dialog_not_disturb_time_wheel_item, NO_RESOURCE);

            setItemTextResource(R.id.country_name);
            wheelStrArrayList.add("不限");
            for (int i = startIndex; i <= endIndex; i++) {
                String string = i + ":00";
                wheelStrArrayList.add(string);
            }
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return wheelStrArrayList.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return wheelStrArrayList.get(index);
        }
    }

    public interface IOkClickListener {
        void onEvent(final int start, final int end);
    }
}
