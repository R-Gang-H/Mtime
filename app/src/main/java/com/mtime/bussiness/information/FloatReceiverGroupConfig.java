package com.mtime.bussiness.information;

import android.content.Context;

import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.mtime.bussiness.video.BaseReceiverGroupConfig;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.player.DataInter;
import com.mtime.player.ReceiverGroupManager;
import com.mtime.player.receivers.PlayerGestureCover;
import com.mtime.player.receivers.UserGuideCover;
import com.mtime.player.receivers.VideoDefinitionCover;

/**
 * Created by JiaJunHui on 2018/6/27.
 */
public class FloatReceiverGroupConfig extends BaseReceiverGroupConfig {

    public static final int RECEIVER_GROUP_STATE_PORTRAIT = 1;
    public static final int RECEIVER_GROUP_STATE_LANDSCAPE = 2;

    private VideoDefinitionCover mDefinitionCover;

    public FloatReceiverGroupConfig(Context context) {
        super(context);
    }

    @Override
    protected ReceiverGroup onInitReceiverGroup(Context context) {
        ReceiverGroup receiverGroup = ReceiverGroupManager.getBaseReceiverGroup(context);
        mDefinitionCover = new VideoDefinitionCover(context);
        PlayerGestureCover gestureCover = new PlayerGestureCover(context);
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, gestureCover);
        return receiverGroup;
    }

    @Override
    protected void onReceiverGroupCreated(Context context) {
        super.onReceiverGroupCreated(context);
        updateGroupValue(DataInter.Key.KEY_NEED_BOTTOM_PROGRESS_BAR, true);
        updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false);
        updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, false);
        updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false);
        updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, false);
        //update page refer
        if(context instanceof BaseFrameUIActivity){
            String pageRefer = ((BaseFrameUIActivity)context).getRefer();
            pageRefer = pageRefer==null?"":pageRefer;
            updateGroupValue(DataInter.Key.KEY_STATISTICS_PAGE_REFER, pageRefer);
        }
    }

    public void setReceiverGroupState(int state){
        switch (state){
            case RECEIVER_GROUP_STATE_PORTRAIT:
                //remove some receivers
                removeReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
                removeReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
                //config params
                updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, false);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
                updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, false);
                break;
            case RECEIVER_GROUP_STATE_LANDSCAPE:
                //need user guide cover
                if(ReceiverGroupManager.isNeedUserGuideCover(getContext())){
                    IReceiver fUserGuideReceiver = getReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
                    if(fUserGuideReceiver==null){
                        addReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER, new UserGuideCover(getContext()));
                    }
                }
                IReceiver definitionReceiver = getReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
                if(definitionReceiver==null){
                    addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, mDefinitionCover);
                }
                //config params
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, true);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, true);
                break;
        }
    }

}
