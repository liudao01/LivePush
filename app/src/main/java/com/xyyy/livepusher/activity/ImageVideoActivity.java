package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.encodec.XYMediaEncodec;
import com.xyyy.livepusher.imgvideo.XYImgVideoView;
import com.ywl5320.libmusic.WlMusic;
import com.ywl5320.listener.OnPreparedListener;
import com.ywl5320.listener.OnShowPcmDataListener;

public class ImageVideoActivity extends AppCompatActivity {

    private XYImgVideoView imgvideoview;

    private XYMediaEncodec xyMediaEncodec;
    private WlMusic wlMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video);

        initView();
    }

    private void initView() {
        imgvideoview = findViewById(R.id.imgvideoview);
        imgvideoview.setCurrentImg(R.drawable.img_1);

        wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);

        wlMusic.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                wlMusic.playCutAudio(0, 60);

            }
        });

        wlMusic.setOnShowPcmDataListener(new OnShowPcmDataListener() {
            @Override
            public void onPcmInfo(int samplerate, int bit, int channels) {
                xyMediaEncodec = new XYMediaEncodec(ImageVideoActivity.this, imgvideoview.getFbotextureid());
                xyMediaEncodec.initEncodec(imgvideoview.getEglContext(),Environment.getExternalStorageDirectory().getAbsolutePath()+"/image_video.mp4",
                        720,500,samplerate,channels);

                xyMediaEncodec.startRecord();

                startImg();
            }

            @Override
            public void onPcmData(byte[] pcmdata, int size, long clock) {
                if (xyMediaEncodec != null) {
                    xyMediaEncodec.putPCMData(pcmdata,size);
                }
            }
        });
    }


    public void start(View view) {

        wlMusic.setSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/xjw.mp3");
        wlMusic.prePared();
    }

    private void startImg(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 256; i++) {
                    //拿到资源
                    int imgsrc = getResources().getIdentifier("img_" + i, "drawable", "com.xyyy.livepusher");
                    imgvideoview.setCurrentImg(imgsrc);
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                if (xyMediaEncodec != null) {
                    wlMusic.stop();
                    xyMediaEncodec.stopRecord();
                    xyMediaEncodec = null;
                }
            }
        }).start();

    }
}
