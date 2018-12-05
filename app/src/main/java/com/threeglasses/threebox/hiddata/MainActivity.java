package com.threeglasses.threebox.hiddata;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xgvr.glasses.XgvrMoudle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        XgvrMoudle.getInstance().xgvr_hdm_init(this);
    }
}

