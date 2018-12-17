package com.xyyy.livepusher.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;
import com.xyyy.livepusher.util.LogUtil;
import com.xyyy.livepusher.yuv.XYYuvView;

import java.io.File;
import java.io.FileInputStream;

public class YuvActivity extends AppCompatActivity {

    private XYYuvView yuvview;
    private FileInputStream fis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        initView();
    }


    private void initView() {
        yuvview = findViewById(R.id.yuvview);
    }

    public void playYUV(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int w = 640;
                    int h = 360;
                    String  url = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sintel_640_360.yuv";
                    LogUtil.d("url = "+url);
                    fis = new FileInputStream(new File(url));
                    byte[] y = new byte[w * h];
                    byte[] u = new byte[w * h / 4];
                    byte[] v = new byte[w * h / 4];
                    //这里是YUV420的格式
                    while (true) {
                        int ry = fis.read(y);
                        int ru = fis.read(u);
                        int rv = fis.read(v);
                        if (ry > 0 && ru > 0 && rv > 0) {
                            yuvview.setFrameData(w, h, y, u, v);
                            Thread.sleep(40);
                        } else {
                            LogUtil.d("完成");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }
}
