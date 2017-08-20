package com.ums.upos.uapi.emv;

import android.content.Context;

import com.socsi.smartposapi.emv2.EmvL2;
import com.socsi.utils.TlvUtil;

import java.util.HashMap;
import java.util.Map;

public class Utility {
	public static byte[] getTlvData(Context context, String packageName, String tlvString) {
		try {
			Map<String, String> mMap = new HashMap<String, String>();
			mMap.put("DF35", tlvString);
//			return EMVL2.getInstance().getTLVData(TlvUtil.mapToTlv(mMap));
			return EmvL2.getInstance(context, packageName).getTLVData(TlvUtil.mapToTlv(mMap));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
