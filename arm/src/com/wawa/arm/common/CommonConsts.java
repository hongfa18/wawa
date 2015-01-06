package com.wawa.arm.common;


/**
 * 描述：全局静态变量
 *
 * @author W.Y
 * @version 1.0
 * @created 2014年5月20日 下午3:14:38
 */
public class CommonConsts {
	/**配置文件*/
	public static final String PROPERTIES_NAME = "om.properties";
	
	/**配置文件key-https请求url基本地址*/
	public static final String HTTPS_BASE = "https_base";
	/**配置文件key-请求方式http/https*/
	public static final String REQUEST_SCHEME = "request_scheme";
	/**配置文件key-请求端口 默认http8 https433*/
	public static final String REQUEST_PORT = "request_port";
	/**配置文件key-是否需要代理*/
	public static final String NEED_PROXY = "needProxy";
	/**配置文件key-代理地址*/
	public static final String PROXY_URL = "proxy_url";
	/**配置文件key-代理端口*/
	public static final String PROXY_PORT = "proxy_port";
	/**配置文件key-代理类型*/
	public static final String PROXY_SCHEME = "proxy_scheme";
	/**配置文件key-每页显示条数*/
	public static final String QUERY_PAGE_SIZE = "query_page_size";

	/**https服务器证书名称*/
	public static final String SSL_CER_NAME = "ca.crt";

	/**https请求类型*/
	public static final String HTTPS_SCHEME = "https";
	/**http请求类型*/
	public static final String HTTP_SCHEME = "http";
	/**服务器的校验码*/
	public static final String DIGEST_CODE = "digest_key_code";

	/**登陆请求*/
	public static final String REQUEST_URI_LOGIN = "/login";
	/**登出请求*/
	public static final String REQUEST_URI_LOGOUT = "/logout";
	/**获取参数请求*/
	public static final String REQUEST_URI_APP_PARAM = "/getAppParam";
	/**验证码获取请求*/
	public static final String REQUEST_URI_SMSCODE = "/sendRandomCode";
	/**余额查询请求*/
	public static final String REQUEST_URI_BANLANCE = "/accBalQuery";
	/**修改登录密码请求*/
	public static final String REQUEST_URI_CHANGE_LOGIN_PWD = "/changeLoginPwd";
	/**修改支付密码请求*/
	public static final String REQUEST_URI_CHANGE_PIN = "/changePayPwd";
	/**进三条交易记录请求*/
	public static final String REQUEST_URI_MINI_STATEMENT = "/accMiniState";
	/**一年内交易记录请求*/
	public static final String REQUEST_URI_YEAR_STATEMENT = "/accQueryState";
	/**转账到别的手机*/
	public static final String REQUEST_URI_CHANGE_MONEY_SEND = "/cSendMoney";
	/**空中充值*/
	public static final String REQUEST_URI_CHANGE_MONEY_BUY_AIRTIME = "/cBuyAirTime";
	/**代理商取款*/
	public static final String REQUEST_URI_CHANGE_MONEY_WITHDRAW_AGENT = "/cAgentWithdraw";
	/**缴费*/
	public static final String REQUEST_URI_CHANGE_MONEY_BILL = "/cPayBill";
	/**ATM无卡取款*/
	public static final String REQUEST_URI_CHANGE_MONEY_WITHDRAW_ATM = "/cATMWithdraw";
	/**商家商品付款*/
	public static final String REQUEST_URI_CHANGE_MONEY_GOODS = "/cBuyGoods";

	public static final String COOKIE = "cookie";
	public static final String CONF_APP_UNIQUEID = "app_uniue_id";
	public static final String LOGIN_USER = "login_user";
	public static final String APP_PARAM_MAP = "app_param_map";
	public static final String LANGUAGE = "language";
	
	public static final String ACCOUNT_CHANGE_PWD_FALG = "account_change_pwd_flag";
	public static final String ACCOUNT_BALANCE_FALG = "account_balance_flag";
	public static final String QUERY_TRADE_RECORD = "query_trade_record";
	public static final String MONYE_CHANGE = "money_change";
	public static final String LAST_LOGIN_NAME = "last_login_name";


	public static final int REQUEST_START = 0x01;
	public static final int REQUEST_SUCCESS = 0x02;
	public static final int REQUEST_ERROR = 0x03;
	public static final int REQUEST_SESSION_VALIDATE = 0x04;
	
	
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	public static final String ARM_PL = "arm_pl";

	public static final String ARM_RM = "arm_rm";

	public static final String ARM_SX = "arm_sx";

	public static final String ARM_ZQ = "arm_zq";

	public static final String APP_TIMEOUT = "app_timeout";

	public static final String ARM_NO = "about_arm_no";

	public static final String ARM_VERSION = "about_arm_version";

	public static final String ARM_NAME = "about_arm_name";

	public static final String ARM_ADDR = "about_arm_addr";

	public static final String APP_SN = "app_sn";

	public static final String APP_VER = "app_ver";

	public static final int STATU_SETTING = 1;
	public static final int STATU_HISTORY = 2;
	public static final int STATU_HISTORY_ONE = 21;
	public static final int STATU_HISTORY_MORE = 22;
	public static final int STATU_WORKING = 3;
	public static final int STATU_PAUSE = 4;

	/**监测设备指令下发次数*/
	public static final int STOP_COUNT = 2;

	public static final int NO_PAIR_DEVICE = 1;
	public static final int PAIR_DEVICE = 2;

	public static final String DEFAULT_PERM_NAME = "ZZ01-BK3231-";
	public static final String DEFAULT_PERM_KEY = "1129";

	public static final String ARM_PAIR_NAME = "arm_pair_name";
	public static final String ARM_PAIR_KEY = "arm_pair_key";

	public static final String SOUND_NOMOR = "nomer_du";

	public static final String SOUND_ALARM = "alarm_du";

	public static final String APP_VOL_OPEN_STATU = "app_vol_isopen_statu";

	public static final String APP_VOL_VAL = "app_vol_val";

}
