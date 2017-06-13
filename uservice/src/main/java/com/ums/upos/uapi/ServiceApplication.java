package com.ums.upos.uapi;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * 服务的Application
 * Created by wangyuan on 17/4/21.
 */

public class ServiceApplication extends Application{

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
