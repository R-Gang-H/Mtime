//package com.mtime.bussiness.video.api;
//
//import com.mtime.base.network.BaseApi;
//import com.mtime.base.network.NetworkManager;
//import com.mtime.beans.ArticleCommonResult;
//import com.mtime.beans.ArticleInfoBean;
//import com.mtime.bussiness.information.bean.ArticleCommentBean;
//import com.mtime.bussiness.information.bean.ArticleDetailReponseBean;
//import com.mtime.bussiness.information.bean.ArticleDetailUserRelatedInfoBean;
//import com.mtime.network.ConstantUrl;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by mtime on 2017/11/21.
// */
//
//public class ArticleApi extends BaseApi {
//
//    private final List<Object> tags = new ArrayList<>();
//
//    private void addTag(Object tag) {
//        tags.add(tag);
//    }
//
//    public void cancelAllTags() {
//        if (tags != null) {
//            for (Object tag : tags) {
//                cancel(tag);
//            }
//            tags.clear();
//        }
//    }
//
//    @Override
//    protected String host() {
//        return null;
//    }
//
//    @Override
//    public void cancel(Object tag) {
//        super.cancel(tag);
//    }
//
//    public void cancelRequest(Object tag) {
//        cancel(tag);
//    }
//
//    /**
//     * 获取文章详情
//     *
//     * @param tag
//     * @param articleId
//     * @param networkListener
//     */
//    public void getArticleDetail(String tag, int articleId, NetworkManager.NetworkListener<ArticleDetailReponseBean> networkListener) {
//        addTag(tag);
//        getArticleDetail(tag, articleId, -1, -1, networkListener);
//    }
//
//    public void getArticleDetail(String tag, int articleId, int topListPageIndex, int locationId, NetworkManager.NetworkListener<ArticleDetailReponseBean> networkListener) {
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("articleId", String.valueOf(articleId));
//        if (topListPageIndex > 0) {
//            params.put("topListPageIndex", String.valueOf(topListPageIndex));
//        }
//        if (locationId > 0) {
//            params.put("locationId", String.valueOf(locationId));
//        }
//        get(tag, ConstantUrl.GET_ARTICLE_DETAIL, params, networkListener);
//    }
//
//    /**
//     * 获取文章的评论
//     *
//     * @param tag
//     * @param articleId
//     * @param pageIndex
//     * @param pageSize
//     * @param networkListener
//     */
//    public void getArticleCommentList(String tag, long articleId, int pageIndex, int pageSize, NetworkManager.NetworkListener<ArticleCommentBean> networkListener) {
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("articleId", String.valueOf(articleId));
//        params.put("pageIndex", String.valueOf(pageIndex));
//        params.put("pageSize", String.valueOf(pageSize));
//        get(tag, ConstantUrl.GET_ARTICLE_COMMENTLIST, params, networkListener);
//    }
//
//    /**
//     * 获取文章用户相关信息
//     *
//     * @param tag
//     * @param articleId
//     * @param networkListener
//     */
//    public void getArticleUserRelatedInfo(String tag, int articleId, NetworkManager.NetworkListener<ArticleDetailUserRelatedInfoBean> networkListener) {
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("articleId", String.valueOf(articleId));
//        get(tag, ConstantUrl.GET_ARTICLE_USER_RELATEDINFO, params, networkListener);
//    }
//
//    /**
//     * 获取批量文章数量相关信息（点赞数、评论数、是否收藏等等）
//     *
//     * @param tag
//     * @param articleIds
//     * @param networkListener
//     */
//    public void getArticleCountInfo(String tag, String articleIds, NetworkManager.NetworkListener<ArticleInfoBean> networkListener) {
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("articleIds", articleIds);
//        get(tag, ConstantUrl.GET_ARTICLES_INFO, params, networkListener);
//    }
//
//    /**
//     * 添加收藏
//     * @param tag
//     * @param articleId
//     * @param networkListener
//     */
//    public void articleAddFavorite(String tag, String articleId, NetworkManager.NetworkListener<ArticleCommonResult> networkListener){
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("articleId", articleId);
//        get(tag, ConstantUrl.GET_ARTICLE_ADD_FAVORITE, params, networkListener);
//    }
//
//    /**
//     * 取消收藏
//     * @param tag
//     * @param articleId
//     * @param networkListener
//     */
//    public void articledeleteFavorite(String tag, String articleId, NetworkManager.NetworkListener<ArticleCommonResult> networkListener){
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("articleId", articleId);
//        get(tag, ConstantUrl.GET_ARTICLE_CANCLE_FAVORITE, params, networkListener);
//    }
//}
