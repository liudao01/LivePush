package com.xyyy.livepusher.push;

/**
 * @author liuml
 * @explain
 * @time 2018/12/21 15:01
 */
public interface XYConnectListener {
    void onConnecting();

    void onConnectSuccess();

    void onConnectFail(String msg);

}
