package com.wawa.arm.entity;

import java.util.List;
import java.util.Map;

public class AppParamMap {
	private Map<String,String> tradTypes;
	private List<LanguageType> languageTypes;
	public Map<String,String> getTradTypes() {
		return tradTypes;
	}
	public void setTradTypes(Map<String,String> tradTypes) {
		this.tradTypes = tradTypes;
	}
	public List<LanguageType> getLanguageTypes() {
		return languageTypes;
	}
	public void setLanguageTypes(List<LanguageType> languageTypes) {
		this.languageTypes = languageTypes;
	}
	
}
