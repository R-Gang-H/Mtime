package com.mtime.mtmovie.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * Created by LiJiaZhi on 2017/9/5.
 *
 * Okhttp3请求网络开启Gzip压缩
 */

public class GzipRequestInterceptor implements Interceptor {
    @Override public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
            return chain.proceed(originalRequest);
        }

        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")
                .method(originalRequest.method(), forceContentLength(gzip(originalRequest.body())))
                .build();
        return chain.proceed(compressedRequest);
    }

    /** https://github.com/square/okhttp/issues/350 */
    private RequestBody forceContentLength(final RequestBody requestBody) throws IOException {
        final Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return requestBody.contentType();
            }

            @Override
            public long contentLength() {
                return buffer.size();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(buffer.snapshot());
            }
        };
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return body.contentType();
            }

            @Override public long contentLength() {
                return -1; // We don't know the compressed length in advance!
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}

//public class GzipRequestInterceptor implements Interceptor {
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request originalRequest = chain.request();
//        if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
//            return chain.proceed(originalRequest);
//        }
//
//        Request compressedRequest = originalRequest.newBuilder().header("Content-Encoding", "gzip")
//            .method(originalRequest.method(), gzip(originalRequest.body())).build();
//        return chain.proceed(compressedRequest);
//    }
//
//    private RequestBody gzip(final RequestBody body) {
//        return new RequestBody() {
//            @Override
//            public MediaType contentType() {
//                return body.contentType();
//            }
//
//            @Override
//            public long contentLength() {
//                return -1; // 无法提前知道压缩后的数据大小
//            }
//
//            @Override
//            public void writeTo(BufferedSink sink) throws IOException {
//                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
//                body.writeTo(gzipSink);
//                gzipSink.close();
//            }
//        };
//    }
//}