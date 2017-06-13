package com.ums.upos.uapi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.ums.upos.uapi.engine.DeviceServiceEngineStub;

public class UService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new DeviceServiceEngineStub(getApplicationContext());
    }
}



