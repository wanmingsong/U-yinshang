package com.ums.upos.uapi.device.reader.mag;

import android.os.Parcel;
import android.os.Parcelable;

public class MagCardInfoEntity implements Parcelable {

	private String tk1;
	private String tk2;
	private String tk3;

	private int tk1ValidResult;
	private int tk2ValidResult;
	private int tk3ValidResult;
	
	private String cardNo;

	public String getTk1() {
		return tk1;
	}

	public void setTk1(String tk1) {
		this.tk1 = tk1;
	}

	public String getTk2() {
		return tk2;
	}

	public void setTk2(String tk2) {
		this.tk2 = tk2;
	}

	public String getTk3() {
		return tk3;
	}

	public void setTk3(String tk3) {
		this.tk3 = tk3;
	}

	public int getTk1ValidResult() {
		return tk1ValidResult;
	}

	public void setTk1ValidResult(int tk1ValidResult) {
		this.tk1ValidResult = tk1ValidResult;
	}

	public int getTk2ValidResult() {
		return tk2ValidResult;
	}

	public void setTk2ValidResult(int tk2ValidResult) {
		this.tk2ValidResult = tk2ValidResult;
	}

	public int getTk3ValidResult() {
		return tk3ValidResult;
	}

	public void setTk3ValidResult(int tk3ValidResult) {
		this.tk3ValidResult = tk3ValidResult;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.tk1);
		dest.writeString(this.tk2);
		dest.writeString(this.tk3);
		dest.writeInt(this.tk1ValidResult);
		dest.writeInt(this.tk2ValidResult);
		dest.writeInt(this.tk3ValidResult);
		dest.writeString(this.cardNo);
	}

	public void readFromParcel(Parcel _reply) {
		this.tk1 = _reply.readString();
		this.tk2 = _reply.readString();
		this.tk3 = _reply.readString();
		this.tk1ValidResult = _reply.readInt();
		this.tk2ValidResult = _reply.readInt();
		this.tk3ValidResult = _reply.readInt();
		this.cardNo = _reply.readString();
	}

	public static final Parcelable.Creator<MagCardInfoEntity> CREATOR = new Creator<MagCardInfoEntity>() {

		@Override
		public MagCardInfoEntity createFromParcel(Parcel source) {
			MagCardInfoEntity mcie = new MagCardInfoEntity();
			mcie.tk1 = source.readString();
			mcie.tk2 = source.readString();
			mcie.tk3 = source.readString();
			mcie.tk1ValidResult = source.readInt();
			mcie.tk2ValidResult = source.readInt();
			mcie.tk3ValidResult = source.readInt();
			mcie.cardNo = source.readString();
			return mcie;
		}

		@Override
		public MagCardInfoEntity[] newArray(int size) {
			return new MagCardInfoEntity[size];
		}
	};

}
