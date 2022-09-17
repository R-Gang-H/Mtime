package com.mtime.player;

import android.app.Application;

import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DecoderPlan;
import com.kk.taurus.playerbase.log.PLog;
import com.mtime.BuildConfig;

/**
 * Created by mtime on 2017/10/12.
 */

public class PlayerLibraryConfig {

    public static final int PLAN_ID_IJK = 1;

    public static boolean ignoreMobile;
    public static boolean isListMode;

    public static void init(Application application) {
        PLog.LOG_OPEN = BuildConfig.DEBUG;
        PlayerConfig.setUseDefaultNetworkEventProducer(true);
        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_IJK, IjkPlayer.class.getName(), "ijkplayer"));
        PlayerConfig.setDefaultPlanId(PLAN_ID_IJK);
        PlayerLibrary.init(application);
    }

}
