package com.xyyy.livepusher.push;


import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.view.Surface;

import com.xyyy.livepusher.egl.EglHelper;
import com.xyyy.livepusher.egl.XYEGLSurfaceView;
import com.xyyy.livepusher.util.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLContext;


public abstract class XYBasePushEncoder {

    private Surface surface;
    private EGLContext eglContext;


    private int width;
    private int height;

    private MediaCodec videoEncodec;
    private MediaFormat videoFormat;
    private MediaCodec.BufferInfo videoBufferInfo;

    private MediaCodec audioEncodec;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo audioBufferInfo;
    private long audioPts = 0;
    private int sampleRate;


    private XYEGLMediaThread xyeglMediaThread;
    private VideoEncodecThread videoEncodecThread;
    private AudioEncodecThread audioEncodecThread;

    private OnMediaInfoListener onMediaInfoListener;

    //控制手动刷新还是自动刷新
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private int mRenderMode = RENDERMODE_CONTINUOUSLY;

    private XYEGLSurfaceView.XYGLRender xyGLRender;

    private boolean encode;

    public XYBasePushEncoder(Context context) {
    }

    public void setRender(XYEGLSurfaceView.XYGLRender xyGLRender) {
        this.xyGLRender = xyGLRender;
    }

    public void setmRenderMode(int mRenderMode) {
        this.mRenderMode = mRenderMode;
    }


    public void initEncodec(EGLContext eglContext, int width, int height, int sampleRate, int channelcount) {
        this.width = width;
        this.height = height;
        this.eglContext = eglContext;
        initMediaEncodec(width, height, sampleRate, channelcount);
    }


    public void setOnMediaInfoListener(OnMediaInfoListener onMediaInfoListener) {
        this.onMediaInfoListener = onMediaInfoListener;
    }

    public void startRecord() {
        if (surface != null && eglContext != null) {

            audioPts = 0;


            xyeglMediaThread = new XYEGLMediaThread(new WeakReference<XYBasePushEncoder>(this));
            videoEncodecThread = new VideoEncodecThread(new WeakReference<XYBasePushEncoder>(this));
            audioEncodecThread = new AudioEncodecThread(new WeakReference<XYBasePushEncoder>(this));
            xyeglMediaThread.isCreate = true;
            xyeglMediaThread.isChange = true;
            xyeglMediaThread.start();
            videoEncodecThread.start();
//            audioEncodecThread.start();
        }
    }

    public void stopRecord() {
        if (xyeglMediaThread != null && videoEncodecThread != null && audioEncodecThread != null) {
            videoEncodecThread.exit();
            audioEncodecThread.exit();
            xyeglMediaThread.onDestory();
            videoEncodecThread = null;
            xyeglMediaThread = null;
            audioEncodecThread = null;
        }
    }


    /**
     * 封装器
     *
     * @param width
     * @param height
     * @param sampleRate
     * @param channelcount
     */
    private void initMediaEncodec(int width, int height, int sampleRate, int channelcount) {

        initVideoEncodec(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        initAudioEncodec(MediaFormat.MIMETYPE_AUDIO_AAC, sampleRate, channelcount);

    }


    /**
     * 初始化视频编码器
     *
     * @param mimeType
     * @param width
     * @param height
     */
    private void initVideoEncodec(String mimeType, int width, int height) {
        try {
            videoBufferInfo = new MediaCodec.BufferInfo();
            LogUtil.d("mimeType = " + mimeType + " width = " + width + "  height = " + height);
            videoFormat = MediaFormat.createVideoFormat(mimeType, width, height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//Surface
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 4);//码率
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);//帧率  30帧是一个i 帧
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
     * 初始化音频编码器
     *
     * @param mimeType     格式
     * @param sampleRate   采样率
     * @param channelCount 声道数
     */
    private void initAudioEncodec(String mimeType, int sampleRate, int channelCount) {
        try {
            this.sampleRate = sampleRate;
            audioBufferInfo = new MediaCodec.BufferInfo();
            audioFormat = MediaFormat.createAudioFormat(mimeType, sampleRate, channelCount);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 96000);//比特率
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);//等级
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 4096);

            audioEncodec = MediaCodec.createEncoderByType(mimeType);
            audioEncodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        } catch (IOException e) {
            e.printStackTrace();
            audioBufferInfo = null;
            audioFormat = null;
            audioEncodec = null;
        }
    }

    /**
     * EGL线程 渲染线程
     */
    static class XYEGLMediaThread extends Thread {
        private WeakReference<XYBasePushEncoder> encoder;
        private EglHelper eglHelper;
        private Object object;

        private boolean isExit = false;
        //记录是否创建
        private boolean isCreate = false;
        private boolean isChange = false;
        private boolean isStart = false;

        public XYEGLMediaThread(WeakReference<XYBasePushEncoder> encoder) {
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
     * 视频录制编码的线程
     */
    static class VideoEncodecThread extends Thread {

        private WeakReference<XYBasePushEncoder> encoder;
        private boolean isExit;

        private MediaCodec videoEncodec;
        private MediaCodec.BufferInfo videoBufferInfo;


        private long pts;
        private byte[] sps;
        private byte[] pps;

        public VideoEncodecThread(WeakReference<XYBasePushEncoder> encoder) {
            this.encoder = encoder;
            videoEncodec = encoder.get().videoEncodec;
            videoBufferInfo = encoder.get().videoBufferInfo;
        }

        @Override
        public void run() {
            super.run();
            isExit = false;
            videoEncodec.start();
            pts = 0;


            while (true) {
                if (isExit) {
                    //先把编码器停止
                    videoEncodec.stop();
                    videoEncodec.release();
                    videoEncodec = null;


                    break;
                }
                //得到队列中可用的输出的索引
                int outputBufferIndex = videoEncodec.dequeueOutputBuffer(videoBufferInfo, 0);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                    LogUtil.d("INFO_OUTPUT_FORMAT_CHANGED");

                    ByteBuffer spsb = videoEncodec.getOutputFormat().getByteBuffer("csd-0");
                    sps = new byte[spsb.remaining()];
                    spsb.get(sps, 0, sps.length);

                    ByteBuffer ppsb = videoEncodec.getOutputFormat().getByteBuffer("csd-1");
                    pps = new byte[ppsb.remaining()];
                    ppsb.get(pps, 0, pps.length);

                    LogUtil.d("得到 sps = " + byteToHex(sps));
                    LogUtil.d("得到 pps = " + byteToHex(pps));

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


                        //得到data
                        byte[] data = new byte[outputBuffer.remaining()];
                        outputBuffer.get(data, 0, data.length);
                        LogUtil.d("data: " + byteToHex(data));



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

    /**
     * put PCM 的数据
     *
     * @param buffer
     * @param size
     */
    public void putPCMData(byte[] buffer, int size) {
        if (audioEncodecThread != null && !audioEncodecThread.isExit && buffer != null && size > 0) {
            int inputBufferindex = audioEncodec.dequeueInputBuffer(0);
            if (inputBufferindex >= 0) {
                ByteBuffer byteBuffer = audioEncodec.getInputBuffers()[inputBufferindex];
                byteBuffer.clear();
                byteBuffer.put(buffer);
                long pts = getAudioPts(size, sampleRate);
                audioEncodec.queueInputBuffer(inputBufferindex, 0, size, pts, 0);
            }
        }
    }

    /**
     * 音频编码的线程
     */
    static class AudioEncodecThread extends Thread {

        private WeakReference<XYBasePushEncoder> encoder;
        private boolean isExit;

        private MediaCodec audioEncodec;
        private MediaCodec.BufferInfo AudioBufferInfo;


        //轨道
        private int audioTrackIndex = -1;

        private long pts;

        public AudioEncodecThread(WeakReference<XYBasePushEncoder> encoder) {
            this.encoder = encoder;

            audioEncodec = encoder.get().audioEncodec;
            AudioBufferInfo = encoder.get().audioBufferInfo;

            audioTrackIndex = -1;
        }

        @Override
        public void run() {
            super.run();

            pts = 0;
            isExit = false;

            audioEncodec.start();

            while (true) {
                if (isExit) {
                    //回收资源
                    audioEncodec.stop();
                    audioEncodec.release();
                    audioEncodec = null;

                    break;
                }

                int outputBufferIndex = audioEncodec.dequeueOutputBuffer(AudioBufferInfo, 0);
                if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                } else {

                    while (outputBufferIndex >= 0) {
                        ByteBuffer outputBuffer = audioEncodec.getOutputBuffers()[outputBufferIndex];
                        outputBuffer.position(AudioBufferInfo.offset);
                        outputBuffer.limit(AudioBufferInfo.offset + AudioBufferInfo.size);
                        //outputBuffer 编码
                        if (pts == 0) {
                            pts = AudioBufferInfo.presentationTimeUs;
                        }
                        AudioBufferInfo.presentationTimeUs = AudioBufferInfo.presentationTimeUs - pts;//实现pts递增




                        if (encoder.get().onMediaInfoListener != null) {
                            encoder.get().onMediaInfoListener.onMediaTime((int) AudioBufferInfo.presentationTimeUs / 1000000);
                        }


                        audioEncodec.releaseOutputBuffer(outputBufferIndex, false);
                        outputBufferIndex = audioEncodec.dequeueOutputBuffer(AudioBufferInfo, 0);


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

    private long getAudioPts(int size, int sampleRate) {
        audioPts = audioPts + (long) ((1.0 * size) / (sampleRate * 2 * 2) * 1000000.0);
        return audioPts;
    }

    public static String byteToHex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i]);
            if (hex.length() == 1) {
                stringBuffer.append("0" + hex);
            } else {
                stringBuffer.append(hex);
            }
            if (i > 20) {
                break;
            }
        }
        return stringBuffer.toString();
    }
}














