package com.mtime.bussiness.common.widget;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.dialog.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-27
 */
public class TwoButtonAlert extends BaseDialogFragment {

    private static final String KEY_TITLE = "TITLE";

    private static final String KEY_POSITIVE = "POSITIVE";

    private static final String KEY_NEGATIVE = "NEGATIVE";

    private static final String KEY_SHOULD_CANCEL_ON_BACK = "SHOULD_CANCEL_ON_BACK";

    private static final String KEY_SHOULD_CANCEL_ON_OUTSIDE = "SHOULD_CANCEL_ON_OUTSIDE";

    private static final String KEY_DIM_AMOUNT = "DIM_AMOUNT";

    private AlertCallback mCallback;

    @Override
    public int getTheme() {
        return R.style.TwoButtonAlert;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_two_button_alert;
    }

    @Override
    protected float getDimAmount() {
        return mDimAmount;
    }

    @BindView(R.id.dlg_content_tv)
    TextView mAlert;
    @BindView(R.id.positive_button_tv)
    TextView mPositiveTv;
    @BindView(R.id.negative_button_tv)
    TextView mNegativeTv;

    private boolean mShouldCancelOnBack = true;
    private boolean mShouldCancelOnOutside = true;

    private float mDimAmount = 0.12f;

    private String mTitle;
    private String mPositive;
    private String mNegative;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        mTitle = args.getString(KEY_TITLE);
        mPositive = args.getString(KEY_POSITIVE);
        mNegative = args.getString(KEY_NEGATIVE);

        mShouldCancelOnBack = args.getBoolean(KEY_SHOULD_CANCEL_ON_BACK, true);
        mShouldCancelOnOutside = args.getBoolean(KEY_SHOULD_CANCEL_ON_OUTSIDE, true);
        mDimAmount = args.getFloat(KEY_DIM_AMOUNT, mDimAmount);
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);

        mAlert.setText(mTitle);
        mPositiveTv.setText(mPositive);
        mNegativeTv.setText(mNegative);

    }

    @Override
    public boolean shouldCancelOnBackPressed() {
        return mShouldCancelOnBack;
    }

    @Override
    public boolean shouldCancelOnTouchOutside() {
        return mShouldCancelOnOutside;
    }

    @OnClick(R.id.positive_button_tv)
    void onPositiveClick() {
        if (mCallback != null) {
            mCallback.onPositiveClick();
        }
        dismiss();
    }

    @OnClick(R.id.negative_button_tv)
    void onNegativeClick() {
        if (mCallback != null) {
            mCallback.onNegativeClick();
        }
        dismiss();
    }

    private void setAlertCallback(AlertCallback c) {
        mCallback = c;
    }

    public interface AlertCallback {
        void onPositiveClick();

        void onNegativeClick();
    }

    public static abstract class SimpleCallback implements AlertCallback {
        @Override
        public void onPositiveClick() {
        }

        @Override
        public void onNegativeClick() {
        }
    }

    public static class Builder {
        private String title;
        private String negativeTxt;
        private String positiveTxt;
        private AlertCallback mCallback;

        private boolean mShouldCancel = true;
        private boolean mShouldCancelOnBack = true;
        private boolean mShouldCancelOnOutside = true;

        private float mDimAmount = 0.12f;

        public Builder dimAmount(float dimAmount) {
            if (dimAmount < 0) {
                dimAmount = 0;
            }
            if (dimAmount > 1) {
                dimAmount = 1;
            }
            mDimAmount = dimAmount;
            return this;
        }

        public Builder shouldCancelOnBack(boolean shouldCancelOnBack) {
            mShouldCancelOnBack = shouldCancelOnBack;
            return this;
        }

        public Builder shouldCancelOnOutside(boolean shouldCancelOnOutside) {
            mShouldCancelOnOutside = shouldCancelOnOutside;
            return this;
        }

        public Builder shouldCancel(boolean shouldCancel) {
            mShouldCancel = shouldCancel;
            return this;
        }

        public Builder alert(String alert) {
            title = alert;
            return this;
        }

        public Builder negative(String negative) {
            negativeTxt = negative;
            return this;
        }

        public Builder positive(String positive) {
            positiveTxt = positive;
            return this;
        }

        public Builder callback(AlertCallback c) {
            mCallback = c;
            return this;
        }

        public TwoButtonAlert build() {
            TwoButtonAlert alert = new TwoButtonAlert();
            Bundle args = new Bundle();
            args.putString(KEY_TITLE, title);
            args.putString(KEY_POSITIVE, positiveTxt);
            args.putString(KEY_NEGATIVE, negativeTxt);
            args.putBoolean(KEY_SHOULD_CANCEL_ON_BACK, mShouldCancel && mShouldCancelOnBack);
            args.putBoolean(KEY_SHOULD_CANCEL_ON_OUTSIDE, mShouldCancel && mShouldCancelOnOutside);
            args.putFloat(KEY_DIM_AMOUNT, mDimAmount);
            alert.setArguments(args);
            alert.setAlertCallback(mCallback);
            return alert;
        }
    }
}
