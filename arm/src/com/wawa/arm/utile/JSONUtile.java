package com.wawa.arm.utile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.AppParamMap;
import com.wawa.arm.entity.LanguageType;
import com.wawa.arm.entity.QueryDataObject;
import com.wawa.arm.entity.TradeRecord;
import com.wawa.arm.entity.User;
import com.wawa.arm.utile.log.LogUtil;

public class JSONUtile {
	
	/**
	 * 描述：
	 * 登录获取返回结果
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年5月23日 下午7:45:12
	 * @param jsonStr
	 * @return
	 */
	public static User parseUserInfoToMomery(String jsonStr){
		User user = null;
		try {
			user = OMApplication.getInstance().getVal(CommonConsts.LOGIN_USER, new User());
			JSONObject js = new JSONObject(jsonStr);
			JSONObject data = js.getJSONObject("data");
			int id = data.getInt("userId");
			String userName = data.getString("userName");
			String sessionId = data.getString("sessionId");
			int language = data.getInt("language");
			int pinState = data.getInt("payPassFlag");
			user.setPinState(pinState);
			int loginPwdState = data.getInt("loginPassFlag");
			user.setLoginPwdState(loginPwdState);
			
			if(id != 0)
				user.setUserId(id);
			if(!StringUtils.isEmpty(userName))
				user.setUesrName(userName);
			if(!StringUtils.isEmpty(sessionId))
				OMApplication.getInstance().setVal(CommonConsts.COOKIE, sessionId,true);
			if(language != 0)
				OMApplication.getInstance().setVal(CommonConsts.LANGUAGE, language,true);
			OMApplication.getInstance().setVal(CommonConsts.LOGIN_USER, user,true);
		} catch (JSONException e) {
			LogUtil.e(e);
			user = null;
		}
		return user;
	}

	/**
	 * 描述：
	 * 服务器APP参数解析
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年5月23日 下午7:28:21
	 * @param resultJsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static AppParamMap parseParamMapToMomery(String resultJsonStr) {
		AppParamMap appParam = null;
		try {
			JSONObject js = new JSONObject(resultJsonStr);
			JSONObject data1 = js.getJSONObject("data");
			appParam = OMApplication.getInstance().getVal(CommonConsts.APP_PARAM_MAP, new AppParamMap());
			JSONObject data2 = data1.getJSONObject("paramMap");
			JSONObject tradeTypes = data2.getJSONObject("tradeType");
			Map<String,String> tradTypeObj = new HashMap<String,String>();
			Iterator<String> it = tradeTypes.keys();  
        	while(it.hasNext()){//遍历JSONObject  
        		String key = it.next();  
        		String val = (String) tradeTypes.get(key);
        		//TradType trad = new TradType(key, val);
        		tradTypeObj.put(key, val);
        	}  
        	//tradTypeObj.put("0","Add");
        	JSONObject languages = data2.getJSONObject("language");
			List<LanguageType> lanObjs = new ArrayList<LanguageType>();
			Iterator<String> it2 = languages.keys();  
        	while(it2.hasNext()){//遍历JSONObject  
        		String key = it2.next();  
        		String val = (String) languages.get(key);
        		LanguageType trad = new LanguageType(key, val);
        		lanObjs.add(trad);
        	}  
        	
			appParam.setTradTypes(tradTypeObj);
			appParam.setLanguageTypes(lanObjs);
			OMApplication.getInstance().setVal(CommonConsts.APP_PARAM_MAP,appParam,true);
		} catch (JSONException e) {
			LogUtil.e(e);
			appParam = null;
		}
		return appParam;
	}

	/**
	 * 描述：
	 * 余额解析
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年5月26日 下午1:28:58
	 * @param resultJsonStr
	 * @return
	 */
	public static String parseBalanceToMomery(String resultJsonStr) {
		String cashBalance = null;
		try {
			JSONObject js = new JSONObject(resultJsonStr);
			JSONObject data = js.getJSONObject("data");
			cashBalance = data.getString("cashBalance");
			User user = OMApplication.getInstance().getVal(CommonConsts.LOGIN_USER,null);
			user.setCashBalance(cashBalance);
			OMApplication.getInstance().setVal(CommonConsts.LOGIN_USER,user,true);
		} catch (JSONException e) {
			LogUtil.e(e);
			cashBalance = null;
		}
		return cashBalance;
	}

	public static QueryDataObject<TradeRecord> parseQueryMiniTradeToMomery(String resultJsonStr) {
		QueryDataObject<TradeRecord> query = OMApplication.getInstance().getVal(CommonConsts.QUERY_TRADE_RECORD,null);;
		try {
			if(query != null){
				query.getDatas().clear();
				query = null;
			}
			JSONObject js = new JSONObject(resultJsonStr);
			JSONObject data = js.getJSONObject("data");
			query = new QueryDataObject<TradeRecord>();
			String rowCount = data.getString("rowCount");
			query.setRowCount(rowCount);
			
			JSONArray results = data.getJSONArray("result");
			List<TradeRecord> records = new ArrayList<TradeRecord>(3);
			for (int i = 0; i < results.length(); i++) {
				JSONArray result = results.getJSONArray(i);
				TradeRecord trade = new TradeRecord();
				for (int j = 0; j < result.length(); j++) {
					setValue(trade,result.get(j),j);
				}
				/*String tradeFlowNo = result.getString("tradeFlowNo");
				trade.setTradeFlowNo(tradeFlowNo);*/
				/*String tradeTime = result.getString("tradeTime");
				trade.setTradeTime(tradeTime);
				String amount = result.getString("amount");
				trade.setAmount(amount);
				String tradeType = result.getString("tradeType");
				trade.setTradeType(tradeType);
				String opType = result.getString("opType");
				trade.setOpType(opType);
				String currency = result.getString("currency");
				trade.setCurrency(currency);
				String dstInfo = result.getString("dstInfo");
				trade.setDstInfo(dstInfo);
				if(result.has("th3Info")){
					String th3Info = result.getString("th3Info");
					trade.setTh3Info(th3Info);
				}*/
				records.add(trade);
			}
			query.setDatas(records);
		} catch (JSONException e) {
			LogUtil.e(e);
			query = null;
		}
		OMApplication.getInstance().setVal(CommonConsts.QUERY_TRADE_RECORD,query,true);
		return query;
	}
	public static QueryDataObject<TradeRecord> parseQueryYearTradeToMomery(String resultJsonStr,Object... val) {
		QueryDataObject<TradeRecord> query = OMApplication.getInstance().getVal(CommonConsts.QUERY_TRADE_RECORD,null);;
		try {
			if(query != null){
				query.getDatas().clear();
				query = null;
			}
			JSONObject js = new JSONObject(resultJsonStr);
			JSONObject data = js.getJSONObject("data");
			query = new QueryDataObject<TradeRecord>();
			String currentPage = data.getString("currentPage");
			query.setCurrentPage(currentPage);
			String nextPage = data.getString("nextPage");
			query.setNextPage(nextPage);
			String pageSize = data.getString("pageSize");
			query.setPageSize(pageSize);
			String pages = data.getString("pages");
			query.setPages(pages);
			String previousPage = data.getString("previousPage");
			query.setPreviousPage(previousPage);
			String rowCount = data.getString("rowCount");
			query.setRowCount(rowCount);
			
			JSONArray results = data.getJSONArray("result");
			int size = OMApplication.getInstance().getVal(CommonConsts.QUERY_PAGE_SIZE, 30);
			List<TradeRecord> records = new ArrayList<TradeRecord>(size);
			for (int i = 0; i < results.length(); i++) {
				JSONArray result = results.getJSONArray(i);
				TradeRecord trade = new TradeRecord();
				for (int j = 0; j < result.length(); j++) {
					setValue(trade,result.get(j),j);
				}
				records.add(trade);
			}
			query.setDatas(records);
			query.setStartTime((String)val[0]);
			query.setEndTime((String)val[1]);
		} catch (JSONException e) {
			LogUtil.e(e);
			query = null;
		}
		OMApplication.getInstance().setVal(CommonConsts.QUERY_TRADE_RECORD,query,true);
		return query;
	}
	
	private static void setValue(TradeRecord trade,Object obj,int j){
		String value = String.valueOf(obj);
		switch (j) {
		case 0:
			//String tradeFlowNo = result.getString("tradeFlowNo");
			trade.setTradeFlowNo(value);
			break;
		case 1:
			//String tradeTime = result.getString("tradeTime");
			trade.setTradeTime(value);
			break;
		case 2:
			//String amount = result.getString("amount");
			trade.setAmount(value);
			break;
		case 3:
			//String tradeType = result.getString("tradeType");
			trade.setTradeType(value);
			break;
		case 4:
			//String opType = result.getString("opType");
			trade.setOpType(value);
			break;
		case 5:
			//String currency = result.getString("currency");
			trade.setCurrency(value);
			break;
		case 6:
			//String dstInfo = result.getString("dstInfo");
			trade.setDstInfo(value);
			break;
		case 7:
			//String th3Info = result.getString("th3Info");
			trade.setTh3Info(value);
			break;
		default:
			break;
		}
	}
}
