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


    public void initLivePush(String url) {
        if (!TextUtils.isEmpty(url)) {
            initPush(url);
        }
    }
    private  native void initPush(String pushUrl);


}
