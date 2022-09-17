retrofit封装库

# 初始化配置
## 请求前参数统一处理，追加渠道名称用户手机号之类
   ParamsInterceptor mParamsInterceptor = new ParamsInterceptor() {
            @Override
            public Map checkParams(Map params) {
                //追加统一参数
                params.put("app_type", "android_price");
                return params;
            }
        };
```
## 请求前headers统一处理
    HeadersInterceptor mHeadersInterceptor = new HeadersInterceptor() {
               @Override
               public Map checkHeaders(Map headers) {
                   //追加统一header，例：数据缓存一天
                   headers.put("Cache-Time", "3600*24");
                   return headers;
               }
           };
```
## 应用入口进行初始化
   HttpUtil.SingletonBuilder builder=new HttpUtil.SingletonBuilder(getApplicationContext())
                .baseUrl("baseurl")//URL请求前缀地址。如果不想传可以不传，每次接口请求时传入完整url baseurl即不生效
//                .versionApi("")//API版本，不传不可以追加接口版本号
//                .client()//OkHttpClient,不传默认OkHttp3
                .paramsInterceptor(mParamsInterceptor)//不传不进行参数统一处理
//               .headersInterceptor(mHeadersInterceptor)//不传不进行headers统一处理
                .timeout();//超时时间，不设置默认5s
                .cookieSaveingMode();//cookie保存方式。不传默认Sharepreference。此时cookie路径不生效。CookieSaveingMode.SDCARD|SHAREDPREFERENCES
                .cachePath();//接口数据缓存路径，不传默认系统缓存路径
                .cookiePath();//cookies保存路径，不传默认系统缓存路径。cookie保存文件名固定为/cookie.txt。传路径时末尾不要带 "/"
                .build();
   调动 CookieJarManager manager=builder.getInstance();  或得到cookie全局管理manager
   manager内部对外提供四个方法getCookie  setCookie  clear claerSession
```

# 发起请求
## get请求
 new HttpUtil.Builder("url")
                .Params(map)
                .Params("key","value")
                .isUpdateCache(false)//当次请求是否更新缓存，实现方式是在请求之前删除当前请求已经保存的cache
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .Success(str->{
                    //do something surccess
                })
                .Fail(v->{
                    //deal something error
                })
                .get();
```
## post请求
new Apiclient.Builder("url")
                .Params(map)
                .Params("key","value")
                .Version()//需要追加API版本号调用
                .Tag(this)//需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(String model) {

                    }
                })
                .Fail(new Fail() {
                    @Override
                    public void Fail(Object... values) {

                    }
                })
                .post();
```
## 下载文件
流式下载，直接写入文件，不存到内存，避免oom
 new Apiclient.Builder("url")
                .SavePath("path")
                .Progress(p -> {
                    progress.setText(100 * p + "%");
                })
                .Success(s -> {
                    //返回path
                })
                .Fail(t -> {
                })
                .download();
```
## 上传文件
new Apiclient.Builder("url")
                .requestBodys("key","value")//单个文件不带参数
                .requestBodys(Map<String, Object> requestBodys)//多文件可带参数,参数key指定， 文件需要传入File(切记)，文件的key没要求，请求时会在key之后拼接文件名
                .Tag(this)//需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(String model) {

                    }
                })
                .Fail(new Fail() {
                    @Override
                    public void Fail(Object... values) {

                    }
                })
                .upload();
```

 ## 取消请求
 调用时添加tag的请求
 要取消下载请求也需要给请求添加对应的独立tag
   @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiClient.cancel(this);
    }
```

## 注意
   所有请求返回的都是服务器直接返回的String，因为格式不一定唯一，所以需要自己再去解析String


 ## 图片框架
 #初始化:
 1.程序入口处调用ImageLoadConfig.setCacheDiskPath("/sdcard/")设置图片sd卡的缓存路径，注意此处必须设置
 2.在AndroidManifest.xml文件中的meta-data标签中增加如下代码，copy过去就可以
   <meta-data
           android:name="com.mtime.mtmovie.network.imageloader.DefaultGlieModule"
           android:value="GlideModule" />

 #使用
 1. 删除已加载过url的图片
 ImageLoader.clearTarget(this, url);

 2. 加载网络图片
 ImageLoader.loadImage(mTargetView, url, ImageLoader.defConfig, mListener);

 3.加载网络的gif
 ImageLoadConfig config2 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).setAsGif(true).
                         setSkipMemoryCache(true).
                         build();
 ImageLoader.loadGif(mTargetView, gif, config2, mListener);

 4. 加载本地图片
 ImageLoadConfig config3 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                         setSkipMemoryCache(true).
                         setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE).setSize(size).
                         build();
 ImageLoader.loadFile(mTargetView, jpgFile, config3, mListener);

 5. 加载本地资源
 ImageLoadConfig config4 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                         setSkipMemoryCache(true).
                         setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE)
                         .build();
 ImageLoader.loadResId(mTargetView, R.drawable.dog, config4, mListener);

 6. 加载资源动画
 ImageLoadConfig config5 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                         setAnimResId(R.anim.left_in).
                         setSkipMemoryCache(true).
                         setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE).
                         setAsGif(true).build();
 ImageLoader.loadResId(mTargetView, R.drawable.smail, config5, mListener);

 7. 加载属性动画
 ViewPropertyAnimation.Animator animationObject = new ViewPropertyAnimation.Animator() {
                     @Override
                     public void animate(View view) {
                         ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationX", -500f, 0f);
                         ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                         ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f);
                         ObjectAnimator moveTop = ObjectAnimator.ofFloat(view, "translationY", 0f, -2000, 0f);
                         AnimatorSet animSet = new AnimatorSet();
                         animSet.play(rotate).with(fadeInOut).after(moveIn).before(moveTop);
                         animSet.setDuration(5000);
                         animSet.start();
                     }
                 };
 ImageLoadConfig config6 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                         setAnimator(animationObject).
                         setSkipMemoryCache(true).
                         setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE).
                         setAsGif(true).
                         build();
 ImageLoader.loadResId(mTargetView, R.drawable.smail, config6, mListener);

 8. 先加载缩略图片,再显示原图
 ImageLoadConfig config7 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                         setSkipMemoryCache(true).
                         setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE).
                         setThumbnailUrl(thumbnailUrl)
                         .build();
 ImageLoader.loadImage(mTargetView, url, config7, mListener);

 9. 加载按比例缩放的缩略图
 ImageLoadConfig config8 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                         setSkipMemoryCache(true).
                         setDiskCacheStrategy(ImageLoadConfig.DiskCache.NONE).
                         setThumbnail(0.7f)
                         .build();
 ImageLoader.loadImage(mTargetView, url, config8, mListener);

 10. 加载裁剪图片
 ImageLoadConfig config9 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                          setCrop(true).
                          setCropType(ImageLoadConfig.CropType.TOP).
                          //setCropType(ImageLoadConfig.CropType.CENTER)
                          //setCropType(ImageLoadConfig.CropType.BOTTOM)
                          setSize(new ImageLoadConfig.OverrideSize(200,200))
                          .build();
 ImageLoader.loadImage(mTargetView, url, config9, mListener);

 11. 加载圆型图片
 ImageLoadConfig config10 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                            setCropCircle(true).
                           .build();
 ImageLoader.loadImage(mTargetView, url, config10, mListener);

 12. 加载圆角图片
 ImageLoadConfig config11 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                             setRoundedCorners(true).
                             setRadius(50).
                             setMargin(50).
                            .build();
 ImageLoader.loadImage(mTargetView, url, config11, mListener);

 13. 加载灰色图片
 ImageLoadConfig config12 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                             setGrayscale(true).
                            .build();
 ImageLoader.loadImage(mTargetView, url, config12, mListener);

 14. 加载高斯模糊图片
 ImageLoadConfig config13 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                              setBlur(true).
                              setRadius(8).  1<radius<=25
                              setSampling(4).
                             .build();
 ImageLoader.loadImage(mTargetView, url, config13, mListener);

 15. 加载旋转图片
 ImageLoadConfig config14 = ImageLoadConfig.parseBuilder(ImageLoader.defConfig).
                               setRotate(true).
                               setRotateDegree(90).
                              .build();
 ImageLoader.loadImage(mTargetView, url, config14, mListener);