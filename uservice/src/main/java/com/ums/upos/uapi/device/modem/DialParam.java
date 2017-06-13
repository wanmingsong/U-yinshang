package com.ums.upos.uapi.device.modem;

import android.os.Parcel;
import android.os.Parcelable;

public class DialParam implements Parcelable {

	private String phoneNumber1;
	private String phoneNumber2;
	private String phoneNumber3;
	private boolean isNeedOutLine;
	private String outLineNumber;
	private int outDelayTime;
	private int connTimeOut;
	
	

	public String getPhoneNumber1() {
		return phoneNumber1;
	}

	public void setPhoneNumber1(String phoneNumber1) {
		this.phoneNumber1 = phoneNumber1;
	}

	public String getPhoneNumber2() {
		return phoneNumber2;
	}

	public void setPhoneNumber2(String phoneNumber2) {
		this.phoneNumber2 = phoneNumber2;
	}

	public String getPhoneNumber3() {
		return phoneNumber3;
	}

	public void setPhoneNumber3(String phoneNumber3) {
		this.phoneNumber3 = phoneNumber3;
	}

	public boolean isNeedOutLine() {
		return isNeedOutLine;
	}

	public void setNeedOutLine(boolean isNeedOutLine) {
		this.isNeedOutLine = isNeedOutLine;
	}

	public String getOutLineNumber() {
		return outLineNumber;
	}

	public void setOutLineNumber(String outLineNumber) {
		this.outLineNumber = outLineNumber;
	}

	public int getOutDelayTime() {
		return outDelayTime;
	}

	public void setOutDelayTime(int outDelayTime) {
		this.outDelayTime = outDelayTime;
	}

	public int getConnTimeOut() {
		return connTimeOut;
	}

	public void setConnTimeOut(int connTimeOut) {
		this.connTimeOut = connTimeOut;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.phoneNumber1);
		dest.writeString(this.phoneNumber2);
		dest.writeString(this.phoneNumber3);
		dest.writeInt(this.isNeedOutLine ? 1 : 0);
		dest.writeString(this.outLineNumber);
		dest.writeInt(this.outDelayTime);
		dest.writeInt(this.connTimeOut);
	}

	public static final Parcelable.Creator<DialParam> CREATOR = new Parcelable.Creator<DialParam>() {

		@Override
		public DialParam createFromParcel(Parcel source) {
			DialParam dp = new DialParam();
			dp.phoneNumber1 = source.readString();
			dp.phoneNumber2 = source.readString();
			dp.phoneNumber3 = source.readString();
			dp.isNeedOutLine = (source.readInt() == 1);
			dp.outLineNumber = source.readString();
			dp.outDelayTime = source.readInt();
			dp.connTimeOut = source.readInt();
			return dp;
		}

		@Override
		public DialParam[] newArray(int size) {
			return new DialParam[size];
		}
	};
}
