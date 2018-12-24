//
// Created by liuml on 2018/12/21.
//

#ifndef LIVE_RTMPPUSH_H
#define LIVE_RTMPPUSH_H

#include <malloc.h>
#include <string.h>
#include "XYQueue.h"
#include "XYCallJava.h"

extern "C" {
#include "librtmp/rtmp.h"
};

class RtmpPush {
public:
    RTMP *rtmp = NULL;
    char *url = NULL;
    XYQueue *xyQueue = NULL;
    pthread_t push_thread = NULL;
    XYCallJava *xyCallJava = NULL;
    bool startPushing = false;
    long startTime = 0;
public:
    RtmpPush(const char *url, XYCallJava *xyCallJava);

    ~RtmpPush();

    void init();

    void pushSPSPPS(char *sps, int sps_len, char *pps, int pps_len);

    void pushVideoData(char *data, int data_len, bool keyframe);
};

#endif //LIVE_RTMPPUSH_H
