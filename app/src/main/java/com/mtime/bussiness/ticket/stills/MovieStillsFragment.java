package com.mtime.bussiness.ticket.stills;

import android.os.Bundle;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.beans.Photo;
import com.mtime.frame.BaseFrameUIFragment;

import java.util.ArrayList;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-27
 */
public class MovieStillsFragment extends BaseFrameUIFragment<ArrayList<Photo>, StillsFragmentHolder> {

    @Override
    public ContentHolder onBindContentHolder() {
        return new StillsFragmentHolder(getActivity());
    }

    @Override
    protected void onLoadState() {
        super.onLoadState();
        Bundle args = getArguments();
        ArrayList<Photo> photos = args.getParcelableArrayList("images");
        int type = args.getInt("type");
        getUserContentHolder().setStillType(type);
        setData(photos);
    }
}
