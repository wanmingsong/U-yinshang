package com.ums.upos.uapi.network;

interface NetWorkListener{
	void onOpenResult(int retCode);
	
	void onPingResult(int retCode);
	
	void onScanWifiResult(int retCode,in Bundle wifiSpots);

	void onCloseResult(int retCode);	
}