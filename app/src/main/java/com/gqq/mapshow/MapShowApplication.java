package com.gqq.mapshow;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by gqq on 16/12/22.
 */

public class MapShowApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // 推荐使用的方法：Application里面进行SDK的初始化
        SDKInitializer.initialize(this);
    }
}
