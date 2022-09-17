package com.mtime.base.network;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mtime.base.bean.MParserBean;
import com.mtime.base.utils.MJsonUtils;

import java.lang.reflect.Type;

/**
 * @author ZhouSuQiang
 * @date 2018/10/31
 */
public class DefaultNetworkParser implements NetworkParser {
    @Override
    public <T> MParserBean<T> onParse(String result, Type type) {
        //兼容旧的数据结构([{},{}])
        if (!TextUtils.isEmpty(result) && result.startsWith("[")) {
            result = compatOldResponse(result);
        }
        JsonObject jsonObject = MJsonUtils.getJsonParserInstance().parse(result).getAsJsonObject();
        //兼容旧的数据结构
        if (!jsonObject.has("code") || !jsonObject.has("data")) {
            result = compatOldResponse(result);
            jsonObject = MJsonUtils.getJsonParserInstance().parse(result).getAsJsonObject();
        }
        
        MParserBean<T> parserBean = new MParserBean<>();
        parserBean.code = jsonObject.has("code") ? jsonObject.get("code").getAsInt() : -1;
        parserBean.msg = jsonObject.has("msg") ? jsonObject.get("msg").getAsString() : "";
        parserBean.showMsg = jsonObject.has("showMsg") ? jsonObject.get("showMsg").getAsString() : "";
        if(jsonObject.has("data")) {
            JsonElement data = jsonObject.get("data");
            if(type == String.class) {
                if (data.isJsonPrimitive()) {
                    parserBean.data = (T) data.getAsString();
                } else {
                    parserBean.data = (T) data.toString();
                }
            } else {
                parserBean.data = null == type ? null : MJsonUtils.parseString(data.toString(), type);
            }
        }
        parserBean.isSucceed = parserBean.code == NetworkConfig.CODE_REQUEST_SUCCESS;
        return parserBean;
    }
    
    //兼容旧的数据结构
    private String compatOldResponse(String data) {
        return "{\"code\": " + NetworkConfig.CODE_REQUEST_SUCCESS + ",\"showMsg\": \"\",\"msg\": \"成功\",\"data\":" + data + "}";
    }
}
