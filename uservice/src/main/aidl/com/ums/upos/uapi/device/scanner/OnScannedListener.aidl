package com.ums.upos.uapi.device.scanner;

interface OnScannedListener{
	void onScanResult(int retCode, in byte[] data);
}