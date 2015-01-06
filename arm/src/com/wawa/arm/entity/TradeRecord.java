package com.wawa.arm.entity;

import java.io.Serializable;

public class TradeRecord implements Serializable{
	private static final long serialVersionUID = 7419598106125671663L;
	private String tradeFlowNo;//������ˮ��	
	private String tradeTime;//����ʱ��
	private String timeDate;
	private String timeHour;
	private String amount;//���׽��	
	private String tradeType;//��������	
	private String opType;//��������	
	private String currency;//����
	private String dstInfo;//�绰��ɷѺ���
	private String th3Info;//�������׶������ ���ɷ��̼ұ���
	public String getTradeFlowNo() {
		return tradeFlowNo;
	}
	public void setTradeFlowNo(String tradeFlowNo) {
		this.tradeFlowNo = tradeFlowNo;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getOpType() {
		return opType;
	}
	public void setOpType(String opType) {
		this.opType = opType;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTimeDate() {
		return timeDate;
	}
	public void setTimeDate(String timeDate) {
		this.timeDate = timeDate;
	}
	public String getTimeHour() {
		return timeHour;
	}
	public void setTimeHour(String timeHour) {
		this.timeHour = timeHour;
	}
	public String getDstInfo() {
		return dstInfo;
	}
	public void setDstInfo(String dstInfo) {
		this.dstInfo = dstInfo;
	}
	public String getTh3Info() {
		return th3Info;
	}
	public void setTh3Info(String th3Info) {
		this.th3Info = th3Info;
	}
	
}
