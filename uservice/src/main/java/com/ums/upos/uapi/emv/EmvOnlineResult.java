package com.ums.upos.uapi.emv;

public class EmvOnlineResult {
	public final static int EMV_ONLINE_APPROVE = 0; //联机39域返回00
	public final static int EMV_ONLINE_FAIL = -1;	//联机39域返回非00
	public final static int EMV_ONLINE_DENIAL = -2;	//连接失败,联机失败 (Y3/Z3)
	public final static int EMV_ONLINE_ABORT = -3;	//交易终止，走冲正流程

	public final static String REJCODE = "rejCode";
	public final static String AUTHCODE = "authCode";
	public final static String RECVF55 = "recvField55";
}
