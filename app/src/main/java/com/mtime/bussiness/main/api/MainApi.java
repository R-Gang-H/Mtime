//package com.mtime.bussiness.main.api;
//
//import com.kotlin.android.app.data.entity.bonus.PopupBonusScene;
//import com.mtime.base.network.BaseApi;
//import com.mtime.base.network.NetworkManager;
//import com.mtime.bussiness.video.bean.PlayerMovieDetail;
//import com.mtime.network.ConstantUrl;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * create by lushan on 2020/12/29
// * description:
// */
//public class MainApi  extends BaseApi {
//    private final List<Object> tags = new ArrayList<>();
//
//    private void addTag(Object tag) {
//        tags.add(tag);
//    }
//
//    public void cancelAllTags() {
//        if (tags != null) {
//            for (Object tag : tags) {
//                cancel(tag);
//            }
//            tags.clear();
//        }
//    }
//    @Override
//    protected String host() {
//        return null;
//    }
//
//    @Override
//    public void cancel(Object tag) {
//        super.cancel(tag);
//    }
//
//    public void cancelRequest(Object tag) {
//        cancel(tag);
//    }
//
//    public void checkPopupBonus(long action, NetworkManager.NetworkListener<PopupBonusScene> networkListener){
//        String tag = "check_popup_bonus";
//        addTag(tag);
//        Map<String, String> params = new HashMap<>();
//        params.put("action", String.valueOf(action));
//        get(tag, ConstantUrl.GET_POPUP_BONUS_SCENE, params, networkListener);
//
//    }
//}
