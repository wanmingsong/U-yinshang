package com.ums.upos.uapi.card.cpu;

import android.os.RemoteException;

import com.socsi.exception.SDKException;
import com.socsi.smartposapi.icc.Icc;
import com.socsi.utils.StringUtil;

public class CPUCardHandlerStub extends CPUCardHandler.Stub {
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
     * 交换APDU指令
     * @param cmd APDU请求指令
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int exchangeAPDUCmd(APDUCmd cmd) throws RemoteException {
        try {
            byte[] result = Icc.getInstance().IsoCommand(Icc.INIT_TYEP_IC, cmd.getDataIn());
            return StringUtil.byteToInt(result);
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 卡片挂起
     * @return 是否挂起成功
     * @throws RemoteException
     */
    @Override
    public boolean halt() throws RemoteException {
        try {
            int result = Icc.getInstance().Icc_Ready();
            if (result == 2 || result == 8){
                return true;
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 卡片激活
     * @return 是否激活成功
     * @throws RemoteException
     */
    @Override
    public boolean active() throws RemoteException {
        byte result = Icc.getInstance().checkCardOn((byte) 0x00);
        return result == 0x02;
    }
}