package com.ums.upos.uapi.device.reader.icc;

interface OnSearchIccCardListener{
	void onSearchResult(int retCode,in Bundle bundle);
}