package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-21
 *
 * 影片详情接口返回的数据实体
 */
public class MovieDetailsBean implements IObfuscateKeepAll {
    public int playState; // 1 选座购票  2 观看全片 3.预售 4暂无排片
    public MovieDetailsBasic basic; // 影片基础信息
    public List<MovieDetailsOnlinePlay> playlist; //第三方播放列表
    public MovieDetailsLive live; //直播
    public MovieDetailsBoxOffice boxOffice; //票房
    public MovieDetailsRelatedGoods related; //关联的商品

    public boolean hasData() {
        return null != basic && basic.movieId > 0;
    }

    public boolean hasBoxOffice() {
        return null != boxOffice && boxOffice.hasData();
    }

    public boolean hasLive() {
        return null != live && live.liveId > 0;
    }

    public MovieDetailsLive getLive() {
        if (hasLive()) {
            live.movieId = basic.movieId;
            return live;
        }
        return null;
    }

    public boolean hasGoods() {
        return null != related && !CollectionUtils.isEmpty(related.goodsList);
    }

    public MovieDetailsRelatedGoods getGoods() {
        if (hasGoods()) {
            related.movieId = basic.movieId;
            return related;
        }
        return null;
    }
}
