package com.mtime.bussiness.ticket.movie.widget;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mtime.R;

public class ShowWholeNameView {
	private final View root;
	private final TextView viewLabel1;
	private final TextView viewLabel2;

	public ShowWholeNameView(final View view) {
		this.root = view;
		this.viewLabel1 = this.root.findViewById(R.id.label1);
		this.viewLabel2 = this.root.findViewById(R.id.label2);

		this.root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setVisibility(View.INVISIBLE);
			}
		});
	}

	public void setLabels(final String label1, final String label2) {
		this.viewLabel1.setText(label1);
		this.viewLabel2.setText(label2);

	}

	public void setVisibility(int visibility) {
		if (root.getVisibility() == visibility) {
			return;
		}

		this.root.setVisibility(visibility);
	}

	public int getVisibility() {
		return this.root.getVisibility();
	}

	public void setBackground(int color) {
		this.root.setBackgroundColor(color);
	}
}
