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
    XYCallJava *xyCallJava= NULL;

public:
    RtmpPush(const char *url,XYCallJava *xyCallJava);
    ~RtmpPush();
    void init();

};

#endif //LIVE_RTMPPUSH_H
