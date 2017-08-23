package com.ums.upos.uapi.device.scanner;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import karics.library.zxing.android.CaptureActivity;

public class InnerScannerStub extends InnerScanner.Stub {
    private Context context;
    private int timeout;
    private OnScannedListener listener;
    private static volatile InnerScannerStub scanner;

    private InnerScannerStub(Context context) {
        this.context = context;
    }

    public static InnerScannerStub getInstance(Context context) {
        if (scanner == null) {
            synchronized (InnerScannerStub.class) {
                if (scanner == null) {
                    scanner = new InnerScannerStub(context);
                }
            }
        }
        return scanner;
    }

    public void setScanner(int timeout, OnScannedListener listener) {
        this.timeout = timeout;
        this.listener = listener;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public OnScannedListener getOnScannerListener() {
        return this.listener;
    }

    /**
     * 初始化扫描头
     * @param bundle 厂家扫描头参数(详见ScannerConfig类定义)
     * @throws RemoteException
     */
    @Override
    public void initScanner(Bundle bundle) throws RemoteException {
        if (bundle != null) {
            switch (bundle.getInt(ScannerConfigConstant.SCANNER_TYPE, -100)) {
                case ScannerConfigConstant.SCANNER_TYPE_FRONT:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//                        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
//                            Camera.CameraInfo info = new Camera.CameraInfo();
//                            Camera.getCameraInfo(i, info);
//                            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) { //这就是前置摄像头，亲。
//                                Camera c = Camera.open(i);
//                                Camera.Parameters parameters = c.getParameters();
//                                parameters.set("camera-id", 2);//sumsung 2.3以前的手机　　前置
//                                c.setParameters(parameters);
//                            }
//                        }
//                    }
                    break;
                case ScannerConfigConstant.SCANNER_TYPE_BACK:
//                    Camera c = Camera.open();
//                    Camera.Parameters parameters = c.getParameters();
//                    parameters.set("camera-id", 1);//sumsung 2.3以前的手机　　后置
//                    c.setParameters(parameters);
                    break;
                case -100:
                    //入参错误
                    break;
                default:
                    //入参new Bundle() 各厂商默认采用最优解码扫描头初始化
                    break;
            }
        }
        //不报错，按照各自厂家默认的扫描头参数进行初始化

    }

    /**
     * 开始扫描
     *
     * @param timeout  扫描超时时间（毫秒）
     * @param listener 扫描监听实例(见OnScannedListener类定义)
     * @return 返回值
     * @throws RemoteException
     */
    @Override
    public int startScan(int timeout, OnScannedListener listener) throws RemoteException {
        if (listener != null) {
            setScanner(timeout, listener);
            Intent intent = new Intent(context, CaptureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return 0;
        }
        return -2;
    }

    /**
     * 停止扫描
     * @throws RemoteException
     */
    @Override
    public void stopScan() throws RemoteException {

    }
}


