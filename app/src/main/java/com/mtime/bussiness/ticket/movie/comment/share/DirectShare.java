package com.mtime.bussiness.ticket.movie.comment.share;

import android.content.Context;
import android.graphics.Bitmap;

import com.kotlin.android.share.ShareManager;
import com.kotlin.android.share.SharePlatform;
import com.kotlin.android.share.entity.ShareEntity;
import com.mtime.base.utils.AppUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.util.MtimeUtils;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-28
 */
class DirectShare {

    private final Context context;

    DirectShare(Context c) {
        context = c;
    }

    static final int CHANNEL_WECHAT = 0;
    static final int CHANNEL_MOMENTS = 1;
    static final int CHANNEL_SINA = 2;
    static final int CHANNEL_QQ = 3;

    public void shareChannel(int channel, Bitmap b) {
        switch (channel) {
            case CHANNEL_WECHAT:
                shareWechat(b, false);
                break;
            case CHANNEL_MOMENTS:
                shareWechat(b, true);
                break;
            case CHANNEL_SINA:
                shareSina(b);
                break;
            case CHANNEL_QQ:
                shareQq(b);
                break;
        }
    }

    private void shareWechat(Bitmap b, boolean moments) {
        if (!AppUtils.isWeChatInstalled(context)) {
            MToastUtils.showShortToast("请先安装微信客户端");
            return;
        }
        String path = MtimeUtils.saveBitmapToInternal(context, b);
        b.recycle();
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setImageLocalUrl(path);
        ShareManager.INSTANCE.share(moments ? SharePlatform.WE_CHAT_TIMELINE : SharePlatform.WE_CHAT, shareEntity);
    }

    private void shareSina(Bitmap b) {
        String path = MtimeUtils.saveBitmapToInternal(context, b);
        b.recycle();
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setImageLocalUrl(path);
        ShareManager.INSTANCE.share(SharePlatform.WEI_BO, shareEntity);
    }

    private void shareQq(Bitmap b) {
        String path = MtimeUtils.saveBitmapToInternal(context, b);
        b.recycle();
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setImageLocalUrl(path);
        ShareManager.INSTANCE.share(SharePlatform.QQ, shareEntity);
    }
}
