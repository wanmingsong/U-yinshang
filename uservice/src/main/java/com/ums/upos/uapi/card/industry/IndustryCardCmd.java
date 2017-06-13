package com.ums.upos.uapi.card.industry;

import android.os.Parcel;
import android.os.Parcelable;

public class IndustryCardCmd implements Parcelable {

	private byte p1;
	private byte p2;
	private int lc;
	private int le;
	private byte ins;
	private byte cla;
	private byte swa;
	private byte swb;
	private int dataOutLen;
	private byte[] dataIn = new byte[256];
	private byte[] dataOut = new byte[256];

	public byte getP1() {
		return p1;
	}

	public void setP1(byte p1) {
		this.p1 = p1;
	}

	public byte getP2() {
		return p2;
	}

	public void setP2(byte p2) {
		this.p2 = p2;
	}

	public int getLc() {
		return lc;
	}

	public void setLc(int lc) {
		this.lc = lc;
	}

	public int getLe() {
		return le;
	}

	public void setLe(int le) {
		this.le = le;
	}

	public byte getIns() {
		return ins;
	}

	public void setIns(byte ins) {
		this.ins = ins;
	}

	public byte getCla() {
		return cla;
	}

	public void setCla(byte cla) {
		this.cla = cla;
	}

	public byte getSwa() {
		return swa;
	}

	public void setSwa(byte swa) {
		this.swa = swa;
	}

	public byte getSwb() {
		return swb;
	}

	public void setSwb(byte swb) {
		this.swb = swb;
	}

	public int getDataOutLen() {
		return dataOutLen;
	}

	public void setDataOutLen(int dataOutLen) {
		this.dataOutLen = dataOutLen;
	}

	public byte[] getDataIn() {
		return dataIn;
	}

	public void setDataIn(byte[] dataIn) {
		this.dataIn = dataIn;
	}

	public byte[] getDataOut() {
		return dataOut;
	}

	public void setDataOut(byte[] dataOut) {
		this.dataOut = dataOut;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(p1);
		dest.writeByte(p2);
		dest.writeInt(lc);
		dest.writeInt(le);
		dest.writeByte(ins);
		dest.writeByte(cla);
		dest.writeByte(swa);
		dest.writeByte(swb);
		dest.writeInt(dataOutLen);
		dest.writeByteArray(dataIn);
		dest.writeByteArray(dataOut);
	}

	public void readFromParcel(Parcel _reply) {
		this.p1 = _reply.readByte();
		this.p2 = _reply.readByte();
		this.lc = _reply.readInt();
		this.le = _reply.readInt();
		this.ins = _reply.readByte();
		this.cla = _reply.readByte();
		this.swa = _reply.readByte();
		this.swb = _reply.readByte();
		this.dataOutLen = _reply.readInt();
		_reply.readByteArray(this.dataIn);
		_reply.readByteArray(this.dataOut);
	}

	public static final Parcelable.Creator<IndustryCardCmd> CREATOR = new Creator<IndustryCardCmd>() {

		@Override
		public IndustryCardCmd createFromParcel(Parcel source) {
			IndustryCardCmd cmd = new IndustryCardCmd();
			cmd.p1 = source.readByte();
			cmd.p2 = source.readByte();
			cmd.lc = source.readInt();
			cmd.le = source.readInt();
			cmd.ins = source.readByte();
			cmd.cla = source.readByte();
			cmd.swa = source.readByte();
			cmd.swb = source.readByte();
			cmd.dataOutLen = source.readInt();
			source.readByteArray(cmd.dataIn);
			source.readByteArray(cmd.dataOut);
			return cmd;
		}

		@Override
		public IndustryCardCmd[] newArray(int size) {
			return new IndustryCardCmd[size];
		}
	};

}
