package com.mtime.player.receivers;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.playerbase.assist.InterKey;
import com.kk.taurus.playerbase.config.PConst;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.provider.IDataProvider;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kk.taurus.playerbase.utils.NetworkUtils;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.video.bean.VideoInfoBean;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerHelper;
import com.mtime.player.PlayerLibraryConfig;
import com.mtime.player.bean.MTimeVideoData;
import com.mtime.util.MtimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mtime on 2017/10/11.
 */

public class ErrorCover extends BaseCover {

    private final String TAG = "ErrorCover";

    private final int ERROR_TYPE_NETWORK_MOBILE = 1;
    private final int ERROR_TYPE_NET_ERROR = 2;
    private final int ERROR_TYPE_OTHER = 3;
    private final int ERROR_TYPE_DATA_SOURCE = 4;

    @BindView(R.id.video_layout_player_error_prompt_tv)
    TextView mTips;
    @BindView(R.id.video_layout_player_retry_tv)
    TextView mHandleButton;
    @BindView(R.id.video_layout_player_error_title_ll)
    View mErrorTitleLayout;
    @BindView(R.id.video_layout_player_error_back_icon)
    ImageView mBackIcon;
    @BindView(R.id.video_layout_player_error_title_tv)
    TextView mTitle;

    private int mErrorType;

    private int mCurrentPosition;
    private Unbinder unbinder;
    private boolean mNeedErrorTitle;

    private String mDefinitionName;
    private long mFileSize;

    public ErrorCover(Context context) {
        super(context);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_error_cover, null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());

        int statusBarHeight = PlayerHelper.getStatusBarHeight(getContext());
        mErrorTitleLayout.setPadding(
                mErrorTitleLayout.getPaddingLeft(),
                statusBarHeight,
                mErrorTitleLayout.getPaddingRight(),
                mErrorTitleLayout.getPaddingBottom());

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private void setErrorTitleLayoutState(boolean state) {
        mErrorTitleLayout.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void setErrorTitle(String text) {
        if (!TextUtils.isEmpty(text))
            mTitle.setText(text);
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
                @Override
                public String[] filterKeys() {
                    return new String[]{
                            DataInter.Key.KEY_NEED_ERROR_TITLE,
                            DataInter.Key.KEY_IS_FULL_SCREEN
                    };
                }

                @Override
                public void onValueUpdate(String key, Object value) {
                    if (DataInter.Key.KEY_NEED_ERROR_TITLE.equals(key)) {
                        mNeedErrorTitle = (boolean) value;
                        if (!mNeedErrorTitle) {
                            setErrorTitleLayoutState(false);
                        } else {
                            checkErrorTitle();
                        }
                    } else if (DataInter.Key.KEY_IS_FULL_SCREEN.equals(key)) {
                        checkErrorTitle();
                    }
                }
            };

    @OnClick({
            R.id.video_layout_player_retry_tv,
            R.id.video_layout_player_error_back_icon})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.video_layout_player_retry_tv:
                errorHandle();
                break;
            case R.id.video_layout_player_error_back_icon:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_BACK, null);
                break;
        }
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        checkNetworkState();
    }

    private void checkNetworkState() {
        boolean netConnected = NetworkUtils.isNetConnected(getContext());
        if (netConnected) {
            int networkState = NetworkUtils.getNetworkState(getContext());
            if (!PlayerLibraryConfig.ignoreMobile && networkState > PConst.NETWORK_STATE_WIFI) {
                showError(ERROR_TYPE_NETWORK_MOBILE);
            }
        } else {
            showError(ERROR_TYPE_NET_ERROR);
        }
    }

    @Override
    public void onProducerEvent(int eventCode, Bundle bundle) {
        super.onProducerEvent(eventCode, bundle);
        switch (eventCode) {
            case DataInter.ProducerEvent.EVENT_AIRPLANE_STATE_CHANGE:
                if (isAirplaneModeOn(getContext())) {
                    showError(ERROR_TYPE_NET_ERROR);
                }
                break;
        }
    }

    @Override
    public void onProducerData(String key, Object data) {
        super.onProducerData(key, data);
        if (InterKey.KEY_NETWORK_STATE.equals(key)) {
            int networkState = (int) data;
            if (networkState > PConst.NETWORK_STATE_WIFI) {
                if (PlayerLibraryConfig.ignoreMobile) {
                    recoveryPlay();
                    hiddenError();
                } else {
                    showError(ERROR_TYPE_NETWORK_MOBILE);
                }
            } else if (networkState < 0) {
                showError(ERROR_TYPE_NET_ERROR);
            } else if (networkState == PConst.NETWORK_STATE_WIFI && isErrorShow()) {
                recoveryPlay();
                hiddenError();
            }
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
                checkNetworkState();
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                DataSource dataSource = (DataSource) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                if (dataSource != null) {
                    mDefinitionName = dataSource.getTag();
                }
                if (dataSource instanceof MTimeVideoData) {
                    MTimeVideoData data = (MTimeVideoData) dataSource;
                    mFileSize = data.getFileSize();
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                hiddenError();
                break;
            case DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY:
                if (bundle != null) {
                    VideoInfoBean bean = (VideoInfoBean) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                    if (bean != null) {
                        setErrorTitle(bean.getTitle());
                    }
                }
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnErrorEventListener.ERROR_EVENT_DATA_PROVIDER_ERROR:
                if (bundle != null) {
                    int code = bundle.getInt(EventKey.INT_DATA);
                    if (IDataProvider.PROVIDER_CODE_DATA_PROVIDER_ERROR == code) {
                        showError(ERROR_TYPE_DATA_SOURCE);
                    }
                }
                break;
            default:
                showError(ERROR_TYPE_OTHER);
                break;
        }
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE:
                if (bundle != null) {
                    mDefinitionName = bundle.getString(EventKey.STRING_DATA);
                    mFileSize = bundle.getLong(EventKey.LONG_DATA);
                }
                break;
        }
    }

    public void setTipText(String text) {
        if (TextUtils.isEmpty(text))
            return;
        mTips.setText(text);
    }

    public void setHandleButtonText(String text) {
        if (TextUtils.isEmpty(text))
            return;
        mHandleButton.setText(text);
    }

    public void setHandleButtonState(boolean state) {
        mHandleButton.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private void showErrorNetToast() {
        MToastUtils.showShortToast(getContext().getResources().getString(R.string.video_string_no_network_connected));
    }

    private int getCurrentPosition() {
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter == null ? 0 : playerStateGetter.getCurrentPosition();
    }

    private void recoveryPlay() {
        Bundle bundle = BundlePool.obtain();
        bundle.putInt(EventKey.INT_DATA, mCurrentPosition);
        requestRetry(bundle);
    }

    private void errorHandle() {
        switch (mErrorType) {
            case ERROR_TYPE_NETWORK_MOBILE:
                PlayerLibraryConfig.ignoreMobile = true;
                //request go on play
                recoveryPlay();
                hiddenError();
                break;
            case ERROR_TYPE_NET_ERROR:
                showErrorNetToast();
                break;
            case ERROR_TYPE_OTHER:
                notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_ERROR_FEED_BACK, null);
                break;
            case ERROR_TYPE_DATA_SOURCE:
                //not handle...
                break;
        }
    }

    private String getString(int id) {
        return getContext().getString(id);
    }

    private boolean isErrorShow() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE);
    }

    private void showError(int errorType) {
        mErrorType = errorType;
        int bgColor = Color.BLACK;
        setHandleButtonState(true);
        mHandleButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        switch (errorType) {
            case ERROR_TYPE_NETWORK_MOBILE:
                setTipText(getString(R.string.video_string_network_no_wifi_tips));
                if (mFileSize <= 0) {
                    setHandleButtonText(getContext().getString(R.string.video_string_go_on_play));
                } else {
                    String text = getContext().getString(R.string.video_string_go_on_play_size,
                            mDefinitionName, MtimeUtils.formatFileSize(mFileSize));
                    setHandleButtonText(text);
                }
                mHandleButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_player_mobile_play_continue,
                        0, 0, 0);
                bgColor = Color.parseColor("#66000000");
                break;
            case ERROR_TYPE_NET_ERROR:
                showErrorNetToast();
                setTipText(getString(R.string.video_string_no_network_connected));
                setHandleButtonText(getString(R.string.video_string_retry));
                break;
            case ERROR_TYPE_OTHER:
                setTipText(getString(R.string.video_string_error_other));
                setHandleButtonText(getString(R.string.video_string_error_feed_back));
                break;
            case ERROR_TYPE_DATA_SOURCE:
                setTipText(getString(R.string.video_string_error_license_tip));
                setHandleButtonState(false);
                break;
        }
        getView().setBackgroundColor(bgColor);
        setCoverVisibility(View.VISIBLE);
        checkCurrentPosition();
        getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE, true);
        //request stop player
        requestStop(null);
        //check whether need error title
        checkErrorTitle();
    }

    private boolean isFullScreen() {
        return getGroupValue().getBoolean(DataInter.Key.KEY_IS_FULL_SCREEN);
    }

    private void checkErrorTitle() {
        setErrorTitleLayoutState(mNeedErrorTitle && mErrorType != ERROR_TYPE_NETWORK_MOBILE && isErrorShow() && isFullScreen());
    }

    private void checkCurrentPosition() {
        int currentPosition = getCurrentPosition();
        if (currentPosition > 0) {
            mCurrentPosition = currentPosition;
        }
    }

    private void hiddenError() {
        mErrorType = -1;
        setCoverVisibility(View.GONE);
        getGroupValue().putBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE, false);
    }

    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);
        unbinder.unbind();
    }

    @Override
    public int getCoverLevel() {
        return levelHigh(DataInter.CoverLevel.COVER_LEVEL_ERROR);
    }

}
