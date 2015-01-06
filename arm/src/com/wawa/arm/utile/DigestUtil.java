package com.wawa.arm.utile;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wawa.arm.utile.log.LogUtil;

public class DigestUtil {

	private static String encodingCharset = "UTF-8";
	
	/**
	 * @param rValue
	 * @param rKey
	 * @return
	 */
	public static boolean verify_hmacSign(String srcString, String rKey) {
		
		 String reg = ",\"digest\":\"(.*)\"";	        
	     Pattern p = Pattern.compile(reg); // 正则表达式
	     Matcher m = p.matcher(srcString); // 操作的字符串
	     while(m.find()) {
	         String g = m.group(1);
	         System.out.println("digest="+g);
	     }
	     String splitString = m.replaceAll("");
//	     System.out.println("srcStr="+srcString);
//	     System.out.println("spliet="+splitString);
	     
	     //verify
	     String digestString = hmacSign(splitString, rKey);
	     String verifyString1 = splitString.substring(0, splitString.length()-1);
	     String verifyString = verifyString1 + ",\"digest\":\""+ digestString+"\"}";
//	     System.out.println("verifyString="+verifyString);
	     
		 if(verifyString.equals(srcString))
		 {			 
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	}
	
	
	public static String AddDigest(String jsonstring, String digest) 
	{
		 String newjsonstring = jsonstring.substring(0, jsonstring.length()-1);
	     String returnjsonString = newjsonstring + ",\"digest\":\""+ digest+"\"}";
		return returnjsonString;
	}
	/**
	 * @param aValue
	 * @param aKey
	 * @return
	 */
	public static String hmacSign(String aValue, String aKey) {
//		System.out.println("aValue：" + aValue);
		System.out.println("aKey：" + aKey);
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = aKey.getBytes(encodingCharset);
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			keyb = aKey.getBytes();
			value = aValue.getBytes();
		}

		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {

			return null;
		}
		md.update(k_ipad);
		md.update(value);
		byte dg[] = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return toHex(dg);
	}

	public static String toHex(byte input[]) {
		if (input == null)
			return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}

	/**
	 * 
	 * @param args
	 * @param key
	 * @return
	 */
	public static String getHmac(String[] args, String key) {
		if (args == null || args.length == 0) {
			return (null);
		}
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			str.append(args[i]);
		}
		return (hmacSign(str.toString(), key));
	}

	/**
	 * @param aValue
	 * @return
	 */
	public static String digest(String aValue) {
		aValue = aValue.trim();
		byte value[];
		try {
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			value = aValue.getBytes();
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return toHex(md.digest(value));

	}	
		
	public static String getMD5(String val){
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes("UTF-8"));
			byte[] m = md5.digest();// 加密
			
			return toHex(m);
		} catch (Exception e) {
			LogUtil.e(e);
			return "";
		}
	}
	
}
