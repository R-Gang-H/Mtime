package com.mtime.mtmovie.network.interfaces;

import java.util.Map;

/**
 * Created by yinguanping on 17/1/13.
 */
public interface HeadersInterceptor {
    Map checkHeaders(Map headers);
}
