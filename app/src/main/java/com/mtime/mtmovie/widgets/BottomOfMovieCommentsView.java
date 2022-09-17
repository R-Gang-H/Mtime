package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.bussiness.mine.login.activity.LoginActivity;

public class BottomOfMovieCommentsView {

    public enum BottomViewActionType {
        TYPE_MOVIE_COMMENTS_SEND, TYPE_MOVIE_COMMENTS_HINT_CLICK,
    }

    public interface IBottomViewActListener {
        void onEvent(BottomViewActionType type, final String contents);
    }

    private final BaseActivity context;
    private final View root;
    private final View hintView;
    private final EditText comments;
    private final TextView send;
    private boolean hideAfterSend;
    private boolean showingKeyboard;
    private boolean sendClickToInvoker;

    public BottomOfMovieCommentsView(final BaseActivity context, final View root, final String contents,
                                     final IBottomViewActListener listener) {
        this.context = context;
        this.root = root;
        this.hideAfterSend = true;
        this.showingKeyboard = false;
        this.sendClickToInvoker = false;

        root.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    lostFocus();
                }
                return false;
            }
        });

        this.hintView = root.findViewById(R.id.hint_view);
        this.hintView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!UserManager.Companion.getInstance().isLogin()) {
//                    context.startActivityForResult(LoginActivity.class, new Intent());
                    UserLoginKt.gotoLoginPage(context, null, 0);
                    return;
                }

                listener.onEvent(BottomViewActionType.TYPE_MOVIE_COMMENTS_HINT_CLICK, null);
                if (sendClickToInvoker) {
                    if (null != listener) {
                        return;
                    }
                }
                hintView.setVisibility(View.INVISIBLE);
                comments.setVisibility(View.VISIBLE);
                comments.requestFocus();

                showingKeyboard = true;
                final InputMethodManager imm = (InputMethodManager) context.getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });
        // send comments button
        this.send = root.findViewById(R.id.send);
        this.send.setEnabled(false);
        send.setBackgroundResource(R.drawable.comments_send_enabled_rectangle);
        send.setTextColor(ContextCompat.getColor(context,R.color.color_888888));
        this.send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onEvent(BottomViewActionType.TYPE_MOVIE_COMMENTS_SEND, comments.getEditableText()
                            .toString());
                }

                lostFocus();
            }
        });

        // comments edit view
        this.comments = root.findViewById(R.id.comments);
        if (!TextUtils.isEmpty(contents)) {
            this.comments.setText(contents);
            send.setEnabled(true);
            send.setBackgroundResource(R.drawable.comments_send_rectangle);
            send.setTextColor(ContextCompat.getColor(context,R.color.color_0075c4));
        }

//        int maxLength = Integer.valueOf(context.getResources().getString(R.string.str_movie_comments_max_length));
//        this.comments.setMaxLines(maxLength);

        // press back key
        this.comments.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    lostFocus();
                }
                return false;
            }
        });

        // text changed
        this.comments.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (comments.getEditableText().length() > 0) {
                    send.setEnabled(true);
                    send.setBackgroundResource(R.drawable.comments_send_rectangle);
                    send.setTextColor(ContextCompat.getColor(context,R.color.color_0075c4));
                } else {
                    send.setBackgroundResource(R.drawable.comments_send_enabled_rectangle);
                    send.setTextColor(ContextCompat.getColor(context,R.color.color_888888));
                    send.setEnabled(false);
                }
            }
        });

        // click send with soft keyboard
        this.comments.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (!TextUtils.isEmpty(comments.getEditableText().toString()) && EditorInfo.IME_ACTION_SEND == actionId
                        && (null != event && KeyEvent.KEYCODE_ENTER == event.getKeyCode()) && null != listener) {
                    listener.onEvent(BottomViewActionType.TYPE_MOVIE_COMMENTS_SEND, comments.getEditableText()
                            .toString());

                    lostFocus();
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * only show/hide the part
     *
     * @param visible
     */
    public void setVisibility(final int visible) {
        if (this.root.getVisibility() == visible) {
            return;
        }

        this.root.setVisibility(visible);
    }

    /**
     * show the part and set focus to the comment edit view
     */
    public void setFocus() {
        // if user not login, show the login UI or goon?
        if (null == this.root) {
            return;
        }

        // show and request focus now.

        this.root.setVisibility(View.VISIBLE);
        this.hintView.setVisibility(View.INVISIBLE);
        this.comments.setVisibility(View.VISIBLE);
        this.comments.requestFocus();

        this.showingKeyboard = true;
        // show soft input method.
        final InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * clear the comments edit view and hide the part
     */
    public void lostFocus() {
        if (null == this.comments) {
            return;
        }

        this.showingKeyboard = false;
        final InputMethodManager imm = (InputMethodManager) this.context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.comments.getWindowToken(), 0);

        this.comments.clearFocus();
        this.comments.setText("");

        if (this.hideAfterSend) {
            this.comments.setVisibility(View.INVISIBLE);
            this.hintView.setVisibility(View.VISIBLE);
            this.root.setVisibility(View.INVISIBLE);
        }
    }

    public void setComments(final String comments) {
        if (null == this.comments || TextUtils.isEmpty(comments)) {
            return;
        }

        this.comments.setText(comments);
        this.comments.setSelection(comments.length());
    }

    public void setHideAfterSend(boolean isHide) {
        this.hideAfterSend = isHide;
    }

    public void hideKeyboard() {
        if (View.VISIBLE == this.root.getVisibility() && this.showingKeyboard) {
            final InputMethodManager imm = (InputMethodManager) this.context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.comments.getWindowToken(), 0);
        }
    }

    public void setMsgToInvoker(final boolean send) {
        this.sendClickToInvoker = send;
    }
}
