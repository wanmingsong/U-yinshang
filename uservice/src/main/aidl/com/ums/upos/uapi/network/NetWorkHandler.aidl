package com.ums.upos.uapi.network;

import com.ums.upos.uapi.network.NetWorkListener;

interface NetWorkHandler{
	int setNetworkConfig(in Bundle bundle);
	
	boolean open(int networkType,in NetWorkListener listener);
	
	boolean close(int networkType,in NetWorkListener listener);

	void ping(String serverIp, int timeout, in NetWorkListener listener);
	
	void scanWifi(in NetWorkListener listener);
}