//package com.ums.upos.uapi.device.pinpad;
//
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.TextView;
//
//import com.socsi.aidl.pinpadservice.OperationPinListener;
//import com.socsi.exception.PINPADException;
//import com.socsi.exception.SDKException;
//import com.socsi.smartposapi.ped.MACResult;
//import com.socsi.smartposapi.ped.Ped;
//import com.socsi.smartposapi.ped.PedVeriAuth;
//import com.socsi.utils.HexUtil;
//import com.socsi.utils.StringUtil;
//
//
///**
// * 惠尔丰pin认证
// * @author yanxz
// *
// */
//public class PinpadActivity extends BaseActivity implements OnClickListener {
//
//	public static final String TAG = "PinpadActivity";
//
//	private int timeout = 20*1000;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_pinpad);
//		init();
//
//		try {
//			PackageInfo pack = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
//			String[] permissions = pack.requestedPermissions;
//
//			for (String str: permissions) {
//				Log.i(TAG, "权限清单--->"+ str);
//			}
//		} catch (PackageManager.NameNotFoundException e) {
//			e.printStackTrace();
//		}
//		int ret = 0;
//		Log.d(TAG, "permission="+ ret);
//		ret = this.checkCallingPermission("android.permission.CLOUDPOS_PIN_MAC");
//		Log.d(TAG, "permission="+ ret);
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		switch (arg0.getId()) {
//			case R.id.pwd_btn1: // 打开密码键盘
//				open();
//				break;
//			case R.id.pwd_btn2: // 关闭密码键盘
//				close();
//				break;
//			case R.id.pwd_btn3: // 回调获得密文
//				open();
//				listenForPinBlockTest();
//				close();
//				break;
//			case R.id.pwd_btn4: // 回调获得明文
//				open();
//				listenForOfflinePin();
//				close();
//				break;
//			case R.id.pwd_btn20: // 阻塞获得密文
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						open();
//						waitForPinBlock();
//						close();
//					}
//				}).start();
//				break;
//			case R.id.pwd_btn21: // 阻塞获得明文
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						open();
//						waitForOfflinePin();
//						close();
//					}
//				}).start();
//				break;
//			case R.id.pwd_btn5: // 单独显示文本
//				open();
//				showText();
//				close();
//				break;
//			case R.id.pwd_btn22: // 显示文本后输入PIN
//				open();
//				showTextTest();
//				close();
//				break;
//			case R.id.pwd_btn6: // 清除文本
//				open();
//				clearTest();
//				close();
//				break;
//			case R.id.pwd_btn23://取消PIN
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						open();
//						cancelRequestPin();
//						close();
//					}
//				}).start();
//				break;
//			case R.id.pwd_btn7: // 设置pinlen
//				open();
//				setPINLength();
//				close();
//				break;
//			case R.id.pwd_btn8: {// 应用层密码键盘调试入口
//				Intent intent = new Intent(this, KeyBoardActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				this.startActivity(intent);
//				}break;
//			case R.id.pwd_btn9: { //
//				Intent intent = new Intent(this, SSLActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				this.startActivity(intent);
//			}break;
//			case R.id.pwd_btn15: // DES计算
////				desCalculate();
//				break;
//			case R.id.pwd_btn16: // MAC计算
//				calculateMac();
//				break;
//			case R.id.pwd_btn13: // 获取随机数
////				getRandom();
//				break;
//			case R.id.pwd_btn14: // 获取SN
////				getSN();
//				break;
//			case R.id.pwd_btn17: // 导入主密钥
//				byte[] oldMasterKey = new byte[]{
//						0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38,
//						0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38};
//				byte[] newMasterKey = new byte[]{
//						0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11,
//						0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11};
//				int ret = PedVeriAuth.getInstance().updateMasterKey(1, oldMasterKey, newMasterKey);
//				mTextView.append("导入主密钥：");
//				if(ret == 0)
//					mTextView.append("成功\n");
//				else
//					mTextView.append("失败\n");
//				break;
//			case R.id.clear:
//				mTextView.setText("");
//				break;
//			default:
//				break;
//		}
//	}
//
//
//	private void init(){
//		findViewById(R.id.pwd_btn1).setOnClickListener(this);
//		findViewById(R.id.pwd_btn2).setOnClickListener(this);
//		findViewById(R.id.pwd_btn3).setOnClickListener(this);
//		findViewById(R.id.pwd_btn4).setOnClickListener(this);
//		findViewById(R.id.pwd_btn5).setOnClickListener(this);
//		findViewById(R.id.pwd_btn6).setOnClickListener(this);
//		findViewById(R.id.pwd_btn7).setOnClickListener(this);
//		findViewById(R.id.pwd_btn8).setOnClickListener(this);
//		findViewById(R.id.pwd_btn9).setOnClickListener(this);
//		findViewById(R.id.pwd_btn11).setOnClickListener(this);
//		findViewById(R.id.pwd_btn12).setOnClickListener(this);
//		findViewById(R.id.pwd_btn13).setOnClickListener(this);
//		findViewById(R.id.pwd_btn14).setOnClickListener(this);
//		findViewById(R.id.pwd_btn15).setOnClickListener(this);
//		findViewById(R.id.pwd_btn16).setOnClickListener(this);
//		findViewById(R.id.pwd_btn17).setOnClickListener(this);
////		findViewById(R.id.pwd_btn18).setOnClickListener(this);
////		findViewById(R.id.pwd_btn19).setOnClickListener(this);
//		findViewById(R.id.pwd_btn20).setOnClickListener(this);
//		findViewById(R.id.pwd_btn21).setOnClickListener(this);
//		findViewById(R.id.pwd_btn22).setOnClickListener(this);
//		findViewById(R.id.pwd_btn23).setOnClickListener(this);
//		findViewById(R.id.clear).setOnClickListener(this);
//		mTextView = (TextView) findViewById(R.id.result);
//	}
//
//	//打开密码键盘
//	private int open(){
//		int res = PedVeriAuth.getInstance().open(this, 0);
//		String resString ="";
//		if(res==0){
//			resString ="打开成功";
//		}else{
//			resString ="打开失败:"+ res;
//		}
//		showMsg(resString);
//		return res;
//	}
//
//	//关闭密码键盘
//	private int close(){
//		int res = PedVeriAuth.getInstance().close();
//		String resString ="";
//		if(res==0){
//			resString ="关闭成功";
//		}else{
//			resString ="关闭失败:"+ res;
//		}
//		showMsg(resString);
//		return res;
//	}
//
//	//请求密文pin
//	private void listenForPinBlock() {
//		//联机pin
//		Bundle param = new Bundle();
//		param.putInt("keyID", 1);//索引
//		param.putString("pan", "1234567");
//		param.putString("promptString", "输入密码");
//		boolean voicePrompt = true;
//		try {
//			int res = PedVeriAuth.getInstance().listenForPinBlock(param, timeout,
//					voicePrompt, true, new OperationPinListener() {
//
//						@Override
//						public void onInput(int len, int key) {
//							Log.e(TAG, "onInput  len:"+len+"  key:"+key );
//						}
//
//						@Override
//						public void onError(int errorCode) {
//							Log.e(TAG, "onError   errorCode:"+errorCode);
//							showMsg("错误码：" + errorCode);
//						}
//
//						@Override
//						public void onConfirm(byte[] data, boolean isNonePin) {
//							Log.e(TAG, "onConfirm   data:"+HexUtil.toString(data)+"  isNonePin:"+isNonePin);
//							showMsg("密码：" + HexUtil.toString(data));
//						}
//
//						@Override
//						public void onCancel() {
//							Log.e(TAG, "onCancel");
//							showMsg("用户取消");
//						}
//					});
//			String resString ="";
//			if(res==0){
//				resString ="打开成功";
//			}else{
//				resString ="打开失败:"+ res;
//			}
//			showMsg(resString);
//		} catch (SDKException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	//回调脱机pin
//	private void listenForOfflinePin(){
//		try {
//			boolean voicePrompt = true;
//
//			int res = PedVeriAuth.getInstance().listenForOfflinePin(timeout,
//					voicePrompt, false, new OperationPinListener() {
//
//						@Override
//						public void onInput(int len, int key) {
//							Log.e(TAG, "onInput  len:"+len+"  key:"+key );
//						}
//
//						@Override
//						public void onError(int errorCode) {
//							Log.e(TAG, "onError   errorCode:"+errorCode);
//							showMsg("错误码：" + errorCode);
//						}
//
//						@Override
//						public void onConfirm(byte[] data, boolean isNonePin) {
//							Log.e(TAG, "onConfirm   data:"+HexUtil.toString(data)+"  isNonePin:"+isNonePin);
//							showMsg("密码：" + HexUtil.toString(PedVeriAuth.getInstance().getOffPinData()));
//						}
//
//						@Override
//						public void onCancel() {
//							Log.e(TAG, "onCancel");
//							showMsg("用户取消");
//						}
//					});
//			String resString ="";
//			if(res==0){
//				resString ="打开成功";
//			}else{
//				resString ="打开失败:"+ res;
//			}
//			showMsg(resString);
//		} catch (SDKException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	//请求密文pin
//	private void waitForPinBlock() {
//		//联机pin
//		Bundle param = new Bundle();
//		param.putInt("keyID", 1);//索引
//		param.putString("pan", "1234567890123456");
//		param.putString("lineIndex_2", "输入密码提示");
//		boolean voicePrompt = true;
//		byte[] pin = new byte[16];
//		try {
//			int res = PedVeriAuth.getInstance().waitForPinBlock(param, timeout,
//					voicePrompt, true, pin);
//			String resString ="";
//			if(res==0){
//				resString ="获取成功：\r\n"+ StringUtil.byte2HexStr(pin);
//			}else{
//				resString ="获取失败:"+ res;
//			}
//			showMsg(resString);
//		} catch (SDKException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	//回调脱机pin
//	private void waitForOfflinePin(){
//		try {
//			boolean voicePrompt = true;
//
//			int res = PedVeriAuth.getInstance().waitForOffPinBlock(timeout,
//					voicePrompt, false);
//			String resString ="";
//			if(res==0){
//				resString ="获取成功：\r\n"+
//						StringUtil.byte2HexStr(PedVeriAuth.getInstance().getOffPinData());
//			}else{
//				resString ="获取失败:"+ res;
//			}
//			showMsg(resString);
//		} catch (SDKException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 显示文本提示
//	 */
//	private void showText(){
//		String resString="";
//		int k = PedVeriAuth.getInstance().showText(1, "测试指定索引1", true);
//		resString+=k+"  \n";
//
//		k = PedVeriAuth.getInstance().showText(2, "测试指定索引2", false);
//		resString+=k+"  \n";
////
//		k = PedVeriAuth.getInstance().showText(3, "测试指定索引3", false);
//		resString+=k+"  \n";
//		showMsg(resString);
//	}
//
//	private void showTextTest(){
//		showText();
//		listenForPinBlock();
//	}
//
//	private void listenForPinBlockTest(){
//		listenForPinBlock();
//
//		showText();
//	}
//
//	/**
//	 * 清除文本内容
//	 */
//	private void clearTest(){
//
//		showText();
//
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				int res = PedVeriAuth.getInstance().clearText();
//				showMsg("清除结果:"+res);
//			}
//		}, 3000);
//	}
//
//	private void cancelRequestPin() {
//
//		mHandler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				int res = PedVeriAuth.getInstance().cancelRequestPin();
//				showMsg("取消结果:"+res);
//			}
//		}, 3000);
//
//		waitForPinBlock();
//	}
//
//	private int setPINLength(){
//
//		int res = PedVeriAuth.getInstance().setPINLength(4, 10);
//
//		listenForPinBlock();
//
//		showMsg("设置PIN长度(0～12):"+res);
//
//		return res;
//	}
//
//	private void loadMasterKey(){
//		String encrykey = "FEFEFEFEFEFEFEFEFEFEFEFEFEFEFEFE"; // 传输密钥明文    KCV:CAAAAF4DEAF1DBAE
//		String old = "01010101010101010101010101010101";
//		String tmk = "11111111111111112222222222222222"; // 主密钥明文
//		byte[] oldKey = StringUtil.hexStr2Bytes(old);
//		byte[] newKey = StringUtil.hexStr2Bytes(tmk);
//		int res = PedVeriAuth.getInstance().updateMasterKey(mKeyIndex, oldKey, newKey);
//		String resString ="";
//		if(res==0){
//			resString ="打开成功";
//		}else{
//			resString ="打开失败:"+ res;
//		}
//		showMsg(resString);
//	}
//
//	private void getSN(){
//
//	}
//
//	private void loadWorkKey(){
//
//	}
//
//	private void getRandom(){
//
//	}
//
//	private void calculateDes(){
//
//	}
//
//	private void calculateMac(){
//		Log.d(TAG, "calculateMac test");
//		MACResult result = new MACResult();
//		try {
//			String data = StringUtil.byte2HexStr(new byte[]{0x38, 0x38, 0x38, 0x38, 0x38,
//					0x38, 0x38, 0x38, 0x38, 0x38,
//					0x38, 0x38, 0x38, 0x38, 0x38});
//			Log.i(TAG, "data="+ data);
//			showMsg("加密数据："+ data);
//			int res = PedVeriAuth.getInstance().calculateMAC((byte)1,
//					Ped.MAC_CALCULATION_MODE_MAC_X99, data,
//                    "0000000000000000".getBytes(), Ped.DES_TYPE_3DES, result);
//			Log.i(TAG, "mac result="+ result.getMAC());
//			String resString ="";
//			if(res==0){
//				resString ="计算成功："+ result.getMAC();
//			}else{
//				resString ="计算失败:"+ res;
//			}
//			showMsg(resString);
//		} catch (SDKException e) {
//			e.printStackTrace();
//		} catch (PINPADException e) {
//			e.printStackTrace();
//		}
//	}
//
//	protected void showMsg(String msg) {
//		Message message = Message.obtain();
//		message.what = 0;
//		message.obj = msg;
//		mHandler.sendMessage(message);
//	}
//
//
//}
