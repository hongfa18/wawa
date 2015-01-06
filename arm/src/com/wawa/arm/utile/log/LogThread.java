package com.wawa.arm.utile.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

import com.wawa.arm.common.OMApplication;

public class LogThread extends Thread{
	public static boolean isPrintLog = true;
	@Override
	public void run() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			StringBuffer logPath = new StringBuffer();
			logPath.append(Environment.getExternalStorageDirectory());
			logPath.append("/");
			logPath.append(OMApplication.getInstance().getPackageName())
					.append("/");
			logPath.append("logs").append("/arm_log.txt");
			String myPid = android.os.Process.myPid() + "";
			 
			String cmds = "logcat -v time |grep \"(" + myPid + ")\"";//加上 -d 只输入当前之前的log，之后的不会输出
			List<String> clears = new ArrayList<String>();
			clears.add("logcat");
			clears.add("-c");
			FileWriter fw = null;
			BufferedWriter bw = null;
			BufferedReader buffereReader = null;
			Process proccess = null;
			try {
				File file = new File(logPath.toString());
				if (!file.getParentFile().exists()) {
					if (file.getParentFile().mkdirs()) {
						file.createNewFile();
					}
				} else {
					if (file.exists()) {
						if (file.length() > 1024 * 1024 * 4) {
							file.delete();
							file.createNewFile();
						}
					} else {
						file.createNewFile();
					}
				}
				fw = new FileWriter(logPath.toString(), true);
				bw = new BufferedWriter(fw);
				proccess = Runtime.getRuntime().exec(cmds);
				buffereReader = new BufferedReader(new InputStreamReader(proccess.getInputStream()));
				String str = null;
				while ((str = buffereReader.readLine()) != null && isPrintLog) {
					if (str.contains(myPid)) {
						bw.write(str);
						bw.newLine();
						bw.flush();
					}
				}
			} catch (IOException e) {
				LogUtil.e("日志输出错误",e);
			} finally {
				try {
					if (buffereReader != null) {
						buffereReader.close();
					}
					if (bw != null) {
						bw.close();
					}
					if (fw != null) {
						fw.close();
					}
					if (proccess != null) {
						proccess.destroy();
						proccess = null;
					}
				} catch (IOException e) {
					LogUtil.e(e);
				}
			}
		} else {
			LogUtil.i("sd卡不存在,日志不能写出到文件");
		}
	}
	
	public boolean stopLogThread(){
		isPrintLog = false;
		return true;
	}
}
