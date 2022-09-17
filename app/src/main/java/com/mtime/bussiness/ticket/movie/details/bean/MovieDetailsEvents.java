package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

/**
 * 影片详情-相关事件
 */
public class MovieDetailsEvents implements IObfuscateKeepAll {
    public long movieId;


    public int eventCount; //了解的事件个数
    public String title; // "title"
    //标题
    public List<String> list;
    //事件列表

}
