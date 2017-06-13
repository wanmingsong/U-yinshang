package com.ums.upos.uapi.device.modem;

public class ModemConfig {
	/** 通用拨号模块KEY **/
	/**
	 * 设置音频脉冲拨号 0x00双音多频拨号 0x01、脉冲拨号方式1、0x02脉冲拨号方式2
	 */
	public static final String COMMON_DIALPLUS_MODE = "dialplus_mode";
	/**
	 * 重拨次数，必须大于等于1
	 */
	public static final String COMMON_REDIALTIMES = "redial_times";
	/**
	 * 通讯超时（若在此时间内没有数据交换，MODEM则挂断，为0时无超时，单位：10S）
	 */
	public static final String COMMON_TIMEOUT = "timeout";
	/**
	 * 异步通讯方式
	 */
	public static final String COMMON_ASMODE = "asMode";
	
	/** 百富专用拨号模块KEY **/
	/**
	 * 是否检测拨号音 D0=0检测拨号音1不检测，D1=0主叫拨号1被叫来铃应答
	 */
	public static final String PAX_CHECK_DIALTONE = "check_dialtone";
	/**
	 * 摘机到拨号的等待时间（单位：100ms）范围20－255
	 */
	public static final String PAX_DT1 = "dt1";
	/**
	 * 双间拨号单一号码保持时间单位：1ms 范围50－255
	 */
	public static final String PAX_HT = "ht";
	/**
	 * 双音拨号两个号码之间的间隔时间 10ms 范围5－25
	 */
	public static final String PAX_WT = "wt";
	/**
	 * 通讯字节的设置、包括设置同步异步、波特率、线路、应答音超时等
	 */
	public static final String PAX_SSETUP = "ssetup";

}
