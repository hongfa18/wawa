package com.wawa.arm.entity;

public class MoneyChange {
	/**�ʽ������ͣ�1��send money 2��buy airtime 3:widthdraw-agent 4:widthdraw-atm 5:bill 6:goods*/
	private int changeType;
	private String inputOne;//��ֵ�绰����-ת��绰����|�����̱��|�̻����
	private String amount;//�ʽ�䶯���
	private String billRef;//�ɷѱ��
	/**
	 * �ʽ������ͣ�
	 * 1��send money 
	 * 2��buy airtime
	 * 3:widthdraw-agent 
	 * 4:widthdraw-atm 
	 * 5:bill 
	 * 6:goods*/
	public int getChangeType() {
		return changeType;
	}
	/**
	 * �ʽ������ͣ�
	 * 1��send money 
	 * 2��buy airtime
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
