//package com.mtime.bussiness.ticket.movie.details.bean;
//
//import com.helen.obfuscator.IObfuscateKeepAll;
//import com.mtime.base.utils.CollectionUtils;
//import com.mtime.bussiness.community.topic.bean.HotTopicBean;
//
//import java.util.List;
//
///**
// * @author ZhouSuQiang
// * @email zhousuqiang@126.com
// * @date 2019/4/1
// */
//public class MovieDetailsHotTopicsBean implements IObfuscateKeepAll {
//    public int countCount; // 100, //热议话题条数  （2期新增） 此字段即 totalCount 话题总条数，此处命名为countCount是为了和话题频道页面保持一致。
//    public String countCountShow; //"100"
//
//    public List<HotTopicBean> list;
//
//    public boolean hasDatas() {
//        return !CollectionUtils.isEmpty(list);
//    }
//
//
//    // 以下为本地字段
//    public long movieId;
//    public String movieName; // "碟中谍4"
//}
