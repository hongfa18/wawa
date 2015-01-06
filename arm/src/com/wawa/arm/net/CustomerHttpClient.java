package com.wawa.arm.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.entity.NetResult;
import com.wawa.arm.entity.User;
import com.wawa.arm.utile.DigestUtil;
import com.wawa.arm.utile.MobileUtils;
import com.wawa.arm.utile.PropertiesUtile;
import com.wawa.arm.utile.StringUtils;
import com.wawa.arm.utile.log.LogUtil;

public class CustomerHttpClient {
	private static final String TAG = "CustomerHttpClient";
	
	private static final String CHARSET = HTTP.UTF_8;
	private static HttpClient customerHttpClient;
	private static String appCookie;
	private static String appUserAgent;
	
	private CustomerHttpClient() {
	}    
	
	public static void cleanCookie() {
		appCookie = "";
	}
	private static String getCookie() {
		if(appCookie == null || appCookie == "") {
			appCookie = OMApplication.getInstance().getVal(CommonConsts.COOKIE, "");
		}
		return appCookie;
	}
	
	private static String getUserAgent() {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OrangeMoneyApp");
			ua.append('/'+OMApplication.getInstance().getPackageInfo().versionName+'_'+OMApplication.getInstance().getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+OMApplication.getInstance().getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	/*private static HttpGet getHttpGet(String url, String cookie, String userAgent) {
		HttpGet httpGet = new HttpGet(url);
		// 设置 请求超时时间
		httpGet.setHeader("Connection","Keep-Alive");
		httpGet.setHeader("Cookie", cookie);
		httpGet.setHeader("User-Agent", userAgent);
		return httpGet;
	}*/
	
	/*private static HttpPost getHttpPost(String url, String cookie, String userAgent) {
		HttpPost httpPost = new HttpPost(url);
		// 设置 请求超时时间
		httpPost.setHeader("Connection","Keep-Alive");
		httpPost.setHeader("Cookie", cookie);
		httpPost.setHeader("User-Agent", userAgent);
		return httpPost;
	}*/
	private static HttpPut getHttpPut(URI url, String cookie, String userAgent) {
		HttpPut httpPut = new HttpPut(url);
		// 设置 请求超时时间
		httpPut.setHeader("Accept", "application/json");
		httpPut.setHeader("Accept-Language", Locale.getDefault().toString());
		//httpPut.setHeader("Content-Length", url.toString().length()+"");
		httpPut.setHeader("Connection","Keep-Alive");
		httpPut.setHeader("Cookie", "JSESSIONID="+cookie);
		httpPut.setHeader("User-Agent", userAgent);
		return httpPut;
	}
	
	public static synchronized HttpClient getHttpClient() { 
		if (null== customerHttpClient) {
			SSLSocketFactory sf = SSLSocketFactory.getSocketFactory();
			/*try {//加载不受信用的证书的https方法一：设置加载全部不受信用
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				sf = new EasySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //允许所有主机
			} catch (Exception e) {
				Log.e(TAG, "-KeyStore-SSLSocketFactory-error",e);
			}*/
			InputStream ins = null;
			try {
				//加载不受信用的证书的https方法二：引入服务器颁发的公开证书 *.cer|*.crt文件
				ins = OMApplication.getInstance().getAssets().open(CommonConsts.SSL_CER_NAME); //下载的证书放到项目中的assets目录中
				CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");//X.509是证书类型【注意密钥库、证书类型】
				Certificate cer = cerFactory.generateCertificate(ins);//步骤一：获取服务器的公开证书文件，加载到android
				KeyStore keyStore = KeyStore.getInstance("BKS", "BC");//android平台上支持的密钥库类型keystore type有PKCS12（含密钥和公钥）|BKS（只含公钥，后缀.cer|.crt），好像不支持JKS，不过在windows平台上是可以代替的
				keyStore.load(null,null);//参数1：密钥库文件对象；参数2：密钥库打开密码    (ins, "123456".toCharArray());//密码是在PKSC12的时候如果证书需要密码打开
				keyStore.setCertificateEntry("trust", cer);//步骤二：将证书添加到密钥库  参数1：证书在密钥库存在的别名-唯一标识；参数2：证书对象
				
				sf = new SSLSocketFactory(keyStore);
				
			} catch (Exception e) {
				Log.e(TAG, "--SSL Certificate load error----", e);
			}finally{
				if(ins != null){
					try {
						ins.close();//?
					} catch (IOException e) {
						Log.e(TAG, "--SSL Certificate close error----", e);
					}
				}
			}

			
			HttpParams params =new BasicHttpParams(); 
			// 设置一些基本参数            
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params,CHARSET); 
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params,getUserAgent());//"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83)"+"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
	        // 设置 默认的超时重试处理策略
			// 超时设置/* 从连接池中取连接的超时时间 */
			ConnManagerParams.setTimeout(params, 5000);
			/* 连接超时 */
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			/* 请求超时 */
			HttpConnectionParams.setSoTimeout(params, 30000);
			// 设置我们的HttpClient支持HTTP和HTTPS两种模式 
			SchemeRegistry schReg =new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf,443));//SSLSocketFactory.getSocketFactory(),443));//把证书库作为信任证书库
			//httpclient.getConnectionManager().getSchemeRegistry().register(schReg);
			// 使用线程安全的连接管理来创建HttpClient 
			ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg);
			customerHttpClient =new DefaultHttpClient(conMgr, params);
			if (OMApplication.getInstance().getVal(CommonConsts.NEED_PROXY, false)) {
				HttpHost proxy = new HttpHost(OMApplication.getInstance().getVal(CommonConsts.PROXY_URL, "proxynj.zte.com.cn"),OMApplication.getInstance().getVal(CommonConsts.PROXY_PORT, 80), OMApplication.getInstance().getVal(CommonConsts.PROXY_SCHEME, "http"));
				customerHttpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}        
		return customerHttpClient;
	}
	
	public static String post(String url,String sendStr, NameValuePair... params) {
		HttpClient client = null;
        try {
            // 编码参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            for (NameValuePair p : params) {
                formparams.add(p);
            }
            //UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,CHARSET);
            StringEntity entity = new StringEntity(sendStr,CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            // 发送请求
            client = getHttpClient();
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity =  response.getEntity();
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "",e);
            return null;
        } catch (ConnectTimeoutException e) {//服务器连接超时
        	 Log.e(TAG, "--服务器连接超时--",e);
        	 return null;
        }catch (SocketTimeoutException e){ //服务器响应超时
        	 Log.e(TAG, "--服务器响应超时--",e);
        	 return null;
        } catch (ClientProtocolException e) {
        	 Log.e(TAG, "",e);
        	 return null;
        } catch (IOException e) {
        	 Log.e(TAG, "",e);
        	 return null;
        }
	 
	}
	
	public static String get(String url,String sendStr, NameValuePair... params) {
		HttpClient client = null;
        try {
            // 编码参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            for (NameValuePair p : params) {
                formparams.add(p);
            }
            URI uri = null;
			try {
				uri = URIUtils.createURI(CommonConsts.HTTPS_SCHEME, OMApplication.getInstance().getVal(CommonConsts.HTTPS_BASE, "www.baidu.com"), -1, CommonConsts.REQUEST_URI_LOGIN,URLEncodedUtils.format(formparams, "UTF-8"), null);
			} catch (URISyntaxException e) {
				LogUtil.e(e);
			}
			HttpGet httpget = new HttpGet(uri);
            // 创建GET请求
            // 发送请求
            client = getHttpClient();
            HttpResponse response = client.execute(httpget);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("请求失败");
            }
            HttpEntity resEntity =  response.getEntity();
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "",e);
            return null;
        } catch (ConnectTimeoutException e) {//服务器连接超时
        	 Log.e(TAG, "--服务器连接超时--",e);
        	 return null;
        }catch (SocketTimeoutException e){ //服务器响应超时
        	 Log.e(TAG, "--服务器响应超时--",e);
        	 return null;
        } catch (ClientProtocolException e) {
        	 Log.e(TAG, "",e);
        	 return null;
        } catch (IOException e) {
        	 Log.e(TAG, "",e);
        	 return null;
        }
	 
	}
	
	@SuppressWarnings("unchecked")
	public synchronized static NetResult put(URI uri,JSONObject jsonObject,int testType){
		NetResult result = new NetResult();
		HttpClient client = null;
		HttpPut request = null;
        try {
            // 创建PUT请求
        	LogUtil.e("-----requset uri-------\n"+uri.toString());
            request = getHttpPut(uri, getCookie(), getUserAgent());
            // 发送请求
            client = getHttpClient();
            JSONObject jsonO = getHeadJsonObject();
            if(jsonObject != null && jsonObject.length() > 0){
            	Iterator<String> it = jsonObject.keys();  
            	while(it.hasNext()){//遍历JSONObject  
            		String key = it.next();  
            		Object val = jsonObject.get(key);
            		jsonO.put(key, val);
            	}  
            }
            StringBuilder sd = new StringBuilder(jsonO.toString());
            //jsonO.put("digest", getRequestDigest(sd.toString()));//将传输的数据的json字符串+平台校验key经过md5后
            sd.insert(sd.lastIndexOf("}"), ",\"digest\":"+"\""+getRequestDigest(sd.toString())+"\"");
            StringEntity entity = new StringEntity(sd.toString(),CHARSET);
            request.setEntity(entity);
            /*Header[] headers = request.getAllHeaders();
            String headersStr = "";
            for (int i = 0; i < headers.length; i++) {
            	headersStr += headers[i].toString()+";";
			}*/
            String rereg = "(,\"password\":)\"(.*?)\"|(,\"mpin\":)\"(.*?)\"|(,\"oldPassword\":)\"(.*?)\"|(,\"newPassword\":)\"(.*?)\"";
            String str = sd.toString();
            LogUtil.i("---requset header--"+"/n---requset body-------\n"+str.replaceAll(rereg, "$1$3$5$7***"));
            
            /*HttpResponse response = client.execute(request);
            LogUtil.e("-----response statusLine------\n"+response.getStatusLine().toString());
            int code = response.getStatusLine().getStatusCode();
            if(code != HttpStatus.SC_OK) {//服务器返回错误
            	result.setSuccess(false);
            	result.setErrorTipStr(String.format(OMApplication.NET_STATUS_ERROR, code));
            	return result;
            }
            HttpEntity resEntity =  response.getEntity();
            String jsonStr = EntityUtils.toString(resEntity, CHARSET);*/
            
            /*********test***********/
            String jsonStr = "";
            switch (testType) {
			case 1://登录
				jsonStr = "{'data':{'userName':'Jack','language':1,'userId':10080,'sessionId':'2D68D35C16787F570A2C69687719E2B8','payPassFlag':3,'loginPassFlag':3},'errorCode':'','errors':{},'success':true,\"digest\":\"68BAEFB485628E0F5FFA4D5A0A7C0E78\"}";
				break;
			case 2://参数
				jsonStr = "{'data': {'paramMap': {'tradeType': {'33': 'Send Money','37': 'Buy Airtime','1303': 'WithDraw Money','4': 'Payment Services'}, 'language': {'1': 'English','2': 'Kiswahili'}}}, 'errorCode': '','errors': {},'success': true,\"digest\":\"5E4BB0EC6EA3A05F0E13589103D8EE11\"}";
				break;
			case 3://登出
				jsonStr = "{'data':{},'errorCode':'','errors':{},'success':true,\"digest\":\"DC2D18C2815F04DBCA20E6DA4A7EBACB\"}";
				break;
			case 4://验证码
				jsonStr = "{'data':{},'errorCode':'CM0000000004','errors':{'msg':'error'},'success':true,\"digest\":\"EFD83C18A3320B9A418ABE42A48C9963\"}";
				break;
			case 5://帐号余额
				jsonStr = "{'data':{'cashBalance':10000},'errorCode':'','errors':{},'success':true,\"digest\":\"13836A6CA098EA70F9A94EF6CEDF93BE\"}";
				break;
			case 6://仅三次交易
				jsonStr = "{  'data': { 'result': [ ['', '2014.04.12 10:26:21','10000', 33, 0, 1,'23423'], [ '','2014.03.12 10:24:21','25600',37, 0,1,'23423' ],  ['','2014.05.12 10:21:21','2000',1303,0,1,'dstInfo','th3Info']],'rowCount': 3 },'errorCode':'','errors': {},'success': true,\"digest\":\"2743A582B97E2FE4B5C2943CBDA7D422\"}";
				break;
			case 7://一年交易
				jsonStr = "{  'data': { 'currentPage': 1,'nextPage': 1,'pageSize': 30,'pages': 1,'previousPage': 1,'result': [ "
						+ "['11001', '2013.04.12 10:26:21',  '10000',  33,  0,  1 , 'dstInfo'],"
						+ "['11001', '2014.03.12 10:24:21','25600', 37, 0, 1, 'dstInfo' ],"
						+ "['11001','2014.05.12 10:21:21','2000', 1303, 0, 1, 'dstInfo','th3Info'],"
						+ "['11001', '2014.04.12 10:26:21',  '10000',  33,  0,  1, 'dstInfo' ],"
						+ "['11001', '2014.03.12 10:24:21','25600', 37, 0, 1 , 'dstInfo'], "
						+ "['11001','2014.05.12 10:21:21','2000', 1303, 0, 1, 'dstInfo','th3Info'],"
						+ "['11001', '2014.04.12 10:26:21',  '10000',  33,  0,  1, 'dstInfo' ],"
						+ "['11001', '2013.03.12 10:24:21','25600', 37, 0, 1 , 'dstInfo'],  "
						+ "['11001','2014.05.12 10:21:21','2000', 1303, 0, 1, 'dstInfo','th3Info'],"
						+ "['11001','2014.05.12 10:21:21','2000', 1303, 0, 1, 'dstInfo','th3Info'],"
						+ "['11001', '2014.04.12 10:26:21',  '10000',  33,  0,  1 , 'dstInfo']"
						+ "],'rowCount': 31 },'errorCode':'','errors': {},'success': true,\"digest\":\"E5F1FECEE9F133B87299418B96FA2244\"}";
				break;
			case 8://修改支付密码 转账 充值 取钱 付款 
				jsonStr = "{'data':{},'errorCode':'CM0000000004','errors':{'msg':'CM0000000004----'},'success':true,\"digest\":\"8D3A9F00EDBC88E938CCA34631FDFDE9\"}";
			default:
				break;
			}
            /*********test***********/
            LogUtil.i("---response entity---\n"+jsonStr);
            //校验签名-待添加
			String reg = ",\"digest\":\"(.*)\"";
			Pattern p = Pattern.compile(reg); // 正则表达式
			Matcher m = p.matcher(jsonStr); // 操作的字符串
			String responseDigest = "";
			while (m.find()) {
				responseDigest = m.group(1);
			}
			String splitString = m.replaceAll("");
			if(!responseDigest.equals(getRequestDigest(splitString))){
				result.setSuccess(false);
            	result.setErrorTipStr(OMApplication.TYPE_NETWORK_DIGEST_ERROR);
            	return result;
			}
			JSONObject js = new JSONObject(jsonStr);
            boolean responseSuccess = js.getBoolean("success");
            if(!responseSuccess){
            	result.setSuccess(false);
            	String responseErrorCode = js.getString("errorCode");
            	String responseErrorMsg = js.getJSONObject("errors").getString("msg");
            	//if(!StringUtils.isEmpty(responseErrorMsg) && responseErrorMsg.equalsIgnoreCase("CM0000000004"))//不需要APP解析错误码，平台直接返回错误信息
            	if(StringUtils.isEmpty(responseErrorMsg))
            			responseErrorMsg = OMApplication.TYPE_NETWORK_OPER_FAILE;
            	result.setErrorCode(responseErrorCode);
            	result.setErrorTipStr(responseErrorMsg);
            }else{
            	result.setSuccess(true);
            	result.setErrorTipStr(OMApplication.TYPE_NETWORK_OPER_SUCCESS);
            	result.setResultJsonStr(jsonStr);
            }
            return result;
        }catch (Exception e) {
        	LogUtil.e("-*******-om net request error-******-", e);
        	String errorStr = "";
        	result.setSuccess(false);
        	if(e instanceof UnknownHostException || e instanceof ConnectException){//手机网络问题或服务器找不到问题
        		errorStr = OMApplication.TYPE_NETWORK_NOT_CONNECT;
    		}else if(e instanceof ConnectTimeoutException){
    			errorStr = OMApplication.TYPE_NETWORK_CONNECT_TIMEOUT;
    		}else if(e instanceof SocketTimeoutException){
    			errorStr = OMApplication.TYPE_NETWORK_SOCKET_TIMEOUT;
    		}else if(e instanceof IOException){
    			errorStr = OMApplication.TYPE_NETWORK_IO_EXCEPTION;
    		}else{//应用内部错误
    			errorStr = OMApplication.APP_ERROR;
    		}
            	result.setErrorTipStr(errorStr);
            return result;
		}finally{
        	if(request!=null){
        		request.abort();
        	}
        }
	 
	}
	private static JSONObject getHeadJsonObject() {
		JSONObject jbHead = new JSONObject();
		try {
			jbHead.put("channelId", 2+"");
			jbHead.put("osName", "Android");
			String[] strs = MobileUtils.getTelInfo();
			jbHead.put("osVersion", strs[6]);
			jbHead.put("deviceNo",strs[0] );
			jbHead.put("imsi",strs[1]);
			
			User user = (User)OMApplication.getInstance().getVal(CommonConsts.LOGIN_USER, null);
			if(user != null && user.getUserId() > 0){
				jbHead.put("userId",user.getUserId());
				jbHead.put("sessionId",OMApplication.getInstance().getVal(CommonConsts.COOKIE, ""));
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, e);
		}
		return jbHead;
	}
	private static String getRequestDigest(String string) {
		//数字签名=发送json数据串+平台分配校验码再MD5摘要--校验方法来自中兴余额宝，校验原理：
		// String digest =  DigestUtil.hmacSign(string, OMApplication.getInstance().getVal(CommonConsts.DIGEST_CODE, ""));
		 String digest1 =  DigestUtil.getMD5(string+OMApplication.getInstance().getVal(CommonConsts.DIGEST_CODE, ""));
		//LogUtil.i("===========send digest1:========"+digest1);
		//String digest2 = MD5Util.md5(string+OMApplication.getInstance().getVal(CommonConsts.DIGEST_CODE, ""));
		 //LogUtil.i("===========send digest1:========"+digest1);
		return digest1.toUpperCase();
	}
	
	/*private static String fromErrorCodeToMsg(String errorCode){
		String msg = "";
		if(errorCode.equals("CM0000000001"))
			msg = OMApplication.getInstance().getString(R.string.CM0000000001);
		if(errorCode.equals("SY0000000001"))
			msg = OMApplication.getInstance().getString(R.string.SY0000000001);
		//TODO 待添加
		return msg;
	}*/
}
