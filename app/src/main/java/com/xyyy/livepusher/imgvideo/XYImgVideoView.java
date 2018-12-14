package com.xyyy.livepusher.imgvideo;

import android.content.Context;
import android.util.AttributeSet;

import com.xyyy.livepusher.egl.XYEGLSurfaceView;

/**
 * @author liuml
 * @explain
 * @time 2018/12/14 13:28
 */
public class XYImgVideoView extends XYEGLSurfaceView {


    private XYImgVideoRender xyImgVideoRender;
    private int fbotextureid;

    public XYImgVideoView(Context context) {
        this(context, null);
    }

    public XYImgVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYImgVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        xyImgVideoRender = new XYImgVideoRender(context);
        setRender(xyImgVideoRender);
        setRenderMode(XYEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
        xyImgVideoRender.setOnSurfaceCreateListener(new XYImgVideoRender.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(int textureId) {
                fbotextureid = textureId;
            }
        });
    }

    public void setCurrentImg(int imgsrc) {
        if (xyImgVideoRender != null) {
            xyImgVideoRender.setCurrentImgSrc(imgsrc);
            requestRender();//手动刷新 调用一次
        }
    }


    public int getFbotextureid(){
        return fbotextureid;
    }
}
