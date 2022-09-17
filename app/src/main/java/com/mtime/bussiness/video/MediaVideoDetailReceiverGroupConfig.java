package com.mtime.bussiness.video;

import android.content.Context;

import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.player.DataInter;
import com.mtime.player.ReceiverGroupManager;
import com.mtime.player.receivers.PlayerGestureCover;
import com.mtime.player.receivers.UserGuideCover;
import com.mtime.player.receivers.VideoDefinitionCover;

/**
 * Created by JiaJunHui on 2018/6/24.
 */
public class MediaVideoDetailReceiverGroupConfig extends BaseReceiverGroupConfig {

    private VideoDefinitionCover mDefinitionCover;

    public MediaVideoDetailReceiverGroupConfig(Context context) {
        super(context);
    }

    @Override
    protected ReceiverGroup onInitReceiverGroup(Context context) {
        ReceiverGroup receiverGroup = ReceiverGroupManager.getBaseReceiverGroup(getContext());
        mDefinitionCover = new VideoDefinitionCover(context);
        PlayerGestureCover gestureCover = new PlayerGestureCover(context);
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, gestureCover);
        return receiverGroup;
    }

    @Override
    protected void onReceiverGroupCreated(Context context) {
        super.onReceiverGroupCreated(context);
        //update page refer
        if (context instanceof BaseFrameUIActivity) {
            String pageRefer = ((BaseFrameUIActivity) context).getRefer();
            pageRefer = pageRefer == null ? "" : pageRefer;
            updateGroupValue(DataInter.Key.KEY_STATISTICS_PAGE_REFER, pageRefer);
        }
        updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, true);
        updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, true);
    }

    public void updateLandScapeDanmuEditState(boolean edit) {
        updateGroupValue(DataInter.Key.KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE, edit);
    }

    public void updateStatePortraitReceiverGroup() {
        //remove some receivers
        removeReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
        removeReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
        //config params
        updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, false);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, true);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, false);
        updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, false);
        updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false);
        updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, false);
    }

    public void updateStateLandScapeReceiverGroup() {
        //need definition cover
        IReceiver fDefinitionReceiver = getReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
        if (fDefinitionReceiver == null) {
            addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, mDefinitionCover);
        }
        //need user guide cover
        if (ReceiverGroupManager.isNeedUserGuideCover(getContext())) {
            IReceiver fUserGuideReceiver = getReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
            if (fUserGuideReceiver == null) {
                addReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER, new UserGuideCover(getContext()));
            }
        }
        //config params
        updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, true);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, true);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, true);
        updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, true);
        updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false);
        updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, true);
    }

}
