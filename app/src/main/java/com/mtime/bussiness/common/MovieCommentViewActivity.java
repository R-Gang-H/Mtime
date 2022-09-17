package com.mtime.bussiness.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.collection.ArrayMap;


import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;
import com.mtime.beans.ResultList;
import com.mtime.beans.UploadImageURLBean;
import com.mtime.beans.UploadResultBean;
import com.mtime.bussiness.ticket.movie.bean.RatingResultJsonBean;
import com.mtime.bussiness.ticket.movie.widget.MovieRateView;
import com.mtime.common.cache.CacheManager;
import com.mtime.common.cache.FileCache;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.baidu.BaiduConstants;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.NativeImageLoader;
import com.mtime.util.UIUtil;
import com.mtime.util.UploadPicture;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by guangshun on 16/8/12.
 * 原生影片评论页/观影后评论页
 * 只在applink中使用，估计也是老需求，现在不用了
 */
public class MovieCommentViewActivity extends BaseActivity {
    private static final String KEY_MOVIE_ID = "movie_id";

    private MovieRateView rateView;
    private String movieId="";
    private Handler uploadImageHandler;
    public CommentBean comment = new CommentBean();

    class CommentBean {//用于存储上传评分的数据
        List<ImageBean> upLoadImages = new ArrayList<>(1);
        String content;
        String uploadImageUrl;
        boolean deploySubitem;
        boolean share;
        double score;
        int rDirector;
        int rOther;
        int rPicture;
        int rShow;
        int rStory;
        int rTotal;

        void clean() {
            upLoadImages = new ArrayList<>(1);
            content = null;
            uploadImageUrl = null;
            deploySubitem = false;
            share = false;
            score = 0.0d;
            rDirector = 0;
            rOther = 0;
            rPicture = 0;
            rShow = 0;
            rStory = 0;
            rTotal = 0;
        }
    }

    @Override
    protected void onInitVariable() {
        movieId = getIntent().getStringExtra(KEY_MOVIE_ID);
        setPageLabel("movieScore");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        View root = LayoutInflater.from(this).inflate(R.layout.movie_rate_view, null);
        setContentView(root);
        root.setFocusable(true);
        root.setEnabled(true);
        root.setClickable(true);
        comment.clean();
        rateView = new MovieRateView(this, root, movieId, new MovieRateView.IMovieRateViewListener() {

            @Override
            public void onEvent(MovieRateView.MovieRateViewEventType type, List<ImageBean> images, final double re, final int rateChangedMusic,
                                final int rateChangedGeneral, final int rateChangedDirector, final int rateChangedStory, final int rateChangedPerform,
                                final int rateChangedImpressions, final String content, final boolean deploySubitem, final boolean share) {
                if (MovieRateView.MovieRateViewEventType.TYPE_OK == type) {
                    UIUtil.showLoadingDialog(MovieCommentViewActivity.this, false);//发送评论过程中不让返回
                    comment.clean();
                    if (null != images && images.size() > 0) {
                        comment.upLoadImages.clear();
                        comment.upLoadImages.add(images.get(0));
                    }
                    comment.score = re;
                    comment.rDirector = rateChangedDirector;
                    comment.rOther = rateChangedMusic;
                    comment.rPicture = rateChangedGeneral;
                    comment.rShow = rateChangedPerform;
                    comment.rStory = rateChangedStory;
                    comment.rTotal = rateChangedImpressions;
                    comment.content = content;
                    comment.deploySubitem = deploySubitem;
                    comment.share = share;
                    comment.uploadImageUrl = "";

                    makeImageUrl();
                } else if (MovieRateView.MovieRateViewEventType.TYPE_CLOSE == type) {
                    JumpUtil.startMovieInfoActivity(MovieCommentViewActivity.this, assemble().toString(), movieId, 0);
                }
            }
        });
    }

    private void makeImageUrl() {
        RequestCallback requestCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                final UploadImageURLBean bean = (UploadImageURLBean) o;
                if (TextUtils.isEmpty(bean.getUploadImageUrl())) {
                    MToastUtils.showShortToast(R.string.st_upload_comment_photo_failed);
                    requestRatingMovie(null);
                    return;
                }

                Message msg = uploadImageHandler.obtainMessage();
                msg.what = 1;
                comment.uploadImageUrl = bean.getUploadImageUrl();
                uploadImageHandler.sendMessage(msg);
            }

            @Override
            public void onFail(Exception e) {
                requestRatingMovie(null);
                MToastUtils.showShortToast(R.string.st_upload_comment_photo_failed);
            }
        };

        HttpUtil.get(ConstantUrl.UPLOAD_IAMGE_URL, UploadImageURLBean.class, requestCallback);// 这上传图片流程垃圾
    }

    private Map<String, String> makeRatingParamers(final String uploadID) {
        Map<String, String> parameterList = new ArrayMap<>(10);
        parameterList.put("movieid", movieId);
        parameterList.put("r", comment.deploySubitem ? "0" : String.valueOf((int) comment.score));
        parameterList.put("ir", String.valueOf(comment.rTotal));
        parameterList.put("str", String.valueOf(comment.rStory));
        parameterList.put("shr", String.valueOf(comment.rShow));
        parameterList.put("dr", String.valueOf(comment.rDirector));
        parameterList.put("pr", String.valueOf(comment.rPicture));
        parameterList.put("mr", String.valueOf(comment.rOther));
        parameterList.put("c", comment.content);
        if (!TextUtils.isEmpty(uploadID)) {
            parameterList.put("ip", uploadID);
        }
        return parameterList;
    }

    private void requestRatingMovie(final String uploadID) {

        Map<String, String> parameterList = makeRatingParamers(uploadID);
        RequestCallback requestCallback = new RequestCallback() {

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                rateView.setRateGridImgs(null);
                CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
                MToastUtils.showShortToast("评分发送失败:" + e.getLocalizedMessage());

                JumpUtil.startMovieInfoActivity(MovieCommentViewActivity.this, assemble().toString(), movieId, 0);
            }

            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();
                RatingResultJsonBean result = (RatingResultJsonBean) o;
                if (null != result && null != result.getError()&&!result.getError().trim().equals("")) {
                    MToastUtils.showShortToast(result.getError());
                    return;
                }
                MToastUtils.showShortToast("评分提交成功");
                DecimalFormat format = new DecimalFormat("0.0");
                CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
                rateView.setRateGridImgs(null);

                if (comment.share) {
//                    final ShareView view = new ShareView(MovieCommentViewActivity.this);
//                    LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
//                        @Override
//                        public void onLocationFailure(LocationException e) {
//                            onLocationSuccess(LocationHelper.getDefaultLocationInfo());
//                        }
//
//                        @Override
//                        public void onLocationSuccess(LocationInfo locationInfo) {
//                            view.setValues(movieId, ShareView.SHARE_TYPE_MOVIE_RATE, locationInfo.getCityId(), null, null);
//                            view.showActionSheet();
//                        }
//                    });
                }

                JumpUtil.startMovieInfoActivity(MovieCommentViewActivity.this, assemble().toString(), movieId, 0);
            }
        };

        HttpUtil.post(ConstantUrl.RATING_MOVIE, parameterList, RatingResultJsonBean.class, requestCallback);
    }

    @Override
    protected void onInitEvent() {
        uploadImageHandler = new Handler() {
            public void handleMessage(final Message msg) {

                switch (msg.what) {
                    case 1:
                        CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
                        String filePath = "";
                        if (comment.upLoadImages != null && comment.upLoadImages.size() > 0) {
                            filePath = comment.upLoadImages.get(0).path;
                            File file = new File(comment.upLoadImages.get(0).path);
                            if (file.exists()) {//保证文件存在才上传
                                if (file.length() > 512000) {//如果文件大于500k，就取几兆的2倍。比如该图片是2.5M，那么就是(2+1)*2=4,产品出的垃圾需求
                                    int mSize = (((int) (file.length() / 1024 / 1024)) + 1) << 1;
                                    Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
                                            filePath, mSize, new NativeImageLoader.ImageLoaderCallBack() {

                                                @Override
                                                public void onImageLoader(Bitmap bitmap, String path) {//如果返回的bitmap对象为空才执行
                                                    File file = null;
                                                    String tmpFilePath = "";
                                                    try {
                                                        file = saveFile(bitmap);
                                                    } catch (Exception e) {

                                                    }
                                                    if (file != null && file.exists()) {
                                                        tmpFilePath = file.getPath();
                                                    } else {
                                                        tmpFilePath = comment.upLoadImages.get(0).path;
                                                    }
                                                    setUpLoadImageHandler(tmpFilePath);
                                                }
                                            });
                                    if (bitmap != null) {//不执行ImageLoaderCallBack
                                        File fl = null;
                                        try {
                                            fl = saveFile(bitmap);
                                        } catch (Exception e) {

                                        }
                                        if (fl != null && fl.exists()) {
                                            filePath = fl.getPath();
                                        } else {
                                            filePath = comment.upLoadImages.get(0).path;
                                        }
                                        setUpLoadImageHandler(filePath);
                                    }
                                } else {
                                    setUpLoadImageHandler(filePath);
                                }

                            } else {//没有有效的图片，直接上传评分
                                requestRatingMovie(null);
                            }
                        } else {//没有有效的图片，直接上传评分
                            requestRatingMovie(null);
                        }
                        break;
                    case 2:
                        String headerImagePath = (String) msg.obj;
                        if (TextUtils.isEmpty(headerImagePath)) {
                            MToastUtils.showShortToast(R.string.st_upload_comment_photo_failed);
                            requestRatingMovie(null);
                            return;
                        }
                        int pos = headerImagePath.indexOf("List");
                        if (pos > 0) {
                            headerImagePath = headerImagePath.replace("List", "resultList");
                        }

                        UploadResultBean b = (UploadResultBean) Utils.handle(headerImagePath, UploadResultBean.class);
                        if (b == null) {
                            MToastUtils.showShortToast(R.string.st_upload_comment_photo_failed);
                            requestRatingMovie(null);
                        }
                        List<ResultList> listData = b.getResult();
                        if (listData != null && listData.size() > 0 && null != listData.get(0)) {
                            String uploadID = listData.get(0).getUploadId();
                            requestRatingMovie(uploadID);
                            return;
                        } else {
                            MToastUtils.showShortToast(R.string.st_upload_comment_photo_failed);
                            requestRatingMovie(null);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private File saveFile(Bitmap bm) throws Exception {
        CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
        File tempFile = new File(FileCache.CACHE_TEMP_PIC_PATH);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        File saveFile = new File(tempFile, "sendTempFile.jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return saveFile;
    }

    private void setUpLoadImageHandler(final String imgPath) {
        if (TextUtils.isEmpty(imgPath) || TextUtils.isEmpty(comment.uploadImageUrl)) {
            requestRatingMovie(null);
            return;
        }
        new Thread() {//图片上传单起一个线程
            @Override
            public void run() {
                String headImagePath = "";
                try {
                    headImagePath = uploadImages(imgPath, "UploadImage", comment.uploadImageUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (TextUtils.isEmpty(headImagePath)) {
                    requestRatingMovie(null);
                    return;
                }
                Message msg = uploadImageHandler.obtainMessage();
                msg.what = 2;
                msg.obj = headImagePath;
                uploadImageHandler.sendMessage(msg);
            }

        }.start();
    }

    private String uploadImages(String file, String type, String uploadURL) throws Exception {
        Map<String, String> params = new ArrayMap<String, String>(3);
        params.put("UploadType", String.valueOf(1));
        params.put("imageClipType", String.valueOf(2));
        params.put("ImageFileType", type);
        UploadPicture.FormFileBean fie = new UploadPicture.FormFileBean("temp", TextUtil.getFileContent(file), "image", "multipart/form-data");
        String str = uploadImage(uploadURL, params, fie);
        return str;
    }

    /**
     * 上传图片
     *
     * @param actionUrl 上传路径
     * @param params    请求参数 key为参数名,value为参数值
     * @param file      上传文件
     */
    private String uploadImage(String actionUrl, Map<String, String> params, UploadPicture.FormFileBean file) throws Exception {

        final String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
        URL url = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 上传的表单参数部分
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
            sb.append(entry.getValue());
            sb.append("\r\n");
        }
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);// 不使用Cache
        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Content-Type", file.getContentType() + "; boundary=" + BOUNDARY);
        conn.setRequestProperty("Accept-Charset", "UTF-8,*");
        conn.setRequestProperty("Accept-Language", "zh-cn");
        conn.setRequestProperty("User-Agent", FrameConstant.UA_STR);
        conn.setRequestProperty("Cache-Control", "no-cache");
        // 设置连接主机超市（单位：毫秒）
        conn.setConnectTimeout(5000);
        // 设置从主机读取数据超时（单位：毫秒）
        conn.setReadTimeout(5000);
        // Map<String, List<String>> map1=conn.getRequestProperties();
        conn.connect();
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());// 发送表单字段数据
        // 上传的图片部分
        StringBuilder split = new StringBuilder();
        split.append("--");
        split.append(BOUNDARY);
        split.append("\r\n");
        split.append("Content-Disposition: form-data;name=\"").append(file.getFormname()).append("\";filename=\"")
                .append(file.getFilname()).append("\"\r\n");
        split.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
        outStream.write(split.toString().getBytes());
        outStream.write(file.getData(), 0, file.getData().length);
        outStream.write("\r\n".getBytes());

        byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
        outStream.write(end_data);
        outStream.flush();
        int cah = conn.getResponseCode();
        if (cah != HttpURLConnection.HTTP_OK) {
            throw new Exception("Mtime:POST request not data :" + actionUrl);
        }
        InputStream is = conn.getInputStream();
        String jsonVale = convertStreamToString(is);
        outStream.close();
        conn.disconnect();
        return jsonVale;
    }

    /**
     * 将输入流解析为String
     *
     * @param is
     * @return
     * @throws Exception
     */
    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    protected void onRequestData() {

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    public void onBackPressed() {
        JumpUtil.startMovieInfoActivity(MovieCommentViewActivity.this, assemble().toString(), movieId, 0);
        finish();
    }

    /**
     * 自己定义refer
     * @param context
     * @param refer
     * @param id
     */
    public static void launch(Context context, String refer, String id) {
        Intent launcher = new Intent(context, MovieCommentViewActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, id);
        dealRefer(context,refer, launcher);
        context.startActivity(launcher);
    }
}
