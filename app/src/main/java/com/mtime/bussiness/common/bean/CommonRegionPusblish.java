package com.mtime.bussiness.common.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;
import java.util.Map;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/11/20
 *
 * 统一的推荐位数据，items里面使用Map，这样推荐位返回的数据都可使用此实体
 */
public class CommonRegionPusblish implements IObfuscateKeepAll {

    public List<String> codes;
    public List<RegionListBean> regionList;

    public static class RegionListBean implements IObfuscateKeepAll {
        /**
         * code : APP_M20_Pop_Screen
         * items : []
         */

        public String code;
        public List<Map<String, String>> items;
    }

    public boolean hasItems() {
        return null != regionList
                && !regionList.isEmpty()
                && null != regionList.get(0).items
                && !regionList.get(0).items.isEmpty();
    }
}
