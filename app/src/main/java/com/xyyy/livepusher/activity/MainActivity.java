package com.xyyy.livepusher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void CameraPreview(View view) {
        Intent intent = new Intent(this, CameraPreActivity.class);
        startActivity(intent);
    }

    public void videoRecode(View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }

    //图片生成视频
    public void imgvideo(View view) {
        Intent intent = new Intent(this, ImageVideoActivity.class);
        startActivity(intent);
    }

    //播放YUV数据
    public void yuvplay(View view) {
        Intent intent = new Intent(this, YuvActivity.class);
        startActivity(intent);

    }

    public void livepush(View view) {

        Intent intent = new Intent(this, LivePushActivity.class);
        startActivity(intent);
    }
}
