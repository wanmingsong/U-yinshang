package com.ums.upos.uapi.device.scanner;

public class ScannerConfig {
	//扫描头种类 0-前置  1-后置
	public static final String COMM_SCANNER_TYPE = "scanner_type";
	//是否为连续扫描
	public static final String COMM_ISCONTINUOUS_SCAN="iscontinuous_scan";
	//连续扫描间隔时间
	public static final String COMM_CONTINUOUS_SCAN_PERIOD="continuous_scan_period";

	/** 百富专用扫描头参数KEY **/
	
	/**
	 * 是否支持1D产品码
	 */
	public static final String PAX_DECODE_1D_PRODUCT = "preferences_decode_1D_product";
	/**
	 * 是否支持1D工业码
	 */
	public static final String PAX_DECODE_1D_INDUSTRIAL = "preferences_decode_1D_industrial";	
	/**
	 * 是否支持PDF417码
	 */
	public static final String PAX_DECODE_PDF417 = "preferences_decode_PDF417";		
	/**
	 * 是否支持DM码
	 */
	public static final String PAX_DECODE_DATA_MATRIX = "preferences_decode_Data_Matrix";	
	/**
	 * 是否支持QR码
	 */
	public static final String PAX_DECODE_QR= "preferences_decode_QR";	
	/**
	 * 是否支持Aztec码
	 */
	public static final String PAX_DECODE_AZTEC = "preferences_decode_Aztec";
	/**
	 * 扫描成功后是否播放beep音
	 */
	public static final String PAX_PLAY_BEEP = "preferences_play_beep";		
	/**
	 * 是否使用前置摄像头
	 */
	public static final String PAX_USE_FRONT_CCD = "preferences_use_front_ccd";	
	/**
	 * 是否自动对焦
	 */
	public static final String PAX_AUTO_FOCUS = "preferences_auto_focus";
	/**
	 * 是否反向扫描
	 */
	public static final String PAX_INVERT_SCAN = "preferences_invert_scan";	
	/**
	 * 是否连续扫描
	 */
	public static final String PAX_BATCH_SCAN = "preferences_batch_scan";			
	/*******************************/		
			
	/** 联迪专用扫描头参数KEY **/
	
	/*******************************/	
	public static final String LANDI_LASER_ON_TIME = "Laser On Time";
	
	public static final String LANDI_PARAM_SCANNING =  "Parameter Scanning";
	
	public static final String LANDI_UPC_E1 = "UPC-E1";
	
	public static final String LANDI_BOOKLAND_EAN = "Bookland EAN";
	
	public static final String LANDI_DECODE_UPCEAN_SUP = "Decode UPC/EAN Supplementals";
	
	public static final String LANDI_EAN8_ZERO  = "EAN-8 Zero Extend";

	public static final String LANDI_BOOKLAND_ISBN_FORMAT = "Bookland ISBN Format";
	
	public static final String LANDI_TRIOPTICCODE39 = "Trioptic Code 39";

	public static final String LANDI_CODE39_TO_CODE32 = "Convert Code 39 to Code 32";

	public static final String LANDI_CODE32_PREF = "Code 32 Prefix";
		
}
