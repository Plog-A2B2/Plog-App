package com.a2b2.plog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLException;
import android.os.Environment;

import com.kakao.vectormap.MapLogger;
import com.kakao.vectormap.graphics.gl.GLSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class MapCapture {

    public interface OnCaptureListener {
        void onCaptured(boolean isSucceed, Bitmap bitmap, String fileName);
    }

    public static void capture(Activity activity, GLSurfaceView surfaceView, OnCaptureListener listener) {
        String fileName = "MapCapture_" + System.currentTimeMillis() + ".png";

        final boolean[] isSucceed = {false}; // isSucceed를 배열로 감쌈

        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                EGL10 egl = (EGL10) EGLContext.getEGL();
                GL10 gl = (GL10) egl.eglGetCurrentContext().getGL();
                Bitmap bitmap = createBitmapFromGLSurface(0, 0, surfaceView.getWidth(),
                        surfaceView.getHeight(), gl);

                if (bitmap != null) {
                    isSucceed[0] = bitmapToImage(activity.getApplicationContext(), bitmap, fileName);
                }

                Bitmap finalBitmap = bitmap;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCaptured(isSucceed[0], finalBitmap, fileName);
                    }
                });
            }
        });
    }


    private static Bitmap createBitmapFromGLSurface(int x, int y, int w, int h, GL10 gl)
            throws OutOfMemoryError {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
            int offset1, offset2;

            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;

                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }

    private static boolean bitmapToImage(Context context, Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return false;
        }

        // DCIM 폴더에 저장
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                .toString()+ "/MapCaptureDemo";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        File tempFile = new File(dir, fileName);

        try {
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            MapLogger.e("FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            MapLogger.e("IOException : " + e.getMessage());
        }
        return false;
    }
}
