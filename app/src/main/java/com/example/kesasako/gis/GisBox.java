package com.example.kesasako.gis;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kesasako on 2015/01/13.
 */
public class GisBox {
    public LatLng leftTop = null;
    public LatLng rightBottom = null;

    public GisBox(LatLng srcLeftTop, LatLng srcRightBottom) {
        leftTop = srcLeftTop;
        rightBottom = srcRightBottom;
    }
}
