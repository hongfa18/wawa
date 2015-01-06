package com.wawa.arm.entity;

public class User {
	private int userId;//id
	private String userCode;//电话号码/用户名
	private String uesrName;//用户姓名
	private String loginPwd;//登录密码
	private String payPwd;//支付密码
	private int loginPwdState;//登录密码状态： 1_初始密码未修改 2_密码被重置后未修改 3_正常
	private int pinState;//支付密码状态： 1_初始密码未修改 2_密码被重置后未修改 3_正常
	private String cashBalance;//账户余额
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
