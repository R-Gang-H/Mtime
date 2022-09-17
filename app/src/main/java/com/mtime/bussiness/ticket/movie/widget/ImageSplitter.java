package com.mtime.bussiness.ticket.movie.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.TypedValue;

import com.mtime.R;
import com.mtime.bussiness.ticket.movie.bean.ImagePiece;

import java.util.ArrayList;
import java.util.List;

public class ImageSplitter {
    public static List<ImagePiece> split(Activity activity, Bitmap bitmap, int xPiece, int yPiece) {

        List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / xPiece;
        int pieceHeight = height / yPiece;
        float number = scaleNumber(activity);
        for (int i = 0; i < yPiece; i++) {
            for (int j = 0; j < xPiece; j++) {
                ImagePiece piece = new ImagePiece();
                piece.index = j + i * xPiece;
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                Bitmap bitmapa = Bitmap.createBitmap(bitmap, xValue, yValue,
                        pieceWidth, pieceHeight);
                piece.bitmap = Bitmap.createScaledBitmap(bitmapa, (int)(pieceWidth*number), (int)(pieceHeight*number), true);
                pieces.add(piece);
            }
        }

        return pieces;
    }

    @SuppressWarnings("ResourceType")
    private static float scaleNumber(Activity activity) {
        TypedValue value = new TypedValue();
        activity.getResources().openRawResource(R.drawable.pic_seat_gray, value);
        int density = value.density;
        if (density == TypedValue.DENSITY_DEFAULT) {
            density = TypedValue.DENSITY_DEFAULT;
        }else if (density != TypedValue.DENSITY_NONE) {
        }

        int inTargetDensity = activity.getResources().getDisplayMetrics().densityDpi;
        return inTargetDensity / (density + 0.0f);
    }

}
