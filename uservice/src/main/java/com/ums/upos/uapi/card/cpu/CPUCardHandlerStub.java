package com.ums.upos.uapi.card.cpu;

import android.os.RemoteException;

public class CPUCardHandlerStub extends CPUCardHandler.Stub {
    /**
     * 模块上电
     * @param atr 上电结果 (ASCII格式)
     * @return 是否上电成功
     * @throws RemoteException
     */
    @Override
    public boolean setPowerOn(byte[] atr) throws RemoteException {
        return false;
    }

    /**
     * 模块下电
     * @throws RemoteException
     */
    @Override
    public void setPowerOff() throws RemoteException {

    }

    /**
     * 交换APDU指令
     * @param cmd APDU请求指令
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int exchangeAPDUCmd(APDUCmd cmd) throws RemoteException {
        return 0;
    }

    /**
     * 卡片挂起
     * @return 是否挂起成功
     * @throws RemoteException
     */
    @Override
    public boolean halt() throws RemoteException {
        return false;
    }

    /**
     * 卡片激活
     * @return 是否激活成功
     * @throws RemoteException
     */
    @Override
    public boolean active() throws RemoteException {
        return false;
    }
}