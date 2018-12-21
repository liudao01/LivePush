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
    xyCallJava  = new XYCallJava(javaVM,env,&instance);
    rtmpPush = new RtmpPush(pushUrl,xyCallJava);
    rtmpPush->init();

    env->ReleaseStringUTFChars(pushUrl_, pushUrl);
}


extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved)
{
    javaVM = jvm;
    JNIEnv* env;
    if (jvm->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK)
    {
        if(LOG_SHOW)
        {
            LOGE("GetEnv failed!");
        }
        return -1;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved){
    javaVM = NULL;
}