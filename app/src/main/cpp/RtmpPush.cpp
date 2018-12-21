//
// Created by liuml on 2018/12/21.
//


#include "RtmpPush.h"

RtmpPush::RtmpPush(const char *url,XYCallJava *xyCallJava) {

    this->url = static_cast<char *>(malloc(512));
    strcpy(this->url, url);
    this->xyCallJava = xyCallJava;
    this->xyQueue = new XYQueue();


}

RtmpPush::~RtmpPush() {
    xyQueue->notifyQueue();
    xyQueue->clearQueue();
    free(url);
}

void *callBackPush(void *data) {
    RtmpPush *rtmpPush = static_cast<RtmpPush *>(data);

    rtmpPush->rtmp = RTMP_Alloc();
    RTMP_Init(rtmpPush->rtmp);
    rtmpPush->rtmp->Link.timeout = 10;//超时时间
    rtmpPush->rtmp->Link.lFlags |= RTMP_LF_LIVE;//|是追加
    RTMP_SetupURL(rtmpPush->rtmp, rtmpPush->url);
    RTMP_EnableWrite(rtmpPush->rtmp);

    if (!RTMP_Connect(rtmpPush->rtmp, NULL)) {
        //失败
        LOGE("can not connect the url")
        rtmpPush->xyCallJava->onConnectFail("can not connect the url");
        goto end;
    }

    if (!RTMP_ConnectStream(rtmpPush->rtmp, 0)) {
        LOGE("can not connect the stream of service")
        rtmpPush->xyCallJava->onConnectFail("can not connect the stream of service");
        goto end;
    }
    LOGD("链接成功");
    rtmpPush->xyCallJava->onConnectsuccess();

//    while (true) {
//
//    }
    end:
    RTMP_Close(rtmpPush->rtmp);
    RTMP_Free(rtmpPush->rtmp);
    rtmpPush->rtmp = NULL;

    pthread_exit(&rtmpPush->push_thread);
}

void RtmpPush::init() {

    xyCallJava->onConnectint(XY_THREAD_MAIN);
    pthread_create(&push_thread, NULL, callBackPush, this);

}
