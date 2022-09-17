package com.mtime.bussiness.mine.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;

import com.mtime.R;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView.IMovieAndCinemaSwitchViewListener;
import com.mtime.widgets.BaseTitleView;

public class TitleOfMyVoucherListView extends BaseTitleView {

	private final View root;
	private final View viewAlpha;
	private final MovieAndCinemaSwitchView viewSwitch;

	@SuppressLint("CutPasteId")
	public TitleOfMyVoucherListView(final Activity context,
			final View root, final IMovieAndCinemaSwitchViewListener listener,
			final ITitleViewLActListener lListener, final int couponRemindType) {

		this.root = root;
		View back = root.findViewById(R.id.back);
		this.viewAlpha = root.findViewById(R.id.background);
		View switchView = root.findViewById(R.id.move_cinema_switched_view);
		viewSwitch = new MovieAndCinemaSwitchView(context, switchView, listener, couponRemindType);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.finish();

				if (null != lListener) {
					lListener.onEvent(ActionType.TYPE_BACK, null);
				}

			}
		});
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public void setAlpha(float alpha) {
		if (null != viewAlpha && android.os.Build.VERSION.SDK_INT >= 11) {
			float a = alpha < MIN_ALPHA ? 0 : alpha;
			viewAlpha.setAlpha(a > 1 ? 1 : a);
		}
	}

	public void setVisibile(int visibility) {
		this.root.setVisibility(visibility);
	}

	public View getRootView() {
		return this.root;
	}

	public void setCinemaViewOn(final Context context, boolean on) {
		viewSwitch.setCinemaViewOn(context, on);
	}
}
