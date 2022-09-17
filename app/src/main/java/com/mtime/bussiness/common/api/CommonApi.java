package com.mtime.bussiness.common.api;

import android.text.TextUtils;

import com.kotlin.android.app.data.entity.community.album.AlbumUpdate;
import com.kotlin.android.app.data.entity.mine.CollectionCinema;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.ResultBean;
import com.mtime.beans.UploadImageURLBean;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.common.bean.CommResultBean;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.common.bean.DeleteImage;
import com.mtime.bussiness.common.bean.MovieLatestReviewBean;
import com.mtime.bussiness.common.bean.MovieWantSeenResultBean;
import com.mtime.bussiness.common.bean.UploadedImageUrlBean;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.util.ToolsUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-30
 */
public class CommonApi extends BaseApi {
    @Override
    protected String host() {
        return null;
    }

    /**
     * 影片-设置想看
     *
     * @param movieId
     * @param flag 1:想看 2:取消想看
     * @param listener
     */
    public void setWantToSee(long movieId, int flag, NetworkManager.NetworkListener<MovieWantSeenResultBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("movieId", String.valueOf(movieId));
        parmas.put("flag", String.valueOf(flag));
        post(this, ConstantUrl.POST_MOVIE_SET_WANT_TO_SEE, parmas, listener);
    }

    /**
     * 点赞/取消赞
     *
     * @param networkListener
     */
    public void clickPraise(String id, int type, NetworkManager.NetworkListener<AddOrDelPraiseLogBean> networkListener) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("relatedObjType", String.valueOf(type));
        post(this, ConstantUrl.ADD_OR_DEL_PRAISELOG, params, networkListener);
    }

    /**
     * 该电影下当前用户的最新一条影评 包括长评和短评
     *
     * @param movieId
     * @param listener
     */
    public void movieLatestReview(String movieId, NetworkManager.NetworkListener<MovieLatestReviewBean> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("movieId", movieId);
        get(this, ConstantUrl.GET_MOVIE_LATEST_REVIEW, params, listener);
    }

    /**
     * 公共的广告请求接口
     *
     * @param cityId 位置id
     * @param positionCode 广告位code，代表是哪个位的广告，取值参考CommonAdListBean类
     * @param networkListener
     */
    public void getAdInfo(String cityId, String positionCode, NetworkManager.NetworkListener<CommonAdListBean> networkListener) {
        Map<String, String> params = new HashMap<>();
        params.put("appId", String.valueOf(FrameConstant.APP_ID));
        params.put("positionCode", positionCode);
        params.put("udid", FrameConstant.deviceToken);
        params.put("cityId", cityId);
        get(this, ConstantUrl.GET_COMMON_AD_INFO, params, networkListener);
    }

    /**
     * 推送---增加或删除上映提醒电影
     *
     * @param movieId
     * @param listener
     */
    public void addOrDeletePushRemindMovie(boolean isAdd, long movieId, NetworkManager.NetworkListener<CommResultBean> listener) {
        String pushtoken = ToolsUtils.getToken(App.getInstance());
        String jpushid = ToolsUtils.getJPushId(App.getInstance());

        if (!TextUtils.isEmpty(pushtoken) || !TextUtils.isEmpty(jpushid)) {
            Map<String, String> parameterList = new HashMap<String, String>(3);
            parameterList.put("movieId", String.valueOf(movieId));
            parameterList.put("deviceToken", pushtoken);
            parameterList.put("jPushRegID", jpushid);
            if (isAdd) {
                post(this, ConstantUrl.ADD_REMIND_MOVIE, parameterList, listener);
            } else {
                post(this, ConstantUrl.DELETE_REMIND_MOVIE, parameterList, listener);
            }
        }
    }

    /**
     * 获取图片上传服务地址
     * @param listener https://upload3.mtime.com/Upload.ashx
     */
    public void getUploadImageUrl(NetworkManager.NetworkListener<UploadImageURLBean> listener) {
        get(this, ConstantUrl.UPLOAD_IAMGE_URL, null, listener);
    }

    /**
     * 获取上传图片后的访问地址url
     * @param imageId 图片文件名 上传图片成功后所得到的文件id（格式一般类似于：[i5]/2019/11/01/23424.234234234）
     *                上传时选择ImageFileType为 ： ImageFileType.UploadImage
     * @param listener
     */
    public void getUploadedImageUrl(String imageId, NetworkManager.NetworkListener<UploadedImageUrlBean> listener) {
        Map<String, String> params = new HashMap<>(1);
        params.put("imageId", imageId);
        get(this, ConstantUrl.GET_UPLOADED_IAMGE_URL, params, listener);
    }

    /**
     * 上传头像
     * @param fileName
     * @param listener
     */
    public void postUploadHeadUrl(String fileName, NetworkManager.NetworkListener<ResultBean> listener) {
        Map<String, String> params = new HashMap<>(1);
        params.put("fileName", fileName);
        post(this, ConstantUrl.GET_UPLOAD_HEAD_URL, params, listener);
    }

    /**
     * 删除相册中图片
     * @param imageId
     * @param albumId
     * @param listener
     */
    public void postDeleteImageFromAlbum(long imageId, long albumId, NetworkManager.NetworkListener<AlbumUpdate> listener){
        DeleteImage deleteImage = new DeleteImage(imageId, albumId);
        postJson(this,ConstantUrl.DELETE_IMAGE_FROM_ALBUM,deleteImage,null,null,listener);
    }


}
