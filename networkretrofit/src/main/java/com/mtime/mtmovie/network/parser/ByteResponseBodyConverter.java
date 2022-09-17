package com.mtime.mtmovie.network.parser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by yinguanping on 17/2/7.
 */

public class ByteResponseBodyConverter implements Converter<ResponseBody, byte[]> {
    @Override
    public byte[] convert(ResponseBody value) throws IOException {
        try {
            return value.bytes();
        } finally {
            value.close();
        }
    }
}

