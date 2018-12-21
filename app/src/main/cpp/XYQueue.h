//
// Created by liuml on 2018/12/21.
//

#ifndef LIVE_XYQUEUE_H
#define LIVE_XYQUEUE_H

#include "queue"
#include "pthread.h"
#include "AndroidLog.h"

extern "C" {
#include "librtmp/rtmp.h"
};

class XYQueue {

public:
    std::queue<RTMPPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;


public:
    XYQueue();

    ~XYQueue();

    int putRtmpPacket(RTMPPacket *packet);

    RTMPPacket *getRtmpPacket();

    void clearQueue();

    void notifyQueue();

};

#endif //LIVE_XYQUEUE_H
