/**
 * Copyright (C) 2013 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mtime.widgets;

import android.content.Context;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 * 好像没有啥用处，后期可以考虑去掉
 */
public class NetworkImageView extends AppCompatImageView {
    public static boolean FILTER_SET_ON = false;
//    private boolean isFilter = false;
//    private int filterResId = -1;
//    private TextView filterTextView;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        loadImageIfNecessary();
    }

   /* public void setFilterTextView(TextView filterTextView) {
        this.filterTextView = filterTextView;
    }

    private void showFilterTextView() {
        if (null != filterTextView) {
            if (isFilter && FILTER_SET_ON) {
                filterTextView.setVisibility(VISIBLE);
            } else {
                filterTextView.setVisibility(GONE);
            }
        }
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }


    public void setFilterResId(int resId) {
        filterResId = resId;
    }

    private void loadImageIfNecessary() {
        if (isFilter && FILTER_SET_ON) {
            if (filterResId != -1) {
                setImageResource(filterResId);
            }
        }
        showFilterTextView();
    }*/
}
