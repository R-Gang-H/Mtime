package com.mtime.base.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by LiJiaZhi on 17/1/20.
 * <p>
 * Json通用类
 */
public class MJsonUtils {

    private static final String TAG = MJsonUtils.class.getSimpleName();


    public static Gson gson = null;

    public static Gson getGsonInstance() {
        if (gson == null) {
            synchronized (Gson.class) {
                if(gson == null){
                    gson = new GsonBuilder().create();
                }
            }
        }
        return gson;
    }

    private static JsonParser jsonParser = null;

    public static JsonParser getJsonParserInstance() {
        if (jsonParser == null) {
            synchronized (JsonParser.class) {
                if(jsonParser == null){
                    jsonParser = new JsonParser();
                }
            }

        }
        return jsonParser;
    }

    //时间格式化
    public static class LPDateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsLong() * 1000);
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getTime() / 1000);
        }
    }


    public static <T> T parseString(String result, Type classOfT) {
        if (result != null && classOfT != null) {
            try {
                return getGsonInstance().fromJson(result, classOfT);
            } catch (JsonSyntaxException var3) {
                Log.e(TAG, "catch exception when format json str:" + result);
                throw var3;
            }
        } else {
            return null;
        }
    }

    public static String toString(Object obj) {
        return obj == null ? "" : getGsonInstance().toJson(obj);
    }

    public static <T> T parseJsonObject(JsonElement result, Class<T> classOfT) {
        return getGsonInstance().fromJson(result, classOfT);
    }

    public static <T> T parseString(String result, Class<T> clazz) {
        return getGsonInstance().fromJson(result, clazz);
    }

    // add ljz
    public static JsonObject toJsonObject(Object obj) {
        return getJsonParserInstance().parse(toString(obj)).getAsJsonObject();
    }
}

