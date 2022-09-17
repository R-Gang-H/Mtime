package com.mtime.bussiness.video.api;

import com.kotlin.android.app.data.entity.common.CommBizCodeResult;
import com.kotlin.android.app.data.entity.community.praisestate.PraiseStateList;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.ArticleCommonResult;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.video.bean.CommentBean;
import com.mtime.bussiness.video.bean.CommentReViewBean;
import com.mtime.bussiness.video.bean.ReViewPariseByRelatedBean;
import com.mtime.network.ConstantUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mtime on 2017/11/22.
 */

public class PraiseCommentApi extends BaseApi {

    public static final int RELATED_OBJ_TYPE_VIDEO_RESOURCE = 118;
    public static final int RELATED_OBJ_TYPE_ABOUT_MOVIE = 41;

    private final List<Object> tags = new ArrayList<>();

    private void addTag(Object tag) {
        tags.add(tag);
    }

    public void cancelAllTags() {
        if (tags != null) {
            for (Object tag : tags) {
                cancel(tag);
            }
            tags.clear();
        }
    }

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    public void cancelRequest(Object tag) {
        cancel(tag);
    }

    /**
     * 半屏播放页获取评论列表
     *
     * @param tag
     * @param vid
     * @param networkListener
     */
    public void getCommentList(String tag, String vid, int top, long time, NetworkManager.NetworkListener<CommentBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("relatedObjId", vid);
        params.put("top", String.valueOf(top));
        if (time > 0) {
            params.put("time", String.valueOf(time));
        }
        params.put("relatedObjType", String.valueOf(RELATED_OBJ_TYPE_ABOUT_MOVIE));
        get(tag, ConstantUrl.GET_REVIEW_SUBJECT_LIST, params, networkListener);
    }

    /**
     * 专题页获取评论列表
     *
     * @param tag
     * @param subjectId
     * @param networkListener
     */
    public void getTopicCommentList(String tag, String subjectId, NetworkManager.NetworkListener<CommentBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("subjectId", subjectId);
        get(tag, ConstantUrl.GET_REVIEW_SUBJECT_LIST, params, networkListener);
    }

    /**
     * 提交用户评论
     *
     * @param tag
     * @param vid
     * @param position
     * @param content
     * @param networkListener
     */
    public void postUserComment(String tag, String vid, long position, String content, String relatedObjType, NetworkManager.NetworkListener<CommentReViewBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("vId", vid);
        params.put("point", String.valueOf(position));
        params.put("content", content);
        params.put("relatedObjType", relatedObjType);
        post(tag, ConstantUrl.POST_REVIEW_SUBJECT, params, networkListener);
    }

    /**
     * 提交回复评论
     *
     * @param tag
     * @param rid
     * @param vid
     * @param position
     * @param content
     * @param targetUserId
     * @param networkListener
     */
    public void postReplyComment(String tag, String rid, String vid, long position, String content, String targetUserId, NetworkManager.NetworkListener<CommentReViewBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("rId", rid);
        params.put("vId", vid);
        params.put("point", String.valueOf(position));
        params.put("content", content);
        params.put("targetUserId", targetUserId);
        params.put("relatedObjType", "41");
        post(tag, ConstantUrl.POST_REVIEW_SUBJECT, params, networkListener);
    }

    /**
     * 提交用户评论{@link ConstantUrl#POST_ARTICLE_COMMENT}
     *
     * @param tag
     * @param content
     * @param networkListener
     */
    public void postArticleUserComment(String tag, int articleId, String commentUserType, String content, int targetUserId, NetworkManager.NetworkListener<SuccessBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("articleId", String.valueOf(articleId));
        params.put("commentUserType", commentUserType);
        params.put("commentId", String.valueOf(-1));
        params.put("content", content);
        params.put("targetUserId", String.valueOf(targetUserId));
        post(tag, ConstantUrl.POST_ARTICLE_COMMENT, params, networkListener);
    }

    /**
     * 提交回复评论{@link ConstantUrl#POST_ARTICLE_COMMENT}
     *
     * @param tag
     * @param content
     * @param targetUserId
     * @param networkListener
     */
    public void postArticleReplyComment(String tag, int articleId, String commentUserType, int commentId, String content, int targetUserId, NetworkManager.NetworkListener<SuccessBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("articleId", String.valueOf(articleId));
        params.put("commentUserType", commentUserType);
        params.put("commentId", String.valueOf(commentId));
        params.put("content", content);
        params.put("targetUserId", String.valueOf(targetUserId));
        post(tag, ConstantUrl.POST_ARTICLE_COMMENT, params, networkListener);
    }

    /**
     * 请求点赞数 {@link ConstantUrl#GET_REVIEW_PARISEINFOS_BY_RELATEDIDS}
     *
     * @param tag
     * @param ids
     * @param relatedObjType
     * @param networkListener
     */
    public void getPraiseInfo(String tag, String ids, String relatedObjType, NetworkManager.NetworkListener<ReViewPariseByRelatedBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("ids", ids);
        params.put("relatedObjType", relatedObjType);
        get(tag, ConstantUrl.GET_REVIEW_PARISEINFOS_BY_RELATEDIDS, params, networkListener);
    }

    /**
     * 请求点赞数 {@link ConstantUrl#GET_ARTICLE_PRAISE_LIST}
     *
     * @param tag
     * @param ids
     * @param relatedObjType
     * @param networkListener
     */
    public void getArticlePraiseInfo(String tag, String ids, String relatedObjType, NetworkManager.NetworkListener<ReViewPariseByRelatedBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("ids", ids);
        params.put("relatedObjType", relatedObjType);
        get(tag, ConstantUrl.GET_ARTICLE_PRAISE_LIST, params, networkListener);
    }

    /**
     * 点赞或取消赞
     *
     * @param tag
     * @param id
     * @param relatedObjType
     * @param networkListener
     */
    public void postEditPraise(String tag, String id, String relatedObjType, NetworkManager.NetworkListener<AddOrDelPraiseLogBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("relatedObjType", relatedObjType);
        post(tag, ConstantUrl.ADD_OR_DEL_PRAISELOG, params, networkListener);
    }

    /**
     * 新点赞取消
     * @param tag
     * @param id
     * @param relatedObjType
     * @param isCancel
     * @param networkListener
     */
    public void postEditPraise(String tag, String id, String relatedObjType,boolean isCancel, NetworkManager.NetworkListener<CommBizCodeResult> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("objId", id);
        params.put("objType", relatedObjType);
        params.put("action",String.valueOf(isCancel?2:1));
        post(tag, ConstantUrl.PRAISEUP_OR_CANCEL, params, networkListener);
    }

    /**
     * 批量查询点赞点踩状态
     * @param objType 点赞主体类型
     * @param objIds  点赞主体对象ID，多个以逗号分隔
     */
    public void getPraiseStatList(String tag, Long objType, String objIds, NetworkManager.NetworkListener<PraiseStateList> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>(2);
        params.put("objType", String.valueOf(objType));
        params.put("objIds",objIds);
        get(tag, ConstantUrl.GET_PRAISE_STAT_LIST, params, networkListener);
    }

    /**
     * 关注公众号
     *
     * @param tag
     * @param publicId
     * @param networkListener
     */
    public void addFollowPublic(String tag, long publicId, NetworkManager.NetworkListener<ArticleCommonResult> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("publicId", String.valueOf(publicId));
        get(tag, ConstantUrl.GET_PUBLIC_ADD_FOLLOW, params, networkListener);
    }

    /**
     * 取消关注公众号
     *
     * @param tag
     * @param publicId
     * @param networkListener
     */
    public void cancelFollowPublic(String tag, long publicId, NetworkManager.NetworkListener<ArticleCommonResult> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("publicId", String.valueOf(publicId));
        get(tag, ConstantUrl.GET_PUBLIC_CANCEL_FOLLOW, params, networkListener);
    }
}
