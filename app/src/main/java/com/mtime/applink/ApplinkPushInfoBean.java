package com.mtime.applink;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * http://apidocs.mt-dev.com/common-front-api/#api-%E6%B6%88%E6%81%AFapi-getPushMessage
 */
public class ApplinkPushInfoBean implements IObfuscateKeepAll {
    public String applinkData;
    public String reach_id;
    public boolean success;
    public String statisticsData;
}
