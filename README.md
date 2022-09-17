时光APP新框架里部分注意事项：
1、isLogin()，顶层判断用户登录状态，一般用于UI交互显示时使用，使用场景：个人中心主页根据不同登录状态显示相应UI
1、afterLogin()，使用场景：点击想看、点击收藏等等
2、敏感信息，如：秘钥，第三方库的AppId等，均放在strings_id.xml(在core里)
3、RouterActivityPath中，User类代表旧的个人中心组件，Mine代表新的个人中心组件
4、data-class，专门存放API json序列化的数据类(也可以放一些通用的view数据类)，根据各自的业务，新建包名，存放对应的data  class。定义数据类时，注意apidoc里的number属性，一律定义为Long类型
5、不可混淆的类，需实现ProguardRule接口
6、不要随意在kt里写硬代码、魔鬼数字(没注释)
7、不要随意在kt里写字符串文案，特殊符号(如：￥，使用转义字符)
8、使用字符串、工具函数时，先在项目里搜一下，不要随意的定义字符串、工具类等
9、字符串定义，<string name="welcome_messages">Hello, %1$s! You have %2$d new messages.</string>
10、关于多个界面按钮、文案状态同步问题，新框架里需保持同步
11、需要用到切图资源，一律由UI给出，优先使用svg；png只适配xxhdpi(从切图文件夹里取3x@xx.png，放到xxhdpi里)使用png时需要关注图片尺寸、图片大小，超大切图需让UI重新切
12、不要随意定义shape文件，使用ShapeExt或者其扩展函数来设置样式
13、需要展示异常界面(空、网络异常)，使用MultiStateView包裹业务布局
14、需要用到WebView时，请依赖jssdk，不要依赖js-sdk(暂未实现Native与H5的通信交互)
15、对需求细节有疑问的，找对应的产品去沟通(需求以做减法为原则去讨论)，所有的需求以产品的结论为准。
16、ApiMTime编辑规范：ApiMTime文件只是APP与接口提供方约定的协议，承接API接口定义职责。要求代码清晰结构一致，以便于后续管理和维护（API数量较多），
    通过顶部密集定义的常量path以便直观的定位接口。
    基于以上宗旨提出以下两点要求：
        1，关于接口定义部分：
            （1）方法命名：要求既简单明了，又不能重复。以get、post等开头（便于识别请求类型），酌情添加模块名称，最后根据API业务命名。
            （2）参数列表：凡有参数都换行（代码结构一致）。
            （3）关于Number类型：经协商统一使用Long类型来接，避免后续问题。
        2，关于注释部分，格式如下：
            （1）接口名称：WIKI中定义的接口名称
            （2）请求方式：GET/POST （请求path：path要求是完整的，host之后的部分，即顶部声明的path常量）
            （3）参数列表：对照API接口WIKI的定义的参数列表直接copy过来，一个参数一行tab对齐，过长的按照刚压过代码标线的标准酌情换行并tab对齐。
                注释要求与WIKI保持一致，不需要过多的解释。
            /**
             * 社区交互-点赞api - 取消点赞
             * POST (/community/cancel_praise_up.api)
             *
             * objId	Number  点赞主体ID
             * objType	Number  点赞主体类型 JOURNAL(1, "日志"),POST(2, "帖子"),FILM_COMMENT(3, "影评"),ARTICLE(4, "文章"),
             *                  ALBUM(5, "相册"),TOPIC_LIST(6, "榜单"),JOURNAL_COMMENT(101, "日志评论"),POST_COMMENT(102, "帖子评论"),
             *                  FILM_COMMENT_COMMENT(103, "影评评论"),ARTICLE_COMMENT(104, "文章评论"),ALBUM_COMMENT(105, "相册评论"),
             *                  TOPIC_LIST_COMMENT(106, "榜单评论"),CINEMA_COMMENT(107, "影院评论"),JOURNAL_REPLY(201, "日志回复"),
             *                  POST_REPLY(202, "帖子回复"),FILM_COMMENT_REPLY(203, "影评回复"),ARTICLE_REPLY(204, "文章回复"),
             *                  ALBUM_REPLY(205, "相册回复"),TOPIC_LIST_REPLY(206, "榜单回复"),CINEMA_REPLY(207, "影院回复");
             */
            @POST(COMMUNITY_CANCEL_PRAISE_UP)
            @FormUrlEncoded
            suspend fun postCancelPraise(
                    @Field("objId") objId: Long
                    @Field("objType") objType: Long,
            ): ApiResponse<Any>
    注意：自行添加API时，新增API定义在对应的模块内，PATH常量声明的顺序即为API方法定义的顺序。
17、测试机sd卡，时光网/test下，创建test.json文件，
    {
        "testApiContent":[
            {
                "code":0,
                "msg":"com.kotlin.android.app.data.entity.home.HomeTabNavList",
                "data":{
                    "navList":[
                        {
                            "nameColor":"",
                            "name":"推荐111",
                            "type":1,
                            "order":1
                        }
                    ]
                }
            },
            {
                "code":0,
                "msg":"com.kotlin.android.app.data.entity.home.HomeTabNavList111",
                "data":{
                    "navList":[
                        {
                            "nameColor":"",
                            "name":"推荐222",
                            "type":2,
                            "order":2
                        }
                    ]
                }
            }
        ]
    }
    集合里的每一个对象对应一个api接口返回数据，msg需要填上dataclass全类名
    配置完成，即可实现本地json数据调试，CoreApp里有开关-testFlag

18、如需使用第三方so库，务必要配置arm64-v8a

19、KAE插件已弃用，如需在代码里获取xml元素，使用ViewBinding、DataBinding获取

关于代码提交规范以及分支管理的流程：
开发人员使用dev_xxx(xxx代表当前开发的版本号)用来开发功能、改bug
测试人员使用rel_xxx(xxx代表当前开发的版本号)来测试，提bug，回归验证
测试人员使用master来打上线渠道包
1、需求开发阶段，开发人员只需要关注dev_xxx分支即可，提测前务必自测各自的开发功能(release编译)，保证主流程无障碍；
2、新拉rel_xxx分支提测后，dev_xxx分支废弃，所修改的bug内容同步到rel_xxx上，再去禅道里更改bug状态。阻塞主流程的bug务必当天解决，如果实在无法当天解决，需在禅道里备注原因以及解决的时间点；
3、测试验收完成后，首先由测试发出验收完成的邮件，然后再由产品、业务验收(此流程也许会跟测试流程有一定的重叠，但是对开发人员没有影响)；
4、待产品、业务验收完毕后，由产品发出合并master分支的邮件，待合并完master，再次发邮件知会测试打上线渠道包；
5、app上线后，需关注bugly、产品反馈的问题，针对不同问题，标定bug级别。

关于组件化混淆说明：
    1、对于不同的组件进行混淆，只需要在当前组件的proguard-rules.pro文件中添加混淆规则，不用再在app中添加对应proguard-rules.pro路径;
    2、library中应使用consumerProguardFiles 方法，若使用app中写法(proguardFiles getDefaultProguardFile)进行混淆，会不生效；
    3、在library的build.gradle中混淆写法
        buildTypes {
                release {
                    minifyEnabled true     //混淆开关
                    zipAlignEnabled true  //压缩优化

                    consumerProguardFiles 'proguard-rules.pro'
                }
            }
    4、在app的build.gradle中写法：
        buildTypes {
                release {
                    signingConfig signingConfigs.release
                    minifyEnabled true  //混淆开关
                    zipAlignEnabled true  //压缩优化
                    shrinkResources true  //无用资源不打入包中只能在app中添加，在library中添加编译会报错
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro', 'obfuscate-helper-rules.pro'
                    ndk {
                        abiFilters "armeabi-v7a", "armeabi"
                    }
                }

            }

