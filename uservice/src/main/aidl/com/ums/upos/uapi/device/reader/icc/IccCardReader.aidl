package com.ums.upos.uapi.device.reader.icc;

import com.ums.upos.uapi.device.reader.icc.OnSearchIccCardListener;

interface IccCardReader{
	int searchCard(in OnSearchIccCardListener listener, int timeout, in String[] cardType);

	void stopSearch();

	boolean isCardExists();

	boolean setupReaderConfig(in Bundle bundle);
}