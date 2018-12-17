package com.xyyy.livepusher.yuv;

import android.content.Context;
import android.util.AttributeSet;

import com.xyyy.livepusher.egl.XYEGLSurfaceView;

/**
 * @author liuml
 * @explain
 * @time 2018/12/17 14:13
 */
public class XYYuvView extends XYEGLSurfaceView {

    private YuvRender yuvRender;

    public XYYuvView(Context context) {
        this(context, null);
    }

    public XYYuvView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XYYuvView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        yuvRender = new YuvRender(context);
        setRender(yuvRender);
        setRenderMode(XYEGLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setFrameData(int w, int h, byte[] by, byte[] bu, byte[] bv) {
        if (yuvRender != null) {
            yuvRender.setFrameData(w, h, by, bu, bv);
            requestRender();
        }
    }

}
