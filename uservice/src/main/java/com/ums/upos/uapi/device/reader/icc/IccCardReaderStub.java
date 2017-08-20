package com.ums.upos.uapi.device.reader.icc;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.Toast;

import com.socsi.smartposapi.card.CardReader;
import com.socsi.smartposapi.card.IcCardSearchCallback;
import com.socsi.smartposapi.card.MagCardSearchCallback;
import com.socsi.smartposapi.card.RfSearchCallback;
import com.socsi.smartposapi.emv2.EmvL2;
import com.socsi.smartposapi.icc.Icc;
import com.ums.upos.uapi.emv.OnEmvProcessListener;

public class IccCardReaderStub extends IccCardReader.Stub {
    private Context mContext;

    /**
     * 单例对象
     */
    private static volatile IccCardReaderStub iccCardReaderStub;

    private IccCardReaderStub(Context context) {
        this.mContext = context;
    }
    /**
     * 获取IccCardReaderStub单例
     *
     * @param context 上下文
     * @return EmvHandlerStub单例
     */
    public static IccCardReaderStub getInstance(Context context) {
        if (iccCardReaderStub == null) {
            synchronized (IccCardReaderStub.class) {
                if (iccCardReaderStub == null) {
                    iccCardReaderStub = new IccCardReaderStub(context);
                }
            }
        }
        return iccCardReaderStub;
    }
    /**
     * 寻卡
     *
     * @param listener 芯片卡操作监听实例
     * @param timeout  超时时间(单位:秒)
     * @param cardType 寻卡类型(见IccCardType类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int searchCard(final OnSearchIccCardListener listener, int timeout, final String[] cardType) throws RemoteException {
        if (listener == null) {
            return -2;
        }
        if (cardType == null) {
            Bundle bundle = new Bundle();
            listener.onSearchResult(-2, bundle);
            return -2;
        }
        CardReader cardReader = CardReader.getInstance();
        IcCardSearchCallback icCardSearchCallback = new IcCardSearchCallback() {
            @Override
            public void onSearchResult(int i) {
                try {
                    switch (i) {
                        case CardReader.SUCCESS:
                            String mCardType = "";
                            for (String type:cardType){
                                if (type.equals("CPUCARD")){
                                    mCardType = IccCardType.CPUCARD;
                                }
                            }

                            if (mCardType.equals("CPUCARD")){
                                Bundle bundle = new Bundle();
                                bundle.putString("CardType", IccCardType.CPUCARD);
                                bundle.putByteArray("m1_sn", null);
                                listener.onSearchResult(0, bundle);
                            }else {
                                Bundle bundle = new Bundle();
                                listener.onSearchResult(-6001, bundle);
                            }
                            break;
                        case CardReader.OVERTIME:
                            Bundle bundle = new Bundle();
                            listener.onSearchResult(-3, bundle);
                            break;
                        case 2:
                            //卡片已插入但是未知卡片类型
                            Bundle bundle2 = new Bundle();
                            listener.onSearchResult(-6001, bundle2);
                            break;
                        case CardReader.FAIL:
                            //TODO 传一个空的bundle？
                            Bundle bundle3 = new Bundle();
                            listener.onSearchResult(-2, bundle3);
                            break;
                        default:
                            break;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };

        RfSearchCallback rfSearchCallback = new RfSearchCallback() {
            @Override
            public void onSearchResult(int i, int i1, byte[] bytes, byte[] bytes1) {
                //0 成功success,1 超时timeout,-1 异常exception
                try {
                    switch (i) {
                        case 0:
                            String m1Card = "";
                            String cpuCard = "";
                            for (String type:cardType){
                                if (type.equals("M1CARD")){
                                    m1Card = IccCardType.M1CARD;
                                }
                                if (type.equals("CPUCARD")){
                                    cpuCard = IccCardType.CPUCARD;
                                }
                            }

                            if (i1 == 0xff){
                                Bundle bundle = new Bundle();
                                listener.onSearchResult(-6001, bundle);
                            } else if (i1 == 0x44) {
                                if (m1Card.equals("M1CARD")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("CardType", IccCardType.M1CARD);
                                    bundle.putByteArray("m1_sn", bytes);
                                    listener.onSearchResult(0, bundle);
                                }else {
                                    Bundle bundle = new Bundle();
                                    listener.onSearchResult(-6001, bundle);
                                }
                            }else {
                                if (cpuCard.equals("CPUCARD")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("CardType", "CPUCARD");
                                    bundle.putByteArray("m1_sn", null);
                                    listener.onSearchResult(0, bundle);
                                } else {
                                    Bundle bundle = new Bundle();
                                    listener.onSearchResult(-6001, bundle);
                                }
                            }
                            break;
                        case 1:
                            Bundle bundle = new Bundle();
                            listener.onSearchResult(-3, bundle);//超时
                            break;
                        case -1:
                            Bundle bundle2 = new Bundle();
                            listener.onSearchResult(-2, bundle2);//异常错误
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
        cardReader.setRfSearchCallback(rfSearchCallback);
        cardReader.searchCard(6, timeout);
        return 0;
    }

    /**
     * 停止寻卡
     *
     * @throws RemoteException
     */
    @Override
    public void stopSearch() throws RemoteException {
        CardReader.getInstance().stopSearch(6);
    }

    /**
     * 判断卡是否存在
     *
     * @return 卡是否存在
     * @throws RemoteException
     */
    @Override
    public boolean isCardExists() throws RemoteException {
        return false;
    }

    /**
     * 设置卡槽属性
     *
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