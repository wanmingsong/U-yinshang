package com.ums.upos.uapi.device.serialport;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.os.RemoteException;

import com.socsi.smartposapi.device.serialport.SerialPortManager;
import com.ums.upos.uapi.ServiceResult;

import java.io.IOException;

public class SerialPortDriverStub extends SerialPortDriver.Stub {
//    private FT311UARTManager uartManager;
    /**
     * 单例对象
     */
    private static volatile SerialPortDriverStub serialPort;
    /**
     * 构造函数
     */
    private SerialPortDriverStub(Context context){
//        uartManager = new FT311UARTManager(context);
//        uartManager.SetConfig();
    }

    /**
     * 获取EmvHandlerStub单例
     *
     * @param context 上下文
     * @return EmvHandlerStub单例
     */
    public static SerialPortDriverStub getInstance(Context context) {
        if (serialPort == null) {
            synchronized (SerialPortDriverStub.class) {
                if (serialPort == null) {
                    serialPort = new SerialPortDriverStub(context);
                }
            }
        }
        return serialPort;
    }

    /**
     * 打开串口
     * @param cfg 串口连接参数？
     * @return 串口连接状态
     * @throws RemoteException
     */
    @Override
    public int connect(String cfg) throws RemoteException {
        try {
            boolean connect = SerialPortManager.getIntance().connect("", 115200);
            if (connect) {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
//        int result = uartManager.ResumeAccessory();
//        if (result == 0){
//            return 0;
//        }
//        return -1;
    }

    /**
     * 发送数据
     * @param data 发送数据
     * @param dataLen 发送数据长度
     * @return 发送数据是否成功
     * @throws RemoteException
     */
    @Override
    public int send(byte[] data, int dataLen) throws RemoteException {
        if (data == null || data.length == 0 || dataLen <= 0){
            return -1;
        }
        if (dataLen > data.length) {
            return -1;
        }
        byte[] finalData = new byte[dataLen];
        System.arraycopy(data, 0, finalData, 0, dataLen);

        try {
            SerialPortManager.getIntance().send(finalData);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
//        byte result = uartManager.SendData(dataLen, data);
//        if (result == 0x00){
//            return 0;
//        }
//        return -1;
    }

    /**
     * 接收数据
     *
     * @param buffer  接收到的数据
     * @param recvLen 期望接收数据长度
     * @param timeout 数据接收超时时间(单位：ms)
     * @return 数据接收状态
     * @throws RemoteException
     */
    @Override
    public int recv(byte[] buffer, int recvLen, long timeout) throws RemoteException {
        if (recvLen <= 0){
            return -1;
        }
        try {
            int recv = SerialPortManager.getIntance().recv(buffer, recvLen, timeout);
            return recv;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
//        byte result = uartManager.ReadData(recvLen, buffer, new int[1]);
//        if (result == 0x00){
//            return 0;
//        }
//        return -1;
    }

    /**
     * 关闭串口
     * @return 串口关闭状态
     * @throws RemoteException
     */
    @Override
    public int disconnect() throws RemoteException {
        boolean disConnect = SerialPortManager.getIntance().disConnect();
        if (disConnect) {
            return 0;
        } else {
            return -4999;
        }
//        uartManager.DestroyAccessory();
//        return 0;
    }

    /**
     * 清除串口缓冲区
     *
     * @throws RemoteException
     */
    @Override
    public void clrBuffer() throws RemoteException {
        try {
            //true stand for input buffer,false stand for output buffer
            if (!SerialPortManager.getIntance().isBufferEmpty(false) || !SerialPortManager.getIntance().isBufferEmpty(true))
                SerialPortManager.getIntance().clrBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
