package com.faisal.bangunruang.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ImageUtils {

    public static Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int yRowStride = planes[0].getRowStride();
        int uvRowStride = planes[1].getRowStride();
        int uvPixelStride = planes[1].getPixelStride();

        byte[] nv21 = new byte[width * height * 3 / 2];
        int pos = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                nv21[pos++] = yBuffer.get(row * yRowStride + col);
            }
        }

        for (int row = 0; row < height / 2; row++) {
            for (int col = 0; col < width / 2; col++) {
                int uvIndex = row * uvRowStride + col * uvPixelStride;
                nv21[pos++] = vBuffer.get(uvIndex);
                nv21[pos++] = uBuffer.get(uvIndex);
            }
        }

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 75, out);
        byte[] jpegBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);

        int rotation = imageProxy.getImageInfo().getRotationDegrees();
        if (rotation != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotated;
        }
        return bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        
        // Center crop to square (assuming target width == height)
        int size = Math.min(srcWidth, srcHeight);
        int x = (srcWidth - size) / 2;
        int y = (srcHeight - size) / 2;
        
        Bitmap cropped = Bitmap.createBitmap(bitmap, x, y, size, size);
        Bitmap scaled = Bitmap.createScaledBitmap(cropped, width, height, true);
        
        if (cropped != bitmap) {
            cropped.recycle();
        }
        
        return scaled;
    }

    /**
     * Preprocessing sesuai training notebook:
     * Channel 0: Grayscale
     * Channel 1: Canny Edge Detection
     * Channel 2: Sobel Magnitude
     * Normalisasi: (pixel / 127.5) - 1.0 → range [-1, 1]
     *
     * Format output: NHWC [1, 224, 224, 3] (TensorFlow/ONNX default)
     */
    public static FloatBuffer preprocessForEdgeDetection(Bitmap bitmap, int width, int height) {
        Bitmap resized = resizeBitmap(bitmap, width, height);

        int[] pixels = new int[width * height];
        resized.getPixels(pixels, 0, width, 0, 0, width, height);

        // Extract grayscale
        float[][] gray = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                gray[y][x] = 0.299f * r + 0.587f * g + 0.114f * b;
            }
        }

        // Canny edge detection
        float[][] canny = cannyEdgeDetection(gray, width, height);

        // Sobel magnitude
        float[][] sobel = sobelMagnitude(gray, width, height);

        // Pack into NHWC format [1, H, W, 3] normalized to [-1, 1]
        FloatBuffer buffer = ByteBuffer.allocateDirect(4 * height * width * 3)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                buffer.put((gray[y][x] / 127.5f) - 1.0f);
                buffer.put((canny[y][x] / 127.5f) - 1.0f);
                buffer.put((sobel[y][x] / 127.5f) - 1.0f);
            }
        }

        buffer.rewind();
        return buffer;
    }

    private static float[][] cannyEdgeDetection(float[][] gray, int width, int height) {
        // Gaussian blur 5x5
        float[][] blurred = gaussianBlur(gray, width, height);

        // Sobel gradients
        float[][] gradX = new float[height][width];
        float[][] gradY = new float[height][width];
        float[][] magnitude = new float[height][width];
        float[][] direction = new float[height][width];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float gx = -blurred[y-1][x-1] + blurred[y-1][x+1]
                        - 2*blurred[y][x-1] + 2*blurred[y][x+1]
                        - blurred[y+1][x-1] + blurred[y+1][x+1];
                float gy = -blurred[y-1][x-1] - 2*blurred[y-1][x] - blurred[y-1][x+1]
                        + blurred[y+1][x-1] + 2*blurred[y+1][x] + blurred[y+1][x+1];
                gradX[y][x] = gx;
                gradY[y][x] = gy;
                magnitude[y][x] = (float) Math.sqrt(gx * gx + gy * gy);
                direction[y][x] = (float) Math.atan2(gy, gx);
            }
        }

        // Non-maximum suppression
        float[][] nms = new float[height][width];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float angle = direction[y][x] * 180f / (float) Math.PI;
                if (angle < 0) angle += 180;

                float q, r;
                if ((angle >= 0 && angle < 22.5) || (angle >= 157.5 && angle <= 180)) {
                    q = magnitude[y][x + 1];
                    r = magnitude[y][x - 1];
                } else if (angle >= 22.5 && angle < 67.5) {
                    q = magnitude[y - 1][x + 1];
                    r = magnitude[y + 1][x - 1];
                } else if (angle >= 67.5 && angle < 112.5) {
                    q = magnitude[y - 1][x];
                    r = magnitude[y + 1][x];
                } else {
                    q = magnitude[y - 1][x - 1];
                    r = magnitude[y + 1][x + 1];
                }

                if (magnitude[y][x] >= q && magnitude[y][x] >= r) {
                    nms[y][x] = magnitude[y][x];
                }
            }
        }

        // Double threshold & hysteresis
        float highThreshold = 150f;
        float lowThreshold = 50f;
        float[][] result = new float[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (nms[y][x] >= highThreshold) {
                    result[y][x] = 255f;
                } else if (nms[y][x] >= lowThreshold) {
                    // Check if connected to strong edge
                    boolean connected = false;
                    for (int dy = -1; dy <= 1 && !connected; dy++) {
                        for (int dx = -1; dx <= 1 && !connected; dx++) {
                            int ny = y + dy, nx = x + dx;
                            if (ny >= 0 && ny < height && nx >= 0 && nx < width) {
                                if (nms[ny][nx] >= highThreshold) connected = true;
                            }
                        }
                    }
                    if (connected) result[y][x] = 255f;
                }
            }
        }

        return result;
    }

    private static float[][] sobelMagnitude(float[][] gray, int width, int height) {
        float[][] result = new float[height][width];
        float maxVal = 0;

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                float gx = -gray[y-1][x-1] + gray[y-1][x+1]
                        - 2*gray[y][x-1] + 2*gray[y][x+1]
                        - gray[y+1][x-1] + gray[y+1][x+1];
                float gy = -gray[y-1][x-1] - 2*gray[y-1][x] - gray[y-1][x+1]
                        + gray[y+1][x-1] + 2*gray[y+1][x] + gray[y+1][x+1];
                result[y][x] = (float) Math.sqrt(gx * gx + gy * gy);
                if (result[y][x] > maxVal) maxVal = result[y][x];
            }
        }

        // Normalize to [0, 255]
        if (maxVal > 0) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    result[y][x] = Math.min(result[y][x] / maxVal * 255f, 255f);
                }
            }
        }

        return result;
    }

    public static FloatBuffer preprocessForEdgeDetectionNCHW(Bitmap bitmap, int width, int height) {
        Bitmap resized = resizeBitmap(bitmap, width, height);

        int[] pixels = new int[width * height];
        resized.getPixels(pixels, 0, width, 0, 0, width, height);

        float[][] gray = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                gray[y][x] = 0.299f * r + 0.587f * g + 0.114f * b;
            }
        }

        float[][] canny = cannyEdgeDetection(gray, width, height);
        float[][] sobel = sobelMagnitude(gray, width, height);

        FloatBuffer buffer = ByteBuffer.allocateDirect(4 * 3 * height * width)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                buffer.put((gray[y][x] / 127.5f) - 1.0f);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                buffer.put((canny[y][x] / 127.5f) - 1.0f);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                buffer.put((sobel[y][x] / 127.5f) - 1.0f);

        buffer.rewind();
        return buffer;
    }

    private static float[][] gaussianBlur(float[][] input, int width, int height) {
        float[][] output = new float[height][width];
        float[][] kernel = {
                {1, 4, 7, 4, 1},
                {4, 16, 26, 16, 4},
                {7, 26, 41, 26, 7},
                {4, 16, 26, 16, 4},
                {1, 4, 7, 4, 1}
        };
        float kernelSum = 273f;

        for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
                float sum = 0;
                for (int ky = -2; ky <= 2; ky++) {
                    for (int kx = -2; kx <= 2; kx++) {
                        sum += input[y + ky][x + kx] * kernel[ky + 2][kx + 2];
                    }
                }
                output[y][x] = sum / kernelSum;
            }
        }
        return output;
    }
}
