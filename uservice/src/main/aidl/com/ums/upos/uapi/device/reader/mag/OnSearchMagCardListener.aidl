package com.ums.upos.uapi.device.reader.mag;

import com.ums.upos.uapi.device.reader.mag.MagCardInfoEntity;

interface OnSearchMagCardListener{
	void onSearchResult(int retCode, in MagCardInfoEntity mcie);
}