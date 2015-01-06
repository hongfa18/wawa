package com.wawa.arm.utile;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.wawa.arm.common.CommonConsts;
import com.wawa.arm.common.OMApplication;
import com.wawa.arm.utile.log.LogUtil;

/**
 * 描述：
 * 手机操作工具类
 *
 * @author W.Y
 * @version 1.0
 * @created 2014年5月20日 下午3:40:25
 */
public class MobileUtils
{
    public Context context;
    public MobileUtils(){}
    public MobileUtils(Context context){
        this.context = context;
    }


    /**
     * 描述：
     *
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午3:42:25
     * @param context
     */
    public static void  getWidthAndHight(Context context){
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = mWindowManager.getDefaultDisplay().getWidth();
        int height = mWindowManager.getDefaultDisplay().getHeight();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int scale = dm.densityDpi;//240 320  densityDpi
        float density = dm.density;//1.5     density    
    }

    /**
     * 描述：
     * 获取IMEI号，IESI号，手机型号
     * (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午3:43:59
     */
    public static String[] getTelInfo(){
    	String[] str = new String[8];
        TelephonyManager mTm = (TelephonyManager)OMApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId(); //  存储在手机上IMEI：international mobile Equipment identity手机唯一标识码；
        str[0]  = imei;
        String imsi = mTm.getSubscriberId();// 存储在手机卡上 international mobiles subscriber identity国际移动用户号码标识
        str[1]  = imsi;
        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得 取出MSISDN，很可能为空
        str[2]  = numer;
        //LogUtil.d(mTm.getSimCountryIso()+";"+mTm.getSimSerialNumber()+";"+mTm.getSimOperatorName());
        //String imei =mTm.getSimSerialNumber();  //取出ICCID
        
        String mtyb = android.os.Build.BRAND;// 手机品牌
        str[3]  = mtyb;
        String mtype = Build.MODEL; // 手机型号
        str[4]  = mtype;
        String sdk = Build.VERSION.SDK;// SDK版本号
        str[5]  = sdk;
        String sysversion = Build.VERSION.RELEASE;// 系统版本号
        str[6]  = sysversion;
        String sn = android.os.Build.SERIAL;// SN
        str[7]  = sn;
        return str;
    }

    /**
     * 描述：
     * 获取手机MAC地址 只有手机开启wifi才能获取到mac地址
     * (WifiManager)context.getSystemService(Context.WIFI_SERVICE)
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午3:44:25
     * @return
     */
    public String getMacAddress()
    {
        String result = "";
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return result;
    }

    /**
     * 描述：
     * 获取系统可用剩余内存
     * (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午3:44:51
     */
    private void getAvailMemory() {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        LogUtil.e( "----获取android当前可用内存大小---="+Formatter.formatFileSize(context, info.availMem));//将获取的内存大小规格化
    }
    
    /**
     * 描述：获取当前SD卡剩余存储空间
     *
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午4:03:35
     * @return
     */
    public long getSDCardFreeSpace(){
        long space = 0;
        try{
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                File path = Environment.getExternalStorageDirectory(); 
                StatFs statfs = new StatFs(path.getPath());
               //获取block的SIZE
                long blocSize = statfs.getBlockSize();
                //空闲的Block的数量
                long availaBlock = statfs.getAvailableBlocks();
                space = availaBlock*blocSize;
            } else {
            	LogUtil.i("SD卡不存在");
            }
        }catch(Exception e){
            LogUtil.e(e);
        }
        return space;
    }
    
    /**
     * 描述：
     *	通过路径名获取在sd卡的存储绝对路径
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午4:04:03
     * @param context
     * @param pathName
     * @return
     */
    public static String getSDCardPath(Context context,String pathName){
        StringBuffer logPath = new StringBuffer();
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory(); 
            logPath.append(path);
            logPath.append("/");
            logPath.append(context.getPackageName())
                    .append("/");
            logPath.append(pathName);

            File file = new File(logPath.toString());
            if (!file.exists()) {
                file.mkdirs();
            }  
        } else {
            MyToast.showToast(context, "请检查手机SD卡是否正确安装", 1500);
        }
        return logPath.toString();
    }

    /**
     * 描述：
     * 清楚APP缓存，webview
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午4:05:30
     * @param file
     * @return
     */
    public static int clearAppWebCache(File file){
    	if(file == null){
    	    //file = CacheManager.getCacheFileBaseDir();
    	    if(file == null)
    	    	file = OMApplication.getInstance().getCacheDir();// /data/data/com.zte.mcs/cache
    	}
    	int deletedFiles = 0;
    	if (file!= null && file.isDirectory()) {
	    	try {
		    	for (File child:file.listFiles()) {
			    	if (child.isDirectory()) {
			    		deletedFiles += clearAppWebCache(child);
			    	}
		    		if (child.delete()) {//child.lastModified()
		    			deletedFiles++;
		    		}
		    	}
	    	} catch(Exception e) {
	    		LogUtil.e(e);
	    	}
    	}
    	OMApplication.getInstance().deleteDatabase("webview.db");
    	OMApplication.getInstance().deleteDatabase("webviewCache.db");
	    return deletedFiles;
    }
    
    /**
     * 描述：判断系统的某个服务是否在运行
     *
     * @author W.Y
     * @version 1.0
     * @created 2014年3月28日 上午9:10:11
     * @param context  
     * @param className  服务名 =“包名+类名”
     * @return  true 正在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context,String className) {       
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
        	String serviceClassName = serviceList.get(i).service.getClassName();
        	LogUtil.e("----system service list -"+i+"---",serviceClassName);
            if (serviceClassName.equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        LogUtil.i("----check service is running ----","service is running?=="+isRunning);
        return isRunning;
    }
    /**
     * 描述：解决listview嵌套在ScrollView或ListView中不能正常加载list的列高问题
     *
     * @author W.Y
     * @version 1.0
     * @created 2014年4月19日 上午10:54:15
     * @param listView
     */
    public static void setListViewHeight(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight()*(listAdapter.getCount()-1));
		listView.setLayoutParams(params);
	}
    
    /**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) OMApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) OMApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = CommonConsts.NETTYPE_CMNET;
				} else {
					netType = CommonConsts.NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = CommonConsts.NETTYPE_WIFI;
		}
		return netType;
	}
	
    
    /**
     * 描述：
     * 校验电话号码输入框的输入内容
     * 肯尼亚电话号码格式未知？？
     * @author W.Y
     * @version 1.0
     * @created 2014年5月20日 下午4:06:39
     * @param phoneNum
     * @return
     */
    public static boolean checkMobileNO(String phoneNum){
    	if(phoneNum == null || phoneNum.length() < 1){
    		return false;
    	}
    	String expression = "(^[0-9]{3,4}[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^[0-9]{3,4}[0-9]{7,8}$)|(^0{0,1}[0-9]{11}$)|(^\\+{0,1}86[0-9]{11}$)";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(phoneNum);
		return matcher.matches();
    }
    
    /**  
	* 返回当前程序版本名  
	*/   
	public static String getAppVersionName(Context context) {   
	    String versionName = "";   
	    try {   
	        // ---get the package info---   
	        PackageManager pm = context.getPackageManager();   
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);   
	        versionName = pi.versionName;   
	        if (versionName == null || versionName.length() <= 0) {   
	            return null;   
	        }   
	    } catch (Exception e) {   
	        LogUtil.e("VersionInfo", "Exception", e);   
	    }   
	    return versionName;   
	}  
}
