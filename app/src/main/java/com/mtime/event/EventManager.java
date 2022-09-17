package com.mtime.event;

import androidx.annotation.Nullable;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.app.router.liveevent.event.LoginState;
import com.mtime.event.entity.CityChangedEvent;
import com.mtime.event.entity.LogoutEvent;
import com.mtime.event.entity.MovieWantSeeChangedEvent;
import com.mtime.event.entity.PosterFilterEvent;

import org.greenrobot.eventbus.EventBus;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_STATE;

/**
 * Created by ZhouSuQiang on 2017/11/30.
 * 事件统一管理类
 */

public class EventManager {

    private static volatile EventManager sInstance;

    private EventManager() {
    }

    public static EventManager getInstance() {
        if (sInstance == null) {
            synchronized (EventManager.class) {
                if (sInstance == null) {
                    sInstance = new EventManager();
                }
            }
        }
        return sInstance;
    }

    //发送城市变更事件
    public void sendCityChangedEvent(String oldCityId, String oldCityName, String newCityId, String newCityName) {
        EventBus.getDefault().post(new CityChangedEvent(oldCityId, oldCityName, newCityId, newCityName));
    }

    //发送退出登录事件
    public void sendLogoutEvent() {
        EventBus.getDefault().post(new LogoutEvent());
    }

    //发送登录成功事件
    public void sendLoginEvent(@Nullable String mTargetActivityId) {
//        EventBus.getDefault().post(new LoginEvent());
        LoginState state = new LoginState(true);
        state.setMTargetActivityId(mTargetActivityId);
        LiveEventBus.get(LOGIN_STATE).post(state);
    }

    //发送获取海报是否过滤事件
    public void sendPosterFilterEvent() {
        EventBus.getDefault().post(new PosterFilterEvent());
    }

    /**
     * 发送影片"想看/取消想看"变化的事件
     *
     * @param movieId   影片ID
     * @param isWantSee 是否想看
     */
    public void sendMovieWantSeeChangedEvent(String movieId, boolean isWantSee) {
        MovieWantSeeChangedEvent event = new MovieWantSeeChangedEvent();
        event.movieId = movieId;
        event.isWantSee = isWantSee;
        EventBus.getDefault().post(event);
    }
}
