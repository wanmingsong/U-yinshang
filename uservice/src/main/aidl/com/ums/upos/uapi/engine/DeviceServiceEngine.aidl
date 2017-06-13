package com.ums.upos.uapi.engine;

import com.ums.upos.uapi.device.beeper.Beeper;
import com.ums.upos.uapi.device.led.LEDDriver;
import com.ums.upos.uapi.device.printer.Printer;
import com.ums.upos.uapi.device.scanner.InnerScanner;
import com.ums.upos.uapi.card.cpu.CPUCardHandler;
import com.ums.upos.uapi.card.mifare.M1CardHandler;
import com.ums.upos.uapi.device.reader.mag.MagCardReader;
import com.ums.upos.uapi.device.reader.icc.IccCardReader;
import com.ums.upos.uapi.emv.EmvHandler;
import com.ums.upos.uapi.device.modem.ModemDriver;
import com.ums.upos.uapi.device.pinpad.PinPad;
import com.ums.upos.uapi.device.serialport.SerialPortDriver;
import com.ums.upos.uapi.network.NetWorkHandler;
import com.ums.upos.uapi.device.cashbox.CashBoxDriver;
import com.ums.upos.uapi.card.industry.IndustryCardHandler;
import com.ums.upos.uapi.engine.OnUninstallAppListener;

interface DeviceServiceEngine{

	int login(in Bundle bundle, String bussinessId);
	
	void logout();
	
	Bundle getDevInfo();
		
	Beeper getBeeper();
	
	LEDDriver getLEDDriver();
	
	Printer getPrinter();
	
	InnerScanner getInnerScanner();
	
	ModemDriver getModemDriver();
	
	SerialPortDriver getSerialPortDriver(int portNo);
	
	MagCardReader getMagCardReader();
	
	IccCardReader getIccCardReader(int slotNo);
	
	CPUCardHandler getCPUCardHandler(in IccCardReader reader);
	
	M1CardHandler  getM1CardHandler(in IccCardReader reader);
	
	PinPad getPinPad();
	
	EmvHandler getEmvHandler();
	
	NetWorkHandler getNetWorkHandler();
	
	//YYYYMMDDHHMMSS
	void setSystemClock(String datetime);
	
	//0-激活  1-关闭
	void setIMEStatus(int status); 
	
	//静默安装指定的App
	//appFilePath 应用文件路径  runActivityName 安装完毕之后需要运行的activity   appName 服务界面提示的应用名称（正在安装xxxx应用,请耐心等待）
	void installApp(String appFilePath,String runActivityName,String appName);
	
	//设备认证
	Bundle authDevice(String authRandom);
	
	//获得钞箱操作对象
	CashBoxDriver getCashBoxDriver();
	
	//获得行业卡操作对象
	IndustryCardHandler getIndustryCardHandler(in IccCardReader reader);
	
	//静默卸载指定的APP
	void uninstallApp(String packageName,OnUninstallAppListener listener);
}