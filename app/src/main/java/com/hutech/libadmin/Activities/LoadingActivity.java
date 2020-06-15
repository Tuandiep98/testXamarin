package com.hutech.libadmin.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hutech.libadmin.R;

public class LoadingActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    ImageView imgLogo;
    TextView txtTitle;

    Handler handler = new Handler();

    int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorBackground));
        }

        imgLogo = findViewById(R.id.imgLogo);
        txtTitle = findViewById(R.id.txtTitle);
        linearLayout = findViewById(R.id.linearLayout);

        Thread loadingThread = new Thread() {

            @Override
            public void run() {

                while (progress < 11) {
                    progress = doWork();

                    try {
                        super.run();
                        sleep(500);

                    } catch (Exception e) {
                        Toast.makeText(LoadingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (progress == 2) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        TransitionManager.beginDelayedTransition(linearLayout);
                                        txtTitle.setVisibility(View.VISIBLE);
                                    }
                                }
                        );
                    }

                    if (progress == 9) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        TransitionManager.beginDelayedTransition(linearLayout);
                                        txtTitle.setVisibility(View.GONE);
                                    }
                                }
                        );
                    }
                }
            }
        };

        loadingThread.start();
    }

    private int doWork() {
        progress++;
        if (progress < 11) {
            return progress;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Intent i = new Intent(LoadingActivity.this,
                    MainActivity.class);
            startActivity(i);
            finish();
        }

        return 11;
    }
}
