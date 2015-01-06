package com.wawa.arm.entity;

public class NetResult {
	private boolean success;//通信是否成功
	private String errorCode;//错误代码
	private String errorTipStr;//错误提示
	private String resultJsonStr;//正常时返回的json串
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getResultJsonStr() {
		return resultJsonStr;
	}
	public void setResultJsonStr(String resultJsonStr) {
		this.resultJsonStr = resultJsonStr;
	}
	public String getErrorTipStr() {
		return errorTipStr;
	}
	public void setErrorTipStr(String errorTipStr) {
		this.errorTipStr = errorTipStr;
	}
	
}
