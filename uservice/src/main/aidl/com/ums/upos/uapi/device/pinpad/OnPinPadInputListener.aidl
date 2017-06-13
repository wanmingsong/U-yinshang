package com.ums.upos.uapi.device.pinpad;

interface OnPinPadInputListener{
	void onInputResult(int retCode, in byte[] data);
	
	void onSendKey(byte keyCode);
}