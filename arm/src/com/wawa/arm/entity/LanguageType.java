package com.wawa.arm.entity;

public class LanguageType {
	private String typeCode;
	private String languageName;
	public LanguageType() {
	}
	public LanguageType(String typeCode, String languageName) {
		this.typeCode = typeCode;
		this.languageName = languageName;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getLanguageName() {
		return languageName;
	}
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
	
}
