package com.xyyy.livepusher.activity;

import android.media.MediaFormat;
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

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private XYCamaryView cameraview;
    private Button btRecord;

    private XYMediaEncodec xyMediaEncodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
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
            xyMediaEncodec = new XYMediaEncodec(this, cameraview.getTextureId());
            xyMediaEncodec.initEncodec(cameraview.getEglContext(),
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_live.mp4",
                    MediaFormat.MIMETYPE_VIDEO_AVC, 1080, 1920);
            xyMediaEncodec.setOnMediaInfoListener(new XYBaseMediaEncoder.OnMediaInfoListener() {
                @Override
                public void onMediaTime(int times) {
                    LogUtil.d("time = " + times);
                }
            });
            xyMediaEncodec.startRecord();
            btRecord.setText("正在录制");

        } else {
            xyMediaEncodec.stopRecord();
            btRecord.setText("开始录制");
            xyMediaEncodec = null;
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
