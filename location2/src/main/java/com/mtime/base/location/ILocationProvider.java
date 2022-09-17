package com.mtime.base.location;

import android.content.Context;

import java.util.Map;

/**
 * Created by mtime on 2017/11/2.
 */

public interface ILocationProvider {

    void initLocation(Context context, Map<String, Object> options);
    void startLocation(OnLocationProviderListener onLocationProviderListener);
    void refreshLocation();
    void stop();

    interface OnLocationProviderListener{
        void onProviderLocationSuccess(LocationInfo locationInfo);
        void onProviderException(LocationException locationException);
    }

}
