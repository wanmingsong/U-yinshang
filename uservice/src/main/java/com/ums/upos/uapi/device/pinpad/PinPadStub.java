package com.ums.upos.uapi.device.pinpad;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.socsi.aidl.pinservice.OperationPinListener;
import com.socsi.exception.PINPADException;
import com.socsi.exception.SDKException;
import com.socsi.smartposapi.ped.Ped;
import com.socsi.utils.StringUtil;

public class PinPadStub extends PinPad.Stub {
    private Context mContex;
    /**
     * 单例对象
     */
    private static volatile PinPadStub pinPadStub;

    private PinPadStub(Context context) {
        this.mContex = context;
    }

    /**
     * 获取EmvHandlerStub单例
     *
     * @param context 上下文
     * @return EmvHandlerStub单例
     */
    public static PinPadStub getInstance(Context context) {
        if (pinPadStub == null) {
            synchronized (PinPadStub.class) {
                if (pinPadStub == null) {
                    pinPadStub = new PinPadStub(context);
                }
            }
        }
        return pinPadStub;
    }

    /**
     * 密码键盘初始化
     *
     * @param type 密码键盘类型(见PinPadType类声明)
     * @return 初始化结果
     * @throws RemoteException
     */
    @Override
    public int initPinPad(int type) throws RemoteException {
        //TODO 内置外置键盘初始化？
        if (type == PinPadType.INTERNAL) {
            //内置键盘
            return 0;
        }
        if (type == PinPadType.EXTERNAL) {
            //外接键盘
            Log.v("initPinPad", "没有外接键盘");
            return 0;
        }
        return -7043;
    }

    /**
     * 加载明文主密钥
     *
     * @param mKeyIdx    主密钥索引
     * @param keyData    主密钥明文
     * @param keyDataLen 主密钥明文长度
     * @param isTmsKey   是否TMS主控密钥
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int loadPlainMKey(int mKeyIdx, byte[] keyData, int keyDataLen, boolean isTmsKey) throws RemoteException {
        //TODO decMKeyIdx解密密钥索引参数如何填
        //TODO 主密钥长度错
        if (keyData == null) {
            return -2;
        }
        if (mKeyIdx < 1 || mKeyIdx > 100) {
            return -7012;
        }
        if (keyDataLen != 8 && keyDataLen != 16 || keyData.length < keyDataLen) {
            return -7014;
        }
        byte[] byteArray = new byte[keyDataLen];
        System.arraycopy(keyData, 0, byteArray, 0, keyDataLen);
        try {
            boolean result = Ped.getInstance().loadMKey((byte) 0xff, (byte) mKeyIdx, StringUtil.byte2HexStr(byteArray), 1, Ped.KEY_TYPE_UNENCTYPTED_KEY, null, isTmsKey);
            if (result) {
                return 0;
            }
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 加载密文主密钥
     *
     * @param mKeyIdx    主密钥索引
     * @param keyData    主密钥密文
     * @param keyDataLen 主控密钥密文长度
     * @param decMKeyIdx 解密密文的密钥索引
     * @param isTmsKey   是否TMS主控密钥
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int loadEncryptMKey(int mKeyIdx, byte[] keyData, int keyDataLen, int decMKeyIdx, boolean isTmsKey) throws RemoteException {
        //TODO loadMkey decMkeyIdx keyType参数如何填
        if (keyData == null) {
            return -2;
        }
        if (mKeyIdx < 1 || mKeyIdx > 100) {
            return -7012;
        }
        if (keyDataLen != 8 && keyDataLen != 16 || keyData.length < keyDataLen) {
            return -7014;
        }
        byte[] byteArray = new byte[keyDataLen];
        System.arraycopy(keyData, 0, byteArray, 0, keyDataLen);
        try {
            boolean result = Ped.getInstance().loadMKey((byte) 0x02, (byte) mKeyIdx, StringUtil.byte2HexStr(byteArray), decMKeyIdx, Ped.KEY_TYPE_ENCTYPTED_KEY, null, isTmsKey);
            if (result) {
                return 0;
            }
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
            return -7001;
        }
        return -1;
    }

    /**
     * 母POS导密钥
     *
     * @param portNo  接收串口号
     * @param keyType 密钥类型（见MainKeyType类说明）
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int loadKeyByCom(int portNo, int keyType) throws RemoteException {

        return 0;
    }

    /**
     * 加载工作密钥
     *
     * @param mKeyIdx    主密钥索引
     * @param keyType    密钥类型(见WorkKeyType类声明)
     * @param keyData    工作密钥密文
     * @param keyDataLen 工作密钥密文长度
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int loadWKey(int mKeyIdx, int keyType, byte[] keyData, int keyDataLen) throws RemoteException {
        if (keyData == null || keyType == 3) {
            return -2;
        }
        if (keyData.length < keyDataLen) {
            return -7014;
        }
        byte[] byteArray = new byte[keyDataLen];
        System.arraycopy(keyData, 0, byteArray, 0, keyDataLen);
        try {
            byte mKeyType = 0;
            switch (keyType) {
                case 0:
                    mKeyType = 0x02;
                    break;
                case 1:
                    mKeyType = 0x03;
                    break;
                case 2:
                    mKeyType = 0x01;//磁道密钥
                    break;
//                case 3:
//                    mKeyType = 0x12;//PIN密钥（SM4算法）
//                    break;
                case 4:
                    mKeyType = 0x13;
                    break;
                case 5:
                    mKeyType = 0x11;//磁道密钥（SM4算法）
                    break;
                default:
                    break;
            }
            boolean succ = Ped.getInstance().loadWorkKey((byte) mKeyIdx, mKeyType, StringUtil.byte2HexStr(byteArray), null);
            if (succ) {
                return 0;
            }
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
            return -7001;
        }
        return -1;
    }

    /**
     * 加载明文Des密钥
     *
     * @param keyIdx  DES密钥索引
     * @param keyData 密钥密文
     * @param keyLen  密钥密文长度
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int loadPlainDesKey(int keyIdx, byte[] keyData, int keyLen) throws RemoteException {
        if (keyData == null) {
            return -2;
        }
        if (keyIdx < 1 || keyIdx > 100) {
            return -7012;
        }
        //数据长度的判断
        if (keyLen != 8 && keyLen != 16 || keyData.length < keyLen) {
            return -7014;
        }
        byte[] byteArray = new byte[keyLen];
        System.arraycopy(keyData, 0, byteArray, 0, keyLen);
        try {
//            byte[] succ = Ped.getInstance().DESComputation((byte) 0x00, (byte) keyIdx, (byte) 0x00, (byte) 0x00, byteArray);
            boolean succ = Ped.getInstance().loadPlainDesKey((byte) keyIdx, StringUtil.byte2HexStr(byteArray));
            if (succ) {
                return 0;
            }
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 计算工作密钥
     *
     * @param mKeyIdx  主密钥索引
     * @param wKeyType 工作密钥类型(见WorkKeyType类声明)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public byte[] calcWKeyKCV(int mKeyIdx, int wKeyType) throws RemoteException {
        byte mKeyType = 0;
        switch (wKeyType) {
            case 0:
                mKeyType = 0x02;
                break;
            case 1:
                mKeyType = 0x03;
                break;
            case 2:
                mKeyType = 0x01;//磁道密钥
                break;
            case 3:
                mKeyType = 0x12;
                break;
            case 4:
                mKeyType = 0x13;
                break;
            case 5:
                mKeyType = 0x11;//磁道密钥（SM4算法）
                break;
            default:
                break;
        }
        try {
            byte[] result = Ped.getInstance().calculateWKeyKCV((byte) mKeyIdx, mKeyType);
            byte[] byteArray = new byte[4];
            System.arraycopy(result, 0, byteArray, 0, 4);
            return byteArray;
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算MAC
     *
     * @param mKeyIdx 主密钥索引
     * @param mode    算法类型(见MacAlgorithmType类声明)
     * @param type    Des算法类型(见DesAlgorithmType类声明)
     * @param data    待算MAC加密数据
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public byte[] getMac(int mKeyIdx, int mode, int type, byte[] data) throws RemoteException {
        //TODO CBCInitVec填null?
        if (mKeyIdx < 1 || mKeyIdx > 100 || data == null) {
            return null;
        }
        try {
            byte mType = 0;
            switch (type) {
                case 0:
                    mType = 0x00;//DES
                    break;
                case 1:
                    mType = 0x01;//TDES
                    break;
                case 2:
                    mType = 0x02;//SM4
                    break;
                default:
                    break;
            }
            byte mMode = 0;
            switch (mode) {
                case 0:
                    mMode = 0x02;//ECB
                    break;
                case 1:
                    mMode = 0x04;//CBC
                    break;
                default:
                    break;
            }
            String result = Ped.getInstance().calculateMAC((byte) mKeyIdx, mMode, StringUtil.byte2HexStr(data), new byte[8], mType).getMAC();
            return StringUtil.hexStr2Bytes(result);
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用除PinKey外的工作钥密进行DES加密
     *
     * @param mKeyIdx   主密钥索引
     * @param wKeyType  工作密钥类型(见WorkKeyType类声明)
     * @param data      待DES加密数据
     * @param dataLen   数据长度
     * @param desType   Des算法类型(见DesAlgorithmType类声明)
     * @param desResult des计算结果
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int desEncByWKey(int mKeyIdx, int wKeyType, byte[] data, int dataLen, int desType, byte[] desResult) throws RemoteException {
        //TODO encryptMode 怎么填
        if (mKeyIdx < 1 || mKeyIdx > 100){
            return -2;
        }
        if (wKeyType == 0){
            return -7010;
        }
        if (data == null || desResult == null){
            return -2;
        }
        if (dataLen != 8 && dataLen != 16 || dataLen > data.length){
            return -2;
        }
        if (desType == 2) {
            return -2;
        }
        try {
            byte mKeyType = 0;
            switch (wKeyType) {
//                case 0:
//                    mKeyType = 0x02;
//                    break;
                case 1:
                    mKeyType = 0x03;
                    break;
                case 2:
                    mKeyType = 0x01;//磁道密钥
                    break;
                case 3:
                    mKeyType = 0x12;
                    break;
                case 4:
                    mKeyType = 0x13;
                    break;
                case 5:
                    mKeyType = 0x11;//磁道密钥（SM4）
                    break;
                default:
                    break;
            }
            byte mDesType = 0;
            switch (desType) {
                case 0:
                    mDesType = 0x00;//DES
                    break;
                case 1:
                    mDesType = 0x01;//TDES
                    break;
//                case 2:
//                    mDesType = 0x02;//SM4
//                    break;
                default:
                    break;
            }
            byte[] byteArray = new byte[dataLen];
            System.arraycopy(data, 0, byteArray, 0, dataLen);
            byte[] result = Ped.getInstance().desEncByWKey((byte) mKeyIdx, mKeyType, (byte) 0x01, mDesType, byteArray, new byte[8]);
            if (result != null) {
                System.arraycopy(result, 0, desResult, 0, result.length);
                return 0;
            }
        } catch (SDKException | PINPADException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 使用明文DES运算
     *
     * @param desKeyId DES密钥索引
     * @param data     待算DES数据
     * @param dataLen  数据长度
     * @param desType  Des算法类型(见DesAlgorithmType类声明)
     * @param desMode  Des模式(见DesMode类声明)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public byte[] desByPlainKey(int desKeyId, byte[] data, int dataLen, int desType, int desMode) throws RemoteException {
        if (desKeyId < 1 || desKeyId > 100){
            return null;
        }
        if (data == null){
            return null;
        }
        if (dataLen != 8 && dataLen != 16 || dataLen > data.length){
            return null;
        }
        try {
            byte mDesType = 0;
            switch (desType) {
                case 0:
                    mDesType = 0x00;
                    break;
                case 1:
                    mDesType = 0x01;
                    break;
                case 2:
                    mDesType = 0x02;
                    break;
                default:
                    break;
            }
            byte mDesMode = 0;
            switch (desType) {
                case 0:
                    mDesMode = 0x00;
                    break;
                case 1:
                    mDesMode = 0x01;
                    break;
                default:
                    break;
            }
            byte[] byteArray = new byte[dataLen];
            System.arraycopy(data, 0, byteArray, 0, dataLen);
            return Ped.getInstance().DESComputation((byte) 0x00, (byte) desKeyId, mDesType, mDesMode, byteArray);
        } catch (PINPADException | SDKException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用TMS主密钥进行Des运算
     *
     * @param tmsMKeyId TMS主密钥索引
     * @param data      待计算DES数据
     * @param dataLen   数据长度
     * @param desType   Des算法类型(见DesAlgorithmType类声明)
     * @param desMode   Des模式(见DesMode类声明)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public byte[] desByTmsKey(int tmsMKeyId, byte[] data, int dataLen, int desType, int desMode) throws RemoteException {
        if (tmsMKeyId < 1 || tmsMKeyId > 100){
            return null;
        }
        if (data == null){
            return null;
        }
        if (dataLen != 8 && dataLen != 16 || dataLen > data.length){
            return null;
        }
        if (desMode == 1){
            return null;
        }
        try {
            byte mDesType = 0;
            switch (desType) {
                case 0:
                    mDesType = 0x00;
                    break;
                case 1:
                    mDesType = 0x01;
                    break;
                case 2:
                    mDesType = 0x02;
                    break;
                default:
                    break;
            }
            byte mDesMode = 0;
            switch (desType) {
                case 0:
                    mDesMode = 0x00;
                    break;
                case 1:
                    mDesMode = 0x01;
                    break;
                default:
                    break;
            }
            byte[] byteArray = new byte[dataLen];
            System.arraycopy(data, 0, byteArray, 0, dataLen);
            return Ped.getInstance().DESComputation((byte) 0x01, (byte) tmsMKeyId, mDesType, mDesMode, byteArray);
        } catch (PINPADException | SDKException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除主密钥
     *
     * @param mKeyIdx 主密钥索引
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public boolean deleteMKey(int mKeyIdx) throws RemoteException {
        try {
            return Ped.getInstance().deleteMasterKey((byte) mKeyIdx);
        } catch (PINPADException | SDKException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 格式化密钥区
     *
     * @return 是否成功
     * @throws RemoteException
     */
    @Override
    public boolean format() throws RemoteException {
        try {
            return Ped.getInstance().formatKeyArea();
        } catch (PINPADException | SDKException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取8字节随机数
     *
     * @return 生成的随机数
     * @throws RemoteException
     */
    @Override
    public byte[] getRandom() throws RemoteException {
        try {
            return Ped.getInstance().getRandom((byte) 0x08);
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 输入字符
     *
     * @param listener PinPad输入监听(详见OnPinPadInputListener类定义)
     * @param mode     输入模式(见DispTextMode类声明)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int inputText(final OnPinPadInputListener listener, int mode) throws RemoteException {
        //TODO mode 现实* or 现实原样
        // keyid?
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("isOnline",false);
//        Ped.getInstance().startPinInput(this, keyid, bundle, new OperationPinListener() {
//            @Override
//            public void onInput(int i, int i1) {
//            }
//
//            @Override
//            public void onConfirm(byte[] bytes, boolean b) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(int i) {
//
//            }
//        });
        return 0;
    }

    /**
     * 输入联机
     *
     * @param panBlock   PAN 主账号（使用于传统模式）手机平台模式下传需要参与分散运算的数据
     * @param mKeyId     主密钥索引
     * @param pinAlgMode Pin算法类型(见PinAlgorithmMode类声明)
     * @param listener   PinPad输入监听(详见OnPinPadInputListener类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int inputOnlinePin(byte[] panBlock, int mKeyId, int pinAlgMode, final OnPinPadInputListener listener) throws RemoteException {
        //TODO mode pinAlgMode?
        if (listener == null || panBlock == null) {
            return -2;
        }
        if (mKeyId < 1 || mKeyId > 100){
            return -7012;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isOnline", true);//online
        bundle.putString("pan", StringUtil.byte2HexStr(panBlock));//card number
        bundle.putIntArray("pinLimit", new int[]{0, 6, 7, 8, 9, 10, 11, 12});//allows the length of the input PIN
        bundle.putInt("timeout", 60);//timeout
        bundle.putString("promptString", "请输入联机密码");//tip info
        try {
            Ped.getInstance().startPinInput(mContex, mKeyId, bundle, new OperationPinListener() {
                @Override
                public void onInput(int i, int i1) {
                    try {
                        listener.onSendKey((byte) 0x2A);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfirm(byte[] bytes, boolean b) {
                    try {
                        if (!b) {
                            listener.onInputResult(0, bytes);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(int i) {

                }
            });
        } catch (SDKException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * 查看是否正在输入
     *
     * @return 是否正在输入
     * @throws RemoteException
     */
    @Override
    public boolean isInputting() throws RemoteException {
        return false;
    }

    /**
     * 取消Pin输入，用户主动取消
     *
     * @return 是否取消成功
     * @throws RemoteException
     */
    @Override
    public boolean cancelInput() throws RemoteException {
        return false;
    }

    /**
     * 设置总超时时间
     *
     * @param timeout 超时时间
     * @throws RemoteException
     */
    @Override
    public void setTimeOut(int timeout) throws RemoteException {

    }

    @Override
    public void ppDispText(String text, int lineNo) throws RemoteException {

    }

    @Override
    public void ppScrClr(int lineNo) throws RemoteException {

    }

    @Override
    public void setSupportPinLen(int[] pinLen) throws RemoteException {

    }

    @Override
    public Bundle getKSN() throws RemoteException {
        return null;
    }

    @Override
    public boolean increaseKSN(int ksnType) throws RemoteException {
        return false;
    }

    @Override
    public String getDiversifiedEncryptData(int keyType, String factor, String orgData) throws RemoteException {
        return null;
    }

    @Override
    public String getDiversifiedDecryptData(int keyType, String factor, String orgData) throws RemoteException {
        return null;
    }

    @Override
    public byte[] tidSNEncrypt(byte[] randomFactor, int randomFactorLen) throws RemoteException {
        return new byte[0];
    }
}