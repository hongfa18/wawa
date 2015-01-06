package com.wawa.arm.common;


/**
 * ������ȫ�־�̬����
 *
 * @author W.Y
 * @version 1.0
 * @created 2014��5��20�� ����3:14:38
 */
public class CommonConsts {
	/**�����ļ�*/
	public static final String PROPERTIES_NAME = "om.properties";
	
	/**�����ļ�key-https����url������ַ*/
	public static final String HTTPS_BASE = "https_base";
	/**�����ļ�key-����ʽhttp/https*/
	public static final String REQUEST_SCHEME = "request_scheme";
	/**�����ļ�key-����˿� Ĭ��http8 https433*/
	public static final String REQUEST_PORT = "request_port";
	/**�����ļ�key-�Ƿ���Ҫ����*/
	public static final String NEED_PROXY = "needProxy";
	/**�����ļ�key-�����ַ*/
	public static final String PROXY_URL = "proxy_url";
	/**�����ļ�key-����˿�*/
	public static final String PROXY_PORT = "proxy_port";
	/**�����ļ�key-��������*/
	public static final String PROXY_SCHEME = "proxy_scheme";
	/**�����ļ�key-ÿҳ��ʾ����*/
	public static final String QUERY_PAGE_SIZE = "query_page_size";

	/**https������֤������*/
	public static final String SSL_CER_NAME = "ca.crt";

	/**https��������*/
	public static final String HTTPS_SCHEME = "https";
	/**http��������*/
	public static final String HTTP_SCHEME = "http";
	/**��������У����*/
	public static final String DIGEST_CODE = "digest_key_code";

	/**��½����*/
	public static final String REQUEST_URI_LOGIN = "/login";
	/**�ǳ�����*/
	public static final String REQUEST_URI_LOGOUT = "/logout";
	/**��ȡ��������*/
	public static final String REQUEST_URI_APP_PARAM = "/getAppParam";
	/**��֤���ȡ����*/
	public static final String REQUEST_URI_SMSCODE = "/sendRandomCode";
	/**����ѯ����*/
	public static final String REQUEST_URI_BANLANCE = "/accBalQuery";
	/**�޸ĵ�¼��������*/
	public static final String REQUEST_URI_CHANGE_LOGIN_PWD = "/changeLoginPwd";
	/**�޸�֧����������*/
	public static final String REQUEST_URI_CHANGE_PIN = "/changePayPwd";
	/**���������׼�¼����*/
	public static final String REQUEST_URI_MINI_STATEMENT = "/accMiniState";
	/**һ���ڽ��׼�¼����*/
	public static final String REQUEST_URI_YEAR_STATEMENT = "/accQueryState";
	/**ת�˵�����ֻ�*/
	public static final String REQUEST_URI_CHANGE_MONEY_SEND = "/cSendMoney";
	/**���г�ֵ*/
	public static final String REQUEST_URI_CHANGE_MONEY_BUY_AIRTIME = "/cBuyAirTime";
	/**������ȡ��*/
	public static final String REQUEST_URI_CHANGE_MONEY_WITHDRAW_AGENT = "/cAgentWithdraw";
	/**�ɷ�*/
	public static final String REQUEST_URI_CHANGE_MONEY_BILL = "/cPayBill";
	/**ATM�޿�ȡ��*/
	public static final String REQUEST_URI_CHANGE_MONEY_WITHDRAW_ATM = "/cATMWithdraw";
	/**�̼���Ʒ����*/
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

	/**����豸ָ���·�����*/
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
