package com.ums.upos.uapi.device.beeper;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.socsi.exception.SDKException;
import com.socsi.smartposapi.terminal.TerminalManager;

/**
 * 蜂鸣器操作对象
 * Created by wangyuan on 17/4/20.
 */

public class BeeperStub extends Beeper.Stub {

    private Context context;

    public BeeperStub(Context context) {
        this.context = context;
    }

    /**
     * 蜂鸣
     *
     * @param mode 蜂鸣模式(见 BeepModeConstrants 类定义)
     */
    @Override
    public void beep(int mode) throws RemoteException {
        Log.e("WY", "beep mode "+ mode);
        if (mode < 0 || mode > 4) {
            mode = 0;
        }
        try {
            TerminalManager.getInstance().beep(context, mode);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }
}
