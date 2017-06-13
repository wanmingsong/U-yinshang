package com.ums.upos.uapi.emv;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.socsi.smartposapi.emv2.*;
import com.socsi.smartposapi.emv2.EmvOnlineResult;
import com.socsi.utils.ByteUtil;
import com.socsi.utils.StringUtil;
import com.ums.upos.uapi.tlvUtil.Tlv;
import com.ums.upos.uapi.tlvUtil.TlvUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import socsi.middleware.emvl2lib.EmvAid;
import socsi.middleware.emvl2lib.EmvAidCandidate;
import socsi.middleware.emvl2lib.EmvApi;
import socsi.middleware.emvl2lib.EmvCallback;
import socsi.middleware.emvl2lib.EmvCallbackGetPinResult;
import socsi.middleware.emvl2lib.EmvErrorCode;
import socsi.middleware.emvl2lib.EmvRiskManageResult;
import socsi.middleware.emvl2lib.EmvRsaPubkey;
import socsi.middleware.emvl2lib.EmvStartProcessParam;
import socsi.middleware.emvl2lib.EmvTermConfig;

public class EmvHandlerStub extends EmvHandler.Stub {
    private Context mContext;
    private OnEmvProcessListener listener;
    private boolean isInit = false;
    /**
     * 单例对象
     */
    private static volatile EmvHandlerStub emvHandlerStub;

    private EmvHandlerStub(Context context) {
        this.mContext = context;
        int ret = EmvL2.getInstance(mContext, mContext.getPackageName()).init();
        if (ret == 0) {
            isInit = true;
        }
    }

    /**
     * 获取EmvHandlerStub单例
     *
     * @param context 上下文
     * @return EmvHandlerStub单例
     */
    public static EmvHandlerStub getInstance(Context context) {
        if (emvHandlerStub == null) {
            synchronized (EmvHandlerStub.class) {
                if (emvHandlerStub == null) {
                    emvHandlerStub = new EmvHandlerStub(context);
                }
            }
        }
        return emvHandlerStub;
    }

    /**
     * 开始EMV处理流程
     *
     * @param data EMV流程所需业务数据(见EMVTransDataConstrants类定义)
     * @param listener EMV流程处理监听器(详见OnEMVProcessListener类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int emvProcess(Bundle data, final OnEmvProcessListener listener) {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        if (data == null || data.isEmpty() || listener == null) {
            if (listener != null) {
                try {
                    listener.onFinish(-2, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return -2;
        }

        EmvStartProcessParam params = new EmvStartProcessParam();
        params.mTransAmt = Long.valueOf(data.getString(EmvTransDataConstrants.TRANSAMT));
        params.mProcType = data.getInt(EmvTransDataConstrants.PROCTYPE);
        params.mSeqNo = Integer.valueOf(data.getString(EmvTransDataConstrants.SEQNO));
        params.mCashbackAmt = Long.valueOf(data.getString(EmvTransDataConstrants.CASHBACKAMT));
        params.mTransDate = data.getByteArray(EmvTransDataConstrants.TRANSDATE);
        params.mTransTime = data.getByteArray(EmvTransDataConstrants.TRANSTIME);
        params.mChannelType = data.getInt(EmvTransDataConstrants.CHANNELTYPE);
        params.mIsQpbocForceOnline = data.getBoolean(EmvTransDataConstrants.ISQPBOCFORCEONLINE);
        params.mIsSupportEC = data.getBoolean(EmvTransDataConstrants.ISSUPPORTEC);
        params.mTag9CValue = data.getByte(EmvTransDataConstrants.B9C);
//        params.mTransType= data.getString(EmvTransDataConstrants.);//交易类型?
        EmvL2.getInstance(mContext, mContext.getPackageName()).startProcess(params, new SyncEmvCallback() {

            @Override
            public int panCofirm(byte[] bytes) {
                Log.v("emvProcess", "panCofirm");
                try {
                    listener.onConfirmCardNo(StringUtil.byte2HexStr(bytes));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public EmvCallbackGetPinResult getPin(int i, int i1) {
                Log.v("emvProcess", "getPin");
                boolean isOnlinePin = true;
                if (i == EmvCallback.EMV_ONLINEPIN || i == EmvCallback.EMV_EC_ONLINEPIN) {
                    isOnlinePin = true;
                } else if (i == EmvCallback.EMV_OFFLINEPIN || i == EmvCallback.EMV_OFFLINE_ONLY) {
                    isOnlinePin = false;
                } else {
                    Log.d("Debug", "EMVDevice SyncEmvCallback getPin type error:" + i);
                }
                try {
                    listener.onCardHolderInputPin(isOnlinePin, i1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public int aidSelect(EmvAidCandidate[] emvAidCandidates, int i) {
                Log.v("emvProcess", "aidSelect");
                List<String> appNameList = new ArrayList<>();
                for (EmvAidCandidate emvAidCandidate : emvAidCandidates) {
                    if (emvAidCandidate.mPreferredName != null) {
                        appNameList.add(new String(emvAidCandidate.mPreferredName));
                    }
                }
                boolean isFirstSelect = false;
                if (i == 0) {
                    isFirstSelect = true;
                }
                try {
                    listener.onSelApp(appNameList, isFirstSelect);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return 0;
            }

            @Override
            public EmvRiskManageResult termRiskManager(byte[] bytes, int i) {
                Log.v("emvProcess", "termRiskManager");
                return null;
            }

            @Override
            public int issuerReference(byte[] bytes) {
                Log.v("emvProcess", "issuerReference");
                return 0;
            }

            @Override
            public int accountTypeSelect() {
                Log.v("emvProcess", "accountTypeSelect");
                return 0;
            }

            @Override
            public int certConfirm(int i, byte[] bytes) {
                Log.v("emvProcess", "certConfirm");
                return 0;
            }

            @Override
            public int lcdMsg(byte[] bytes, byte[] bytes1, boolean b, int i) {
                Log.v("emvProcess", "lcdMsg");
                return 0;
            }

            @Override
            public int confirmEC() {
                Log.v("emvProcess", "confirmEC");
                return 0;
            }

            @Override
            public EmvOnlineResult onOnlineProc() {
                Log.v("emvProcess", "onOnlineProc");
                return null;
            }

            @Override
            public void processResult(int i) {
                Log.v("emvProcess", "processResult");
            }
        });

        return 0;
    }

    /**
     * 初始化终端参数
     *
     * @param cfg 终端参数(见EMVTermCfgConstrants类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int initTermConfig(Bundle cfg) throws RemoteException {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        if (cfg != null) {
            EmvTermConfig config = new EmvTermConfig();
            config.setCapability(cfg.getByteArray(EmvTermCfgConstrants.TERMCAP));
            config.setCountryCode(cfg.getByteArray(EmvTermCfgConstrants.COUNTRYCODE));
            config.setExtCapability(cfg.getByteArray(EmvTermCfgConstrants.ADDTERMCAP));
//            config.setMerchCateCode(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setMerchId(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setMerchName(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setReferCurrCode(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setReferCurrCon(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setReferCurrExp(cfg.getInt(EmvTermCfgConstrants.));
            config.setTermId(cfg.getByteArray(EmvTermCfgConstrants.TERMID));
            config.setTermType(cfg.getByte(EmvTermCfgConstrants.TERMTYPE));//预期的值是int类型
//            config.setTransCurrCode(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setTransCurrExp(cfg.getInt(EmvTermCfgConstrants.));
            EmvL2.getInstance(mContext, mContext.getPackageName()).setTermConfig(config);
            return 0;
        }
        return -2;
    }

    /**
     * 加载EMV参数列表
     *
     * @param aidParaList AID列表
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int setAidParaList(List<EmvAidPara> aidParaList) throws RemoteException {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        if (aidParaList == null || aidParaList.size() == 0) {
            return -2;
        }
        EmvAid[] emvAids = new EmvAid[aidParaList.size()];
        for (int i = 0; i < aidParaList.size(); i++) {
            EmvAidPara aidPara = aidParaList.get(i);
            if (StringUtil.byteToInt(aidPara.getAID()) == 0 || StringUtil.byteToInt(aidPara.getTermAppVer()) == 0) {
                return -8011;
            }
            emvAids[i] = new EmvAid(aidPara.getAID(), aidPara.getAppSelIndicator(), aidPara.getTermAppVer());
            emvAids[i].setTac(aidPara.getTAC_Default(), aidPara.getTAC_Online(), aidPara.getTAC_Denial());
            emvAids[i].setDDOL(aidPara.getDDOL());
            emvAids[i].setLimit(aidPara.getTFL_Domestic(), aidPara.getEC_TFL(), aidPara.getRFTransLimit(),
                    aidPara.getRFOfflineLimit(), aidPara.getRFCVMLimit());
            emvAids[i].setRandomTransactionParam(aidPara.getThresholdValueInt(), aidPara.getMaxTargetPercentageInt(),
                    aidPara.getTargetPercentageInt());
        }
        //TODO 未完成
        int result = EmvL2.getInstance(mContext, mContext.getPackageName()).addAids(emvAids);
        if (result != 0) {
            return -1;
        }
        return 0;
    }

    /**
     * 输出String类型的EMV参数
     *
     * @param aidPara EMV参数
     * @return 字符串
     */
    private String emvAidParaToString(EmvAidPara aidPara) {
        if (aidPara == null || aidPara.getAID_Length() == 0 || aidPara.getAID() == null
                || aidPara.getTermAppVer() == null) {
            return null;
        }
        String string = "";
        //TODO 未完成
        //aid
        string += "9f06";
        string += String.format(Locale.getDefault(), "%02x", aidPara.getAID_Length());
        byte[] correctAid = ByteUtil.cut(aidPara.getAID(), 0, aidPara.getAID_Length());
        string += StringUtil.byte2HexStr(correctAid);
//        //应用选择指示符
//        string += "DF0101" + StringUtil.byte2HexStr(aidPara.getAppSelIndicator());
//        //终端优先级
//
//        //偏置随机选择的最大目标百分数
//        string += "DF1601" + StringUtil.byte2HexStr(aidPara.getMaxTargetPercentageInt());
//        //随机选择的目标百分数
//        string += "DF1701" + StringUtil.byte2HexStr(aidPara.getTargetPercentageDomestic());
//        //终端最低限额
//        string += "9F1B04" + StringUtil.byte2HexStr(aidPara.getTFL_Domestic());
//        //偏置随机选择的阈值
//        string += "DF1504" + StringUtil.byte2HexStr(aidPara.getThresholdValueDomestic());

        Log.e("WY", "aidPara:" + string);
        return string;
    }

    /**
     * 加载EMV公钥列表
     *
     * @param capkList CAPK列表
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int setCAPKList(List<EmvCapk> capkList) throws RemoteException {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        if (capkList == null || capkList.size() == 0) {
            return -2;
        }
        String[] emvCapks = new String[capkList.size()];
//        socsi.middleware.emvl2lib.EmvCapk[] emvCapks = new socsi.middleware.emvl2lib.EmvCapk[capkList.size()];
        for (int i = 0; i < capkList.size(); i++) {
            EmvCapk emvCapk = capkList.get(i);
            if (StringUtil.byteToInt(emvCapk.getCAPKModulus()) == 0 || StringUtil.byteToInt(emvCapk.getRID()) == 0
                    || StringUtil.byteToInt(emvCapk.getCAPKExponent()) == 0) {
                return -8011;
            }
            String str = "RID"+StringUtil.bytesToHexString(emvCapk.getRID())+"  ;"
                    +"CA_PKIndex"+StringUtil.byte2HexStr(emvCapk.getCA_PKIndex())+"  ;"
                    +"CA_HashAlgoIndicator"+StringUtil.byte2HexStr(emvCapk.getCA_HashAlgoIndicator())+"  ;"
                    +"CA_PKAlgoIndicator"+StringUtil.byte2HexStr(emvCapk.getCA_PKAlgoIndicator())+"  ;"
                    +"LengthOfCAPKModulus"+emvCapk.getLengthOfCAPKModulus()+"  ;"
                    +"CAPKModulus"+StringUtil.bytesToHexString(emvCapk.getCAPKModulus())+"  ;"
                    +"LengthOfCAPKExponent"+emvCapk.getLengthOfCAPKExponent()+"  ;"
                    +"CAPKExponent"+StringUtil.bytesToHexString(emvCapk.getCAPKExponent())+"  ;"
                    +"ChecksumHash"+StringUtil.bytesToHexString(emvCapk.getChecksumHash())+"  ;"
                    +"CAPKExpDate"+StringUtil.bytesToHexString(emvCapk.getCAPKExpDate());
            Log.v("Capk", str);

//            byte[] capkExponent ;//公钥指数
//            if (StringUtil.bytesToHexString(emvCapk.getCAPKExponent()).equals("030000")){
//                capkExponent = new byte[]{StringUtil.hexStr2Bytes("03")[0]};
//            }else {
//                capkExponent = emvCapk.getCAPKExponent();
//            }
//            EmvRsaPubkey rsaPubKey = new EmvRsaPubkey(emvCapk.getCAPKModulus(), capkExponent);
//            emvCapks[i] = new socsi.middleware.emvl2lib.EmvCapk(emvCapk.getCAPKExpDate(), emvCapk.getRID(),
//                    emvCapk.getCA_PKIndex(), rsaPubKey, emvCapk.getChecksumHash());
            emvCapks[i] = emvCapkToString(emvCapk);
        }
        int result = EmvL2.getInstance(mContext, mContext.getPackageName()).addCapks(emvCapks);
        if (result != 0) {
            return -1;
        }
        return 0;
    }

    /**
     * 输出String类型的EMV公钥
     *
     * @param capk EMV公钥
     * @return 字符串
     */
    private String emvCapkToString(EmvCapk capk) {
        if (capk == null) {
            return null;
        }
        String string = "";
        string += "9F06";
        string += String.format(Locale.getDefault(), "%02x", capk.getRID().length);
        string += StringUtil.byte2HexStr(capk.getRID());

        string += "9F22";
        string += "01";
        string += StringUtil.byte2HexStr(capk.getCA_PKIndex());
        string += "DF05";
        string += String.format(Locale.getDefault(), "%02x", capk.getCAPKExpDate().length);
        string += StringUtil.byte2HexStr(capk.getCAPKExpDate());
        string += "DF06";
        string += "01";
        string += StringUtil.byte2HexStr(capk.getCA_HashAlgoIndicator());
        string += "DF07";
        string += "01";
        string += StringUtil.byte2HexStr(capk.getCA_PKAlgoIndicator());
        string += "DF02";
        string += String.format(Locale.getDefault(), "%02x", capk.getCAPKModulus().length);
        byte[] moduls = ByteUtil.cut(capk.getCAPKModulus(), 0 ,capk.getLengthOfCAPKModulus());
        string += StringUtil.byte2HexStr(moduls);
//        if (StringUtil.byte2HexStr(capk.getCAPKExponent()).equals("030000")) {
//            string += "DF04";
//            string += "01";
//            string += "03";
//        }else {
            string += "DF04";
            string += String.format(Locale.getDefault(), "%02x", capk.getCAPKExponent().length);
            byte[] exponent = ByteUtil.cut(capk.getCAPKExponent(), 0 ,capk.getLengthOfCAPKExponent());
            string += StringUtil.byte2HexStr(exponent);
//        }
        string += "DF03";
        string += String.format(Locale.getDefault(), "%02x", capk.getChecksumHash().length);
        string += StringUtil.byte2HexStr(capk.getChecksumHash());
        Log.v("TLV", string);
        return string;
    }

    /**
     * 读取EMV公钥列表
     *
     * @return EMV公钥列表
     * @throws RemoteException
     */
    @Override
    public List<EmvCapk> getCapkList() throws RemoteException {
        if (!isInit) {
            return null;
        }
        List<byte[]> emvCapks = EmvL2.getInstance(mContext, mContext.getPackageName()).getCapks();
        if (emvCapks != null) {
            List<EmvCapk> emvCapkList = new ArrayList<>();
            for (int i = 0; i < emvCapks.size(); i++) {
                byte[] emvCapk = emvCapks.get(i);
                EmvCapk mEmvCapk = new EmvCapk();
//                Log.v("Tlv", StringUtil.bytesToHexString(emvCapk) + "\r\n");
                Map<String, Tlv> tlvMap = TlvUtils.builderTlvMap(StringUtil.bytesToHexString(emvCapk));
                assert tlvMap != null;
                Tlv tlv = tlvMap.get("9F06");
                if (tlv != null) {
                    mEmvCapk.setRID(StringUtil.hexStr2Bytes(tlv.getValue()));//应用标识AID/RID
                }
                Tlv tlv2 = tlvMap.get("9F22");
                if (tlv2 != null) {
                    mEmvCapk.setCA_PKIndex(StringUtil.hexStr2Bytes(tlv2.getValue())[0]);//认证中心公钥索引
                }
                Tlv tlv3 = tlvMap.get("DF06");
                if (tlv3 != null) {
                    mEmvCapk.setCA_HashAlgoIndicator(StringUtil.hexStr2Bytes(tlv3.getValue())[0]);//认证中心公钥哈什算法标识
                }
                Tlv tlv4 = tlvMap.get("DF07");
                if (tlv4 != null) {
                    mEmvCapk.setCA_PKAlgoIndicator(StringUtil.hexStr2Bytes(tlv4.getValue())[0]);//认证中心公钥算法标识
                }
                Tlv tlv5 = tlvMap.get("DF02");
                if (tlv5 != null) {
                    mEmvCapk.setCAPKModulus(StringUtil.hexStr2Bytes(tlv5.getValue()));//认证中心公钥模
                }
                Tlv tlv6 = tlvMap.get("DF04");
                if (tlv6 != null) {
                    mEmvCapk.setCAPKExponent(StringUtil.hexStr2Bytes(tlv6.getValue()));//认证中心公钥指数
                }
                Tlv tlv7 = tlvMap.get("DF03");
                if (tlv7 != null) {
                    mEmvCapk.setChecksumHash(StringUtil.hexStr2Bytes(tlv7.getValue()));//认证中心公钥校验值
                }
                Tlv tlv8 = tlvMap.get("DF05");
                if (tlv8 != null) {
                    mEmvCapk.setCAPKExpDate(StringUtil.hexStr2Bytes(tlv8.getValue()));//认证中心公钥有效期
                }
                emvCapkList.add(mEmvCapk);
            }
            return emvCapkList;
        }
        return null;
    }

    /**
     * 读取EMV AID参数列表
     *
     * @return AID参数列表
     * @throws RemoteException
     */
    @Override
    public List<EmvAidPara> getAidParaList() throws RemoteException {
        if (!isInit) {
            return null;
        }
        List<byte[]> emvAids = EmvL2.getInstance(mContext, mContext.getPackageName()).getAids();
        if (emvAids != null) {
            List<EmvAidPara> emvAidParaList = new ArrayList<>();
            for (int i = 0; i < emvAids.size(); i++) {
                byte[] emvAid = emvAids.get(i);
                EmvAidPara emvAidPara = new EmvAidPara();
                Map<String, Tlv> tlvMap = TlvUtils.builderTlvMap(StringUtil.bytesToHexString(emvAid));
                assert tlvMap != null;
                Tlv tlv = tlvMap.get("9F06");
                if (tlv != null) {
                    emvAidPara.setAID(StringUtil.hexStr2Bytes(tlv.getValue()));//应用标识AID/RID
                }
                Tlv tlv2 = tlvMap.get("DF01");
                if (tlv2 != null) {
                    emvAidPara.setAppSelIndicator(StringUtil.hexStr2Bytes(tlv2.getValue())[0]);//应用选择指示符（ASI）
                }
                Tlv tlv3 = tlvMap.get("9F08");
                if (tlv3 != null) {
                    emvAidPara.setTermAppVer(StringUtil.hexStr2Bytes(tlv3.getValue()));//应用版本号(卡片)
                }
                Tlv tlv4 = tlvMap.get("DF11");
                if (tlv4 != null) {
                    emvAidPara.setTAC_Default(StringUtil.hexStr2Bytes(tlv4.getValue()));//TAC－缺省
                }
                Tlv tlv5 = tlvMap.get("DF12");
                if (tlv5 != null) {
                    emvAidPara.setTAC_Online(StringUtil.hexStr2Bytes(tlv5.getValue()));//TAC－联机
                }
                Tlv tlv6 = tlvMap.get("DF13");
                if (tlv6 != null) {
                    emvAidPara.setTAC_Denial(StringUtil.hexStr2Bytes(tlv6.getValue()));//TAC－拒绝
                }
                Tlv tlv7 = tlvMap.get("9F1B");
                if (tlv7 != null) {
                    emvAidPara.setTFL_International(StringUtil.hexStr2Bytes(tlv7.getValue()));//终端最低限额
                }
                Tlv tlv8 = tlvMap.get("DF15");
                if (tlv8 != null) {
                    emvAidPara.setThresholdValueInt(StringUtil.hexStr2Bytes(tlv8.getValue()));//偏置随机选择的阈值
                }
                Tlv tlv9 = tlvMap.get("DF16");
                if (tlv9 != null) {
                    emvAidPara.setMaxTargetPercentageInt(StringUtil.hexStr2Bytes(tlv9.getValue())[0]);//偏置随机选择的最大目标百分数
                }
                Tlv tlv10 = tlvMap.get("DF17");
                if (tlv10 != null) {
                    emvAidPara.setTargetPercentageInt(StringUtil.hexStr2Bytes(tlv10.getValue())[0]);//随机选择的目标百分数
                }
                Tlv tlv11 = tlvMap.get("DF14");
                if (tlv11 != null) {
                    emvAidPara.setDDOL(StringUtil.hexStr2Bytes(tlv11.getValue()));//缺省DDOL(?)
                }
                Tlv tlv12 = tlvMap.get("DF18");
                if (tlv12 != null) {
                    emvAidPara.setTermCap(StringUtil.hexStr2Bytes(tlv12.getValue()));//	终端联机PIN支持能力(?)
                }
                Tlv tlv13 = tlvMap.get("9F7B");
                if (tlv13 != null) {
                    emvAidPara.setEC_TFL(StringUtil.hexStr2Bytes(tlv13.getValue()));//终端电子现金交易限额
                }
                Tlv tlv14 = tlvMap.get("DF19");
                if (tlv14 != null) {
                    emvAidPara.setRFOfflineLimit(StringUtil.hexStr2Bytes(tlv14.getValue()));//非接触读写器脱机最低限额
                }
                Tlv tlv15 = tlvMap.get("DF20");
                if (tlv15 != null) {
                    emvAidPara.setRFTransLimit(StringUtil.hexStr2Bytes(tlv15.getValue()));//非接触读写器交易限额
                }
                Tlv tlv16 = tlvMap.get("DF21");
                if (tlv16 != null) {
                    emvAidPara.setRFCVMLimit(StringUtil.hexStr2Bytes(tlv16.getValue()));//读写器持卡人验证方法（CVM）所需限制
                }
                emvAidParaList.add(emvAidPara);
            }
            return emvAidParaList;
        }
        return null;
    }

    /**
     * 根据PathId读取Tag值
     *
     * @param tag    待读取的TAG
     * @param pathId 数据来源(见EMVDataSource类描述)
     * @return Tag值
     * @throws RemoteException
     */
    @Override
    public byte[] getTlvs(byte[] tag, int pathId) throws RemoteException {
        if (!isInit) {
            return null;
        }
        return EmvL2.getInstance(mContext, mContext.getPackageName()).getTag(tag, pathId);
    }

    /**
     * 设置Tag值至EMV内核
     *
     * @param tag   待设置的TAG
     * @param value 数值
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int setTlv(byte[] tag, byte[] value) throws RemoteException {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        if (tag == null || value == null) {
            return -2;
        }
        int result = EmvL2.getInstance(mContext, mContext.getPackageName()).setTLV(StringUtil.byteArrayToInt(tag), value);
        if (result != 0) {
            return -1;
        }
        return 0;
    }

    /**
     * 获取卡片日志
     *
     * @param channelType 卡片交易日志来源(见EmvChannelType类描述)
     * @param listener    EMV流程处理监听器(详见OnEMVProcessListener类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int getEmvCardLog(int channelType, final OnEmvProcessListener listener) throws RemoteException {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        //参数错误
        if (listener == null) {
            return -2;
        }
        if (channelType < 0 || channelType > 2) {
            listener.onFinish(-2, null);
            return -2;
        }

        EmvL2.getInstance(mContext, mContext.getPackageName()).startGetIccLogProcess(channelType, EmvStartProcessParam.EMV_API_TRANS_TYPE_ICC_LOG, new SyncEmvCallback() {
            @Override
            public int panCofirm(byte[] bytes) {
                return 0;
            }

            @Override
            public EmvCallbackGetPinResult getPin(int i, int i1) {
                return null;
            }

            @Override
            public int aidSelect(EmvAidCandidate[] emvAidCandidates, int i) {
                return 0;
            }

            @Override
            public EmvRiskManageResult termRiskManager(byte[] bytes, int i) {
                return null;
            }

            @Override
            public int issuerReference(byte[] bytes) {
                return 0;
            }

            @Override
            public int accountTypeSelect() {
                return 0;
            }

            @Override
            public int certConfirm(int i, byte[] bytes) {
                return 0;
            }

            @Override
            public int lcdMsg(byte[] bytes, byte[] bytes1, boolean b, int i) {
                return 0;
            }

            @Override
            public int confirmEC() {
                return 0;
            }

            @Override
            public EmvOnlineResult onOnlineProc() {
                return null;
            }

            @Override
            public void processResult(int i) {
                if (i == 9) {
                    int iccLogNumber = EmvL2.getInstance(mContext, mContext.getPackageName()).getIccLogNumber(EmvApi.EMV_TRANS_PBOCLOG);
                    for (int j = 0; j < iccLogNumber; j++) {
                        byte[] iccLog = EmvL2.getInstance(mContext, mContext.getPackageName()).getIccLogWithIndex(EmvApi.EMV_TRANS_PBOCLOG, j);
                        if (iccLog != null) {
                            //TODO 需确认返回的日志数据格式

                        }
                    }
//                    listener.onFinish(0, );
                }
            }
        });
        return 0;
    }

    /**
     * 清除EMV内核交易日志
     *
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int clearLog() throws RemoteException {
        return 0;
    }

    /**
     * 第一电子货币余额
     */
    private long firstCurrencyBalance;
    /**
     * 第二电子货币余额
     */
    private long secondCurrencyBalance;

    /**
     * 获取电子现金余额
     *
     * @param listener    执行结果监听
     * @param channelType 应用来源(见EmvChannelType类描述)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int emvGetEcBalance(OnEmvProcessListener listener, int channelType) throws RemoteException {
        if (!isInit) {
            return EmvL2.ERROR_UNINITIALIZED;
        }
        if (listener == null) {
            return -2;
        }
        EmvL2.getInstance(mContext, mContext.getPackageName()).startQueryECBalanceProcess(channelType, new SyncEmvCallback() {

            @Override
            public int panCofirm(byte[] bytes) {
                return 0;
            }

            @Override
            public EmvCallbackGetPinResult getPin(int i, int i1) {
                return null;
            }

            @Override
            public int aidSelect(EmvAidCandidate[] emvAidCandidates, int i) {
                return 0;
            }

            @Override
            public EmvRiskManageResult termRiskManager(byte[] bytes, int i) {
                return null;
            }

            @Override
            public int issuerReference(byte[] bytes) {
                return 0;
            }

            @Override
            public int accountTypeSelect() {
                return 0;
            }

            @Override
            public int certConfirm(int i, byte[] bytes) {
                return 0;
            }

            @Override
            public int lcdMsg(byte[] bytes, byte[] bytes1, boolean b, int i) {
                return 0;
            }

            @Override
            public int confirmEC() {
                return 0;
            }

            @Override
            public EmvOnlineResult onOnlineProc() {
                return null;
            }

            @Override
            public void processResult(int i) {
                switch (i) {
                    case EmvApi.EMV_TRANS_EC_GOON_AMOUNT:
                        firstCurrencyBalance = EmvL2.getInstance(mContext, mContext.getPackageName()).getECBalance(false);
                        secondCurrencyBalance = EmvL2.getInstance(mContext, mContext.getPackageName()).getECBalance(true);
                    case EmvApi.EMV_TRANS_RF_GOON_AMOUNT:
                        firstCurrencyBalance = EmvL2.getInstance(mContext, mContext.getPackageName()).getECBalance(false);
                        secondCurrencyBalance = EmvL2.getInstance(mContext, mContext.getPackageName()).getECBalance(true);
                        break;
                    case EmvApi.EMV_TRANS_FALLBACK:
                        break;
                    case EmvApi.EMV_TRANS_TERMINATE:
                        break;
                    case EmvApi.EMV_TRANS_ACCEPT:
                        break;
                    case EmvApi.EMV_TRANS_DENIAL:
                        break;
                    case EmvApi.EMV_TRANS_GOONLINE:
                        break;
                    case EmvApi.EMV_TRANS_QPBOC_ACCEPT:
                        break;
                    case EmvApi.EMV_TRANS_QPBOC_DENIAL:
                        break;
                    case EmvApi.EMV_TRANS_QPBOC_GOONLINE:
                        break;
                    case EmvErrorCode.EMV_ERR_BASE:
                        break;
                    default:
                        break;
                }
            }
        });
        if (firstCurrencyBalance < 0 || secondCurrencyBalance < 0) {
            return -1;
        }
        return 0;
    }

    /**
     * 应用选择结果响应
     *
     * @param selResult 应用索引(< 0 取消选择)
     * @throws RemoteException
     */
    @Override
    public void onSetSelAppResponse(int selResult) throws RemoteException {
        if (selResult < 0) {
            listener.onFinish(-8020, null);
        }
    }

    /**
     * 确认卡号结果响应
     *
     * @param isConfirm 是否确认卡号
     * @throws RemoteException
     */
    @Override
    public void onSetConfirmCardNoResponse(boolean isConfirm) throws RemoteException {
        Log.v("V", "ConfirmCardNoResponse");
        if (!isConfirm) {
            listener.onFinish(-8020, null);
        }
    }

    /**
     * 证件确认结果响应
     *
     * @param isVerify 是否确认证件号
     * @throws RemoteException
     */
    @Override
    public void onSetCertVerifyResponse(boolean isVerify) throws RemoteException {

    }

    /**
     * 联机回调结果响应
     *
     * @param retCode 联机函数执行结果
     * @param data    联机结果数据(见EmvOnLineResult类定义)
     * @throws RemoteException
     */
    @Override
    public void onSetOnlineProcResponse(int retCode, Bundle data) throws RemoteException {

    }

    /**
     * 设置AID参数结果响应
     *
     * @param aid AID参数实例
     * @throws RemoteException
     */
    @Override
    public void onSetAIDParameterResponse(EmvAidPara aid) throws RemoteException {

    }

    /**
     * 设置CA公钥结果响应
     *
     * @param capk CAPK公钥实例
     * @throws RemoteException
     */
    @Override
    public void onSetCAPubkeyResponse(EmvCapk capk) throws RemoteException {

    }

    /**
     * 终端风险管理响应
     *
     * @param result 格式为长度13的字符串 第一位（0-非黑名单 1-黑名单）后12位（右靠左补0 12位金额）当黑名单时填12个0
     * @throws RemoteException
     */
    @Override
    public void onSetTRiskManageResponse(String result) throws RemoteException {

    }

    /**
     * 非接流程错误代码获取
     *
     * @return 非接流程错误码
     * @throws RemoteException
     */
    @Override
    public String getRFErrorCode() throws RemoteException {
        return null;
    }
}



