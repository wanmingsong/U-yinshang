package com.ums.upos.uapi.device.reader.icc;

public class ReaderConfig {
	/** 通用IC读头KEY **/
	/**
	 * 需要初始化的通道号(1-7)
	 */
	public final static String COMMON_SLOT_CHANNEL = "slot_channel";
	/**
	 * 上电电压 0-1.8V 1-3V 2-5V
	 */
	public final static String COMMON_ICC_VOL = "icc_vol";
	/**
	 * 是否支持PPS协议
	 */
	public final static String COMMON_ICC_SUPPORT_PPS = "icc_support_pps";
	/**
	 * 表示上电复位使用速率 	0-9600 1-38400
	 */
	public final static String COMMON_ICC_SPEED = "icc_speed";
	/**
	 * 表示支持的规范类型 0-EMV 1-ISO7816-3
	 */
	public final static String COMMON_ICC_PROTOCOL = "icc_protocol";
	
	/** 百富专用IC读头KEY **/
	/**
	 * A卡输出电导写入允许：1--允许,其它—不允许
	 */
	public final static String PAX_A_CONDUCT_W = "a_conduct_w";
	/**
	 * A卡输出电导控制变量,有效范围0~63,超出时视为63
	 */
	public final static String PAX_A_CONDUCT_VAL = "a_conduct_val";
	/**
	 * M1卡输出电导写入允许 
	 */
	public final static String PAX_M_CONDUCT_W = "m_conduct_w";
	/**
	 * M1卡输出电导控制变量,有效范围0~63,超出时视为63 
	 */
	public final static String PAX_M_CONDUCT_VAL = "m_conduct_val";
	/**
	 * B卡调制指数写入允许
	 */
	public final static String PAX_B_MODULATE_W = "b_modulate_w";
	/**
	 * B卡调制指数控制变量,有效范围0~63,超出时视为63 
	 */
	public final static String PAX_B_MODULATE_VAL = "b_modulate_val";
	/**
	 * 卡片接收缓冲区大小写入允许
	 */
	public final static String PAX_CARD_BUFFER_W = "card_buffer_w";
	/**
	 * 卡片接收缓冲区大小参数(单位：字节),有效值1~256.大于256时,将以256写入设为0时,将不会写入  
	 */
	public final static String PAX_CARD_BUFFER_VAL = "card_buffer_val";
	/**
	 * S(WTX)响应发送次数写入允许（暂不适用）
	 */
	public final static String PAX_WAIT_RETRY_LIMIT_W = "wait_retry_limit_w";
	/**
	 * S(WTX)响应最大重试次数（暂不适用）
	 */
	public final static String PAX_WAIT_RETYR_LIMIT_VAL = "wait_retry_limit_val";
	/**
	 * 卡片类型检查写入允许
	 */
	public final static String PAX_CARD_TYPE_CHECK_W = "card_type_check_w";
	/**
	 * 0-检查卡片类型,其他－不检查卡片类型(默认为检查卡片类型)  
	 */
	public final static String PAX_CARD_TYPE_CHECK_VAL = "card_type_check_val";
	/**
	 * B卡片接收灵敏度写入允许：1--允许， 其它值--不允许。该值不可读  
	 */
	public final static String PAX_CARD_RXTHRESHOLD_W = "card_RxThreshold_w";
	/**
	 * B卡片接收灵敏度
	 */
	public final static String PAX_CARD_RXTHRESHOLD_VAL = "card_RxThreshold_val";
	/**
	 * Felica调制指数写入允许
	 */
	public final static String PAX_F_MODULATE_W = "f_modulate_w";
	/**
	 * Felica调制指数
	 */
	public final static String PAX_F_MODULATE_VAL = "f_modulate_val";
	/**
	 * A卡调制指数写入允许：1--允许，其它值—不允许
	 */
	public final static String PAX_A_MODULATE_W = "a_modulate_w";
	/**
	 * A卡调制指数控制变量，有效范围0~63,超出时视为63
	 */
	public final static String PAX_A_MODULATE_VAL = "a_modulate_val";
	/**
	 * A卡接收灵敏度检查写入允许：1--允许，其它值--不允许
	 */
	public final static String PAX_A_CARD_RXTHRESHOLD_W = "a_card_RxThreshold_w";
	/**
	 * A卡接收灵敏度
	 */
	public final static String PAX_A_CARD_RXTHRESHOLD_VAL = "a_card_RxThreshold_val";
	/**
	 * A卡的天线增益 
	 */
	public final static String PAX_A_CARD_ANTENNA_GAIN_W = "a_card_antenna_gain_w";
	/**
	 * A卡的天线增益
	 */
	public final static String PAX_A_CARD_ANTENNA_GAIN_VAL = "a_card_antenna_gain_val";
	/**
	 * B卡的天线增益
	 */
	public final static String PAX_B_CARD_ANTENNA_GAIN_W = "b_card_antenna_gain_w";
	/**
	 * B卡的天线增益
	 */
	public final static String PAX_B_CARD_ANTENNA_GAIN_VAL = "b_card_antenna_gain_val";
	/**
	 * C卡的天线增益 
	 */
	public final static String PAX_F_CARD_ANTENNA_GAIN_W = "f_card_antenna_gain_w";
	/**
	 * C卡的天线增益 
	 */
	public final static String PAX_F_CARD_ANTENNA_GAIN_VAL = "f_card_antenna_gain_val";
	/**
	 * Felica的接收灵敏度 
	 */
	public final static String PAX_F_CARD_RXTHRESHOLD_W = "f_card_RxThreshold_w";
	/**
	 * Felica的接收灵敏度 
	 */
	public final static String PAX_F_CARD_RXTHRESHOLD_VAL = "f_card_RxThreshold_val";
	/**
	 * Felica的电导率
	 */
	public final static String PAX_F_CONDUCT_W = "f_conduct_w";
	/**
	 * Felica的电导率
	 */
	public final static String PAX_F_CONDUCT_VAL = "f_conduct_val";

}
