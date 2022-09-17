package com.mtime.player.receivers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.player.OnTimerUpdateListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.touch.OnTouchGestureListener;
import com.kk.taurus.playerbase.utils.TimeUtil;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.video.bean.VideoInfoBean;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by JiaJunHui on 2018/6/14.
 */
public class NewControllerCover extends BaseCover implements OnTouchGestureListener, OnTimerUpdateListener {

    @BindView(R.id.player_back_icon)
    ImageView mBackIv;
    @BindView(R.id.player_title)
    TextView mTitleTv;
    @BindView(R.id.video_layout_player_top_barrage_open_state_tb)
    ToggleButton mBarrageToggle;
    @BindView(R.id.video_layout_player_top_share_iv)
    ImageView mShareIv;
    @BindView(R.id.controller_cover_top_container)
    LinearLayout mTopContainer;
    @BindView(R.id.layout_controller_cover_edit_barrage_iv)
    ImageView mEditBarrageIv;
    @BindView(R.id.layout_controller_cover_edit_rl)
    RelativeLayout mEditDanmuIconContainer;
    @BindView(R.id.layout_controller_cover_play_pause_rl)
    RelativeLayout mPlayPauseBigStateContainer;
    @BindView(R.id.layout_controller_cover_replay_iv)
    RelativeLayout mReplayIconContainer;
    @BindView(R.id.video_layout_player_control_review_iv)
    ImageView mReplayIv;
    @BindView(R.id.layout_controller_cover_play_pause_tb)
    ImageView mPlayStateBigIconIv;
    @BindView(R.id.rl_buttons_container)
    RelativeLayout mButtonsContainer;
    @BindView(R.id.layout_player_controller_play_next_tip_tv)
    TextView mPlayNextTipTv;
    @BindView(R.id.player_state_icon)
    ImageView mStateSmallIconIv;
    @BindView(R.id.player_next_icon)
    ImageView mPlayerNextIconIv;
    @BindView(R.id.cover_player_controller_text_view_curr_time)
    TextView mCurrTimeTv;
    @BindView(R.id.cover_player_controller_seek_bar)
    SeekBar mSeekBar;
    @BindView(R.id.cover_player_controller_text_view_total_time)
    TextView mTotalTime;
    @BindView(R.id.view_player_bottom_section_tv)
    TextView mRecommendVideosTv;
    @BindView(R.id.view_player_bottom_definition_tv)
    TextView mDefinitionTv;
    @BindView(R.id.video_layout_player_screen_switch_iv)
    ImageView mScreenSwitchIv;
    @BindView(R.id.rl_bottom_container)
    LinearLayout mBottomContainer;
    @BindView(R.id.layout_player_controller_bottom_dark_bg_ll)
    View mBottomDarkBg;
    @BindView(R.id.cover_player_controller_bottom_progress_bar)
    ProgressBar mBottomProgressBar;

    private Unbinder unbinder;

    private boolean isControllerShow;
    private boolean mTimerUpdateEnable = true;

    private boolean isControllerEnable = true;

    private String mTimeFormat;

    private int mSeekTraceProgress;

    private final int PLAY_STATE_ICON_MIN_W_H_DP = 41;
    private final int PLAY_STATE_ICON_MAX_W_H_DP = 62;

    private final int MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CODE_DELAY_HIDDEN_CONTROLLER:
                    hiddenController();
                    break;
            }
        }
    };

    public NewControllerCover(Context context) {
        super(context);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_new_controller_cover, null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());

        int statusBarHeight = PlayerHelper.getStatusBarHeight(getContext());
        mTopContainer.setPadding(
                mTopContainer.getPaddingLeft(),
                statusBarHeight,
                mTopContainer.getPaddingRight(),
                mTopContainer.getPaddingBottom());

        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mBarrageToggle.setOnCheckedChangeListener(onCheckedChangeListener);

        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    getGroupValue().putBoolean(DataInter.Key.KEY_DANMU_OPEN_STATE, isChecked);
                }
            };

    private int getDuration() {
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter == null ? 0 : playerStateGetter.getDuration();
    }

    private final SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int duration = getDuration();
                    if (fromUser && duration > 0) {
                        updateUI(progress, duration, -1);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mTimerUpdateEnable = false;
                    removeDelayHiddenMessage();
                    notifyReceiverPrivateEvent(
                            DataInter.ReceiverKey.KEY_LOG_RECEIVER,
                            DataInter.ReceiverEvent.EVENT_CODE_SEEK_START_TRACING_TOUCH, null);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    sendDelayHiddenMessage();
                    mSeekTraceProgress = seekBar.getProgress();
                    mHandler.removeCallbacks(mDelaySeekRunnable);
                    mHandler.postDelayed(mDelaySeekRunnable, 500);
                    notifyReceiverPrivateEvent(
                            DataInter.ReceiverKey.KEY_LOG_RECEIVER,
                            DataInter.ReceiverEvent.EVENT_CODE_SEEK_END_TRACING_TOUCH, null);
                }
            };

    private final Runnable mDelaySeekRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSeekTraceProgress < 0 || mSeekTraceProgress > getDuration()) {
                return;
            }
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_DATA, mSeekTraceProgress);
            requestSeek(bundle);
        }
    };

    private boolean isFullScreen() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_IS_FULL_SCREEN);
    }

    private boolean isNeedBottomProgressBar() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_NEED_BOTTOM_PROGRESS_BAR);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        changePlayStateCircleIcon(isFullScreen());
        changeReplayIcon(isFullScreen());
        cleanHandler();
        if (isControllerShow) {
            showControllerAndDelayHidden();
        }
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        cleanHandler();
    }

    private void cleanHandler() {
        removeDelayHiddenMessage();
        mHandler.removeCallbacks(mDelaySeekRunnable);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        cleanHandler();
        getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);

        unbinder.unbind();
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                if (!isFullScreen() && !isControllerShow) {
                    setScreenSwitchIconState(false);
                }
                setPlayIconState(true);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
                hiddenController();
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                mTimerUpdateEnable = true;
                break;
            case DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY:
                if (bundle != null) {
                    VideoInfoBean bean = (VideoInfoBean) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                    if (bean != null) {
                        setTitle(bean.getTitle());
                    }
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_RESUME:
                setPlayIconState(true);
                getGroupValue().putBoolean(DataInter.Key.KEY_IS_USER_PAUSE, false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE:
                setPlayIconState(false);
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE:
                if (bundle != null) {
                    setDefinitionName(bundle.getString(EventKey.STRING_DATA));
                }
                break;
        }
    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.ReceiverEvent.EVENT_CODE_UPDATE_SEEK:
                if (bundle != null) {
                    updateUI(
                            bundle.getInt(EventKey.INT_ARG1),
                            bundle.getInt(EventKey.INT_ARG2),
                            -1);
                }
                break;
        }
        return super.onPrivateEvent(eventCode, bundle);
    }

    private void setTopContainerState(boolean state) {
        mTopContainer.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setTitle(String text) {
        if (!TextUtils.isEmpty(text)) {
            mTitleTv.setText(text);
        }
    }

    private void setDanmuToggleState(boolean state) {
//        mBarrageToggle.setVisibility(state?View.VISIBLE:View.GONE);
    }

    private void setShareIconState(boolean state) {
        mShareIv.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setButtonsContainerState(boolean state) {
        mButtonsContainer.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setPlayStateSmallIconState(boolean state) {
        mStateSmallIconIv.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setPlayStateBigIconContainerState(boolean state) {
        mPlayPauseBigStateContainer.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setEditDanmuIconContainerState(boolean state) {
//        mEditDanmuIconContainer.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setReplayIconContainerState(boolean state) {
        mReplayIconContainer.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setBottomContainerState(boolean state) {
        mBottomContainer.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setPlayNextIconState(boolean state) {
        mPlayerNextIconIv.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setRecommendVideosState(boolean state) {
        mRecommendVideosTv.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setDefinitionState(boolean state) {
        mDefinitionTv.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setScreenSwitchIconState(boolean state) {
        mScreenSwitchIv.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setBottomProgressBarState(boolean state) {
        mBottomProgressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void updateBottomProgress(int curr, int duration, int bufferPercentage) {
        mBottomProgressBar.setMax(duration);
        mBottomProgressBar.setProgress(curr);
        if (bufferPercentage > 0 && bufferPercentage <= 100) {
            int secondProgress = (int) (bufferPercentage * 1.0f / 100 * duration);
            mSeekBar.setSecondaryProgress(secondProgress);
        }
    }

    private void setDefinitionName(String definitionName) {
        if (!TextUtils.isEmpty(definitionName))
            mDefinitionTv.setText(definitionName);
    }

    /**
     * 大小屏切换时，动态调整圆形播放状态按钮的大小
     *
     * @param isFullScreen
     */
    private void changePlayStateCircleIcon(boolean isFullScreen) {
        ViewGroup.LayoutParams layoutParams = mPlayPauseBigStateContainer.getLayoutParams();
        int px = MScreenUtils.dp2px(PLAY_STATE_ICON_MIN_W_H_DP);
        if (isFullScreen) {
            px = MScreenUtils.dp2px(PLAY_STATE_ICON_MAX_W_H_DP);
        }
        layoutParams.width = px;
        layoutParams.height = px;
        mPlayPauseBigStateContainer.setLayoutParams(layoutParams);
    }

    /**
     * 大小屏切换时，动态调整重播按钮的大小
     *
     * @param isFullScreen
     */
    private void changeReplayIcon(boolean isFullScreen) {
        ViewGroup.LayoutParams layoutParams = mReplayIconContainer.getLayoutParams();
        int px = MScreenUtils.dp2px(PLAY_STATE_ICON_MIN_W_H_DP);
        if (isFullScreen) {
            px = MScreenUtils.dp2px(PLAY_STATE_ICON_MAX_W_H_DP);
        }
        layoutParams.width = px;
        layoutParams.height = px;
        mReplayIconContainer.setLayoutParams(layoutParams);
    }

    private void setSeekProgress(int curr, int duration, int bufferPercentage) {
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(curr);
        if (bufferPercentage > 0 && bufferPercentage <= 100) {
            int secondProgress = (int) (bufferPercentage * 1.0f / 100 * duration);
            mSeekBar.setSecondaryProgress(secondProgress);
        }
    }

    private void setCurrTime(int curr) {
        mCurrTimeTv.setText(TimeUtil.getTime(mTimeFormat, curr));
    }

    private void setTotalTime(int duration) {
        mTotalTime.setText(TimeUtil.getTime(mTimeFormat, duration));
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[]{
                            DataInter.Key.KEY_IS_FULL_SCREEN,
                            DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE,
                            DataInter.Key.KEY_CONTROLLER_TOP_ENABLE,
                            DataInter.Key.KEY_NEED_RECOMMEND_LIST,
                            DataInter.Key.KEY_NEED_VIDEO_DEFINITION,
                            DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE,
                            DataInter.Key.KEY_DANMU_EDIT_ENABLE,
                            DataInter.Key.KEY_NEED_PLAY_NEXT,
                            DataInter.Key.KEY_USER_GUIDE_STATE,
                            DataInter.Key.KEY_LIST_COMPLETE,
                            DataInter.Key.KEY_CURRENT_DEFINITION
                    };
                }

                @Override
                public void onValueUpdate(String s, Object o) {
                    if (DataInter.Key.KEY_IS_FULL_SCREEN.equals(s)) {
                        boolean isFullScreen = (boolean) o;
                        if (isFullScreen) {
                            setScreenSwitchIconState(false);
                        } else if (isControllerShow) {
                            setScreenSwitchIconState(true);
                        }
                        setPlayStateSmallIconState(isFullScreen);
                        changePlayStateCircleIcon(isFullScreen);
                        changeReplayIcon(isFullScreen);
                    } else if (DataInter.Key.KEY_CONTROLLER_TIMER_UPDATE_ENABLE.equals(s)) {
                        mTimerUpdateEnable = (boolean) o;
                    } else if (DataInter.Key.KEY_CONTROLLER_TOP_ENABLE.equals(s)) {
                        boolean topEnable = (boolean) o;
                        if (isControllerShow) {
                            setTopContainerState(topEnable);
                        }
                    } else if (DataInter.Key.KEY_NEED_RECOMMEND_LIST.equals(s)) {
                        boolean needRecommendState = (boolean) o;
                        setRecommendVideosState(needRecommendState);
                        setPlayNextIconState(needRecommendState);
                    } else if (DataInter.Key.KEY_NEED_VIDEO_DEFINITION.equals(s)) {
                        setDefinitionState((Boolean) o);
                    } else if (DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE.equals(s)) {
                        setShareIconState((Boolean) o);
                    } else if (DataInter.Key.KEY_DANMU_EDIT_ENABLE.equals(s)) {
                        boolean state = (boolean) o;
                        if (state && isControllerShow) {
                            setEditDanmuIconContainerState(true);
                        } else if (!state) {
                            setEditDanmuIconContainerState(false);
                        }
                    } else if (DataInter.Key.KEY_NEED_PLAY_NEXT.equals(s)) {
                        setPlayNextIconState((Boolean) o);
                    } else if (DataInter.Key.KEY_USER_GUIDE_STATE.equals(s)) {
                        boolean state = (boolean) o;
                        isControllerEnable = !state;
                        if (!isErrorState() && state) {
                            hiddenController();
                        }
                    } else if (DataInter.Key.KEY_LIST_COMPLETE.equals(s)) {
                        boolean state = (boolean) o;
                        setReplayIconContainerState(state);
                        if (state) {
                            showController();
                        }
                    } else if (DataInter.Key.KEY_CURRENT_DEFINITION.equals(s)) {
                        String definitionName = (String) o;
                        setDefinitionName(definitionName);
                    }
                }
            };

    @OnClick({
            R.id.player_back_icon,
            R.id.video_layout_player_top_share_iv,
            R.id.layout_controller_cover_edit_barrage_iv,
            R.id.layout_controller_cover_play_pause_tb,
            R.id.video_layout_player_control_review_iv,
            R.id.player_state_icon,
            R.id.player_next_icon,
            R.id.view_player_bottom_section_tv,
            R.id.view_player_bottom_definition_tv,
            R.id.video_layout_player_screen_switch_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.player_back_icon:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_BACK, null);
                break;
            case R.id.layout_controller_cover_edit_barrage_iv:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_EDIT_DANMU, null);
                break;
            case R.id.layout_controller_cover_play_pause_tb:
                togglePlayState();
                break;
            case R.id.video_layout_player_control_review_iv:
                requestReplay(null);
                setReplayIconContainerState(false);
                getGroupValue().putBoolean(DataInter.Key.KEY_LIST_COMPLETE, false);
                break;
            case R.id.player_state_icon:
                togglePlayState();
                break;
            case R.id.player_next_icon:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO, null);
                break;
            case R.id.view_player_bottom_section_tv:
                Bundle bundle = BundlePool.obtain();
                bundle.putBoolean(EventKey.BOOL_DATA, true);
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_RECOMMEND_LIST_CHANGE, bundle);
                break;
            case R.id.view_player_bottom_definition_tv:
                notifyReceiverPrivateEvent(
                        DataInter.ReceiverKey.KEY_DEFINITION_COVER,
                        DataInter.ReceiverEvent.EVENT_REQUEST_SHOW_DEFINITION_LIST, null);
                break;
            case R.id.video_layout_player_screen_switch_iv:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE, null);
                break;
            case R.id.video_layout_player_top_share_iv:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_SHARE, null);
                break;
        }
    }

    @Override
    public void onTimerUpdate(int curr, int duration, int bufferPercentage) {
        if (!mTimerUpdateEnable || duration <= 0) {
            return;
        }
        if (mTimeFormat == null) {
            mTimeFormat = TimeUtil.getFormat(duration);
        }
        updateUI(curr, duration, bufferPercentage);
    }

    private void updateUI(int curr, int duration, int bufferPercentage) {
        setSeekProgress(curr, duration, bufferPercentage);
        setCurrTime(curr);
        setTotalTime(duration);
        updateBottomProgress(curr, duration, bufferPercentage);
    }

    private void togglePlayState() {
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null) {
            int state = playerStateGetter.getState();
            if (state == IPlayer.STATE_STARTED) {
                requestPause(null);
                getGroupValue().putBoolean(DataInter.Key.KEY_IS_USER_PAUSE, true);
                setPlayIconState(false);
            } else if (state == IPlayer.STATE_PAUSED) {
                requestResume(null);
                setPlayIconState(true);
            }
        }
    }

    private void setPlayIconState(boolean isPlaying) {
        mStateSmallIconIv.setSelected(!isPlaying);
        mPlayStateBigIconIv.setSelected(!isPlaying);
    }

    private boolean isPreparing() {
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null) {
            int state = playerStateGetter.getState();
            return state == IPlayer.STATE_INITIALIZED || state == IPlayer.STATE_PREPARED;
        }
        return true;
    }

    private boolean isAllComplete() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_LIST_COMPLETE);
    }

    private boolean isTopEnable() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE);
    }

    private boolean isRecommendListEnable() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_NEED_RECOMMEND_LIST);
    }

    private boolean isVideoDefinitionEnable() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_NEED_VIDEO_DEFINITION);
    }

    private boolean isErrorState() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE);
    }

    private boolean isEditDanmuEnable() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_DANMU_EDIT_ENABLE);
    }

    private boolean isDanmuSwitchEnable() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE);
    }

    private void toggleControllerState() {
        if (isControllerShow) {
            hiddenController();
        } else {
            showControllerAndDelayHidden();
        }
    }

    private void showControllerAndDelayHidden() {
        showController();
        sendDelayHiddenMessage();
    }

    private void removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER);
    }

    private void sendDelayHiddenMessage() {
        removeDelayHiddenMessage();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 5000);
    }

    private void setBottomBgState(boolean show) {
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.player_peogress_bg);
        mBottomDarkBg.setBackground(show ? drawable : null);
    }

    private void showController() {
        isControllerShow = true;
        setBottomBgState(true);
        setScreenSwitchIconState(!isFullScreen());
        if (isPreparing()) {
            setPlayStateSmallIconState(false);
            setTopContainerState(false);
            setBottomContainerState(false);
        } else {
            if (isTopEnable()) {
                setTopContainerState(true);
            }
            setBottomContainerState(true);
        }
        if (isRecommendListEnable()) {
            setRecommendVideosState(true);
        }
        if (isVideoDefinitionEnable()) {
            setDefinitionState(true);
        }
        if (isDanmuSwitchEnable()) {
            setDanmuToggleState(true);
        }
        setBottomProgressBarState(false);
        showButtons();
        //update show state
        updateControllerShowState(true);
    }

    private void hiddenController() {
        isControllerShow = false;
        setBottomBgState(false);
        //hide
        setTopContainerState(false);
        setButtonsContainerState(false);
        setBottomContainerState(false);
        setScreenSwitchIconState(false);
        setRecommendVideosState(false);
        setDefinitionState(false);
        setDanmuToggleState(false);
        if (isNeedBottomProgressBar()) {
            setBottomProgressBarState(true);
        }
        //update show state
        updateControllerShowState(false);
        removeDelayHiddenMessage();
    }

    private void updateControllerShowState(boolean show) {
        getGroupValue().putBoolean(DataInter.Key.KEY_IS_CONTROLLER_SHOW, show);
    }

    private void showButtons() {
        setButtonsContainerState(true);
        if (isAllComplete()) {
            setPlayStateBigIconContainerState(false);
            setReplayIconContainerState(true);
        } else {
            setReplayIconContainerState(false);
            if (!isPreparing()) {
                setPlayStateBigIconContainerState(true);
            }
        }
        //edit danmu icon show state by danmuEditEnable
        setEditDanmuIconContainerState(isEditDanmuEnable());
    }

    private boolean isControllerShowEnable() {
        return isControllerEnable && !isErrorState();
    }

    @Override
    public void onSingleTapConfirmed(MotionEvent motionEvent) {
        if (isControllerShowEnable()) {
            toggleControllerState();
        } else if (isControllerShow) {
            hiddenController();
        }
    }

    @Override
    public void onLongPress(MotionEvent event) {

    }

    @Override
    public void onDoubleTap(MotionEvent motionEvent) {
        togglePlayState();
    }

    @Override
    public void onDown(MotionEvent motionEvent) {

    }

    @Override
    public void onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

    }

    @Override
    public void onEndGesture() {

    }

    @Override
    public int getCoverLevel() {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_CONTROLLER);
    }
}
