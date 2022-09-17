package com.mtime.bussiness.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;
import com.mtime.bussiness.common.adapter.PictureSelectAdapterNew;
import com.mtime.common.cache.FileCache;
import com.mtime.frame.BaseActivity;
import com.mtime.util.NativeImageLoader;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by guangshun on 15-6-20.
 * 选择图片页
 */
public class PictureSelectActivity extends BaseActivity implements
        View.OnClickListener {
    public static final String KEY_SELECT_IMAGE = "selectImage";

    public static void launch(Context context, List<ImageBean> imageBeans) {
        Intent launcher = new Intent(context, PictureSelectActivity.class);
        if (imageBeans != null) {
            launcher.putExtra(KEY_SELECT_IMAGE, (Serializable) imageBeans);
        }
        context.startActivity(launcher);
    }

    public static void launch(Activity context, String refer, List<ImageBean> imageBeans, int requestcode) {
        launch(context, refer, imageBeans, requestcode, PictureSelectAdapterNew.PIC_SELECT_MAX_NUM, false);
    }

    public static void launch(Activity context, String refer, List<ImageBean> imageBeans, int requestcode, int max, boolean includeGif) {
        Intent launcher = new Intent(context, PictureSelectActivity.class);
        if (imageBeans != null) {
            launcher.putExtra(KEY_SELECT_IMAGE, (Serializable) imageBeans);
        }
        launcher.putExtra("maxSelect", max);
        launcher.putExtra("includeGif", includeGif);
        dealRefer(context, refer, launcher);
        context.startActivityForResult(launcher, requestcode);
    }

    private static final int OPENCAMERA_RESULT = 1;
    private static final int MSG_WHAT = 10000;
    public static final String IMAGESEXTRA = "imagesExtra";
    public static final String INDEX_EXTRA = "index_Extra";
    public static final String IMAGES_PARCELEXTRA = "images_parcelExtra";
    public static final int ACTIVITY_RESULT_CODE_PICTURESELECT = 6236;

    private PictureSelectAdapterNew adapter;
    private TextView selectedNum;
    private List<ImageBean> imageBeans = new ArrayList<>();// 所有图片的信息
    private List<ImageBean> selectedImage = new ArrayList<>();// 已选择的图片，用于再次进入时勾选操作
    private String fileName;// 文件名
    private File saveFile;
    private int mMaxSelect = PictureSelectAdapterNew.PIC_SELECT_MAX_NUM;
    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  mtime.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/camera_pic/mtime.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.DATE_MODIFIED};    //图片被添加的时间，long型  1450518608

    private boolean mIncludeGif;

    public interface OnImageSelectedListener {
        void notifyChecked(ImageBean selectBean);
    }

    public interface OnImageSelectedCountListener {
        int getImageSelectedCount();
    }

    PictureSelectAdapterNew.OnImageSelectedCountListener onImageSelectedCountListener = new PictureSelectAdapterNew.OnImageSelectedCountListener() {

        @Override
        public int getImageSelectedCount() {
            return selectedImage.size();
        }

        @Override
        public void onCameraClick() {
            openCamera();
        }
    };

    PictureSelectAdapterNew.OnImageSelectedListener onImageSelectedListener = new PictureSelectAdapterNew.OnImageSelectedListener() {

        @Override
        public void notifyChecked(ImageBean selectBean) {
            if (selectedImage.contains(selectBean)) {
                selectedImage.remove(selectBean);
            } else {
                selectedImage.add(selectBean);
            }
            selectedNum.setText(String.valueOf(selectedImage.size()));
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.complete) {
            completeSelect();
        }
    }

    private Uri mSaveImgUri;

    @Override
    protected void onInitVariable() {
        setPageLabel("selectPicture");
        if (getIntent().getSerializableExtra(KEY_SELECT_IMAGE) != null) {
            selectedImage = (List<ImageBean>) getIntent().getSerializableExtra(KEY_SELECT_IMAGE);
        }
        mMaxSelect = getIntent().getIntExtra("maxSelect", PictureSelectAdapterNew.PIC_SELECT_MAX_NUM);
        mIncludeGif = getIntent().getBooleanExtra("includeGif", false);

        fileName = getFileName();
        File fileFolder = new File(FileCache.CACHE_CAMERA_PIC_PATH);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        saveFile = new File(fileFolder, fileName + ".jpg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSaveImgUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", saveFile);
        } else {
            mSaveImgUri = Uri.fromFile(saveFile);
        }
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_picture_selection);
        View navbar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navbar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE, getResources().getString(
                R.string.st_picture_selection_title), null);
        RecyclerView gridView = findViewById(R.id.grid);
        TextView complete = this.findViewById(R.id.complete);
        selectedNum = this.findViewById(R.id.num);
        selectedNum.setText(String.valueOf(selectedImage.size()));
        adapter = new PictureSelectAdapterNew(this, onImageSelectedCountListener, mMaxSelect);
        gridView.setLayoutManager(new GridLayoutManager(this, 3));
        gridView.setAdapter(adapter);
        gridView.addItemDecoration(new ItemDivider());
        adapter.setOnImageSelectedListener(onImageSelectedListener);
        complete.setOnClickListener(this);
        loadPic();
    }

    private static class ItemDivider extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int dp3 = MScreenUtils.dp2px(3);
            outRect.left = dp3;
            outRect.top = dp3;
            outRect.right = dp3;
            outRect.bottom = dp3;
        }
    }

    @Override
    protected void onInitEvent() {

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


    private void loadPic() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.what = MSG_WHAT;
                msg.obj = getImages();
                msg.sendToTarget();
            }
        }).start();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                imageBeans = (List<ImageBean>) msg.obj;
                if (imageBeans != null) {
                    adapter.setList(imageBeans);
                }
            }
        }
    };

    private List<ImageBean> getImages() {
        List<ImageBean> beans = new ArrayList<ImageBean>();
        beans.add(new ImageBean(""));

        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 只选择需要的字段，提升性能
        Cursor mCursor = getContentResolver().query(mImageUri, IMAGE_PROJECTION,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? " +
                        (mIncludeGif ? " or " + MediaStore.Images.Media.MIME_TYPE + "=? " : ""),
                mIncludeGif ? new String[]{"image/jpeg", "image/png", "image/gif"} : new String[]{"image/jpeg", "image/png"},
                IMAGE_PROJECTION[3] + " DESC");
        if (null == mCursor) {
            return beans;
        }

        while (mCursor.moveToNext()) {
            try {
                String display_name = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(IMAGE_PROJECTION[0]));

                String path = mCursor.getString(mCursor
                        .getColumnIndexOrThrow(IMAGE_PROJECTION[1]));

                long size = mCursor.getLong(mCursor
                        .getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                String parentName = new File(path).getParentFile().getName();
                beans.add(new ImageBean(parentName, size, display_name, path, false));

                for (ImageBean bean : selectedImage) {
                    if (path.equals(bean.path)) {
                        beans.get(beans.size() - 1).isChecked = true;
                        break;
                    }
                }
            } catch (Exception e) {

            }
        }

        mCursor.close();
        return beans;
    }

    private void completeSelect() {
        Intent intent = new Intent();
        intent.putExtra(IMAGESEXTRA, (Serializable) selectedImage);
        intent.putParcelableArrayListExtra(IMAGES_PARCELEXTRA, new ArrayList<>(selectedImage));
        setResult(ACTIVITY_RESULT_CODE_PICTURESELECT, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPENCAMERA_RESULT && resultCode == RESULT_OK) {
            if (null == saveFile) {
                return;
            }

//            /** 扫描图片,非常重要，会将图片加入media database，如果不加入，getImages()将获取不到此图 */
//            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//            Uri contentUri = Uri.fromFile(saveFile);
//            mediaScanIntent.setData(contentUri);
//            sendBroadcast(mediaScanIntent);

            ImageBean shootPohot = new ImageBean(null, saveFile.getTotalSpace(), null,
                    FileCache.CACHE_CAMERA_PIC_PATH + fileName + ".jpg", true);
//            onImageSelectedListener.notifyChecked(shootPohot);
            selectedImage.add(shootPohot);
            completeSelect();
        }
    }

    public void openCamera() {
//        ArrayList<String> permissions = new ArrayList<>();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//
//        } else {
//            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            permissions.add(Manifest.permission.CAMERA);
//        }

        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA).build(), new AcpListener() {
            @Override
            public void onGranted() {
                Acp.getInstance(getApplicationContext()).onDestroy();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mSaveImgUri);
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, OPENCAMERA_RESULT);
                } else {
                    MToastUtils.showShortToast("未检测到存储卡，拍照不可用!");
                }
            }

            @Override
            public void onDenied(List<String> permissions) {
                Acp.getInstance(getApplicationContext()).onDestroy();
                MToastUtils.showShortToast(permissions.toString() + "权限拒绝");
            }
        });
    }

    private String getFileName() {
        StringBuffer sb = new StringBuffer();
        Calendar calendar = Calendar.getInstance();
        long millis = calendar.getTimeInMillis();
        String[] dictionaries = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
                "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9"};
        sb.append("mtime");
        sb.append(millis);
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            sb.append(dictionaries[random.nextInt(dictionaries.length - 1)]);
        }
        return sb.toString();
    }

    @Override
    protected void clearMemory() {
        super.clearMemory();
        NativeImageLoader.getInstance().cleanBitmap();
    }

}
