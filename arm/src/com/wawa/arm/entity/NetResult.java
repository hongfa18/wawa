package com.wawa.arm.entity;

public class NetResult {
	private boolean success;//ͨ���Ƿ�ɹ�
	private String errorCode;//�������
	private String errorTipStr;//������ʾ
	private String resultJsonStr;//����ʱ���ص�json��
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
