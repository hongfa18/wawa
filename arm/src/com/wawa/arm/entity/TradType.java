package com.wawa.arm.entity;

import java.io.Serializable;

public class TradType implements Serializable{
	private static final long serialVersionUID = 8241407603269595167L;
	private String typeCode;
	private String typeName;
	
	public TradType() {
	}
	public TradType(String key, String val) {
		this.typeCode = key;
		this.typeName = val;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
