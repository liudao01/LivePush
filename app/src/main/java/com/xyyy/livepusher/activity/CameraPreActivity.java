package com.xyyy.livepusher.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.camera.XYCamaryView;

/**
 * @author liuml
 * @explain
 * @time 2018/12/10 13:45
 */
public class CameraPreActivity extends AppCompatActivity {

    private XYCamaryView cameraview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
        initView();
    }

    private void initView() {
        cameraview = findViewById(R.id.cameraview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraview.onDestory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        cameraview.previewAngle(this);
    }
}

