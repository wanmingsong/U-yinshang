package com.ums.upos.uapi.card.cpu;

import com.ums.upos.uapi.card.cpu.APDUCmd;

interface CPUCardHandler{
	boolean setPowerOn(out byte[] atr);
	
	void setPowerOff();
	
	int exchangeAPDUCmd(inout APDUCmd cmd);
	
	boolean halt();
	
	boolean active();
}