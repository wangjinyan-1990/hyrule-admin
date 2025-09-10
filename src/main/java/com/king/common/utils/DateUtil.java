package com.king.common.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate) throws ParseException {    
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
        smdate = sdf.parse(sdf.format(smdate));  
        bdate = sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days = (time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }  
    
    /**date函数
     * 传入类似： date(‘YYYYMMDD’)+1，支持各种标准写法yyyyMMddHHmmss等,返回最后计算出的日期字符串
     * @param dateString
     * @return
     */
    public static String dateCount(String dateString){
  	   int beginIndex = 6;
  	   int endIndex = dateString.lastIndexOf(")");
  	   int addOrReduceIndex = 0;
  	   if(dateString.contains("+")){
  		   addOrReduceIndex = dateString.lastIndexOf("+");
  	   }
  	   if(dateString.contains("-")){
  		   addOrReduceIndex =  dateString.lastIndexOf("-");
  		}
  	   String addOrReduce = dateString.substring(addOrReduceIndex);
  	   String dateFormatStrig = dateString.substring(beginIndex, endIndex-1);
  	   return getDateString(dateFormatStrig,Integer.valueOf(addOrReduce));
    }
    
    public static String getDateString(String dateFormatStrig , int addOrReduce){
  	  Calendar cal = Calendar.getInstance();
  	  Date date = new Date();
  	  cal.setTime(date);
  	  cal.add(Calendar.DATE, addOrReduce);
  	  return new SimpleDateFormat(dateFormatStrig).format(cal.getTime());
    }

	public static String getDateFormatYMD() {
		// 获取当前日期
		Date currentDate = new Date();
		// 定义日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// 将日期格式化为指定格式的字符串
		String formattedDate = sdf.format(currentDate);
		return  formattedDate;
	}

	public static String getDateFormatYMDHMS() {
		// 获取当前日期
		Date currentDate = new Date();
		// 定义日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 将日期格式化为指定格式的字符串
		String formattedDate = sdf.format(currentDate);
		return  formattedDate;
	}

}
