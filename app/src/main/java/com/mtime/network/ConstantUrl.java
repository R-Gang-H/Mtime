package com.mtime.network;

import com.kotlin.android.api.config.Env;

public class ConstantUrl {
    //host
    public static final String HOST;

    static {
        HOST = Env.Companion.getInstance().getHostMTime();
    }
    // app下载的H5页面
    public static final String APP_DOWNLOAD_H5_URL = "https://m.mtime.cn/download";
    // 购票帮助中心
    public static final String FEATURE_TICKET_HELP_URL = "https://general.mtime.cn/help/mtime/h5/ticket/index.html";
    // 诚信url
    public static final String FEATURE_LICENCE_URL = "https://general.mtime.cn/mobile/2018/licence/licence.html";
    // 网络视听许可证
    public static final String FEATURE_AUDIO_VISUAL_URL = "https://general.mtime.cn/mobile/2019/audioVisual/audio-visual.html";
    // 广播电视节目制作经营许可证
    public static final String FEATURE_PROGRAM_URL = "https://general.mtime.cn/mobile/2019/program/program.html";

    // 示例:get 不带参数 不带缓存时间
    public static final String ACCOUNT_DETAIL = HOST + "/user/account/detail.api";
    // 示例:get 带参数 带缓存时间
    // 演员列表
    public static final String GET_FULL_CREDITS = HOST + "/library/movie/movieCreditsWithTypes.api";
    // 电影发行商列表
    public static final String GET_FULL_PRODUCERS = HOST + "/library/movie/companyList.api";
    // 电影详情关联更多资料详情页
    public static final String GET_MOVIE_MORE_INFO = HOST + "/library/movie/moreDetail.api";
    // 根据经纬度获取地址（2级，直辖市视为省，不分区）
    public static final String GET_CELLCHINA_LOCATIONS = HOST + "/ticket/schedule/cinema/GetCityByLongitudelatitude.api";

    // 返回某地区的影院列表
    public static final String GET_ONLINE_CINEMANS_BY_CITY = HOST + "/ticket/schedule/cinema/onlineCinemasByCity.api";

    // 获取所有城市
    public static final String GET_ALLCITY = HOST + "/common/utility/hotCitiesByCinema.api";
    // getMoviesByCityId获取有影讯的最近三天
    public static final String GET_CITY_CINEMA_MOVIES = HOST + "/ticket/schedule/showing/movies.api";
    /**
     * 获取最近三天有影讯的时间(影院影讯/影片影讯)
     * <p/>
     * 规则：城市Id和电影院Id必须有一个值传入， 1.当城市Id和电影院Id都传入时，则认为城市Id无效，只查找指定影院下的指定电影的影讯日期
     * 2.当只传入城市Id时，则指查找当前城市下的指定电影的影讯日期 3.当只传入影院Id时，则指查找当前影院下的指定电影的影讯日期
     */
    public static final String GET_MOVIE_SHOWTIMEDATA = HOST + "/ticket/schedule/showtime/show_dates.api";
    public static final String GET_MOVIE_LOCSHOWTIMECINEMA = HOST + "/ticket/schedule/showtime/location_movie_showtimes.api";

    // 知道的事
    public static final String GET_MOVIE_SECRET = HOST + "/Movie/events.api?";
    // 幕后制作
    public static final String GET_MOVIE_BEHIND = HOST + "/Movie/BehindMake.api?";
    // 发行商详情
    public static final String GET_COMPANY_DETAIL = HOST + "/library/company/makeMovies.api";
    // 媒体评论
    public static final String GET_MEDIA_REVIEW = HOST + "/Movie/MediaComments.api?";
    // 影片资料页-时光原创新闻列表页
    public static final String GET_MOVIE_ORIGINAL_NEWS_LIST = HOST + "/library/movie/timeNewsList.api";
    // 影片资料页-时光对话列表页
    public static final String GET_MOVIE_TALK_NEWS_LIST = HOST + "/library/movie/timeTalksList.api";
    // 影院详情
    public static final String GET_CINEMA_DETAIL = HOST + "/ticket/schedule/cinema/detail.api";
    // 获取影院评论信息列表（分页） 
    public static final String GET_CINEMA_COMMENT = HOST + "/Cinema/Comment.api?";
    /**
     * 获取影院优惠信息
     */
    public static final String GET_COUPON_FAVOURABLE_INFO = HOST + "/Cinema/CouponFavourableInfo.api?";
    // 影人详情 
    public static final String GET_ACTOR_DETAIL_INFO = HOST + "/library/person/detail.api";
    // 影人作品年表 @param sortType 排序类型（1：评分高到低，2：年代从近到远 3：年代从远到近）
    public static final String GET_FILMOGRAPHIES = HOST + "/library/person/movies.api";
    // 登录模块-获取邮箱后缀列表
    public static final String MAIL_EXTENSIONS = HOST + "/Showtime/MailExtensions.api";
    // 获取图片验证码
    public static final String GET_IMAGE_VERITY_CODE = HOST + "/user/user/getImageVerifyCode.api";
    // 意见反馈
    public static final String FEED_BACK = HOST + "/Mobile/Feedback.api";
    // 意见反馈列表
    public static final String FEED_BACK_LIST = HOST + "/Mobile/FeedBackList.api?";
    // 意见反馈－有奖反馈文字
    public static final String FEED_BACK_AWARD_TIPS = HOST + "/Mobile/FeedbackAwardTips.api";
    // 电子券模块  获取电子券订单信息
    public static final String GET_ETICKET_ORDER_INFO = HOST + "/Eticket/getETicketOrderInfo.api?";
    // 下电子券订单
    public static final String CREATE_ETICKET_ORDER = HOST + "/Eticket/createETicketOrder.api";
    // 轮询主订单状态
    public static final String GET_ORDER_STATUS = HOST + "/ticket/order/order/getOrderStatus.api";
    // 购票模块 某影院某影片某日所有的场次
    public static final String GET_SHOWTIMES_BY_CINEMA_MOVIE_DATE = HOST + "/Showtime/ShowTimesByCinemaMovieDate.api?";
    // 返回选座相关信息-根据场次id获取选座信息
    public static final String GET_SEAT_INFO = HOST + "/ticket/schedule/showtime/online_seats_by_showtime_id.api";
    // 下订单
    public static final String CREATE_ORDER = HOST + "/ticket/order/showtime/createOrder.api";
    // 下订单(非会员)(匿名购票)
    public static final String CREATE_ORDER_NOT_VIP = HOST + "/Showtime/AnonymousCreateOrder.api";

    // 取消订单
    public static final String CANCEL_ORDER = HOST + "/ticket/order/order/cancel.api";
    // 轮询子订单状态
    public static final String GET_SUB_ORDER_STATUS = HOST + "/ticket/order/order/getSubOrderStatus.api";
    // 获获取在线选座订单信息(开始倒计时页面)
    public static final String GET_ONLINE_ORDER_INFO = HOST + "/Showtime/getOnlineOrderInfo.api?";
    // 在线选座订单详情 
    public static final String ONLINE_TICKET_DATEIL = HOST + "/ticket/order/order/onlineticketdetail.api";
    // 第三方直销跳转数据准备
    public static final String SELLING_ORDER_PREPARE = HOST + "/directselling/orderPrepare.api";
    // 支付成功-保存图片到相册--获取一张以流的形式的空图片（票/电子券支付成功页）
    public static final String GET_TICKET_OR_ETICKET_IMAGE = HOST + "/ticket/order/ticket/getTicketOrETicketImage.api";
    // 获取用户最后一条未支付在线订单
    public static final String GET_WITHOUT_PAYMENT_ONLINE_SEAT = HOST + "/ticket/order/showtime/getWithoutPaymentOnlineSeat.api";
    // 礼品卡兑换金额计算
    public static final String GET_CARD_LOGIC = HOST + "/ticket/order/order/mtimeCardChangePrice.api";
    // 银行卡列表
    public static final String GET_BANK_CARD = HOST + "/Order/GetBankCardList.api";
    /**
     * 激活礼品卡（只跟支付有关） cardNum：礼品卡卡号，必填 password：卡号密码，必填.经Des加密
     * vcode：图形验证码，默认为空字符串 vcodeId:验证码Id，默认为空字符串 orderId: 必填。
     * isBind：是否需要绑定礼品卡，默认为false 不绑定。
     */
    public static final String GET_MTIME_CARD = HOST + "/ticket/market/card/ActivateMtimeCard.api";
    // 获取第三方支付列表
    public static final String GET_PAY_LIST = HOST + "/ticket/order/order/getPayTypeList.api";
    // 检测使用多张优惠券
    public static final String GET_CHECK_VOUCHER = HOST + "/ticket/order/ticket/checkUseMoreVoucher.api";
    // 获取影片影人影院的收藏评分上映提醒等状态 relateType 1电影 ，2影人， 33影院 6影评  X榜单（废弃）
    public static final String GET_RELATED_OBJ_STATUS = HOST + "/Showtime/GetRelatedObjStatus.api?";
    // 查询当前用户的收藏状态
    public static final String USER_COLLECT_QUERY = HOST + "/community/user_collect_query.api";
    // 获取新记录IDv2，此ID供未发布内容-保存记录API中recId使用
    public static final String POST_COMMUNITY_RECORD_ID = HOST + "/community/record_id/v2.api";
    // 未发布内容-保存记录
    public static final String POST_COMMUNITY_RECORD = HOST + "/community/record.api";
    // 影人评分：喜欢/不喜欢
    public static final String POST_PERSON_RATING = HOST + "/library/person/rating.api";
    /**
     * 删除收藏
     *
     * @param targetId
     * 新闻Id/影评id/ 影片id/影院id
     * @param targetType
     * 新闻-51, 影评-6, 影片-1,影人-2,影院-33
     */
    public static final String CANCEL_FAVORITE = HOST + "/Favorite/FavoriteDeleteByReletedId.api";
    /**
     * 添加收藏
     *
     * @param id
     * 新闻Id/影评id/ 影片id/影院id
     * @param type
     * 新闻-51, 影评-6, 影片-1,影人-2,影院-33
     */
    public static final String ADD_FAVORITE = HOST + "/Favorite/Add.api";
    // 获取图片列表（影人）
    public static final String GET_PERSON_IMAGES = HOST + "/library/person/imageAll.api";
    // 获取图片列表（影片）
    public static final String GET_MOVIE_IMAGES = HOST + "/library/movie/imageAll.api";
    // 获取图片列表（影院）
    public static final String GET_CINEMA_IMAGES = HOST + "/Cinema/CinemaGalleryList.api?";
    // 收藏文章列表
    public static final String GET_FAVORITE_ARTICLE_LIST = HOST + "/favorite/article/list.api";

    // 给影片评分 总评分r: (-1时，代表想看) ir: 印象分 str: 故事分 shr: 表演分 dr: 导演分 pr: 画面分 mr: 音乐分 c: 一句话影评
    public static final String RATING_MOVIE = HOST + "/Showtime/ratingmovie.api";
    // 新接口，只有单纯的评分功能
    public static final String POST_MOVIE_RATING = HOST + "/library/movie/movierating.api";
    // 影人评论列表
    public static final String GET_PERSON_COMMENTS = HOST + "/library/person/comment.api";
    // 发现-特色榜单
    public static final String GET_RECOMMEND_TOP_MOVIEDETAIL = HOST + "/TopList/TopListDetails.api?";
    // 发现-新闻评论页面
    public static final String GET_RECOMMEND_COMMENT_NEWS = HOST + "/News/Comment.api?";
    // 发现-影评评论页面
    public static final String GET_RECOMMEND_COMMENT_REVIEW = HOST + "/Review/Comment.api?";
    // 发现-新闻写评论
    public static final String GET_RECOMMEND_COMMENT_DONEWS = HOST + "/News/commentpost.api";
    // 发现-影评写评论
    public static final String GET_RECOMMEND_COMMENT_DOREVIEW = HOST + "/Review/commentpost.api";
    // 发现-固定的四个特色榜单
    public static final String GET_RECOMMEND_TOP_MOVIEDETAIL_FIX = HOST + "/TopList/TopListDetailsByRecommend.api?";
    // 电子券详情
    public static final String GET_ETICKET_DETAIL = HOST + "/Order/eticketdetail.api?";
    // 获取兑换码
    public static final String SEND_EXCHANGE_SMS = HOST + "/Order/resendSMS.api";
    // 影片分类筛选项（2020-9-22）
    public static final String CHOOSE_MOVIE_ITEM = HOST + "/mtime-search/search/getSearchItem";
    // 影片分类筛选（2020-9-22）
    public static final String POST_SEARCH_MOVIE_FILTER = HOST + "/mtime-search/search/movieFilter";
    // 搜索建议（2020-9-22）
    public static final String POST_SEARCH_SUGGESTION = HOST + "/mtime-search/search/suggest";
    // 影人、影片、影院、商城搜索（2020-9-22）
    public static final String POST_SEARCH_MULTI_SEARCH = HOST + "/mtime-search/search/unionSearch";
    // 修改昵称
    public static final String CHANGE_NICKNAME = HOST + "/user/user/nickname/edit.api";
    // 修改性别
    public static final String CHANGE_SEX = HOST + "/user/user/updateUserSex.api";
    // 修改会员基本信息（生日/居住地/签名）
    public static final String UPDATE_MEMBERINFO = HOST + "/user/account/updateMemberInfo.api";
    // 获取国家、省、城市列表
    public static final String GET_LOCATION_LIST = HOST + "/common/utility/locationList.api";
    // 获取更新版本
    public static final String GET_UPDATE_VER = HOST + "/common/mobileVersion.api";
    // 激活优惠券
    public static final String GET_ACTIVATEVUCHERCODE = HOST + "/ticket/market/voucher/activateVoucherCode.api";
    // 重新请求优惠券
    public static final String GET_AGAIN_USE_VOUCHER = HOST + "/Ticket/AvaliableVoucherListByUserID.api?";
    // 匿名购票15分钟时间同一IP需验证码
    public static final String GET_VERIFY_CODE = HOST + "/Order/AnonymousVerifyCode.api";

    public static final String GET_ALL_ETICKET_AND_TICKET = HOST + "/ticket/market/voucher/AvaliableETicketAndTicket.api";

    /**
     * 混合支付，目前支持优惠券、余额,第三方支付。加动态口令验证 orderId： 订单Id vcode：动态验证码
     * voucherIdList：优惠券id、多个用“,”隔开。如“123654，123659”。 balancePayAmount：12
     * //余额支付金额 rechargePayAmount：21 //第三方支付金额 payType：第三方支付支付类型，银联:5, 支付宝手机:6,
     * 支付宝wap:7 returnURL: 支付完成后的返回URL，目前用于工行wap支付和html5版的wap支付
     * cardId:礼品卡Id，使用礼品卡时必填 useNum:使用礼品卡次数，该参数仅在使用次卡时必填。
     * token:使用未绑定礼品卡的凭证。指当前用户使用非自己绑定的礼品卡时，必需带该凭证。 bankId:银行Id
     * ，主要用于第三方支付宝wap支付类型，该参数有效。默认为 0
     * 表示纯支付宝wap支付。如果传入银行Id，将使用支付宝wap银行支付。具体支持的银行列表，请见接口获取银行列表。
     */
    public static final String BLEND_PAY = HOST + "/ticket/order/order/blendPay.api";
    // 订单支付相关——发送动态验证码
    public static final String SEND_CODE = HOST + "/ticket/order/order/sendBindMobileIdentifyingCode.api";
    // 匿名使用电影卡(非会员 添加卡)
    public static final String USE_MTIMECARD = HOST + "/Order/AnonymousUseMtimeCard.api";
    // 非会员支付
    public static final String USE_NOTVIPPAY = HOST + "/Order/AnonymousPay.api";
    // 影片预告页列表
    public static final String GET_VIDEO_LIST = HOST + "/movie/category/video.api";

    public static final String UPLOAD_IAMGE_URL = HOST + "/GetUploadImageUrl.api";

    // 获取上传图片后的访问地址url
    public static final String GET_UPLOADED_IAMGE_URL = HOST + "/getUploadedImageUrl.api";

    // 上传头像
    public static final String GET_UPLOAD_HEAD_URL = HOST + "/user/user/avatar/edit.api";
    // 微评-影片影人评论列表-获取影片影人该微博信息及回复列表
    public static final String GET_TWITTER = HOST + "/Weibo/TweetCommentReplies.api?";

    public static final String GET_TWITTER_REPLY = HOST + "/Weibo/Reponse.api";
    // 影院评论列表-获取影院该微博信息及回复列表
    public static final String GET_TWITTER_CINEMA = HOST + "/Cinema/CinemaCommentReplies.api?";

    public static final String GET_TWITTER_CINEMA_REPLY = HOST + "/Cinema/ReplyCinemaComment.api";
    // 生成带登陆token的Url
    public static final String GET_COUPON_URL_WITH_LOGIN = HOST + "/Advertisement/GetCouponURLWithLogin.api";
    // 推送---增加上映提醒电影
    public static final String ADD_REMIND_MOVIE = HOST + "/Push/AddRemindMovie.api";
    // 推送---删除上映提醒电影
    public static final String DELETE_REMIND_MOVIE = HOST + "/Push/DeleteRemindMovie.api";
    //
    public static final String GET_MEMBER_CARD_LIST = HOST + "/ticket/market/card/GetAccountMembershipCardList.api";
    // 二维码请求接口
    public static final String POST_QR = HOST + "/Showtime/QRCodeControl.api";
    // 口令红包请求接口
    public static final String POST_RED_PACKET = HOST + "/redpacket/token.api";
    // 获取消息通知设置接口
    public static final String GET_MESSAGECONFIGESBYDEVICE = HOST + "/common/push/getMessageConfigesByDevice.api";
    // 设置消息通知设置接口
    public static final String SET_MESSAGECONFIGS = HOST + "/common/push/setMessageConfigs.api";
    // 资料页-点赞或取消赞
    public static final String ADD_OR_DEL_PRAISELOG = HOST + "/Comment/AddOrDelPraiseLog.api";
    //    点赞，取消点赞
    public static final String PRAISEUP_OR_CANCEL = HOST + "/community/praise_up.api";
    // 同步影院收藏
    public static final String SYNC_FAVORITE_CINEMA = HOST + "/Favorite/SyncFavoriteCinema.api";
    // 批量查询点赞点踩状态
    public static final String GET_PRAISE_STAT_LIST = HOST + "/community/praise_stat_list.api";
    // 广告-根据城市Id过滤
    public static final String AD_MOBILE_ADVERTISEMENT_INFO = HOST + "/Advertisement/MobileAdvertisementInfo.api?";
    // 根据影片id获取影片详情
    public static final String MOVIE_DETAIL_V2 = HOST + "/library/movie/detail.api";
    // 根据影片id获取影片扩展详情
    public static final String MOVIE_EXTEND_DETAIL_V2 = HOST + "/library/movie/extendDetail.api";

    // 影片长评论列表 /Movie/HotLongComments.api
    public static final String GET_MOVIE_HOTLONGCOMMENTS_V2 = HOST + "/library/movie/longCommentList.api";
    //获取某部电影关联的片单列表
    public static final String GET_MOVIE_LIST_RECOMMEND = HOST + "/library/movie/movieListRecommend.api";
    //即将上映列表
    public static final String GET_INCOMING_RECOMMEND_LIST = HOST + "/ticket/schedule/movie/coming_list.api";
    //获取即将上映列表想看电影的ids
    public static final String GET_WANT_MOVIE_IDS = HOST + "/ticket/schedule/movie/wantSeeMovieIds.api";
    // 第三方登录, h5页面接口
    public static final String GET_AUTHORIZE_PAGE = HOST + "/OAuth/AuthorizePage.api";

    //统一获取推荐位数据
    public static final String GET_RCMD_REGION_PUBLISH = HOST + "/common/rcmd_region_publish/list.api";
    //影院场次列表页公告推荐位
    public static final String GET_TICKET_SHOWTIME_NOTICE = HOST + "/common/rcmd_region_publish/getTicketM22ShowtimeNotice.api";
    //影院场次列表页活动推荐位
    public static final String GET_TICKET_SHOWTIME_ACTIVITY = HOST + "/common/rcmd_region_publish/getTicketM22ShowtimeActivity.api";

    //影院排片页关联商品
//    public static final String GET_MALL_MOVIE_RELEATED_GOODS = HOST + "/goods/movieReleatedGoods.api";
    // mall根据关联类型和ID获取商品 @param relatedObjType 关联类型，支持 1 影片、2影人、23 新闻
//    public static final String GET_RELATEDGOODSBYID = HOST + "/Search/RelatedGoodsById.api?";
    // 检查是否登录状态
    public static final String IS_LOGIN = HOST + "/user/user/is_login.api";
    // 座位是否可选
    public static final String ONLINE_SEATS_STATUS = HOST + "/ticket/schedule/showtime/online_seats_status.api";
    //支付-购票
    public static final String PAYMENT_ITEMS = HOST + "/ticket/order/order/paymentItems.api";

    //退票信息
    public static final String REFOUND_TICKET_MSG = HOST + "/ticket/order/cinema/refundTicket.api";
    // 购票支付放弃原因统计接口
    public static final String GET_GIVEUP_TICKET_PAY = HOST + "/ticket/order/ticket/giveUpReasons.api";
    //关联电影
    public static final String GET_RELATED_MOVIES = HOST + "/Movie/RelatedMovies.api?";
    //关联榜单
    public static final String GET_RELATED_TOP_LIST = HOST + "/relatedRankingList.api";
    //影片选座使用座位图标
    public static final String USE_SEATS_ICON = HOST + "/ticket/schedule/PageSubArea/UseSeatsIcon.api";
    // 帮助用户自动选择最优座位
    public static final String AUTO_OPTIMAL_SEATS = HOST + "/ticket/schedule/ticket/auto_optimal_seats.api";
    //获取影片短评列表数据 /Showtime/HotMovieComments.api?
    public static final String GET_MOVIE_COMMENTS = HOST + "/library/movie/comment.api";
    //
    public static final String GET_MOVIE_VOTE = HOST + "/Movie/VoteUrl.api?";
    public static final String GET_SUBMIT_VOTE = HOST + "/utility/vote.api?";
    //我的————我的电影短评和回复
    public static final String GET_MY_COMMENT_REPLY = HOST + "/User/UserComments.api?";

    // ************************性能优化开始********************
    public static final String GET_APP_START_REQUEST = HOST + "/common/startup/load.api";
    // 广告位统一接口，获取指定广告位最近一个月的前2条广告
    public static final String GET_COMMON_AD_INFO = HOST + "/svstg/getneartg.api";
    /**
     * 影院拍片页新优化接口
     */
    public static final String CINEMA_MOVIES = HOST + "/ticket/schedule/cinema/showtime.api";

    // 直播分享接口,以后旧接口也会逐步的迁移到这个接口上来
    public static final String GET_SHARE_DATA = HOST + "/common/utility/share.api";

    // 用户登录接口(包括关联第三方，强制绑定手机号登录)
    public static final String POST_LOGIN = HOST + "/user/user/login.api";
    // 第三方授权登录新接口
    public static final String POST_THIRD_LOGIN = HOST + "/user/user/oauth/login.api";

    // 发送短信验证码（不检查手机号是否已经注册，短信验证码登录页使用）
    public static final String POST_LOGIN_SMS_CODE = HOST + "/user/user/getSmsLoginCode.api";

    // 绑定手机号获取验证码接口
    public static final String POST_BIND_GET_SMSCODE = HOST + "/user/user/checkBindMobile.api";
    // 绑定手机号(已经注册为时光网账号的用户绑定手机号)
    public static final String POST_BIND_MOBILE = HOST + "/user/user/bindMobile.api";

    // 登出
    public static final String POST_LOGOUT = HOST + "/user/user/logout.api";

    // 找回密码
    // 找回密码时的发送校验码接口
    public static final String POST_REGET_PASSWORD_SEND_SMSCODE = HOST + "/user/user/forgetPwdSendCode.api";
    // 校验手机
    public static final String POST_REGET_PASSWORD_VERYCODE = HOST + "/user/user/verifyCode.api";
    // 通过找回密码进行的新密码设置
    public static final String POST_SAVE_NEW_PASSWORD_OF_FORGET = HOST + "/user/user/saveNewPwd.api";

    // 正常的修改密码
    public static final String POST_MODIFY_PASSWORD = HOST + "/user/user/modifyPassword.api";

    // 第三方授权H5回调登录接口
    public static final String POST_THIRD_LOGIN_WITHH5 = HOST + "/user/user/authWap/login.api";

    // 绑定手机和校验码接口
    public static final String POST_VERIFY_BIND_MOBILE_WITH_SMS_CODE = HOST + "/user/user/verifyBindMobileSmsCode.api";

    //第三方登录后进行注册后的设置密码接口
    public static final String POST_OUTH_SET_PASSWORD = HOST + "/user/user/oauthSetPassword.api";

    // 登录后的第一次绑定手机号后设置密码接口
    public static final String POST_SET_MOBILE_PASSWORD = HOST + "/user/user/setPwdAndBindMobile.api";

    // 获取影片评分分享图片
    public static final String GET_SHARE_IMAGES = HOST + "/movie/score/getShareImage.api";
    // 购票用户商品促销提示
    public static final String GET_PROMOTION_PROMPT = HOST + "/ticket/goods/promotionPrompt.api";

    //公众号添加关注
    public static final String GET_PUBLIC_ADD_FOLLOW = HOST + "/public/number/addFollow.api";

    //取消关注公众号
    public static final String GET_PUBLIC_CANCEL_FOLLOW = HOST + "/public/number/cancelFollow.api";

    // 我关注的公众号列表
    public static final String GET_MY_FOLLOW_PUBLIC_LIST = HOST + "/my/follow/publicList.api";

    // 文章发布评论/回复
    public static final String POST_ARTICLE_COMMENT = HOST + "/articleComment/reply.api";

    // 用户点赞评论列表
    public static final String GET_ARTICLE_PRAISE_LIST = HOST + "/Review/PariseInfosByRelatedIds.api";

    // 删除文章评论详情的评论
    public static final String DELETE_ARTICLE_COMMENT = HOST + "/articleComment/delete.api";

    //根据id获取push详细信息
    public static final String GET_PUSH_MESSAGE_BY_ID = HOST + "/common/push/message.api";

    // 会员中心首页弹窗
    public static final String GET_MEMBER_CENTER_POPUP = HOST + "/member/center/popup.api";

    // 会员中心首页拆礼包
    public static final String GET_MEMBER_GIFT_DISMANTLE = HOST + "/member/gift/dismantle.api";

    // 获取时光币列表
    public static final String GET_MTIME_COIN_LIST = HOST + "/memeber/exchange/couponList.api";

    // 时光币兑换优惠券
    public static final String EXCHANGE_COUPON_BY_COIN = HOST + "/memeber/exchange/couponBind.api";

    // 根据id获取sms详细信息
    public static final String GET_SMS_MESSAGE_BY_ID = HOST + "/short/message.api";

    //获取专题评论列表
    public static final String GET_REVIEW_SUBJECT_LIST = HOST + "/subject/review/list.api";

    //获取专题视频弹幕列表
    public static final String GET_BARRAGE_SUBJECT_LIST = HOST + "/subject/barrage/list.api";

    //专题视频发射弹幕
    public static final String POST_BARRAGE_SUBJECT_SHOOT = HOST + "/subject/barrage/shoot.api";

    //专题评论及回复
    public static final String POST_REVIEW_SUBJECT = HOST + "/subject/review.api";

    //获取关联评论的点赞信息列表
    public static final String GET_REVIEW_PARISEINFOS_BY_RELATEDIDS = HOST + "/Review/PariseInfosByRelatedIds.api";

    //获取影片视频详情
    public static final String GET_MOVIE_DETAIL = HOST + "/library/movie/video/detail.api";

    // 获取用户收藏、上次去过影院列表
    public static final String GET_CINEMA_FAVORITE_LIST = HOST + "/user/cinema/favoriteList.api";
    // 分页查询用户收藏影院
    public static final String GET_COLLECT_CINEMA_LIST = HOST + "/community/collect_cinemas.api";
    //影片外部播放源信息接口
    public static final String GET_MOVIE_EXTERNAL_INFO = HOST + "/library/movie/externalPlayInfos.api";

    // 影院列表页面筛选
    public static final String GET_CINEMA_SCREENING = HOST + "/cinema/screening.api";

    //获取播放地址接口
    public static final String GET_PLAY_URL = HOST + "/video/play_url2";

    //获取视频的广告列表
    public static final String GET_VIDEO_AD_INFO = HOST + "/video/dalist.api";

    //查询视频信息列表
    public static final String POST_PLAY_VIDEO_INFO = HOST + "/play/getVideoInfo";

    //获取影片分类视频
    public static final String GET_CATEGORY_VIDEO = HOST + "/library/movie/category/video.api";

    //---- 每日推荐 ↓
    // 每日弹窗(/dailyRcmdMoviePopup.api)
    public static final String DAILY_MOVIE_POPUP = HOST + "/library/index/dailyRcmdMoviePopup.api";
    // 按月返回的历史推荐数据(/dailyRcmdMoviesByMonth.api)
    public static final String HISTORY_RECMD_MOVIE = HOST + "/library/index/dailyRcmdMoviesByMonth.api";
    // 每日推荐(/dailyRcmdMovies.api)
    public static final String DAILY_RECMD_MOVIE = HOST + "/library/index/dailyRcmdMovies.api";
    //---- 每日推荐 ↑

    //用户签到
    public static final String USER_SIGN = HOST + "/user/member/sign.api";

    //TODO 订单详情页请求华为钱包的
    public static final String GET_HUA_WEI_WALLET_INFO = HOST + "/hwpass/downloadTicketFile.api";

    // 片单列表接口
    public static final String SUBJECT_LIST = HOST + "/movieList/channel/list.api";

    // 当前用户最新的一条影评
    public static final String GET_MOVIE_LATEST_REVIEW = HOST + "/library/movie/currentUser/latestComment.api";

    // 设置电影为看过
    public static final String POST_MOVIE_SET_SEEN = HOST + "/library/movie/setHasSeen.api";

    // 设置电影想看
    public static final String POST_MOVIE_SET_WANT_TO_SEE = HOST + "/library/movie/setWantToSee.api";

    // 影片的热门 短评/影评
    public static final String GET_MOVIE_HOT_REVIEW = HOST + "/library/movie/hotComments.api";

    // 删除短影评
    public static final String POST_MOVIE_DELETE_SHORT_COMMENT = HOST + "/movie/shortcomment/delete.api";
    // 删除上影评
    public static final String POST_MOVIE_DELETE_LONG_COMMENT = HOST + "/movie/longcomment/delete.api";

    // 发布长影评
    public static final String POST_MOVIE_LONG_COMMENT = HOST + "/movie/comment/submit.api";
    // 发布短影评
    public static final String POST_MOVIE_SHORT_COMMENT = HOST + "/movie/shortComment/submit.api";

    // Mtime服务条款与隐私政策 弹框
    public static final String GET_PRIVACY_POLICY = HOST + "/common/privacyPolicy.api";

    // 获取 我的长影评列表
    public static final String GET_MY_LONG_MOVIE_COMMENTS = HOST + "/library/movie/myComment/lists.api";

    // 设置群组管理员
    public static final String SET_GROUP_ADMINISTRATOR = HOST + "/group/setAdministrator.api";

    // 获取群组申请列表
    public static final String GET_GROUP_APPLICANT_LIST = HOST + "/group/applicantList.api";

    /*
     *  删除评论
     */
    public static final String GROUP_POST_DELETE_COMMENT = HOST + "/group/comment/delete.api";

    /**
     * 获取购票订单实名预约信息
     */
    public static final String GET_REAL_NAME_RESERVATION_DETAIL = HOST + "/ticket/schedule/real_name_reservation/detail.api";

    /**
     * h5 填写实名预约购票身份信息
     */
    public static final String FEATURE_COMMIT_REAL_NAME_INFO = "https://m.mtime.cn/realName?orderId=%1$s&seatNum=%2$s";

    // 收藏/取消收藏(2020.09改版新增接口）
    public static final String POST_COMMUNITY_COLLECT = HOST + "/community/collect.api";

    // 影人的关联新闻列表(2020.09改版新增接口）
    public static final String GET_PERSON_NEWS_LIST = HOST + "/library/person/timeNewsList.api";

    // 获取用户对影人的动态信息(2020.09改版新增接口）
    public static final String GET_PERSON_DYNAMIC = HOST + "/library/person/dynamic.api";

    //    相册api - 删除图片（/user_image/delete）
    public static final String DELETE_IMAGE_FROM_ALBUM = "/community/user_image/delete";

    // 热门点击
    public static final String POST_SEARCH_POPULAR_CLICK = "/mtime-search/search/poplarClick";

    //是否需要弹出彩蛋
    public static final String GET_POPUP_BONUS_SCENE = "/user/member/popupBonusScene.api";
}
