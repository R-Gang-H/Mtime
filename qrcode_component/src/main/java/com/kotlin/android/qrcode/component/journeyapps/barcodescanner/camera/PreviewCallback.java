package com.kotlin.android.qrcode.component.journeyapps.barcodescanner.camera;


import com.kotlin.android.qrcode.component.journeyapps.barcodescanner.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
