package com.mtime.base.imageload;

import android.text.TextUtils;

import java.util.Locale;

import com.kotlin.android.app.data.ext.VariateExt;

/**
 * Created by ZhouSuQiang on 2018/1/26.
 */

public class ImageProxyUrl {
    private static String PROXY_URL_HOST = VariateExt.INSTANCE.getImgProxyUrl();
    private static final String PROXY_URL =
            "%1$s%2$s&width=%3$d&height=%4$d&clipType=%5$d";
    private static final ImageLoadOptions.ImageSize mProxySize = new ImageLoadOptions.ImageSize(0, 0);
    
    //生成代理URL
    public static String createProxyUrl(String url, ImageLoadOptions.ImageSize size, SizeType sizeType, ClipType clipType) {
        mProxySize.setSize(0, 0);
        if (!hasProxy(url) && url.startsWith("http") && null != sizeType && null != clipType) {
            switch (sizeType) {
                case ORIGINAL_SIZE: //原始大小
                    mProxySize.setSize(0, 0);
                    break;
                case CUSTOM_SIZE: //自定义大小
                    mProxySize.setSize(size.width, size.height);
                    break;
                case RATIO_1_1: //图片比例1:1
                    if (size.width <= 120 && size.width > 0) {
                        mProxySize.setSize(120, 120);
                    } else if (size.width <= 240 && size.width > 0) {
                        mProxySize.setSize(240, 240);
                    } else if (size.width > 360) {
                        mProxySize.setSize(750, 750);
                    } else {
                        mProxySize.setSize(360, 360);
                    }
                    break;
                case RATIO_2_3: //图片比例2：3
                    if (size.width <= 160 && size.width > 0) {
                        mProxySize.setSize(160, 240);
                    } else if (size.width >= 360) {
                        mProxySize.setSize(360, 540);
                    } else {
                        mProxySize.setSize(240, 360);
                    }
                    break;
                case RATIO_3_2: //图片比例3：2
                    if (size.width <= 360 && size.width > 0) {
                        mProxySize.setSize(360, 240);
                    } else if (size.width >= 660) {
                        mProxySize.setSize(660, 440);
                    } else {
                        mProxySize.setSize(540, 360);
                    }
                    break;
                case RATIO_4_1: //图片比例4：1
                    mProxySize.setSize(240, 60);
                    break;
                case RATIO_4_3: //图片比例4：3
                    mProxySize.setSize(240, 180);
                    break;
                case RATIO_16_9: //图片比例16：9
                    if (size.width >= 640) {
                        mProxySize.setSize(640, 360);
                    } else {
                        mProxySize.setSize(320, 180);
                    }
                    break;
                case RATIO_20_9: //图片比例20：9
                    mProxySize.setSize(640, 288);
                    break;
                case RATIO_32_9: //图片比例32：9
                    mProxySize.setSize(640, 180);
                    break;
                default:
                    //默认自定义大小
                    mProxySize.setSize(size.width, size.height);
                    break;
            }
    
            //屏幕宽度和高度大于屏幕的0.6倍，就启动循序渐进加载效果
//            boolean progressive = size.width >= ImageLoadStrategyManager.sScreenWidth*0.6 || size.height >= ImageLoadStrategyManager.sScreenWidth*0.6;
            
            if(size.width > mProxySize.width || size.height > mProxySize.height) {
                size.width = mProxySize.width;
                size.height = mProxySize.height;
            }
            
            return String.format(Locale.ENGLISH, PROXY_URL, PROXY_URL_HOST, url,
                    mProxySize.width, mProxySize.height,
                    clipType.getValue());
        }
        
        return url;
    }
    
    /**
     * 是否含有代理路径的url
     */
    private static boolean hasProxy(String url) {
        return TextUtils.isEmpty(url) || url.startsWith(PROXY_URL_HOST);
    }
    
    //枚举定义：图片大小类型
    public enum SizeType {
        ORIGINAL_SIZE, //原始大小
        CUSTOM_SIZE, //自定义大小
        RATIO_1_1, //图片比例1:1
        RATIO_2_3, //图片比例2:3
        RATIO_3_2, //图片比例3:2
        RATIO_4_1, //图片比例4:1
        RATIO_4_3, //图片比例4:3
        RATIO_16_9, //图片比例16:9
        RATIO_20_9, //图片比例20:9
        RATIO_32_9 //图片比例32:9
    }
    
    //枚举定义：图片裁剪类型
    public enum ClipType {
        // 等比缩放
        SCALE_TO_FIT(0),
        // 定宽等比缩放，如果高大于需求则截取高为需求
        FIX_WIDTH_TRIM_HEIGHT(1),
        // 定宽等比缩放，不限制高
        FIX_WIDTH(2),
        // 固定宽或者固定高
        // 如果宽大于需求，高大于需求，宽>高，宽等于需求宽，高等比
        // 如果宽大于需求，高大于需求，高>宽，高等于需求高，宽等比
        // 如果宽大于需求，高小于需求，宽等于需求宽，高等比
        // 如果宽小于需求，高大于需求，高等于需求高，宽等比
        // 如果都小则不做处理
        FIX_WIDTH_OR_HEIGHT(3),
        // 固定宽和高，如果小于则放大，宽截取中间的需求宽
        FIX_WIDTH_AND_HEIGHT(4);
        
        private final int mValue;
        
        ClipType(int value) {
            this.mValue = value;
        }
        
        public int getValue() {
            return mValue;
        }
    }
}
