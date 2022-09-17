package com.mtime.bussiness.ticket.stills;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.beans.Photo;
import com.mtime.bussiness.ticket.stills.utils.PhotoManager;
import com.mtime.frame.BaseFrameUIActivity;

import java.util.ArrayList;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-27
 */
public class MovieStillsDetailActivity extends BaseFrameUIActivity<ArrayList<Photo>, MovieStillsDetailHolder> {

    static final String KEY_PHOTO_LIST_TARGET_TYPE = "photo_list_target_type";
    static final String KEY_PHOTO_LIST_PHOTOS = "photo_list_totalcount";
    static final String KEY_PHOTO_LIST_POSITION = "photo_list_position_clicked";

    public static void launch(Context context, int type, ArrayList<Photo> photos, int pos) {
        Intent launcher = new Intent(context, MovieStillsDetailActivity.class);
        launcher.putExtra(KEY_PHOTO_LIST_TARGET_TYPE, type);
//        launcher.putParcelableArrayListExtra(KEY_PHOTO_LIST_PHOTOS, photos);
        PhotoManager.setPhotoData(photos);
        launcher.putExtra(KEY_PHOTO_LIST_POSITION, pos);
        dealRefer(context, "", launcher);
        context.startActivity(launcher);
    }

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new MovieStillsDetailHolder(this);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        setPageLabel("photoDetail");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PhotoManager.clearPhotoData();
    }
}
