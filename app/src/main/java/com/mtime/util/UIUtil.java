package com.mtime.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.UILoadingDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class UIUtil {
    public static String loadingFileName = "loadicon";
    public static String loadFailFileName = "loading_failed";
    public static String locationFailFileName = "location_failed";
    protected static UILoadingDialog uiLoadingDialog;
    public static PopupWindow popupWindow;

    /**
     * 显示通用加载框
     */
    public static void showLoadingDialog(Context ctx) {
        showLoadingDialog(ctx, true);
    }

    /**
     * 显示通用加载框
     */
    public static void showLoadingDialog(Context ctx, boolean cancelable) {
        // 如果用户按下back取消动画，那么这里再也不会掉起动画了。因为对象不为null.
        // 或者通过监听dismiss来设置对象为null也可以。
        try {
            if (null != uiLoadingDialog) {
                uiLoadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            uiLoadingDialog = null;
        }

        try {
            uiLoadingDialog = new UILoadingDialog(ctx, R.style.LoadingDialogStyle);
            uiLoadingDialog.show();
            uiLoadingDialog.setCanceledOnTouchOutside(false);
            uiLoadingDialog.setCancelable(cancelable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏通用加载框
     */
    public static void dismissLoadingDialog() {
        try {
            if (uiLoadingDialog != null && uiLoadingDialog.isShowing()) {
//                uiLoadingDialog.recycle();
                uiLoadingDialog.dismiss();
            }
            uiLoadingDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setLoadingDialogCanceledOnTouchOutside(boolean cancelOut) {
        if (uiLoadingDialog != null) {
            uiLoadingDialog.setCanceledOnTouchOutside(cancelOut);
        }
    }

    /**
     * 爆米花扑街--普通
     */
    public static void showLoadingFailedLayout(final Activity baseActivity, final View.OnClickListener clickListener) {
        final View loading_failed_layout = baseActivity.findViewById(R.id.loading_failed_layout);
        if (loading_failed_layout == null) {
            return;
        }
        loading_failed_layout.setVisibility(View.VISIBLE);
        loading_failed_layout.findViewById(R.id.retryErrorTv).setOnClickListener(v -> {
            loading_failed_layout.setVisibility(View.GONE);
            if (clickListener != null) {
                clickListener.onClick(v);
            }
        });

    }

    /**
     * 爆米花扑街--普通
     */
    public static void showLoadingFailedLayout(final View root, final ImageView iv, final View.OnClickListener listener) {
        if (root == null) {
            return;
        }
        root.setVisibility(View.VISIBLE);
        root.setOnClickListener(v -> {
            root.setVisibility(View.GONE);
            if (listener != null) {
                listener.onClick(v);
            }
        });
        ImageHelper.with()
                .view(iv)
                .load(LoadManager.getLoadFailIcon())
                .error(R.drawable.icon_exception)
                .callback(new ImageShowLoadCallback() {
                    @Override
                    public void onLoadCompleted(Bitmap bitmap) {
                        int bwidth = bitmap.getWidth();
                        int bHeight = bitmap.getHeight();
                        int height = FrameConstant.SCREEN_WIDTH * bHeight / bwidth;
                        ViewGroup.LayoutParams para = iv.getLayoutParams();
                        para.width = FrameConstant.SCREEN_WIDTH;
                        para.height = height;
                        iv.setLayoutParams(para);
//                        iv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadFailed() {
//                        iv.setImageResource(R.drawable.loading_failed);
                    }
                })
                .showload();
    }

    public static void showLoadingFailedLayout(final View root, final TextView tv, final View.OnClickListener listener) {
        if (root == null) {
            return;
        }
        root.setVisibility(View.VISIBLE);
        tv.setOnClickListener(v -> {
            root.setVisibility(View.GONE);
            if (listener != null) {
                listener.onClick(v);
            }
        });
    }

    /**
     * 爆米花扑街--位置获取失败
     * viewId 是R.id.xxx
     * imageId 是R.id.location_failed之类
     */
    public static void showLocationFailedLayout(final BaseActivity baseActivity, int viewId, int imageId, final View.OnClickListener clickListener) {
        final View location_failed_layout = baseActivity.findViewById(viewId);
        if (location_failed_layout == null) {
            return;
        }
        location_failed_layout.setVisibility(View.VISIBLE);
        location_failed_layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                location_failed_layout.setVisibility(View.GONE);
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        });
        ImageView iv = baseActivity.findViewById(imageId);
        ImageHelper.with()
                .load(LoadManager.getLoadFailIcon())
                .callback(new ImageShowLoadCallback() {
                    @Override
                    public void onLoadCompleted(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadFailed() {
                        iv.setImageResource(R.drawable.location_failed);
                    }
                })
                .showload();
    }

    public static void loadLocationFailedPicture(final ImageView iv) {
        if (null == iv) {
            return;
        }

        ImageHelper.with()
                .load(LoadManager.getLocationIcon())
                .error(R.drawable.location_failed)
                .callback(new ImageShowLoadCallback() {
                    @Override
                    public void onLoadCompleted(Bitmap bitmap) {
                        iv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onLoadFailed() {
//                        iv.setImageResource(R.drawable.location_failed);
                    }
                })
                .showload();

        iv.setVisibility(View.VISIBLE);
    }

    public static InputStream getLoadImage(String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                File file = new File(MtimeUtils.DOWNLOAD_FILENAME + name);
                if (!file.exists()) {
                    return null;
                }
                FileInputStream inputStream = new FileInputStream(file);
                return inputStream;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static boolean fileExist(String name) {
        File file = new File(MtimeUtils.DOWNLOAD_FILENAME + name);
        return file.exists();
    }

    /**
     * 压缩图片  100k以内，不信还分享不了
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception e) {
            return image;
        }
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public interface OnPopUpComemntEditClick {
        void onSendClick(String content);
    }

    /**
     * 弹出通用评论框
     */
    public static void showPopUpComemntEdit(final Activity activity, String hint, final OnPopUpComemntEditClick onPopUpComemntEditClick) {
        if (null == popupWindow) {
            popupWindow = new PopupWindow(activity);
        }
        View contentView = LayoutInflater.from(activity).inflate(R.layout.pop_etittext, null);
        final EditText editText = contentView.findViewById(R.id.etittext);
        final Button btnSend = contentView.findViewById(R.id.btnSend);
        editText.setHint(TextUtils.isEmpty(hint) ? "写评论" : hint);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    btnSend.setEnabled(true);
                    btnSend.setBackgroundResource(R.drawable.btn_send_normal);
                    btnSend.setTextColor(ContextCompat.getColor(activity, R.color.color_f97d3f));
                } else {
                    btnSend.setEnabled(false);
                    btnSend.setBackgroundResource(R.drawable.btn_send_gray);
                    btnSend.setTextColor(ContextCompat.getColor(activity, R.color.color_bbbbbb));
                }
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
                    if (null != popupWindow) {
                        popupWindow.dismiss();
                    }
                return false;
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    MToastUtils.showLongToast("您还没有输入内容！");
                    return;
                }

                if (null != onPopUpComemntEditClick) {
                    onPopUpComemntEditClick.onSendClick(content);
                }
            }
        });
        popupWindow.setContentView(contentView);
        ColorDrawable colorDrawable = new ColorDrawable(0x00000000);
        popupWindow.setBackgroundDrawable(colorDrawable);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setInputMethodMode();
//        popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    public static void dismissPopUpComemntEdit() {
        try {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public static boolean getDialogVisible() {
        try {
            if (null != popupWindow) {
                return popupWindow.isShowing();
            }
        } catch (Exception e) {
        }
        return false;
    }
}
