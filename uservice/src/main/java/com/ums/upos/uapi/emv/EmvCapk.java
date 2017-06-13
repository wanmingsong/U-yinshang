package com.ums.upos.uapi.emv;

import android.os.Parcel;
import android.os.Parcelable;

public class EmvCapk implements Parcelable {
	/** Registered Application Provider Identifier */
	private byte[] RID = new byte[5];
	
	/** 认证中心公钥索引 */
	private byte CA_PKIndex;

	/** 认证中心公钥哈什算法标识 */
	private byte CA_HashAlgoIndicator;

	/** 认证中心公钥算法标识 */
	private byte CA_PKAlgoIndicator;

	/** 认证中心公钥模长度 */
	private int LengthOfCAPKModulus;

	/** 认证中心公钥模 */
	private byte[] CAPKModulus = new byte[248];

	/** 认证中心公钥指数长度 */
	private int LengthOfCAPKExponent;

	/** 认证中心公钥指数 */
	private byte[] CAPKExponent = new byte[3];

	/** 认证中心公钥校验值 */
	private byte[] ChecksumHash = new byte[20];

	/** 认证中心公钥有效期 */
	private byte[] CAPKExpDate = new byte[4];

	public byte[] getRID() {
		return RID;
	}

	public void setRID(byte[] rID) {
		RID = rID;
	}

	public byte getCA_PKIndex() {
		return CA_PKIndex;
	}

	public void setCA_PKIndex(byte cA_PKIndex) {
		CA_PKIndex = cA_PKIndex;
	}

	public byte getCA_HashAlgoIndicator() {
		return CA_HashAlgoIndicator;
	}

	public void setCA_HashAlgoIndicator(byte cA_HashAlgoIndicator) {
		CA_HashAlgoIndicator = cA_HashAlgoIndicator;
	}

	public byte getCA_PKAlgoIndicator() {
		return CA_PKAlgoIndicator;
	}

	public void setCA_PKAlgoIndicator(byte cA_PKAlgoIndicator) {
		CA_PKAlgoIndicator = cA_PKAlgoIndicator;
	}

	public int getLengthOfCAPKModulus() {
		return LengthOfCAPKModulus;
	}

	public void setLengthOfCAPKModulus(int lengthOfCAPKModulus) {
		LengthOfCAPKModulus = lengthOfCAPKModulus;
	}

	public byte[] getCAPKModulus() {
		return CAPKModulus;
	}

	public void setCAPKModulus(byte[] cAPKModulus) {
		CAPKModulus = cAPKModulus;
	}

	public int getLengthOfCAPKExponent() {
		return LengthOfCAPKExponent;
	}

	public void setLengthOfCAPKExponent(int lengthOfCAPKExponent) {
		LengthOfCAPKExponent = lengthOfCAPKExponent;
	}

	public byte[] getCAPKExponent() {
		return CAPKExponent;
	}

	public void setCAPKExponent(byte[] cAPKExponent) {
		CAPKExponent = cAPKExponent;
	}

	public byte[] getChecksumHash() {
		return ChecksumHash;
	}

	public void setChecksumHash(byte[] checksumHash) {
		ChecksumHash = checksumHash;
	}

	public byte[] getCAPKExpDate() {
		return CAPKExpDate;
	}

	public void setCAPKExpDate(byte[] cAPKExpDate) {
		CAPKExpDate = cAPKExpDate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByteArray(RID);
		dest.writeByte(CA_PKIndex);
		dest.writeByte(CA_HashAlgoIndicator);
		dest.writeByte(CA_PKAlgoIndicator);
		dest.writeInt(LengthOfCAPKModulus);
		dest.writeByteArray(CAPKModulus);
		dest.writeInt(LengthOfCAPKExponent);
		dest.writeByteArray(CAPKExponent);
		dest.writeByteArray(ChecksumHash);
		dest.writeByteArray(CAPKExpDate);
	}

	public static final Parcelable.Creator<EmvCapk> CREATOR = new Creator<EmvCapk>() {

		@Override
		public EmvCapk createFromParcel(Parcel source) {
			EmvCapk ec = new EmvCapk();
			source.readByteArray(ec.RID);
			ec.CA_PKIndex = source.readByte();
			ec.CA_HashAlgoIndicator = source.readByte();
			ec.CA_PKAlgoIndicator = source.readByte();
			ec.LengthOfCAPKModulus = source.readInt();
			source.readByteArray(ec.CAPKModulus);
			ec.LengthOfCAPKExponent = source.readInt();
			source.readByteArray(ec.CAPKExponent);
			source.readByteArray(ec.ChecksumHash);
			source.readByteArray(ec.CAPKExpDate);
			return ec;
		}

		@Override
		public EmvCapk[] newArray(int size) {
			return new EmvCapk[size];
		}
	};
}
