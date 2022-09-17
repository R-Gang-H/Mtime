package com.mtime.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mtime.base.network.BaseApi;
import com.mtime.beans.ImageBean;
import com.mtime.beans.UploadImageResultBean;
import com.mtime.beans.UploadImageURLBean;
import com.mtime.frame.App;
import com.mtime.network.ConstantUrl;
import com.mtime.util.image.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-04-03
 * <p>
 * 图片上传工具类
 */
public class UploadImageHelper {

    private UploadFileCallback mCallback;
    private Subscription mSubscribe;

    public UploadImageHelper(UploadFileCallback callback) {
        mCallback = callback;
    }

    private final UploadApi mApi = new UploadApi();

    public void uploadImageBean(ImageBean image) {
        uploadImage(image.path);
    }

    public void uploadImageBeans(ImageBean... images) {
        List<File> files = new ArrayList<>();
        for (ImageBean image : images) {
            files.add(new File(image.path));
        }
        uploadImageFiles(files);
    }

    public void uploadImageBeans(List<ImageBean> images) {
        List<File> files = new ArrayList<>();
        for (ImageBean image : images) {
            files.add(new File(image.path));
        }
        uploadImageFiles(files);
    }

    public void uploadImage(String path) {
        List<File> files = new ArrayList<>();
        files.add(new File(path));
        uploadImageFiles(files);
    }

    public void uploadImages(String... paths) {
        List<File> files = new ArrayList<>();
        for (String path : paths) {
            files.add(new File(path));
        }
        uploadImageFiles(files);
    }

    public void uploadImages(List<String> paths) {
        List<File> files = new ArrayList<>(paths.size());
        for (String path : paths) {
            files.add(new File(path));
        }
        uploadImageFiles(files);
    }

    public void uploadImageFiles(File... files) {
        List<File> fileList = new ArrayList<>();
        fileList.addAll(Arrays.asList(files));
        uploadImageFiles(fileList);
    }

    public void uploadImageFiles(List<File> files) {
        mSubscribe = Observable.fromCallable(new Callable<ResBean>() {
            @Override
            public ResBean call() {
                Map<String, String> imageIds = new HashMap<>();
                try {
                    UploadImageURLBean urlBean = mApi.getUploadUrl();
                    String url = urlBean.getUploadImageUrl();

                    for (File file : files) {

                        String filePath = file.getAbsolutePath();

                        if (!FileUtils.getSuffix(filePath).contains("gif")) { // gif 图片不压缩

                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            File cacheDir = App.getInstance().getCacheDir();
                            File shrink = new File(cacheDir, file.getName());

                            if (shrink.exists()) {
                                shrink.delete();
                            }
                            try {
                                FileOutputStream fos = new FileOutputStream(shrink);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos);
                                bitmap.recycle();
                                file = shrink;
                                fos.close();
                            } catch (Throwable e) {
                                e.printStackTrace();
                                bitmap.recycle();
                            }
                        }

                        UploadImageResultBean resultBean = mApi.uploadImage(url, file.getName(), file);
                        if (resultBean.httpCode != 200) {
                            if (resultBean.httpMessage != null
                                    && resultBean.httpMessage.toLowerCase().contains("large")) {
                                return ResBean.error("您上传的图片过大");
                            }
                            return ResBean.error("上传图片失败");
                        }
                        imageIds.put(filePath, resultBean.resultList.get(0).getUploadId());
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                    return ResBean.error("上传图片失败");
                }

                return ResBean.suc(imageIds);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ResBean>() {
                    @Override
                    public void call(ResBean resBean) {
                        if (resBean.suc) {
                            if (mCallback != null) {
                                mCallback.onUploadFileSuccess(resBean.imageIds);
                            }
                        } else {
                            if (mCallback != null) {
                                mCallback.onUploadFileFail(resBean.msg);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        if (mCallback != null) {
                            mCallback.onUploadFileFail("上传图片失败");
                        }
                    }
                });
    }

    public void destroy() {
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
        mCallback = null;
    }

    private static class ResBean {

        private final boolean suc;
        private final String msg;
        private final Map<String, String> imageIds;

        private ResBean(boolean suc, String msg, Map<String, String> imageIds) {
            this.suc = suc;
            this.msg = msg;
            this.imageIds = imageIds;
        }

        private static ResBean error(String msg) {
            return new ResBean(false, msg, null);
        }

        private static ResBean suc(Map<String, String> imageIds) {
            return new ResBean(true, "", imageIds);
        }
    }

    /**
     * 上传回调 ui thread
     */
    public interface UploadFileCallback {

        void onUploadFileFail(String errorMsg);

        /**
         * @param imageIds 图片 地址 id 映射表
         */
        void onUploadFileSuccess(Map<String, String> imageIds);
    }

    /**
     * api ，都是同步api，为了配合rxjava使用
     */
    private static class UploadApi extends BaseApi {

        @Override
        protected String host() {
            return null;
        }

        /**
         * 获取上传地址
         */
        private UploadImageURLBean getUploadUrl() {
            return syncGet(ConstantUrl.UPLOAD_IAMGE_URL, null, UploadImageURLBean.class);
        }

        /**
         * 上传图片接口
         * 这里是同步上传
         */
        private UploadImageResultBean uploadImage(String url, String name, File file) {
            Map<String, String> params = new HashMap<>();
            params.put("UploadType", String.valueOf(1));
            params.put("imageClipType", String.valueOf(2));
            params.put("ImageFileType", "UploadImage");
            return syncUploadFile(url, params, name, file, UploadImageResultBean.class);
        }

    }
}
