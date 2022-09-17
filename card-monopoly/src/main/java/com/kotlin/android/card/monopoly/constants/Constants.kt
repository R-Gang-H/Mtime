package com.kotlin.android.card.monopoly.constants

object Constants {
    const val PARAM_SUIT_ID = "suitID"
    const val PARAM_SUIT_TYPE = "suitType"


    //拍卖行购买页的常量数据
    //结束时间倒序
    const val ORDER_TIME_DES = 1

    //结束时间正序
    const val ORDER_TIME_ASE = 2

    //一口价倒序
    const val ORDER_PRICE_DES = 3

    //一口价正序
    const val ORDER_PRICE_ASE = 4

    //当前价倒序
    const val ORDER_CURRENT_DES = 5

    //当前价正序
    const val ORDER_CURRENT_ASE = 6

    //卡片类型
    const val ORDER_CARD = 1L

    //套装类型
    const val ORDER_SUIT = 3L

    //道具卡类型
    const val ORDER_TOOLS = 2L


    //卡片商店宝箱类型
    //活动宝箱
    const val STORE_BOX_ACTIVITY = 1

    //正常宝箱
    const val STORE_BOX_NORMAL = 2

    //限量宝箱
    const val STORE_BOX_LIMIT = 3

    //未领取的宝箱
    const val STORE_BOX_REWARD = 4

    //游戏信息交易记录
    const val GAME_RECORD_GIFT_CARD = 0L //赠送卡片
    const val GAME_RECORD_USE_PROPS = 1L //使用道具
    const val GAME_RECORD_INIT_TRANS = 2L //发起交易
    const val GAME_RECORD_DISS_PRICE = 3L //议价
    const val GAME_RECORD_TRANS_SUCCESS = 4L //交易成功
    const val GAME_RECORD_TRANS_FAIL = 5L //交易失败
    const val GAME_RECORD_REFUSE_TRANS = 6L //拒绝交易
    const val GAME_RECORD_REFUSE_DISS = 7L //拒绝议价
    const val GAME_RECORD_INVITE_FRIENDS = 8L //邀请好友
    const val GAME_RECORD_BIDDING_SUCCESS = 9L //竞拍成功
    const val GAME_RECORD_BIDDING_FAIL = 10L //竞拍失败
    const val GAME_RECORD_AUCTION_SUCCESS = 11L //拍卖成功
    const val GAME_RECORD_AUCTION_FAIL = 12L //拍卖失败
    const val GAME_RECORD_ADD_FRIEND = 13L //添加卡友
    const val GAME_RECORD_ADD_FRIEND_SUCCESS = 14L //添加卡友成功
    const val GAME_RECORD_REFUSE_ADD = 15L //拒绝添加卡友
    const val GAME_RECORD_DEL_FRIEND = 16L //删除好友
    const val GAME_RECORD_GIFT_GOLD = 17L //赠送金币
    const val GAME_RECORD_GIFT_PROP = 18L //赠送道具卡

    //我的卡友列表点击跳转类型
//    const val CARD_FRIEND_CARD = "card_friend"  //跳转好友详情
    const val CARD_FRIEND_CARD_MAIN = 1  //跳转好友详情
    const val CARD_FRIEND_CARD_DETAIL = 2  //跳转卡片弹框
    const val CARD_FRIEND_CARD_DISCARD = 3  //跳转丢弃卡片
    const val CARD_FRIEND_TOOLS_SLAVE = 4  //使用奴隶卡
    const val CARD_FRIEND_TOOLS_ROB = 5  //使用打劫卡
    const val CARD_FRIEND_TOOLS_ROB_LIMIT = 7  //使用限量打劫卡
    const val CARD_FRIEND_TOOLS_HACK = 6  //使用黑客卡

    //使用道具
    const val PROPS_USE_TYPE_FROM = "use_tools_flag" //
    const val PROPS_USE_TYPE_0 = 0L //对自己使用
    const val PROPS_USE_TYPE_1 = 1L //对自己使用
    const val PROPS_USE_TYPE_2 = 2L //对好友使用
    const val PROPS_USE_TYPE_3 = 3L //不能直接使用

    //头像的宽和高
    const val IMG_AVATAR_WIDTH = 40
    const val IMG_AVATAR_HEIGHT = 40

    const val TYPE_999 = 999L

    //游戏信息中的item点击类型
    //同意添加好友
    const val TYPE_AGREE_FRIEND = 1
    //同意交易
    const val TYPE_AGREE_TRADE = 2
    //拒绝交易
    const val TYPE_REFUSE_TRADE = 3
    //拒绝添加好友
    const val TYPE_REFUSE_FRIEND = 4
    //交易加价
    const val TYPE_ADD_TRADE_PRICE = 5
    //使用道具
    const val TYPE_USE_TOOLS = 6
    //删除信息
    const val TYPE_DELETE = 7

    //道具卡的类别id
    //财神卡
    const val TOOlS_CARD_MAMMON = 1L
    //流氓卡
    const val TOOlS_CARD_SCAMP = 2L
    //奴隶卡
    const val TOOlS_CARD_SLAVE = 3L
    //防盗卡
    const val TOOlS_CARD_GUARD = 4L
    //打劫卡
    const val TOOlS_CARD_ROB = 5L
    //运气卡
    const val TOOlS_CARD_LUCK = 6L
    //急救卡
    const val TOOlS_CARD_CURE = 7L
    //复制卡
    const val TOOlS_CARD_COPY = 8L
    //黑客卡
    const val TOOlS_CARD_HACKER = 9L
    //恶魔卡
    const val TOOlS_CARD_DEMON = 10L
    //反弹卡
    const val TOOlS_CARD_BOUNCE = 11L
    //礼品卡
    const val TOOlS_CARD_GIFT = 12L
    //空位卡
    const val TOOLS_CARD_BAGPOS = 13L
    //保险箱卡
    const val TOOLS_CARD_BAG = 14L
    //拆套卡
    const val TOOLS_CARD_DISMANTLE = 15L
    //打劫卡(限量)
    const val TOOlS_CARD_ROB_LIMIT = 16L
    //口袋卡
    const val TOOlS_CARD_POCKET = 17L
    //隐身卡
    const val TOOlS_CARD_HIDE = 18L

    const val KEY_START_PRICE = "start_price"

    const val KEY_FIX_PRICE = "fix_price"

    const val KEY_TIME_AUCTION = "time_auction"

    const val KEY_POINT_X = "ponit_X"

    const val KEY_POINT_Y = "point_Y"

    const val KEY_CARD_DETAIL = "card_img_detail"

    const val AUCTION_TIME_TWO = 2L

    const val AUCTION_TIME_EIGHT = 8L

    const val AUCTION_TIME_SIXTEEN = 16L

    const val TYPE_CARD = 1L
    const val TYPE_SUIT = 3L
    const val TYPE_TOOLS = 2L




}