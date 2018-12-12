package com.xyyy.livepusher.encodec;


import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;

import com.xyyy.livepusher.egl.EglHelper;
import com.xyyy.livepusher.egl.XYEGLSurfaceView;
import com.xyyy.livepusher.util.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;


/**
 * @author liuml
 * @explain 仿照XYEGLSurfaceView写 的编码类
 * @time 2018/12/11 13:36
 */
public abstract class XYBaseMediaEncoder {

    private Surface surface;
    private EGLContext eglContext;


    private int width;
    private int height;

    private MediaCodec videoEncodec;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferInfo;

    private MediaMuxer mediaMuxer;

    private XYEGLMediaThread xyeglMediaThread;
    private VideoEncodecThread videoEncodecThread;

    private OnMediaInfoListener onMediaInfoListener;

    //控制手动刷新还是自动刷新
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    private XYEGLSurfaceView.XYGLRender xyGLRender;

    public XYBaseMediaEncoder(Context context) {
    }

    public void setRender(XYEGLSurfaceView.XYGLRender xyGLRender) {
        this.xyGLRender = xyGLRender;
    }

    public void setmRenderMode(int mRenderMode) {
        this.mRenderMode = mRenderMode;
    }


    public void initEncodec(EGLContext eglContext, String savePath, String mimeType, int width, int height) {
        this.width = width;
        this.height = height;
        this.eglContext = eglContext;
        initMediaEncodec(savePath, mimeType, width, height);
    }


    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public void startRecord() {
        if (surface != null && eglContext != null) {
            xyeglMediaThread = new XYEGLMediaThread(new WeakReference<XYBaseMediaEncoder>(this));
            videoEncodecThread = new VideoEncodecThread(new WeakReference<XYBaseMediaEncoder>(this));
            xyeglMediaThread.isCreate = true;
            xyeglMediaThread.isChange = true;
            xyeglMediaThread.start();
            videoEncodecThread.start();
        }
    }

    public void stopRecord() {
        if (xyeglMediaThread != null && videoEncodecThread != null) {
            videoEncodecThread.exit();
            xyeglMediaThread.onDestory();
            videoEncodecThread = null;
            xyeglMediaThread = null;
        }
    }


    /**
     * 封装器
     *
     * @param savePath
     * @param mimeType
     * @param width
     * @param height
     */
    private void initMediaEncodec(String savePath, String mimeType, int width, int height) {

        try {
            mediaMuxer = new MediaMuxer(savePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            initVideoEncodec(mimeType, width, height);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 创建编码器
     *
     * @param mimeType
     * @param width
     * @param height
     */
    private void initVideoEncodec(String mimeType, int width, int height) {
        try {
            videoBufferInfo = new MediaCodec.BufferInfo();
            LogUtil.d("mimeType = "+mimeType + " width = "+width + "  height = "+height);
            videoFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//Surface
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);//码率
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);//帧率
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);//I帧 关键帧的间隔  设置为1秒



            //编码
            videoEncodec = MediaCodec.createEncoderByType(mimeType);
            videoEncodec.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            surface = videoEncodec.createInputSurface();


        } catch (IOException e) {
            LogUtil.e(e.getMessage());
            e.printStackTrace();
            videoEncodec = null;
            videoFormat = null;
            videoBufferInfo = null;
        }

    }

    /**
     * EGL线程
     */
    static class XYEGLMediaThread extends Thread {
        private WeakReference<XYBaseMediaEncoder> encoder;
        private EglHelper eglHelper;
        private Object object;

        private boolean isExit = false;
        //记录是否创建
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        public XYEGLMediaThread(WeakReference<XYBaseMediaEncoder> encoder) {
            this.encoder = encoder;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            isStart = false;
            object = new Object();
            eglHelper = new EglHelper();

            eglHelper.initEgl(encoder.get().surface, encoder.get().eglContext);
            while (true) {
                if (isExit) {
                    release();
                    break;
                }
                if (isStart) {
                    if (encoder.get().mRenderMode == RENDERMODE_WHEN_DIRTY) {
                        //手动刷新
                        synchronized (object) {
                            try {
                                object.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (encoder.get().mRenderMode == RENDERMODE_CONTINUOUSLY) {
                        try {
                            Thread.sleep(1000 / 60);//每秒60帧
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        throw new RuntimeException("mRenderMode is wrong value");
                    }
                }

                onCreate();
                onChange(encoder.get().width, encoder.get().height);
                onDraw();
                isStart = true;
            }
        }


        private void onCreate() {
            if (isCreate && encoder.get().xyGLRender != null) {
                isCreate = false;
                encoder.get().xyGLRender.onSurfaceCreated();
            }
        }

        private void onChange(int width, int height) {
            if (isChange && encoder.get().xyGLRender != null) {
                isChange = false;
                encoder.get().xyGLRender.onSurfaceChanged(width, height);
            }
        }

        private void onDraw() {
            if (encoder.get().xyGLRender != null && eglHelper != null) {
                encoder.get().xyGLRender.onDrawFrame();
                //必须调用两次 才能显示
                if (!isStart) {
                    encoder.get().xyGLRender.onDrawFrame();
                }
                eglHelper.swapBuffers();
            }
        }


        private void requestRunder() {
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();//解除
                }
            }
        }

        public void onDestory() {
            isExit = true;
            requestRunder();

        }

        public void release() {
            if (eglHelper != null) {
                eglHelper.destoryEgl();
                eglHelper = null;
                object = null;
                encoder = null;
            }
        }

    }

    /**
     * 视频录制的线程
     */
    static class VideoEncodecThread extends Thread {

        private WeakReference<XYBaseMediaEncoder> encoder;
        private boolean isExit;

        private MediaCodec videoEncodec;
        private MediaFormat videoFormat;
        private MediaCodec.BufferInfo videoBufferInfo;
        private MediaMuxer mediaMuxer;

        private int videoTrackIndex;//视频轨道

        private long pts;

        public VideoEncodecThread(WeakReference<XYBaseMediaEncoder> encoder) {
            this.encoder = encoder;
            videoEncodec = encoder.get().videoEncodec;
            videoFormat = encoder.get().videoFormat;
            videoBufferInfo = encoder.get().videoBufferInfo;
            mediaMuxer = encoder.get().mediaMuxer;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            videoEncodec.start();
            videoTrackIndex = -1;
            pts = 0;
            while (true) {
                if (isExit) {
                    videoEncodec.stop();
                    videoEncodec.release();
                    videoEncodec = null;
                    LogUtil.d("录制完成 ");
                    break;
                }
                //得到队列中可用的输出的索引
                int outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferInfo, 0);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    videoTrackIndex = mediaMuxer.addTrack(videoEncodec.getOutputFormat());
                    mediaMuxer.start();
                } else {
                    while (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = videoEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(videoBufferInfo.offset);
                        outputBuffer.limit(videoBufferInfo.offset + videoBufferInfo.size);
                        //outputBuffer 编码
                        if (pts == 0) {
                            pts = videoBufferInfo.presentationTimeUs;
                        }
                        videoBufferInfo.presentationTimeUs = videoBufferInfo.presentationTimeUs - pts;//实现pts递增
                        mediaMuxer.writeSampleData(videoTrackIndex, outputBuffer, videoBufferInfo);

                        if (encoder.get().onMediaInfoListener != null) {
                            encoder.get().onMediaInfoListener.onMediaTime((int) videoBufferInfo.presentationTimeUs / 1000000);
                        }

                        //编码完了释放
                        videoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferInfo, 0);
                    }
                }

            }
        }


        public void exit() {
            isExit = true;
        }
    }


    public interface OnMediaInfoListener {
        void onMediaTime(int times);
    }
}














