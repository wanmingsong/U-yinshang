package com.ums.upos.uapi.emv;

interface OnEmvProcessListener{
	void onSelApp(in List<String> appNameList, boolean isFirstSelect);
	
	void onConfirmCardNo(String cardNo);
	
	void onCardHolderInputPin(boolean isOnlinePin,int leftTimes);
	
	void onPinPress(byte keyCode);
	
	void onCertVerify(String certName, String certInfo);
	
	void onOnlineProc(in Bundle data);
	
	void onFinish(int retCode,in Bundle data);
	
	void onSetAIDParameter(String aid);
	
	void onSetCAPubkey(String rid, int index,int algMode);
	
	void onTRiskManage(String pan, String panSn);
}