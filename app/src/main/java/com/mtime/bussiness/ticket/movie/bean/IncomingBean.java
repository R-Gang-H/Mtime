package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class IncomingBean extends MBaseBean {
    
    private boolean         isPreSell = false;
    private String          director;
    private int             id;
    private String          image;
    private boolean         isVideo;
    private String          releaseDate;
    private String          title;
    private String          type;
    private int             wantedCount;
    private boolean         isHead    = false;
    private boolean         isTicket;
    private int             rYear;
    private int             rMonth;
    private int             rDay;
    private String          actor1;
    private String          actor2;
    
    private List<VideoBean> videos;
    private int             videoCount;
    private String          url;
    
    private String          month;
    private String          day;
    private String          year;
    private long            date;
    private String          locationName;
    private boolean         isFilter;      //是否屏蔽海报
    private int position;
    public void setPosition(int position){
        this.position=position;
    }
    public int getPosition(){
        return position;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }
    
    public String getDirector() {
        return director;
    }
    
    public void setDirector(final String director) {
        this.director = director;
    }
    
    public String getId() {
        return String.valueOf(id);
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public void setId(final String id) {
        try {
            this.id = Integer.parseInt(id);
        }
        catch (Exception e) {
            this.id = 0;
        }
        
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(final String image) {
        this.image = image;
    }
    
    public boolean isVideo() {
        return isVideo;
    }
    
    public void setVideo(final boolean isVideo) {
        this.isVideo = isVideo;
    }
    
    public String getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getType() {
        if (TextUtils.isEmpty(type)) {
            return "";
        }
        return type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public int getWantedCount() {
        return wantedCount;
    }
    
    public void setWantedCount(final int wantedCount) {
        this.wantedCount = wantedCount;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public List<VideoBean> getVideos() {
        return videos;
    }
    
    public String getVideoUrl() {
        int index = -1;
        int length = 0;
        final String url = null;
        if (videos != null) {
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getLength() > length) {
                    length = videos.get(i).getLength();
                    index = i;
                }
            }
            if (index != -1) {
                return videos.get(index).getUrl();
            }
        }
        return url;
    }
    
    public void setVideos(final List<VideoBean> videos) {
        this.videos = videos;
    }
    
    public int getVideoCount() {
        return videoCount;
    }
    
    public void setVideoCount(final int videoCount) {
        this.videoCount = videoCount;
    }
    
    public String getMonth() {
        return month;
    }
    
    public void setMonth(final String month) {
        this.month = month;
    }
    
    public String getDay() {
        return day;
    }
    
    public void setDay(final String day) {
        this.day = day;
    }
    public String getYear() {
        return year;
    }
    
    public void setYear(final String year) {
        this.year = year;
    }
    
    /**
     * 貌似预告片应该返回第一条
     * 
     * @return 预告片地址
     */
    public String getFirstVideoUrl() {
        final int length = 0;
        final String url = null;
        if (videos != null) {
            if (videos.get(0).getLength() > length) {
                return videos.get(0).getUrl();
            }
        }
        return url;
    }

    public String getFirstHighVideoUrl() {
        final int length = 0;
        final String url = null;
        if (videos != null) {
            if (videos.get(0).getLength() > length) {
                return videos.get(0).getHightUrl();
            }
        }
        return url;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(final long date) {
        this.date = date;
    }
    
    public boolean isPreSell() {
        return isPreSell;
    }
    
    public void setPreSell(boolean isPreSell) {
        this.isPreSell = isPreSell;
    }
    
    public boolean isHead() {
        return isHead;
    }
    
    public void setHead(boolean isHead) {
        this.isHead = isHead;
    }
    
    public boolean isTicket() {
        return isTicket;
    }
    
    public void setisTicket(boolean isTicket) {
        this.isTicket = isTicket;
    }
    
    public int getrMonth() {
        return rMonth < 0 ? 0 : rMonth;
    }
    
    public void setrMonth(final int rMonth) {
        this.rMonth = rMonth;
    }
    
    public String getactor1() {
        return actor1;
    }
    
    public void setactor1(final String actor1) {
        this.actor1 = actor1;
    }
    
    public String getactor2() {
        return actor2;
    }
    
    public void setactor2(final String actor2) {
        this.actor2 = actor2;
    }
    
    public int getrDay() {
        return rDay < 0 ? 0 : rDay;
    }
    
    public void setrDay(final int rDay) {
        this.rDay = rDay;
    }
    
    public int getrYear() {
        return rYear;
    }
    
    public void setrYear(final int rYear) {
        this.rYear = rYear;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }

    public boolean isFilter() {
        return isFilter;
    }
}
