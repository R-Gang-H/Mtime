package com.mtime.bussiness.common.utils;

import android.content.Context;

import com.mtime.base.utils.MSharePreferenceUtils;
import com.mtime.frame.App;

/**
 * @author ZhouSuQiang
 * @date 2018/9/27
 */
public class MSharePreferenceHelper extends MSharePreferenceUtils {
    private static class Holder {
        private static final MSharePreferenceHelper sHelper = new MSharePreferenceHelper(App.get());
    }

    private MSharePreferenceHelper(Context context) {
        this(context, null);
    }

    private MSharePreferenceHelper(Context context, String sharePreFileName) {
        super(context, sharePreFileName);
    }
    
    public static MSharePreferenceHelper get() {
        return Holder.sHelper;
    }
}
