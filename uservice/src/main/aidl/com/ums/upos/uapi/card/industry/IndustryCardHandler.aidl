package com.ums.upos.uapi.card.industry;

import com.ums.upos.uapi.card.industry.IndustryCardCmd;

interface IndustryCardHandler{
	boolean setPowerOn(out byte[] atr);
	
	void setPowerOff();
	
	int exchangeIndustryCardCmd(inout IndustryCardCmd cmd);
	
	//获取行业卡类型,见IccCardType
	String getCardType();
}