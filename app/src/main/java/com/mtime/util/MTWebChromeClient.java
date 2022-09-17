package com.mtime.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.mtime.R;

public class MTWebChromeClient extends WebChromeClient {
    
    private final Context context;
    
    public MTWebChromeClient(Context context) {
        super();
        this.context = context;
    }
    
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        Builder builder = new Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
        return true;
    }
    
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        Builder builder = new Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
        return true;
    }
    
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        final LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.web_prom_dialog, null);
        ((TextView) dialogView.findViewById(R.id.TextView_PROM)).setText(defaultValue);
        ((EditText) dialogView.findViewById(R.id.EditText_PROM)).setText(defaultValue);
        Builder builder = new Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String value = ((EditText) dialogView.findViewById(R.id.EditText_PROM)).getText().toString();
                result.confirm(value);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        });
        builder.setOnCancelListener(new AlertDialog.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                result.cancel();
            }
        });
        builder.show();
        return true;
    }
    
}
