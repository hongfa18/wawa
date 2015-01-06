package com.wawa.arm.service;

import java.net.URI;

import org.apache.http.client.utils.URIUtils;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.MoneyChange;
import com.wawa.arm.entity.NetResult;
import com.wawa.arm.net.CustomerHttpClient;
import com.wawa.arm.utile.PropertiesUtile;


/**
 * ������
 * TRS:���׹���
 * �����û�ת�ˡ��Ӵ�����ȡǮ����ATMȡǮ���ֻ���ֵ���ɷѡ���Ʒ֧��
 * @author W.Y
 * @version 1.0
 * @created 2014��5��23�� ����6:53:25
 */
public class TRSService  extends BaseService{
	private static final String TAG = TRSService.class.getSimpleName();
	
	public TRSService(Handler coomonHandler) {
		super(coomonHandler);
	}

	public void moneyChange(Handler handler, Object... val) {
		try {
			start(handler);
			MoneyChange moneyChange = OMApplication.getInstance().getVal(CommonConsts.MONYE_CHANGE, null);
			if(moneyChange == null)
				throw new Exception();
			String requestType = "";
			JSONObject jb = new JSONObject();
			jb.put("randomCode", val[0]);
			jb.put("mpin", val[1]);
			int type = moneyChange.getChangeType();
			if(type != 4)//�����ATM�޿�ȡ�û�н���ֶ�
				jb.put("amount", moneyChange.getAmount());
			switch (type) {
			case 1:
				requestType = CommonConsts.REQUEST_URI_CHANGE_MONEY_SEND;
				jb.put("phoneNo", moneyChange.getInputOne());
				break;
			case 2:
				requestType = CommonConsts.REQUEST_URI_CHANGE_MONEY_BUY_AIRTIME;
				jb.put("phoneNo", moneyChange.getInputOne());
				break;
			case 3:
				requestType = CommonConsts.REQUEST_URI_CHANGE_MONEY_WITHDRAW_AGENT;
				jb.put("agentCode", moneyChange.getInputOne());
				break;
			case 4:
				requestType = CommonConsts.REQUEST_URI_CHANGE_MONEY_WITHDRAW_ATM;
				//TODO ��ȷ��
				break;
			case 5:
				requestType = CommonConsts.REQUEST_URI_CHANGE_MONEY_BILL;
				jb.put("businessNumber", moneyChange.getInputOne());
				jb.put("billRef", moneyChange.getBillRef());
				break;
			case 6:
				requestType = CommonConsts.REQUEST_URI_CHANGE_MONEY_GOODS;
				jb.put("purchaseCode", moneyChange.getInputOne());
				break;
			default:
				throw new Exception();
			}
			URI uri = URIUtils.createURI(requestScheme,baseUrl , port, requestType,null, null);
			NetResult result = CustomerHttpClient.put(uri,jb,8);
			if(result.isSuccess()){
				//TODO ��ʱ����Ҫ�����ķ�������
				end(true, OMApplication.getInstance().getTradeStateSuccessTip(type), handler,null);
			}else{
				end(false, result.getErrorTipStr(), handler,null,result.getErrorCode());
			}
		}catch (Exception e) {
       	 	 Log.e(TAG, "",e);
       	 	end(false, OMApplication.APP_ERROR, handler,null);
        }
	}

}
