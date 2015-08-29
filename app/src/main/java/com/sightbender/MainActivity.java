package com.sightbender;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    ImageView logo;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE); // No Title

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        logo = (ImageView) findViewById(R.id.logo);
        info = (TextView) findViewById(R.id.info);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/hel.otf");
        info.setTypeface(tf, 0);

        // Check for Camera


        PackageManager pm = getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "Sorry, No Available Camera", Toast.LENGTH_SHORT).show();
            return;
        }

        // Animations part
        DisplayMetrics displaymetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        float screenHeight = displaymetrics.heightPixels;

        ObjectAnimator showImg = ObjectAnimator.ofFloat(logo,
                "translationY", screenHeight / 2, -screenHeight / 4);

        showImg.setDuration(2000);

        showImg.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator fadeMsg = ObjectAnimator.ofFloat(info, "alpha", 0f,
                        1f);
                fadeMsg.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        info.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                fadeMsg.setDuration(1000);
                fadeMsg.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });

        ObjectAnimator fadeText = ObjectAnimator.ofFloat(logo, "alpha", 0f,
                1f);

        fadeText.setDuration(3000);

        showImg.start();
        fadeText.start();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent serviceIntent = new Intent(MainActivity.this, FloatingCameraService.class);
                startService(serviceIntent);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, (int) (Constants.SPLASH_SCREEN_OUT));
            }
        }, Constants.SPLASH_SCREEN_OUT);
    }


}
