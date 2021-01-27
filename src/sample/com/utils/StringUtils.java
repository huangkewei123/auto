package sample.com.utils;

import sample.com.constants.RobotConstants;
import sample.com.exception.SubException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {

	public static String fmtMicrometer(String text) {
		String str = checkAmountNumber(text);
		DecimalFormat df = null;
		if (str.indexOf(".") > 0) {
			if (str.length() - str.indexOf(".") - 1 == 0) {
				df = new DecimalFormat("###,##0.");
			} else {
				df = new DecimalFormat("###,##0.00");
			}
		} else {
			df = new DecimalFormat("###,##0.00");
		}
		double number = 0.0;
		try {
			number = Double.parseDouble(text);
		} catch (Exception e) {
			number = 0.0;
		}
		return df.format(number);
	}

	public static BigDecimal str2BigDecimal(String value) {
		return new BigDecimal(value);
	}

	public static String bigDecimal2String(BigDecimal d) {
		if (d == null) {
			return "0.00";
		} else {
			return StringUtils.fmtMicrometer(d.toPlainString());
		}
	}
	
	public static String bigDecimal2NormalString(BigDecimal d) {
		if (d == null) {
			return "0";
		} else {
			return d.stripTrailingZeros().toPlainString();
		}
	}
	
	public static String bigDecimal2StringForIOS(BigDecimal amount) {
		if (amount == null){
			return "0万";
		} else {
			MathContext mc = new MathContext(6,RoundingMode.HALF_UP);
			BigDecimal result = amount.divide(new BigDecimal("10000"), mc);
			return result.toPlainString() +"万";
		}
	}

	public static String subStr2(BigDecimal value) {
		String v = checkPercentNumber(value);
		if (v.length() > 2) {
			return v.substring(0, v.length() - 2);
		}
		return v;
	}

	public static String subStr2(BigDecimal value, String unit) {
		String v = checkPercentNumber(value);
		if (v.length() > 2) {
			return v.substring(0, v.length() - 2) + unit;
		}
		return v + unit;
	}

	public static String checkAmountNumber(BigDecimal value) {
		if (value == null || "".equals(value.toPlainString())) {
			return "0.00";
		}
		return value.toPlainString();
	}
	
	public static BigDecimal checkBigDecimal(BigDecimal value) {
		if (value == null || "".equals(value.toPlainString())) {
			return new BigDecimal("0");
		}
		return value;
	}

	public static String checkAmountNumber(String value) {
		if (value == null || "".equals(value)) {
			return "0.00";
		}
		return value;
	}

	public static String checkPercentNumber(BigDecimal value) {
		if (value == null || "".equals(value.toPlainString())) {
			return "0.0000";
		}
		return value.toPlainString();
	}

	public static String checkNullAndEmpty(String src) {
		if (src == null || "".equals(src) ||"null".equals(src)) {
			return "";
		}
		return src;
	}
	
	public static boolean isEmpty(String src) {
		if (src == null || "".equals(src)) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String src){
		return !isEmpty(src);
	}

	/**
	 * 将字符编码转换成UTF-8码
	 */
//	public String toUTF_8(String str) throws UnsupportedEncodingException{
//		return this.changeCharset(str, "UTF-8");
//	}

	public static String getEncoding(String str) {
		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是GB2312
				String s = encode;
				return s; //是的话，返回“GB2312“，以下代码同理
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是ISO-8859-1
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是UTF-8
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) { //判断是不是GBK
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		return ""; //如果都不是，说明输入的内容不属于常见的编码格式。
	}
	/**
	 * 字符串编码转换的实现方法
	 * @param str 待转换编码的字符串
	 * @param oldCharset 原编码
	 * @param newCharset 目标编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String changeCharset(String str, String oldCharset, String newCharset)
			throws UnsupportedEncodingException {
		if (str != null) {
			//用旧的字符编码解码字符串。解码可能会出现异常。
			byte[] bs = str.getBytes(oldCharset);
			//用新的字符编码生成字符串
			return new String(bs, newCharset);
		}
		return null;
	}

	public static String toRate(BigDecimal decimal) {
		if (decimal == null || "".equals(decimal)) {
			return "";
		}
		BigDecimal multiplicand = new BigDecimal("100").multiply(decimal);
		String temp = new DecimalFormat("#0.00").format(multiplicand.doubleValue());
		return temp;
	}

	public static String toRateWithUnit(BigDecimal decimal) {
		if (decimal == null || "".equals(decimal)) {
			return "";
		}
		String multifarious = "%";
		BigDecimal multiplicand = new BigDecimal("100").multiply(decimal);
		String temp = new DecimalFormat("#0.00").format(multiplicand.doubleValue());
		return temp + multifarious;
	}
	
	public static String toPeriodWithDays(Integer period) {
		if (period == null || "".equals(period)) {
			return "";
		}
		String multifarious = "天";
		return period.toString() + multifarious;
	}
	
	public static String toAmountWithUnit(BigDecimal amount) {
		if (amount == null || "".equals(amount)) {
			return "";
		}
		return bigDecimal2String(amount) +"元";
	}

	public static String toRateAlias(BigDecimal decimal, String unit) {
		if (decimal == null || "".equals(decimal)) {
			return "";
		}
		String multifarious = "%";
		BigDecimal multiplicand = new BigDecimal("100");
		String result = decimal.multiply(multiplicand).toPlainString();
		int index = result.indexOf(".");
		String rate = "";
		if (index != -1) {
			rate = result.substring(0, index + 3);
		}
		return rate + multifarious + "/" + unit;

	}
	
	public static String toRateAlias(BigDecimal decimal) {
		if (decimal == null || "".equals(decimal)) {
			return "";
		}
		String multifarious = "%";
		BigDecimal multiplicand = new BigDecimal("100");
		String result = decimal.multiply(multiplicand).toPlainString();
		int index = result.indexOf(".");
		String rate = "";
		if (index != -1) {
			rate = result.substring(0, index + 3);
		}
		return rate + multifarious;

	}

	public static String toFlag(String flag) {
		if ("Y".equals(flag)) {
			return "是";
		} else if ("N".equals(flag)) {
			return "否";
		}
		return "";
	}

	public static String formatString(String text) {
		return text == null ? "" : text;
	}

	public static String maskString(String msg) {
		if (msg != null) {
			StringBuffer sb = new StringBuffer(msg.length());
			if (msg.length() < 3) {
				return msg;
			}else	{
				sb.append(msg.substring(0, 1));
				for (int i = 0; i < msg.length() - 2; i++) {
					sb.append("*");
				}
				sb.append(msg.substring(msg.length() - 1, msg.length()));
				return sb.toString();
			}
		} else {
			return "";
		}
	}
	
	
	public static String maskIdNo(String idNo) {
		if ((idNo == null) || (idNo.length()<8)) {
			throw new RuntimeException("Invaid id no.");
		}
			
		String prefix = idNo.substring(0, 3);
		String postfix = idNo.substring(idNo.length()-3);
		
		return prefix + "************" + postfix;
	}
	
	public static String maskName(String name) {
		String subStr = "";
		if ((name == null) || (name.length() == 0)) {
			return subStr;
		}
		
		if(name.length() == 2){
			subStr = name.substring(0, 1) + "*";
		}else if(name.length() == 3){
			subStr = name.substring(0, 1) + "**";
		}else{
			subStr = name.substring(0, 1) + "***";
		}
			
		return subStr;
	}
	
	public static String maskBankAccountNo(String bankAccountNo) {
		if ((bankAccountNo == null) || (bankAccountNo.length()<10)) {
			throw new RuntimeException("Invaid bank account no.");
		}
			
		String prefix = bankAccountNo.substring(0, 6);
		String postfix = bankAccountNo.substring(bankAccountNo.length()-4);
		
		return prefix + "************" + postfix;
	}

	 public static  boolean regexLoginName(String loginName)  
	  {  
	   loginName =  loginName.trim();
	   boolean flag=loginName.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$");  
	   return  flag;  
	  }  
	 
	 public static boolean regexMobile(String mobile){
		 if(null == mobile || "".equals(mobile) || mobile.length() < 11){
			 return false;
		 }else{
			 String regExp = "^0?(13|15|18|14|17|16|19)[0-9]{1}[0-9]{8}$";
			 //boolean flag=mobile.matches("^[0-9]*$");
			 boolean flag = mobile.matches(regExp);
			 return flag;
		 }
	 }
	 public static boolean regexRecommenderCode(String recommenderCode){
		return recommenderCode.matches("^[A-Z]{4}[0-9]{4}$");
	 }
	 public static boolean isNumeric(String str){  
	    Pattern pattern = Pattern.compile("[0-9]*");  
	    return pattern.matcher(str).matches();     
	 }  
	 public static boolean regexLong(String str){
		return str.matches("[0-9]+");
	 }
	 public static boolean regexIdNo(String idNo){
		 Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");  
         Matcher idNumMatcher = idNumPattern.matcher(idNo);  
         boolean flag=idNumMatcher.matches();
         return flag;
	 }
	 
	 public static  boolean regexEmail(String email){  
	   boolean flag=email.matches("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");  
	   return  flag;  
	  }

	 public static String maskAddr(String ddr) {
		 
		 String subStr = "";
		if ((ddr == null) || (ddr.length() == 0)) {
			return subStr;
		}
		
		if(ddr.length() <= 6){
			subStr = ddr.substring(0, 3) + "****" ;
		}else{
			subStr = ddr.substring(0, 6) + "******" ;
		}
			
		return subStr;
	 }
	 
	 public static String maskMobile(String mobile) {
		 String subMobile = "";
		 if(null == mobile || "".equals(mobile) || mobile.length() < 11){
			 return "";
		 }else{
			 subMobile = mobile.substring(0, 3)  + "****" + mobile.substring(7, 11);
		 }
		 
		 return subMobile;
	 }
	 
	 public static String maskProjectName(String projectName) {
		 if(null == projectName || "".equals(projectName)){
			 return "";
		 }
		 
		 if(projectName.length() <= 15){
			 return projectName;
		 }else{
			 String maskName = projectName.substring(0, 15);
			 return maskName + "...";
		 }
	 }
	 
	/**
	 * 取出一对标签之间的值
	 * @param name  标签名
	 * @return
	 */
	public static String getTagValue(String name, String xml) {
		String start = "<" + name + ">";
		String end = "</" + name + ">";
		String value = xml.substring(xml.indexOf(start) + start.length(), xml.indexOf(end));
		return value;
	}

	/**
	 * 取出一对标签值(包括那对标签符)
	 * @param name 标签名
	 * @return
	 */
	public static String getTagValueWithTag(String name, String xml) {
		String start = "<" + name + ">";
		String end = "</" + name + ">";
		String value = xml.substring(xml.indexOf(start), xml.indexOf(end) + end.length());
		return value;
	}

	/**
	 * 取出一对符号之间的值
	 * @param param 值（包含括号）
	 * @param startTag 起始符号
	 * @param endTag 结束符号
	 * @return
	 */
	public static String getParamValueWithTag(String param ,String startTag , String endTag) {
		String value = param.substring(param.indexOf(startTag), param.lastIndexOf(endTag) + endTag.length());
		return value;
	}

	/**
	 * 取出一对符号之间的值
	 * @param param 值
	 * @param startTag 起始符号
	 * @param endTag 结束符号
	 * @return
	 */
	public static String getParamValue(String param ,String startTag , String endTag) {
		String value = param.substring(param.indexOf(startTag) + startTag.length(), param.lastIndexOf(endTag));
		return value;
	}

	 public static String getUtf8EscapedString(String input) {
	        String result = null;
	        try {
	            result = URLEncoder.encode(input, "utf8");
	        } catch (UnsupportedEncodingException e) {
	            return null;
	        }
	        return result;
	    }
	 
	 
	public static String toRatePercentage(BigDecimal decimal) {
		if (decimal == null || "".equals(decimal)) {
			return "";
		}
		String temp = new DecimalFormat("#0.00").format(decimal.doubleValue());
		return temp;

	}

	/**
	 * decimal 除以 100
	 * @param decimal
	 * @return
	 */
	public static BigDecimal toRateDivide(BigDecimal decimal) {
		if (decimal == null || "".equals(decimal)) {
			return null;
		}
		MathContext mc = new MathContext(2,RoundingMode.HALF_UP);
		BigDecimal result = decimal.divide(new BigDecimal("100"), mc);
		return result;

	}

	/**
	 * 将日期转为YYYY年MM月DD日
	 * @param dateStr	日期字符串
	 * @return
	 */
	public static String[] dateFormat(String dateStr) throws SubException {
		if(isEmpty(dateStr)){
			LoggerUtils.error(StringUtils.class , "日期为空，请确认");
			throw new SubException("日期为空，请确认");
		}
		String []dateStrArr = null;
		try {
			dateStrArr = dateStr.split("-");
		} catch (Exception e) {
			LoggerUtils.error(StringUtils.class , "日期格式转换失败，请确认输入的值是否正确，输入值为：" + dateStr);
			throw new SubException("日期格式转换失败，请确认输入的值是否正确，输入值为：" + dateStr , e);
		}
		return dateStrArr;
	}

	/**
	 * 返回变量转换后的最终值
	 * @param str		脚本中的原始变量
	 * @param dataMap	表头所对应的数据
	 * @return
	 */
	public static String variableChange(String str, Map<String , String> dataMap) throws SubException {
		String variable = null;
		if(str.contains(RobotConstants.VAR_START_TAG)){
			int x = str.indexOf(RobotConstants.VAR_START_TAG) + RobotConstants.VAR_START_TAG.length();
			int y = str.indexOf(RobotConstants.VAR_END_TAG);
			try {
				variable = str.substring(x,y);
				str = str.replace(str.substring(str.indexOf(RobotConstants.VAR_START_TAG),y + RobotConstants.VAR_END_TAG.length()),dataMap.get(variable));
				str = variableChange(str  , dataMap);
			} catch (NullPointerException e) {
				throw new SubException("您填写的变量不存在，请仔细查阅表头是否包含此变量:{{" + variable + "}}。\n");
			}
		}
		return str;
	}

	private static SimpleDateFormat sf = null;

	// 将时间戳转成字符串
	public static String getDateToString(long time) {
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}

	//获取当前时间
	public static String getCurrentDate() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sf.format(d);
	}

	//将字符串转换为时间戳
	public static long getStringToDate(String time) {
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	//直接获取当前时间戳
	public static String getTimeStamp() {
		String currentDate = getCurrentDate();
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = sf.parse(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return String.valueOf(date.getTime());
	}

	/**
	 * 返回时间戳，精确到毫秒
	 * @return
	 */
	public static String getTimeStampms(){
		sf = new SimpleDateFormat("yyyyMMddHHmmssms");
		Timestamp date = new Timestamp(System.currentTimeMillis());
		return sf.format(date);
	}

	public static void main(String[] args) {
		//直接获取当前时间戳
		String a = "ceshi(test(1));";
		String t = getParamValue(a,"(",")");
		System.out.println(t);
	}

}
