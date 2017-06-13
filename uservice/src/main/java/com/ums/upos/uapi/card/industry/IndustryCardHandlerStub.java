package com.ums.upos.uapi.card.industry;

import android.os.RemoteException;

public class IndustryCardHandlerStub extends IndustryCardHandler.Stub{
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
     * 交换行业卡指令
     * @param cmd 行业卡请求指令
     * @return
     * @throws RemoteException
     */
    @Override
    public int exchangeIndustryCardCmd(IndustryCardCmd cmd) throws RemoteException {
        return 0;
    }

    /**
     * 获取行业卡类型
     * @return 行业卡类型
     * @throws RemoteException
     */
    @Override
    public String getCardType() throws RemoteException {
        return null;
    }
}