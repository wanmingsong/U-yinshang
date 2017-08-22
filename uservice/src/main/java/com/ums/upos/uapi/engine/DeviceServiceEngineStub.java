package com.ums.upos.uapi.engine;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;

import com.blankj.utilcode.utils.AppUtils;
import com.blankj.utilcode.utils.EmptyUtils;
import com.blankj.utilcode.utils.KeyboardUtils;
import com.socsi.exception.SDKException;
import com.socsi.smartposapi.emv.emvl2.EMVL2;
import com.socsi.smartposapi.serialnumber.SerialNumberOperation;
import com.socsi.smartposapi.serialnumber.TerminalSerialNumberInfo;
import com.socsi.smartposapi.terminal.TerminalManager;
import com.ums.upos.uapi.card.cpu.CPUCardHandler;
import com.ums.upos.uapi.card.cpu.CPUCardHandlerStub;
import com.ums.upos.uapi.card.industry.IndustryCardHandler;
import com.ums.upos.uapi.card.industry.IndustryCardHandlerStub;
import com.ums.upos.uapi.card.mifare.M1CardHandler;
import com.ums.upos.uapi.card.mifare.M1CardHandlerStub;
import com.ums.upos.uapi.device.beeper.Beeper;
import com.ums.upos.uapi.device.beeper.BeeperStub;
import com.ums.upos.uapi.device.cashbox.CashBoxDriver;
import com.ums.upos.uapi.device.cashbox.CashBoxDriverStub;
import com.ums.upos.uapi.device.led.LEDDriver;
import com.ums.upos.uapi.device.led.LEDDriverStub;
import com.ums.upos.uapi.device.modem.ModemDriver;
import com.ums.upos.uapi.device.modem.ModemDriverStub;
import com.ums.upos.uapi.device.pinpad.PinPad;
import com.ums.upos.uapi.device.pinpad.PinPadStub;
import com.ums.upos.uapi.device.printer.Printer;
import com.ums.upos.uapi.device.printer.PrinterStub;
import com.ums.upos.uapi.device.reader.icc.IccCardReader;
import com.ums.upos.uapi.device.reader.icc.IccCardReaderStub;
import com.ums.upos.uapi.device.reader.mag.MagCardReader;
import com.ums.upos.uapi.device.reader.mag.MagCardReaderStub;
import com.ums.upos.uapi.device.scanner.InnerScanner;
import com.ums.upos.uapi.device.scanner.InnerScannerStub;
import com.ums.upos.uapi.device.serialport.SerialPortDriver;
import com.ums.upos.uapi.device.serialport.SerialPortDriverStub;
import com.ums.upos.uapi.emv.EmvHandler;
import com.ums.upos.uapi.emv.EmvHandlerStub;
import com.ums.upos.uapi.network.NetWorkHandler;
import com.ums.upos.uapi.network.NetWorkHandlerStub;

/**
 * 设备服务驱动引擎
 * Created by wangyuan on 17/4/19.
 */
public class DeviceServiceEngineStub extends DeviceServiceEngine.Stub {
    /**
     * 上下文
     */
    private Context mContext;
    private int workMode;

    public DeviceServiceEngineStub(Context context) {
        this.mContext = context;
    }

    /**
     * 登录设备服务
     *
     * @param bundle WorkMode 工作模式 0-传统模式 1-手机支付模式
     *                    无 WorkMode 的 Key 值时,默认工作模式为传统模式
     * @param bussinessId 业务 ID
     * @return 0 登录成功服务层存在EMV文件  1 登陆失败  2 登陆成功服务层不存在EMV文件
     *
     */
    @Override
    public int login(Bundle bundle, String bussinessId) throws RemoteException {
        Log.e("WY", "Bundle:" + bundle + ",bussinessId:" + bussinessId);
        if (bundle == null || bussinessId == null || bussinessId.length() == 0) {
            return 1;
        }
        if (Integer.parseInt(bussinessId) == 0) {
            return -4;  //参数错
        }
        //TODO 需确认两个入参有什么用
        workMode = bundle.getInt("WorkMode", 0);

//        String[] aids;
//        String[] rids;
//        try {
//            long start = System.currentTimeMillis();
//            aids = EMVL2.getInstance().getAID();
//            rids = EMVL2.getInstance().getRID();
//            long end = System.currentTimeMillis();
//            Log.e("WY", "get end-start=" + (end - start));
//        } catch (SDKException e) {
//            e.printStackTrace();
//            return 1;
//        }
//        if (aids != null && aids.length > 0 && rids != null && rids.length > 0) {
//            return 0;
//        }
//        return 2;
        long start = System.currentTimeMillis();
        String capkVersion = EMVL2.getInstance().getCapkCardParamsVersion();
        String contactVersion = EMVL2.getInstance().getContactIcCardParamsVersion();
        String contactlessVersion = EMVL2.getInstance().getContactlessIcCardParamsVersion();
        long end = System.currentTimeMillis();
        Log.e("WY", "get end-start=" + (end - start));
        if ("1.0.0".equals(capkVersion) || "1.0.0".equals(contactVersion) || "1.0.0".equals(contactlessVersion)) {
            return 2;
        }
        return 0;
    }

    /**
     * 登出设备服务
     */
    @Override
    public void logout() throws RemoteException {

    }

    /**
     * 获取基础系统信息
     *
     * @return 厂商硬件配置信息，返回值 KEY 定义见 DeviceInfoConstrants 类描述
     */
    @Override
    public Bundle getDevInfo() throws RemoteException {
        //TODO 需确认SERVICE_VER的命名规则
        Bundle bundle = new Bundle();
        bundle.putString(DeviceInfoConstrants.COMMOM_VENDOR, "信雅达");
        bundle.putString(DeviceInfoConstrants.COMMOM_MODEL, "I80");
        bundle.putString(DeviceInfoConstrants.COMMOM_OS_VER, "" + Build.VERSION.SDK_INT);
        try {
//            String sn = TerminalManager.getInstance().getDeviceInfo().getProduceSN();
            TerminalSerialNumberInfo terminalSerialNumberInfo = SerialNumberOperation.getInstance().queryTerminalSn(SerialNumberOperation.TERMINAL_MODEL_I80, (byte) 6);
            bundle.putString(DeviceInfoConstrants.COMMOM_SN, terminalSerialNumberInfo.getSN());
        } catch (Exception e) {
            e.printStackTrace();
        }
        bundle.putString(DeviceInfoConstrants.COMMON_SERVICE_VER, "1.0.0");
        return bundle;
    }

    /**
     * 获取蜂鸣器操作对象
     *
     * @return 成功 返回 返回蜂鸣器操作实例，失败 返回 Null
     */
    @Override
    public Beeper getBeeper() throws RemoteException {
        return BeeperStub.getInstance(mContext);
    }

    /**
     * 获取 LED 灯操作对象
     *
     * @return 成功 返回 LED 操作实例 失败 返回 Null
     */
    @Override
    public LEDDriver getLEDDriver() throws RemoteException {
        return LEDDriverStub.getInstance();
    }

    /**
     * 获取打印机操作对象
     *
     * @return 成功 返回打印机操作实例 失败 返回 Null
     */
    @Override
    public Printer getPrinter() throws RemoteException {
        return PrinterStub.getInstance();
    }

    /**
     * 获取内置扫描器操作对象
     *
     * @return 成功 返回扫描头操作实例 失败 返回 Null
     */
    @Override
    public InnerScanner getInnerScanner() throws RemoteException {
        return InnerScannerStub.getInstance(mContext);
    }

    /**
     * 获取 Modem 操作对象
     *
     * @return 成功 返回 Modem 操作实例 失败 返回 Null
     */
    @Override
    public ModemDriver getModemDriver() throws RemoteException {
        return new ModemDriverStub();
    }

    /**
     * 获取串口操作对象
     *
     * @return 成功 返回串口操作实例 失败 返回 Null
     */
    @Override
    public SerialPortDriver getSerialPortDriver(int portNo) throws RemoteException {
//        switch (portNo){
//            case 1:
//                break;
//            case 2:
//                break;
//            case 3:
//                break;
//            case 4:
//                break;
//            default:
//                break;
//
//        }
        if (portNo > 4){
            return null;
        }
        return SerialPortDriverStub.getInstance(mContext);
    }

    /**
     * 获取磁条读卡器操作对象
     *
     * @return 成功 返回磁条读卡器操作实例 失败 返回 Null
     */
    @Override
    public MagCardReader getMagCardReader() throws RemoteException {
        return MagCardReaderStub.getInstance();
    }

    /**
     * 获取芯片卡读卡器控制对象
     *
     * @param slotNo 卡槽号
     * @return 成功 返回芯片卡读卡器操作实例 失败 返回 Null
     */
    @Override
    public IccCardReader getIccCardReader(int slotNo) throws RemoteException {
        return IccCardReaderStub.getInstance(mContext);
    }

    /**
     * 获取芯片卡读卡器控制对象
     *
     * @param reader 芯片读卡器操作实例
     * @return 成功 返回 CPU 卡操作实例 失败 返回 Null
     */
    @Override
    public CPUCardHandler getCPUCardHandler(IccCardReader reader) throws RemoteException {
        return new CPUCardHandlerStub();
    }

    /**
     * 获取 M1 卡操作对象
     *
     * @param reader 芯片读卡器操作实例
     * @return 成功 返回 M1 卡操作实例 失败 返回 Null
     */
    @Override
    public M1CardHandler getM1CardHandler(IccCardReader reader) throws RemoteException {
        return new M1CardHandlerStub();
    }

    /**
     * 获取 PinPad 操作对象
     *
     * @return 成功 返回 PinPad 操作实例 失败 返回 Null
     */
    @Override
    public PinPad getPinPad() throws RemoteException {
        return PinPadStub.getInstance(mContext);
    }

    /**
     * 获取 EMV 流程操作对象
     *
     * @return 成功 返回 EMV 流程操作实例 失败 返回 Null
     */
    @Override
    public EmvHandler getEmvHandler() throws RemoteException {
        return EmvHandlerStub.getInstance(mContext);
    }

    /**
     * 获取网络模块操作对象
     *
     * @return 成功 返回网络模块操作实例 失败 返回 Null
     */
    @Override
    public NetWorkHandler getNetWorkHandler() throws RemoteException {
        return new NetWorkHandlerStub();
    }

    /**
     * 设置 Android 系统时间
     *
     * @param datetime YYYYMMDDHHMMSS
     */
    @Override
    public void setSystemClock(String datetime) throws RemoteException {
        try {
            TerminalManager.getInstance().setSystemTime(datetime);
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置软键盘开关
     *
     * @param status 状态 0-激活 1-关闭
     */
    @Override
    public void setIMEStatus(int status) throws RemoteException {
//        if (status == 0){
//            KeyboardUtils.showSoftInput(new EditText(mContext));
//        }
//        if (status == 1){
//            KeyboardUtils.hideSoftInput(mContext);
//        }
    }

    /**
     * 静默安装应用
     *
     * @param appFilePath     应用文件路径
     * @param runActivityName 安装完毕之后需要运行的 activity
     * @param appName         服务界面提示的应用名称(正在安装 xxxx 应 用,请耐心等待)
     */
    @Override
    public void installApp(final String appFilePath, final String runActivityName, final String appName) throws RemoteException {
        if (EmptyUtils.isEmpty(appFilePath) || EmptyUtils.isEmpty(appName)) {
            return;
        }
        //TODO 起对话框
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = AppUtils.installAppSilent(appFilePath);
                if (isSuccess) {
                    if (EmptyUtils.isNotEmpty(runActivityName))
                        AppUtils.launchApp(runActivityName);
                } else {
                    Log.v("installApp", "安装失败");
                }
            }
        }).start();
    }

    /**
     * 设备认证[仅适用于手机支付平台]
     *
     * @param authRandom 认证数据
     * @return KEY AUTHDATA - 加密认证数据 见 DeviceAuthConstrants 类描述
     */
    @Override
    public Bundle authDevice(String authRandom) throws RemoteException {
        //TODO 对authRandom以何种数据加密形式加密
        Bundle bundle = new Bundle();
        bundle.putString(DeviceAuthConstrants.AUTHDATA, "");
        return bundle;
    }

    /**
     * 获取钞箱操作对象
     *
     * @return 成功 返回 钞箱操作对象实例 失败 返回 Null
     * @throws RemoteException
     */
    @Override
    public CashBoxDriver getCashBoxDriver() throws RemoteException {
        return new CashBoxDriverStub();
    }

    /**
     * 获取行业卡操作对象
     *
     * @param reader 芯片读卡器操作实例
     * @return 成功 返回 行业卡操作对象实例 失败 返回 Null
     * @throws RemoteException
     */
    @Override
    public IndustryCardHandler getIndustryCardHandler(IccCardReader reader) throws RemoteException {
        return new IndustryCardHandlerStub();
    }

    /**
     * 静默卸载应用
     *
     * @param packageName 应用包名
     * @param listener    卸载应用监听器
     * @throws RemoteException
     */
    @Override
    public void uninstallApp(final String packageName, OnUninstallAppListener listener) throws RemoteException {
        if (EmptyUtils.isEmpty(packageName)) {
            return;
        }
        //TODO 起对话框
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSuccess = AppUtils.uninstallAppSilent(mContext, packageName, false);
                if (isSuccess) {
                    Log.v("uninstallApp", "卸载成功");
                } else {
                    Log.v("uninstallApp", "卸载失败");
                }
            }
        }).start();
    }
}















