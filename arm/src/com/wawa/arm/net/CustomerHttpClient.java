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
			ua.append('/'+OMApplication.getInstance().getPackageInfo().versionName+'_'+OMApplication.getInstance().getPackageInfo().versionCode);//App�汾
			ua.append("/Android");//�ֻ�ϵͳƽ̨
			ua.append("/"+android.os.Build.VERSION.RELEASE);//�ֻ�ϵͳ�汾
			ua.append("/"+android.os.Build.MODEL); //�ֻ��ͺ�
			ua.append("/"+OMApplication.getInstance().getAppId());//�ͻ���Ψһ��ʶ
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	/*private static HttpGet getHttpGet(String url, String cookie, String userAgent) {
		HttpGet httpGet = new HttpGet(url);
		// ���� ����ʱʱ��
		httpGet.setHeader("Connection","Keep-Alive");
		httpGet.setHeader("Cookie", cookie);
		httpGet.setHeader("User-Agent", userAgent);
		return httpGet;
	}*/
	
	/*private static HttpPost getHttpPost(String url, String cookie, String userAgent) {
		HttpPost httpPost = new HttpPost(url);
		// ���� ����ʱʱ��
		httpPost.setHeader("Connection","Keep-Alive");
		httpPost.setHeader("Cookie", cookie);
		httpPost.setHeader("User-Agent", userAgent);
		return httpPost;
	}*/
	private static HttpPut getHttpPut(URI url, String cookie, String userAgent) {
		HttpPut httpPut = new HttpPut(url);
		// ���� ����ʱʱ��
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
			/*try {//���ز������õ�֤���https����һ�����ü���ȫ����������
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				sf = new EasySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  //������������
			} catch (Exception e) {
				Log.e(TAG, "-KeyStore-SSLSocketFactory-error",e);
			}*/
			InputStream ins = null;
			try {
				//���ز������õ�֤���https������������������䷢�Ĺ���֤�� *.cer|*.crt�ļ�
				ins = OMApplication.getInstance().getAssets().open(CommonConsts.SSL_CER_NAME); //���ص�֤��ŵ���Ŀ�е�assetsĿ¼��
				CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");//X.509��֤�����͡�ע����Կ�⡢֤�����͡�
				Certificate cer = cerFactory.generateCertificate(ins);//����һ����ȡ�������Ĺ���֤���ļ������ص�android
				KeyStore keyStore = KeyStore.getInstance("BKS", "BC");//androidƽ̨��֧�ֵ���Կ������keystore type��PKCS12������Կ�͹�Կ��|BKS��ֻ����Կ����׺.cer|.crt��������֧��JKS��������windowsƽ̨���ǿ��Դ����
				keyStore.load(null,null);//����1����Կ���ļ����󣻲���2����Կ�������    (ins, "123456".toCharArray());//��������PKSC12��ʱ�����֤����Ҫ�����
				keyStore.setCertificateEntry("trust", cer);//���������֤����ӵ���Կ��  ����1��֤������Կ����ڵı���-Ψһ��ʶ������2��֤�����
				
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
			// ����һЩ��������            
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params,CHARSET); 
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params,getUserAgent());//"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83)"+"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			// ���� HttpClient ���� Cookie,���������һ���Ĳ���
	        // ���� Ĭ�ϵĳ�ʱ���Դ������
			// ��ʱ����/* �����ӳ���ȡ���ӵĳ�ʱʱ�� */
			ConnManagerParams.setTimeout(params, 5000);
			/* ���ӳ�ʱ */
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			/* ����ʱ */
			HttpConnectionParams.setSoTimeout(params, 30000);
			// �������ǵ�HttpClient֧��HTTP��HTTPS����ģʽ 
			SchemeRegistry schReg =new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", sf,443));//SSLSocketFactory.getSocketFactory(),443));//��֤�����Ϊ����֤���
			//httpclient.getConnectionManager().getSchemeRegistry().register(schReg);
			// ʹ���̰߳�ȫ�����ӹ���������HttpClient 
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
            // �������
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // �������
            for (NameValuePair p : params) {
                formparams.add(p);
            }
            //UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,CHARSET);
            StringEntity entity = new StringEntity(sendStr,CHARSET);
            // ����POST����
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            // ��������
            client = getHttpClient();
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("����ʧ��");
            }
            HttpEntity resEntity =  response.getEntity();
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "",e);
            return null;
        } catch (ConnectTimeoutException e) {//���������ӳ�ʱ
        	 Log.e(TAG, "--���������ӳ�ʱ--",e);
        	 return null;
        }catch (SocketTimeoutException e){ //��������Ӧ��ʱ
        	 Log.e(TAG, "--��������Ӧ��ʱ--",e);
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
            // �������
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // �������
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
            // ����GET����
            // ��������
            client = getHttpClient();
            HttpResponse response = client.execute(httpget);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new RuntimeException("����ʧ��");
            }
            HttpEntity resEntity =  response.getEntity();
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "",e);
            return null;
        } catch (ConnectTimeoutException e) {//���������ӳ�ʱ
        	 Log.e(TAG, "--���������ӳ�ʱ--",e);
        	 return null;
        }catch (SocketTimeoutException e){ //��������Ӧ��ʱ
        	 Log.e(TAG, "--��������Ӧ��ʱ--",e);
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
            // ����PUT����
        	LogUtil.e("-----requset uri-------\n"+uri.toString());
            request = getHttpPut(uri, getCookie(), getUserAgent());
            // ��������
            client = getHttpClient();
            JSONObject jsonO = getHeadJsonObject();
            if(jsonObject != null && jsonObject.length() > 0){
            	Iterator<String> it = jsonObject.keys();  
            	while(it.hasNext()){//����JSONObject  
            		String key = it.next();  
            		Object val = jsonObject.get(key);
            		jsonO.put(key, val);
            	}  
            }
            StringBuilder sd = new StringBuilder(jsonO.toString());
            //jsonO.put("digest", getRequestDigest(sd.toString()));//����������ݵ�json�ַ���+ƽ̨У��key����md5��
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
            if(code != HttpStatus.SC_OK) {//���������ش���
            	result.setSuccess(false);
            	result.setErrorTipStr(String.format(OMApplication.NET_STATUS_ERROR, code));
            	return result;
            }
            HttpEntity resEntity =  response.getEntity();
            String jsonStr = EntityUtils.toString(resEntity, CHARSET);*/
            
            /*********test***********/
            String jsonStr = "";
            switch (testType) {
			case 1://��¼
				jsonStr = "{'data':{'userName':'Jack','language':1,'userId':10080,'sessionId':'2D68D35C16787F570A2C69687719E2B8','payPassFlag':3,'loginPassFlag':3},'errorCode':'','errors':{},'success':true,\"digest\":\"68BAEFB485628E0F5FFA4D5A0A7C0E78\"}";
				break;
			case 2://����
				jsonStr = "{'data': {'paramMap': {'tradeType': {'33': 'Send Money','37': 'Buy Airtime','1303': 'WithDraw Money','4': 'Payment Services'}, 'language': {'1': 'English','2': 'Kiswahili'}}}, 'errorCode': '','errors': {},'success': true,\"digest\":\"5E4BB0EC6EA3A05F0E13589103D8EE11\"}";
				break;
			case 3://�ǳ�
				jsonStr = "{'data':{},'errorCode':'','errors':{},'success':true,\"digest\":\"DC2D18C2815F04DBCA20E6DA4A7EBACB\"}";
				break;
			case 4://��֤��
				jsonStr = "{'data':{},'errorCode':'CM0000000004','errors':{'msg':'error'},'success':true,\"digest\":\"EFD83C18A3320B9A418ABE42A48C9963\"}";
				break;
			case 5://�ʺ����
				jsonStr = "{'data':{'cashBalance':10000},'errorCode':'','errors':{},'success':true,\"digest\":\"13836A6CA098EA70F9A94EF6CEDF93BE\"}";
				break;
			case 6://�����ν���
				jsonStr = "{  'data': { 'result': [ ['', '2014.04.12 10:26:21','10000', 33, 0, 1,'23423'], [ '','2014.03.12 10:24:21','25600',37, 0,1,'23423' ],  ['','2014.05.12 10:21:21','2000',1303,0,1,'dstInfo','th3Info']],'rowCount': 3 },'errorCode':'','errors': {},'success': true,\"digest\":\"2743A582B97E2FE4B5C2943CBDA7D422\"}";
				break;
			case 7://һ�꽻��
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
			case 8://�޸�֧������ ת�� ��ֵ ȡǮ ���� 
				jsonStr = "{'data':{},'errorCode':'CM0000000004','errors':{'msg':'CM0000000004----'},'success':true,\"digest\":\"8D3A9F00EDBC88E938CCA34631FDFDE9\"}";
			default:
				break;
			}
            /*********test***********/
            LogUtil.i("---response entity---\n"+jsonStr);
            //У��ǩ��-�����
			String reg = ",\"digest\":\"(.*)\"";
			Pattern p = Pattern.compile(reg); // ������ʽ
			Matcher m = p.matcher(jsonStr); // �������ַ���
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
            	//if(!StringUtils.isEmpty(responseErrorMsg) && responseErrorMsg.equalsIgnoreCase("CM0000000004"))//����ҪAPP���������룬ƽֱ̨�ӷ��ش�����Ϣ
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
        	if(e instanceof UnknownHostException || e instanceof ConnectException){//�ֻ����������������Ҳ�������
        		errorStr = OMApplication.TYPE_NETWORK_NOT_CONNECT;
    		}else if(e instanceof ConnectTimeoutException){
    			errorStr = OMApplication.TYPE_NETWORK_CONNECT_TIMEOUT;
    		}else if(e instanceof SocketTimeoutException){
    			errorStr = OMApplication.TYPE_NETWORK_SOCKET_TIMEOUT;
    		}else if(e instanceof IOException){
    			errorStr = OMApplication.TYPE_NETWORK_IO_EXCEPTION;
    		}else{//Ӧ���ڲ�����
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
		//����ǩ��=����json���ݴ�+ƽ̨����У������MD5ժҪ--У�鷽��������������У��ԭ��
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
		//TODO �����
		return msg;
	}*/
}
