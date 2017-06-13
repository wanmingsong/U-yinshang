package com.ums.upos.uapi.device.modem;

import android.os.Bundle;
import android.os.RemoteException;

public class ModemDriverStub extends ModemDriver.Stub{
    /**
     * 初始化拨号模块
     * @param data 各厂家Modem模块底层参数(见ModemConfig类定义)
     * @throws RemoteException
     */
    @Override
    public void initModem(Bundle data) throws RemoteException {

    }

    /**
     * 建立拨号连接
     * @param dialParam 拨号参数(见DialParam类定义)
     * @param listener 拨号监听器(见OnDialListener类定义)
     * @return 拨号状态
     * @throws RemoteException
     */
    @Override
    public int connect(DialParam dialParam, OnDialListener listener) throws RemoteException {
        return 0;
    }

    /**
     * 检测拨号状态
     * @return 是否连接
     * @throws RemoteException
     */
    @Override
    public boolean isConnected() throws RemoteException {
        return false;
    }

    /**
     * 断开拨号连接
     * @throws RemoteException
     */
    @Override
    public void disconnect() throws RemoteException {

    }

    /**
     * 发送数据
     * @param buffer 发送数据
     * @return 发送状态
     * @throws RemoteException
     */
    @Override
    public int send(byte[] buffer) throws RemoteException {
        return 0;
    }

    /**
     * 接收数据
     * @param buffer 接收数据
     * @return 接收状态
     * @throws RemoteException
     */
    @Override
    public int recv(byte[] buffer) throws RemoteException {
        return 0;
    }

    /**
     * 清空缓冲区
     * @throws RemoteException
     */
    @Override
    public void clrBuffer() throws RemoteException {

    }
}