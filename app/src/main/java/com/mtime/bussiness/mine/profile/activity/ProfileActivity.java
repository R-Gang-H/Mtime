package com.mtime.bussiness.mine.profile.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.image.component.ChooseAvatarExtKt;
import com.kotlin.android.image.component.PhotoCropExtKt;
import com.kotlin.android.mtime.ktx.FileEnv;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.app.data.entity.user.UserLocation;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.profile.bean.UpdateMemberInfoBean;
import com.mtime.bussiness.mine.profile.widget.BirthDlg;
import com.mtime.common.utils.DateUtil;
import com.mtime.common.utils.TextUtil;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;
import com.mtime.util.UploadPicture;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import androidx.collection.ArrayMap;
import androidx.fragment.app.DialogFragment;

import kotlin.Unit;

/**
 * 个人资料页
 * 
 */
@SuppressLint("NewApi")
@Route(path = RouterActivityPath.AppUser.PAGE_PROFILE)
public class ProfileActivity extends BaseActivity implements OnClickListener{

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String UPLOAD_USERAVATAR = "UserAvatar";
    private static final String CROSS_LINE = "-";
    private static final String ZERO = "0";
    // 1.生日，2居住地 3,用户签名
    private static final String TYPE_BIRTHDAY = "1";

    private ImageView           img_photo;
    private String              CAMEAR_TEMP_NAME; // 头像压缩后存放的地址
    private Bitmap              head_photo;
//    private RequestCallback getUploadImgUrl;
//    private String              headURL;
    // 签名
    private TextView mSignTv;
    // 生日
    private TextView tvBirthday;
    private BirthDlg birthDlg;
    private int birthYear = 1990;
    private int birthMonthOfYear = 0;
    private int birthDayOfMonth = 1;
    // 居住地
    private UserLocation locationBean;

//    private Handler             headerImageHandler;
    private MineApi mMineApi;
    
    @Override
    protected void onInitVariable() {
//        CAMEAR_TEMP_NAME = FileCache.CACHE_PATH + "temp.jpg";
        CAMEAR_TEMP_NAME = FileEnv.INSTANCE.getHeadDownPic() + "/temp.jpg";
        setPageLabel("profile");
        mMineApi = new MineApi();
    }
    
    @SuppressLint("NewApi")
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_profile);
        View navbar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navbar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, getResources().getString(
                R.string.str_profile), null);

        img_photo = findViewById(R.id.img_photo);
        tvBirthday = findViewById(R.id.birthday);
        mSignTv = findViewById(R.id.sign_item_tv);
    }
    
    @Override
    protected void onInitEvent() {
//        getUploadImgUrl = new RequestCallback() {
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                ResultBean bean = (ResultBean) o;
//                headURL = bean.getHeadPic();
//                if (TextUtils.isEmpty(headURL) || null == UserManager.Companion.getInstance().getUser()) {
//                    return;
//                }
//                UserManager.Companion.getInstance().setUserAvatar(headURL);
//                volleyImageLoader.displayImage(headURL, img_photo, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, Utils.dip2px(ProfileActivity.this, 45), Utils.dip2px(ProfileActivity.this, 45), ImageURLManager.FIX_WIDTH_AND_HEIGHT, null);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast(getString(R.string.text_upload_photo_failed) + ":" + e.getLocalizedMessage());
//            }
//        };

        findViewById(R.id.head_item).setOnClickListener(this);
        findViewById(R.id.nickname_item).setOnClickListener(this);
        findViewById(R.id.sign_item).setOnClickListener(this);
        findViewById(R.id.sex_item).setOnClickListener(this);
        findViewById(R.id.bind_phone_item).setOnClickListener(this);
        findViewById(R.id.ll_pwd).setOnClickListener(this);
        findViewById(R.id.birthday_item).setOnClickListener(this);
        findViewById(R.id.location_item).setOnClickListener(this);
//        findViewById(R.id.change_address).setOnClickListener(this);

//        headerImageHandler = new Handler() {
//            public void handleMessage(final Message msg) {
//
//                switch (msg.what) {
//                    case 1:
//                        // get picture url
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                String headImagePath = null;
//                                try {
//                                    headImagePath = uploadImagess(CAMEAR_TEMP_NAME, UPLOAD_USERAVATAR, url);
//                                }
//                                catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                Message msg = headerImageHandler.obtainMessage();
//                                msg.what = 2;
//                                msg.obj = headImagePath;
//                                headerImageHandler.sendMessage(msg);
//                            }
//                        }.start();
//                        break;
//                    case 2:
//                        // upload header
//                        String headerImagePath = (String) msg.obj;
//                        if (TextUtils.isEmpty(headerImagePath)) {
//                            MToastUtils.showShortToast(ProfileActivity.this.getString(R.string.text_upload_photo_failed));
//                            UIUtil.dismissLoadingDialog();
//                            return;
//                        }
//
//                        UpLoadImageBean b = (UpLoadImageBean) Utils.handle(headerImagePath, UpLoadImageBean.class);
//                        PhotoInfo photoInfo = b.getData();
//                        if (photoInfo!=null && !TextUtils.isEmpty(photoInfo.getFileID())) {
//                            Map<String, String> parameterList = new ArrayMap<String, String>(1);
//                            parameterList.put("fileName", photoInfo.getFileID());
//                            HttpUtil.post(ConstantUrl.GET_UPLOAD_HEAD_URL, parameterList, ResultBean.class, getUploadImgUrl);
//                            return;
//                        }
//
//                        MToastUtils.showShortToast(getString(R.string.text_upload_photo_failed) );
//                        UIUtil.dismissLoadingDialog();
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//        };
    }
    
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        DialogFragment dialog = ChooseAvatarExtKt.getChooseAvatarFragment(this);
        if (dialog != null) {
            dialog.onActivityResult(arg0, arg1, arg2);
        }
//        if (arg0 == 0 || arg2 == null) {
//            return;
//        }
//
//        switch (arg0) {
//            case 1: {
//
//                // 拍照
//                Bundle extras = arg2.getExtras();
//                if (null != extras) {
//                    // 有些手机是直接返回
//                    head_photo = (Bitmap) extras.get("data");
//                    settingUploadImage();
//                }
//                else {
//                    // 处理没有返回的情况
//                    Uri uri = arg2.getData();
//                    if (uri != null) {
//                        ContentResolver resolver = getContentResolver();
//                        try {
//                            byte[] mContent = getFileByte(resolver.openInputStream(Uri.parse(uri.toString())));
//                            head_photo = getPicFromBytes(mContent, null);
//                            settingUploadImage();
//                        }
//                        catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//                break;
//            case 2: {
//
//                // 读取相册缩放图片
//                Uri uri = arg2.getData();
//                if (uri != null) {
//                    ContentResolver resolver = getContentResolver();
//                    try {
//                        head_photo = BitmapFactory.decodeStream(resolver.openInputStream(uri));
//                        settingUploadImage();
//                    }
//                    catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//                break;
//            default:
//                break;
//        }
    }
    
    private void settingUploadImage() {
        try {
//            Utils.openFolder(FileCache.CACHE_PATH);

            File file = new File(FileEnv.INSTANCE.getHeadDownPic()+File.separator);
            if (!file.exists()){
                file.mkdirs();
            }
            File tempFile = new File(CAMEAR_TEMP_NAME);
            if (!tempFile.exists()){
                tempFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(CAMEAR_TEMP_NAME);
            BufferedOutputStream bos = new BufferedOutputStream(out);
            head_photo.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            // iconView.setImageBitmap(photo);
            bos.flush();
            bos.close();
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

//        Message msg = headerImageHandler.obtainMessage();
//        msg.what = 1;
//        url = VariateExt.INSTANCE.getImgUploadUrl();
//        headerImageHandler.sendMessage(msg);
        
//        UIUtil.showLoadingDialog(this);
//        HttpUtil.get(ConstantUrl.UPLOAD_IAMGE_URL, UploadImageURLBean.class, new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                final UploadImageURLBean bean = (UploadImageURLBean) o;
//                if (null == bean || TextUtils.isEmpty(bean.getUploadImageUrl())) {
//                    MToastUtils.showShortToast(getString(R.string.text_upload_photo_failed) + ":上传地址异常");
//                    UIUtil.dismissLoadingDialog();
//                    return;
//                }
//
//                Message msg = headerImageHandler.obtainMessage();
//                msg.what = 1;
//                url = bean.getUploadImageUrl();
//
//                headerImageHandler.sendMessage(msg);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                MToastUtils.showShortToast(ProfileActivity.this.getString(R.string.text_upload_photo_failed) + ":" + e.getLocalizedMessage());
//                UIUtil.dismissLoadingDialog();
//            }
//        });
        
    }
    
    /**
     * 上传图片
     * 
     * @param file
     * @param type
     *            图片类型 ImageFileType 参数为UserAvatar
     * @return
     * @throws Exception
     */
    private String uploadImagess(String file, String type, String uploadURL) throws Exception {
        Map<String, String> params = new ArrayMap<String, String>(3);
        params.put("imageType", String.valueOf(1));
        UploadPicture.FormFileBean fie = new UploadPicture.FormFileBean("temp", TextUtil.getFileContent(file), "file", "multipart/form-data");
        String str = uploadImage(uploadURL, params, fie);
        return str;
    }
    
    /**
     * 上传图片
     * 
     * @param actionUrl
     *            上传路径
     * @param params
     *            请求参数 key为参数名,value为参数值
     * @param file
     *            上传文件
     */
    public static String uploadImage(String actionUrl, Map<String, String> params, UploadPicture.FormFileBean file) throws Exception {
        
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
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                line = null;
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        line = sb.toString();
        sb = null;
        return line;
    }
    
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            }
            else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return null;
    }
    
    public static byte[] getFileByte(InputStream is) {
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] data = outStream.toByteArray();
            outStream.close();
            is.close();
            return data;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void onLoadData() {
        if (null == UserManager.Companion.getInstance().getUser()) {
            return;
        }
        TextView nickname = findViewById(R.id.nickname);
        TextView sex = findViewById(R.id.sex);
        TextView bindphone = findViewById(R.id.bind_phone);
        TextView tvLocation = findViewById(R.id.location);

        nickname.setText(UserManager.Companion.getInstance().getNickname());
        // 个性签名
        mSignTv.setText(UserManager.Companion.getInstance().getSign());
        // 手机号
        String mobile = UserManager.Companion.getInstance().getBindMobile();
        if (mobile != null && mobile.startsWith("1")) {
            bindphone.setText(mobile.substring(0, 3) + "****" + mobile.substring(7));
        }
        else {
            bindphone.setText(getString(R.string.str_unbind));
        }
        int userSex = UserManager.Companion.getInstance().getUserSex();
        if (userSex == App.getInstance().male) {
            sex.setText(getString(R.string.s_male));
        }
        else if (userSex == App.getInstance().female){
            sex.setText(getString(R.string.s_female));
        } else {
            sex.setText(getString(R.string.str_sex_protected));
        }
        // 生日
        String birthday = UserManager.Companion.getInstance().getBirthday();
        if(TextUtils.isEmpty(birthday)) {
            tvBirthday.setText(getResources().getString(R.string.profile_default));
        } else {
            tvBirthday.setText(birthday);
            Date d = DateUtil.getDateFromStr(birthday);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            birthYear = c.get(Calendar.YEAR);
            birthMonthOfYear = c.get(Calendar.MONTH);
            birthDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        }
        // 居住地
        locationBean = UserManager.Companion.getInstance().getLocation();
        if(null != locationBean && locationBean.getLocationId() > 0 && !TextUtils.isEmpty(locationBean.getLocationName())) {
            tvLocation.setText(locationBean.getLocationName());
        } else {
            tvLocation.setText(getResources().getString(R.string.profile_default));
        }
        // 头像
        volleyImageLoader.displayImage(UserManager.Companion.getInstance().getUserAvatar(), img_photo, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90,
                Utils.dip2px(ProfileActivity.this, 45), Utils.dip2px(ProfileActivity.this, 45), ImageURLManager.FIX_WIDTH_AND_HEIGHT, null);
    }
    
    @Override
    protected void onRequestData() {
        
    }
    
    @Override
    protected void onUnloadData() {
        
    }
    
//    private String url;

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.nickname_item:
                ProfileActivity.this.startActivity(ChangeNickNameActivity.class, intent);
                break;
            case R.id.sign_item:
                String sign = UserManager.Companion.getInstance().getSign();
                JumpUtil.startEditInfoActivity(this, assemble().toString(),
                        EditInfoActivity.EDIT_INFO_TYPE_SIGN, sign, 0);
                break;
            case R.id.sex_item:
                ProfileActivity.this.startActivity(ChangeSexActivity.class, intent);
                break;
            case R.id.bind_phone_item:
                // 2019新版：第三方登录必须绑定手机才能成功登录；账号密码登录可以不绑定手机号直接登录
                String bindMobile = UserManager.Companion.getInstance().getBindMobile();
                if (!TextUtils.isEmpty(bindMobile) && bindMobile.startsWith("1")) {
                    // 已绑定手机号：进入更换手机页（"title手机绑定"更换手机号->"title身份验证"手机号、手机验证码->"title手机绑定"手机号、验证码
                    ProfileActivity.this.startActivity(ChangeMobileBindingActivity.class, intent);
                }
                else if(UserManager.Companion.getInstance().getUser() != null) {
                    // 账号密码登录，未绑定手机号：进入非登录手机绑定页（2019版第三方登录不存在这种情况）
                    JumpUtil.startBindPhoneActivity(this, assemble().toString(), UserManager.Companion.getInstance().getHasPassword());
                }
                break;
            case R.id.ll_pwd: // 修改密码
                JumpUtil.startChangePasswordActivity(this, assemble().toString());
                break;
            case R.id.head_item:
                ChooseAvatarExtKt.showChooseAvatarDialog(this, (photo) -> {
                    PhotoCropExtKt.showPhotoCropDialog(this, photo, CommConstant.IMAGE_UPLOAD_USER_UPLOAD,PhotoCropExtKt.CROP_TYPE_1_1, (avatarUrl) -> {
                        showAvatarView(avatarUrl);
                        return Unit.INSTANCE;
                    }, null);
                    return Unit.INSTANCE;
                });
//                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
//                builder.setTitle(R.string.select_head);
//                builder.setItems(R.array.upload_head, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // String[] items
//                        // =getResources().getStringArray(R.array.upload_head);
//                        if (which == 0) {
//                            try {
//                                // 危险权限：运行时请求
//                                Acp.getInstance(ProfileActivity.this).request(new AcpOptions.Builder()
//                                                .setPermissions(
//                                                        Manifest.permission.CAMERA
//                                                ).build(),
//                                        new AcpListener() {
//                                            @Override
//                                            public void onGranted() {
//                                                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                                startActivityForResult(i, 1);
//                                            }
//
//                                            @Override
//                                            public void onDenied(List<String> permissions) {
//                                                MToastUtils.showShortToast("相机权限拒绝");
//                                            }
//                                        });
//                            } catch (Exception e) {
//
//                            }
//                        }
//                        else if (which == 1) {
//                            try {
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
//                                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
//                                startActivityForResult(intent, 2);
//                            } catch (Exception e) {
//
//                            }
//                        }
//                    }
//                });
//
//                builder.create().show();
                break;
            case R.id.birthday_item: // 生日
                // 显示日期选择组件
                showDatePicker();
                break;
            case R.id.location_item: // 居住地
                String level = (null == locationBean || TextUtils.isEmpty(locationBean.getLevelRelation())) ? "" : locationBean.getLevelRelation();
                intent.putExtra(App.getInstance().KEY_LOCATION_LEVEL_RELATION, level);
                startActivity(LocationSelectActivity.class, intent);
                break;
//            case R.id.change_address:
//                JumpUtil.startNativeAddressListActivity(this,"","",0,0,"",true,0);
//                break;
            default:
                break;
        }
    }

    private void showAvatarView(String avatarUrl) {
        if (TextUtils.isEmpty(avatarUrl) || null == UserManager.Companion.getInstance().getUser()) {
            return;
        }
        UserManager.Companion.getInstance().setUserAvatar(avatarUrl);
        volleyImageLoader.displayImage(avatarUrl, img_photo, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, Utils.dip2px(ProfileActivity.this, 45), Utils.dip2px(ProfileActivity.this, 45), ImageURLManager.FIX_WIDTH_AND_HEIGHT, null);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(head_photo != null && !head_photo.isRecycled()){
            head_photo.recycle();
            head_photo = null;
        }
        if(null != birthDlg) {
            birthDlg = null;
        }
        if(mMineApi != null) {
            mMineApi = null;
        }
    }

    // 显示日期选择组件
    private void showDatePicker() {
        if(null == birthDlg) {
            birthDlg = new BirthDlg(this, birthYear, birthMonthOfYear, birthDayOfMonth, new OnBirthDlgClickListener() {
                @Override
                public void okClick(int year, int monthOfYear, int dayOfMonth) {
                    // 更新生日
                    updateBirthday(year, monthOfYear, dayOfMonth);
                }
            });
        }
        birthDlg.show();
    }

    // 更新生日
    private void updateBirthday(final int year, final int monthOfYear, final int dayOfMonth) {
        if(!TextUtils.isEmpty(UserManager.Companion.getInstance().getBirthday())
                && year == birthYear && monthOfYear == birthMonthOfYear && dayOfMonth == birthDayOfMonth) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append(CROSS_LINE);
        if((monthOfYear + 1) < 10) {
            builder.append(ZERO);
        }
        builder.append(monthOfYear + 1);
        builder.append(CROSS_LINE);
        if(dayOfMonth < 10) {
            builder.append(ZERO);
        }
        builder.append(dayOfMonth);
        final String newBirth = builder.toString();

        UIUtil.showLoadingDialog(ProfileActivity.this);
        mMineApi.updateMemberInfo(newBirth, "", "", TYPE_BIRTHDAY, new NetworkManager.NetworkListener<UpdateMemberInfoBean>() {
            @Override
            public void onSuccess(UpdateMemberInfoBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (result.getBizCode() == 0) {
                    // 更新成功后设置
                    tvBirthday.setText(newBirth);
                    UserManager.Companion.getInstance().setBirthday(newBirth);
                    birthYear = year;
                    birthMonthOfYear = monthOfYear;
                    birthDayOfMonth = dayOfMonth;
                    MToastUtils.showShortToast("生日修改成功");
                } else {
                    MToastUtils.showShortToast(result.getBizMsg());
                }
            }

            @Override
            public void onFailure(NetworkException<UpdateMemberInfoBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("修改生日失败:" + showMsg);
            }
        });
    }

    // 生日弹框点击完成Listener
    public interface OnBirthDlgClickListener {
        void okClick(int year, int monthOfYear, int dayOfMonth);
    }

    /**
     * 自己定义refer
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, ProfileActivity.class);
        dealRefer(context,refer, launcher);
        context.startActivity(launcher);
    }

}
