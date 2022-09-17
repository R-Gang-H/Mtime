package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.Relation;

import java.io.Serializable;
import java.util.List;

public class NewsDetailMain extends MBaseBean implements Serializable {

    private static final long serialVersionUID = -4832317148328035932L;
    private int type;//类型 0-普通、1-图集
    private int id;//新闻Id
    private String title;//新闻标题
    private String title2;//新闻标题2(副标题)
    private String content;//新闻内容
    private String time;//发布时间
    private String source; //来源
    private String author;//作者
    private String editor;//编辑
    private int commentCount;//评论总数
    private String summaryText;//新闻摘要
    private String url;//新闻url
    private List<Relation> relations;//相关影人/影片
    private List<NewsImagesBean> images;//图集图片
    private String wapUrl;//新闻wap
    private String previousNewsID; //上一篇新闻的ID
    private String nextNewsID; //下一篇新闻的id

    public List<NewsImagesBean> getImages() {
        return images;
    }

    public void setImages(final List<NewsImagesBean> images) {
        this.images = images;
    }

    public int getType() {
        return type;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(final String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String author) {
        this.author = author;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(final String editor) {
        this.editor = editor;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(final int commentCount) {
        this.commentCount = commentCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void setRelations(final List<Relation> relations) {
        this.relations = relations;
    }

    public String getSummaryInfo() {
        return summaryText;
    }

    public void setSummaryInfo(final String summaryInfo) {
        summaryText = summaryInfo;
    }

    public String getWapUrl() {
        return wapUrl;
    }

    public void setWapUrl(final String wapUrl) {
        this.wapUrl = wapUrl;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public String getPreviousNewsID() {
        return previousNewsID;
    }

    public void setPreviousNewsID(String previousNewsID) {
        this.previousNewsID = previousNewsID;
    }

    public String getNextNewsID() {
        return nextNewsID;
    }

    public void setNextNewsID(String nextNewsID) {
        this.nextNewsID = nextNewsID;
    }
}
