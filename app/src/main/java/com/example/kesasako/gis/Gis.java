package com.example.kesasako.gis;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kesasako on 2015/01/13.
 */
public class Gis {

    static double LAT_DEF = 30.8184;
    static double LONG_DEF = 25.2450;
    static double ANGLE_DEF = 0.000277778;

    static public GisBox getBox(LatLng center, int radius) {
        double latLeft = center.latitude - (radius / LONG_DEF * ANGLE_DEF);
        double latRight = center.latitude + (radius / LAT_DEF * ANGLE_DEF);

        double longTop = center.longitude + (radius / LONG_DEF * ANGLE_DEF);
        double longBottom = center.longitude - (radius / LONG_DEF * ANGLE_DEF);

        LatLng leftTop = new LatLng(latLeft, longTop);
        LatLng rightBottom = new LatLng(latRight, longBottom);

        return new GisBox(leftTop, rightBottom);
    }
//    緯度：35.71654578
//    経度：139.777254
//
//
//    範囲を指定するために、基準の緯度経度に500m足した緯度経度と500mマイナスした緯度経度を求める。
//            ※このやり方だと大体500mの距離内にあるものを取得することができるが正確なデータは取得できない。
//
//
//            500m足した緯度・経度を求める
//
//                      緯度(500mプラス) ＝ 基準の緯度 + (範囲 ÷ 1秒当たりの緯度 × 1秒当たりの度)
//            ＝ 35.71654578 + (500 ÷ 30.8184 × 0.000277778)
//            ＝ 35.721052470808088674298471043273
//            ≒ 35.72105247
//
//    経度(500mプラス) ＝ 基準の経度 + (範囲 ÷ 1秒当たりの緯度 × 1秒当たりの度)
//            ＝ 139.777254 + (500 ÷ 25.2450 × 0.000277778)
//            ＝ 139.78275564388987918399683105565
//            ≒ 139.782756
//
//
//    次に500mマイナスした緯度・経度を求める
//
//                   緯度(500mマイナス) ＝ 基準の緯度 – (範囲 ÷ 1秒当たりの緯度 × 1秒当たりの度)
//            ＝ 35.71654578 – (500 ÷ 30.8184 × 0.000277778)
//            ＝ 35.712039056743763465981361783869
//            ≒ 35.71203906
//
//    経度(500mマイナス) ＝ 基準の経度 – (範囲 ÷ 1秒当たりの緯度 × 1秒当たりの度)
//            ＝ 139.777254 – (500 ÷ 25.2450 × 0.000277778)
//            ＝ 139.77175235611012081600316894435
//            ≒ 139.771752
//
//
//    緯度の範囲は 35.71203906 ～ 35.72105247
//    経度の範囲は 139.771752 ～ 139.782756
}
