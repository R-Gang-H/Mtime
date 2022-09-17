package com.mtime.applink;

import org.json.JSONObject;

public interface ApplinkListener {
    boolean onJumpPage(String pageType, JSONObject json, String refer);
}
