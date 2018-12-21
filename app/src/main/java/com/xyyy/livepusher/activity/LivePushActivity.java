package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.push.PushVideo;

public class LivePushActivity extends AppCompatActivity {

    private PushVideo pushVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_push);

        pushVideo = new PushVideo();
    }


    public void startpush(View view) {
        pushVideo.initLivePush("rtmp://120.27.17.132/myapp/mystream");

    }
}
