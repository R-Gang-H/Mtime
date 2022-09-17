package com.mtime.bussiness.mine.history.dao;

import android.text.TextUtils;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;

import org.litepal.LitePal;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import org.litepal.crud.callback.FindCallback;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

/**
 * Created by vivian.wei on 2018/8/21.
 */

public class HistoryDao extends LitePalSupport implements IObfuscateKeepAll {

    private static final int MAX_COUNT = 1000;

    private int contentType;    //内容类型：1 图文、2 视频、4 榜单（与HomeFeedItemBean里contentType保持一致）
    private long relatedId;     // 文章或视频Id
    private long readTime;      // 阅读时间
    private String title;       // 标题
    private String publicName;  // 发布人
    private String img;         // 封面图
    private boolean showVideoIcon;  // 是否显示播放按钮
    private int videoId;            //视频id
    private int videoSourceType;    //视频类型

    // 以下字段界面操作使用，不用存数据库
    @Column(ignore = true)
    private boolean select;         // 是否选中

    @Column(ignore = true)
    private int datePosition;       // 在时间分组里的位置，从1开始

    @Column(ignore = true)
    private String dateGroup;       // 时间分组：1今日 2一周内 3更早

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(long relatedId) {
        this.relatedId = relatedId;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isShowVideoIcon() {
        return showVideoIcon;
    }

    public void setShowVideoIcon(boolean showVideoIcon) {
        this.showVideoIcon = showVideoIcon;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getVideoSourceType() {
        return videoSourceType;
    }

    public void setVideoSourceType(int videoSourceType) {
        this.videoSourceType = videoSourceType;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getDatePosition() {
        return datePosition;
    }

    public void setDatePosition(int datePosition) {
        this.datePosition = datePosition;
    }

    public String getDateGroup() {
        return dateGroup;
    }

    public void setDateGroup(String dateGroup) {
        this.dateGroup = dateGroup;
    }

    // 获取本地数据数
    public static int getCount() {
        return LitePal.count(HistoryDao.class);
    }

    //加载本地缓存的阅读历史数据
    public static void getLocalDatas(FindMultiCallback<HistoryDao> callback) {
        LitePal.order("readTime desc").findAsync(HistoryDao.class).listen(callback);
    }

    // 保存一条阅读历史到本地数据库
    public static void save(final int contentType, final long relatedId,
                                             final String title, final String publicName,
                                             final String img, final boolean showVideoIcon,
                                             final int videoId, final int videoSourceType) {
        if(relatedId > 0 && !TextUtils.isEmpty(title)) {
            // 在最后插入一条
            HistoryDao history = new HistoryDao();
            history.setContentType(contentType);
            history.setRelatedId(relatedId);
            history.setReadTime(System.currentTimeMillis());
            history.setTitle(title);
            history.setPublicName(publicName);
            history.setImg(img);
            history.setShowVideoIcon(showVideoIcon);
            history.setVideoId(videoId);
            history.setVideoSourceType(videoSourceType);
            history.saveOrUpdate("contentType = ? and relatedId = ?",
                    String.valueOf(contentType), String.valueOf(relatedId));
    
            // 最多存1000条
            if (getCount() > MAX_COUNT) {
                LitePal.order("readTime desc")
                        .findLastAsync(HistoryDao.class)
                        .listen(new FindCallback<HistoryDao>() {
                            @Override
                            public void onFinish(HistoryDao historyDao) {
                                historyDao.delete();
                            }
                        });
            }
        }
    }

    // 从本地数据库移除阅读历史（列表）
    public static void remove(final List<HistoryDao> removeItems) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(CollectionUtils.isNotEmpty(removeItems)) {
                    for(HistoryDao dao : removeItems) {
                        dao.delete();
                    }
                }
            }
        }).start();
    }

    // 清除阅读历史
    public static void clearAll() {
        LitePal.deleteAllAsync(HistoryDao.class).listen((rowsAffected) -> {
        });
    }
}
