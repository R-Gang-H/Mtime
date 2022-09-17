package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.bussiness.video.bean.VideoAdBean;
import com.mtime.player.AdHelper;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerHelper;
import com.mtime.player.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtime on 2017/10/18.
 * 暂停广告控制层
 */

public class PauseAdCover extends BaseCover {

    public static final String KEY = "pause_ad_cover";

    private final String TAG = "PauseAdCover";

    private TextView mAdTag;
    private ImageView mAdImage;
    private ImageView mCloseIcon;
    private RelativeLayout mAdContainer;

    private boolean isAdShow;
    private boolean isFullScreen;

    private int mIndex;

    private int mPauseCount;
    private boolean needLargeDelay;

    private final float rate_w = 0.6f;
    private final float rate_h = 0.61f;

    private int image_w;
    private int image_h;

    private List<VideoAdBean.AdItem> mAdItems = new ArrayList<>();

    private final int MSG_CODE_SHOW_IMAGE = 333;
    private boolean isEditDanmu;

    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_SHOW_IMAGE:
                    final VideoAdBean.AdItem item = (VideoAdBean.AdItem) msg.obj;
                    Log.d(TAG,"id = " + item.getaID() + " url = " + item.getImage());
                    //display image
                    ImageHelper.with(getContext(), ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                            .override(image_w, image_h)
                            .load(item.getImage())
                            .view(mAdImage)
                            .error(R.drawable.collections_default)
                            .placeholder(R.drawable.collections_default)
                            .showload();
                    final Bundle bundle = new Bundle();
                    bundle.putInt(EventKey.INT_ARG1,item.getaID());
                    bundle.putInt(EventKey.INT_ARG2,mIndex+1);
                    //notify show event for statistics
                    notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_ON_PAUSE_AD_SHOW,bundle);
                    //handle applink for ad image
                    final String appLink = item.getApplinkData();
                    Log.d(TAG,"applink = " + appLink);
                    mAdImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //notify ad click event for statistics
                            notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_ON_PAUSE_AD_CLICK, bundle);
                            ApplinkManager.jump(getContext(), appLink, getPageRefer());
                        }
                    });

                    //handle ad tag
                    mAdTag.setVisibility(View.GONE);
                    if(item.isShowTag()){
                        Log.d(TAG,"showTag ..." + item.getTagDesc());
                        mAdTag.setVisibility(View.VISIBLE);
                        mAdTag.setText(item.getTagDesc());
                    }

                    //delay show next ad item
                    mIndex++;
                    int size = mAdItems.size();
                    mIndex = mIndex%size;
                    Message message = Message.obtain();
                    message.what = MSG_CODE_SHOW_IMAGE;
                    message.obj = mAdItems.get(mIndex);

                    //default use item delay duration
                    long delayTime = item.getDuration() * 1000;
                    if(mPauseCount > 0 && needLargeDelay){
                        needLargeDelay = false;
                        delayTime = delayTime * 3;
                    }

                    //loop next item
                    mHandler.sendMessageDelayed(message, delayTime);
                    break;
            }
        }
    };

    public PauseAdCover(Context context) {
        super(context);
    }

    private String getPageRefer(){
        return getGroupValue().getString(DataInter.Key.KEY_STATISTICS_PAGE_REFER);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mAdTag = findViewById(R.id.layout_pause_ad_cover_tag_tv);
        mAdContainer = findViewById(R.id.layout_pause_ad_cover_container_rl);
        mAdImage = findViewById(R.id.iv_ad_img);
        mCloseIcon = findViewById(R.id.iv_close_ad);
        mCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAdShow){
                    //hidden ad
                    setAdState(false);
                }
            }
        });

        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{
                    DataInter.Key.KEY_IS_FULL_SCREEN,
                    DataInter.Key.KEY_IS_LANDSCAPE_EDIT_DANMU
            };
        }

        @Override
        public void onValueUpdate(String s, Object o) {
            if(DataInter.Key.KEY_IS_FULL_SCREEN.equals(s)){
                isFullScreen = (boolean) o;
                setAdShowMode(isFullScreen);
            }else if(DataInter.Key.KEY_IS_LANDSCAPE_EDIT_DANMU.equals(s)){
                isEditDanmu = (boolean) o;
            }
        }
    };

    /**
     * 大小屏切换时，动态调整广告图的大小
     * @param isFullScreen
     */
    private void setAdShowMode(boolean isFullScreen){
        int screen_w = PlayerHelper.getScreenW(getContext());
        int screen_h = PlayerHelper.getScreenH(getContext());
        ViewGroup.LayoutParams layoutParams = mAdContainer.getLayoutParams();
        if(isFullScreen){
            int w = Math.max(screen_w, screen_h);
            int h = Math.min(screen_w, screen_h);
            image_w = (int) (w * rate_w);
            image_h = (int) (h * rate_h);
        }else{
            image_w = (int) (screen_w * rate_w);
            image_h = (int) ((screen_w * 9/16) * rate_h);
        }
        layoutParams.width = image_w;
        layoutParams.height = image_h;
        mAdContainer.setLayoutParams(layoutParams);
    }

    public void setAdState(boolean state){
        if(!state){
            mHandler.removeMessages(MSG_CODE_SHOW_IMAGE);
        }
        if(state && !isAdDataAvailable()){
            return;
        }
        if(state && isEditDanmu){
            setCoverVisibility(View.GONE);
            return;
        }
        isAdShow = state;
        if(isAdShow){
            handleDisPlayImage();
        }
        setAdShowMode(isFullScreen);
        setCoverVisibility(state?View.VISIBLE:View.GONE);
        if(!state){
            mAdImage.setImageResource(R.drawable.collections_default);
        }
    }

    /**
     * 需求：广告内容为2以上时，每次暂停时按照权重顺序轮换展示,当停留时间过长时（界定条件：后台配置的 多张轮播时间*3），每则广告循环播放3次，继续播放下一则广告，依次循环

     假设：

     广告A  权重：1  多张轮播时间：10s

     广告B  权重：2  多张轮播时间：15s

     广告C  权重：3  多张轮播时间：20s

     1、视频第一次暂停，显示广告A，当停留时间超过10s*3，则显示广告B

     2、视频继续播放，第二次暂停，停留时间超过15s*3，则显示广告C

     3、视频继续播放，第三次暂停，停留时间超过20s*3，则显示广告A
     */
    private void handleDisPlayImage() {
        if(isAdDataAvailable()){
            mIndex = 0;
            VideoAdBean.AdItem adItem = mAdItems.get(mIndex);
            int size = mAdItems.size();
            if(size>=2){
                int tempIndex = mPauseCount%size;
                adItem = mAdItems.get(tempIndex);
                mIndex = tempIndex;
                mPauseCount++;
                needLargeDelay = true;
            }else{
                mPauseCount = 0;
            }
            Message message = Message.obtain();
            message.what = MSG_CODE_SHOW_IMAGE;
            message.obj = adItem;
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_RESUME:
                setAdState(false);
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE:
                if(!isDanmuCoverInEditState()){
                    setAdState(true);
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                if(isAdShow){
                    setAdState(false);
                }
                break;
        }
    }

    private boolean isDanmuCoverInEditState(){
        return getGroupValue().getBoolean(DataInter.Key.KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE);
    }

    @Override
    public void onErrorEvent(int i, Bundle bundle) {

    }

    private VideoInfo getCurrentVideoInfo(){
        return getGroupValue().get(DataInter.Key.KEY_VIDEO_INFO);
    }

    private boolean isAdDataAvailable(){
        VideoInfo videoInfo = getCurrentVideoInfo();
        if(videoInfo==null)
            return false;
        List<VideoAdBean.AdItem> adList = AdHelper.getAdList(
                String.valueOf(videoInfo.getVid()), Integer.parseInt(videoInfo.getSourceType()));
        if(adList==null || adList.size()<=0)
            return false;
        mAdItems = adList;
        return true;
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_pause_ad_cover,null);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        mHandler.removeMessages(MSG_CODE_SHOW_IMAGE);
        getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    @Override
    public int getCoverLevel() {
            return levelLow(DataInter.CoverLevel.COVER_LEVEL_PAUSE_AD);
    }
}
