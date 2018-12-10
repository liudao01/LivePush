package com.xyyy.livepusher.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;

import com.xyyy.livepusher.LogUtil;
import com.xyyy.livepusher.egl.XYEGLSurfaceView;

/**
 * @author liuml
 * @explain
 * @time 2018/12/7 15:53
 */
public class XYCamaryView extends XYEGLSurfaceView {

    private XYCameraRender xyCameraRender;
    private XYCamera xyCamera;
    //摄像头
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;


    public XYCamaryView(Context context) {
        this(context, null);
    }

    public XYCamaryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYCamaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        xyCameraRender = new XYCameraRender(context);
        xyCamera = new XYCamera();
        setRender(xyCameraRender);
        xyCameraRender.setOnSurfaceCreateListener(new XYCameraRender.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture) {
                LogUtil.d("render 回调");
                xyCamera.initCamera(surfaceTexture, cameraId);
            }
        });
    }


    public void onDestory() {
        if (xyCamera != null) {
            xyCamera.stopPreview();
        }
    }
}
