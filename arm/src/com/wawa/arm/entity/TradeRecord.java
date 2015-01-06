package com.wawa.arm.entity;

import java.io.Serializable;

public class TradeRecord implements Serializable{
	private static final long serialVersionUID = 7419598106125671663L;
	private String tradeFlowNo;//交易流水号	
	private String tradeTime;//交易时间
	private String timeDate;
	private String timeHour;
	private String amount;//交易金额	
	private String tradeType;//交易类型	
	private String opType;//操作类型	
	private String currency;//币种
	private String dstInfo;//电话或缴费号码
	private String th3Info;//三方交易对象编码 ，缴费商家编码
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
