package com.wawa.arm.entity;

public class User {
	private int userId;//id
	private String userCode;//�绰����/�û���
	private String uesrName;//�û�����
	private String loginPwd;//��¼����
	private String payPwd;//֧������
	private int loginPwdState;//��¼����״̬�� 1_��ʼ����δ�޸� 2_���뱻���ú�δ�޸� 3_����
	private int pinState;//֧������״̬�� 1_��ʼ����δ�޸� 2_���뱻���ú�δ�޸� 3_����
	private String cashBalance;//�˻����
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getLoginPwd() {
		return loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}
	public String getPayPwd() {
		return payPwd;
	}
	public void setPayPwd(String payPwd) {
		this.payPwd = payPwd;
	}
	public String getCashBalance() {
		return cashBalance;
	}
	public void setCashBalance(String cashBalance) {
		this.cashBalance = cashBalance;
	}
	public String getUesrName() {
		return uesrName;
	}
	public void setUesrName(String uesrName) {
		this.uesrName = uesrName;
	}
	public int getLoginPwdState() {
		return loginPwdState;
	}
	public void setLoginPwdState(int loginPwdState) {
		this.loginPwdState = loginPwdState;
	}
	public int getPinState() {
		return pinState;
	}
	public void setPinState(int pinState) {
		this.pinState = pinState;
	}
	
}
