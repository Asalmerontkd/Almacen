package com.example.antonio.almacen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View splashScreen = new EasySplashScreen(this)
                .withFullScreen()
                .withTargetActivity(MainActivity.class)
                .withSplashTimeOut(5000)
                .withBackgroundResource(R.color.background)
                //.withHeaderText("Demo")
                //.withFooterText("Copyright 2017")
                .withBeforeLogoText("Mariachi.io")
                .withLogo(R.drawable.truck)
                .withAfterLogoText("Demo almacén.")
                .create();
        setContentView(splashScreen);
    }
}
