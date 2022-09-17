package com.mtime.event.entity;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by ZhouSuQiang on 2017/11/30.
 * 城市变更事件实体
 */

public class CityChangedEvent implements IObfuscateKeepAll{
    
    public String oldCityId;
    public String oldCityName;
    public String newCityId;
    public String newCityName;
    
    public CityChangedEvent(String oldCityId, String oldCityName, String newCityId, String newCityName) {
        this.oldCityId = oldCityId;
        this.oldCityName = oldCityName;
        this.newCityId = newCityId;
        this.newCityName = newCityName;
    }
}
