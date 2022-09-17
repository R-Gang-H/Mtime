package com.mtime.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.kotlin.android.core.CoreApp;
import com.kotlin.android.ktx.utils.LogUtils;
import com.kotlin.android.mtime.ktx.FileEnv;
import com.mtime.BuildConfig;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.BoughtTicketListBean;
import com.mtime.beans.DlgRedPacketBean;
import com.mtime.beans.SaveSeenRecommendBean;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.bussiness.ticket.bean.BaseCityBean;
import com.mtime.common.cache.CacheManager;
import com.mtime.common.cache.FileCache;
import com.mtime.common.utils.DateUtil;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.h5.StatisticH5;
import com.unionpay.UPPayAssistEx;
import com.unionpay.UPQuerySEPayInfoCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static com.kotlin.android.ktx.ext.StringExtKt.getSuffixName;

public class MtimeUtils {
    public static final String DOWNLOAD_FILENAME = FileEnv.INSTANCE.getDownloadImageDir()+"/";
    public static final String DOWNLOAD_SEATICON_DIRNAME =
            Environment.getExternalStorageDirectory().getPath() + "/时光网/seaticon/";

    // 获取两点之间距离4
    private static final double EARTH_RADIUS = 6378137.0;
    private static final int PLUGIN_NOT_INSTALLED = -1;
    private static final int PLUGIN_NEED_UPGRADE = 2;

    public static double gps2m(final double lat_a, final double lng_a, final double lat_b, final double lng_b) {

        // 取两点之间的距离，调用百度sdk提供的方法
        double dis;
        try {
            LatLng startLatLng = new LatLng(lat_a, lng_a);
            LatLng endLatLng = new LatLng(lat_b, lng_b);

            dis = DistanceUtil.getDistance(startLatLng, endLatLng);
        } catch (Throwable e) {
            final double radLat1 = ((lat_a * Math.PI) / 180.0);
            final double radLat2 = ((lat_b * Math.PI) / 180.0);
            final double a = radLat1 - radLat2;
            final double b = ((lng_a - lng_b) * Math.PI) / 180.0;
            dis = 2 * Math.asin(Math.sqrt(
                    Math.pow(Math.sin(a / 2), 2) + (Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2))));
            dis = dis * MtimeUtils.EARTH_RADIUS;
            dis = Math.round(dis * 10000) / 10000;
        }

        return dis;
    }

    // 地区筛选
    public static ArrayList<Integer> shifDistrict(final BaseCityBean baseCityBean, final int districtId) {

        final ArrayList<Integer> cId = new ArrayList<Integer>();

        if (null == baseCityBean || null == baseCityBean.getCinemas()) {
            return cId;
        }

        for (int i = 0; i < baseCityBean.getCinemas().size(); i++) {
            if (districtId == baseCityBean.getCinemas().get(i).getDistrictId()) {
                cId.add(baseCityBean.getCinemas().get(i).getId());
            }

        }

        return cId;
    }

    // 商圈筛选
    public static ArrayList<Integer> shifBusiness(final BaseCityBean baseCityBean, final int businessId) {
        final ArrayList<Integer> busiCinemaIds = new ArrayList<Integer>();

        if (null == baseCityBean || null == baseCityBean.getBusinessCinemas()) {
            return busiCinemaIds;
        }

        for (int i = 0; i < baseCityBean.getBusinessCinemas().size(); i++) {
            if (businessId == baseCityBean.getBusinessCinemas().get(i).getBusId()) {
                busiCinemaIds.add(baseCityBean.getBusinessCinemas().get(i).getCId());
            }

        }

        return busiCinemaIds;
    }

    // 地铁筛选
    public static ArrayList<Integer> shifbyTrainId(final BaseCityBean baseCityBean, final int trainid) {
        final ArrayList<Integer> cinemaIds = new ArrayList<Integer>();

        if (null == baseCityBean || null == baseCityBean.getSubwayCinemas()) {
            return cinemaIds;
        }

        for (int i = 0; i < baseCityBean.getSubwayCinemas().size(); i++) {
            if (trainid == baseCityBean.getSubwayCinemas().get(i).getStationId()) {
                cinemaIds.add(baseCityBean.getSubwayCinemas().get(i).getCId());
            }
        }

        return cinemaIds;
    }

    // 比较本地常去看的电影的观看次数
    public static void compareOffenToSee(final List<BoughtTicketListBean> offenGoList) {
        if (null == offenGoList) {
            return;
        }

        Collections.sort(offenGoList, new Comparator<Object>() {
            @Override
            public int compare(final Object arg0, final Object arg1) {
                final BoughtTicketListBean bean1 = (BoughtTicketListBean) arg0;
                final BoughtTicketListBean bean2 = (BoughtTicketListBean) arg1;
                final int flag = bean2.getBuyCount() - bean1.getBuyCount();
                if (flag == 0) {
                    return (int) (bean2.getLastBuyTime() - bean1.getLastBuyTime());// 如果次数相同
                    // 比较时间
                } else {
                    return flag;
                }
            }
        });
    }

    // 判断字符串为空
    public static boolean isNull(String str) {
        return str == null || str.length() <= 0;
    }

    public static boolean isNull(Object o) {
        return o == null;
    }

    public static String getVersion(final BaseActivity context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            App.getInstance().getPrefsManager().putString("VERSION_CODE", info.versionName);
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 引导用户去应用商店评价弹窗
    public static void showLeadToCommentDialog(final BaseActivity context) {
        final CustomAlertDlg dlg = new CustomAlertDlg(context, CustomAlertDlg.TYPE_OK_CANCEL);
        dlg.setBtnCancelListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                dlg.dismiss();
            }
        });

        dlg.setBtnOKListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                MtimeUtils.showGrade(context);
                dlg.dismiss();
            }
        });
        dlg.show();
        dlg.setText(context.getText(R.string.str_give_good_comment));
        dlg.setLabels(context.getText(R.string.str_reject) + "", context.getText(R.string.str_go_and_comment) + "");
    }

    // 弹出红包优惠券弹窗
    public static void showRedPacketDlg(final DlgRedPacketBean bean, final BaseActivity context, final int type) {
        if (null == context || context.isFinishing()) {
            return;
        }
        final Dialog advertDialog = new Dialog(context, R.style.fullscreen_notitle_transparent_dialog);
        advertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        advertDialog.setContentView(R.layout.dialog_homepage_popup_bigad);
        final ImageView img = advertDialog.findViewById(R.id.dialog_popup_bigad_img);
        ImageView imgClose = advertDialog.findViewById(R.id.dialog_popup_bigad_close);

        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (null != response.getBitmap()) {
                    int height = (int) context.getResources().getDimension(R.dimen.home_pop_ad_height);
                    int width = height * 1126 / 1522;
                    ViewGroup.LayoutParams para = img.getLayoutParams();
                    para.width = width;
                    para.height = height;
                    img.setLayoutParams(para);
                    img.setImageBitmap(response.getBitmap());
                    if (context.canShowDlg) {
                        advertDialog.show();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };

        context.volleyImageLoader.displayOriginalImg(bean.getImage(), img, listener);

        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                advertDialog.dismiss();


//                Intent intent = new Intent();
//                intent.putExtra(AdvRecommendActivity.KEY_ADVERT_ID, bean.getUrl());
//                intent.putExtra(App.getInstance().KEY_SHOWTITLE, true);
//                context.startActivity(AdvRecommendActivity.class, intent);

//                JumpUtil.startAdvRecommendActivity(context, context.assemble().toString(), bean.getUrl(),
//                        true, true, null, null, -1);
                JumpUtil.startCommonWebActivity(context, bean.getUrl(), StatisticH5.PN_H5, null,
                        true, true, true, false, context.assemble().toString());
            }
        });
        imgClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                advertDialog.dismiss();
            }
        });
    }

    /**
     * 银联支付
     * <p/>
     * “00” – 银联正式环境 “01” – 银联测试环境，该环境中不发生真实交易
     * 网站：https://open.unionpay.com/ajweb/product/detail?id=3
     */
    public static void doPay(final Activity context, final String xml) {
        if (TextUtils.isEmpty(xml)) {
            MToastUtils.showShortToast("支付报文为null,请检查");
            return;
        }
        UPPayAssistEx.startPay(context, null, null, xml, "00");

    }

    /**
     * 银联在线支付_获取手机pay名称和类型
     * <p>
     * 返回值:
     * UPSEInfoResp.SUCCESS — 成功获取 SEPay 状态
     * UPSEInfoResp. PARAM_ERROR — 传入参数有一个以上为空，获取失败
     */
    public static int getSEPayinfo(Context context, UPQuerySEPayInfoCallback callback) {
        return UPPayAssistEx.getSEPayInfo(context, callback);
    }

    /**
     * 银联在线支付_指定手机pay支付接口
     * <p>
     * 参数说明:
     * context —— 用于获取启动支付控件的活动对象的context
     * spId —— 保留使用，这里输入null
     * sysProvider —— 保留使用，这里输入null
     * xml —— 订单信息为交易流水号，即TN，为商户后台从银联后台获取。
     * mode —— 银联后台环境标识，“00”将在银联正式环境发起交易,“01”将在银联测试环境发起 交易
     * seType —— 手机pay支付类别，见表1 返回值
     */
    public static void doSEPay(Context context, String xml, String seType) {
        if (TextUtils.isEmpty(xml)) {
            MToastUtils.showShortToast("支付报文为null,请检查");
            return;
        }
        UPPayAssistEx.startSEPay(context, null, null, xml, "00", seType);
    }

    // 保存图片
    public static String savaBitmap(final Context context, final Bitmap bitmap, final String imgname) {
        final File f = new File(MtimeUtils.DOWNLOAD_FILENAME);
        // 先判断sd卡里是否有此文件夹 没有则创建
        if (!f.exists()) {
            f.mkdirs();
        }
        final String path = String.format("%s%s", MtimeUtils.DOWNLOAD_FILENAME, imgname);
        final File file = new File(path);
        FileOutputStream fOut = null;
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (fOut != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);// 把Bitmap对象解析成流
        } else {
            return null;
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });

        return path;
    }

    public static String saveBitmapToInternal(Context context, Bitmap b) {
        File dir = context.getExternalFilesDir(null);
        if (dir == null) {
            dir = context.getFilesDir();
        }
        File realDir = new File(dir, "img_cache");
        if (!realDir.exists()) {
            realDir.mkdirs();
        }
        File file = new File(realDir, System.currentTimeMillis() + ".jpg");
        FileOutputStream fOut = null;
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fOut = new FileOutputStream(file);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (fOut != null) {
            b.compress(Bitmap.CompressFormat.PNG, 100, fOut);// 把Bitmap对象解析成流
        } else {
            return null;
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static void savaSeatsIconBitmap(final Context context, final Bitmap bitmap, final String imgname) {
        final File froot = new File(MtimeUtils.DOWNLOAD_FILENAME);
        // 先判断sd卡里是否有此文件夹 没有则创建
        if (!froot.exists()) {
            froot.mkdirs();
        }
        final File f = new File(MtimeUtils.DOWNLOAD_SEATICON_DIRNAME);
        // 先判断sd卡里是否有此文件夹 没有则创建
        if (!f.exists()) {
            f.mkdirs();
        }
        final File file = new File(MtimeUtils.DOWNLOAD_SEATICON_DIRNAME + imgname);
        FileOutputStream fOut = null;
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (fOut != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);// 把Bitmap对象解析成流
        } else {
            return;
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static InputStream getSeatsIcon(String name) {
        if (!TextUtils.isEmpty(App.getInstance().getPrefsManager().getString(App.getInstance().SEATS_ICON_TAG))) {
            String iconPath = App.getInstance().getPrefsManager().getString(LoadManager.SEAT_ICON_SP_PREFIX + name);
            if (!TextUtils.isEmpty(iconPath)) {
                File iconFile = new File(iconPath);
                if (iconFile.exists()) {
                    try {
                        return new FileInputStream(iconFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        /*if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File froot = new File(MtimeUtils.DOWNLOAD_FILENAME);
                if (!froot.exists()) {
                    return null;
                }
                File file = new File(MtimeUtils.DOWNLOAD_SEATICON_DIRNAME + name);
                if (!file.exists()) {
                    return null;
                }
                FileInputStream inputStream = new FileInputStream(file);
                return inputStream;
            } catch (Exception e) {
                return null;
            }
        }*/
        return null;
    }

    public static void deleteSeatsIcon() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(MtimeUtils.DOWNLOAD_SEATICON_DIRNAME);
            if (dir.exists()) {
                deleteDir(dir);
            }
        }
    }

    public static void deleteDir(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteDir(childFiles[i]);
            }
            file.delete();
        }
    }

    public static void saveLoadImage(final Context context, final ByteArrayInputStream inStream, final String imgname) {
        final File f = new File(MtimeUtils.DOWNLOAD_FILENAME);
        if (!f.exists()) {
            f.mkdirs();
        }

        File file = new File(MtimeUtils.DOWNLOAD_FILENAME + imgname);
        try {
            if (inStream != null) {
                FileOutputStream fos = new FileOutputStream(file);
                int readLen = 0;
                byte[] buf = new byte[1024];
                while ((readLen = inStream.read(buf)) != -1) {
                    fos.write(buf, 0, readLen);
                }
                fos.flush();
                fos.close();
                inStream.close();
            }
        } catch (Exception e) {
            file.delete();
            e.printStackTrace();
        }
    }

    public static void removeLoadAndFailImage() {
        final File f = new File(MtimeUtils.DOWNLOAD_FILENAME);
        if (!f.exists()) {
            f.mkdirs();
        }

        File fileLoad = new File(MtimeUtils.DOWNLOAD_FILENAME + UIUtil.loadingFileName);
        File fileLoadFail = new File(MtimeUtils.DOWNLOAD_FILENAME + UIUtil.loadFailFileName);

        if (fileLoad.exists()) {
            fileLoad.delete();
        }
        if (fileLoadFail.exists()) {
            fileLoadFail.delete();
        }
    }

    public static void removeLocationImage() {
        final File f = new File(MtimeUtils.DOWNLOAD_FILENAME);
        if (!f.exists()) {
            f.mkdirs();
        }

        File fileLocationFail = new File(MtimeUtils.DOWNLOAD_FILENAME + UIUtil.locationFailFileName);
        if (fileLocationFail.exists()) {
            fileLocationFail.delete();
        }
    }

    public static boolean copyFile(BaseActivity context, File oldfile, String newPath) {
        try {
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldfile);

                File newFile = new File(newPath);
                FileOutputStream fos = new FileOutputStream(newFile);
                int readLen = 0;
                byte[] buf = new byte[1024];
                while ((readLen = inStream.read(buf)) != -1) {
                    fos.write(buf, 0, readLen);
                }
                fos.flush();
                fos.close();
                inStream.close();
                MediaScannerConnection.scanFile(context, new String[]{newFile.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 分割日期字符串
     */
    @Deprecated
    public static String getSplitStr(final String text, final boolean isDay) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }

        final int pos1 = text.indexOf("月");
        final int pos2 = text.indexOf("日");
        String result = null;
        if (isDay) {
            if (pos2 >= 0) {
                result = text.substring(pos1 + 1, pos2);
            } else {
                result = "";
            }

        } else {
            if (pos1 >= 0) {
                result = text.substring(0, pos1);
            } else {
                result = "";
            }
        }

        return result;
    }

    public static String[] getSplitStr(final String text) {
        String[] result = new String[3];
        if (TextUtils.isEmpty(text)) {
            return result;
        }
        final int pos0 = text.indexOf("年");
        final int pos1 = text.indexOf("月");
        final int pos2 = text.indexOf("日");
        if (pos0 != -1) {
            result[0] = text.substring(0, pos0);
        }
        if (pos1 != -1) {
            result[1] = text.substring((pos0 == -1) ? 0 : (pos0 + 1), pos1);
        }
        if (pos2 != -1) {
            result[2] = text.substring((pos1 == -1) ? ((pos0 == -1) ? 0 : (pos0 + 1)) : (pos1 + 1), pos2);
        }

        return result;
    }

    public static String getSplitOtherYearStr(final int year) {
        Time time = new Time("GMT+8");
        time.setToNow();
        int lYear = time.year;
        if (lYear != year && year > 0) {
            return year + "年";
        }
        return "";
    }

    /**
     * 如果价格后面有.0或.00则返回整数，否则返回原来字符串
     *
     * @param price
     * @return
     */
    public static String formatPrice(final String price) {
        if ((price != null) && !"".equals(price)) {
            if (price.endsWith(".0")) {
                return price.substring(0, price.length() - 2);
            }

            if (price.endsWith(".00")) {
                return price.substring(0, price.length() - 3);
            }
        }

        return price;
    }

    /**
     * 保留两位小数
     */
    public static String double2show(double num) {
        return new java.text.DecimalFormat("#0.00").format(num);
    }

    /**
     * 如果价格后面有.0或.00则返回整数，否则返回原来字符串
     *
     * @param price
     * @return
     */
    public static String formatPrice(final double price) {
        return MtimeUtils.formatPrice(String.valueOf(price));
    }

    /**
     * 如果价格后面有.0或.00则返回整数，否则返回原来字符串
     *
     * @param price
     * @return
     */
    public static String formatPrice(final float price) {
        return MtimeUtils.formatPrice(String.valueOf(price));
    }

    // 存储推荐页面看过的Id;type为类型，新闻，影评，新片热映，top100
    public static void addSeenrecommendId(final String id, final String type) {
        if (!SaveSeenRecommendId.getInstance().contains(id)) {
            final SaveSeenRecommendBean seenBean = new SaveSeenRecommendBean();
            seenBean.setId(id);
            seenBean.setType(type);
            SaveSeenRecommendId.getInstance().add(seenBean);
        }
    }

    public static void changeCitySize(final TextView textView) {
        if (textView.getText().toString().length() >= 4) {
            textView.setTextSize(12);
        } else {
            textView.setTextSize(16);
        }
    }

    // 去除中文空格
    public static String trimChineseSpace(final Context context, final String text) {
        final char[] charArray = text.toCharArray();
        final String tmp = context.getString(R.string.str_chinese_space);
        final char space = tmp.charAt(0);
        int i;
        int j;
        for (i = 0; i < charArray.length; i++) {
            if (charArray[i] == space) {
                continue;
            } else {
                break;
            }
        }

        for (j = charArray.length - 1; j >= 0; j--) {
            if (charArray[j] == space) {
                continue;
            } else {
                break;
            }
        }
        return text.substring(i, j - i);
    }

    public static Bitmap getDefaultBitmap(final Context context, final int imgW, final int imgH, final String key) {
        final Bitmap bitmap = (Bitmap) CacheManager.getInstance().getFileCache(key);
        if (bitmap != null) {
            return bitmap;
        }
        return MtimeUtils.createDefaultBitmap(context, imgW, imgH, key);
    }

    public static Bitmap getDefaultPhotoBitmap(final Context context) {
        return ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.mine_main_logout_head)).getBitmap();
    }

    private static Bitmap defaultBitmapBack;
    private static NinePatch backNinePatch;
    private static Bitmap defaultMtime;

    public static Bitmap createDefaultBitmap(final Context context, final int imgW, final int imgH, final String key) {
        if (MtimeUtils.defaultBitmapBack == null) {
            MtimeUtils.defaultBitmapBack =
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.default_back_image);
        }
        if ((MtimeUtils.backNinePatch == null) && (MtimeUtils.defaultBitmapBack != null)) {
            MtimeUtils.backNinePatch =
                    new NinePatch(MtimeUtils.defaultBitmapBack, MtimeUtils.defaultBitmapBack.getNinePatchChunk(), null);
        }
        if (MtimeUtils.defaultMtime == null) {
            MtimeUtils.defaultMtime = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_mtime);
        }
        final Bitmap bmp = Bitmap.createBitmap(imgW, imgH, Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        final Rect backRect = new Rect(0, 0, imgW, imgH);
        MtimeUtils.backNinePatch.draw(canvas, backRect);
        canvas.drawBitmap(MtimeUtils.defaultMtime, (imgW - MtimeUtils.defaultMtime.getWidth()) >> 1,
                (imgH - MtimeUtils.defaultMtime.getHeight()) >> 1, null);
        CacheManager.getInstance().putFileCache(key, bmp);
        return bmp;
    }

    public static void changeEditHint(final Activity context, final EditText editText, final String info) {
        editText.setHintTextColor(ContextCompat.getColor(context, R.color.gray_line));
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    editText.setHint("");
                } else {
                    editText.setHint(info);
                }
            }
        });
    }

    /**
     * 普通新闻 下载图片
     */
    public static void downLoadImgFromNet(final Activity context, final String imgUrl) {
        downLoadImgFromNet(context, imgUrl, false);
    }

    /**
     * 普通新闻 下载图片
     */
    public static void downLoadImgFromNet(final Activity context, final String imgUrl, boolean gif) {
        final String fileName = DOWNLOAD_FILENAME + Utils.getMd5(imgUrl) + getSuffixName(imgUrl);
        File file = new File(DOWNLOAD_FILENAME);
        if (!file.exists()){
            file.mkdirs();
        }
        final TipsDlg tDlg = new TipsDlg(context);
        final RequestCallback imgCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {
                try {
                    tDlg.dismiss();
                    MToastUtils.showShortToast("保存成功");
                    /** 扫描图片,非常重要，会将图片加入media database，如果不加入，getImages()将获取不到此图 */
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File(fileName));
                    mediaScanIntent.setData(contentUri);
                    context.sendBroadcast(mediaScanIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(final Exception e) {
                tDlg.getImg().setVisibility(View.GONE);
                tDlg.getProgressBar().setVisibility(View.GONE);
                tDlg.getTextView().setText("生成图片失败,请稍后重试");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tDlg.dismiss();
                    }
                }, 2000);
            }
        };
        tDlg.show();
        tDlg.getTextView().setText("下载中...");
        tDlg.getImg().setVisibility(View.GONE);
        tDlg.getProgressBar().setVisibility(View.VISIBLE);
        HttpUtil.download(imgUrl, null, fileName, imgCallback);
    }

    // 获取类似138****8888之类的字符串
    public static String getBindMobile(String bindMobile) {
        if (!TextUtils.isEmpty(bindMobile) && bindMobile.length() > 8) {
            return (bindMobile.substring(0, 3) + "****" + bindMobile.substring(7));
        }
        return bindMobile;
    }

    public static int getPasswordStrength(String password) {
        int score = 0;
        // 长度
        // 长度<6 -25
        // 6<=长度<16 25
        // 16<=长度<20 50

        if (password.length() < 6) {
            score = -25;
        } else if (password.length() >= 6 && password.length() < 16) {
            score = 25;
        } else if (password.length() >= 16 && password.length() <= 20) {
            score = 50;
        }

        // 组合
        // 字母大写、字母小写、数字、符号，仅其中任意1种 -25
        // 字母大写、字母小写、数字、符号，其中任意2种组合 25
        // 字母大写、字母小写、数字、符号，其中任意3种以及以上 50

        if (password.matches("^[A-Z]+$") || password.matches("^[a-z]+$")) {
            return -1; // all ABC
        } else if (password.matches("^[0-9]+$")) {
            return -2; // all Num
        } else if (password.matches("^(\\W|[_])+$")) {
            return -3; // all char
        }

        if (password.matches("^[A-Z]+$") || password.matches("^[a-z]+$") || password.matches("^[0-9]+$")
                || password.matches("^(\\W|[_])+$")) {
            score += -25;
        } else if (password.matches("^[A-Za-z]+$") || password.matches("^[A-Z0-9]+$")
                || password.matches("^([A-Z_]|\\W)+$") || password.matches("^[0-9a-z]+$")
                || password.matches("^([a-z_]|\\W)+$") || password.matches("^([0-9_]|\\W)+$")) {
            score += 25;
        } else if (password.matches("^[0-9A-Za-z]+$") || password.matches("^([A-Za-z_]|\\W)+$")
                || password.matches("^([0-9a-z_]|\\W)+$") || password.matches("^([0-9A-Za-z_]|\\W)+$")) {
            score += 50;
        }

        // 其他
        // 连续字符≥3个(123,abc)，符号不含 -10
        // 连续重复字符≥2个(aa,11)，符号不含 -10

        if (checkContinue(password)) {
            score += -10;
        }
        if (checkRepeat(password)) {
            score += -10;
        }

        int level;
        if (score <= 25) {
            level = 1; // 弱
        } else if (score >= 25 && score < 75) {
            level = 2; // 中
        } else {
            level = 3; // 强
        }

        return level;
    }

    // 检查连续：正序或倒序都算
    static boolean checkContinue(String str) {
        //
        if (TextUtils.isEmpty(str) || str.length() < 3) {
            return false;
        }

        int count = 0, direct = 1;
        String cur = "";
        int prevCharCode = -2, charCode, diff;

        for (int i = 0; i < str.length(); i++) {
            cur = str.substring(i, i + 1);
            charCode = str.charAt(i);

            if (cur.matches("^[0-9A-Za-z]+$")) {
                diff = charCode - prevCharCode;
                if (diff == 1) {
                    if (direct == 1) {
                        ++count;
                    } else {
                        count = 1;
                    }
                    direct = 1;
                } else if (diff == -1) {
                    if (direct == -1) {
                        ++count;
                    } else {
                        count = 1;
                    }
                    direct = -1;
                } else {
                    count = 0;
                }
            }

            if (count >= 2) {
                break;
            }

            prevCharCode = charCode;
        }

        return count >= 2;
    }

    // 检查数字，字母（区分大小写）重复：
    static boolean checkRepeat(String str) {
        if (str.length() < 2)
            return false;

        String cur, next;
        for (int i = 0; i < str.length(); i++) {
            cur = str.substring(i, i + 1);

            if (cur.matches("^[0-9A-Za-z]+$") && i < str.length() - 1) {
                next = str.substring(i + 1, i + 2);
                if (cur.equals(next)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String changeWebUrl(final String url) {
        String newUrl = "";

        try {
            URL urlQuery = new URL(url);
            String queryStr = urlQuery.getQuery();
            if (queryStr == null) {
                newUrl = url + "?androidversion=" + BuildConfig.VERSION_NAME;
            } else {
                newUrl = url + "&androidversion=" + BuildConfig.VERSION_NAME;
            }
        } catch (MalformedURLException e) {
            LogWriter.e(e.toString());
        }

        return newUrl;
    }

    public static void callBrowser(Context activity, String url) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            if (activity instanceof Activity) {
            } else {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            i.setData(Uri.parse(url));
            activity.startActivity(i);
        } catch (Exception e) {
            MToastUtils.showShortToast("暂无法从其他浏览器打开");
        }

    }

    // 分转元
    public static String moneyF2Y(long fen) {
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100)).toString();
    }

    // 分转元
    public static String moneyF2Y(double fen) {
        return BigDecimal.valueOf(fen).divide(new BigDecimal(100)).toString();
    }

    // 元转分
    public static String moneyY2F(double yuan) {
        return BigDecimal.valueOf(yuan).multiply(new BigDecimal(100)).toBigInteger().toString();
    }

    // 元转string
    public static String moneyY2Str(double yuan) {
        return BigDecimal.valueOf(yuan).toString();
    }

    // 判断用户手机是否已安装某App
    public static boolean isInstallAppByPackageName(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    // 校验日期是否符合yyyy-MM-dd格式
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年-两位月份-两位日期，注意yyyy-MM-dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007-02-29会被接受，并转换成2007-03-01
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static String parseMills(int time) {
        int hour = time / 3600;
        if (hour > 99) {
            hour = 99;
        }
        int minute = (time % 3600) / 60;
        int mill = ((time % 3600) % 60) % 60;
        String hourStr = String.valueOf(hour).length() == 1 ? "0" + hour : String.valueOf(hour);
        String minuteStr = String.valueOf(minute).length() == 1 ? "0" + minute : String.valueOf(minute);
        String millStr = String.valueOf(mill).length() == 1 ? "0" + mill : String.valueOf(mill);
        return new StringBuffer().append(hourStr).append(":").append(minuteStr).append(":").append(millStr).toString();
    }

    public static int[] parseMillsToArray(int time) {
        int day = time / 86400; // 86400 = 24 * 60 * 60
        int hour = (time - day * 86400) / 60 / 60;
        int min = (time - day * 86400 - hour * 3600) / 60;
        int sec = time - day * 86400 - hour * 3600 - min * 60;

        int[] array = {day, hour, min, sec};
        return array;
    }

    /**
     * 解析时间
     */
    public static String formatLongToTimeStr(Long time) {

        StringBuilder returnString = new StringBuilder();
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));

        returnString.append(hoursStr).append(":").append(minutesStr).append(":").append(secondStr);
        return returnString.toString();

    }

    public static String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }

    /**
     * 图片去色,返回灰度图片
     *
     * @param bmpOriginal 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    // 支付宝： com.eg.android.AlipayGphone
    // 微信："com.tencent.mm"
    // 微博：com.sina.weibo
    public static boolean isAppInstalled(final Context context, final String pkgName) {
        if (null == context || TextUtils.isEmpty(pkgName)) {
            return false;
        }

        try {
            context.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 因为360卫士会清理掉SD卡下mTime文件夹的内容，所以将mTime文件夹的内容移动到时光网文件夹，以后不再有mTime文件夹
     */
    public static void checkMtimeFolder() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        String mTimeFolderPath = Environment.getExternalStorageDirectory().getPath() + "/mTime/";
        if (sdCardExist) {
            File mtimeFolder = new File(mTimeFolderPath);
            if (!mtimeFolder.exists()) {
                return;
            }
            String cookie = HttpUtil.getCookieStr();
            if (!TextUtils.isEmpty(cookie)) {
                return;
            }
            boolean copyOk = copyFolder(mTimeFolderPath, MtimeUtils.DOWNLOAD_FILENAME);
            if (copyOk) {
                recursionDeleteFile(mtimeFolder);
            }
        }

    }

    /**
     * 复制整个文件夹
     */
    public static boolean copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()));
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void recursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDeleteFile(f);
            }
            file.delete();
        }
    }
    

    /*public static void setAlarmTime(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.mtime.util.LocationAlarmReceiver");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = 3 * 60 * 1000;// 闹铃间隔
        am.setRepeating(AlarmManager.RTC_WAKEUP, 1000, interval, sender);
    }

    public static void cancelAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.mtime.util.LocationAlarmReceiver");
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }*/

    /* 安装apk文件 */
    public static void downLoadApk(final Activity context) {
        // 下载软件
        HttpUtil.download(App.getInstance().updateVersion.getUrl().trim(), null,
                FileEnv.INSTANCE.getDownDir() + "mtmovie.apk", new RequestCallback() {
                    @SuppressLint("SdCardPath")
                    @Override
                    public void onSuccess(final Object o) {
                        final File file = new File(String.valueOf(o));
                        try {
                            final String command = "chmod 777 " + file.getAbsolutePath();
                            final Runtime runtime = Runtime.getRuntime();
                            runtime.exec(command);
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                        // 安装更新的apk
                        installApk(context, file);
                    }

                    @Override
                    public void onFail(final Exception e) {
                    }
                });
        MToastUtils.showShortToast(R.string.str_download_tips);
    }

    private static void installApk(final Context context, final File apkFile){
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(context, App.getInstance().getPackageName()+".fileprovider",apkFile);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /*
     * 根据unicode 判断中文字符,包括标点
     */
    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static final boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

    /*public static void updateUserInfo(final UserItem user, final boolean hasPassword) {
        if (null == App.getInstance().userInfo) {
            App.getInstance().userInfo = new AccountDetailBean();
        }

        UserManager.Companion.getInstance().isLogin() = true;
        App.getInstance().userInfo.setHasPassword(hasPassword);
        App.getInstance().userInfo.setUserId(user.getUserId());
        App.getInstance().userInfo.setHeadPic(user.getHeadImg());
        App.getInstance().userInfo.setMobile(user.getMobile());
        App.getInstance().userInfo.setSex(user.getGender());
        App.getInstance().userInfo.setNickname(user.getNickname());

        AccountManager.updateAccountInfo(App.getInstance().userInfo);
    }*/

    public static void startActivityWithID(final BaseActivity activity, final String activityId) {
        if (!TextUtils.isEmpty(activityId)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(activityId);
                activity.startActivity(clazz, activity.getIntent());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: 2019/8/12 wwl 需要验证跳转是否正确
    public static void startActivityWithID(Context context, String activityId) {
        if (!TextUtils.isEmpty(activityId)) {
            Class<?> clazz;
            try {
                clazz = Class.forName(activityId);
                Intent intent = new Intent(context, clazz);
                context.startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showGrade(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!TextUtils.isEmpty(FrameConstant.CHANNEL_ID)) {
            String markPkg = "";
            // 这里需要检查下是否存在, 存在才设置,不存在就让用户自己选择
            /*
             * huawei,360,yyb,xiaomi,oppoapp,ppuc,meizu,baidu,lenovo,letv,vivo
             */
            if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("huawei")) {
                // 华为
                markPkg = "com.huawei.appmarket";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("360")) {
                // 360 com.qihoo.appstore
                markPkg = "com.qihoo.appstore";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("yyb")) {
                // 应用宝 com.tencent.android.qqdownloader
                markPkg = "com.tencent.android.qqdownloader";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("xiaomi")) {
                // 小米
                markPkg = "com.xiaomi.market";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("oppoapp")) {
                // oppo
                markPkg = "com.oppo.market";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("ppuc")) {
                // ppuc
                markPkg = "com.pp.assistant";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("meizu")) {
                // 魅族
                markPkg = "";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("baidu")) {
                // 百度
                markPkg = "com.baidu.appsearch";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("lenovo")) {
                // 联想
                markPkg = "com.lenovo.leos.appstore";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("letv")) {
                // 乐视
                markPkg = "com.letv.tvos.appstore";
            } else if (FrameConstant.CHANNEL_ID.equalsIgnoreCase("vivo")) {
                // vivo
                markPkg = "com.bbk.appstore";
            } else {
                markPkg = null;
            }
            if (isAppInstalled(activity, markPkg)) {
                LogWriter.e("checkGrade", "install:" + markPkg);
                intent.setPackage(markPkg);
            }
        }
        // 添加保护,防止哪种什么市场都没有的,吊起crash
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            LogWriter.e("checkGrade", "ex:" + e.getLocalizedMessage());
            MToastUtils.showShortToast("您的手机没有安装Android应用市场");
        }
    }

    public static void getInstalledApps(final Activity activity) {
        List<PackageInfo> packs = activity.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            LogWriter.e("checkMarket", "pkgname:" + p.packageName);
            LogWriter.e("checkMarket", "process name:" + p.applicationInfo.processName);
            LogWriter.e("checkMarket",
                    "appName:" + p.applicationInfo.loadLabel(activity.getPackageManager()).toString());

        }
    }

    public static boolean isValiParseInt(String text) {
        boolean result = true;
        try {
            int i = Integer.parseInt(text);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private static int mDelayTime = 500;
    private static long sLastClickTime = 0;

    public static boolean isDoubleClick() {
        long curr = System.currentTimeMillis();
        if (curr - sLastClickTime > mDelayTime) {
            sLastClickTime = curr;
            return false;
        } else {
            return true;
        }
    }

    public static boolean isDoubleClick(int delayTime) {
        mDelayTime = delayTime;
        return isDoubleClick();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String formatShowTime(long second) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy年MM月dd日 (E) HH:mm");
        f.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formatTime = f.format(new Date(second * 1000));
        return formatTime.replace("星期", "周");
    }

    /**
     * 通用的 格式化发布时间
     *
     * @param second 时间/单位秒
     * @return 刚刚/分钟前/小时前/天前/周前/yyyy-MM-dd
     */
    public static String formatPublishTime(long second) {
        if (second <= 0) {
            return "";
        }
        long currentTime = MTimeUtils.getLastDiffServerTime() / 1000;
        if (currentTime == 0) {
            currentTime = System.currentTimeMillis() / 1000;
        }
        return DateUtil.getArticleCommonTime(currentTime, second > 1000000000000L ? second / 1000 : second);
    }

    private static final DecimalFormat sFormat = new DecimalFormat(",###.#");

    /**
     * 通用的 格式化count值
     *
     * @param cnt
     * @param zero
     * @return
     */
    public static String formatCount(long cnt, boolean zero) {
        if (cnt >= 100000000) {
            float f = cnt / 100000000f;
            return sFormat.format(f) + "亿";
        }
        if (cnt >= 10000) {
            float f = cnt / 10000f;
            return sFormat.format(f) + "万";
        }
        if (cnt <= 0) {
            return zero ? "0" : "";
        }
        return String.valueOf(cnt);
    }

    public static String formatCount(long cnt) {
        return formatCount(cnt, true);
    }

    /**
     * 通用的 根据影片评分获取默认内容值
     *
     * @param score
     * @return
     */
    public static int getDefaultScoreContent(double score) {
        if (score <= 2.9) {
            return R.string.st_rate_result_29;
        } else if (score <= 5.9) {
            return R.string.st_rate_result_59;
        } else if (score <= 7.9) {
            return R.string.st_rate_result_75;
        } else if (score <= 8.9) {
            return R.string.st_rate_result_89;
        } else {
            return R.string.st_rate_result_100;
        }
    }

    public static String formatFileSize(long size) {
        float kb = size / 1024f;
        float mb = kb / 1024;
        if (mb <= 1) {
            return "1M";
        }
        if (mb < 1024) {
            if (mb == (int) mb) {
                return (int) mb + "M";
            }
            return String.format("%.1fM", mb);
        }
        float gb = mb / 1024;
        return String.format("%.1fG", gb);
    }
}
