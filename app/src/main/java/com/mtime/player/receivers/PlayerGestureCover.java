package com.mtime.player.receivers;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;
import com.kk.taurus.playerbase.utils.TimeUtil;
import com.mtime.R;
import com.mtime.player.DataInter;

/**
 * Created by mtime on 2017/10/12.
 */

public class PlayerGestureCover extends BaseCover implements OnTouchGestureListener{

    private final String TAG = "PlayerGestureCover";

    private final int MAX_FORWARD_MS = 3 * 60 * 1000;

    private View mBrightnessBox;
    private View mVolumeBox;
    private View mFastForwardBox;
    private TextView mBrightTips;
    private TextView mSeekTips;
    private ProgressBar mVolumeProgress;
    private ProgressBar mSeekProgress;

    private long newPosition = -1;
    private float brightness = -1;
    private float currentBrightness = -1;
    private int volume;
    private AudioManager audioManager;
    private int mMaxVolume;

    private boolean firstTouch;

    private int mWidth,mHeight;

    private final boolean mGestureEnable = true;

    private boolean horizontalSlide;
    private boolean rightVerticalSlide;

    private boolean mTimerUpdateEnable = true;

    private Bundle mBundle;

    public PlayerGestureCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mBrightnessBox = findViewById(R.id.brightness_root);
        mBrightTips = findViewById(R.id.app_video_brightness);
        mSeekTips = findViewById(R.id.tv_current);
        mVolumeBox = findViewById(R.id.volume_root);
        mVolumeProgress = findViewById(R.id.volume_progressbar);
        mSeekProgress = findViewById(R.id.duration_progressbar);
        mFastForwardBox = findViewById(R.id.fast_forward_root);

        mBundle = BundlePool.obtain();

        initAudioManager(getContext());
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();

        notifyMeasure();

//        float recordBrightnessValue = PlayerHelper.getHistoryBrightness();
//        if(recordBrightnessValue > 0f){
//            setBrightness(recordBrightnessValue);
//        }
    }

    private void notifyMeasure() {
        if(mWidth <=0 || mHeight <= 0){
            getView().post(new Runnable() {
                @Override
                public void run() {
                    mWidth = getView().getWidth();
                    mHeight = getView().getHeight();
                }
            });
        }
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_gesture_cover,null);
    }

    private void setVolumeBoxState(boolean state){
        mVolumeBox.setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setBrightnessBoxState(boolean state){
        mBrightnessBox.setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setFastForwardState(boolean state){
        mFastForwardBox.setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setFastForwardProgress(int newPosition){
        int duration = getDuration();
        if(duration <= 0)
            return;
        mSeekProgress.setMax(getDuration());
        mSeekProgress.setProgress(newPosition);
    }

    private void setFastForwardTips(String text){
        int duration = getDuration();
        if(duration <= 0)
            return;
        mSeekTips.setText(text);
    }

    private void setBrightnessText(String text){
        mBrightTips.setText(text);
    }

    private void updateVolumeProgress(int curr, int max){
        mVolumeProgress.setMax(max);
        mVolumeProgress.setProgress(curr);
    }

    private void initAudioManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                notifyMeasure();
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:

                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:

                break;
        }
    }

    @Override
    public void onErrorEvent(int i, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int i, Bundle bundle) {

    }


    @Override
    public void onSingleTapConfirmed(MotionEvent event) {

    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public void onDoubleTap(MotionEvent event) {

    }

    @Override
    public void onDown(MotionEvent event) {
        firstTouch = true;
        volume = getVolume();
    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(!mGestureEnable)
            return;
        float mOldX = e1.getX(), mOldY = e1.getY();
        float deltaY = mOldY - e2.getY();
        float deltaX = mOldX - e2.getX();
        if (firstTouch) {
            horizontalSlide = Math.abs(distanceX) >= Math.abs(distanceY);
            rightVerticalSlide = mOldX > mWidth * 0.5f;
            firstTouch = false;
        }

        if(horizontalSlide){
            onHorizontalSlide(-deltaX / mWidth);
        }else{
            if(Math.abs(deltaY) > mHeight)
                return;
            if(rightVerticalSlide){
                onRightVerticalSlide(deltaY / mHeight, distanceX, distanceY);
            }else{
                onLeftVerticalSlide(deltaY / mHeight, distanceX, distanceY);
            }
        }
    }

    private int getDuration(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter==null?0:playerStateGetter.getDuration();
    }

    private int getCurrentPosition(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter==null?0:playerStateGetter.getCurrentPosition();
    }

    private void onHorizontalSlide(float percent){
        Log.d(TAG,"horizontal slide percent = " + percent);
        if(getDuration() <= 0)
            return;
        int minW = (int) (mWidth * 0.05f);
        int slideD = Math.abs((int)(mWidth * percent));
        if(slideD <= minW){
            return;
        }
        if(mTimerUpdateEnable){
            getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE, false);
            mTimerUpdateEnable = false;
        }
        long position = getCurrentPosition();
        long duration = getDuration();
        long deltaMax = Math.min(duration, MAX_FORWARD_MS);
        long delta = (long) (deltaMax * percent);
        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition =0;
        }
        setVolumeBoxState(false);
        setBrightnessBoxState(false);
        setFastForwardState(true);
        String progressText = TimeUtil.getTime(TimeUtil.TIME_FORMAT_01, newPosition);
        setFastForwardProgress((int) newPosition);
        setFastForwardTips(progressText);

        mBundle.putInt(EventKey.INT_ARG1, (int) newPosition);
        mBundle.putInt(EventKey.INT_ARG2, (int) duration);
        notifyReceiverPrivateEvent(
                DataInter.ReceiverKey.KEY_CONTROLLER_COVER,
                DataInter.ReceiverEvent.EVENT_CODE_UPDATE_SEEK,
                mBundle);
    }

    private void onRightVerticalSlide(float percent, float distanceX, float distanceY){
        if(Math.abs(distanceX) > Math.abs(distanceY))
            return;
        Log.d(TAG,"right slide percent = " + percent);
        int maxD = mMaxVolume;
        int index = (int) (percent * maxD) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        Log.d(TAG,"volume = " + index + " maxVolume = " + mMaxVolume);
        // 显示
        setBrightnessBoxState(false);
        setFastForwardState(false);
        setVolumeBoxState(true);
        updateVolumeProgress(index,mMaxVolume);
    }

    private void onLeftVerticalSlide(float percent, float distanceX, float distanceY){
        if(Math.abs(distanceX) > Math.abs(distanceY))
            return;
        Activity activity = getActivity();
        if(activity==null)
            return;
        if (brightness < 0) {
            brightness = activity.getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f){
                brightness = 0.50f;
            }else if (brightness < 0.01f){
                brightness = 0.01f;
            }
        }
        setVolumeBoxState(false);
        setFastForwardState(false);
        setBrightnessBoxState(true);
        float brightnessValue = setBrightness(brightness + percent);
        setBrightnessText(((int) (brightnessValue * 100))+"%");
        currentBrightness = brightnessValue;
    }

    private Activity getActivity(){
        Context context = getContext();
        if(context instanceof Activity){
            return (Activity)context;
        }
        return null;
    }

    private float setBrightness(float brightness){
        Activity activity = getActivity();
        if(activity==null)
            return 0f;
        WindowManager.LayoutParams lpa = activity.getWindow().getAttributes();
        lpa.screenBrightness = brightness;
        if (lpa.screenBrightness > 1.0f){
            lpa.screenBrightness = 1.0f;
        }else if (lpa.screenBrightness < 0.01f){
            lpa.screenBrightness = 0.01f;
        }
        activity.getWindow().setAttributes(lpa);
        return lpa.screenBrightness;
    }

    @Override
    public void onEndGesture() {
        volume = -1;
        brightness = -1f;
        setVolumeBoxState(false);
        setBrightnessBoxState(false);
        if(newPosition>=0 && getDuration()>0){
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_DATA, (int) newPosition);
            requestSeek(bundle);
            newPosition = -1;
        }
        setFastForwardState(false);
//        PlayerHelper.recordBrightness(currentBrightness);
        mTimerUpdateEnable = true;
    }

    private int getVolume(){
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume < 0)
            volume = 0;
        return volume;
    }

    @Override
    public int getCoverLevel() {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_GESTURE);
    }

}
