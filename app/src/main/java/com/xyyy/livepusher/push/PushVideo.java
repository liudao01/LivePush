package com.xyyy.livepusher.push;

import android.text.TextUtils;

/**
 * @author liuml
 * @explain
 * @time 2018/12/21 10:43
 */
public class PushVideo {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("push");
    }


    private  XYConnectListener connectListener;


    public void initLivePush(String url) {
        if (!TextUtils.isEmpty(url)) {
            initPush(url);
        }
    }

    public void pushSPSPPS(byte[] sps, byte[] pps) {
        if (sps != null && pps != null) {
            pushSPSPPS(sps, sps.length, pps, pps.length);
        }
    }

    public void pushVideoData(byte[] data,boolean keyframe){
        if (data != null) {
            pushvideodata(data, data.length,keyframe);
        }
    }

    public void pushAudioData(byte[] data) {
        if (data != null) {
            pushAudioData(data,data.length);
        }
    }

    public void stopPush(){
        pushStop();
    }

    public void setConnectListener(XYConnectListener connectListener) {
        this.connectListener = connectListener;
    }



    private void onConnecting()
    {
        if(connectListener != null)
        {
            connectListener.onConnecting();
        }
    }

    private void onConnectSuccess()
    {
        if(connectListener != null)
        {
            connectListener.onConnectSuccess();
        }
    }

    private void onConnectFial(String msg)
    {
        if(connectListener != null)
        {
            connectListener.onConnectFail(msg);
        }
    }



    private  native void initPush(String pushUrl);

    private native void pushSPSPPS(byte[] sps,int sps_len,byte[] pps,int pps_len);

    private native void pushvideodata(byte[] data,int data_len,boolean keyframe);

    private native void pushAudioData(byte[] data,int data_len);

    private native void pushStop();
}
