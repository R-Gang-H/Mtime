package com.mtime.util;

import com.mtime.common.utils.TextUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class MallUrlHelper {
    private static final String DEFAULT_URL_HOST = "https://mall-wv.mtime.cn";
    private static final String DEFAULT_URL_PATH_CONNECT = "#!";

    public static String URL_HOST = DEFAULT_URL_HOST;
    private static final long serialVersionUID = 1L;

    private static final Hashtable<MallUrlType, String> urls = new Hashtable<MallUrlType, String>() {
        {
            put(MallUrlType.HOME, "/mall/index/");//首页
            put(MallUrlType.LOGIN, "/member/signin");//登录
            put(MallUrlType.HELP, "/help/helpCenter/");//商城使用帮助
            put(MallUrlType.CART_NUM, "/commerce/cart/");//购物车
            put(MallUrlType.ADD_CART, "/commerce/addcart/");//添加购物车
            put(MallUrlType.ORDER_CONFIRM, "/commerce/order/confirm/");//确认订单
            put(MallUrlType.PRODUCTS_LIST, "/commerce/list/");//打开相关商品列表页
            put(MallUrlType.PRODUCTS_LIST_SEARCH, "/commerce/list/");//打开相关商品列表页
            put(MallUrlType.PRODUCT_VIEW, "/commerce/item/");//商品详情
            put(MallUrlType.ADDRESS_LIST, "/commerce/address/list/");//地址列表
            put(MallUrlType.ADDRESS_SECLET, "/commerce/address/select/");//选择地址
            put(MallUrlType.ADDRESS_EDIT, "/commerce/address/edit/");//用户收货编辑地址
            put(MallUrlType.ADDRESS_ADD, "/commerce/address/add/");//添加地址
            put(MallUrlType.VOUCHER_SELECT, "/commerce/voucher/select/");//选择优惠券
            put(MallUrlType.ORDER_INFO, "/commerce/order/");//订单
            put(MallUrlType.ORDER_PAY, "/commerce/order/pay/");//订单支付
            put(MallUrlType.MY_GOODS_ORDERS, "/my/goodsOrders/-1/");//全部订单，1代付款2，待收货4，待评价
            put(MallUrlType.MY_GOODS_ORDERS_PENDING_EVALUATION, "/my/goodsOrders/4/");//待评价
            put(MallUrlType.MY_TICKET_ORDERS, "/my/account/");//我的账号
            put(MallUrlType.MY_VOUCHER_LIST, "/my/voucher/goods/1/");//
            put(MallUrlType.ADD_VOUCHER, "/commerce/voucher/add/");//添加优惠券
            put(MallUrlType.BACK_VOUCHER_LIST, "/backtovoucherlist/");//优惠券列表页
            put(MallUrlType.VOUCHER_HELP, "/help/discountCoupon/");//优惠券使用说明
            put(MallUrlType.INVOICE_INFO, "/commerce/invoice/info/");//发票信息
            put(MallUrlType.LOGISTICS_INFO, "/commerce/logistics/info");//配送方式及送货时间

            //noticeUrls
            put(MallUrlType.SHOW_PROCUDTS_FILTER, "/commerce/showfilterbtn");//显示筛选btn
            put(MallUrlType.FILTER_CONFIRM, "/commerce/list/confirmfilter");//筛选按钮是否被按下
            put(MallUrlType.LOADING_ERROR, "showpage404");//
            put(MallUrlType.LOADING_END, "closeloading");//
            //
            put(MallUrlType.ORDERS_COMMENT, "order/comment/\\d+/");//

            // 客服页， 待添加地址
            // http://mtime.udesk.cn/im_client/
            put(MallUrlType.MALL_CUSTOM_SERVICE, "/im_client/");//
        }
    };

    private static final Hashtable<MallUrlType, String> urlsMatchers = new Hashtable<MallUrlType, String>() {
        {
            put(MallUrlType.HOME, "/mall/index/");
            put(MallUrlType.LOGIN, "/member/signin");
            put(MallUrlType.HELP, "/help/helpCenter/");
            put(MallUrlType.CART_NUM, "/commerce/cart/\\$");
            put(MallUrlType.CART_NUM_NOTICE, "/commerce/cart/\\d+");
            put(MallUrlType.ADD_CART, "/commerce/addcart/");
            put(MallUrlType.ORDER_CONFIRM, "/commerce/order/confirm/");
            put(MallUrlType.PRODUCTS_LIST, "/commerce/list/");
            put(MallUrlType.PRODUCTS_LIST_SEARCH, "/commerce/list/");
            put(MallUrlType.PRODUCT_VIEW, "/commerce/item/(\\d+)/");
            put(MallUrlType.ADDRESS_LIST, "/commerce/address/list/");
            put(MallUrlType.ADDRESS_SECLET, "/commerce/address/select/");
            put(MallUrlType.ADDRESS_EDIT, "/commerce/address/edit/");
            put(MallUrlType.ADDRESS_ADD, "/commerce/address/add/");
            put(MallUrlType.VOUCHER_SELECT, "/commerce/voucher/select/");
            put(MallUrlType.ORDER_INFO, "/commerce/order/\\d+/");
            put(MallUrlType.ORDER_PAY, "/commerce/order/pay/\\d+/");
            put(MallUrlType.MY_GOODS_ORDERS, "/my/goodsOrders/(\\-?[0-9]+)/");
            put(MallUrlType.MY_GOODS_ORDERS_PENDING_EVALUATION, "/my/goodsOrders/");
            put(MallUrlType.MY_TICKET_ORDERS, "/my/account/");
            put(MallUrlType.MY_VOUCHER_LIST, "/my/voucher/goods/\\d+/");
            put(MallUrlType.ADD_VOUCHER, "/commerce/voucher/add/");
            put(MallUrlType.BACK_VOUCHER_LIST, "/backtovoucherlist/");
            put(MallUrlType.VOUCHER_HELP, "/help/discountCoupon/");
            put(MallUrlType.INVOICE_INFO, "/commerce/invoice/info/");
            put(MallUrlType.LOGISTICS_INFO, "/commerce/logistics/info");
            put(MallUrlType.TRACK, "/commerce/track/\\d+/");

            put(MallUrlType.FEATURE, "feature");
            put(MallUrlType.PRODUCT_COMMENTS, "/commerce/item/\\d+/comments/");

            //noticeUrls
            put(MallUrlType.SHOW_PROCUDTS_FILTER, "/commerce/showfilterbtn");
            put(MallUrlType.FILTER_CONFIRM, "/commerce/list/confirmfilter");
            put(MallUrlType.LOADING_ERROR, "showpage404");
            put(MallUrlType.LOADING_END, "closeloading");
            //
            put(MallUrlType.ORDERS_COMMENT, "order/comment/\\d+/");

            //退货申请地址
            put(MallUrlType.GOODS_RETURNED_ASK, "/returned/apply/\\d+/");
            // 退货服务说明
            put(MallUrlType.GOODS_RETURN_POLICY, "/returned/apply/servernote");
            // 退货列表
            put(MallUrlType.GOODS_RETURNED_LIST, "/my/goodsOrders/5/");
            //退货详情页,待添加地址
            put(MallUrlType.GOODS_RETURNED_DETAIL, "/returned/detail/\\d+/");
            // 客服页， 待添加地址
            put(MallUrlType.MALL_CUSTOM_SERVICE, "/im_client/");

        }
    };

    private static final ArrayList<MallUrlType> noticeUrlTypes = new ArrayList<MallUrlType>();

    static {
        noticeUrlTypes.add(MallUrlType.ADD_CART);
        noticeUrlTypes.add(MallUrlType.CART_NUM_NOTICE);
        noticeUrlTypes.add(MallUrlType.LOADING_END);
        noticeUrlTypes.add(MallUrlType.FILTER_CONFIRM);
        noticeUrlTypes.add(MallUrlType.LOADING_ERROR);
        noticeUrlTypes.add(MallUrlType.BACK_VOUCHER_LIST);
        noticeUrlTypes.add(MallUrlType.SHOW_PROCUDTS_FILTER);
//        noticeUrlTypes.add(MallUrlType.ORDERS_COMMENT);
    }

    public enum MallUrlType {
        GENERAL, HOME, HELP, CART_NUM, CART_NUM_NOTICE, ADD_CART, ORDER_CONFIRM,
        BUY_NOW, PRODUCTS_LIST, PRODUCTS_LIST_SEARCH, FILTER_CONFIRM,
        PRODUCT_VIEW, ADDRESS_LIST, ADDRESS_EDIT, ADDRESS_SECLET,
        ADDRESS_ADD, LOGIN, VOUCHER_SELECT, PAGE_LOAD_FINISHED, ORDER_PAY,
        MY_GOODS_ORDERS,MY_GOODS_ORDERS_PENDING_EVALUATION,MY_TICKET_ORDERS, MY_VOUCHER_LIST, ADD_VOUCHER,
        VOUCHER_HELP, ORDER_INFO, PRODUCT_COMMENTS, SHOW_PROCUDTS_FILTER, FEATURE,
        LOADING_END, LOADING_ERROR, BACK_VOUCHER_LIST, TOOGGLE_PAGE, INVOICE_INFO, LOGISTICS_INFO, TRACK,
        ORDERS_COMMENT, GOODS_RETURNED_ASK, GOODS_RETURN_POLICY, GOODS_RETURNED_LIST, GOODS_RETURNED_DETAIL,
        MALL_CUSTOM_SERVICE,
    }

    public static void setHost(final String host) {
        URL_HOST = host;
        if (URL_HOST.contains("#!")) {
            URL_HOST = URL_HOST.substring(0, URL_HOST.lastIndexOf("#!"));
        }
    }

    /*
     * 获取后缀短路径
     */
    public static String getShortUrl(MallUrlType urlType) {
        String url = "";
        if (urls.containsKey(urlType)) {
            url = urls.get(urlType);
        }
        return url;
    }

    /*
     * 获取完整路径
     */
    public static String getUrl(MallUrlType urlType) {
        String url = "";
        if (urls.containsKey(urlType)) {
            url = String.format("%s%s%s", URL_HOST, DEFAULT_URL_PATH_CONNECT, getShortUrl(urlType));
        }
        return url;
    }

    /*
     * 获取完整路径,带参数数组，多个参数自动拼接/id1/id2
     */
    public static <T> String getUrl(MallUrlType urlType, T... args) {
        String url = "";
        if (urls.containsKey(urlType)) {
            if (MallUrlType.PRODUCTS_LIST_SEARCH == urlType) {
                url = String.format("%s%s%s?q=%s", URL_HOST, DEFAULT_URL_PATH_CONNECT, getShortUrl(urlType), args[0]);
            } else {
                url = String.format("%s%s%s%s", URL_HOST, DEFAULT_URL_PATH_CONNECT, getShortUrl(urlType),
                        getUrlParams(args));
            }
        }
        return url;
    }

    /*
     * 用/拼接动态参数
     */
    private static <T> String getUrlParams(T... args) {
        StringBuilder params = new StringBuilder();
        for (T urlParam : args) {
            params.append(urlParam).append("/");
        }
        return params.toString();
    }

    /*
     * 判断url是否是指定类型的路径
     */
    public static Boolean isMallUrl(String url, MallUrlType urlType) {
        return url.contains(getShortUrl(urlType));
    }

    /*
     * 根据地址信息判断当前页面是那种类型
     */
    public static MallUrlType getUrlType(String url) {
        MallUrlType result = MallUrlType.GENERAL;
        for (@SuppressWarnings("rawtypes")
             Iterator iterator = urlsMatchers.keySet().iterator(); iterator.hasNext(); ) {

            MallUrlType mallUrlType = (MallUrlType) iterator.next();
            String matcher = urlsMatchers.get(mallUrlType);
            if (TextUtil.isMatcher(url, matcher)) {
                result = mallUrlType;
                break;
            }
        }
        return result;
    }

    public static boolean isNoticeUrl(String url) {
        return isNoticeUrl(getUrlType(url));
    }

    public static boolean isNoticeUrl(MallUrlType urlType) {
        return noticeUrlTypes.contains(urlType);
    }
}
