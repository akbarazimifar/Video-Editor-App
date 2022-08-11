package com.hw.photomovie.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hw.photomovie.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huangwei on 2018/9/3 0003.
 */
public class SimplePhotoData extends PhotoData {

    private ExecutorService mPool = Executors.newFixedThreadPool(4);
    private Context mContext;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    ImageView iv_blurimage, iv_originalimage_new, iv_blurimage_new;
    FrameLayout fl_blurview, fl_blurview_new;
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 25f;

    public SimplePhotoData(Context context, String uri, int state) {
        super(uri, state);
        mContext = context.getApplicationContext();
    }

    @Override
    public void prepareData(int targetState, final OnDataLoadListener onDataLoadListener) {
        mTargetState = targetState;
        switch (mState) {
            case STATE_BITMAP:
                if (targetState == STATE_BITMAP && onDataLoadListener != null) {
                    onDataLoadListener.onDataLoaded(this, getBitmap());
                } else if (targetState == STATE_LOCAL && onDataLoadListener != null) {
                    onDataLoadListener.onDownloaded(this);
                }
                break;
            case STATE_LOADING:
                break;
            case STATE_LOCAL:
                if (targetState == STATE_BITMAP) {
                    mPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            mState = STATE_LOADING;
                            mBitmap = loadBitmap(getUri());

                            if (mBitmap != null) {
                                if (mTargetState == STATE_LOCAL) {
                                    mState = STATE_LOCAL;
                                } else if (mTargetState == STATE_BITMAP) {
                                    mState = STATE_BITMAP;
                                }
                                if (onDataLoadListener != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mTargetState >= STATE_LOCAL) {
                                                onDataLoadListener.onDownloaded(SimplePhotoData.this);
                                            }
                                            if (mTargetState == STATE_BITMAP) {
                                                onDataLoadListener.onDataLoaded(SimplePhotoData.this, mBitmap);
                                            }
                                        }
                                    });
                                }
                            } else {
                                mState = STATE_ERROR;
                                if (onDataLoadListener != null) {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onDataLoadListener.onError(SimplePhotoData.this, null);
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else if (targetState == STATE_LOCAL && onDataLoadListener != null) {
                    onDataLoadListener.onDownloaded(this);
                }
                break;
            case STATE_DOWNLOADING:
                break;
            case STATE_REMOTE:
            case STATE_ERROR:
                mPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        mState = STATE_LOADING;
                        loadBitmap(getUri());
                        mState = STATE_BITMAP;
                    }
                });
                break;
        }
    }

    private Bitmap loadBitmap(String uri) {
        Bitmap bitmap = null;
        Bitmap resizedBitmap;
        if (uri.startsWith("drawable://")) {
            String idStr = uri.substring("drawable://".length());
            int id = Integer.parseInt(idStr);
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), id);
        } else if (uri.startsWith("file://")) {
            Log.d("checktype", "file");
            String path = uri.substring("file://".length());
            bitmap = BitmapFactory.decodeFile(path);
        } else if (uri.startsWith("http")) {
            InputStream is = null;
            try {
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                is = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Log.d("checktype", "else");
            String path = uri;
            bitmap = BitmapFactory.decodeFile(path);
            Log.d("ppppp", "Beforewidth :- " + bitmap.getWidth() + " " + "Beforeheight :- " + bitmap.getHeight());

            LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentView1 = inflater1.inflate(R.layout.raw_newimageview, null, false);

            iv_originalimage_new = contentView1.findViewById(R.id.iv_originalimage_new);
            iv_blurimage_new = contentView1.findViewById(R.id.iv_blurimage_new);
            fl_blurview_new = contentView1.findViewById(R.id.fl_blurview_new);

            if (bitmap.getWidth() < 1280 || bitmap.getHeight() < 1280) {
                // Bitmap blurredBitmap = fastBlur(bitmap, 25);
                Bitmap blurredBitmap = fastBlur(bitmap, 25);
                // iv_blurimage_new.setImageBitmap(bitmapblur);
                iv_blurimage_new.setImageBitmap(blurredBitmap);
                iv_originalimage_new.setImageBitmap(bitmap);
                // iv_newblurimg.setImageBitmap(blurredBitmap);

                Log.d("sdfhsfhsfjsdf", "hdfjshdfjsh");
                bitmap = viewToBitmap(fl_blurview_new);
                Log.d("bitmapsize", "width: " + bitmap.getWidth() + "hight: " + bitmap.getHeight());

               // bitmap = getResizedBitmap(bitmap, 1280, 1280);
                bitmap = getResizedBitmap(bitmap, 1280, 1840);

            } else {
               // bitmap = getResizedBitmap(bitmap, 1042, 788);
               // bitmap = getResizedBitmap(bitmap, 1042, 950);
                bitmap = getResizedBitmap(bitmap, 1280, 1493);
                Bitmap blurredBitmap = fastBlur(bitmap, 25);
                // Bitmap blurredBitmap = blur(mContext, bitmap);
                // iv_blurimage_new.setImageBitmap(bitmapblur);
                iv_blurimage_new.setImageBitmap(blurredBitmap);
                iv_originalimage_new.setImageBitmap(bitmap);
                // iv_newblurimg.setImageBitmap(blurredBitmap);
                bitmap = viewToBitmap(fl_blurview_new);
                Log.d("ppppp", "elsewidth: " + bitmap.getWidth() + "elseheight: " + bitmap.getHeight());
               // bitmap = getResizedBitmap(bitmap, 1280, 1280);
                bitmap = getResizedBitmap(bitmap, 1280, 1840);

            }
            //  bitmap=getResizedBitmap(bitmap);
            //    bitmap=scaleBitmap(bitmap,1042,788);
            //   bitmap= Bitmap.createScaledBitmap(bitmap, 1042 , 788 , true);
            Log.d("ppppp", "width :- " + bitmap.getWidth() + " " + "height :- " + bitmap.getHeight());
        }
        return bitmap;
    }

    public Bitmap viewToBitmap(final View view) {
        // view.measure(displayMetrics.widthPixels,view.getHeight());
        view.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        //view.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        view.draw(c);
        return bitmap;


    }

    private Bitmap scaleBitmap(Bitmap bm, int maxWidth, int maxHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int) ( height / ratio );
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int) ( width / ratio );
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ( (float) newWidth ) / width;
        float scaleHeight = ( (float) newHeight ) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private static Bitmap fastBlur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return ( null );
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = ( div + 1 ) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = ( i / divsum );
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = ( p & 0xff0000 ) >> 16;
                sir[1] = ( p & 0x00ff00 ) >> 8;
                sir[2] = ( p & 0x0000ff );
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = ( p & 0xff0000 ) >> 16;
                sir[1] = ( p & 0x00ff00 ) >> 8;
                sir[2] = ( p & 0x0000ff );

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = ( stackpointer + 1 ) % div;
                sir = stack[( stackpointer ) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = ( stackpointer + 1 ) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return ( bitmap );
    }

    public static Bitmap blur(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

}
