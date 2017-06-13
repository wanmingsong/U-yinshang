package com.ums.upos.uapi.device.cashbox;

import com.ums.upos.uapi.device.cashbox.CashBoxListener;

interface CashBoxDriver{
	void openCashBox(CashBoxListener listener);
}