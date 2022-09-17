package com.kotlin.android.popup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({ARROWDIRECTION.TOP, ARROWDIRECTION.BOTTOM, ARROWDIRECTION.LEFT, ARROWDIRECTION.RIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface ARROWDIRECTION {
    int TOP = 12;
    int BOTTOM = 13;
    int LEFT = 14;
    int RIGHT = 15;
}