package com.mtime.mtmovie.network;


import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by yinguanping on 17/1/13.
 */
public interface RetrofitHttpService {

    @GET()
    Call<ResponseBody> get(@HeaderMap Map<String, String> headers, @Url String url, @QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST()
    Call<ResponseBody> post(@HeaderMap Map<String, String> headers, @Url String url, @FieldMap Map<String, String> params);

    @Streaming
    @GET()
    Call<ResponseBody> download(@Url String url);

    @Multipart
    @POST()
    Call<ResponseBody> upload(@HeaderMap Map<String, String> headers, @Url String url, @PartMap Map<String, RequestBody> body);

    @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
    @POST()
    Call<ResponseBody> postJson(@HeaderMap Map<String, String> headers, @Url String url, @Body RequestBody route);//传入的参数为RequestBody

}
