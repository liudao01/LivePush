package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.camera.XYCamaryView;
import com.xyyy.livepusher.encodec.XYBaseMediaEncoder;
import com.xyyy.livepusher.encodec.XYMediaEncodec;
import com.xyyy.livepusher.util.LogUtil;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnCompleteListener;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private XYCamaryView cameraview;
    private Button btRecord;

    private XYMediaEncodec xyMediaEncodec;

    private WlMusic wlMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);
        wlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                wlMusic.playCutAudio(20, 40);
            }
        });

        wlMusic.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                if (xyMediaEncodec != null) {
                    xyMediaEncodec.stopRecord();
                    xyMediaEncodec = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            btRecord.setText("开始录制");
                        }
                    });
                }
            }
        });

        wlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                xyMediaEncodec = new XYMediaEncodec(VideoActivity.this, cameraview.getTextureId());
                xyMediaEncodec.initEncodec(cameraview.getEglContext(),
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_live.mp4", 1080, 1920, samplerate, channels);
                xyMediaEncodec.setOnMediaInfoListener(new XYBaseMediaEncoder.OnMediaInfoListener() {
                    @Override
                    public void onMediaTime(int times) {
                        LogUtil.d("time = " + times);
                    }
                });
                xyMediaEncodec.startRecord();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if (xyMediaEncodec != null) {
                    xyMediaEncodec.putPCMData(pcmdata, size);
                }
            }
        });
        initView();


    }

    private void initView() {
        cameraview = findViewById(R.id.cameraview);
        btRecord = findViewById(R.id.bt_record);

        btRecord.setOnClickListener(this);
    }


    //录制
    public void btrecord() {
        if (xyMediaEncodec == null) {

            wlMusic.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/xjw.mp3");
            wlMusic.prePared();
            btRecord.setText("正在录制");

        } else {
            xyMediaEncodec.stopRecord();
            btRecord.setText("开始录制");
            xyMediaEncodec = null;

            wlMusic.stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_record:
                btrecord();
                break;
        }
    }

}
