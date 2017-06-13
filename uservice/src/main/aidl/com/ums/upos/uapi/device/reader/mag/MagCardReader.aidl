package com.ums.upos.uapi.device.reader.mag;

import  com.ums.upos.uapi.device.reader.mag.OnSearchMagCardListener;

interface MagCardReader{
	int searchCard(in OnSearchMagCardListener listener, int timeout);
	
	int searchCardEx(in OnSearchMagCardListener listener, int timeout,in Bundle data);
		
	void stopSearch();
	
	void setIsCheckLrc(boolean isCheckLrc);
}