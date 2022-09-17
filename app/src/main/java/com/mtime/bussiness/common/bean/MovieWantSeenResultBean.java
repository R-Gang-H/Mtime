package com.mtime.bussiness.common.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-03
 *
 * 设置影片想看、看过的返回结果
 */
public class MovieWantSeenResultBean implements IObfuscateKeepAll {
    public int status; //1为成功
    public String statusMsg;
    public String wantToSeeNumberShow;
}
