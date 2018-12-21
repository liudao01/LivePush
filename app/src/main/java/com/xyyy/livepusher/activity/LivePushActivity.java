package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.push.PushVideo;
import com.xyyy.livepusher.push.XYConnectListener;
import com.xyyy.livepusher.util.LogUtil;

public class LivePushActivity extends AppCompatActivity {

    private PushVideo pushVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_push);

        pushVideo = new PushVideo();

        pushVideo.setConnectListener(new XYConnectListener() {
            @Override
            public void onConnecting() {
                LogUtil.d("链接ing");
            }

            @Override
            public void onConnectSuccess() {
                LogUtil.d("链接成功");

            }

            @Override
            public void onConnectFail(String msg) {
                LogUtil.d("链接失败"+msg);

            }
        });
    }


    public void startpush(View view) {
        pushVideo.initLivePush("rtmp://120.27.17.132/myapp/mystream");

    }
}
