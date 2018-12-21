package com.xyyy.livepusher.push;

import android.content.Context;

/**
 * @author liuml
 * @explain
 * @time 2018/12/11 16:41
 */
public class XYPushEncodec extends XYBasePushEncoder {

    private XYEncoderPushRender xyEncodecRender;

    public XYPushEncodec(Context context, int textureId) {
        super(context);
        xyEncodecRender = new XYEncoderPushRender(context, textureId);
        setRender(xyEncodecRender);
        setmRenderMode(XYBasePushEncoder.RENDERMODE_CONTINUOUSLY);

    }
}
