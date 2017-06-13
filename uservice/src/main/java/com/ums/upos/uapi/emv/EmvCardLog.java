package com.ums.upos.uapi.emv;

import android.os.Parcel;
import android.os.Parcelable;

public class EmvCardLog implements Parcelable {
	/**
	 * 交易金额存在标识
	 * */
	private boolean isAmtExist;

	/**
	 * 交易金额（右靠左补0）
	 * */
	private String amt;

	/**
	 * 其它金额存在标识
	 * */
	private boolean isOtherAmtExist;

	/**
	 * 其它金额（右靠左补0）
	 * */
	private String otherAmt;

	/**
	 * 交易日期存在标识
	 * */
	private boolean isDateExist;

	/**
	 * 交易日期（YYMMDD）
	 * */
	private String transDate;

	/**
	 * 交易时间存在标识
	 * */
	private boolean isTimeExist;

	/**
	 * 交易时间
	 * */
	private String transTime;

	/**
	 * 国家代码存在标识
	 * */
	private boolean isCntCodeExist;

	/**
	 * 国家代码(9F1A)
	 * */
	private String cntCode;

	/**
	 * 货币代码存在标识
	 * */
	private boolean isCurExist;

	/**
	 * 货币代码(5F2A)
	 * */
	private String curCode;

	/**
	 * 交易计数器存在标识
	 * */
	private boolean isAtcExist;

	/**
	 * 交易计数器(9F36)
	 * */
	private String atc;

	/**
	 * 交易类型存在标识
	 * */
	private boolean is9Cexist;

	/**
	 * 交易类型(9C)
	 * */
	private String serveType;

	/**
	 * 商户名称存在标识
	 * */
	private boolean isMerNameExist;

	/**
	 * 商户名称(9F4E)
	 * */
	private String merName;

	/**
	 * TLV长度
	 * */
	private int tlvLen;

	/**
	 * TLV
	 * */
	private byte[] tlv = new byte[256];

	public boolean isAmtExist() {
		return isAmtExist;
	}

	public void setAmtExist(boolean isAmtExist) {
		this.isAmtExist = isAmtExist;
	}

	public String getAmt() {
		return amt;
	}

	public void setAmt(String amt) {
		this.amt = amt;
	}

	public boolean isOtherAmtExist() {
		return isOtherAmtExist;
	}

	public void setOtherAmtExist(boolean isOtherAmtExist) {
		this.isOtherAmtExist = isOtherAmtExist;
	}

	public String getOtherAmt() {
		return otherAmt;
	}

	public void setOtherAmt(String otherAmt) {
		this.otherAmt = otherAmt;
	}

	public boolean isDateExist() {
		return isDateExist;
	}

	public void setDateExist(boolean isDateExist) {
		this.isDateExist = isDateExist;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public boolean isTimeExist() {
		return isTimeExist;
	}

	public void setTimeExist(boolean isTimeExist) {
		this.isTimeExist = isTimeExist;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public boolean isCntCodeExist() {
		return isCntCodeExist;
	}

	public void setCntCodeExist(boolean isCntCodeExist) {
		this.isCntCodeExist = isCntCodeExist;
	}

	public String getCntCode() {
		return cntCode;
	}

	public void setCntCode(String cntCode) {
		this.cntCode = cntCode;
	}

	public boolean isCurExist() {
		return isCurExist;
	}

	public void setCurExist(boolean isCurExist) {
		this.isCurExist = isCurExist;
	}

	public String getCurCode() {
		return curCode;
	}

	public void setCurCode(String curCode) {
		this.curCode = curCode;
	}

	public boolean isAtcExist() {
		return isAtcExist;
	}

	public void setAtcExist(boolean isAtcExist) {
		this.isAtcExist = isAtcExist;
	}

	public String getAtc() {
		return atc;
	}

	public void setAtc(String atc) {
		this.atc = atc;
	}

	public boolean isIs9Cexist() {
		return is9Cexist;
	}

	public void setIs9Cexist(boolean is9Cexist) {
		this.is9Cexist = is9Cexist;
	}

	public String getServeType() {
		return serveType;
	}

	public void setServeType(String serveType) {
		this.serveType = serveType;
	}

	public boolean isMerNameExist() {
		return isMerNameExist;
	}

	public void setMerNameExist(boolean isMerNameExist) {
		this.isMerNameExist = isMerNameExist;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public int getTlvLen() {
		return tlvLen;
	}

	public void setTlvLen(int tlvLen) {
		this.tlvLen = tlvLen;
	}

	public byte[] getTlv() {
		return tlv;
	}

	public void setTlv(byte[] tlv) {
		this.tlv = tlv;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.isAmtExist ? 1 : 0);
		dest.writeString(this.amt);
		dest.writeInt(this.isOtherAmtExist ? 1 : 0);
		dest.writeString(this.otherAmt);
		dest.writeInt(this.isDateExist ? 1 : 0);
		dest.writeString(this.transDate);
		dest.writeInt(this.isTimeExist ? 1 : 0);
		dest.writeString(this.transTime);
		dest.writeInt(this.isCntCodeExist ? 1 : 0);
		dest.writeString(this.cntCode);
		dest.writeInt(this.isCurExist ? 1 : 0);
		dest.writeString(this.curCode);
		dest.writeInt(this.isAtcExist ? 1 : 0);
		dest.writeString(this.atc);
		dest.writeInt(this.is9Cexist ? 1 : 0);
		dest.writeString(this.serveType);
		dest.writeInt(this.isMerNameExist ? 1 : 0);
		dest.writeString(this.merName);
		dest.writeInt(this.tlvLen);
		dest.writeByteArray(this.tlv);
	}

	public static final Parcelable.Creator<EmvCardLog> CREATOR = new Creator<EmvCardLog>() {

		@Override
		public EmvCardLog createFromParcel(Parcel source) {
			EmvCardLog cardLog = new EmvCardLog();
			cardLog.isAmtExist = (source.readInt() == 1);
			cardLog.amt = source.readString();
			cardLog.isOtherAmtExist = (source.readInt() == 1);
			cardLog.otherAmt = source.readString();
			cardLog.isDateExist = (source.readInt() == 1);
			cardLog.transDate = source.readString();
			cardLog.isTimeExist = (source.readInt() == 1);
			cardLog.transTime = source.readString();
			cardLog.isCntCodeExist = (source.readInt() == 1);
			cardLog.cntCode = source.readString();
			cardLog.isCurExist = (source.readInt() == 1);
			cardLog.curCode = source.readString();
			cardLog.isAtcExist = (source.readInt() == 1);
			cardLog.atc = source.readString();
			cardLog.is9Cexist = (source.readInt() == 1);
			cardLog.serveType = source.readString();
			cardLog.isMerNameExist = (source.readInt() == 1);
			cardLog.merName = source.readString();
			cardLog.tlvLen = source.readInt();
			source.readByteArray(cardLog.tlv);
			return cardLog;
		}

		@Override
		public EmvCardLog[] newArray(int size) {
			return new EmvCardLog[size];
		}

	};
}
