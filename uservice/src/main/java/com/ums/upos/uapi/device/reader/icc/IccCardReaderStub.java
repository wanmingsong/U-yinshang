package com.ums.upos.uapi.device.reader.icc;

import android.os.Bundle;
import android.os.RemoteException;

import com.socsi.smartposapi.card.CardReader;
import com.socsi.smartposapi.card.IcCardSearchCallback;

public class IccCardReaderStub extends IccCardReader.Stub{
    /**
     * 寻卡
     * @param listener 芯片卡操作监听实例
     * @param timeout 超时时间(单位:秒)
     * @param cardType 寻卡类型
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int searchCard(final OnSearchIccCardListener listener, int timeout, String[] cardType) throws RemoteException {
        if(listener != null){
                CardReader cardReader = CardReader.getInstance();
                IcCardSearchCallback icCardSearchCallback = new IcCardSearchCallback() {
                    @Override
                    public void onSearchResult(int i) {
                        try {
                            switch (i) {
                                case CardReader.SUCCESS:
                                    break;
                                case CardReader.OVERTIME:
                                    listener.onSearchResult(-3, null);
                                    break;
                                case CardReader.FAIL:
                                    break;
                                default:
                                    break;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                };
                cardReader.setIcCardSearchCallback(icCardSearchCallback);
                cardReader.searchCard(2, timeout);
            return 0;
        }
        return -2;
    }

    /**
     * 停止寻卡
     * @throws RemoteException
     */
    @Override
    public void stopSearch() throws RemoteException {
        CardReader.getInstance().stopSearch(2);
    }

    /**
     * 判断卡是否存在
     * @return 卡是否存在
     * @throws RemoteException
     */
    @Override
    public boolean isCardExists() throws RemoteException {
        return false;
    }

    /**
     * 设置卡槽属性
     * @param bundle bundle卡槽参数(见ReaderConfig类描述)
     * @return 卡是否在位
     * @throws RemoteException
     */
    @Override
    public boolean setupReaderConfig(Bundle bundle) throws RemoteException {
        if (bundle != null) {
            bundle.getString(ReaderConfig.COMMON_SLOT_CHANNEL, "");

            return true;
        }
        return false;
    }
}