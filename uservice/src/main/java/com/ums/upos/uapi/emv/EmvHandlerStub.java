package com.ums.upos.uapi.emv;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.socsi.aidl.pinpadservice.OperationPinListener;
import com.socsi.exception.SDKException;
import com.socsi.smartposapi.emv.emvl2.EMVL2;
import com.socsi.smartposapi.emv.emvl2.ICCardLog;
import com.socsi.smartposapi.emv.emvl2.TwiceAuthorResult;
import com.socsi.smartposapi.emv2.*;
import com.socsi.smartposapi.emv2.EmvOnlineResult;
import com.socsi.smartposapi.ped.PedVeriAuth;
import com.socsi.utils.ByteUtil;
import com.socsi.utils.HexUtil;
import com.socsi.utils.StringUtil;
import com.socsi.utils.TlvUtil;
import com.ums.upos.uapi.tlvUtil.Tlv;
import com.ums.upos.uapi.tlvUtil.TlvUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import socsi.middleware.emvl2lib.EmvAid;
import socsi.middleware.emvl2lib.EmvAidCandidate;
import socsi.middleware.emvl2lib.EmvApi;
import socsi.middleware.emvl2lib.EmvCallback;
import socsi.middleware.emvl2lib.EmvCallbackGetPinResult;
import socsi.middleware.emvl2lib.EmvErrorCode;
import socsi.middleware.emvl2lib.EmvPubkey;
import socsi.middleware.emvl2lib.EmvPubkeyType;
import socsi.middleware.emvl2lib.EmvRiskManageResult;
import socsi.middleware.emvl2lib.EmvRsaPubkey;
import socsi.middleware.emvl2lib.EmvSm2Pubkey;
import socsi.middleware.emvl2lib.EmvStartProcessParam;
import socsi.middleware.emvl2lib.EmvTermConfig;

public class EmvHandlerStub extends EmvHandler.Stub {
    private Context mContext;
    private OnEmvProcessListener emvListener;
    private OnEmvProcessListener logListener;
    private boolean isInit = false;
    private EmvL2.PanConfirmHandler confirmHandler;
    /**
     * 单例对象
     */
    private static volatile EmvHandlerStub emvHandlerStub;
    private EmvL2.AidSelectHandler selectHandler;
    private EmvL2.GetPinHandler pinHandler;
    private EmvL2.TermRiskManageHandler riskManagerHandler;
    private EmvL2.CertConfirmHandler verifyHandler;

    public static final String TAG = "EmvHandlerStub";

    private int timeout = 20 * 1000;
    private String cardNum;
    private byte[] pinEncrypt;

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
     * @param data     EMV流程所需业务数据(见EMVTransDataConstrants类定义)
     * @param listener EMV流程处理监听器(详见OnEMVProcessListener类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int emvProcess(final Bundle data, final OnEmvProcessListener listener) {
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

        emvListener = listener;

        EmvStartProcessParam params = new EmvStartProcessParam();
        params.mTransAmt = Long.parseLong(data.getString(EmvTransDataConstrants.TRANSAMT));
        params.mProcType = data.getInt(EmvTransDataConstrants.PROCTYPE);
        params.mSeqNo = Integer.parseInt(data.getString(EmvTransDataConstrants.SEQNO).replaceAll(" ", ""));
        params.mCashbackAmt = Long.parseLong(data.getString(EmvTransDataConstrants.CASHBACKAMT));
        params.mTransDate = StringUtil.hexStr2Bytes(data.getString(EmvTransDataConstrants.TRANSDATE));
        params.mTransTime = StringUtil.hexStr2Bytes(data.getString(EmvTransDataConstrants.TRANSTIME));
        params.mChannelType = data.getInt(EmvTransDataConstrants.CHANNELTYPE);
        params.mIsQpbocForceOnline = data.getBoolean(EmvTransDataConstrants.ISQPBOCFORCEONLINE);
        params.mIsSupportEC = data.getBoolean(EmvTransDataConstrants.ISSUPPORTEC);
        params.mTag9CValue = data.getByte(EmvTransDataConstrants.B9C);
        //交易类型，EMV_API_TRANS_TYPE_NORMAL：正常交易  EMV_API_TRANS_TYPE_GET_EC：获取EC交易  EMV_API_TRANS_TYPE_ICC_LOG：获取日志交易
        params.mTransType = EmvStartProcessParam.EMV_API_TRANS_TYPE_NORMAL;

        EmvL2.getInstance(mContext, mContext.getPackageName()).startProcess(params, new AsyncEmvCallback() {
            @Override
            public void panCofirm(byte[] bytes, EmvL2.PanConfirmHandler panConfirmHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess panConfirm");
                confirmHandler = panConfirmHandler;
                try {
                    String panNum = StringUtil.byte2HexStr(bytes).replace("F", "");
                    cardNum = panNum;
                    listener.onConfirmCardNo(panNum);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getPin(int i, int i1, EmvL2.GetPinHandler getPinHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess getPin");
                pinHandler = getPinHandler;
                Log.d("pinHandler", "EMVDevice SyncEmvCallback getPin type error:" + pinHandler);
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

                    PedVeriAuth.getInstance().open(mContext, 0);
                    //调用密码键盘
                    Bundle bundle = new Bundle();
                    bundle.putString("pan", cardNum);//card number
                    bundle.putInt("keyID", 1);//索引
//                    bundle.putInt("pinAlgMode", pinAlgMode);//pin加密类型
                    bundle.putString("promptString", "请输入联机密码");//tip info
                    PedVeriAuth.getInstance().listenForPinBlock(bundle, 60 * 1000,
                            true, true, new com.socsi.aidl.pinpadservice.OperationPinListener() {

                                @Override
                                public void onInput(int len, int key) {
                                    Log.e(TAG, "onInput  len:" + len + "  key:" + key);

                                }

                                @Override
                                public void onError(int errorCode) {
                                    Log.e(TAG, "onError   errorCode:" + errorCode);
                                    com.socsi.utils.Log.d("错误码：" + errorCode);
                                }

                                @Override
                                public void onConfirm(byte[] data, boolean isNonePin) {
                                    Log.e(TAG, "onConfirm   data:" + HexUtil.toString(data) + "  isNonePin:" + isNonePin);
                                    com.socsi.utils.Log.d("密码：" + HexUtil.toString(data));
                                    pinEncrypt = data;
                                    if (isNonePin) {
                                        pinHandler.onGetPin(EmvCallbackGetPinResult.CV_PIN_SUCC, new byte[]{0x00, 0x00});
                                    } else {
                                        pinHandler.onGetPin(EmvCallbackGetPinResult.CV_PIN_SUCC, data);
                                    }
                                }

                                @Override
                                public void onCancel() {
                                    Log.e(TAG, "onCancel");
                                    com.socsi.utils.Log.d("用户取消");
                                    try {
                                        listener.onFinish(-8020, null);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                } catch (RemoteException | SDKException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void aidSelect(EmvAidCandidate[] emvAidCandidates, int i, EmvL2.AidSelectHandler aidSelectHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess aidSelect");
                selectHandler = aidSelectHandler;
                //i 已经选择了多少次，当超过0时，要提示重新选择
                List<String> appNameList = new ArrayList<>();
                for (EmvAidCandidate emvAidCandidate : emvAidCandidates) {
                    if (emvAidCandidate.mPreferredName != null) {
                        appNameList.add(StringUtil.byte2HexStr(emvAidCandidate.mPreferredName));
                    } else {
                        com.socsi.utils.Log.d("EMVDevice SyncEmvCallback aidSelect mPreferredName error");
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
            }

            @Override
            public void termRiskManager(byte[] bytes, int i, EmvL2.TermRiskManageHandler termRiskManageHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess termRiskManager");
                riskManagerHandler = termRiskManageHandler;
                //bytes 主账号  i 主账号序列号
                try {
                    listener.onTRiskManage(StringUtil.byte2HexStr(bytes), String.valueOf(i));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void issuerReference(byte[] bytes, EmvL2.IssuerReferenceHandler issuerReferenceHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess issuerReference");
                issuerReferenceHandler.onIssuerReference(0);
            }

            @Override
            public void accountTypeSelect(EmvL2.AccountTypeSelectHandler accountTypeSelectHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess accountTypeSelect");
                accountTypeSelectHandler.onAccountType(EmvCallback.EMV_ACCOUNT_TYPE_DEFAULT);
            }

            @Override
            public void certConfirm(int i, byte[] bytes, EmvL2.CertConfirmHandler certConfirmHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess certConfirm");
                verifyHandler = certConfirmHandler;
                String certType = "其他";
                switch (i) {
                    case EmvCallback.EMV_CERT_TYPE_ID_CARD:
                        certType = "身份证";
                        break;
                    case EmvCallback.EMV_CERT_TYPE_OFFICERS:
                        certType = "军官证";
                        break;
                    case EmvCallback.EMV_CERT_TYPE_PASSPORT:
                        certType = "护照";
                        break;
                    case EmvCallback.EMV_CERT_TYPE_ARRIVAL_CARD:
                        certType = "入境证";
                        break;
                    case EmvCallback.EMV_CERT_TYPE_INTERIM_ID:
                        certType = "临时身份证";
                        break;
                    case EmvCallback.EMV_CERT_TYPE_OTHERS:
                        certType = "其他";
                        break;
                }
                String certValue = "";
                if (bytes != null) {
                    certValue = StringUtil.byte2HexStr(bytes);
                }
                try {
                    listener.onCertVerify(certType, certValue);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void lcdMsg(byte[] bytes, byte[] bytes1, boolean b, int i, EmvL2.LcdMsgHandler lcdMsgHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess lcdMsg");
                lcdMsgHandler.onLcdMsg(1);
            }

            @Override
            public void confirmEC(EmvL2.ConfirmEcHandler confirmEcHandler) {
                com.socsi.utils.Log.d("EMVDevice process startProcess confirmEC");
                confirmEcHandler.onConfirmEc(0);
            }

            @Override
            public void processResult(int i) {
                com.socsi.utils.Log.d("EMVDevice process startProcess processResult");
                String desc = "";
                switch (i) {
                    case EmvApi.EMV_TRANS_FALLBACK:
                        desc = "Fallback";
                        break;
                    case EmvApi.EMV_TRANS_TERMINATE:
                        desc = "交易中止";
                        break;
                    case EmvApi.EMV_TRANS_ACCEPT:
                        desc = "交易授受";
                        break;
                    case EmvApi.EMV_TRANS_DENIAL:
                        desc = "交易拒绝";
                        break;
                    case EmvApi.EMV_TRANS_GOONLINE:
                        desc = "联机";
                        break;
                    case EmvApi.EMV_TRANS_QPBOC_ACCEPT:
                        desc = "非接触QPBOC交易接受";
                        break;
                    case EmvApi.EMV_TRANS_QPBOC_DENIAL:
                        desc = "非接触QPBOC交易拒绝";
                        break;
                    case EmvApi.EMV_TRANS_QPBOC_GOONLINE:
                        desc = "非接触QPBOC交易联机";
                        break;
                    case EmvErrorCode.EMV_ERR_BASE:
                        if (data.getInt(EmvTransDataConstrants.PROCTYPE) == EmvTransFlow.SIMPLE) {
                            desc = "简易流程成功";
                        }
                        break;
                    default:
                        break;
                }

                //打印返回值
                com.socsi.utils.Log.d(desc);
                /**
                 * IC卡交易时，获得交易所需数据,卡序列号
                 */
                Map<String, String> map = new HashMap<String, String>();
                byte[] cardData = Utility.getTlvData(mContext, mContext.getPackageName(), "5F34");
                Map<String, String> cardDataMap = TlvUtil.tlvToMap(cardData);
                String sn = cardDataMap.get("5F34");//23卡序列号
                com.socsi.utils.Log.d("sn---------" + sn);

                Bundle bundle = new Bundle();
                bundle.putByteArray("Pin", pinEncrypt);
                bundle.putByteArray("cardSn", StringUtil.hexStr2Bytes(sn));
                try {
                    listener.onOnlineProc(bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
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
        if (cfg == null) {
            return -2;
        }
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
        config.setTermType(cfg.getByte(EmvTermCfgConstrants.TERMTYPE));
//            config.setTransCurrCode(cfg.getByteArray(EmvTermCfgConstrants.));
//            config.setTransCurrExp(cfg.getInt(EmvTermCfgConstrants.));
        EmvL2.getInstance(mContext, mContext.getPackageName()).setTermConfig(config);
        return 0;
    }

    /**
     * 加载EMV Aid参数列表
     *
     * @param aidParaList AID列表
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int setAidParaList(List<EmvAidPara> aidParaList) throws RemoteException {
        if (aidParaList == null || aidParaList.size() == 0) {
            return -2;
        }
//        EmvAid[] emvAids = new EmvAid[aidParaList.size()];
        String[] strEmvAids = new String[aidParaList.size()];
        for (int i = 0; i < aidParaList.size(); i++) {
            EmvAidPara aidPara = aidParaList.get(i);
//            Log.d("----------", StringUtil.bytesToHexString(aidPara.getAID())+"     "+StringUtil.bytesToHexString(aidPara.getTermAppVer())
//                    +"      "+StringUtil.bytesToHexString(aidPara.getDDOL()));
            if (StringUtil.byteToInt(aidPara.getAID()) == 0 || StringUtil.byteToInt(aidPara.getTermAppVer()) == 0 ||
                    StringUtil.byteToInt(aidPara.getDDOL()) == 0) {
                return -8011;
            }

//            emvAids[j] = new EmvAid(aidPara.getAID(), aidPara.getAppSelIndicator(), aidPara.getTermAppVer());
//            emvAids[j].setTac(aidPara.getTAC_Default(), aidPara.getTAC_Online(), aidPara.getTAC_Denial());
//            emvAids[j].setLimit(aidPara.getTFL_Domestic(), aidPara.getEC_TFL(),aidPara.getRFTransLimit(), aidPara.getRFOfflineLimit(), aidPara.getRFCVMLimit());
//            emvAids[j].setDDOL(aidPara.getDDOL());
//            emvAids[j].setRandomTransactionParam(aidPara.getThresholdValueDomestic(), aidPara.getMaxTargetDomestic(), aidPara.getTargetPercentageDomestic());

            strEmvAids[i] = emvAidParaToTlv(aidPara);
            if (i == (aidParaList.size() - 1)) {
                break;
            }
        }

        int result = EmvL2.getInstance(mContext, mContext.getPackageName()).addAids(strEmvAids);
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
    private String emvAidParaToTlv(EmvAidPara aidPara) {
        if (aidPara == null || aidPara.getAID_Length() == 0 || aidPara.getAID() == null
                || aidPara.getTermAppVer() == null) {
            return null;
        }
        String string = "";
        //aid
        string += "9F06";
        string += String.format(Locale.getDefault(), "%02x", aidPara.getAID_Length());
        byte[] correctAid = ByteUtil.cut(aidPara.getAID(), 0, aidPara.getAID_Length());
        string += StringUtil.byte2HexStr(correctAid);
        //应用选择指示符
        string += "DF0101" + StringUtil.byte2HexStr(aidPara.getAppSelIndicator());
        //应用版本号
        string += "9F0802" + StringUtil.byte2HexStr(aidPara.getTermAppVer());
        //TAC－缺省
        string += "DF1105" + StringUtil.byte2HexStr(aidPara.getTAC_Default());
        //TAC－联机
        string += "DF1205" + StringUtil.byte2HexStr(aidPara.getTAC_Online());
        //TAC－拒绝
        string += "DF1305" + StringUtil.byte2HexStr(aidPara.getTAC_Denial());
        //终端最低限额
        string += "9F1B04" + StringUtil.byte2HexStr(aidPara.getTFL_Domestic());
        //偏置随机选择的阈值
        string += "DF1504" + StringUtil.byte2HexStr(aidPara.getThresholdValueDomestic());
        //偏置随机选择的最大目标百分数
        string += "DF1601" + StringUtil.byte2HexStr(aidPara.getMaxTargetDomestic());
        //随机选择的目标百分数
        string += "DF1701" + StringUtil.byte2HexStr(aidPara.getTargetPercentageDomestic());
        //	缺省DDOL
        string += "DF14";
        string += String.format(Locale.getDefault(), "%02x", aidPara.getDDOL_Length());
        byte[] ddol = ByteUtil.cut(aidPara.getDDOL(), 0, aidPara.getDDOL_Length());
        string += StringUtil.byte2HexStr(ddol);
        //终端联机PIN支持能力
        string += "DF180101";
        //终端电子现金交易限额
        string += "9F7B06" + StringUtil.byte2HexStr(aidPara.getEC_TFL());
        //非接触读写器脱机最低限额
        string += "DF1906" + StringUtil.byte2HexStr(aidPara.getRFOfflineLimit());
        //非接触读写器交易限额
        string += "DF2006" + StringUtil.byte2HexStr(aidPara.getRFTransLimit());
        //读写器持卡人验证方法（CVM）所需限制
        string += "DF2106" + StringUtil.byte2HexStr(aidPara.getRFCVMLimit());
        Log.e("Aid Emv参数", string);
        return string;
    }

    /**
     * 加载EMV 公钥参数列表
     *
     * @param capkList CAPK列表
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int setCAPKList(List<EmvCapk> capkList) throws RemoteException {
        if (capkList == null || capkList.size() == 0) {
            return -2;
        }
//        socsi.middleware.emvl2lib.EmvCapk[] emvCapks = new socsi.middleware.emvl2lib.EmvCapk[capkList.size()];
        String[] strEmvCapks = new String[capkList.size()];
        for (int i = 0; i < capkList.size(); i++) {
            EmvCapk emvCapk = capkList.get(i);
            if (StringUtil.byteToInt(emvCapk.getCAPKModulus()) == 0 || StringUtil.byteToInt(emvCapk.getRID()) == 0
                    || StringUtil.byteToInt(emvCapk.getCAPKExponent()) == 0) {
                return -8011;
            }

//            String str = "RID " + StringUtil.bytesToHexString(emvCapk.getRID()) + "\n"
//                    + "CA_PKIndex " + StringUtil.byte2HexStr(emvCapk.getCA_PKIndex()) + "\n"
//                    + "CA_HashAlgoIndicator " + StringUtil.byte2HexStr(emvCapk.getCA_HashAlgoIndicator()) + "\n"
//                    + "CA_PKAlgoIndicator " + StringUtil.byte2HexStr(emvCapk.getCA_PKAlgoIndicator()) + "\n"
//                    + "LengthOfCAPKModulus " + emvCapk.getLengthOfCAPKModulus() + "\n"
//                    + "CAPKModulus " + StringUtil.bytesToHexString(emvCapk.getCAPKModulus()) + "\n"
//                    + "LengthOfCAPKExponent " + emvCapk.getLengthOfCAPKExponent() + "\n"
//                    + "CAPKExponent " + StringUtil.bytesToHexString(emvCapk.getCAPKExponent()) + "\n"
//                    + "ChecksumHash " + StringUtil.bytesToHexString(emvCapk.getChecksumHash()) + "\n"
//                    + "CAPKExpDate " + StringUtil.bytesToHexString(emvCapk.getCAPKExpDate());
//            Log.v("EmvCapks", str);

            strEmvCapks[i] = emvCapkToTlv(emvCapk);
            if (i == (capkList.size() - 1)) {
                break;
            }
//            EmvSm2Pubkey sm2Pubkey = new EmvSm2Pubkey();//国密算法
//            EmvRsaPubkey rsaPubkey = new EmvRsaPubkey(emvCapk.getCAPKModulus(), emvCapk.getCAPKExponent());//Rsa 算法
//            emvCapks[j] = new socsi.middleware.emvl2lib.EmvCapk(emvCapk.getCAPKExpDate(), emvCapk.getRID(), emvCapk.getCA_PKIndex(), rsaPubkey, emvCapk.getChecksumHash());
        }

        int result = EmvL2.getInstance(mContext, mContext.getPackageName()).addCapks(strEmvCapks);
        if (result != 0) {
            return -1;
        }
        return 0;
    }

    /**
     * 装载String类型的EMV公钥
     *
     * @param capk EMV公钥
     * @return 字符串
     */
    private String emvCapkToTlv(EmvCapk capk) {
        if (capk == null) {
            return null;
        }
        String string = "";
        //应用标识 RID
        string += "9F0605" + StringUtil.byte2HexStr(capk.getRID());
        //认证中心公钥索引
        string += "9F2201" + StringUtil.byte2HexStr(capk.getCA_PKIndex());
        //认证中心公钥失效期
//        byte[] temp = new byte[8];
//        System.arraycopy(capk.getCAPKExpDate(), 0, temp, 0, 4);
        string += "DF0504" + StringUtil.byte2HexStr(capk.getCAPKExpDate());
        //认证中心哈希算法标识
        string += "DF0601" + StringUtil.byte2HexStr(capk.getCA_HashAlgoIndicator());
        //认证中心公钥算法标识
        string += "DF0701" + StringUtil.byte2HexStr(capk.getCA_PKAlgoIndicator());
        //认证中心公钥模
        string += "DF02";
        if (capk.getLengthOfCAPKModulus() > 127) {
            string += "81";
        }
        string += String.format(Locale.getDefault(), "%02x", capk.getLengthOfCAPKModulus());
        byte[] moduls = ByteUtil.cut(capk.getCAPKModulus(), 0, capk.getLengthOfCAPKModulus());
        string += StringUtil.byte2HexStr(moduls);
        //认证中心公钥指数
        string += "DF04";
        string += String.format(Locale.getDefault(), "%02x", capk.getLengthOfCAPKExponent());
        byte[] exponent = ByteUtil.cut(capk.getCAPKExponent(), 0, capk.getLengthOfCAPKExponent());
        string += StringUtil.byte2HexStr(exponent);
        //认证中心公钥校验值
        string += "DF0314" + StringUtil.byte2HexStr(capk.getChecksumHash());
        Log.v("Capk EMV公钥", string);
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
        List<byte[]> emvCapks = EmvL2.getInstance(mContext, mContext.getPackageName()).getCapks();
        if (emvCapks != null) {
            List<EmvCapk> emvCapkList = new ArrayList<>();
            for (int i = 0; i < emvCapks.size(); i++) {
                byte[] emvCapk = emvCapks.get(i);
                EmvCapk mEmvCapk = new EmvCapk();
                Map<String, Tlv> tlvMap = TlvUtils.builderTlvMap(StringUtil.bytesToHexString(emvCapk));
                assert tlvMap != null;
                Tlv tlv = tlvMap.get("9F06");
                if (tlv != null) {
                    mEmvCapk.setRID(StringUtil.hexStr2Bytes(tlv.getValue()));//应用标识AID/RID
                }
                Tlv tlv2 = tlvMap.get("9F22");
                if (tlv2 != null) {
                    mEmvCapk.setCA_PKIndex((byte) ((int) Integer.valueOf(tlv2.getValue())));//认证中心公钥索引
                }
                Tlv tlv3 = tlvMap.get("DF05");
                if (tlv3 != null) {
                    mEmvCapk.setCAPKExpDate(StringUtil.hexStr2Bytes(tlv3.getValue()));//认证中心公钥有效期
                }
                Tlv tlv4 = tlvMap.get("DF06");
                if (tlv4 != null) {
                    mEmvCapk.setCA_HashAlgoIndicator((byte) ((int) Integer.valueOf(tlv4.getValue())));//认证中心公钥哈什算法标识
                }
                Tlv tlv5 = tlvMap.get("DF07");
                if (tlv5 != null) {
                    mEmvCapk.setCA_PKAlgoIndicator((byte) ((int) Integer.valueOf(tlv5.getValue())));//认证中心公钥算法标识
                }
                Tlv tlv6 = tlvMap.get("DF02");
                if (tlv6 != null) {
                    mEmvCapk.setCAPKModulus(StringUtil.hexStr2Bytes(tlv6.getValue()));//认证中心公钥模
                }
                Tlv tlv7 = tlvMap.get("DF04");
                if (tlv7 != null) {
                    mEmvCapk.setCAPKExponent(StringUtil.hexStr2Bytes(tlv7.getValue()));//认证中心公钥指数
                }
                Tlv tlv8 = tlvMap.get("DF03");
                if (tlv8 != null) {
                    mEmvCapk.setChecksumHash(StringUtil.hexStr2Bytes(tlv8.getValue()));//认证中心公钥校验值
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
        List<byte[]> emvAids = EmvL2.getInstance(mContext, mContext.getPackageName()).getAids();
        if (emvAids != null) {
            List<EmvAidPara> emvAidParaList = new ArrayList<>();
            for (int i = 0; i < emvAids.size(); i++) {
                byte[] emvAid = emvAids.get(i);
                Log.v("Tlv", StringUtil.bytesToHexString(emvAid) + "\r\n");
                EmvAidPara emvAidPara = new EmvAidPara();
                Map<String, Tlv> tlvMap = TlvUtils.builderTlvMap(StringUtil.bytesToHexString(emvAid));
                assert tlvMap != null;
                Tlv tlv = tlvMap.get("9F06");
                if (tlv != null) {
                    emvAidPara.setAID(StringUtil.hexStr2Bytes(tlv.getValue()));//应用标识AID/RID
                }
                Tlv tlv2 = tlvMap.get("DF01");
                if (tlv2 != null) {
                    emvAidPara.setAppSelIndicator((byte) ((int) Integer.valueOf(tlv2.getValue())));//应用选择指示符（ASI）
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
                    emvAidPara.setMaxTargetPercentageInt((byte) ((int) Integer.valueOf(tlv9.getValue())));//偏置随机选择的最大目标百分数
                }
                Tlv tlv10 = tlvMap.get("DF17");
                if (tlv10 != null) {
                    emvAidPara.setTargetPercentageInt((byte) ((int) Integer.valueOf(tlv10.getValue())));//随机选择的目标百分数
                }
                Tlv tlv11 = tlvMap.get("DF14");
                if (tlv11 != null) {
                    emvAidPara.setDDOL(StringUtil.hexStr2Bytes(tlv11.getValue()));//缺省DDOL
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
        int type = 0;
        switch (pathId) {
            case 0:
                type = 0x01;//内核数据
                break;
            case 1:
                type = 0x00;//卡片数据
                break;
            default:
                break;
        }
        return EmvL2.getInstance(mContext, mContext.getPackageName()).getTag(tag, type);
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
        if (tag == null || value == null) {
            return -2;
        }
        int result = EmvL2.getInstance(mContext, mContext.getPackageName()).setTLV(StringUtil.byteArrayToInt(tag), value);
        if (result == 0) {
            return 0;
        }
        return -1;
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
        //参数错误
        if (listener == null) {
            return -2;
        }
        if (channelType != 0 && channelType != 1) {
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
                    if (iccLogNumber >= 0) {
                        Log.v("iccLogNumber", "交易日志个数为" + iccLogNumber);
                    } else {
                        Log.v("iccLogNumber", "获取交易日志个数失败");
                    }
                    List<EmvCardLog> cardLogList = new ArrayList<EmvCardLog>();
                    for (int j = 0; j < iccLogNumber; j++) {
                        EmvCardLog cardLog = new EmvCardLog();
                        ICCardLog icCardLog = EmvL2.getInstance(mContext, mContext.getPackageName()).getICCardMedelWithIndex(EmvApi.EMV_TRANS_PBOCLOG, j);
                        byte[] tlvs = EmvL2.getInstance(mContext, mContext.getPackageName()).getIccLogWithIndex(EmvApi.EMV_TRANS_PBOCLOG, j);
                        if (icCardLog != null) {
                            cardLog.setTransDate(icCardLog.getTransDate());//交易日期
                            cardLog.setDateExist(!icCardLog.getTransDate().isEmpty());
                            cardLog.setTransTime(icCardLog.getTransTime());//交易时间
                            cardLog.setTimeExist(!icCardLog.getTransTime().isEmpty());
                            cardLog.setAmt(icCardLog.getTransAmount());//授权金额
                            cardLog.setAmtExist(!icCardLog.getTransAmount().isEmpty());
                            cardLog.setOtherAmt(icCardLog.getOtherAmount());//其他金额
                            cardLog.setOtherAmtExist(!icCardLog.getOtherAmount().isEmpty());
                            cardLog.setCntCode(icCardLog.getTermCountryCode());//终端国家代码
                            cardLog.setCntCodeExist(!icCardLog.getTermCountryCode().isEmpty());
                            cardLog.setCurCode(icCardLog.getTransCurrencyCode());//交易货币代码
                            cardLog.setCurExist(!icCardLog.getTransCurrencyCode().isEmpty());
                            cardLog.setMerName(icCardLog.getMerchantName());//商户名称
                            cardLog.setMerNameExist(!icCardLog.getMerchantName().isEmpty());
                            cardLog.setAtc(icCardLog.getTransCount());//交易计数器
                            cardLog.setAtcExist(!icCardLog.getTransCount().isEmpty());
                            cardLog.setServeType(icCardLog.getTransType());//交易类型
                            cardLog.setIs9Cexist(!icCardLog.getTransType().isEmpty());
                            cardLog.setTlv(tlvs);//TLV
                            cardLog.setTlvLen(tlvs.length);//TLV长度
                            cardLogList.add(cardLog);
                        }
                    }

                    try {
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("scriptResult", null);
                        bundle.putParcelableArrayList("emvLog", (ArrayList<? extends Parcelable>) cardLogList);
                        bundle.putByteArray("ecBalance", null);
                        listener.onFinish(0, bundle);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
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
        EmvL2.getInstance(mContext, mContext.getPackageName()).clearAids();
        EmvL2.getInstance(mContext, mContext.getPackageName()).clearCapks();
        return 0;
    }

//    /**
//     * 第一电子货币余额
//     */
//    private long firstCurrencyBalance;
//    /**
//     * 第二电子货币余额
//     */
//    private long secondCurrencyBalance;

    /**
     * 获取电子现金余额
     *
     * @param listener    执行结果监听
     * @param channelType 应用来源(见EmvChannelType类描述)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int emvGetEcBalance(final OnEmvProcessListener listener, int channelType) throws RemoteException {
        if (channelType != 0 && channelType != 1 || listener == null) {
            return -2;
        }
        logListener = listener;
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
                    case EmvApi.EMV_TRANS_EC_GOON_AMOUNT://接触
                        break;
                    case EmvApi.EMV_TRANS_RF_GOON_AMOUNT://非接触
                        //第一余额
                        long firstCurrencyBalance = EmvL2.getInstance(mContext, mContext.getPackageName()).getECBalance(false);
                        //第二余额
//                        long secondCurrencyBalance = EmvL2.getInstance(mContext, mContext.getPackageName()).getECBalance(true);
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putByteArray("scriptResult", null);
                            bundle.putParcelableArrayList("emvLog", null);
                            bundle.putByteArray("ecBalance", StringUtil.hexStr2Bytes(Long.toHexString(firstCurrencyBalance)));
                            listener.onFinish(0, bundle);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
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
        Log.d("Debug", "onSetSelAppResponse" + selResult);
        if (selResult < 0) {
            emvListener.onFinish(-8020, null);
            logListener.onFinish(-8020, null);
            selectHandler.onAidSelect(-1);
        }
        selectHandler.onAidSelect(0);
//        Bundle bundle = new Bundle();
//        logListener.onFinish(0, bundle);
    }

    /**
     * 确认卡号结果响应
     *
     * @param isConfirm 是否确认卡号
     * @throws RemoteException
     */
    @Override
    public void onSetConfirmCardNoResponse(final boolean isConfirm) throws RemoteException {
        Log.d("Debug", "ConfirmCardNoResponse:" + isConfirm);
        if (!isConfirm) {
            confirmHandler.onPanConfirm(-1);
            Bundle bundle = new Bundle();
            try {
                emvListener.onFinish(-8020, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }
        confirmHandler.onPanConfirm(0);
    }


    /**
     * 证件确认结果响应
     *
     * @param isVerify 是否确认证件号
     * @throws RemoteException
     */
    @Override
    public void onSetCertVerifyResponse(boolean isVerify) throws RemoteException {
        Log.d("Debug", "onSetCertVerifyResponse" + isVerify);
        if (!isVerify) {
            verifyHandler.onCertConfirm(0);
        } else {
            verifyHandler.onCertConfirm(1);
        }
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
        Log.d("Debug", "onSetOnlineProcResponse" + retCode);
        String resultCode = data.getString("RejCode");
        byte[] tlv = data.getByteArray("RecvField55");
        try {
             EmvL2.getInstance(mContext, mContext.getPackageName()).twiceAuthorization(resultCode, StringUtil.byte2HexStr(tlv));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置AID参数结果响应
     *
     * @param aid AID参数实例
     * @throws RemoteException
     */
    @Override
    public void onSetAIDParameterResponse(EmvAidPara aid) throws RemoteException {
        Log.d("Debug", "onSetAIDParameterResponse");
    }

    /**
     * 设置CA公钥结果响应
     *
     * @param capk CAPK公钥实例
     * @throws RemoteException
     */
    @Override
    public void onSetCAPubkeyResponse(EmvCapk capk) throws RemoteException {
        Log.d("Debug", "onSetCAPubkeyResponse");
    }

    /**
     * 终端风险管理响应
     *
     * @param result 格式为长度13的字符串 第一位（0-非黑名单 1-黑名单）后12位（右靠左补0 12位金额）当为黑名单时填12个0
     * @throws RemoteException
     */
    @Override
    public void onSetTRiskManageResponse(String result) throws RemoteException {
        Log.d("Debug", "onSetTRiskManageResponse" + result);
        //黑名单
        Character c = result.charAt(0);
        //金额
        String balance = result.substring(1);
        riskManagerHandler.onTermRiskManager(Long.parseLong(balance), Integer.parseInt(c.toString()));
    }

    /**
     * 非接流程错误代码获取
     *
     * @return 非接流程错误码
     * @throws RemoteException
     */
    @Override
    public String getRFErrorCode() throws RemoteException {
        Log.d("Debug", "getRFErrorCode");
        return null;
    }
}



