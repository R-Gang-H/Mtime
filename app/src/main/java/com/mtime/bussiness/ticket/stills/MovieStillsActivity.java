package com.mtime.bussiness.ticket.stills;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.util.Pair;
import android.util.SparseArray;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.beans.Photo;
import com.mtime.beans.PhotoAll;
import com.mtime.beans.PhotoType;
import com.mtime.bussiness.ticket.bean.CinemaPhoto;
import com.mtime.bussiness.ticket.bean.CinemaPhotoList;
import com.mtime.frame.BaseFrameUIActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-26
 * 影片剧照页面
 */
public class MovieStillsActivity extends BaseFrameUIActivity<Void, MovieStillsHolder> {

    public static final int TARGET_TYPE_ACTOR = 0; // 演员
    public static final int TARGET_TYPE_MOVIE = 1; // 电影
    public static final int TARGET_TYPE_CINEMA = 2; // 影院

    private static final String KEY_PHOTO_LIST_TARGET_ID = "photo_list_target_id";
    static final String KEY_PHOTO_LIST_TARGET_TYPE = "photo_list_target_type";
    static final String KEY_PHOTO_LIST_TITLE = "photo_list_title"; // 名称标题

    public static void launch(Context context, int type, String targetId, String title, String refer) {
        Intent launcher = new Intent(context, MovieStillsActivity.class);
        launcher.putExtra(KEY_PHOTO_LIST_TARGET_TYPE, type);
        launcher.putExtra(KEY_PHOTO_LIST_TARGET_ID, targetId);
        launcher.putExtra(KEY_PHOTO_LIST_TITLE, title);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

    private int mType;
    private String mTargetId;

    private MovieStillsHolder mHolder;
    private final StillsApi mApi = new StillsApi();

    private final SparseArray<Pair<String, ArrayList<Photo>>> mDataMap = new SparseArray<>();

    private final Map<String, String> mParams = new HashMap<>();

    @Override
    public ContentHolder onBindContentHolder() {
        return (mHolder = new MovieStillsHolder(this));
    }

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        mType = getIntent().getIntExtra(KEY_PHOTO_LIST_TARGET_TYPE, -1);
        mTargetId = getIntent().getStringExtra(KEY_PHOTO_LIST_TARGET_ID);

        if (0 == mType) {
            mParams.put("type", "2");
        } else if (1 == mType) {
            mParams.put("type", "1");
        } else {
            mParams.put("type", "3");
        }
        setPageLabel("stillsList");

        mParams.put("typeID", mTargetId);

        setBaseStatisticParam(mParams);
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.LOADING);
        switch (mType) {
            case TARGET_TYPE_ACTOR:
                mApi.getActorStills(mTargetId, photoCallback);
                break;
            case TARGET_TYPE_MOVIE:
                mApi.getMovieStills(mTargetId, photoCallback);
                break;
            case TARGET_TYPE_CINEMA:
                mApi.getCinemaStills(mTargetId, cinemaCallback);
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onErrorRetry() {
        onLoadState();
    }

    /**
     * 获取电影/影人图片回调
     */
    private final NetworkManager.NetworkListener<PhotoAll> photoCallback = new NetworkManager.NetworkListener<PhotoAll>() {
        @Override
        public void onSuccess(PhotoAll result, String showMsg) {
            setPageState(BaseState.SUCCESS);
            mHolder.showImages(result);
        }

        @Override
        public void onFailure(NetworkException<PhotoAll> exception, String showMsg) {
            setPageState(BaseState.ERROR);
        }
    };

    /**
     * 获取影院图片回调
     */
    private final NetworkManager.NetworkListener<CinemaPhotoList> cinemaCallback = new NetworkManager.NetworkListener<CinemaPhotoList>() {
        @Override
        public void onSuccess(CinemaPhotoList result, String showMsg) {
            setPageState(BaseState.SUCCESS);
            changeCinema2Photo(result);
        }

        @Override
        public void onFailure(NetworkException<CinemaPhotoList> exception, String showMsg) {
            setPageState(BaseState.ERROR);
        }
    };

    /**
     * 转换影院图片并显示
     */
    private void changeCinema2Photo(CinemaPhotoList cinemaList) {
        if(cinemaList == null || CollectionUtils.isEmpty(cinemaList.getGalleryList())) {
            return;
        }

        ArrayList<CinemaPhoto> cinemaPhotos = cinemaList.getGalleryList();
        ArrayList<Photo> photos = new ArrayList<>();
        for (CinemaPhoto cPhoto : cinemaPhotos) {
            Photo photo = new Photo();
            photo.setId(cPhoto.getImageId());
            photo.setImage(cPhoto.getImageUrl());
            int type = 0;
            try {
                type = Integer.parseInt(cPhoto.getImageType());
            } catch (Exception e) {
                e.printStackTrace();
            }

            photo.setType(type);
            photos.add(photo);
        }

        PhotoAll photoAll = new PhotoAll();
        List<PhotoType> imageTypes = new ArrayList<>();
        PhotoType type = new PhotoType();
        type.type = -1;
        type.typeName = "";
        imageTypes.add(type);
        photoAll.imageTypes = imageTypes;
        photoAll.images = photos;

        mHolder.showImages(photoAll);
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        if (null != bundle) {
            CharSequence name = bundle.getCharSequence("name", "");
            String segmentName = name == null ? "" : name.toString();

            Map<String, String> params = new HashMap<>(mParams);
            params.put("segmentName", segmentName);

            assembleOnlyRegion(params, "segment", "click").submit();
        }
    }
}
