package com.mtime.bussiness.ticket.movie.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 座位选择 缩略图
 * 
 */
public class SeatThumView extends View {
    private Bitmap bitmap = null;
    private Paint  paint  = null;
    
    private int width;
    private int height;
    
    public SeatThumView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }
    
    public void setBitmap(Bitmap paramBitmap) {
        this.bitmap = paramBitmap;
    }
    
    public void setValues(int marginWidth, int marginHeight) {
        this.width = marginWidth;
        this.height = marginHeight;
        this.paint = new Paint();
    }
    
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        
        if (null == bitmap) {
            return;
        }
        
        // draw the seats bitmap on canvas center 
        paramCanvas.drawBitmap(this.bitmap, width/2, height/2, this.paint);
        
    }

}