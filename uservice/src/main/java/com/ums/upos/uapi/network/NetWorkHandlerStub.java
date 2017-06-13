package com.ums.upos.uapi.network;

import android.os.Bundle;
import android.os.RemoteException;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.ShellUtils;

public class NetWorkHandlerStub extends NetWorkHandler.Stub {
    /**
     * 设置网络参数
     * @param bundle 网络参数数据(见 NetWorkParams 类定义)
     * @return 见返回值列表
     */
    @Override
    public int setNetworkConfig(Bundle bundle) throws RemoteException {
        if (bundle == null) {
            return -4;
        }
        //TODO 根据实际传入的值来分析
        int networkType = bundle.getInt(NetWorkParams.COMMON_NETWORKTYPE, -1);
        if (networkType > NetWorkType.LAN && networkType <= NetWorkType.WIFI) {

        }
        String wifiSSID = bundle.getString(NetWorkParams.WIFI_SSID, "");
        if (wifiSSID.length() > 0) {

        }
        int wifiAuthType = bundle.getInt(NetWorkParams.WIFI_AUTHTYPE, -1);
        if (wifiAuthType != -1) {

        }
        String wifiPassword = bundle.getString(NetWorkParams.WIFI_PASSWORD, "");
        if (wifiPassword.length() > 0) {

        }

        return 0;
    }

    /**
     * 打开指定通道
     * @param networkType 网络通道类型,见 NetWorkType 类定义
     * @param listener    网络操作结果监听
     * @return 是否打开成功
     */
    @Override
    public boolean open(int networkType, NetWorkListener listener) throws RemoteException {
        //不支持LAN
        if (networkType <= NetWorkType.LAN || networkType > NetWorkType.WIFI) {
            return false;
        }
        if (networkType == NetWorkType.GPRS) {
            if (!NetworkUtils.getDataEnabled()) {
                NetworkUtils.setDataEnabled(true);
            }
        } else if (networkType == NetWorkType.WIFI) {
            if (!NetworkUtils.getWifiEnabled()) {
                NetworkUtils.setWifiEnabled(true);
            }
        }
        return true;
    }

    /**
     * 关闭通道
     * @param networkType 网络通道类型,见 NetWorkType 类定义
     * @param listener    网络操作结果监听
     * @return 是否关闭成功
     */
    @Override
    public boolean close(int networkType, NetWorkListener listener) throws RemoteException {
        //不支持LAN
        if (networkType <= NetWorkType.LAN || networkType > NetWorkType.WIFI) {
            return false;
        }
        if (networkType == NetWorkType.GPRS) {
            if (NetworkUtils.getDataEnabled()) {
                NetworkUtils.setDataEnabled(false);
            }
        } else if (networkType == NetWorkType.WIFI) {
            if (NetworkUtils.getWifiEnabled()) {
                NetworkUtils.setWifiEnabled(false);
            }
        }
        return true;
    }

    /**
     * 检测通道
     * @param serverIp 服务器 IP
     * @param timeout  ping 超时时间(单位: 秒)
     * @param listener 网络操作监听
     */
    @Override
    public void ping(String serverIp, int timeout, NetWorkListener listener) throws RemoteException {
        if (listener != null) {
            if (serverIp == null || serverIp.length() == 0 || timeout < 0) {
                listener.onPingResult(-2);
                return;
            }
            //TODO 需测试验证
            ShellUtils.CommandResult result = ShellUtils.execCmd("ping -c 1 -W " + timeout + " " + serverIp, false);
            if (result.result == 0) {
                listener.onPingResult(0);
            } else {
                listener.onPingResult(-1);
            }
        }
    }

    /**
     * 获取 WIFI 热点名称
     * @param listener 网络操作监听
     */
    @Override
    public void scanWifi(NetWorkListener listener) throws RemoteException {
        if (listener != null) {
            if (!NetworkUtils.isWifiConnected()) {
                open(NetWorkType.WIFI, listener);
            }
//                    WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
//                    //存放周围wifi热点对象的列表
//                    ArrayList<ScanResult> list = (ArrayList<ScanResult>) wifiManager.getScanResults();
//                    //TODO 要测试下打开wifi是否是阻塞的
//                    Bundle wifiSpots = new Bundle();
//                    for (ScanResult result : list) {
//                    }
//                    //TODO 要以怎样的形式来存放热点列表
//                    listener.onScanWifiResult(0, wifiSpots);
        }
    }
}