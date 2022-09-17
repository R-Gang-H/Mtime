package com.mtime.widgets;

import android.os.Handler;
import android.os.Message;

public class FrameAnimationController {
    private static final int     MSG_ANIMATE              = 1000;

    public static final int      ANIMATION_FRAME_DURATION = 1000 / 60;

    private static final Handler mHandler                 = new AnimationHandler();

    private FrameAnimationController() {
        throw new UnsupportedOperationException();
    }

    public static void requestAnimationFrame(final Runnable runnable) {
        final Message message = new Message();
        message.what = FrameAnimationController.MSG_ANIMATE;
        message.obj = runnable;
        FrameAnimationController.mHandler
                .sendMessageDelayed(message, FrameAnimationController.ANIMATION_FRAME_DURATION);
    }

    public static void requestFrameDelay(final Runnable runnable, final long delay) {
        final Message message = new Message();
        message.what = FrameAnimationController.MSG_ANIMATE;
        message.obj = runnable;
        FrameAnimationController.mHandler.sendMessageDelayed(message, delay);
    }

    private static class AnimationHandler extends Handler {
        @Override
        public void handleMessage(final Message m) {
            switch (m.what) {
                case MSG_ANIMATE:
                    if (m.obj != null) {
                        ((Runnable) m.obj).run();
                    }
                    break;
            }
        }
    }
}
