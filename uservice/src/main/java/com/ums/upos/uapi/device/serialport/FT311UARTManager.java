package com.ums.upos.uapi.device.serialport;

import android.content.Context;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/******************************
 * FT311 GPIO class
 ******************************************/
public class FT311UARTManager {

    private UsbManager usbmanager;
    private UsbAccessory usbaccessory;
    private ParcelFileDescriptor fileDescriptor = null;
    private FileInputStream inputStream = null;
    private FileOutputStream outputStream = null;
    private read_thread readThread;

    private byte[] usbData;
    private byte[] writeUSBData;
    private byte[] readBuffer; /*circular buffer 循环缓冲*/
    private int readCount;
    private int totalBytes;
    private int writeIndex;
    private int readIndex;
    private byte status;
    final int maxnumbytes = 65536;

    public boolean datareceived = false;
    public boolean READ_ENABLE = false;
    public boolean accessory_attached = false;

    public Context global_context;

    private String ManufacturerString = "mManufacturer=FTDI";
    private String ModelString1 = "mModel=FTDIUARTDemo";
    private String ModelString2 = "mModel=Android Accessory FT312D";
    private String VersionString = "mVersion=1.0";

    public FT311UARTManager(Context context) {
        global_context = context;
        /*shall we start a thread here or what 我们应该在这里开始一个线程还是什么 */
        usbData = new byte[1024];
        writeUSBData = new byte[256];
        /*128(make it 256, but looks like bytes should be enough)*/
        readBuffer = new byte[maxnumbytes];

        readIndex = 0;
        writeIndex = 0;
        /***********************USB handling 处理 ******************************************/
        usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        inputStream = null;
        outputStream = null;
    }

    public void SetConfig() {
        /*prepare the baud rate buffer*/
        int baud = 9600;
        byte dataBits = (byte) 8;
        byte stopBits = (byte) 1;
        byte parity = (byte) 0;
        byte flowControl = (byte) 0;
        writeUSBData[0] = (byte) baud;
        writeUSBData[1] = (byte) (baud >> 8);
        writeUSBData[2] = (byte) (baud >> 16);
        writeUSBData[3] = (byte) (baud >> 24);
        /*data bits*/
        writeUSBData[4] = dataBits;
		/*stop bits*/
        writeUSBData[5] = stopBits;
		/*parity*/
        writeUSBData[6] = parity;
		/*flow control*/
        writeUSBData[7] = flowControl;
		/*send the UART configuration packet*/
        SendPacket((int) 8);
    }

    /*write data*/
    public byte SendData(int numBytes, byte[] buffer) {
        status = 0x00; /*success by default 成功默认 */
		/*
		 * if num bytes are more than maximum limit  如果num字节大于最大限制
		 */
        if (numBytes < 1) {
			/*return the status with the error in the command 返回状态与命令中的错误 */
            return status;
        }
		/*check for maximum limit*/
        if (numBytes > 256) {
            numBytes = 256;
        }
		/*prepare the packet to be sent*/
        System.arraycopy(buffer, 0, writeUSBData, 0, numBytes);
        if (numBytes != 64) {
            SendPacket(numBytes);
        } else {
            byte temp = writeUSBData[63];
            SendPacket(63);
            writeUSBData[0] = temp;
            SendPacket(1);
        }
        return status;
    }

    /*read data*/
    public byte ReadData(int numBytes, byte[] buffer, int[] actualNumBytes) {
        status = 0x00; /*success by default*/

		/*should be at least one byte to read 应至少有一个字节要读 */
        if ((numBytes < 1) || (totalBytes == 0)) {
            actualNumBytes[0] = 0;
            status = 0x01;
            return status;
        }

		/*check for max limit*/
        if (numBytes > totalBytes)
            numBytes = totalBytes;

		/*update the number of bytes available 更新可用的字节数 */
        totalBytes -= numBytes;

        actualNumBytes[0] = numBytes;

		/*copy to the user buffer*/
        for (int count = 0; count < numBytes; count++) {
            buffer[count] = readBuffer[readIndex];
            readIndex++;
			/*should not read more than what is there in the buffer,
			 * 	so no need to check the overflow 不应该读取多于缓冲区中存在的内容，因此不需要检查溢出
			 */
            readIndex %= maxnumbytes;
        }
        return status;
    }

    /**
     * method to send on USB 方法在USB上发送
     *
     * @param numBytes 发送的长度
     */
    private void SendPacket(int numBytes) {
        try {
            if (outputStream != null) {
                outputStream.write(writeUSBData, 0, numBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*resume accessory 恢复附件 */
    public int ResumeAccessory() {
        if (inputStream != null && outputStream != null) {
            return 4;
        }

        UsbAccessory[] accessories = usbmanager.getAccessoryList();
        if (accessories != null && accessories.length > 0) {
			Toast.makeText(global_context, "通道列表已获得...", Toast.LENGTH_SHORT).show();
        } else {
            accessory_attached = false;
            return 2;
        }

        UsbAccessory accessory = accessories[0];
        if (accessory != null) {
            if (!accessory.toString().contains(ManufacturerString)) {
                return 1;
            }
            if (!accessory.toString().contains(ModelString1) && !accessory.toString().contains(ModelString2)) {
                return 1;
            }
            if (!accessory.toString().contains(VersionString)) {
                return 1;
            }
			Toast.makeText(global_context, "通道开启!", Toast.LENGTH_SHORT).show();
            accessory_attached = true;
            if (usbmanager.hasPermission(accessory)) {
                OpenAccessory(accessory);
            } else {
				Toast.makeText(global_context, "串口打开失败，没有操作USB的权限", Toast.LENGTH_SHORT).show();
                return 3;
            }
        }
        return 0;
    }

    /**
     * 关闭串口设备
     */
    public void DestroyAccessory() {

//		if(true == bConfiged){
        READ_ENABLE = false;  // set false condition for handler_thread to exit waiting data loop 设置false条件使处理程序线程退出等待数据循环
        writeUSBData[0] = 0;  // send dummy data for inStream.read going 发送用于instream.read的虚拟数据
        SendPacket(1);
//		}
//		else
//		{
//			SetConfig();  // send default setting data for config
//			try{Thread.sleep(10);}
//			catch(Exception e){}
//
//			READ_ENABLE = false;  // set false condition for handler_thread to exit waiting data loop
//			writeUSBData[0] = 0;  // send dummy data for inStream.read going
//			SendPacket(1);
////			if(true == accessory_attached)
////			{
////				saveDefaultPreference();
////			}
//		}

        try {
            Thread.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloseAccessory();
    }

    public void OpenAccessory(UsbAccessory accessory) {
        fileDescriptor = usbmanager.openAccessory(accessory);
        if (fileDescriptor != null) {
            usbaccessory = accessory;

            FileDescriptor fd = fileDescriptor.getFileDescriptor();

            inputStream = new FileInputStream(fd);
            outputStream = new FileOutputStream(fd);
			/*check if any of them are null*/
            if (inputStream == null || outputStream == null) {
                return;
            }
            if (!READ_ENABLE) {
                READ_ENABLE = true;
                readThread = new read_thread(inputStream);
                readThread.start();
            }
        }
    }

    private void CloseAccessory() {
        try {
            if (fileDescriptor != null) {
                fileDescriptor.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

		/*FIXME, add the notification also to close the application 添加通知也可以关闭应用程序 */

        fileDescriptor = null;
        inputStream = null;
        outputStream = null;
    }

    /*usb input data handler*/
    private class read_thread extends Thread {
        FileInputStream inStream;

        read_thread(FileInputStream stream) {
            inStream = stream;
            this.setPriority(Thread.MAX_PRIORITY);
        }

        public void run() {
            while (READ_ENABLE) {
                while (totalBytes > (maxnumbytes - 1024)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    if (inStream != null) {
                        readCount = inStream.read(usbData, 0, 1024);
                        if (readCount > 0) {
                            for (int count = 0; count < readCount; count++) {
                                readBuffer[writeIndex] = usbData[count];
                                writeIndex++;
                                writeIndex %= maxnumbytes;
                            }

                            if (writeIndex >= readIndex)
                                totalBytes = writeIndex - readIndex;
                            else
                                totalBytes = (maxnumbytes - readIndex) + writeIndex;
                        }
                    }
                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        }
    }
}