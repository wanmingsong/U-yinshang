package com.ums.upos.uapi.device.modem;

import com.ums.upos.uapi.device.modem.DialParam;

import com.ums.upos.uapi.device.modem.OnDialListener;

interface ModemDriver{
	void initModem(in Bundle data);

	int connect(in DialParam dialParam, in OnDialListener listener);

	boolean isConnected();

	void disconnect();

	int send(in byte[] buffer);

	int recv(out byte[] buffer);

	void clrBuffer();
}
