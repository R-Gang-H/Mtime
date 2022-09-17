package com.mtime.util;

import com.mtime.bussiness.ticket.movie.bean.RechargeBean;
import com.mtime.beans.URLData;
import com.mtime.common.utils.LogWriter;
import com.mtime.widgets.ParserInterface;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by yinguanping on 17/2/23.
 */

public class RechargeHandler implements ParserInterface {
    public static boolean getBoolean(final JSONObject o, final String name)
            throws JSONException {
        if (!o.isNull(name)) {
            return o.getBoolean(name);
        }
        return false;
    }

    public static int getInt(final JSONObject o, final String name)
            throws JSONException {
        if (!o.isNull(name)) {
            return o.getInt(name);
        }
        return 0;
    }

    public static long getLong(final JSONObject o, final String name)
            throws JSONException {
        if (!o.isNull(name)) {
            return o.getLong(name);
        }
        return 0l;
    }

    public static String getString(final JSONObject o, final String name)
            throws JSONException {
        if (!o.isNull(name)) {
            return o.getString(name);
        }
        return "";
    }

    @Override
    public Object handle(final String data, final URLData url) {
        if ((url != null) && (data != null)) {
            final RechargeBean bean = new RechargeBean();
            try {
                final JSONObject json = new JSONObject(data);
                bean.setSuccess(RechargeHandler.getBoolean(json, "success"));
                bean.setMessage(RechargeHandler.getString(json, "msg"));
                bean.setStatus(RechargeHandler.getInt(json, "status"));
                bean.setError(RechargeHandler.getString(json, "error"));
                bean.setRechargeNum(RechargeHandler.getString(json,
                        "rechargeNumber"));
                bean.setFromXML(RechargeHandler.getString(json, "formXML"));
                bean.setOrderId(RechargeHandler.getLong(json, "orderId"));
                bean.setSubOrderId(RechargeHandler.getLong(json, "subOrderId"));
                return bean;
            } catch (final JSONException e) {
                LogWriter.debugError("RechargeHandler :Parser Error! \r\n"
                        + e.toString());
            }

        }

        return null;
    }
}
