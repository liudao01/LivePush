#include <jni.h>
#include <string>

#include "RtmpPush.h"

RtmpPush *rtmpPush = NULL;


extern "C"
JNIEXPORT void JNICALL
Java_com_xyyy_livepusher_push_PushVideo_initPush(JNIEnv *env, jobject instance, jstring pushUrl_) {
    const char *pushUrl = env->GetStringUTFChars(pushUrl_, 0);

    rtmpPush = new RtmpPush(pushUrl);
    rtmpPush->init();

    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}