//
// Created by liuml on 2018/12/21.
//

#ifndef LIVE_XYCALLJAVA_H
#define LIVE_XYCALLJAVA_H

#include <jni.h>
#include <cwchar>

#define XY_THREAD_MAIN 1
#define XY_THREAD_CHILD 2

class XYCallJava {
public:

    JNIEnv *jniEnv = NULL;
    JavaVM *javaVM = NULL;
    jobject jobj;

    jmethodID jmid_connecting;
    jmethodID jmid_connectsuccess;
    jmethodID jmid_connectfail;


public:
    XYCallJava(JavaVM *javaVM, JNIEnv *jniEnv, jobject *jobj);

    ~XYCallJava();

    void onConnectint(int type);

    void onConnectsuccess();

    void onConnectFail(char *msg);


};

#endif //LIVE_XYCALLJAVA_H
