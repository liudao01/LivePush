#include <jni.h>
#include <string>

#include "RtmpPush.h"
#include "XYCallJava.h"

RtmpPush *rtmpPush = NULL;
XYCallJava *xyCallJava = NULL;
JavaVM *javaVM = NULL;


extern "C"
JNIEXPORT void JNICALL
Java_com_xyyy_livepusher_push_PushVideo_initPush(JNIEnv *env, jobject instance, jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);
    xyCallJava = new XYCallJava(javaVM, env, &instance);
    rtmpPush = new RtmpPush(pushUrl, xyCallJava);
    rtmpPush->init();

    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}


extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    javaVM = jvm;
    JNIEnv *env;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        if (LOG_SHOW) {
            LOGE("GetEnv failed!");
        }
        return -1;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    javaVM = NULL;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xyyy_livepusher_push_PushVideo_pushSPSPPS(JNIEnv *env, jobject instance, jbyteArray sps_,
                                                   jint sps_len, jbyteArray pps_, jint pps_len) {
    jbyte *sps = env->GetByteArrayElements(sps_, NULL);
    jbyte *pps = env->GetByteArrayElements(pps_, NULL);

    // TODO
    if (rtmpPush != NULL) {
        rtmpPush->pushSPSPPS(reinterpret_cast<char *>(sps), sps_len, reinterpret_cast<char *>(pps),
                             pps_len);
    }


    env->ReleaseByteArrayElements(sps_, sps, 0);
    env->ReleaseByteArrayElements(pps_, pps, 0);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_xyyy_livepusher_push_PushVideo_pushvideodata(JNIEnv *env, jobject instance,
                                                      jbyteArray data_, jint data_len,
                                                      jboolean keyframe) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    if (rtmpPush != NULL) {
        rtmpPush->pushVideoData(reinterpret_cast<char *>(data), data_len, keyframe);
    }
    // TODO

    env->ReleaseByteArrayElements(data_, data, 0);
}