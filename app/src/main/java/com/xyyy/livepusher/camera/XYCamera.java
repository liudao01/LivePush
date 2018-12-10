package com.xyyy.livepusher.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

/**
 * @author liuml
 * @explain
 * @time 2018/12/10 11:08
 */
public class XYCamera {

    private SurfaceTexture surfaceTexture;
    //导包注意用硬件的
    private Camera camera;


    public XYCamera() {

    }

    /**
     * @param surfaceTexture
     * @param cameraId       前置还是后置
     */
    public void initCamera(SurfaceTexture surfaceTexture, int cameraId) {
        this.surfaceTexture = surfaceTexture;
        setCameraParm(cameraId);
    }

    private void setCameraParm(int cameraId) {
        try {
            camera = Camera.open(cameraId);
            camera.setPreviewTexture(surfaceTexture);
            Camera.Parameters parameters = camera.getParameters();

            parameters.setFlashMode("off");//闪光灯
            parameters.setPreviewFormat(ImageFormat.NV21);

            parameters.setPictureSize(parameters.getSupportedPictureSizes().get(0).width,
                    parameters.getSupportedPictureSizes().get(0).height);

            parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(0).width,
                    parameters.getSupportedPreviewSizes().get(0).height);

            camera.setParameters(parameters);
            camera.startPreview();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void changeCamera(int cameraId) {

        if (camera != null) {
            stopPreview();
        }
        setCameraParm(cameraId);

    }
}






