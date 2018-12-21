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


}
