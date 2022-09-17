package com.mtime.base.network;

/**
 * Created by LiJiaZhi on 17/4/1.
 *
 * token 过期
 */

public class TokenExpireFilter implements Filter<Integer> {


    @Override
    public boolean onFilter(Integer code, String message) {
        //            EventManager.sendTokenExpire();
        return code == NetworkConst.ERROR_CODE_TOKEN_BROKEN;
    }
}