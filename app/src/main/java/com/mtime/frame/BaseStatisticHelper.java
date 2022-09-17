package com.mtime.frame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticDataBuild;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.statistic.large.StatisticWrapBean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-10
 * <p>
 * 页面的基础上报 统一管理
 */
public class BaseStatisticHelper {

    public static final String INTENT_IN_OR_OUT_REFER = "intent_in_or_out_refer";

    /**
     * 上个页面的refer
     */
    private String mLastPageRefer;
    /**
     * 当前页面的标识(名称)
     */
    private String mPageLabel = "";
    /**
     * 页面打开的时间戳
     */
    private long mStartTime;
    /**
     * 埋点_页面open/close等基础参数值（可能有多个）
     */
    private Map<String, String> mBaseParam = new HashMap<>();

    /**
     * 是否开启统计4.0的open close   默认开启
     * 此功能废弃，待重构时删掉logx埋点代码
     */
    private boolean mIsSubmit = false;


    public BaseStatisticHelper() {

    }

    public BaseStatisticHelper(boolean isSubmit) {
        mIsSubmit = isSubmit;
    }


    /**
     * 统一处理refer，如果有自定义refer已自定义为准，例如push，如果没有并且是Activity的话，则上个页面的mLastPageRefer为准
     *
     * @param context
     * @param refer
     * @param launcher
     */
    public static void dealRefer(Context context, String refer, Intent launcher) {
        if (!TextUtils.isEmpty(refer)) {
            launcher.putExtra(INTENT_IN_OR_OUT_REFER, refer);
        }
        if (!(context instanceof Activity)) {
            launcher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    public static String getSavedPageRefer(Intent intent) {
        if (intent == null) {
            return "";
        }
        return intent.getStringExtra(INTENT_IN_OR_OUT_REFER);
    }

    /**
     * 组装统计类（用于无统计，方便生成refer）
     */
    public StatisticWrapBean assemble() {
        return assemble1(null, null, null, null, null, null, null);
    }

    /**
     * 组装统计类
     *
     * @param firstRegion
     * @param firstRegionMark
     * @param secRegion
     * @param secRegionMark
     * @param thrRegion
     * @param thrRegionMark
     * @param businessParam
     * @return
     */
    public StatisticPageBean assemble(String firstRegion, String firstRegionMark, String secRegion,
                                      String secRegionMark, String thrRegion, String thrRegionMark, Map<String, String> businessParam) {
        return StatisticDataBuild.assemble(mLastPageRefer, mPageLabel,
                firstRegion, firstRegionMark, secRegion, secRegionMark, thrRegion, thrRegionMark, businessParam);
    }

    public StatisticWrapBean assemble1(String firstRegion, String firstRegionMark, String secRegion,
                                       String secRegionMark, String thrRegion, String thrRegionMark, Map<String, String> businessParam) {
        return new StatisticWrapBean(assemble(firstRegion, firstRegionMark, secRegion, secRegionMark, thrRegion, thrRegionMark, businessParam));
    }

    public StatisticWrapBean assemble(Map<String, String> param, String... rms) {
        if (rms == null || rms.length == 0) {
            return assemble1(null, null, null, null, null, null, param);
        }
        switch (rms.length) {
            case 1:
                return assemble1(rms[0], null, null, null, null, null, param);
            case 2:
                return assemble1(rms[0], rms[1], null, null, null, null, param);
            case 3:
                return assemble1(rms[0], rms[1], rms[2], null, null, null, param);
            case 4:
                return assemble1(rms[0], rms[1], rms[2], rms[3], null, null, param);
            case 5:
                return assemble1(rms[0], rms[1], rms[2], rms[3], rms[4], null, param);
            case 6:
            default:
                return assemble1(rms[0], rms[1], rms[2], rms[3], rms[4], rms[5], param);
        }
    }

    /**
     * @param rms 按 区域-标号 排列
     */
    public StatisticWrapBean assemble(String... rms) {
        return assemble(null, rms);
    }

    /**
     * @param param   参数
     * @param regions 单纯的 区域数据
     */
    public StatisticWrapBean assembleOnlyRegion(Map<String, String> param, String... regions) {
        if (regions == null || regions.length == 0) {
            return assemble1(null, null, null, null, null, null, param);
        }
        String first = regions[0];
        String second = null;
        String third = null;
        if (regions.length >= 2) {
            second = regions[1];
        }
        if (regions.length >= 3) {
            third = regions[2];
        }
        return assemble1(first, null, second, null, third, null, param);
    }

    public StatisticWrapBean assembleRegion(String... regions) {
        return assembleOnlyRegion(null, regions);
    }


    /********** ******************/

    public void setBaseParam(Map<String, String> baseParam) {
        mBaseParam = baseParam;
    }

    public Map<String, String> getBaseParam() {
        return mBaseParam;
    }

    public void putBaseParam(String key, String value) {
        mBaseParam.put(key, value);
    }

    public void setPageLabel(String pageLabel) {
        mPageLabel = pageLabel;
    }

    public String getPageLabel() {
        return mPageLabel;
    }

    public void setLastPageRefer(String refer) {
        mLastPageRefer = refer;
    }

    public void setLastPageRefer(Intent intent) {
        if (null == intent)
            return;
        mLastPageRefer = intent.getStringExtra(INTENT_IN_OR_OUT_REFER);
    }

    public String getLastPageRefer() {
        return mLastPageRefer;
    }

    public void setSubmit(boolean submit) {
        mIsSubmit = submit;
    }

    public boolean isSubmit() {
        return mIsSubmit;
    }

    /**
     * 上报打开
     */
    public void onResume(Context context) {
        if (mIsSubmit) {
            mStartTime = System.currentTimeMillis();
            if (null != mBaseParam) {
                // 清楚空数据
                Iterator<Map.Entry<String, String>> iterator = mBaseParam.entrySet().iterator();
                while (iterator.hasNext()) {
                    if (TextUtils.isEmpty(iterator.next().getValue())) {
                        iterator.remove();
                    }
                }
            }
            // open 上报
            assemble1(StatisticConstant.OPEN, null, null,
                    null, null, null, mBaseParam).submit();
        }
    }

    /**
     * 上报close and timing
     *
     * @param context
     */
    public void onPause(Context context) {
        if (mIsSubmit) {
            // close 上报
            assemble1(StatisticConstant.CLOSE, null, null,
                    null, null, null, mBaseParam).submit();
            // timing 上报
            Map<String, String> params = new HashMap<>();
            params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
            if (null != mBaseParam && !mBaseParam.isEmpty()) {
                params.putAll(mBaseParam);
            }
            assemble1(StatisticConstant.TIMING, null,
                    null, null, null, null, params).submit();
        }
    }

}
