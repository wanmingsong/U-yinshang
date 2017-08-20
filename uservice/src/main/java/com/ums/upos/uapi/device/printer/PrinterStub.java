package com.ums.upos.uapi.device.printer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.RemoteException;
import com.socsi.exception.SDKException;
import com.socsi.smartposapi.printer.FontLattice;
import com.socsi.smartposapi.printer.PrintRespCode;
import com.socsi.smartposapi.printer.Printer2;
import com.ums.upos.uapi.ServiceResult;
import static com.ums.upos.uapi.ServiceResult.Success;
import static com.ums.upos.uapi.ServiceResult.Printer_Base_Error;
import static com.ums.upos.uapi.ServiceResult.Printer_Busy;
import static com.ums.upos.uapi.ServiceResult.Printer_NoFontLib;
import static com.ums.upos.uapi.ServiceResult.Printer_Other_Error;
import static com.ums.upos.uapi.ServiceResult.Printer_PaperLack;
import static com.ums.upos.uapi.ServiceResult.Printer_Print_Fail;
import static com.ums.upos.uapi.ServiceResult.Printre_TooHot;

public class PrinterStub extends Printer.Stub{
	/**
	 * 单例对象
	 */
	private static volatile PrinterStub printerStub;
	/**
	 * 获取PrinterStub单例
	 *
	 * @return PrinterStub单例
	 */
	public static PrinterStub getInstance() {
		if (printerStub == null) {
			synchronized (PrinterStub.class) {
				if (printerStub == null) {
					printerStub = new PrinterStub();
				}
			}
		}
		return printerStub;
	}
	/**
	 * 打印机初始化
	 *
	 * @return 成功 Success 失败 Fail
	 */
	@Override
	public int initPrinter() throws RemoteException {
		try {
			PrintRespCode printRespCode = Printer2.getInstance().initPrinter();
			return toUnionRespCode(printRespCode);
		} catch (SDKException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 设置打印机参数
	 *
	 * @param bundle 各厂家打印机参数信息(见 PrinterConfig 类定义)
	 */
	@Override
	public void setConfig(Bundle bundle) throws RemoteException {
		if (bundle != null) {
			try {
				int grayLevel = bundle.getInt(PrinterConfig.COMMON_GRAYLEVEL, -1);
				if (grayLevel >= 0) {
					if (grayLevel > 15) {
						grayLevel = 15;
					}
					try {
						Printer2.getInstance().setPrintConcentration(grayLevel);
					} catch (SDKException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开始打印
	 *
	 * @param listener 打印结果监听实例
	 * @return 见返回值列表
	 */
	@Override
	public int startPrint(OnPrintListener listener) throws RemoteException {
		if (listener == null) {
			return -2;
		}
		PrintRespCode printRespCode = Printer2.getInstance().startPrint();
		int printerStatus = toUnionRespCode(printRespCode);
		switch (printerStatus){
			case ServiceResult.Success:
				listener.onPrintResult(0);
			break;
			case ServiceResult.Printer_Print_Fail:
				listener.onPrintResult(-1001);
				break;
			case ServiceResult.Printer_Busy:
				listener.onPrintResult(-1004);
				break;
			case ServiceResult.Printer_PaperLack:
				listener.onPrintResult(-1005);//打印机缺纸
				break;
			default:
				break;
		}
		return printerStatus;
	}

	/**
	 * 获取当前打印机状态
	 *
	 * @return 见返回值列表
	 */
	@Override
	public int getStatus() throws RemoteException {
		try {
			return toUnionRespCode(Printer2.getInstance().getPrinterStatus());
		} catch (SDKException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 设置打印文字数据
	 *
	 * @param text       打印数据
	 * @param fontSize   字体 (以目前银商总公司银行卡程序大中小字体为准,见 FontFamily 类定义)
	 * @param isBoldFont 是否加粗(详见FontFamily类定义)
	 * @return 见返回值列表
	 */
	@Override
	public int appendPrnStr(String text, int fontSize, boolean isBoldFont) throws RemoteException {
		if (text == null || text.isEmpty()) {
			return -1006;
		}
		if (fontSize < 0 || fontSize > 2){
			fontSize = 1;
		}
		FontLattice fontLattice;
		switch (fontSize) {
			case 0:
				fontLattice = FontLattice.SIXTEEN;
				break;
			case 1:
				fontLattice = FontLattice.TWENTY_FOUR;
				break;
			case 2:
				fontLattice = FontLattice.THIRTY_TWO;
				break;
			default:
				return -2;
		}
		return Printer2.getInstance().appendPrnStr(text, fontLattice, isBoldFont);
	}

	/**
	 * 设置打印图片数据
	 *
	 * @param bitmap 图片位图
	 * @return 见返回值列表
	 */
	@Override
	public int appendImage(Bitmap bitmap) throws RemoteException {
		if (bitmap == null) {
			return -1006;
		}
		return Printer2.getInstance().appendImage(bitmap);
	}

	/**
	 * 打印机走纸
	 *
	 * @param value 走纸量
	 * @param unit  走纸单位(见FeedUnit类定义)
	 */
	@Override
	public void feedPaper(int value, int unit) throws RemoteException {
		Printer2.getInstance().takePaper(unit, value);
	}

	/**
	 * 切纸补偿
	 */
	@Override
	public void cutPaper() throws RemoteException {
		//暂定5行，需根据实际情况修改
		feedPaper(5, FeedUnit.LINE);
	}

	/**
	 * 将打印机的返回值转为银联的返回值
	 * @param printRespCode 打印机的返回值
	 * @return 银联的返回值
	 */
	private int toUnionRespCode(PrintRespCode printRespCode) {
		if (printRespCode == PrintRespCode.Print_Success) {
			return Success;
		} else if (printRespCode == PrintRespCode.Printer_Busy) {
			return Printer_Busy;
		} else if (printRespCode == PrintRespCode.Printer_PaperLack) {
			return Printer_PaperLack;
		} else if (printRespCode == PrintRespCode.Printre_TooHot) {
			return Printre_TooHot;
		} else if (printRespCode == PrintRespCode.Printer_NO_TTF) {
			return Printer_NoFontLib;
		} else if (printRespCode == PrintRespCode.Printer_Fail) {
			return Printer_Print_Fail;
		} else if (printRespCode == PrintRespCode.Print_NO_CONTENT){
			return Printer_Print_Fail;
		}else {
			return Printer_Other_Error;
		}
	}
}