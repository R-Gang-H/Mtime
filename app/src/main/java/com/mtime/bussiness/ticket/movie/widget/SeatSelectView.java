package com.mtime.bussiness.ticket.movie.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.Seat;
import com.mtime.bussiness.ticket.movie.bean.SeatRowNameInfo;
import com.mtime.bussiness.ticket.movie.bean.SeatInfo;
import com.mtime.common.utils.LogWriter;
import com.mtime.mtmovie.widgets.ISeatSelectInterface;
import com.mtime.bussiness.ticket.movie.SeatManager;
import com.mtime.bussiness.ticket.movie.bean.ImagePiece;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectView extends SurfaceView {

    // distance between two seas or two lines
    private final int DISTANCE_HORIZONAL = 20;
    private final int DISTANCE_VERTICAL = 20;
//    private final int             SEATS_NAME_LIST_LEFT_DISTANCE = 20;

    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_DOWN = 1;
    private static final int TOUCH_STATE_MOVIE = 2;
    private static final int TOUCH_STATE_MULIT = 3;

    private final int frame = 50;
    private int times = 10;

    private final Handler mHandler = new Handler();

    private SurfaceHolder mSurfaceHolder = null;

    private int imgH;
    private int imgW;
    private Paint paint = null;

    // seats icon.
    private Bitmap seatNormal;
    private Bitmap seatNormalSelect;
    private Bitmap seatNormalGray;
    private List<ImagePiece> seatIconNet = new ArrayList<ImagePiece>();
    // couple seats, not allow to select one
    private Bitmap seatLeft;
    private Bitmap seatLeftSelect;
    private Bitmap seatLeftGray;
    private Bitmap seatRight;
    private Bitmap seatRightSelect;
    private Bitmap seatRightGray;

    // 座位图的最大横向和纵向座位数量
    private int maxX = 0;
    private int maxY = 0;
    // seat manager and informations
    private final SeatManager seatManager = new SeatManager();
    private List<Seat> seats;
    public SeatInfo[] allSeats;

    private float posX;
    private float posY;
    private float oriDownX;
    private float oriDownY;
    private int touchState = TOUCH_STATE_NONE;

    // the seat icon's width, height;
    private int height;
    private int width;
    private int seatNameListWidth;

    private int coupleHeight;

    private int offsetX = 0;
    private int offsetY = 0;
    private int totalWidth = 0;
    private int totalHeight = 0;

    private Matrix matrix;

    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float lastScale = 1.0f;
    private float minScale = 1.0f;
    private float distance = 0.0f;
    private boolean isFirst = true;
    private List<SeatRowNameInfo> rowNameList;

    private ISeatSelectInterface seatSelectedInterface;

    private Matrix thumMatrix;
    private SeatThumView thumbView = null;
    private final float thumbWidth = 231;
    private float thumbHeight = 1; //0的话有可能崩

    /**
     * 缩略图画布
     */
    private Canvas thumbCanvas = null;
    /**
     * 选座缩略图
     */
    private Bitmap bmpThumb = null;
    /**
     * 是否显示缩略图
     */
    private boolean isNeedThumImg = false;

    private float thumMinScale;

    private Callback surfaceCallback;
    private Runnable mDrawRun;

    private Activity activity;

    public void setContext(Activity activity) {
        this.activity = activity;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setScale(float x, float y) {
        scaleX = x;
        scaleY = y;
        LogWriter.e("checkScale", "setscale:scaleX:" + scaleX + ", scaleY:" + scaleY);
        matrix.setScale(scaleX, scaleY);

        offsetX = 0;
        offsetY = 0;
    }

    public float getCurrentScaleX() {
        return scaleX;
    }

    public float getCurrentScaleY() {
        return scaleY;
    }


    public void setSeatInterface(ISeatSelectInterface seatInterface) {
        this.seatSelectedInterface = seatInterface;
        if (seatManager != null) {
            seatManager.setSeatInterface(seatSelectedInterface);
        }
    }

    public SeatManager getSeatManager() {
        return seatManager;
    }

    public void setData(final List<Seat> data, List<SeatRowNameInfo> rowList, int limitCount, int rows, int columns) {
        LogWriter.e("checkScale", "setdata:");
        rowNameList = rowList;
        seats = data;
        offsetX = 0;
        offsetY = 0;

        getMaxXY();
        initSeatMap();

        seatManager.initSeats(seats, limitCount, rows, columns);
        allSeats = seatManager.getAllSeats();
    }

    public void setSeatIcon(InputStream inStream, ImageView seattip_selected_byself, ImageView seattip_selected_byother) {
        if (inStream != null) {
            Bitmap seatSelectNetListBitmap = BitmapFactory.decodeStream(inStream);
            seatIconNet.clear();
            seatIconNet = ImageSplitter.split(activity, seatSelectNetListBitmap, 9, 1);
            seatManager.setNetSeatIcons(seatIconNet);
            LogWriter.e("mylog", "size:" + seatIconNet.size());
            seatNormalGray = seatIconNet.get(4).bitmap;
            seatLeftSelect = seatIconNet.get(5).bitmap;
            seatRightSelect = seatIconNet.get(6).bitmap;
            seatLeftGray = seatIconNet.get(7).bitmap;
            seatRightGray = seatIconNet.get(8).bitmap;
            seattip_selected_byself.setImageBitmap(seatIconNet.get(0).bitmap);
            seattip_selected_byother.setImageBitmap(seatNormalGray);
        }
    }

    public void refreshData(final List<Seat> data) {
        seats = data;
    }

    /**
     * @param context
     */
    public SeatSelectView(final Context context) {
        super(context);

        this.initEvent();
        this.initView(context);
    }

    public SeatSelectView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        this.initEvent();
        this.initView(context);
    }

    private void initEvent() {
        surfaceCallback = new Callback() {
            @Override
            public void surfaceDestroyed(final SurfaceHolder holder) {
                mHandler.removeCallbacks(mDrawRun);
                System.gc();
            }

            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                mHandler.postDelayed(mDrawRun, frame);
            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
                imgW = width;
                imgH = height;
                if (totalWidth < imgW) {
                    totalWidth = imgW;
                }
                if (totalHeight < imgH) {
                    totalHeight = imgH;
                }

                float scaleW = (float) imgW / (float) totalWidth;
                float scaleH = (float) imgH / (float) totalHeight;
                minScale = Math.min(scaleW, scaleH);
//                Log.d("surfacechanged:checkseat", "scale:" + minScale);
                LogWriter.e("checkScale", "surfacechanged:scaleX:" + scaleX + ", scaleY:" + scaleY);
                LogWriter.e("checkScale", "surfacechanged:scaleW:" + scaleW + ", scaleH:" + scaleH + ", min:" + minScale);
                scaleX = minScale;
                scaleY = minScale;
                matrix.setScale(scaleX, scaleY);
            }
        };

        //
        mDrawRun = new Runnable() {
            @Override
            public void run() {
                Canvas canvas = null;
                try {
                    synchronized (mSurfaceHolder) {
                        canvas = mSurfaceHolder.lockCanvas();
                        doDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                mHandler.removeCallbacks(mDrawRun);
                mHandler.postDelayed(mDrawRun, frame);
            }
        };
    }

    private void initView(final Context c) {

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(surfaceCallback);
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);

        setFocusable(true);
        setClickable(true);
        setLongClickable(false);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(40);

        loadBmps();

        this.matrix = new Matrix();
        this.matrix.setScale(scaleX, scaleY);

        this.thumMatrix = new Matrix();

        this.setZOrderOnTop(false);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    private void doDraw(final Canvas canvas) {
        if (null == mSurfaceHolder || null == canvas || null == allSeats) {
            return;
        }

        // initialize the thumb view
        this.bmpThumb = Bitmap.createBitmap((int) thumbWidth, (int) thumbHeight, Bitmap.Config.ARGB_8888);
        this.thumbCanvas = new Canvas();
        thumbCanvas.clipRect(0, 0, thumbWidth, thumbHeight);
        thumbCanvas.drawRect(0, 0, totalWidth, totalHeight, paint);
        thumbCanvas.setBitmap(this.bmpThumb);
        thumbCanvas.save();

        thumbCanvas.setMatrix(thumMatrix);

        // prepare the canvas to draw bitmap.
        paint.setColor(getResources().getColor(R.color.color_ebebeb));
        canvas.clipRect(0, 0, this.getWidth(), this.getHeight());
        canvas.drawRect(0, 0, totalWidth, totalHeight, paint);
        canvas.save();

        matrix.setScale(scaleX, scaleY);
        canvas.setMatrix(matrix);

        paint.setColor(Color.BLACK);// 座位上文字的颜色
        for (SeatInfo s : allSeats) {
            drawSeats(canvas, s);
            drawThumSeats(thumbCanvas, s);
        }
        if (maxX > 0) {
            // get the middle position.
            // +2 是+1+1 第一次+1是因为从0开始，第二次是因为13行的时候要求在7和8之间加线
            int midX = (maxX + 2) >> 1;
            int midY = (maxY + 2) >> 1;

            int x = offsetX + seatNameListWidth + (width + DISTANCE_HORIZONAL) * midX + DISTANCE_HORIZONAL / 2;
            int y = offsetY + (this.height + DISTANCE_VERTICAL) * midY + (DISTANCE_VERTICAL / 2);

            paint.setStrokeWidth((float) 2.0);
            paint.setStyle(Paint.Style.STROKE);
            PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
            paint.setPathEffect(effects);
            paint.setColor(getResources().getColor(R.color.color_999999));
            // from top to bottom, left to right.
            canvas.drawLine(x, 0, x, totalHeight, paint);
            canvas.drawLine(0, y, totalWidth, y, paint);

            // 左边的数字栏
            if (null == rowNameList || rowNameList.size() < 1) {
                return;
            }

            final FontMetrics f = this.paint.getFontMetrics();
            final int fontHeight = (int) (Math.ceil(-f.top + f.descent));
            final int top = (int) (Math.abs(f.top));
            paint.setStyle(Paint.Style.FILL);
            paint.setPathEffect(null);
            paint.setColor(getResources().getColor(R.color.color_30333b));
            RectF round = new RectF(0, 0, seatNameListWidth, totalHeight);
            canvas.drawRoundRect(round, 50, 25, paint);

            for (final SeatRowNameInfo seatRow : rowNameList) {
                paint.setColor(Color.WHITE);
                int i = seatRow.getRowId();
                final int y1 = (this.height >> 1) + offsetY + height * i + DISTANCE_VERTICAL * (i + 1) + top
                        - (fontHeight >> 1);
                canvas.drawText(seatRow.getRowName(), (seatNameListWidth >> 1), y1, paint);
            }
        }

        // draw thumb view
        if (this.isNeedThumImg) {
            this.thumbView.setVisibility(View.VISIBLE);
            Paint localPaint2 = new Paint();
            // 画缩略图的红色框
            localPaint2.setColor(Color.RED);
            localPaint2.setStyle(Paint.Style.STROKE);
            localPaint2.setStrokeWidth(10);

            float left = -offsetX;
            float top = -offsetY;
            float right = this.getWidth() / scaleX - offsetX;
            float bottom = this.getHeight() / scaleY - offsetY;

            right = right > (totalWidth - DISTANCE_HORIZONAL - seatNameListWidth) ? (totalWidth - DISTANCE_HORIZONAL - seatNameListWidth) : right;
            bottom = bottom > (totalHeight - DISTANCE_VERTICAL) ? (totalHeight - DISTANCE_VERTICAL) : bottom;

            thumbCanvas.drawRect(left, top, right, bottom, localPaint2);
            thumbCanvas.save();

            this.thumbCanvas.restore();
            if (this.thumbView != null) {
                this.thumbView.setBitmap(bmpThumb);
                this.thumbView.invalidate();
            }
        } else {
            if (this.thumbView != null) {
                this.thumbView.setVisibility(View.INVISIBLE);
            }
        }

        canvas.save();
        canvas.restore();

        thumbCanvas.save();
        thumbCanvas.restore();
    }

    /**
     * 绘制座位图
     */
    private void drawSeats(final Canvas canvas, final SeatInfo s) {
        if (null == canvas || null == s) {
            return;
        }

        switch (s.getType()) {
            case SeatManager.TYPE_NORMAL:// 普通座位
                drawCommonSeat(canvas, s, s.getStatus());
                break;
            case SeatManager.TYPE_LOVERLEFT:// 情侣座位，左侧
                drawCoupleLeftSeat(canvas, s, s.getStatus());
                break;
            case SeatManager.TYPE_LOVERRIGHT:// 情侣座位，右侧
                drawCoupleRightSeat(canvas, s, s.getStatus());
                break;
            case SeatManager.TYPE_DISABLED:// 残疾人座位
            default:
                break;
        }
    }

    private void drawCoupleRightSeat(final Canvas canvas, final SeatInfo s, int drawStatus) {
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);

        final int x = offsetX + seatNameListWidth + width * s.getX() + DISTANCE_HORIZONAL * (s.getX() + 1) - DISTANCE_HORIZONAL / 2;
        final int y = offsetY + coupleHeight * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);
        switch (drawStatus) {
            case SeatManager.STATUS_CAN_SELECT:
                canvas.drawBitmap(seatRight, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_OTHERS:
                canvas.drawBitmap(seatRightGray, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_SELF:
                canvas.drawBitmap(seatRightSelect, x, y, paint);
                break;
            case SeatManager.STATUS_NOT_SEAT:
            default:
                break;
        }
    }

    private void drawCoupleLeftSeat(final Canvas canvas, final SeatInfo s, int drawStatus) {
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);

        final int x = offsetX + seatNameListWidth + width * s.getX() + DISTANCE_HORIZONAL * (s.getX() + 1) + DISTANCE_HORIZONAL / 2;
        final int y = offsetY + coupleHeight * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);
        switch (drawStatus) {
            case SeatManager.STATUS_CAN_SELECT:
                canvas.drawBitmap(seatLeft, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_OTHERS:
                canvas.drawBitmap(seatLeftGray, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_SELF:
                canvas.drawBitmap(seatLeftSelect, x, y, paint);
                break;
            case SeatManager.STATUS_NOT_SEAT:
            default:
                break;
        }
    }

    private void drawCommonSeat(final Canvas canvas, final SeatInfo s, int drawStatus) {
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);

        final int x = offsetX + seatNameListWidth + width * s.getX() + DISTANCE_HORIZONAL * (s.getX() + 1);
        final int y = offsetY + height * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);
        switch (drawStatus) {
            case SeatManager.STATUS_CAN_SELECT:
                canvas.drawBitmap(seatNormal, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_SELF:
                if (s.getSelectImage() != null) {
                    canvas.drawBitmap(s.getSelectImage().bitmap, x, y, paint);
                } else {
                    canvas.drawBitmap(seatNormalSelect, x, y, paint);
                }
                break;
            case SeatManager.STATUS_SELECTED_BY_OTHERS:
                canvas.drawBitmap(seatNormalGray, x, y, paint);
                break;
            case SeatManager.STATUS_NOT_SEAT:
            default:
                break;
        }
    }

    public boolean isInRect(final float posX, final float posY, final int x, final int y, final int w, final int h) {
        final int rawX = (int) posX;
        final int rawY = (int) posY;
        return ((rawX > x) && (rawX < (x + w)) && (rawY > y) && (rawY < (y + h)));
    }

    public SeatInfo getSeatPos(final float x, final float y) {
        if (allSeats == null || isInRect(x, y, 0, 0, (int) (width * scaleX), (int) (totalHeight * scaleY))) {
            return null;
        }

        for (final SeatInfo s : allSeats) {
            if (SeatManager.TYPE_DISABLED == s.getType()) {
                LogWriter.d("残疾人座位");
                continue;
            }

            SeatInfo seat = null;
            seat = getPosInSeat(x, y, seat, s);
            if (null != seat) {
                return seat;
            }
        }

        return null;
    }

    /**
     * 根据x,y获取选择的座位
     */
    private SeatInfo getPosInSeat(final float x, final float y, SeatInfo seat, final SeatInfo s) {
        final int seatX = (int) ((offsetX + seatNameListWidth + (width * s.getX()) + (DISTANCE_HORIZONAL * (s.getX() + 1))) * scaleX);
        final int seatY = (int) ((offsetY + (height * s.getY()) + (DISTANCE_VERTICAL * (s.getY() + 1))) * scaleY);
//        final int seatW = (int) (width * scaleX);
//        final int seatH = (int) (height * scaleY);
        // 因为点两个座位间的空白, 产品童鞋也想要选中某一个座位,所以加了DISTANCE_HORIZONAL
        final int seatW = (int) ((width + DISTANCE_HORIZONAL) * scaleX);
        final int seatH = (int) ((height + DISTANCE_VERTICAL) * scaleY);
        if (isInRect(x, y, seatX, seatY, seatW, seatH)) {
            seat = s;
        }

        return seat;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final int action = event.getAction();
        final int pointCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                isFirst = true;
                touchState = TOUCH_STATE_DOWN;
                posX = event.getX();
                posY = event.getY();
                oriDownX = posX;
                oriDownY = posY;
                distance = 0.0f;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                this.isNeedThumImg = true;
                if (pointCount >= 2) {
                    touchState = TOUCH_STATE_MULIT;
                    final float x1 = event.getX(0);
                    final float y1 = event.getY(0);
                    final float x2 = event.getX(1);
                    final float y2 = event.getY(1);

                    if (isFirst) {
                        distance = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                        isFirst = false;
                        lastScale = scaleX;
                    } else {
                        float dis = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
                        float scale = dis / distance;
                        float scaleNew = scale * lastScale;

                        if (scaleNew < minScale) {
                            scaleNew = minScale;
                            if (seatSelectedInterface != null) {
                                seatSelectedInterface.onMaxOrMin(false);
                            }
                        }

                        if (scaleNew > 1.0f) {
                            scaleNew = 1.0f;
                            if (seatSelectedInterface != null) {
                                seatSelectedInterface.onMaxOrMin(true);
                            }
                        }

                        scaleX = scaleNew;
                        scaleY = scaleNew;

                        float tempX = event.getX() - posX;
                        float tempY = event.getY() - posY;

//                        offsetX += tempX / scaleX;
//                        offsetY += tempY / scaleY;

                        offsetX += tempX;
                        offsetX = (int) (offsetX * scaleX);

                        offsetY += tempY;
                        offsetY = (int) (offsetY * scaleY);

                        posX = event.getX();
                        posY = event.getY();
                    }
                    LogWriter.e("mylog", "双指 event.getX()" + event.getX() + ", event.getY()" + event.getY() + ", offsetX:" + offsetX + ", offsetY:" + offsetY);

                } else {
                    // offsetX/Y is accumulations value. not changed each touch.
                    float tempX = event.getX() - posX;
                    float tempY = event.getY() - posY;

                    offsetX += tempX / scaleX;
                    offsetY += tempY / scaleY;

                    if (Math.abs(event.getX() - oriDownX) > 5 || Math.abs(event.getY() - oriDownY) > 5) {
                        touchState = TOUCH_STATE_MOVIE;
                    }

                    posX = event.getX();
                    posY = event.getY();
//                    LogWriter.e("mylog", "单指 event.getX()" + event.getX() + ", event.getY()" + event.getY() + ", offsetX:" + offsetX + ", offsetY:" + offsetY);
                }
                moveLimition();
            }
            break;
            case MotionEvent.ACTION_UP: {
                // 缩放过程中不进行统计，否则数据量太大了
                if (lastScale != scaleX && null != seatSelectedInterface) {
                    seatSelectedInterface.onScaleChenged(scaleX);
                }
                if (TOUCH_STATE_DOWN == touchState) {
                    setSeatIsSelect(event);
                }

                touchState = TOUCH_STATE_NONE;
                distance = 0;
                oriDownX = 0;
                oriDownY = 0;
                isFirst = true;
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (touchState == TOUCH_STATE_NONE) {
                            isNeedThumImg = false;
                        }
                    }
                }, 1000);
            }
            break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 对座位图拖动的限制
     */
    private void moveLimition() {

        if (offsetX > 0) {
            offsetX = 0;
        } else {
            if (offsetX < (-totalWidth + this.getWidth() / scaleX)) {
                float dis = totalWidth - this.getWidth() / scaleX;
                offsetX = (int) (dis < 0 ? 0 : -1 * dis);
            }
        }

        if (offsetY > 0) {
            offsetY = 0;
        } else {
            if (offsetY < (-totalHeight + this.getHeight() / scaleY)) {
                float dis = totalHeight - this.getHeight() / scaleY;
                offsetY = (int) (dis < 0 ? 0 : -1 * dis);
            }
        }

    }

    private void setSeatIsSelect(final MotionEvent event) {
        final SeatInfo seat = getSeatPos(event.getX(), event.getY());
        if (null == seat) {
            return;
        }

        int result = seatManager.chooseSeat(seat.getX(), seat.getY());
        if (seatSelectedInterface != null) {
            final SeatInfo s = seatManager.getSeat(seat.getX(), seat.getY());
            seatSelectedInterface.onSelect(s, result);
            if (s.getStatus() == SeatManager.STATUS_SELECTED_BY_SELF) {
                if (scaleX == getMinScale() && scaleY == getMinScale()) {
                    times = 10;
                    final int x = offsetX + seatNameListWidth + width * s.getX() + DISTANCE_HORIZONAL * (s.getX() + 1);
                    final int y = offsetY + height * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);
                    final int tempX = offsetX;
                    final int tempY = offsetY;
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            int absx = Math.abs((int) (totalWidth * getMinScale() / 2 - x));
                            int absy = Math.abs((int) (totalHeight * getMinScale() / 2 - y));
                            if (totalWidth * getMinScale() / 2 >= x) {
                                if (offsetX <= tempX + (int) (totalWidth * getMinScale() / 2 - x)) {
                                    offsetX += absx / 10;
                                }
                            } else {
                                if (offsetX > tempX + (int) (totalWidth * getMinScale() / 2 - x)) {
                                    offsetX -= absx / 10;
                                }
                            }
                            if (totalHeight * getMinScale() / 2 <= y) {
                                if (offsetY >= tempY + (int) (totalHeight * getMinScale() / 2 - y)) {
                                    offsetY -= absy / 10;
                                }
                            } else {
                                if (offsetY < tempY + (int) (totalHeight * getMinScale() / 2 - y)) {
                                    offsetY += absy / 10;
                                }
                            }

                            scaleX += (1 - getMinScale()) / 10;
                            scaleY += (1 - getMinScale()) / 10;
                            LogWriter.e("checkScale", "setseatisselect:scaleX:" + scaleX + ", scaleY:" + scaleY);
                            times--;
                            if (times == 0) {
                                handler.removeCallbacks(this);
                                scaleX = 1.0f;
                                scaleY = 1.0f;
                            } else {
                                handler.postDelayed(this, 20);
                            }
                            moveLimition();
                        }

                    };
                    handler.postDelayed(runnable, 20);

                }
            }
        }
    }

    public void initSeatMap() {

        if (null == seatNormal || maxX <= 0 || maxY <= 0) {
            return;
        }

        int calWidth = (width * (maxX + 1)) + (DISTANCE_HORIZONAL * (maxX + 2)) + seatNameListWidth;
        int calHeight = (height * (maxY + 1)) + (DISTANCE_VERTICAL * (maxY + 2));
        //TODO 直接用计算出的宽高就好，为啥要做判断
        totalWidth = calWidth > totalWidth ? calWidth : totalWidth;
        totalHeight = calHeight > totalHeight ? calHeight : totalHeight;
//        totalWidth = calWidth;
//        totalHeight = calHeight;

        float scaleW = (float) this.imgW / (float) totalWidth;
        float scaleH = (float) this.imgH / (float) totalHeight;
        minScale = Math.min(scaleW, scaleH);
//        Log.d("checkseat", "initseatmap:min scale is " + minScale);
        LogWriter.e("checkScale", "initseatmap:scaleX:" + scaleX + ", scaleY:" + scaleY);
        LogWriter.e("checkScale", "initseatmap:offsetx:" + offsetX + ", offsetY:" + offsetY);
        scaleX = minScale;
        scaleY = minScale;
        matrix.setScale(scaleX, scaleY);

        // thumb view
        if (null == this.thumbView) {
            return;
        }

        thumbHeight = totalHeight / ((totalWidth - seatNameListWidth) / this.thumbWidth);

        int marginWidth = getResources().getDimensionPixelSize(R.dimen.sealect_seat_view_margin_width);
        int marginHeight = getResources().getDimensionPixelSize(R.dimen.sealect_seat_view_margin_height);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) thumbWidth + marginWidth,
                (int) thumbHeight + marginHeight);

        thumbView.setLayoutParams(layoutParams);
        thumbView.setValues(marginWidth, marginHeight);
        thumbView.setBackgroundColor(getResources().getColor(R.color.color_bb858585));

        float thumScaleW = this.thumbWidth / (float) (totalWidth - seatNameListWidth);
        float thumScaleH = this.thumbHeight / (float) totalHeight;
        thumMinScale = Math.min(thumScaleW, thumScaleH);
        thumMatrix.setScale(thumMinScale, thumMinScale);
    }

    /**
     * 获取座位图的最大横向和纵向座位数量，保存在maxX,maxY 这个名字起的让人发疯
     */
    public void getMaxXY() {
        if (null == seats) {
            return;
        }

        maxX = 0;
        maxY = 0;
        for (Seat s : seats) {
            if (s.getX() > maxX) {
                maxX = s.getX();
            }
            if (s.getY() > maxY) {
                maxY = s.getY();
            }
        }
    }

    /******************************
     * 以下全为缩略图
     ************************************/
    public void setThumView(SeatThumView view) {
        this.thumbView = view;
    }

    /**
     * 绘制座位图
     */
    private void drawThumSeats(final Canvas canvas, final SeatInfo s) {
        if (null == canvas || null == s) {
            return;
        }
        switch (s.getType()) {
            case SeatManager.TYPE_NORMAL:
                // 普通座位
                drawThumCommonSeat(canvas, s, s.getStatus());
                break;
            case SeatManager.TYPE_LOVERLEFT:
                // 情侣座位，左侧
                drawThumCoupleLeftSeat(canvas, s, s.getStatus());
                break;
            case SeatManager.TYPE_LOVERRIGHT:
                // 情侣座位，右侧
                drawThumCoupleRightSeat(canvas, s, s.getStatus());
                break;
            case SeatManager.TYPE_DISABLED: // 残疾人座位
            default:
                break;
        }
    }

    private void drawThumCommonSeat(final Canvas canvas, final SeatInfo s, int selected) {
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);

        final int x = width * s.getX() + DISTANCE_HORIZONAL * (s.getX() + 1);
        final int y = height * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);
        switch (selected) {
            case SeatManager.STATUS_CAN_SELECT:
                canvas.drawBitmap(seatNormal, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_SELF:
                if (s.getSelectImage() != null) {
                    canvas.drawBitmap(s.getSelectImage().bitmap, x, y, paint);
                } else {
                    canvas.drawBitmap(seatNormalSelect, x, y, paint);
                }
                break;
            case SeatManager.STATUS_SELECTED_BY_OTHERS:
                canvas.drawBitmap(seatNormalGray, x, y, paint);
                break;
            case SeatManager.STATUS_NOT_SEAT:
            default:
                break;
        }
    }

    private void drawThumCoupleLeftSeat(final Canvas canvas, final SeatInfo s, int selected) {
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);

        final int x = width * s.getX() + DISTANCE_HORIZONAL * (s.getX() + 1);
        final int y = coupleHeight * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);

        switch (selected) {
            case SeatManager.STATUS_CAN_SELECT:
                canvas.drawBitmap(seatLeft, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_OTHERS:
                canvas.drawBitmap(seatLeftGray, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_SELF:
                canvas.drawBitmap(seatLeftSelect, x, y, paint);
                break;
            case SeatManager.STATUS_NOT_SEAT:
            default:
                break;
        }
    }

    private void drawThumCoupleRightSeat(final Canvas canvas, final SeatInfo s, int selectType) {
        // why is 23? should calculate the offset value
        // int offset = 23;// 9
        this.paint.setTextSize(40);
        this.paint.setTextAlign(Paint.Align.CENTER);

        final int x = width * s.getX() + DISTANCE_HORIZONAL * s.getX();
        final int y = coupleHeight * s.getY() + DISTANCE_VERTICAL * (s.getY() + 1);
        switch (selectType) {
            case SeatManager.STATUS_CAN_SELECT:
                canvas.drawBitmap(seatRight, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_OTHERS:
                canvas.drawBitmap(seatRightGray, x, y, paint);
                break;
            case SeatManager.STATUS_SELECTED_BY_SELF:
                canvas.drawBitmap(seatRightSelect, x, y, paint);
                break;
            case SeatManager.STATUS_NOT_SEAT:
            default:
                break;
        }
    }

    private void loadBmps() {
        // normal
        seatNormal = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_white);
        seatNormalSelect = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_green);
        seatNormalGray = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_red);

        width = seatNormal.getWidth();
        height = seatNormal.getHeight();
        seatNameListWidth = width;

        // couple left
        seatLeft = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_couple_white_left);
        seatLeftSelect = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_couple_green_left);
        seatLeftGray = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_couple_red_left);
        coupleHeight = seatLeft.getHeight();

        // couple right
        seatRight = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_couple_white_right);
        seatRightSelect = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_couple_green_right);
        seatRightGray = BitmapFactory.decodeResource(getResources(), R.drawable.pic_seat_couple_red_right);
    }
}
