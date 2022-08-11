package com.example.fullscreenvideocreator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.fullscreenvideocreator.R;
import com.victor.loading.newton.NewtonCradleLoading;

public class ActivityLoading extends AppCompatActivity {

    Handler handler;
    Context context;
    NewtonCradleLoading newtonCradleLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        context=ActivityLoading.this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        newtonCradleLoading=findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(context, ActivityHome.class);
                startActivity(intent);
                finish();
            }
        }, 3000);


    }
}
