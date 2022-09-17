//package com.mtime.bussiness.ticket.movie.comment.bean;
//
//import androidx.core.util.ObjectsCompat;
//import android.text.TextUtils;
//
//import com.mtime.base.imageload.ImageDownloadCallback;
//import com.mtime.base.imageload.ImageHelper;
//import com.mtime.base.imageload.ImageProxyUrl;
//import com.mtime.frame.App;
//import com.mtime.util.image.FileUtils;
//import com.mtime.util.image.Size;
//
//import java.io.File;
//
///**
// * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
// * <p>
// * On 2019-05-21
// */
//public class CommentImageBean extends BaseCommentBean {
//
//    public String pathOrUrl; // 本地路径或者服务器url
//
//    public String desc; // 图片描述
//
//    public File localFile;
//
//    public int width;
//
//    public int height;
//
//    private DownloadCallback mCallback;
//
//    public CommentImageBean(String pathOrUrl) {
//        this(pathOrUrl, null);
//    }
//
//    public CommentImageBean(String url, String desc) {
//        super(TYPE_IMAGE);
//        if (TextUtils.isEmpty(url)) {
//            throw new NullPointerException("url or path is null");
//        }
//        this.pathOrUrl = url;
//        this.desc = desc;
//        if (url.startsWith("/")) {
//            localFile = new File(url);
//            Size size = FileUtils.getImageSize(url);
//            width = size.width;
//            height = size.height;
//        }
//    }
//
//    public boolean isLocalFile() {
//        return pathOrUrl.startsWith("/");
//    }
//
//    public interface DownloadCallback {
//        void onDownloadComplete();
//    }
//
//    public void setDownloadCallback(DownloadCallback callback) {
//        mCallback = callback;
//    }
//
//    public void downloadImage() {
//        ImageHelper.with(App.getInstance(),
//                ImageProxyUrl.SizeType.ORIGINAL_SIZE,
//                ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                .load(pathOrUrl)
//                .callback(new ImageDownloadCallback() {
//                    @Override
//                    public void onDownloadCompleted(String url, File file) {
//                        pathOrUrl = file.getAbsolutePath();
//                        localFile = new File(pathOrUrl);
//
//                        Size size = FileUtils.getImageSize(pathOrUrl);
//                        width = size.width;
//                        height = size.height;
//
//                        if (mCallback != null) {
//                            mCallback.onDownloadComplete();
//                        }
//                    }
//
//                    @Override
//                    public void onDownLoadFailed(String url) {
//                    }
//                }).download();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        CommentImageBean that = (CommentImageBean) o;
//        return width == that.width &&
//                height == that.height &&
//                ObjectsCompat.equals(pathOrUrl, that.pathOrUrl) &&
//                mType == that.mType;
//    }
//
//    @Override
//    public int hashCode() {
//        return ObjectsCompat.hash(pathOrUrl, width, height, mType);
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//
//    @Override
//    public BaseCommentBean copy() {
//        CommentImageBean imageBean = new CommentImageBean(pathOrUrl, desc);
//        imageBean.width = width;
//        imageBean.height = height;
//        return imageBean;
//    }
//}
