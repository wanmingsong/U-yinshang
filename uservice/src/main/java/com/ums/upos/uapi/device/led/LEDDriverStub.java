package com.ums.upos.uapi.device.led;

import android.os.RemoteException;

import com.socsi.exception.SDKException;
import com.socsi.smartposapi.terminal.TerminalManager;

/**
 * LED 灯操作对象
 * Created by wangyuan on 17/4/20.
 */

public class LEDDriverStub extends LEDDriver.Stub {
    /**
     * 单例对象
     */
    private static volatile LEDDriverStub ledDriverStub;

    /**
     * 获取EmvHandlerStub单例
     *
     * @return EmvHandlerStub单例
     */
    public static LEDDriverStub getInstance() {
        if (ledDriverStub == null) {
            synchronized (LEDDriverStub.class) {
                if (ledDriverStub == null) {
                    ledDriverStub = new LEDDriverStub();
                }
            }
        }
        return ledDriverStub;
    }

    /**
     * LED 灯操作
     *
     * @param light LED 灯操作位(见 LEDLightConstrants 类定义)
     * @param isOn  亮暗操作(true:亮 false:暗
     */
    @Override
    public void setLed(int light, boolean isOn) throws RemoteException {
        byte type;
        if (light == LEDLightConstrants.RED) {
            //红灯
            type = 0x08;
        } else if (light == LEDLightConstrants.GREEN) {
            //绿灯
            type = 0x02;
        } else if (light == LEDLightConstrants.YELLOW) {
            //黄灯
            type = 0x04;
        } else if (light == LEDLightConstrants.BLUE) {
            //蓝灯
            type = 0x01;
        } else {
            return;
        }
        byte status = 0;
        if (isOn) {
            status = 1;
        }
        try {
            TerminalManager.getInstance().indicatorLight(type, status);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }
}
