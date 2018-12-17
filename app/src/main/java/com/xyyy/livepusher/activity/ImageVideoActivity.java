package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.imgvideo.XYImgVideoView;

public class ImageVideoActivity extends AppCompatActivity {

    private XYImgVideoView imgvideoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_video);

        initView();
    }

    private void initView() {
        imgvideoview = findViewById(R.id.imgvideoview);
        imgvideoview.setCurrentImg(R.drawable.img_1);
    }


    public void start(View view) {

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
            }
        }).start();

    }
}
