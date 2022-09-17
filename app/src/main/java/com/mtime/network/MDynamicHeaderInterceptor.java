package com.mtime.network;

import com.mtime.base.network.DynamicHeaderInterceptor;
import com.mtime.base.utils.Guid;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;

import java.util.HashMap;
import java.util.Map;

public class MDynamicHeaderInterceptor implements DynamicHeaderInterceptor {
    private final String HEADER_CHECK_VALUE = "X-Mtime-Mobile-CheckValue";
    private final String HEADER_MX_CID = "MX-CID";

    @Override
    public Map<String, String> checkHeaders(int type, Map<String, String> headers, String url, Map<String, String> params) {
        return appendDynamicHeader(headers, url, params);
    }

    //统一增加动态消息头
    private Map<String, String> appendDynamicHeader(Map<String, String> headers, String url, Map<String, String> params) {
        if (null == headers) {
            headers = new HashMap<>();
        }
        headers.putAll(NetworkConstant.getCommonHeaders()); //network底层没有把公共头传过来,后期要在network优化,不应该在这里面追加
        headers.put(HEADER_CHECK_VALUE, getCheckValueHeader(url, params));
        headers.put(HEADER_MX_CID, Guid.get());
        return headers;
    }

    /**
     * 获取CheckValueHeader
     */
    private String getCheckValueHeader(String url, Map<String, String> params) {
        final long time = System.currentTimeMillis();

        final StringBuilder paramBuffer = new StringBuilder();
        // TODO  尝试添加header数据。验证的还是需要在这里负值。
        if (null != params && params.size() > 0) {
            LogWriter.i("MTNet", "businessParam:" + params.toString());
            int index = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                //TODO X key需要encode吗?
                paramBuffer.append(entry.getKey());
                paramBuffer.append('=');
                String value = entry.getValue() != null ? entry.getValue() : "";
                // TODO 改成原来的url encode方法，需要验证一下
                paramBuffer.append(Utils.UrlEncodeUnicode(value));
                if (++index >= params.size()) {
                    break;
                }
                paramBuffer.append('&');
            }

        }

        StringBuilder sb1 = new StringBuilder();
        sb1.append(FrameConstant.APP_ID).append(FrameConstant.CLIENT_KEY).append(time).append(url).append(paramBuffer.toString());

        StringBuilder sb = new StringBuilder();
        sb.append(FrameConstant.APP_ID).append(FrameConstant.COMMA).append(time).append(FrameConstant.COMMA);
        sb.append(Utils.getMd5(sb1.toString()));
        sb.append(FrameConstant.COMMA).append(FrameConstant.CHANNEL_ID);

        return sb.toString();
    }
}
