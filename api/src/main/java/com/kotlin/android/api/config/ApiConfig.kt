package com.kotlin.android.api.config

/**
 * API配置
 *
 * Created on 2020/5/6.
 *
 * @author o.s
 */
object ApiConfig {
    const val NET_ERROR = "暂无网络，请重试" // "网络不给力"
    const val CONVERTER_CONTENT_ERROR = "(-76) error content" // API转换内容错误
    const val CONVERTER_TYPE_ERROR = "(-77) error type" // API转换类型错误
    const val GATEWAY_ERROR = "gateway获取失败"
    const val CONFIG_FILE_NAME = "config.properties"
    const val CERTIFICATE_FILE_NAME_CRT = "certificate/wandafilm.com.crt"
    const val CERTIFICATE_FILE_NAME_CER = "certificate/wandafilm.com.cer"

    const val PAY_SUCCESS_URL = "wandafilm/pay/finished"//自定义的跳转url  监听到webview往这个地址跳就关闭webview
    const val CHANNEL_TYPE = 1 // 1APP, 2PC, 3H5, 4KIOSK
    const val BUSINESS_ID_TICKET = 1 //业务范围：1购票
    const val BUSINESS_ID_SNACK = 2 //业务范围：2小卖
    const val BUSINESS_ID_CARD_CHARGE = 3 //业务范围：3会员卡充值
    const val BUSINESS_ID_CARD_BUY = 4 //业务范围： 4.会员卡购买
    const val BUSINESS_ID_COUPON_BUY = 5 //业务范围：5.券销售
    const val BUSINESS_ID_GATHER_BUY = 6 //业务范围：6.拼活动

    // 批量请求接口（batch_request.api） host 字段
    const val LOCAL_DNS_MISC = "miscApiHost"
    const val LOCAL_DNS_CINEMA = "cinemaApiHost"
    const val LOCAL_DNS_USER = "userApiHost"
    const val LOCAL_DNS_TICKET = "ticketApiHost"
    const val LOCAL_DNS_PAYMENT = "paymentApiHost"
    const val LOCAL_DNS_CREATE = "createTimpstamp"
    const val LOCAL_DNS_LAST_MODIFY = "lastModifyTimpstamp"
    const val LOCAL_DNS_LOG = "logApiHost"
    const val LOCAL_DNS_CARD = "cardApiHost"
    const val LOCAL_DNS_COUPON = "couponApiHost"
    const val LOCAL_DNS_ACTIVITY = "activityApiHost"
    const val LOCAL_DNS_ACTIVITY_MKT = "activityMktApiHost"
    const val LOCAL_DNS_COUNT = "countApiHost"

    /**
     * AES加密需要的Key
     */
    const val AES_QAS = "2CDA6075E48B701F"
    const val AES_STG = "001B069F645188F4"
    const val AES_PRD = "001B069F645188F4"


    /**
     * POST批量请求接口  return BatchBean
     */
    const val POST_BATCH_URL_QAS = "http://gateway.api.qas.mx.com/batch_request.api"
    const val POST_BATCH_URL_STG = "https://gateway-api-stg-mx.wandafilm.com/batch_request.api"
    const val POST_BATCH_URL_PRD = "https://gateway-api-prd-mx.wandafilm.com/batch_request.api"

    /**
     * 埋点接口
     */
    const val POST_LOG_URL_QAS = "https://weblog-api-qas-mx.wandafilm.com/m?format=json"
    const val POST_LOG_URL_STG = "https://weblog-api-stg-mx.wandafilm.com/m?format=json"
    const val POST_LOG_URL_PRD = "https://weblog-api-prd-mx.wandafilm.com/m?format=json"

    /**
     * gateway地址
     */
    const val GATEWAY_URL_QAS = "http://gateway.api.qas.mx.com/gateway_info.api"
    const val GATEWAY_URL_STG = "https://gateway-api-stg-mx.wandafilm.com/gateway_info.api"
    const val GATEWAY_URL_PRD = "https://gateway-api-prd-mx.wandafilm.com/gateway_info.api"

    /**
     * 服务器集群默认域名
     */
    val HOST_ARRAY_DEV = arrayOf(
            "http://misc.api.qas.mtime.cn",
            "http://cinema.api.qas.mtime.cn",
            "http://user.api.qas.mtime.cn",
            "http://ticket.api.qas.mtime.cn",
            "http://payment.api.qas.mtime.cn",
            "http://card.api.qas.mx.com",
            "http://snack.api.qas.mx.com",
            "http://coupon-api-qas.mtime.cn",
            "http://activity-cms-api-qas.mtime.cn",
            "http://mkt-activity.api.qas.mx.com",
            "http://count-api-qas-mx.wandafilm.com",
            "http://front-gateway.mtime.cn",
            "http://ticket-api-m.mtime.cn",
            "http://live-api-m.mtime.cn"
    )
    val HOST_ARRAY_QAS = arrayOf(
            "http://misc.api.qas.mtime.cn",
            "http://cinema.api.qas.mtime.cn",
            "http://user.api.qas.mtime.cn",
            "http://ticket.api.qas.mtime.cn",
            "http://payment.api.qas.mtime.cn",
            "http://card.api.qas.mx.com",
            "http://snack.api.qas.mx.com",
            "http://coupon-api-qas.mtime.cn",
            "http://activity-cms-api-qas.mtime.cn",
            "http://mkt-activity.api.qas.mx.com",
            "http://count-api-qas-mx.wandafilm.com",
            "http://front-gateway.mtime.cn",
            "http://ticket-api-m.mtime.cn",
            "http://live-api-m.mtime.cn"
    )
    val HOST_ARRAY_STG = arrayOf(
            "https://misc-api-stg-mx.wandafilm.com",
            "https://cinema-api-stg-mx.wandafilm.com",
            "https://user-api-stg-mx.wandafilm.com",
            "https://ticket-api-stg-mx.wandafilm.com",
            "https://payment-api-stg-mx.wandafilm.com",
            "https://card-api-stg-mx.wandafilm.com",
            "https://snack-api-stg-mx.wandafilm.com",
            "https://coupon-api-stg-mx.wandafilm.com",
            "https://cms-activity-api-stg-mx.wandafilm.com",
            "https://mkt-activity-api-stg-mx.wandafilm.com",
            "https://count-api-stg-mx.wandafilm.com",
            "https://front-gateway.mtime.cn",
            "https://ticket-api-m.mtime.cn",
            "https://live-api-m.mtime.cn"
    )

    //    http://front-gateway.mtime.cn
    // https://ticket-api-m.mtime.cn
    val HOST_ARRAY_PRD = arrayOf(
            "https://misc-api-prd-mx.wandafilm.com",
            "https://cinema-api-prd-mx.wandafilm.com",
            "https://user-api-prd-mx.wandafilm.com",
            "https://ticket-api-prd-mx.wandafilm.com",
            "https://payment-api-prd-mx.wandafilm.com",
            "https://card-api-prd-mx.wandafilm.com",
            "https://snack-api-prd-mx.wandafilm.com",
            "https://coupon-api-prd-mx.wandafilm.com",
            "https://cms-activity-api-prd-mx.wandafilm.com",
            "https://mkt-activity-api-prd-mx.wandafilm.com",
            "https://count-api-prd-mx.wandafilm.com",
            "https://front-gateway.mtime.cn",
            "https://ticket-api-m.mtime.cn",
            "https://live-api-m.mtime.cn"
    )

    const val REMIND_TIME = "24"//产品需求：获取24小时内的电影信息，如有多个，取最近的一个
}