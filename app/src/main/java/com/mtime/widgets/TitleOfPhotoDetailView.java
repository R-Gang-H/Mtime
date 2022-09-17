package com.mtime.widgets;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.frame.BaseActivity;
import com.mtime.R;

public class TitleOfPhotoDetailView extends BaseTitleView {

    private final TextView title;
    private final ImageView share;

    public TitleOfPhotoDetailView(final BaseActivity context, final View root, final ITitleViewLActListener listener) {

        title = root.findViewById(R.id.title);

        share = root.findViewById(R.id.share);
        share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(ActionType.TYPE_SHARE, null);
                }
            }
        });

    }

    @Override
    public void setAlpha(float alpha) {

    }

    public void hideShare() {
        if (null != share) {
            share.setVisibility(View.GONE);
        }
    }

    /**
     * 设置标题文字
     */
    public void setTitleText(final String label) {
        if (!TextUtils.isEmpty(label)) {
            title.setText(label);
        }

    }

}
