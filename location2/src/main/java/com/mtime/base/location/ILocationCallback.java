package com.mtime.base.location;

/**
 * Created by mtime on 2017/10/30.
 */

public interface ILocationCallback {
    void onStartLocation();
    void onLocationSuccess(LocationInfo locationInfo);
    void onLocationFailure(LocationException e);
}
