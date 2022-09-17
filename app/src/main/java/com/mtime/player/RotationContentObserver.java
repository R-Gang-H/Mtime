package com.mtime.player;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.provider.Settings;

public class RotationContentObserver extends ContentObserver {

    private final ContentResolver mResolver;
    private OnSettingChangeListener mOnSettingChangeListener;

    /**
     * Creates a content observer.
     *
     * @param context
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public RotationContentObserver(Context context) {
        super(null);
        mResolver = context.getContentResolver();
    }

    public void setOnSettingChangeListener(OnSettingChangeListener onSettingChangeListener) {
        this.mOnSettingChangeListener = onSettingChangeListener;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (mOnSettingChangeListener != null) {
            mOnSettingChangeListener.onSettingChange(isAutoRotation(mResolver));
        }
    }

    public static boolean isAutoRotation(ContentResolver resolver) {
        return Settings.System.getInt(resolver,
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    public void startObserver() {
        mResolver.registerContentObserver(Settings.System
                        .getUriFor(Settings.System.ACCELEROMETER_ROTATION), false,
                this);
    }

    public void stopObserver() {
        mResolver.unregisterContentObserver(this);
    }

    public interface OnSettingChangeListener {
        void onSettingChange(boolean autoRotation);
    }

}
