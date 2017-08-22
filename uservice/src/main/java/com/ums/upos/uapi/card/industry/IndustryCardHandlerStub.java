package com.ums.upos.uapi.card.industry;

import android.os.RemoteException;

import com.socsi.exception.SDKException;
import com.socsi.smartposapi.icc.Icc;

public class IndustryCardHandlerStub extends IndustryCardHandler.Stub{
    /**
     * 模块上电
     * @param atr 上电结果 (ASCII格式)
     * @return 是否上电成功
     * @throws RemoteException
     */
    @Override
    public boolean setPowerOn(byte[] atr) throws RemoteException {
        try {
            byte[] result = Icc.getInstance().init(Icc.INIT_TYEP_IC);
            System.arraycopy(result, 0, atr, 0, result.length);
            return true;
        } catch (SDKException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 模块下电
     * @throws RemoteException
     */
    @Override
    public void setPowerOff() throws RemoteException {
        try {
            Icc.getInstance().close(Icc.INIT_TYEP_IC);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    /**
     * 交换行业卡指令
     * @param cmd 行业卡请求指令
     * @return 行业卡应答结果
     * @throws RemoteException
     */
    @Override
    public int exchangeIndustryCardCmd(IndustryCardCmd cmd) throws RemoteException {
        byte[] data = new byte[6 + cmd.getLc()];
        data[0] = cmd.getCla();
        data[1] = cmd.getIns();
        data[2] = cmd.getP1();
        data[3] = cmd.getP2();
        data[4] = (byte) cmd.getLc();
        System.arraycopy(cmd.getDataIn(), 0, data, 5, cmd.getLc());
        data[5 + cmd.getLc()] = (byte) cmd.getLe();
        try {
            Icc.getInstance().IsoCommand(Icc.INIT_TYEP_IC, data);
        } catch (SDKException e) {
            e.printStackTrace();
        }
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