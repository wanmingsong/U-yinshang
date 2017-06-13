package com.ums.upos.uapi.emv;

import android.os.Parcel;
import android.os.Parcelable;

public class EmvAidPara implements Parcelable {
	/** AID */
	private byte[] AID = new byte[16];

	/** AID长度 */
	private int AID_Length;

	/** 应用选择指示符 */
	private byte AppSelIndicator;

	/** 终端优先级 */
	private byte TerminalPriority;

	/** 偏置随机选择的最大目标百分数 */
	private byte MaxTargetDomestic;

	/** 随机选择的目标百分数 */
	private byte TargetPercentageDomestic;

	/** 终端最低限额 */
	private byte[] TFL_Domestic = new byte[4];

	/** 偏置随机选择的阈值 */
	private byte[] ThresholdValueDomestic = new byte[4];

	/** 偏置随机选择的最大目标百分数 */
	private byte MaxTargetPercentageInt;

	/** 随机选择的目标百分数 */
	private byte TargetPercentageInt;

	/** 终端最低限额 */
	private byte[] TFL_International = new byte[4];

	/** 偏置随机选择的阈值 */
	private byte[] ThresholdValueInt = new byte[4];

	/** 终端应用版本 */
	private byte[] TermAppVer = new byte[4];

	/** 商户类别代码tag: 9F15 */
	private byte[] MerCateCode = new byte[2];

	/** 交易类别代码Europay only, tag: 9F53 */
	private byte TransCateCode;

	/** 货币代码tag: 5F2A */
	private byte[] TrnCurrencyCode = new byte[2];

	/** 国家代码终端tag: 9F1A */
	private byte[] TermCountryCode = new byte[2];

	/** TAC缺省data format (n 5) */
	private byte[] TAC_Default = new byte[5];

	/** TAC拒绝data format (n 5) */
	private byte[] TAC_Denial = new byte[5];

	/** TAC联机data format (n 5) */
	private byte[] TAC_Online = new byte[5];

	/** DDOL */
	private byte[] DDOL = new byte[20];

	/** DDOL长度 */
	private byte DDOL_Length;

	/** TDOL */
	private byte[] TDOL = new byte[20];

	/** TDOL长度 */
	private int TDOL_Length;

	/** ag: 5F36 */
	private byte TrnCurrencyExp;

	/** <终端电子现金交易限额tag: 9F7B n12 */
	private byte[] EC_TFL = new byte[6];

	/** 终端类型data format (n 3) */
	private byte TermType;

	/** 终端能力data format (n 3) */
	private byte[] TermCap = new byte[3];

	/** 终端附加性能data format (n 3) */
	private byte[] AddTermCap = new byte[5];

	/** 非接触脱机最低限额DF19 */
	private byte[] RFOfflineLimit = new byte[6];

	/** 非接触交易限额DF20 */
	private byte[] RFTransLimit = new byte[6];

	/** 终端执行CVM限额DF21 */
	private byte[] RFCVMLimit = new byte[6];

	/** 终端交易属性9F66 */
	private byte[] TransProp = new byte[4];

	/** 非接触状态检查， 0x00-不检查，0x01-检查 */
	private byte StatusCheck;

	private byte[] AcquirerID = new byte[6];

	public byte[] getAID() {
		return AID;
	}

	public void setAID(byte[] aID) {
		AID = aID;
	}

	public int getAID_Length() {
		return AID_Length;
	}

	public void setAID_Length(int aID_Length) {
		AID_Length = aID_Length;
	}

	public byte getAppSelIndicator() {
		return AppSelIndicator;
	}

	public void setAppSelIndicator(byte appSelIndicator) {
		AppSelIndicator = appSelIndicator;
	}

	public byte getTerminalPriority() {
		return TerminalPriority;
	}

	public void setTerminalPriority(byte terminalPriority) {
		TerminalPriority = terminalPriority;
	}

	public byte getMaxTargetDomestic() {
		return MaxTargetDomestic;
	}

	public void setMaxTargetDomestic(byte maxTargetDomestic) {
		MaxTargetDomestic = maxTargetDomestic;
	}

	public byte getTargetPercentageDomestic() {
		return TargetPercentageDomestic;
	}

	public void setTargetPercentageDomestic(byte targetPercentageDomestic) {
		TargetPercentageDomestic = targetPercentageDomestic;
	}

	public byte[] getTFL_Domestic() {
		return TFL_Domestic;
	}

	public void setTFL_Domestic(byte[] tFL_Domestic) {
		TFL_Domestic = tFL_Domestic;
	}

	public byte[] getThresholdValueDomestic() {
		return ThresholdValueDomestic;
	}

	public void setThresholdValueDomestic(byte[] thresholdValueDomestic) {
		ThresholdValueDomestic = thresholdValueDomestic;
	}

	public byte getMaxTargetPercentageInt() {
		return MaxTargetPercentageInt;
	}

	public void setMaxTargetPercentageInt(byte maxTargetPercentageInt) {
		MaxTargetPercentageInt = maxTargetPercentageInt;
	}

	public byte getTargetPercentageInt() {
		return TargetPercentageInt;
	}

	public void setTargetPercentageInt(byte targetPercentageInt) {
		TargetPercentageInt = targetPercentageInt;
	}

	public byte[] getTFL_International() {
		return TFL_International;
	}

	public void setTFL_International(byte[] tFL_International) {
		TFL_International = tFL_International;
	}

	public byte[] getThresholdValueInt() {
		return ThresholdValueInt;
	}

	public void setThresholdValueInt(byte[] thresholdValueInt) {
		ThresholdValueInt = thresholdValueInt;
	}

	public byte[] getTermAppVer() {
		return TermAppVer;
	}

	public void setTermAppVer(byte[] termAppVer) {
		TermAppVer = termAppVer;
	}

	public byte[] getMerCateCode() {
		return MerCateCode;
	}

	public void setMerCateCode(byte[] merCateCode) {
		MerCateCode = merCateCode;
	}

	public byte getTransCateCode() {
		return TransCateCode;
	}

	public void setTransCateCode(byte transCateCode) {
		TransCateCode = transCateCode;
	}

	public byte[] getTrnCurrencyCode() {
		return TrnCurrencyCode;
	}

	public void setTrnCurrencyCode(byte[] trnCurrencyCode) {
		TrnCurrencyCode = trnCurrencyCode;
	}

	public byte[] getTermCountryCode() {
		return TermCountryCode;
	}

	public void setTermCountryCode(byte[] termCountryCode) {
		TermCountryCode = termCountryCode;
	}

	public byte[] getTAC_Default() {
		return TAC_Default;
	}

	public void setTAC_Default(byte[] tAC_Default) {
		TAC_Default = tAC_Default;
	}

	public byte[] getTAC_Denial() {
		return TAC_Denial;
	}

	public void setTAC_Denial(byte[] tAC_Denial) {
		TAC_Denial = tAC_Denial;
	}

	public byte[] getTAC_Online() {
		return TAC_Online;
	}

	public void setTAC_Online(byte[] tAC_Online) {
		TAC_Online = tAC_Online;
	}

	public byte[] getDDOL() {
		return DDOL;
	}

	public void setDDOL(byte[] dDOL) {
		DDOL = dDOL;
	}

	public byte getDDOL_Length() {
		return DDOL_Length;
	}

	public void setDDOL_Length(byte dDOL_Length) {
		DDOL_Length = dDOL_Length;
	}

	public byte[] getTDOL() {
		return TDOL;
	}

	public void setTDOL(byte[] tDOL) {
		TDOL = tDOL;
	}

	public int getTDOL_Length() {
		return TDOL_Length;
	}

	public void setTDOL_Length(int tDOL_Length) {
		TDOL_Length = tDOL_Length;
	}

	public byte getTrnCurrencyExp() {
		return TrnCurrencyExp;
	}

	public void setTrnCurrencyExp(byte trnCurrencyExp) {
		TrnCurrencyExp = trnCurrencyExp;
	}

	public byte[] getEC_TFL() {
		return EC_TFL;
	}

	public void setEC_TFL(byte[] eC_TFL) {
		EC_TFL = eC_TFL;
	}

	public byte getTermType() {
		return TermType;
	}

	public void setTermType(byte termType) {
		TermType = termType;
	}

	public byte[] getTermCap() {
		return TermCap;
	}

	public void setTermCap(byte[] termCap) {
		TermCap = termCap;
	}

	public byte[] getAddTermCap() {
		return AddTermCap;
	}

	public void setAddTermCap(byte[] addTermCap) {
		AddTermCap = addTermCap;
	}

	public byte[] getRFOfflineLimit() {
		return RFOfflineLimit;
	}

	public void setRFOfflineLimit(byte[] rFOfflineLimit) {
		RFOfflineLimit = rFOfflineLimit;
	}

	public byte[] getRFTransLimit() {
		return RFTransLimit;
	}

	public void setRFTransLimit(byte[] rFTransLimit) {
		RFTransLimit = rFTransLimit;
	}

	public byte[] getRFCVMLimit() {
		return RFCVMLimit;
	}

	public void setRFCVMLimit(byte[] rFCVMLimit) {
		RFCVMLimit = rFCVMLimit;
	}

	public byte[] getTransProp() {
		return TransProp;
	}

	public void setTransProp(byte[] transProp) {
		TransProp = transProp;
	}

	public byte getStatusCheck() {
		return StatusCheck;
	}

	public void setStatusCheck(byte statusCheck) {
		StatusCheck = statusCheck;
	}

	public byte[] getAcquirerID() {
		return AcquirerID;
	}

	public void setAcquirerID(byte[] acquirerID) {
		AcquirerID = acquirerID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByteArray(this.AID);
		dest.writeInt(this.AID_Length);
		dest.writeByte(this.AppSelIndicator);
		dest.writeByte(this.TerminalPriority);
		dest.writeByte(this.MaxTargetDomestic);
		dest.writeByte(this.TargetPercentageDomestic);
		dest.writeByteArray(this.TFL_Domestic);
		dest.writeByteArray(this.ThresholdValueDomestic);
		dest.writeByte(this.MaxTargetPercentageInt);
		dest.writeByte(this.TargetPercentageInt);
		dest.writeByteArray(this.TFL_International);
		dest.writeByteArray(this.ThresholdValueInt);
		dest.writeByteArray(this.TermAppVer);
		dest.writeByteArray(this.MerCateCode);
		dest.writeByte(this.TransCateCode);
		dest.writeByteArray(this.TrnCurrencyCode);
		dest.writeByteArray(this.TermCountryCode);
		dest.writeByteArray(this.TAC_Default);
		dest.writeByteArray(this.TAC_Denial);
		dest.writeByteArray(this.TAC_Online);
		dest.writeByteArray(this.DDOL);
		dest.writeByte(this.DDOL_Length);
		dest.writeByteArray(this.TDOL);
		dest.writeInt(this.TDOL_Length);
		dest.writeByte(this.TrnCurrencyExp);
		dest.writeByteArray(this.EC_TFL);
		dest.writeByte(this.TermType);
		dest.writeByteArray(this.TermCap);
		dest.writeByteArray(this.AddTermCap);
		dest.writeByteArray(this.RFOfflineLimit);
		dest.writeByteArray(this.RFTransLimit);
		dest.writeByteArray(this.RFCVMLimit);
		dest.writeByteArray(this.TransProp);
		dest.writeByte(this.StatusCheck);
		dest.writeByteArray(this.AcquirerID);
	}

	public static final Parcelable.Creator<EmvAidPara> CREATOR = new Creator<EmvAidPara>() {

		@Override
		public EmvAidPara createFromParcel(Parcel source) {
			EmvAidPara eap = new EmvAidPara();
			source.readByteArray(eap.AID);
			eap.AID_Length = source.readInt();
			eap.AppSelIndicator = source.readByte();
			eap.TerminalPriority = source.readByte();
			eap.MaxTargetDomestic = source.readByte();
			eap.TargetPercentageDomestic = source.readByte();
			source.readByteArray(eap.TFL_Domestic);
			source.readByteArray(eap.ThresholdValueDomestic);
			eap.MaxTargetPercentageInt = source.readByte();
			eap.TargetPercentageInt = source.readByte();
			source.readByteArray(eap.TFL_International);
			source.readByteArray(eap.ThresholdValueInt);
			source.readByteArray(eap.TermAppVer);
			source.readByteArray(eap.MerCateCode);
			eap.TransCateCode = source.readByte();
			source.readByteArray(eap.TrnCurrencyCode);
			source.readByteArray(eap.TermCountryCode);
			source.readByteArray(eap.TAC_Default);
			source.readByteArray(eap.TAC_Denial);
			source.readByteArray(eap.TAC_Online);
			source.readByteArray(eap.DDOL);
			eap.DDOL_Length = source.readByte();
			source.readByteArray(eap.TDOL);
			eap.TDOL_Length = source.readInt();
			eap.TrnCurrencyExp = source.readByte();
			source.readByteArray(eap.EC_TFL);
			eap.TermType = source.readByte();
			source.readByteArray(eap.TermCap);
			source.readByteArray(eap.AddTermCap);
			source.readByteArray(eap.RFOfflineLimit);
			source.readByteArray(eap.RFTransLimit);
			source.readByteArray(eap.RFCVMLimit);
			source.readByteArray(eap.TransProp);
			eap.StatusCheck = source.readByte();
			source.readByteArray(eap.AcquirerID);
			return eap;
		}

		@Override
		public EmvAidPara[] newArray(int size) {
			return new EmvAidPara[size];
		}

	};
}
