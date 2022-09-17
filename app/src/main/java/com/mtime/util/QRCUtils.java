package com.mtime.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aosyee on 16/11/11.
 */

public class QRCUtils {

    /**
     * 生成二维码Bitmap
     *
     * @param content    内容
     * @param filePath   用于存储二维码图片的文件路径
     * @param blackColor 二维码黑色部分的颜色
     * @param whiteColor 二维码白色部分的颜色
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImage(String content, int dstWidth, int dstHeight, int blackColor, int whiteColor, File filePath) {
        try {
            Bitmap qrImage = createQRImage(content, dstWidth, dstHeight, blackColor, whiteColor);
            if (qrImage == null) {
                return false;
            }
            FileOutputStream fos = new FileOutputStream(filePath);
            boolean compress = qrImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return compress;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content  内容
     * @param filePath 用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImage(String content, int dstWidth, int dstHeight, File filePath) {
        return createQRImage(content, dstWidth, dstHeight, Color.BLACK, Color.WHITE, filePath);
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content 内容
     * @return 生成二维码及保存文件是否成功
     */
    public static Bitmap createQRImage(String content, int dstWidth, int dstHeight, int blackColor, int whiteColor) {
        try {
            if (content == null || "".equals(content)) {
                return null;
            }

            //配置参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
//            hints.put(EncodeHintType.MARGIN, 0);
//            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            QRCode qrCode = Encoder.encode(content, ErrorCorrectionLevel.H, hints);
            ByteMatrix matrix = qrCode.getMatrix();
            int width = matrix.getWidth();
            int height = matrix.getHeight();

            BitMatrix bitMatrix = renderResult(matrix, width * 4, height * 4);
//            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, dstWidth, dstHeight, hints);

            width = bitMatrix.getWidth();
            height = bitMatrix.getHeight();

            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = blackColor;
                    } else {
                        pixels[y * width + x] = whiteColor;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
            if (scaledBitmap != bitmap) {
                bitmap.recycle();
            }
            bitmap = scaledBitmap;

            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static BitMatrix renderResult(ByteMatrix input, int width, int height) {
        if (input == null) {
            throw new IllegalStateException();
        } else {
            int inputWidth = input.getWidth();
            int inputHeight = input.getHeight();
            int outputWidth = Math.max(width, inputWidth);
            int outputHeight = Math.max(height, inputHeight);
            int multiple = Math.min(outputWidth / inputWidth, outputHeight / inputHeight);
            int leftPadding = (outputWidth - inputWidth * multiple) / 2;
            int topPadding = (outputHeight - inputHeight * multiple) / 2;
            BitMatrix output = new BitMatrix(outputWidth, outputHeight);
            int inputY = 0;
            for (int outputY = topPadding; inputY < inputHeight; outputY += multiple) {
                int inputX = 0;
                for (int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
                    if (input.get(inputX, inputY) == 1) {
                        output.setRegion(outputX, outputY, multiple, multiple);
                    }
                    ++inputX;
                }
                ++inputY;
            }
            return output;
        }
    }

}
