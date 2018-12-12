package com.xyyy.livepusher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xyyy.livepusher.R;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

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
}
