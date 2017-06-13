package com.ums.upos.uapi.card.mifare;

import android.os.RemoteException;

import com.socsi.exception.MifareDriverException;
import com.socsi.exception.SDKException;
import com.socsi.smartposapi.card.rf.MifareDriver;

public class M1CardHandlerStub extends M1CardHandler.Stub {

    @Override
    public int authority(int keyType, int blkNo, byte[] pwd, byte[] serialNo) throws RemoteException {
        try {
            MifareDriver.getInstance().certificationWithExternalKey((byte) keyType, serialNo, (byte) blkNo, pwd);
        } catch (SDKException e) {
            e.printStackTrace();
        } catch (MifareDriverException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int readBlock(int blkNo, byte[] blkValue) throws RemoteException {
        try {
            blkValue = MifareDriver.getInstance().read((byte) blkNo);
        } catch (SDKException e) {
            e.printStackTrace();
        } catch (MifareDriverException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int writeBlock(int blkNo, byte[] blkValue) throws RemoteException {
        try {
            MifareDriver.getInstance().write((byte) blkNo, blkValue);
        } catch (SDKException e) {
            e.printStackTrace();
        } catch (MifareDriverException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int operateBlock(int operType, int blkNo, byte[] value, int updBlkNo) {
        try {
            switch (operType) {
                case M1CardOperType.INCREMENT:
                    MifareDriver.getInstance().appand((byte) blkNo, value);
                    break;
                case M1CardOperType.DECREMENT:
                    MifareDriver.getInstance().reduction((byte) blkNo, value);
                    break;
                case M1CardOperType.BACKUP:
                    MifareDriver.getInstance().backup((byte) blkNo, (byte) updBlkNo);
                    break;
            }
        } catch (SDKException | MifareDriverException e) {
            e.printStackTrace();
        }
        return 0;
    }
}



