package com.wawa.arm.common;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.wawa.arm.R;
import com.wawa.arm.utile.AppException;
import com.wawa.arm.utile.StringUtils;
import com.wawa.arm.utile.log.LogThread;
import com.wawa.arm.utile.log.LogUtil;

public class OMApplication extends Application {
	private static final String TAG = OMApplication.class.getSimpleName();

	private static OMApplication omApp;
	
	private static final String PREFS_NAME = "com.wawa.arm";
	private static SharedPreferences sharePrefer;
	private static Editor sharePreferEd;
	
	private static Map<String,Object> configs = new HashMap<String,Object>();
	private static Map<String,Object> cacheVal = new HashMap<String,Object>();

	public static String NET_STATUS_ERROR;
	public static String TYPE_NETWORK_NOT_CONNECT;
	public static String TYPE_NETWORK_CONNECT_TIMEOUT;
	public static String TYPE_NETWORK_SOCKET_TIMEOUT;
	public static String TYPE_NETWORK_IO_EXCEPTION;
	public static String APP_ERROR;
	public static String TYPE_NETWORK_OPER_SUCCESS;
	public static String TYPE_NETWORK_OPER_FAILE;
	public static String TYPE_NETWORK_DIGEST_ERROR;
	public static String LOGIN_TIP_001;
	public static String LOGIN_TIP_002;
	public static String BALANCE_TIP_001;
	public static String BALANCE_TIP_002;
	public static String STATEMENT_TIP_002;
	public static String LOGOUT_TIP_001;
	
	private Thread logthread;
	
	public OMApplication() {
		super();
	}
	public static OMApplication getInstance() {
        return omApp;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		NET_STATUS_ERROR = getString(R.string.net_status_code_error);
		TYPE_NETWORK_NOT_CONNECT = getString(R.string.network_not_connected);
		TYPE_NETWORK_CONNECT_TIMEOUT = getString(R.string.net_exception_error);
		TYPE_NETWORK_SOCKET_TIMEOUT = getString(R.string.socket_exception_error);
		TYPE_NETWORK_IO_EXCEPTION = getString(R.string.io_exception_error);
		APP_ERROR = getString(R.string.app_error);
		TYPE_NETWORK_OPER_SUCCESS = getString(R.string.oper_success);
		TYPE_NETWORK_OPER_FAILE = getString(R.string.oper_fail);
		LOGIN_TIP_001 = getString(R.string.login_tip_001);
		LOGIN_TIP_002 = getString(R.string.login_tip_002);
		BALANCE_TIP_001 = getString(R.string.balance_money_tip_001);
		BALANCE_TIP_002 = getString(R.string.balance_money_tip_002);
		STATEMENT_TIP_002 = getString(R.string.statement_tip_002);
		LOGOUT_TIP_001 = getString(R.string.logout_tip_001);
		TYPE_NETWORK_DIGEST_ERROR = getString(R.string.service_response_data_error);
		
		omApp = this;//赋值application
		logthread =  new LogThread();
		logthread.setName("-Log Thread For App--");
		logthread.start();//启动logcat日志打印到file线程
		
		//OMApplication.getInstance().initPropertiesFromFile(omApp);//从配置文件初始化配置信息--要不要拿到startActivity里加载？
		initPropertiesFromFile(omApp);
		//注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}


	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getVal(CommonConsts.CONF_APP_UNIQUEID, "");
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setVal(CommonConsts.CONF_APP_UNIQUEID, uniqueID,true);
		}
		return uniqueID;
	}
	
	private void changeAppLanguage(){
		Resources resources = getResources();//获得res资源对象
		Configuration config = resources.getConfiguration();//获得设置对象
		DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
		resources.updateConfiguration(config, dm);
	}
	public String getTradeStateSuccessTip(int type) {
		String tip = "";
		switch (type) {
		case 1:
			tip = getString(R.string.send_money_state_success_tip001);
			break;
		case 2:
			tip = getString(R.string.buy_airtime_state_success_tip001);
			break;
		case 3:
			tip = getString(R.string.agent_withdraw_money_state_success_tip001);
			break;
		case 4:
			tip = getString(R.string.atm_withdraw_money_state_success_tip001);
			break;
		case 5:
			tip = getString(R.string.pay_bill_state_success_tip001);
			break;
		case 6:
			tip = getString(R.string.buy_goods_state_success_tip001);
			break;
		case 7://登录密码修改
			tip = getString(R.string.loginpwd_change_state_success_tip001);
			break;
		case 8://支付密码修改
			tip = getString(R.string.pin_change_state_success_tip001);
			break;
		default:
			break;
		}
		return tip;
	}
	public String getTradePINTitle(int type) {
		String tip = "";
		switch (type) {
		case 1:
			tip = getString(R.string.send_money);
			break;
		case 2:
			tip = getString(R.string.buy_airtime);
			break;
		case 3:
			tip = getString(R.string.withdraw_money_agent_btn_tip);
			break;
		case 4:
			tip = getString(R.string.withdraw_money_atm_btn_tip);
			break;
		case 5:
			tip = getString(R.string.pay_bill_title);
			break;
		case 6:
			tip = getString(R.string.buy_goods_title);
			break;
		default:
			break;
		}
		return tip;
	}
	
	public void initPropertiesFromFile(Context context){
		Properties props = new Properties();
        InputStream in = null;
        try {
            in = context.getAssets().open(CommonConsts.PROPERTIES_NAME);
            props.load(in);
            /*String httpsUrl = props.getProperty(CommonConsts.HTTPS_BASE);
            configs.put(CommonConsts.HTTPS_BASE, httpsUrl);
            String requestScheme = props.getProperty(CommonConsts.REQUEST_SCHEME);
            configs.put(CommonConsts.REQUEST_SCHEME, requestScheme);
            Integer requestPort = Integer.valueOf(props.getProperty(CommonConsts.REQUEST_PORT));
            configs.put(CommonConsts.REQUEST_PORT, requestPort);
            Boolean needproxy = Boolean.valueOf(props.getProperty(CommonConsts.NEED_PROXY));
            configs.put(CommonConsts.NEED_PROXY, needproxy);
            String proxyUrl = props.getProperty(CommonConsts.PROXY_URL);
            configs.put(CommonConsts.PROXY_URL, proxyUrl);
            Integer proxyPort = Integer.valueOf(props.getProperty(CommonConsts.PROXY_PORT));
            configs.put(CommonConsts.PROXY_PORT, proxyPort);
            String proxyScheme = props.getProperty(CommonConsts.PROXY_SCHEME);
            configs.put(CommonConsts.PROXY_SCHEME, proxyScheme);
            int pageSize = Integer.valueOf(props.getProperty(CommonConsts.QUERY_PAGE_SIZE));
            configs.put(CommonConsts.QUERY_PAGE_SIZE, pageSize);
            String digestCode = props.getProperty(CommonConsts.DIGEST_CODE);
            configs.put(CommonConsts.DIGEST_CODE, digestCode);*/
            
            int armpl = Integer.valueOf(props.getProperty(CommonConsts.ARM_PL));
            configs.put(CommonConsts.ARM_PL, armpl+"");
            int armrm = Integer.valueOf(props.getProperty(CommonConsts.ARM_RM));
            configs.put(CommonConsts.ARM_RM, armrm+"");
            int armsx = Integer.valueOf(props.getProperty(CommonConsts.ARM_SX));
            configs.put(CommonConsts.ARM_SX, armsx+"");
            int armzq = Integer.valueOf(props.getProperty(CommonConsts.ARM_ZQ));
            configs.put(CommonConsts.ARM_ZQ, armzq+"");
            int apptimeout = Integer.valueOf(props.getProperty(CommonConsts.APP_TIMEOUT));
            configs.put(CommonConsts.APP_TIMEOUT, apptimeout+"");
            String armpairname = props.getProperty(CommonConsts.ARM_PAIR_NAME);
            configs.put(CommonConsts.ARM_PAIR_NAME, armpairname+"");
            String armpairkey = props.getProperty(CommonConsts.ARM_PAIR_KEY);
            configs.put(CommonConsts.ARM_PAIR_KEY, armpairkey+"");
            if (context != null){
            	sharePrefer = context.getSharedPreferences(PREFS_NAME, 0);
            	sharePreferEd = sharePrefer.edit();
            	setVal(CommonConsts.LAST_LOGIN_NAME, sharePrefer.getString(CommonConsts.LAST_LOGIN_NAME, ""), true);
            	setVal(CommonConsts.ARM_PL, sharePrefer.getString(CommonConsts.ARM_PL, armpl+""), true);
            	setVal(CommonConsts.ARM_RM, sharePrefer.getString(CommonConsts.ARM_RM, armrm+""), true);
            	setVal(CommonConsts.ARM_SX, sharePrefer.getString(CommonConsts.ARM_SX, armsx+""), true);
            	setVal(CommonConsts.ARM_ZQ, sharePrefer.getString(CommonConsts.ARM_ZQ, armzq+""), true);
            	setVal(CommonConsts.APP_TIMEOUT, sharePrefer.getString(CommonConsts.APP_TIMEOUT, apptimeout+""), true);
            	setVal(CommonConsts.ARM_PAIR_NAME, sharePrefer.getString(CommonConsts.ARM_PAIR_NAME, armpairname+""), true);
            	setVal(CommonConsts.ARM_PAIR_KEY, sharePrefer.getString(CommonConsts.ARM_PAIR_KEY, armpairkey+""), true);
            	setVal(CommonConsts.APP_VOL_OPEN_STATU, sharePrefer.getString(CommonConsts.APP_VOL_OPEN_STATU, "0"), true);
            	setVal(CommonConsts.APP_VOL_VAL, sharePrefer.getString(CommonConsts.APP_VOL_VAL, "4"), true);
            }
        }catch(Exception e){
        	LogUtil.e(TAG, "get properties error", e);
        }
	}
	
	/**
	 * 描述：
	 * 存储数据到本地
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年6月10日 下午5:04:13
	 * @param key
	 * @param val
	 */
	public void setValToSharePrefer(String key,String val){
		if(sharePreferEd != null){
			sharePreferEd.putString(key, val);
			sharePreferEd.commit();
		}
		cacheVal.put(key, val);
		configs.put(key, val);
	}
	/**
	 * 描述：
	 *
	 * @author W.Y
	 * @version 1.0
	 * @created 2014年5月23日 下午7:59:40
	 * @param key  
	 * @param val
	 * @param isCacheVal 是否缓存
	 */
	public void setVal(String key,Object val,boolean isCacheVal){
		if(isCacheVal){
			cacheVal.put(key, val);
		}else{
			configs.put(key, val);
		}

	}
	
	@SuppressWarnings("unchecked")
	public <T>  T getVal(String key,T defaultVal){
		T result = (T) cacheVal.get(key);
		if(result == null){
			result = (T) configs.get(key);
			if(result == null){
				if(key.equals(CommonConsts.LAST_LOGIN_NAME)){
					result = (T) sharePrefer.getString(CommonConsts.LAST_LOGIN_NAME, (String) defaultVal);
				}else{
					result = defaultVal;
				}
			}
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public <T>  T getValNew(String key,T defaultVal){
		T result = (T) cacheVal.get(key);
		if(result == null){
			result = (T) configs.get(key);
			if(result == null){
				result = defaultVal;
			}
		}
		return result;
	}
	
	public void logoutClearCacheData(){
		cacheVal.clear();
	}
}
