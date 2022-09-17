package com.mtime.bussiness.ticket.cinema.activity;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-25
 * <p>
 * 合作影院选座页
 */
//public class CooperationSeatSelectActivity extends AbsWebViewActivity implements JSHandleDialTelListener {
//
//    public static void launch(Context context, String url) {
//        Intent intent = new Intent(context, CooperationSeatSelectActivity.class);
//        // 父类里默认显示 titlebar，如果不显示，加入这一行即可
////        intent.putExtra(KEY_OF_SHOW_TITLE, false);
//        // load url 也在 父类处理，如果需要拦截 url ，则重写 setData() 或者 重写 onInterceptUrlLoading 并返回 ture
//        intent.putExtra(KEY_OF_URL, url);
//        if (!(context instanceof Activity)) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onInit(Bundle savedInstanceState) {
//        super.onInit(savedInstanceState);
//        mTitleBar.setCloseShow(true);
//        mTitleBar.setBackShow(false);
//    }
//
//    @Override
//    public boolean shouldGoBack() {
//        return false;
//    }
//
//    @Override
//    public void onConfigBrowser(Browser browser) {
//        JSCenter jsCenter = browser.enableJsSdk(this, JSNAME);
//        jsCenter.setJSHandleDialTelListener(this);
//    }
//
//    @Override
//    public void handleDialTel(HandleTelBean bean) {
//        //调用系统打电话页面
//        if(null != bean && null != bean.getData() && !TextUtils.isEmpty(bean.getData().phoneNumber)) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Acp.getInstance(CooperationSeatSelectActivity.this).request(new AcpOptions.Builder().setPermissions(
//                            Manifest.permission.CALL_PHONE).build(), new AcpListener() {
//                        @Override
//                        public void onGranted() {
//                            // 直接拨打电话
//                            final Intent intent = new Intent();
//                            intent.setData(Uri.parse("tel:"+bean.getData().phoneNumber));
//                            intent.setAction(Intent.ACTION_CALL);
//                            startActivity(intent);
//                        }
//
//                        @Override
//                        public void onDenied(List<String> permissions) {
//                            MToastUtils.showShortToast(permissions.toString() + "权限拒绝");
//                        }
//                    });
//                }
//            });
//        }
//    }
//}
