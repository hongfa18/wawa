package com.wawa.arm.common;

public class CommonMethod {
	public static boolean checkDeviceName(String name){
		String permName = OMApplication.getInstance().getVal(CommonConsts.ARM_PAIR_NAME, CommonConsts.DEFAULT_PERM_NAME);
		if(permName == null || "".equals(permName)){
			return true;
		}
		if(name != null && name.contains(permName)){
			return true;
		}else{
			return false;
		}
	}
}
