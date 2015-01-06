package com.wawa.arm.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.wawa.arm.utile.MyToast;
import com.wawa.arm.utile.widgets.OMNetDialog;

public class WaitUIElement {

	private     boolean needUI=false;
	private     Context context;
	private     OMNetDialog progressDialog=null;
	private    boolean needDialog = true;
	
	public  WaitUIElement(Context context)
	{
		if(null == context)
			needUI=false;
		else
			needUI=true;
		this.context = context;
		this.progressDialog=null;
	}
	
	public  WaitUIElement(Context context,boolean needDialog)
	{
		this(context);
		this.needDialog=needDialog;
	}
	
	public void showToast(String messageStr,boolean isSuccess)
	{
		if(needUI && messageStr != null && messageStr.trim().length() > 0 )
		    MyToast.showDIYToast((Activity)context,messageStr, 2000,isSuccess);
	}
	
	public void showProcessDialog(String messageStr)
	{
		if(needUI && needDialog){
			if(changeMag(messageStr)){
				progressDialog = OMNetDialog.createDialog(context, 1, messageStr);
				progressDialog.show();
			}
		}
	}
	public boolean changeMag(String msg){
		if(progressDialog != null  && progressDialog.isShowing()){
			progressDialog.setContent(msg);
			return false;
		}else{
			return true;
		}
	}
	public void dismissProcessDialog()
	{
		if(needUI)
			 if (progressDialog != null && progressDialog.isShowing()) {
		        	progressDialog.dismiss();
		        }
	}
	public Context getContext()
	{
		return context;
	}
	
	public boolean isShowing(){
		if (progressDialog != null && progressDialog.isShowing()) {
			return true;
		}else{
			return false;
		}
	}
	public void setCancelLis(DialogInterface.OnCancelListener lis){
		if (progressDialog != null){
			progressDialog.setOnCancelListener(lis);
		}
	}
}
