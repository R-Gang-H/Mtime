package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-04
 */
public class MovieDetailsTab implements IObfuscateKeepAll {
    public int titleResid;
    public String subTitle;
    public int startPos; //列表开始位置
    public int endPos; //列表结束位置

    public String secRegion; //埋点的二级标识
}
