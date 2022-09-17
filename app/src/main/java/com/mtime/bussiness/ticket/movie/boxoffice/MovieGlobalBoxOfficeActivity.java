package com.mtime.bussiness.ticket.movie.boxoffice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.bussiness.ticket.movie.boxoffice.fragment.BoxOfficeFragment;
import com.mtime.bussiness.ticket.movie.details.holder.MovieGlobalBoxOfficeHolder;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;

/**
 * @author vivian.wei
 * @date 2019/6/20
 * @desc 影片全球票房榜页
 */
public class MovieGlobalBoxOfficeActivity extends BaseFrameUIActivity<Void, MovieGlobalBoxOfficeHolder> {

    private int mFromType;   //0:默认，设置为全球页签  1：点击内地票房榜。第一个tab

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new MovieGlobalBoxOfficeHolder(this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return super.onBindLoadingHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mFromType = getIntent().getIntExtra(BoxOfficeFragment.KEY_FROM_TYPE, BoxOfficeFragment.FROM_TYPE_GLOBAL);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        BoxOfficeFragment fragment = BoxOfficeFragment.newInstance(mFromType);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(getUserContentHolder().getContainerId(), fragment);
        fragmentTransaction.commit();

        // 埋点
        mBaseStatisticHelper.setPageLabel("boxOffice");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case App.MOVIE_SUB_PAGE_EVENT_CODE_BACK:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void launch(Context context, String refer, int fromType){
        Intent launcher = new Intent(context, MovieGlobalBoxOfficeActivity.class);
        launcher.putExtra(BoxOfficeFragment.KEY_FROM_TYPE,fromType);
        dealRefer(context,refer,launcher);
        context.startActivity(launcher);
    }

}
