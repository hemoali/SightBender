package com.sightbender;

/**
 * Created by ibrahimradwan on 8/23/15.
 */

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

public class FloatingCameraService extends Service implements SurfaceHolder.Callback {

    ImageView close, flash, resize, rotate;
    RelativeLayout wrap_layout;
    static Camera camera = null;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false, flashOn = false;

    private WindowManager windowManager;
    private LinearLayout floatingCamLL;
    private int mRotate = 0;
    int width, height;

    @Override
    public void onCreate () {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        DisplayMetrics displaymetrics = new DisplayMetrics();

        windowManager.getDefaultDisplay().getMetrics(displaymetrics);

        width = displaymetrics.widthPixels;
        height = displaymetrics.heightPixels;

        floatingCamLL = new LinearLayout(getApplicationContext());

        View viewToLoad = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.service_camera, null);

        floatingCamLL.addView(viewToLoad);

        surfaceView = (SurfaceView) viewToLoad.findViewById(R.id.surfaceview);
        close = (ImageView) viewToLoad.findViewById(R.id.close);
        flash = (ImageView) viewToLoad.findViewById(R.id.flash);
        resize = (ImageView) viewToLoad.findViewById(R.id.resize);
        rotate = (ImageView) viewToLoad.findViewById(R.id.rotate);
        wrap_layout = (RelativeLayout) viewToLoad.findViewById(R.id.wrap_layout);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final WindowManager.LayoutParams floatingCamLLParams = new WindowManager.LayoutParams(
                width / 2, height / 2,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        floatingCamLLParams.x = width / 4;
        floatingCamLLParams.y = -height / 4;


        // Animation

        ObjectAnimator showCam = ObjectAnimator.ofFloat(floatingCamLL,
                "translationY", height, 0);

        showCam.setDuration(800);
        showCam.setInterpolator(new BounceInterpolator());
        showCam.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart (Animator animation) {
                windowManager.addView(floatingCamLL, floatingCamLLParams);
            }

            @Override
            public void onAnimationRepeat (Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd (Animator animation) {

            }

            @Override
            public void onAnimationCancel (Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        showCam.start();

        resize.setOnTouchListener(new View.OnTouchListener() {
            private int initialWidth;

            private int initialHeight;

            private float initialTouchX;

            private float initialTouchY;

            @Override
            public boolean onTouch (View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        close.setVisibility(View.INVISIBLE);
                        resize.setVisibility(View.INVISIBLE);
                        wrap_layout.setBackgroundColor(Color.TRANSPARENT);

                        initialWidth = floatingCamLLParams.width;

                        initialHeight = floatingCamLLParams.height;

                        initialTouchX = event.getRawX();

                        initialTouchY = event.getRawY();

                        return true;

                    case MotionEvent.ACTION_UP:

                        close.setVisibility(View.VISIBLE);
                        resize.setVisibility(View.VISIBLE);
                        wrap_layout.setBackgroundColor(Color.WHITE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        floatingCamLLParams.width = initialWidth
                                - (int) (event.getRawX() - initialTouchX);

                        floatingCamLLParams.height = initialHeight
                                + (int) (event.getRawY() - initialTouchY);
                        if (floatingCamLLParams.width > (int) (width * 0.95)) {
                            floatingCamLLParams.width = (int) (width * 0.95);
                        }
                        if (floatingCamLLParams.height > (int) (height * 0.95)) {
                            floatingCamLLParams.height = (int) (height * 0.95);
                        }
                        if (floatingCamLLParams.width < (int) (width * 0.15)) {
                            floatingCamLLParams.width = (int) (width * 0.15);
                        }
                        if (floatingCamLLParams.height < (int) (height * 0.15)) {
                            floatingCamLLParams.height = (int) (height * 0.15);
                        }
                        windowManager.updateViewLayout(floatingCamLL,
                                floatingCamLLParams);

                        return true;
                }
                return false;
            }
        });

        wrap_layout.setOnTouchListener(
                new View.OnTouchListener() {

                    private int initialX;

                    private int initialY;

                    private float initialTouchX;

                    private float initialTouchY;

                    @Override
                    public boolean onTouch (View v, MotionEvent event) {

                        switch (event.getAction()) {

                            case MotionEvent.ACTION_DOWN:

                                initialX = floatingCamLLParams.x;

                                initialY = floatingCamLLParams.y;

                                initialTouchX = event.getRawX();

                                initialTouchY = event.getRawY();
                                return true;


                            case MotionEvent.ACTION_UP:

                                return true;

                            case MotionEvent.ACTION_MOVE:

                                floatingCamLLParams.x = initialX
                                        + (int) (event.getRawX() - initialTouchX);

                                floatingCamLLParams.y = initialY
                                        + (int) (event.getRawY() - initialTouchY);

                                if (floatingCamLLParams.x > (width / 2 - floatingCamLLParams.width / 2))
                                    floatingCamLLParams.x = (width / 2 - floatingCamLLParams.width / 2);
                                if (floatingCamLLParams.y > (height / 2 - floatingCamLLParams.height / 2))
                                    floatingCamLLParams.y = (height / 2 - floatingCamLLParams.height / 2);
                                if (floatingCamLLParams.x < -(width / 2 - floatingCamLLParams.width / 2))
                                    floatingCamLLParams.x = -(width / 2 - floatingCamLLParams.width / 2);
                                if (floatingCamLLParams.y < -(height / 2 - floatingCamLLParams.height / 2))
                                    floatingCamLLParams.y = -(height / 2 - floatingCamLLParams.height / 2);

                                windowManager.updateViewLayout(floatingCamLL,
                                        floatingCamLLParams);
                                return true;
                        }
                        return false;
                    }
                });

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                if (!flashOn) {
                    Camera.Parameters p = camera.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(p);
                } else {
                    Camera.Parameters p = camera.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(p);
                }
                flashOn = !flashOn;
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent intent = new Intent(FloatingCameraService.this, FloatingCameraService.class);
                stopService(intent);
                previewing = false;
                if (camera != null) {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                    camera = null;
                }
                windowManager.removeView(floatingCamLL);

            }
        });
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                mRotate += 90;
                if (mRotate == 360) mRotate = 0;
                previewing = false;
                if (camera != null) {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);

                    camera.release();
                    camera = null;
                }
                windowManager.removeView(floatingCamLL);
                onCreate();
            }
        });
    }

    @Override
    public IBinder onBind (Intent intent) {


        return null;
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int rotation = display.getRotation();
        int degrees = 90;
        String rotate = "portrait";
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                rotate = "portrait";
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                rotate = "landscape";
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                rotate = "portrait";
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                rotate = "landscape";
                break;
        }
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int result = (info.orientation - degrees + 360) % 360;
        result = (result + mRotate) % 360;
        if (!previewing) {
            camera = Camera.open();

            if (camera != null) {
                try {
                    Camera.Parameters params = camera.getParameters();
                    params.set("jpeg-quality", 72);
                    params.setPictureFormat(PixelFormat.JPEG);
                    camera.setParameters(params);
                    camera.setDisplayOrientation((result >= 180) ? result - 180 : result + 180);
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                    previewing = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {

    }

    @Override
    public void onConfigurationChanged (Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        previewing = false;
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);

            camera.release();
            camera = null;
        }
        windowManager.removeView(floatingCamLL);
        mRotate = 0;
        onCreate();
    }
}