package com.ums.upos.uapi.device.reader.mag;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.socsi.command.magcard.CmdMagcard;
import com.socsi.exception.SDKException;
import com.socsi.smartposapi.card.CardReader;
import com.socsi.smartposapi.card.MagCardSearchCallback;
import com.socsi.smartposapi.magcard.CardInfo;
import com.socsi.smartposapi.magcard.Magcard;

public class MagCardReaderStub extends MagCardReader.Stub {
    /**
     * 单例对象
     */
    private static volatile MagCardReaderStub magCardReaderStub;

    /**
     * 获取EmvHandlerStub单例
     *
     * @return EmvHandlerStub单例
     */
    public static MagCardReaderStub getInstance() {
        if (magCardReaderStub == null) {
            synchronized (MagCardReaderStub.class) {
                if (magCardReaderStub == null) {
                    magCardReaderStub = new MagCardReaderStub();
                }
            }
        }
        return magCardReaderStub;
    }
    /**
     * 寻卡
     * @param listener 磁条读卡器操作监听器
     * @param timeout 寻卡超时时间<=0(永不超时)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int searchCard(final OnSearchMagCardListener listener, int timeout) throws RemoteException {
//        try {
//            Magcard magcard = Magcard.getInstance();
//            magcard.Mag_Open();
//            if(listener != null){
//                switch (magcard.search(timeout)){
//                    case MagCardConstant.SEARCH_CARD_SUCC:
//                        MagCardInfoEntity entity = new MagCardInfoEntity();
//                        CardInfo info = magcard.readUnEncryptTrack();
//                        entity.setCardNo(info.getCardNo());
//                        entity.setTk1(info.getTk1());
//                        entity.setTk1ValidResult(info.getTk1ValidResult());
//                        entity.setTk2(info.getTk2());
//                        entity.setTk2ValidResult(info.getTk2ValidResult());
//                        entity.setTk3(info.getTk3());
//                        entity.setTk3ValidResult(info.getTk3ValidResult());
//                        listener.onSearchResult(MagCardConstant.RESULT_SUCC,entity);
//                        break;
//                    case MagCardConstant.SEARCH_CARD_NON_CARD:
//                        listener.onSearchResult(MagCardConstant.RESULT_OVER_TIME,null);
//                        break;
//                }
//            }else{
//                listener.onSearchResult(-2,null);
//            }
//
//
//        } catch (SDKException e) {
//            e.printStackTrace();
//        }
        if (listener != null) {
            CardReader cardReader = CardReader.getInstance();
            MagCardSearchCallback magCardSearchCallback = new MagCardSearchCallback() {
                @Override
                public void onSearchResult(int i) {
                    try {
                        switch (i) {
                            case CardReader.SUCCESS:
//                                CardInfo info = CmdMagcard.getInstance().readTrackUnEncrypt(false);//boolean 是否判断二磁道(银联卡 和会员卡)
                                CardInfo info = Magcard.getInstance().readUnEncryptTrack();
                                MagCardInfoEntity entity = new MagCardInfoEntity();
                                entity.setCardNo(info.getCardNo());
                                entity.setTk1(info.getTk1());
                                entity.setTk1ValidResult(info.getTk1ValidResult());
                                entity.setTk2(info.getTk2());
                                entity.setTk2ValidResult(info.getTk2ValidResult());
                                entity.setTk3(info.getTk3());
                                entity.setTk3ValidResult(info.getTk3ValidResult());
                                listener.onSearchResult(MagCardConstant.RESULT_SUCC, entity);
                                break;
                            case CardReader.OVERTIME:
                                listener.onSearchResult(MagCardConstant.RESULT_OVER_TIME, null);
                                break;
                            case CardReader.FAIL:
                                break;
                            default:
                                break;
                        }
                    } catch (SDKException | RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            cardReader.setMagCardSearchCallback(magCardSearchCallback);
            cardReader.searchCard(1, timeout);
            return 0;
        }
        return -2;
    }

    /**
     * 手机支付平台模式寻卡
     * @param listener 磁条读卡器操作监听器
     * @param timeout 寻卡超时时间   <=0(永不超时)
     * @param data 数据
     * @return 返回结果
     * @throws RemoteException
     */
    @Override
    public int searchCardEx(OnSearchMagCardListener listener, int timeout, Bundle data) throws RemoteException {
        return 0;
    }

    /**
     * 停止寻卡
     * @throws RemoteException
     */
    @Override
    public void stopSearch() throws RemoteException {
        CardReader.getInstance().stopSearch(1);
    }

    /**
     * 设置Lrc校验开关
     * @param isCheckLrc 是否校验磁道LRC
     * @throws RemoteException
     */
    @Override
    public void setIsCheckLrc(boolean isCheckLrc) throws RemoteException {
        try {
            Magcard.getInstance().setIsCheckLrc(isCheckLrc);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }
}