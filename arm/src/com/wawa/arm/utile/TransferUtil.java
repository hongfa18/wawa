package com.wawa.arm.utile;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * 工具�?
 * 
 * @corporation SIMS
 * @author cy_bruce
 * @date 2011-5-11 上午10:26:06
 * @path com.zte.ecard.washcard.operate.util
 * @description
 */
public class TransferUtil {

    /**
     * Short to byte数组
     * 
     * @param s
     *            short
     * 
     * @return byte[]
     */
    public static byte[] int2bytes(int s) {
        byte[] bytes = new byte[2];
        for (int i = 1; i >= 0; i--) {
            bytes[i] = (byte) (s % 256);
            s >>= 8;
        }
        return bytes;
    }

    /**
     * byte数组转换为整�?
     * 
     * @param b
     *            b
     * @return int
     */
    public static int byteArrayToInt(byte[] b) {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            if (b[i] >= 0) {
                s = s + b[i];
            } else {
                s = s + 256 + b[i];
            }
            s = s * 256;
        }
        if (b[3] >= 0) {
            s = s + b[3];
        } else {
            s = s + 256 + b[3];
        }
        return s;
    }

    /**
     * byte数组转换为短整型
     * 
     * @param b
     *            b
     * @return short
     */
    public static short byteArrayToShort(byte[] b) {
        return (short) ((b[0] << 8) + (b[1] & 0xFF));
    }

    /**
     * 长整型转四字节数�?
     * 
     * @param value
     *            v
     * @return b v
     */
    public static byte[] longTo4Bytes(long value) {

        byte[] bb = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        bb[0] = (byte) ((value >>> 24) & 0xFF);
        bb[1] = (byte) ((value >>> 16) & 0xFF);
        bb[2] = (byte) ((value >>> 8) & 0xFF);
        bb[3] = (byte) (value & 0xFF);
        return bb;
    }

    /**
     * 将字节数组num组装成long型的数字
     * 
     * @param num
     *            num得长度不能大�?
     * @return long型数�?
     */
    public static long byteArray2Long(byte[] num) {
        if (num == null || num.length > 8) {
            return 0;
        }
        long result = 0;
        for (int i = num.length - 1; i >= 0; i--) {
            result = result | ((0xffl & num[i]) << ((num.length - 1 - i) * 8));
        }
        return result;
    }

    /**
     * 字节数组转整型数�?
     * 
     * @param bcds
     *            byte[]
     * @return short[]
     */
    public static short[] parseByteArrayToShort(byte[] bcds) {
        short[] s = null;
        s = new short[bcds.length];
        for (int i = 0; i < bcds.length; i++) {
            s[i] = bcds[i];
            if (s[i] < 0) {
                s[i] += 256;
            }
        }
        return s;
    }

    /**
     * 整型数组转字节数�?
     * 
     * @param bcds
     *            short[]
     * @return byte[]
     */
    public static byte[] parseShortArrayToByte(short[] bcds) {
        if (bcds == null || bcds.length == 0) {
            return new byte[0];
        }
        byte[] byteBcd = new byte[bcds.length];
        for (int i = 0; i < bcds.length; i++) {
            byteBcd[i] = (byte) bcds[i];
        }
        return byteBcd;
    }

    /**
     * 16进制字符串转bcd
     * 
     * @param src
     *            String
     * @return int bcd
     */
    public static int string2bcd(String src) {
        return Integer.parseInt(src, 16);
    }

    /**
     * 不足则前面补0，超过长度则截取�?��边的
     * 
     * @param str
     *            str
     * @param len
     *            len
     * @return str
     */
    public static String fillStrWith_0_Start(final String str, int len) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append('0');
        }
        if (str == null || str.length() == 0) {
            return sb.toString();
        }
        if (str.length() < len) {
            return sb.toString().substring(str.length(), len) + str;
        } else {
            return str.substring(str.length() - len, str.length());
        }
    }

    /**
     * 不足则后面补0，超过长度则截取�?��边的
     * 
     * @param str
     *            str
     * @param len
     *            len
     * @return str
     */
    public static String fillStrWith_0_End(final String str, int len) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append('0');
        }
        if (str == null || str.length() == 0) {
            return sb.toString();
        }
        if (str.length() < len) {
            return str + sb.toString().substring(str.length(), len);
        } else {
            return str.substring(str.length() - len, str.length());
        }
    }

    /**
     * 不足则后面补F，超过长度则截取�?��边的
     * 
     * @param str
     *            str
     * @param len
     *            len
     * @return str
     */
    public static String fillStrWith_F_End(final String str, int len) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; i++) {
            sb.append('F');
        }
        if (str == null || str.length() == 0) {
            return sb.toString();
        }
        if (str.length() < len) {
            return str + sb.toString().substring(str.length(), len);
        } else {
            return str.substring(str.length() - len, str.length());
        }
    }

    /**
     * 字节转字符串
     * 
     * @param a
     *            s
     * @return s
     */
    public static String byteToStr(byte[] a) {
        StringBuffer sb = new StringBuffer("");
        if (a == null || a.length == 0) {
            return "";
        }
        for (int i = 0; i < a.length; i++) {
            sb.append(Integer.toHexString((a[i] >>> 4) & 0x0F).toUpperCase());
            sb.append(Integer.toHexString(a[i] & 0x0F).toUpperCase());
        }
        return sb.toString();
    }

    /**
     * bytes转换成十六进制字符串
     * 
     * @param b
     *            byte[]
     * @return String
     */
    public static String byte2HexStr(byte[] b) {

        if (b == null || b.length == 0) {
            return "";
        }

        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    /**
     * 16进制的数字字符串转为bcd
     * 
     * @param src
     *            String
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {

        if (src == null || src.length() == 0) {
            return new byte[0];
        }

        int m = 0;
        int n = 0;
        int l = src.length() / 2;
        // LogUtil.i(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    /**
     * 整合两个字符串为�?��字节
     * 
     * @param src0
     *            String
     * @param src1
     *            String
     * @return byte
     */
    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    // 日期相关转换
    /**
     * 将yyyy-MM-dd HH:mm:ss转换为yyyy.MM.dd HH:mm:ss
     * 
     * @param olddate
     *            olddate
     * @return String String
     * 
     */
    public static String transferDate(String olddate) {
        StringTokenizer token = new StringTokenizer(olddate, "-");
        StringBuffer strbuf = new StringBuffer();
        String year = token.nextToken();
        strbuf.append(year).append(".");
        String month = token.nextToken();
        if (month.length() == 1) {
            strbuf.append("0");
        }
        strbuf.append(month).append(".");
        String day = token.nextToken();
        if (day.length() == 1) {
            strbuf.append("0");
        }
        strbuf.append(day);
        return strbuf.toString();
    }

    /**
     * 将yyyy-MM-dd 转换为yyyyMMdd
     * 
     * @param olddate
     *            String
     * @return String
     */
    public static String transferDateformat(String olddate) {
        StringTokenizer token = new StringTokenizer(olddate, "-");
        StringBuffer strbuf = new StringBuffer();
        String year = token.nextToken();
        strbuf.append(year).append("");
        String month = token.nextToken();
        if (month.length() == 1) {
            strbuf.append("0");
        }
        strbuf.append(month).append("");
        String day = token.nextToken();
        if (day.length() == 1) {
            strbuf.append("0");
        }
        strbuf.append(day);
        return strbuf.toString();
    }

    /**
     * 
     * @param out
     *            out
     * @param value
     *            value
     * @param len
     *            len
     */
    public static void writeString(DataOutputStream out, String value, int len) throws java.io.IOException {
        byte[] data = new byte[len];
        java.util.Arrays.fill(data, (byte) 0);
        if (value != null && value.length() > 0) {
            byte[] temp = value.getBytes("GBK");
            int availableLength = temp.length <= len ? temp.length : len;
            System.arraycopy(temp, 0, data, 0, availableLength);
        }
        out.write(data);
    }

    /**
     * 获取当前时间 yyyy.MM.dd HH:mm:ss
     * 
     * @return String
     */
    public static String getNowTime() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获取yyyyMMddHHmmss格式的当前时�?
     * 
     * @return String
     */
    public static String getNowTimeFormat() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式的当前时�?
     * 
     * @return String
     */
    public static String getNowDatetimeFormat() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(now);
    }

    /**
     * String 转成 DateTime10格式
     * 
     * @param s
     *            String
     * @return Date
     */
    public static Date transformDate(String s) {

        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            d = format.parse(s);

            return d;
        } catch (Exception e) {
            return d;
        }
    }

    /**
     * String 转成 Date
     * 
     * @param date
     *            date String
     * @return Date
     */
    public static Date transformDateSimple(String date) {
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            d = format.parse(date);
            return d;
        } catch (Exception e) {
            return d;
        }
    }

    /**
     * 获取当前时间
     * 
     * @return String String
     */
    public static String getNowAllTime() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssssss", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获取当前日期
     * 
     * @param formate
     *            String
     * @return String String
     */
    public static String getNowDateFormate(String formate) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat(formate, Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获取当前日期,格式为yyyyMMdd
     * 
     * @return String 返回当前日期
     */
    public static String getNowDate() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获取日期
     * 
     * @param datetime14
     *            String格式yyyyMMddHHMMSS
     * @return string 日期格式yyyy-mm-dd
     */
    public static String get10DateBy14DateTime(String datetime14) {
        StringBuffer sb = new StringBuffer();
        sb.append(datetime14.substring(0, 4)).append("-");
        sb.append(datetime14.substring(4, 6)).append("-");
        sb.append(datetime14.substring(6, 8));
        return sb.toString();
    }

    /**
     * 获取时间
     * 
     * @param datetime14
     *            String格式yyyyMMddHHMMSS
     * @return string 日期格式HH:MM:SS
     */
    public static String getTimeBy14DateTime(String datetime14) {
        StringBuffer sb = new StringBuffer();
        sb.append(datetime14.substring(8, 10)).append(":").append(datetime14.substring(10, 12)).append(":")
                .append(datetime14.substring(12, 14));
        return sb.toString();
    }

    /**
     * 获取日期时间
     * 
     * @param datetime14
     *            String格式yyyyMMddHHMMSS
     * @return string 日期格式yyyy-mm-dd hh:mm:ss
     */
    public static String get10DateTimeBy14DateTime(String datetime14) {
        StringBuffer sb = new StringBuffer();
        sb.append(get10DateBy14DateTime(datetime14)).append(" ").append(getTimeBy14DateTime(datetime14));
        return sb.toString();
    }

    /**
     * 获取当前年月,格式为yyyyMM
     * 
     * @return String 当前年月
     */
    public static String getNowMONTH() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获取当前�?格式为yyyy
     * 
     * @return String 返回�?
     */
    public static String getNowYear() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 获得当前日期的月份�?格式为MM �?1�?2�?3�?4�?5�?6�?7�?8�?9�?0�?1�?2
     * 
     * @return int 月份
     */
    public static String getNowMonth() {
        int month = 1 + Calendar.getInstance().get(Calendar.MONTH);
        String mon = "";
        if (month < 10) {
            mon = "0" + month;
        } else {
            mon = month + "";
        }
        return mon;
    }

    /**
     * 
     * @param date
     *            date
     * @return string
     */
    public static String date2String(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format1.format(d).substring(0, 10).trim();
    }

    /**
     * 根据传入的日期，转换成相应格式的字符串输入出
     * 
     * @param date
     *            Date
     * @param pattern
     *            String
     * @return String
     */
    public static String getStringFromDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String str = sdf.format(date);
        return str;
    }

    /**
     * 根据传入的时间字符串，转换成特定的时间格式字符串输出
     * 
     * @param dateString
     *            日期字符�?
     * @param origPattern
     *            源字符串格式
     * @param destPattern
     *            目标字符串格�?
     * @return String
     */
    public static String getFormatString(String dateString, String origPattern, String destPattern) {
        // 根据提供的源字符串格式转换成时间类型
        SimpleDateFormat sdf = new SimpleDateFormat(origPattern);
        // 创建输出的字符串格式对象
        SimpleDateFormat sdf2 = new SimpleDateFormat(destPattern);
        String destString = "";
        try {
            destString = sdf2.format(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return destString;
    }

    /**
     * 转bcd格式为yyyy-mm-dd hh:mm:ss
     * 
     * @param bcd
     *            string
     * @return string
     */
    public static String bcd2DateString(String bcd) {
        // bcd = "20101001021011";
        StringBuffer sb = new StringBuffer();
        return sb.append(bcd.substring(0, 4)).append("-").append(bcd.substring(4, 6)).append("-")
                .append(bcd.substring(6, 8)).append(" ").append(bcd.substring(8, 10)).append(":")
                .append(bcd.substring(10, 12)).append(":").append(bcd.substring(12, 14)).toString();
    }

    /**
     * 转bcd格式为yyyy-mm-dd
     * 
     * @param bcd
     *            string
     * @return string
     */
    public static String bcd2DateString2(String bcd) {
        // bcd = "20101001021011";
        StringBuffer sb = new StringBuffer();
        return sb.append(bcd.substring(0, 4)).append("-").append(bcd.substring(4, 6)).append("-")
                .append(bcd.substring(6, 8)).toString();
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式的时�?
     * 
     * @param now
     *            date
     * @return String
     */
    public static String getDate2String(Date now) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(now);
    }

    /**
     * 计算心跳中的超时时间
     * 
     * @param heart
     *            Integer 心跳间隔
     * @param currenttimemil
     *            long 当前时间
     * @return 超时时间 String
     */
    public static String calcTimeOutByHeartCurrent(Integer heart, long currenttimemil) {
        Date timeout = new Date(Long.valueOf(heart.toString()) + currenttimemil);
        return getDate2String(timeout);
    }

    /**
     * 时间转换
     * 
     * @param currenttimemil
     *            long
     * @return 时间字符�?
     */
    public static String transformLong2String(long currenttimemil) {
        Date date = new Date(currenttimemil);
        return date2String(date);
    }

    /**
     * 时间转字符串格式
     * 
     * @param date
     *            Date
     * @return String
     */
    public static String date2String(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 获取yyyyMMddHHmmss格式的当前时�?
     * 
     * @param date
     *            Date
     * @return String
     */
    public static String getNowTimeFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 判断命令返回是否正常
     * 
     * @param b
     *            b
     * @return b
     */
    public static boolean isSucessResult(byte[] b) {
        if (b == null) {
            return false;
        }
        String resultString = TransferUtil.byte2HexStr(b);
        boolean b1 = false;
        if (resultString.length() >= 4) {
            String result61 = "";
            result61 = resultString.substring(resultString.length() - 4, resultString.length() - 2);
            b1 = result61.equals("61");
        }
        return resultString.endsWith("9000") || b1;
    }

    /**
     * 响应�?
     * 
     * @param sw
     *            sw
     * @return s
     */
    public static String getRecord(byte[] sw) {
        if (sw == null) {
            return "";
        }
        String rs = TransferUtil.byte2HexStr(sw);
        int len = rs.length();
        if (len < 4) {
            return rs;
        }
        rs = rs.substring(rs.length() - 4, rs.length());
        return rs;
    }

    /**
     * 左补0已达到TotalLen长度
     * 
     * @param s
     *            s
     * @param totalLen
     *            totalLen
     * @return string
     */
    public static final String leftAddZero(String s, int totalLen) {
        String sTemp = "";
        int l = s.length();
        if (l >= totalLen) {
            return s;
        } else {
            int j = totalLen - l;

            for (int i = 0; i < j; i++) {
                sTemp += "0";
            }
            sTemp += s;
        }
        return sTemp;
    }

    /**
     * Short to byte数组
     * 
     * @param s
     *            short
     * 
     * @return byte[]
     */
    public static byte[] short2bytes(short s) {
        byte[] bytes = new byte[2];
        for (int i = 1; i >= 0; i--) {
            bytes[i] = (byte) (s % 256);
            s >>= 8;
        }
        return bytes;
    }

    /**
     * 16进制的数字字符串转为char数组
     * 
     * @description
     * @date 2011-7-12
     * @author cy_bruce
     * @param src s
     * @return c
     */
    public static char[] hexStr2Chars(String src) {
        if (src == null || src.length() == 0) {
            return new char[0];
        }
        int m = 0;
        int n = 0;
        int l = src.length() / 2;
        // LogUtil.i(l);
        char[] ret = new char[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = (char) uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    /**
     * 转换short为低字节优先
     * 
     * @param s
     *            short
     * @return short
     */
    public static short transToLittleShort(short s) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(s);
        byte[] b = buffer.array();
        return TransferUtil.byteArrayToShort(b);
    }

    /**
     * 汉军的时间转化，将byte[]转换�?9位的字符�?
     * 
     * @param b
     *            byte[]
     * @return String
     */
    public static String hundureTransDateBytes2String(byte[] b) {
        return null;
    }

    /**
     * 汉军的时间转化，�?9位的字符串转换为byte[]
     * 
     * @param s
     *            String
     * @return byte[]
     */
    public static byte[] hundureTransDateString2Bytes(String s) {
        return null;
    }

    /**
     * 字节序转�?
     * 
     * @param src
     *            byte[]
     * @return byte[]
     */
    public static byte[] transByteOrder(byte[] src) {
        int len = src.length;
        byte[] dest = new byte[len];
        for (int i = 0; i < len; i++) {
            dest[len - i - 1] = src[i];
        }
        return dest;
    }

    /**
     * 十六进制转换字符�?ASCII 31323334 转换后为�?234
     * 
     * @param hexStr
     *            String
     * @return String
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);

    }

    /**
     * 字符串转换成十六进制字符�?ASCII 1234 转换后为�?1323334
     * 
     * @param str
     *            String
     * @return String
     */

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();

    }

    /**
     * 获取当前年的后两�?
     * 
     * @return String current year
     */
    public static String getYear() {
        GregorianCalendar c = new GregorianCalendar();
        return String.valueOf(c.get(GregorianCalendar.YEAR)).substring(2, 4);
    }

    /**
     * 获取当前�?
     * 
     * @return String current month
     */
    public static String getMonth() {
        GregorianCalendar c = new GregorianCalendar();
        return String.valueOf(c.get(GregorianCalendar.MONTH) + 1);
    }

    /**
     * 获取当前月的当前�?
     * 
     * @return String current day of month
     */
    public static String getDayOfMonth() {
        GregorianCalendar c = new GregorianCalendar();
        return String.valueOf(c.get(GregorianCalendar.DAY_OF_MONTH));
    }

    /**
     * 获取当前天是星期�?
     * 
     * @return String day of week
     */
    public static String getDayOfWeek() {
        GregorianCalendar c = new GregorianCalendar();
        String w = String.valueOf(c.get(GregorianCalendar.DAY_OF_WEEK));
        if (w.equals("0")) {
            return "7";
        }
        return w;
    }

    /**
     * 获取当前小时
     * 
     * @return String hour of day
     */
    public static String getHour() {
        GregorianCalendar c = new GregorianCalendar();
        return String.valueOf(c.get(GregorianCalendar.HOUR_OF_DAY));
    }

    /**
     * 获取当前时间的分�?
     * 
     * @return String
     */
    public static String getMin() {
        GregorianCalendar c = new GregorianCalendar();
        return String.valueOf(c.get(GregorianCalendar.MINUTE));
    }

    /**
     * 获取当前时间的秒
     * 
     * @return String
     */
    public static String getSec() {
        GregorianCalendar c = new GregorianCalendar();
        return String.valueOf(c.get(GregorianCalendar.SECOND));
    }

    /**
     * 把字符串转换成ASCII码串
     * 
     * @param head
     *            h
     * @param src
     *            s
     * @return s
     */
    public static String ascii_src16(int head, String src) {
        String hex = TransferUtil.str2HexStr(src);
        int zero = 32 - hex.length() - head;
        if (zero != 0) {
            StringBuilder sb = new StringBuilder(hex);
            for (int i = 0; i < zero; i++) {
                sb.append("0");
            }
            return sb.toString();
        }
        return hex;
    }

    /**
     * 中卡算取crc�?
     * 
     * @param date
     *            d
     * @return b
     */
    public static byte xor(byte[] date) {
        byte b = date[0];
        for (int i = 1; i < date.length; i++) {
            b = (byte) (b ^ date[i]);
        }
        return b;
    }

    /**
     * 按位取反
     * 
     * @param src
     *            s
     * @return b
     */
    public static byte[] bitNot(final byte[] src) {
        if (src == null) {
            return null;
        }
        byte[] des = new byte[src.length];
        for (int i = 0; i < src.length; i++) {
            des[i] = (byte) (~src[i] & 0xFF);
        }
        return des;
    }

    /**
     * 转换卡号
     * 将M1卡转换为10位长的数�?
     * @param cardid
     * @return
     */
    public static String transCardid(String cardid) {
        byte[] switchCardidByte = TransferUtil.transByteOrder(TransferUtil.hexStr2Bytes(cardid));
        String tmpcardseq = TransferUtil.fillStrWith_0_Start(String.valueOf(Long.parseLong(TransferUtil.byte2HexStr(switchCardidByte),16)), 10);
        return tmpcardseq;
    }
    
    /**
     * 把字符串首字母转换成大写字母
     * @param str
     * @return
     */
    public static String firstLetter2Upper(String str){
   	if (str!=null && !"".equals(str)) {
   	    str=str.toLowerCase();
   	    String firstLetter=str.substring(0,1);
   	    firstLetter=firstLetter.toUpperCase();
   	    str=firstLetter+str.substring(1);
   	}
   	return str;
       }
    
    public static void main(String[] args) {
	}
}
