package com.mtime.player;

import android.content.Context;

import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.mtime.common.utils.PrefsManager;
import com.mtime.player.receivers.DataReceiver;
import com.mtime.player.receivers.LogReceiver;
import com.mtime.player.receivers.NewControllerCover;
import com.mtime.player.receivers.ErrorCover;
import com.mtime.player.receivers.LoadingCover;
import com.mtime.player.receivers.PauseAdCover;
import com.mtime.player.receivers.PlayerGestureCover;
import com.mtime.player.receivers.UserGuideCover;
import com.mtime.player.receivers.VideoDefinitionCover;

/**
 * Created by JiaJunHui on 2018/6/14.
 */
public class ReceiverGroupManager {

    public static final String FIRST_ENTRY_FULL_SCREEN = "first_entry_full_screen";

    public static ReceiverGroup getBaseReceiverGroup(Context context) {
        ReceiverGroup receiverGroup = new ReceiverGroup();
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_DATA_RECEIVER, new DataReceiver(context));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_LOG_RECEIVER, new LogReceiver(context));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, new LoadingCover(context));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, new NewControllerCover(context));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, new ErrorCover(context));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_PAUSE_AD_COVER, new PauseAdCover(context));
        return receiverGroup;
    }

    public static ReceiverGroup getStandardReceiverGroup(Context context) {
        ReceiverGroup receiverGroup = getBaseReceiverGroup(context);
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, new VideoDefinitionCover(context));
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new PlayerGestureCover(context));
        if (isNeedUserGuideCover(context))
            receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER, new UserGuideCover(context));
        return receiverGroup;
    }

    public static boolean isNeedUserGuideCover(Context context) {
        return !PrefsManager.get(context).getBoolean(FIRST_ENTRY_FULL_SCREEN);
    }

    public static void updateUserGuideIKnow(Context context) {
        PrefsManager.get(context).putBoolean(FIRST_ENTRY_FULL_SCREEN, true);
    }

}
