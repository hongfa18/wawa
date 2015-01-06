package com.wawa.arm.utile.log;

import android.util.Log;

import com.wawa.arm.common.OMApplication;

/**
 * ��������־��ӡ
 *
 * @author W.Y
 * @version 1.0
 * @created 2014��5��20�� ����3:37:52
 */
public class LogUtil {
	
	private static final int INDEX_OF_CALLER_IN_STACK = 4;//�������ڶ�ջ�е�����λ��
	private static String LOG_TAG = OMApplication.getInstance().getPackageName(); // �Զ���tag
	
	
	public static void setLogTag(String logTag) {
		LOG_TAG = logTag;
	}
	
	
	/**
	 * ������
	 * ��ȡ�����ߵ�����������־��¼����Ϊtagʹ��
	 * ��ȡ�����ߵķ�����������־��¼����Ϊmsgʹ��
	 * @author W.Y
	 * @version 1.0
	 * @created 2014��5��20�� ����3:38:12
	 * @return
	 */
	private static String[] getCallerClassNameAndMethodName() {
		StackTraceElement[] allStackTraceElements = Thread.currentThread().getStackTrace();
		return new String[]{allStackTraceElements[INDEX_OF_CALLER_IN_STACK].getClassName(), allStackTraceElements[INDEX_OF_CALLER_IN_STACK].getMethodName() + " - line " + allStackTraceElements[INDEX_OF_CALLER_IN_STACK].getLineNumber() + " "};
	}
	

	public static void v(String msg) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.v(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg);
	}
	public static void d(String msg) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.d(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg);
	}
	public static void i(String msg) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.i(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg);
	}
	public static void w(String msg) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.w(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg);
	}
	public static void e(String msg) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.e(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg);
	}
	
	public static void v(Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.v(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1], t);
	}
	public static void d(Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.d(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1], t);
	}
	public static void i(Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.i(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1], t);
	}
	public static void w(Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.w(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1], t);
	}
	public static void e(Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.e(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1], t);
	}
	
	
	public static void v(String msg, Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.v(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg, t);
	}
	public static void d(String msg, Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.d(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg, t);
	}
	public static void i(String msg, Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.i(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg, t);
	}
	public static void w(String msg, Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.w(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg, t);
	}
	public static void e(String msg, Throwable t) {
		String[] classNameAndMethodName = getCallerClassNameAndMethodName();
		Log.e(LOG_TAG, classNameAndMethodName[0] + "--" + classNameAndMethodName[1] + msg, t);
	}
	
	
	public static void v(String tag, String msg) {
		Log.v(tag, msg);
	}
	public static void d(String tag, String msg) {
		Log.d(tag, msg);
	}
	public static void i(String tag, String msg) {
		Log.i(tag, msg);
	}
	public static void w(String tag, String msg) {
		Log.w(tag, msg);
	}
	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}
	
	
	
	public static void v(String tag, String msg, Throwable t) {
		Log.v(tag, msg, t);
	}
	public static void d(String tag, String msg, Throwable t) {
		Log.d(tag, msg, t);
	}
	public static void i(String tag, String msg, Throwable t) {
		Log.i(tag, msg, t);
	}
	public static void w(String tag, String msg, Throwable t) {
		Log.w(tag, msg, t);
	}
	public static void e(String tag, String msg, Throwable t) {
		Log.e(tag, msg, t);
	}
	
	
}
