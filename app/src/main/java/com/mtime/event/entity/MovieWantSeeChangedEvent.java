package com.mtime.event.entity;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-08-02
 *
 * 影片"想看/取消想看"变化的事件
 */
public class MovieWantSeeChangedEvent implements IObfuscateKeepAll {
    public String movieId;
    public boolean isWantSee;
}
