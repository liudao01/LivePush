package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.camera.XYCamaryView;
import com.xyyy.livepusher.push.PushVideo;
import com.xyyy.livepusher.push.XYBasePushEncoder;
import com.xyyy.livepusher.push.XYConnectListener;
import com.xyyy.livepusher.push.XYPushEncodec;
import com.xyyy.livepusher.util.LogUtil;

public class LivePushActivity extends AppCompatActivity {

    private PushVideo pushVideo;
    private XYCamaryView cameraview;
    private boolean start = false;
    private XYPushEncodec xyPushEncodec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_push);

        initView();
        pushVideo = new PushVideo();

        pushVideo.setConnectListener(new XYConnectListener() {
            @Override
            public void onConnecting() {
                LogUtil.d("链接ing");
            }

            @Override
            public void onConnectSuccess() {
                LogUtil.d("链接成功");
                xyPushEncodec = new XYPushEncodec(LivePushActivity.this, cameraview.getTextureId());
                xyPushEncodec.initEncodec(cameraview.getEglContext(),1080,1920,44100,2);
                xyPushEncodec.startRecord();


                xyPushEncodec.setOnMediaInfoListener(new XYBasePushEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
//                        LogUtil.d("时间");
                    }

                    @Override
                    public void onSPSPPSInfo(byte[] sps, byte[] pps) {
//                        LogUtil.d("回调SPS PPS 数据 推头信息");
                        pushVideo.pushSPSPPS(sps,pps);

                    }

                    @Override
                    public void onVideoInfo(byte[] data, boolean keyframe) {

                        LogUtil.d("回调视频数据 推流");
                        pushVideo.pushVideoData(data,keyframe);
                    }
                });
            }

            @Override
            public void onConnectFail(String msg) {
                LogUtil.d("链接失败" + msg);

            }
        });
    }

    private void initView() {
        cameraview = findViewById(R.id.cameraview);
    }

    public void startpush(View view) {

        start = !start;
        if (start) {
            pushVideo.initLivePush("rtmp://120.27.17.132/myapp/mystream");

        } else {

            if (xyPushEncodec != null) {
                xyPushEncodec.stopRecord();
                xyPushEncodec = null;
            }
        }

    }


}
