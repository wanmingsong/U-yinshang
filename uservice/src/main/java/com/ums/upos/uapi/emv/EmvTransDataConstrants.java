package com.ums.upos.uapi.emv;

public class EmvTransDataConstrants {
	public final static String PROCTYPE = "procType";
	public final static String SEQNO = "posSer";
	public final static String TRANSAMT = "transAmt";
	public final static String CASHBACKAMT = "cashbackAmt";
	public final static String TRANSDATE = "transDate";
	public final static String TRANSTIME = "transTime";
	public final static String MERNAME = "merName";
	public final static String MERID = "merId";
	public final static String TERMID = "termId";
	public final static String B9C = "9C";
	public final static String ISSUPPORTEC = "isSupportEC";
	public final static String CHANNELTYPE = "channelType";
	public final static String MKEYIDX = "MKeyIdx";
	public final static String ISQPBOCFORCEONLINE = "isQpbocForceLine";
	public final static String ISNEEDPAN = "isNeedPan";
	public final static String PINPADTYPE = "pinPadType";
	public final static String ISSUPPORTCHINESECRYPTALG ="isSupportChineseCryptAlg";//是否支持国密
	/**
	 * PIN加密算法类型，取值见PinAlgorithmMode；
	 * 注意：如上层未传该值或者传入的值不在PinAlgorithmMode范围内，则默认使用PinAlgorithmMode.ISO9564FMT1
	 */
	public final static String PINALGMODE = "pinAlgMode";
}
