package com.ums.upos.uapi.emv;

import com.ums.upos.uapi.emv.EmvCardLog;
import com.ums.upos.uapi.emv.EmvAidPara;
import com.ums.upos.uapi.emv.EmvCapk;
import com.ums.upos.uapi.emv.OnEmvProcessListener;

interface EmvHandler{
	int emvProcess(in Bundle data, in OnEmvProcessListener listener);

	int initTermConfig(in Bundle cfg);
	
	int setAidParaList(in List<EmvAidPara> aidParaList);
	
	int setCAPKList(in List<EmvCapk> capkList);
	
	List<EmvCapk> getCapkList();
	
	List<EmvAidPara> getAidParaList();
	
	byte[] getTlvs(in byte[] tag, int pathId);
	
	int setTlv(in byte[] tag, in byte[] value);
	
	int getEmvCardLog(int channelType,in OnEmvProcessListener listener);
	
	int clearLog();
	
	int emvGetEcBalance(in OnEmvProcessListener listener,int channelType);
	
	void onSetSelAppResponse(int selResult);
	
	void onSetConfirmCardNoResponse(boolean isConfirm);
	
	void onSetCertVerifyResponse(boolean isVerify);
	
	void onSetOnlineProcResponse(int retCode,in Bundle data);
	
	void onSetAIDParameterResponse(in EmvAidPara aid);
	
	void onSetCAPubkeyResponse(in EmvCapk capk);
	
	void onSetTRiskManageResponse(String result);
	
	String getRFErrorCode();
}