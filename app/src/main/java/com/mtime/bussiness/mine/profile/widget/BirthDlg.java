package com.mtime.bussiness.mine.profile.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.bussiness.mine.profile.activity.ProfileActivity;

/**
 * Created by vivian.wei on 2017/5/5.
 * 选择生日弹框
 */

public class BirthDlg extends Dialog {

    private DatePicker datePicker;
    private final ProfileActivity.OnBirthDlgClickListener listener;
    private int year = 1990;
    private int month = 0;
    private int day = 1;

    public BirthDlg(Context context, int initYear, int initMonthOfYear, int initDayOfMonth,
                    ProfileActivity.OnBirthDlgClickListener okListener) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        year = initYear;
        month = initMonthOfYear;
        day = initDayOfMonth;
        listener = okListener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_birth);
        datePicker = findViewById(R.id.date_picker);
        TextView tvOk = findViewById(R.id.tv_ok);
        TextView tvCancel = findViewById(R.id.tv_cancel);

        datePicker.init(year, month, day, null);

        if(null != listener) {
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.okClick(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    dismiss();
                }
            });
        }

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
