package com.wawa.arm.entity;

public class MoneyChange {
	/**资金变更类型；1：send money 2：buy airtime 3:widthdraw-agent 4:widthdraw-atm 5:bill 6:goods*/
	private int changeType;
	private String inputOne;//充值电话号码-转入电话号码|代理商编号|商户编号
	private String amount;//资金变动金额
	private String billRef;//缴费编号
	/**
	 * 资金变更类型：
	 * 1：send money 
	 * 2：buy airtime
	 * 3:widthdraw-agent 
	 * 4:widthdraw-atm 
	 * 5:bill 
	 * 6:goods*/
	public int getChangeType() {
		return changeType;
	}
	/**
	 * 资金变更类型：
	 * 1：send money 
	 * 2：buy airtime
	 * 3:widthdraw-agent 
	 * 4:widthdraw-atm 
	 * 5:bill 
	 * 6:goods*/
	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getInputOne() {
		return inputOne;
	}
	public void setInputOne(String inputOne) {
		this.inputOne = inputOne;
	}
	public String getBillRef() {
		return billRef;
	}
	public void setBillRef(String billRef) {
		this.billRef = billRef;
	}
	
}
