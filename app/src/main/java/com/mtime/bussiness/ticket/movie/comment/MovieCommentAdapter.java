//package com.mtime.bussiness.ticket.movie.comment;
//
//import android.content.Context;
//import androidx.annotation.NonNull;
//import android.text.Editable;
//import android.text.InputFilter;
//import android.text.Spanned;
//import android.text.TextWatcher;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.mtime.R;
//import com.mtime.base.imageload.ImageHelper;
//import com.mtime.base.utils.MScreenUtils;
//import com.mtime.base.utils.MToastUtils;
//import com.mtime.base.views.ForegroundImageView;
//import com.mtime.bussiness.ticket.movie.comment.bean.BaseCommentBean;
//import com.mtime.bussiness.ticket.movie.comment.bean.CommentImageBean;
//import com.mtime.bussiness.ticket.movie.comment.bean.CommentScoreBean;
//import com.mtime.bussiness.ticket.movie.comment.bean.CommentTextBean;
//import com.mtime.bussiness.ticket.movie.comment.bean.CommentTitleBean;
//import com.mtime.bussiness.ticket.movie.comment.bean.UnknownCommentBean;
//import com.mtime.bussiness.ticket.movie.comment.widget.CommentEditText;
//import com.mtime.bussiness.ticket.movie.comment.widget.MovieScoreSeekBar;
//import com.mtime.util.MtimeUtils;
//import com.woxthebox.draglistview.DragItemAdapter;
//
//import java.text.DecimalFormat;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
// * <p>
// * On 2019-05-21
// */
//class MovieCommentAdapter extends DragItemAdapter<BaseCommentBean, MovieCommentAdapter.BaseHolder> {
//
//    interface ActionCallback {
//        void scrollToPosition(int position);
//
//        void requestDeleteImage(CommentImageBean imageBean);
//
//        void onOriginalAgreementClick();
//
//        void onScorePanelOpen(boolean open);
//    }
//
//    private final Context mContext;
//    private final LayoutInflater mInflater;
//    private int mIdGenerator = 0;
//    private final InputMethodManager mInputManager;
//
//    private boolean mLongCommentMode = false; // 长影评模式
//    private final ActionCallback mCallback;
//
//    private TextHolder mLastFocus;
//
//    private TextHolder mFirstText;
//
//    private int mSelectedImage = -1;
//
//    MovieCommentAdapter(Context context, ActionCallback callback) {
//        mContext = context;
//        mInflater = LayoutInflater.from(context);
//        mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (mInputManager == null) {
//            throw new IllegalArgumentException("input method is null");
//        }
//        mCallback = callback;
//    }
//
//    int getLastFocusPosition() {
//        return mLastFocus == null ? 2 : getPositionForItemId(mLastFocus.mItemId);
//    }
//
//    int getLastFocusSelectionStart() {
//        return mLastFocus == null ? 0 : mLastFocus.selectionStart;
//    }
//
//    int getLastFocusSelectionEnd() {
//        return mLastFocus == null ? 0 : mLastFocus.selectionEnd;
//    }
//
//    void setLongCommentMode() {
//        mLongCommentMode = true;
//    }
//
//    @Override
//    public long getUniqueItemId(int position) {
//        BaseCommentBean bean = mItemList.get(position);
//        if (bean.hasId()) {
//            return bean.getId();
//        }
//        int id = ++mIdGenerator;
//        bean.setId(id);
//        return id;
//    }
//
//    int textLength() {
//        int len = 0;
//        for (BaseCommentBean bean : mItemList) {
//            len += bean.length();
//        }
//        return len;
//    }
//
//    void clearImageSelect() {
//        mSelectedImage = -1;
//    }
//
//    private void onItemTextChanged() {
//        if (mFirstText == null) {
//            return;
//        }
//        if (mFirstText.isFirstText()) {
//            if (textLength() == 0) {
//                mFirstText.mEditText.setHint(R.string.movie_comment_write_your_comment);
//            } else {
//                mFirstText.mEditText.setHint("");
//            }
//        }
//    }
//
//    // <editor-fold defaultstate="collapsed" desc="生命周期处理">
//    @Override
//    public void onViewAttachedToWindow(@NonNull BaseHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        holder.onAttached();
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(@NonNull BaseHolder holder) {
//        super.onViewDetachedFromWindow(holder);
//        holder.onDetached();
//    }
//
//    @Override
//    public void onViewRecycled(@NonNull BaseHolder holder) {
//        super.onViewRecycled(holder);
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="通用处理">
//    @Override
//    public int getItemViewType(int position) {
//        BaseCommentBean baseCommentBean = mItemList.get(position);
//        return baseCommentBean.getType();
//    }
//
//    @NonNull
//    @Override
//    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
//        switch (viewType) {
//            case BaseCommentBean.TYPE_SCORE:
//                view = mInflater.inflate(R.layout.layout_movie_comment_score_item, parent, false);
//                return new ScoreHolder(view);
//            case BaseCommentBean.TYPE_TITLE:
//                view = mInflater.inflate(R.layout.layout_long_movie_comment_title_item, parent, false);
//                return new TitleHolder(view);
//            case BaseCommentBean.TYPE_TEXT:
//                view = mInflater.inflate(R.layout.layout_movie_comment_txt_item, parent, false);
//                return new TextHolder(view);
//            case BaseCommentBean.TYPE_IMAGE:
//                view = mInflater.inflate(R.layout.layout_movie_comment_img_item, parent, false);
//                return new ImageHolder(view);
//            case BaseCommentBean.TYPE_UNKNOWN:
//            default:
//                view = mInflater.inflate(R.layout.layout_movie_comment_unknown_type_item, parent, false);
//                return new UnknownTypeHolder(view);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//        if (holder instanceof UnknownTypeHolder ||
//                holder instanceof TitleHolder ||
//                holder instanceof TextHolder) {
//            // 关闭拖拽
//            holder.setDragStartCallback(null);
//        }
//        holder.bind();
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="图片 item">
//
//    /**
//     * 图片 item
//     */
//    class ImageHolder extends BaseHolder<CommentImageBean> implements CommentImageBean.DownloadCallback {
//
//        @BindView(R.id.movie_comment_img_iv)
//        ForegroundImageView mImageView;
//        @BindView(R.id.movie_comment_img_delete_iv)
//        View mDelete;
//
//        @BindView(R.id.movie_comment_img_move_handle_iv)
//        View mEditHandle;
//        @BindView(R.id.movie_comment_img_desc_et)
//        EditText mImageDescEt;
//
//        private final int mImageWidth;
//
//        ImageHolder(View itemView) {
//            super(itemView, R.id.movie_comment_img_move_handle_iv);
//            ButterKnife.bind(this, itemView);
//            mImageWidth = MScreenUtils.getScreenWidth() - MScreenUtils.dp2px(30);
//        }
//
//        @Override
//        void bind() {
//
//            loadImageFile();
//
////            if (mSelectedImage == getPositionForItemId(mItemId)) {
//            mDelete.setVisibility(View.VISIBLE);
////            } else {
////                mDelete.setVisibility(View.GONE);
////            }
//
//        }
//
//        @Override
//        public void onItemClicked(View view) {
//            super.onItemClicked(view);
////            int position = getPositionForItemId(mItemId);
////            if (position == mSelectedImage) {
////                clearImageSelect();
////            } else {
////                mSelectedImage = position;
////            }
////            notifyDataSetChanged();
//        }
//
//        private void loadImageFile() {
//            CommentImageBean imageBean = getBean();
//            handleImageHeight(imageBean);
//
//            imageBean.setDownloadCallback(null);
//            if (imageBean.pathOrUrl.startsWith("/")) {
//                ImageHelper.with(mContext)
//                        .load(imageBean.pathOrUrl)
//                        .view(mImageView)
//                        .placeholder(R.drawable.default_image)
//                        .showload();
//            } else {
//                mImageView.setImageResource(R.drawable.default_image);
//                imageBean.setDownloadCallback(this);
//                imageBean.downloadImage();
//            }
//        }
//
//        @OnClick(R.id.movie_comment_img_delete_iv)
//        void onDeleteClick() {
//            mCallback.requestDeleteImage(getBean());
//        }
//
//        private void handleImageHeight(CommentImageBean bean) {
//            if (bean.width <= 0) {
//                return;
//            }
//            ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
//            if (lp.height > 0) {
//                float source = lp.width / (float) lp.height;
//                float target = bean.width / (float) bean.height;
//                if (source == target) {
//                    return;
//                }
//            }
//
//            lp.height = (int) (mImageWidth * bean.height / (float) bean.width);
//            mImageView.setLayoutParams(lp);
//        }
//
//        @Override
//        public void onDownloadComplete() {
//            loadImageFile();
//        }
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="文字 item">
//
//    /**
//     * 文字 item
//     */
//    class TextHolder extends BaseHolder<CommentTextBean> implements TextWatcher, View.OnKeyListener,
//            View.OnFocusChangeListener, CommentEditText.OnSelectionChangedListener {
//
//        CommentEditText mEditText;
//
//        private int selectionStart = -1;
//        private int selectionEnd = -1;
//
//        TextHolder(View itemView) {
//            super(itemView, R.id.movie_comment_edit_et);
//            mEditText = itemView.findViewById(R.id.movie_comment_edit_et);
//            mEditText.addTextChangedListener(this);
//            mEditText.setOnSelectionChangedListener(this);
//
//            InputFilter[] oldFilters = mEditText.getFilters();
//
//            int len = oldFilters.length;
//
//            InputFilter[] filters = new InputFilter[len + 1];
//
//            System.arraycopy(oldFilters, 0, filters, 0, len);
//
//            filters[len] = (source, start, end, dest, dstart, dend) -> {
//                if (!mLongCommentMode) { // 短评模式 支持控件内 换行
//                    return null;
//                }
//                if (source.length() == 1 && source.charAt(0) == '\n') {
//                    // 长影评不支持控件内换行，回车需要额外处理
//                    onClickEnter(dest, dstart, dend);
//                    return "";
//                }
//                return null;
//            };
//            mEditText.setFilters(filters);
//
//            mEditText.setOnKeyListener(this);
//
//            mEditText.setOnFocusChangeListener(this);
//
//        }
//
//        @Override
//        void bind() {
//            CommentTextBean bean = getBean();
//            mEditText.setText(bean.text);
//            if (bean.showInput) {
//                bean.showInput = false;
//                mEditText.requestFocus();
//                mInputManager.showSoftInput(mEditText, 0);
//            }
//            if (bean.selection >= 0 && bean.selection <= mEditText.getText().length()) {
//                mEditText.setSelection(bean.selection);
//                bean.selection = -1;
//            }
//            if (!mLongCommentMode) {
//                return;
//            }
//            if (isFirstText()) { // 第一个 文本输入框
//                mFirstText = this;
//                if (textLength() == 0 || mEditText.getText().length() != 0) {
//                    mEditText.setHint(R.string.movie_comment_write_your_comment);
//                } else {
//                    mEditText.setHint("");
//                }
//            } else {
//                mEditText.setHint("");
//            }
//        }
//
//        private boolean isFirstText() {
//            int N = getItemCount();
//            int firstTextId = 0;
//            for (int i = 0; i < N; ++i) {
//                BaseCommentBean bean = mItemList.get(i);
//                if (bean.getType() == BaseCommentBean.TYPE_TEXT) {
//                    firstTextId = bean.getId();
//                    break;
//                }
//            }
//            return firstTextId == mItemId;
//        }
//
//        private boolean mClickEnter = false;
//
//        private void onClickEnter(Spanned dest, int dstart, int dend) {
//            if (dend - dstart == 0) {
//                if (dest.length() == dstart) { // 文字末尾
//                    addNewText("", true);
//                } else { // 文字中间
//                    CharSequence preSub = dest.subSequence(0, dstart);
//                    getBean().text = preSub.toString();
//                    addNewText(dest.subSequence(dstart, dest.length()).toString(), false);
//                }
//                return;
//            }
//            mClickEnter = true;
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            recordSelection();
//
//            if (mOnTextChangedListener != null) {
//                mOnTextChangedListener.onTextChanged(s.length());
//            }
//
//            CommentTextBean textBean = getBean();
//
//            if (mClickEnter) {
//                mClickEnter = false;
//                int start = mEditText.getSelectionStart();
//                if (start < 0) {
//                    return;
//                }
//                CharSequence preSub = s.subSequence(0, start);
//                textBean.text = preSub.toString();
//                addNewText(s.subSequence(start, s.length()).toString(), false);
//            } else {
//                textBean.text = s.toString();
//            }
//
//            onItemTextChanged();
//        }
//
//        private void addNewText(String text, boolean end) {
//            CommentTextBean textBean = new CommentTextBean(text);
//            textBean.selection = end ? text.length() : 0;
//            textBean.showInput = true;
//            int index = getPositionForItemId(mItemId) + 1;
//            mItemList.add(index, textBean);
//            notifyDataSetChanged();
//
//            mCallback.scrollToPosition(index);
//        }
//
//        private void onClickDelete() {
//            int position = getPositionForItemId(mItemId);
//            CommentTextBean thatText = getBean();
//            int prePosition = position - 1;
//            BaseCommentBean preBean = mItemList.get(prePosition);
//            int preType = preBean.getType();
//            if (preType == BaseCommentBean.TYPE_TEXT) {
//                mItemList.remove(position);
//                CommentTextBean preTextBean = (CommentTextBean) preBean;
//                int selection = preTextBean.text == null ? 0 : preTextBean.text.length();
//                contactText(preTextBean, thatText);
//                preTextBean.showInput = true;
//                preTextBean.selection = selection;
//            }
//            // 可能需要处理未知类型的 item
//            notifyDataSetChanged();
//
//        }
//
//        private void contactText(CommentTextBean t1, CommentTextBean t2) {
//            t1.text = t1.text == null ? "" : t1.text;
//            if (t2.text != null) {
//                t1.text += t2.text;
//            }
//        }
//
//        @Override
//        public boolean onKey(View v, int keyCode, KeyEvent event) {
//            if (!mLongCommentMode) {
//                return false;
//            }
//            if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                if (keyCode == KeyEvent.KEYCODE_DEL) {
//                    int selectionStart = mEditText.getSelectionStart();
//                    int selectionEnd = mEditText.getSelectionEnd();
//                    if (selectionStart == 0 && selectionEnd == selectionStart) {
//                        onClickDelete();
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if (hasFocus) {
//                mCallback.scrollToPosition(getPositionForItemId(mItemId));
//                mLastFocus = this;
//
//                recordSelection();
//            }
//        }
//
//        private void recordSelection() {
//            selectionStart = mEditText.getSelectionStart();
//            selectionEnd = mEditText.getSelectionEnd();
//        }
//
//        @Override
//        void onDetached() {
//            if (mInputManager.isActive(mEditText)) { // view 已经被移除 但 输入法仍然在显示，则隐藏输入法
//                mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
//            }
//        }
//
//        @Override
//        public void onItemClicked(View view) {
//            super.onItemClicked(view);
//            mEditText.requestFocus();
//            mEditText.setSelection(mEditText.getText().length());
//            mInputManager.showSoftInput(mEditText, 0);
//        }
//
//        @Override
//        public void onSelectionChanged(View v, int selStart, int selEnd) {
//            if (v.hasFocus()) {
//                selectionStart = selStart;
//                selectionEnd = selEnd;
//            }
//        }
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="长影评 title">
//
//    /**
//     * 长影评 title
//     */
//    class TitleHolder extends BaseHolder<CommentTitleBean> implements CompoundButton.OnCheckedChangeListener, TextWatcher {
//
//        @BindView(R.id.movie_comment_title_et)
//        EditText mTitleEt;
//        @BindView(R.id.movie_comment_is_my_original_cb)
//        CheckBox mOriginalCb; // 原创
//        @BindView(R.id.movie_comment_contains_movie_content_cb)
//        CheckBox mContainMovieContentCb; // 剧透
//
//        TitleHolder(View itemView) {
//            super(itemView, R.id.center_divider);
//            ButterKnife.bind(this, itemView);
//
//            mOriginalCb.setOnCheckedChangeListener(this);
//            mContainMovieContentCb.setOnCheckedChangeListener(this);
//
//            mTitleEt.addTextChangedListener(this);
//
//            InputFilter[] oldFilters = mTitleEt.getFilters();
//
//
//            int oldLen = oldFilters.length;
//            InputFilter[] filters = new InputFilter[oldLen + 1];
//
//            System.arraycopy(oldFilters, 0, filters, 0, oldLen);
//
//            filters[oldLen] = new InputFilter() {
//
//                // 标题最多100个字
//                final InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(100);
//
//                @Override
//                public CharSequence filter(CharSequence source, int start, int end,
//                                           Spanned dest, int dstart, int dend) {
//                    CharSequence cs = lengthFilter.filter(source, start, end, dest, dstart, dend);
//                    if (cs != null) {
//                        MToastUtils.showShortToast(R.string.mobie_comment_title_max_len_hint);
//                    }
//                    return cs;
//                }
//            };
//
//            mTitleEt.setFilters(filters);
//        }
//
//        @OnClick(R.id.movie_comment_original_declare_tv)
//        void onClickOriginalAgreement() { // 原创声明点击事件
//            mCallback.onOriginalAgreementClick();
//        }
//
//        @Override
//        void bind() {
//            CommentTitleBean bean = getBean();
//
//            mTitleEt.setText(bean.title);
//
//            mOriginalCb.setChecked(bean.original);
//            mContainMovieContentCb.setChecked(bean.containMovieContent);
//        }
//
//        @Override
//        public void onCheckedChanged(CompoundButton v, boolean isChecked) {
//            CommentTitleBean bean = getBean();
//            if (v == mOriginalCb) {
//                bean.original = isChecked;
//            } else {
//                bean.containMovieContent = isChecked;
//            }
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            CommentTitleBean bean = getBean();
//            bean.title = s.toString();
//        }
//
//        @Override
//        void onDetached() {
//            if (mInputManager.isActive(mTitleEt)) { // view 已经被移除 但 输入法仍然在显示，则隐藏输入法
//                mInputManager.hideSoftInputFromWindow(mTitleEt.getWindowToken(), 0);
//            }
//        }
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="评分 item">
//
//    /**
//     * 顶部 评分 item
//     */
//    class ScoreHolder extends BaseHolder<CommentScoreBean> implements MovieScoreSeekBar.OnSeekBarChangeListener {
//
//        private final DecimalFormat format = new DecimalFormat("0.0");
//        private static final String TEN = " 10 ";
//
//        @BindView(R.id.item_movie_score_tv)
//        TextView mScoreShowTv;
//        @BindView(R.id.item_movie_score_desc_tv)
//        TextView mSelectScoreTv;
//        @BindView(R.id.item_movie_whole_score_bar)
//        MovieScoreSeekBar mWholeScoreSb;
//        @BindView(R.id.item_movie_detail_score_bars_expand_tv)
//        TextView mExpandDetailScoreTv;
//
//        @BindView(R.id.item_movie_detail_score_bars_panel)
//        View mDetailScorePanel;
//
//        @BindView(R.id.item_movie_score_music_bar)
//        MovieScoreSeekBar mMusicScoreSb; // 音乐
//        @BindView(R.id.item_movie_score_picture_bar)
//        MovieScoreSeekBar mPictureScoreSb; // 画面
//        @BindView(R.id.item_movie_score_director_bar)
//        MovieScoreSeekBar mDirectorScoreSb; // 导演
//        @BindView(R.id.item_movie_score_story_bar)
//        MovieScoreSeekBar mStoryScoreSb; // 故事
//        @BindView(R.id.item_movie_score_acting_bar)
//        MovieScoreSeekBar mActingScoreSb; // 表演
//        @BindView(R.id.item_movie_score_impressions_bar)
//        MovieScoreSeekBar mImpressionsScoreSb; // 印象
//
//        ScoreHolder(View itemView) {
//            super(itemView, R.id.item_movie_score_tv);
//            ButterKnife.bind(this, itemView);
//            mWholeScoreSb.setOnSeekBarChangeListener(this);
//
//            mMusicScoreSb.setOnSeekBarChangeListener(this);
//            mPictureScoreSb.setOnSeekBarChangeListener(this);
//            mDirectorScoreSb.setOnSeekBarChangeListener(this);
//            mStoryScoreSb.setOnSeekBarChangeListener(this);
//            mActingScoreSb.setOnSeekBarChangeListener(this);
//            mImpressionsScoreSb.setOnSeekBarChangeListener(this);
//        }
//
//        @Override
//        void bind() {
//            CommentScoreBean bean = getBean();
//            handleWholeScore(bean.getWholeScore());
//            handleDetailPanel(bean.expandDetailPanel);
//
//            mMusicScoreSb.setScore((int) bean.musicScore);
//            mPictureScoreSb.setScore((int) bean.pictureScore);
//            mDirectorScoreSb.setScore((int) bean.directorScore);
//            mStoryScoreSb.setScore((int) bean.storyScore);
//            mActingScoreSb.setScore((int) bean.actingScore);
//            mImpressionsScoreSb.setScore((int) bean.impressionsScore);
//        }
//
//        private void handleWholeScore(float score) {
//            if (score >= 10) {
//                mScoreShowTv.setText(TEN);
//                mWholeScoreSb.setScore(10);
//            } else {
//                mWholeScoreSb.setScore((int) Math.floor(score));
//                mScoreShowTv.setText(String.format(" %s ", format.format(score)));
//            }
//
//            boolean enable = score > 0;
//            mScoreShowTv.setEnabled(enable);
//            mSelectScoreTv.setEnabled(enable);
//
//            if (score <= 0) {
//                mSelectScoreTv.setText(R.string.movie_comment_select_movie_score_hint);
//            } else {
//                mSelectScoreTv.setText(MtimeUtils.getDefaultScoreContent(score));
//            }
//        }
//
//        private void handleDetailPanel(boolean expand) {
//            if (mCallback != null) {
//                mCallback.onScorePanelOpen(expand);
//            }
//            if (expand) {
//                mExpandDetailScoreTv.setText(R.string.st_rate_deploy_subitem_close);
//                mExpandDetailScoreTv.setCompoundDrawablesWithIntrinsicBounds(0,
//                        0, R.drawable.common_icon_up_arrow, 0);
//                mDetailScorePanel.setVisibility(View.VISIBLE);
//                mWholeScoreSb.setVisibility(View.GONE);
//            } else {
//                mExpandDetailScoreTv.setText(R.string.st_rate_deploy_subitem);
//                mExpandDetailScoreTv.setCompoundDrawablesWithIntrinsicBounds(0,
//                        0, R.drawable.common_icon_down_arrow, 0);
//                mDetailScorePanel.setVisibility(View.GONE);
//                mWholeScoreSb.setVisibility(View.VISIBLE);
//            }
//        }
//
//        @OnClick(R.id.item_movie_detail_score_bars_expand_tv)
//        void onExpandClick() {
//            CommentScoreBean scoreBean = getBean();
//            boolean expand = !scoreBean.expandDetailPanel;
//            scoreBean.expandDetailPanel = expand;
//            handleDetailPanel(expand);
//        }
//
//        @Override
//        public void onProgressChanged(MovieScoreSeekBar seekBar, int score, boolean fromUser) {
//            if (!fromUser) {
//                return;
//            }
//            CommentScoreBean bean = getBean();
//            if (seekBar == mWholeScoreSb) {
//                bean.useWholeScore = true;
//                bean.wholeScore = score;
//                handleWholeScore(score);
//
//                mMusicScoreSb.setScore(0);
//                mPictureScoreSb.setScore(0);
//                mDirectorScoreSb.setScore(0);
//                mStoryScoreSb.setScore(0);
//                mActingScoreSb.setScore(0);
//                mImpressionsScoreSb.setScore(0);
//
//                bean.musicScore = 0;
//                bean.pictureScore = 0;
//                bean.directorScore = 0;
//                bean.storyScore = 0;
//                bean.actingScore = 0;
//                bean.impressionsScore = 0;
//            } else {
//                computeWholeScore(bean, seekBar.getId(), score);
//            }
//        }
//
//        private void computeWholeScore(CommentScoreBean bean, int id, int score) {
//            switch (id) {
//                case R.id.item_movie_score_music_bar:
//                    bean.musicScore = score;
//                    break;
//                case R.id.item_movie_score_picture_bar:
//                    bean.pictureScore = score;
//                    break;
//                case R.id.item_movie_score_director_bar:
//                    bean.directorScore = score;
//                    break;
//                case R.id.item_movie_score_story_bar:
//                    bean.storyScore = score;
//                    break;
//                case R.id.item_movie_score_acting_bar:
//                    bean.actingScore = score;
//                    break;
//                case R.id.item_movie_score_impressions_bar:
//                    bean.impressionsScore = score;
//                    break;
//                default:
//                    return;
//            }
//            bean.useWholeScore = false;
//
//            handleWholeScore(bean.getWholeScore());
//        }
//
//        @Override
//        public void onStartTrackingTouch(MovieScoreSeekBar seekBar) {
//        }
//
//        @Override
//        public void onStopTrackingTouch(MovieScoreSeekBar seekBar) {
//        }
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="未知类型">
//
//    /**
//     * 未知类型
//     */
//    class UnknownTypeHolder extends BaseHolder<UnknownCommentBean> {
//
//        UnknownTypeHolder(View itemView) {
//            super(itemView, R.id.placeholder_id);
//        }
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="holder 基类">
//
//    /**
//     * 基类
//     */
//    class BaseHolder<B extends BaseCommentBean> extends DragItemAdapter.ViewHolder {
//
//        BaseHolder(View itemView, int handleResId) {
//            super(itemView, handleResId, true);
//        }
//
//        @SuppressWarnings("unchecked")
//        final B getBean() {
//            int position = getPositionForItemId(mItemId);
//            return (B) mItemList.get(position);
//        }
//
//        void onDetached() {
//        }
//
//        void onAttached() {
//        }
//
//        void bind() {
//        }
//    }
//    // </editor-fold>
//
//    // <editor-fold defaultstate="collapsed" desc="文本变化回调">
//    interface OnTextChangedListener {
//        void onTextChanged(int length);
//    }
//
//    private OnTextChangedListener mOnTextChangedListener;
//
//    void setOnTextChangedListener(OnTextChangedListener l) {
//        mOnTextChangedListener = l;
//    }
//    // </editor-fold>
//}
