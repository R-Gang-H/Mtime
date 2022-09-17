package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;

public class MessageBtn extends FrameLayout {

	public MessageBtn(Context con, AttributeSet attrs) {
		super(con, attrs);
		LayoutInflater.from(getContext()).inflate(R.layout.messagebtn_widget,
				this);
	}

	public void setMessageImage(int resId) {
		ImageView btn = findViewById(R.id.btn_msg);
		btn.setImageResource(resId);
	}

	public void setNum(int notifyCount, int broadCount) {
		TextView num = findViewById(R.id.btn_msg_img);
		ImageView noNum = findViewById(R.id.btn_msg_img_onlynotify);
		if (notifyCount > 0) {
			num.setText(String.valueOf(notifyCount));
			num.setVisibility(View.VISIBLE);
			noNum.setVisibility(View.GONE);
		} else if (broadCount > 0) {
			num.setText("");
			num.setVisibility(View.GONE);
			noNum.setVisibility(View.VISIBLE);
		} else {
			num.setVisibility(View.GONE);
			noNum.setVisibility(View.GONE);
		}
	}

}
