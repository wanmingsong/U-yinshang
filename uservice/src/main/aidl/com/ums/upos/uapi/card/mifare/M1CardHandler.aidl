package com.ums.upos.uapi.card.mifare;

interface M1CardHandler{
	int authority(int keyType,int blkNo,in byte[] pwd,in byte[] serialNo);

	int readBlock(int blkNo,out byte[] blkValue);

	int writeBlock(int blkNo,in byte[] blkValue);

	int operateBlock(int operType,int blkNo,in byte[] value,int updBlkNo);
}



