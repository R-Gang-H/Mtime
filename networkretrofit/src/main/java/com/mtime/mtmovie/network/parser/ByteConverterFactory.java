package com.mtime.mtmovie.network.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 自定义将数据解析成最基本的byte返回
 * retrofit对于解析器是由添加的顺序分别试用的，解析成功就直接返回，失败则调用下一个解析器。
 * 如果服务器一开始基本返回格式不固定，而后来固定了的话，
 * 可以按如下代码的方式将GsonConverterFactory添加在StringConverterFactory前面。
 * 这样如果是固定格式的就可以直接解析返回了。
 * .addConverterFactory(GsonConverterFactory.create())
 * .addConverterFactory(ByteConverterFactory.create())
 * Created by yinguanping on 17/2/7.
 */

public class ByteConverterFactory extends Converter.Factory {

    public static ByteConverterFactory create() {
        return new ByteConverterFactory();
    }

    private ByteConverterFactory() {

    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new ByteResponseBodyConverter();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new ByteRequestBodyConverter();
    }
}