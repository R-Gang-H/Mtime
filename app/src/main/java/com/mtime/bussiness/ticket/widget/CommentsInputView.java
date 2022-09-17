package com.mtime.bussiness.ticket.widget;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.common.utils.LogWriter;

/**
 * Created by LEE on 9/18/16.
 */
public class CommentsInputView extends RelativeLayout {
    public interface CommentsInputViewListener {
        void onEvent(final String content);
    }


    private CommentsInputViewListener listener;

    public CommentsInputView(Context context) {
        super(context);

        init(context);
    }

    private EditText input;
    private TextView send;
    public CommentsInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public CommentsInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(final Context context) {
        View root = LayoutInflater.from(context).inflate(R.layout.comments_input, null);
        input = root.findViewById(R.id.input);

        send = root.findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != input && null != input.getEditableText() && !TextUtils.isEmpty(input.getEditableText().toString()) && null != listener) {
                    LogWriter.e("checkComments", "发起通知调用");
                    listener.onEvent(input.getEditableText().toString());
                } else {
                    MToastUtils.showShortToast(R.string.st_actor_info_input_commentcontent);
//                    setFocus(true);
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (input.getEditableText().length() > 0) {
                    send.setEnabled(true);
                    send.setBackgroundResource(R.drawable.comments_send_enable);
                    send.setTextColor(ContextCompat.getColor(context,R.color.color_ff8600));
                } else {
                    send.setBackgroundResource(R.drawable.comments_send);
                    send.setTextColor(ContextCompat.getColor(context,R.color.color_888888));
                    send.setEnabled(false);
                }
            }
        });

        this.addView(root);
    }

    public void setListener(CommentsInputViewListener listener) {
        this.listener = listener;
    }

    public void setHints(final String hints) {
        if (null != input) {
            input.setHint(hints);
        }
    }

    public void setFocus(final boolean focus) {
        if (null != input) {
            if (focus) {
                input.requestFocus();

                InputMethodManager imm = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            } else {
                input.clearFocus();
                InputMethodManager imm = (InputMethodManager) input.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        }
    }

    public void clear() {
        if (null != input) {
            input.setText(null);
        }
    }
}
