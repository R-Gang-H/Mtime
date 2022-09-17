/**
 *
 */
package com.mtime.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtime.R;

/**
 * @author wangjin
 */
public class SecurityDlg extends Dialog {
    private TextView sendBtn;
    private EditText securityEdit;
    private TextView okBtn;
    private View backBtn;
    private TextView info;
    private int time = 60;
    private final Context context;
    
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            
            try {
                while (time > 0) {
                    Message msg = new Message();
                    msg.what = 1;
                    time--;
                    Bundle bundle = new Bundle();
                    bundle.putInt("time", time);
                    
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                    
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int times = msg.getData().getInt("time");
                if (times == 0) {
                    sendBtn.setClickable(true);
                    sendBtn.setEnabled(true);
                    sendBtn.setText("重新发送");
                    sendBtn.setTextColor(ContextCompat.getColor(context,
                            R.color.color_0075c4));
                    sendBtn.setBackgroundResource(R.drawable.register_get_signcode_icon);
                } else {
                    sendBtn.setTextColor(ContextCompat.getColor(context,
                            R.color.color_bbbbbb));
                    sendBtn.setBackgroundResource(R.drawable.bt_solid_gray_h66);
                    sendBtn.setClickable(false);
                    sendBtn.setEnabled(false);
                    sendBtn.setText(times + "秒后重发");
                }
            }
            
        }
        
    };
    
    public SecurityDlg(final Context context) {
        super(context, R.style.fullscreen_notitle_dialog);
        this.context = context;
    }
    
    public void setBtnOkClickListener(View.OnClickListener okClickListener) {
        if (okBtn != null) {
            okBtn.setOnClickListener(okClickListener);
        }
    }
    
    public void setBtnBackClickListener(View.OnClickListener backClickListener) {
        if (backBtn != null) {
            backBtn.setOnClickListener(backClickListener);
        }
    }
    
    public void setBtnSendClickListener(View.OnClickListener sendClickListener) {
        if (sendBtn != null) {
            sendBtn.setOnClickListener(sendClickListener);
        }
    }
    
    public TextView getSendBtn() {
        return sendBtn;
    }
    
    public void setSendBtn(TextView sendBtn) {
        this.sendBtn = sendBtn;
    }
    
    public EditText getSecurityEdit() {
        return securityEdit;
    }
    
    public void setSecurityEdit(EditText securityEdit) {
        this.securityEdit = securityEdit;
    }
    
    public TextView getOkBtn() {
        return okBtn;
    }
    
    public int getTime() {
        return time;
    }
    
    public void setTime(int time) {
        this.time = time;
    }
    
    public void setOkBtn(TextView okBtn) {
        this.okBtn = okBtn;
    }
    
    public TextView getInfo() {
        return info;
    }
    
    public void setInfo(TextView info) {
        this.info = info;
    }
    
    public void setThreadStart() {
        new Thread(runnable).start();
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_dlg);
        initTitle();
        sendBtn = findViewById(R.id.send_btn);
        okBtn = findViewById(R.id.btn_ok);
        info = findViewById(R.id.security_info);
        securityEdit = findViewById(R.id.security_edit);
    }
    
    private void initTitle() {
        findViewById(R.id.share).setVisibility(View.INVISIBLE);
        findViewById(R.id.favorite).setVisibility(View.INVISIBLE);
        findViewById(R.id.message).setVisibility(View.INVISIBLE);
        findViewById(R.id.feedbacklist).setVisibility(View.INVISIBLE);
        backBtn = findViewById(R.id.back);
    }
    
    public void setTitle(String titleString) {
        ((TextView) findViewById(R.id.title)).setText(titleString);
    }
    
    public void setTelText(String telString) {
        ((TextView) findViewById(R.id.security_dlg_tel)).setText(telString);
    }
    
}
