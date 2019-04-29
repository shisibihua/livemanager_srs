package com.honghe.livemanager.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/** 
 * 日期时间工具类 
 *  
 * @author zhaowj
 *  
 */  
public class DateUtil {


 SimpleDateFormat timeShortFormat = new SimpleDateFormat(
    		"HHmmss");

    private DateUtil(){}

    static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    /**
     * 获得当前日期时间 
     * <p> 
     * 日期时间格式yyyy-MM-dd HH:mm:ss 
     *  
     * @return 
     */  
    public static String currentDatetime() {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return datetimeFormat.format(now());
    }  
  
    /** 
     * 格式化日期时间 
     * <p> 
     * 日期时间格式yyyy-MM-dd HH:mm:ss 
     *  
     * @return 
     */  
    public static String formatDatetime(Date date) {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return datetimeFormat.format(date);  
    }  
  
    /** 
     * 格式化日期时间 
     *  
     * @param date 
     * @param pattern 
     *            格式化模式，详见{@link SimpleDateFormat}构造器
     *            <code>SimpleDateFormat(String pattern)</code>
     * @return
     */
    public static String formatDatetime(Date date, String pattern) {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat customFormat = (SimpleDateFormat) datetimeFormat
                .clone();
        customFormat.applyPattern(pattern);
        return customFormat.format(date);
    }

    /**
     * 获得当前日期
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        return dateFormat.format(now());
    }

    /**
     * 获得当前日期 (短)
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String currentShortDate() {
        SimpleDateFormat dateShortFormat = new SimpleDateFormat(
                "yyyyMMdd");
        return dateShortFormat.format(now());
    }


    /**
     * 格式化日期
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 获得当前时间
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String currentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "HH:mm:ss");
        return timeFormat.format(now());
    }


    /**
     * 获得当前时间 (短)
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String currentShortTime() {
        SimpleDateFormat timeShortFormat = new SimpleDateFormat(
                "HHmmss");
        return timeShortFormat.format(now());
    }


    /**
     * 获得当前日期时间 (短)
     * <p>
     * 日期格式yyyy-MM-dd
     *
     * @return
     */
    public static String currentShortDateTime() {
        SimpleDateFormat datetimeShortFormat = new SimpleDateFormat(
                "yyyyMMddHHmmss");
        return datetimeShortFormat.format(now());
    }

    /**
     * 格式化时间
     * <p>
     * 时间格式HH:mm:ss
     *
     * @return
     */
    public static String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "HH:mm:ss");
        return timeFormat.format(date);
    }

    public static String formatShortTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "HH:mm");
        return timeFormat.format(date);
    }

    /**
     * 获得当前时间的<code>java.util.Date</code>对象
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    public static Calendar calendar() {
        Calendar cal = GregorianCalendar.getInstance(Locale.CHINESE);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        return cal;
    }

    /**
     * 获得当前时间的毫秒数
     * <p>
     * 详见{@link System#currentTimeMillis()}
     *
     * @return
     */
    public static long millis() {
        return System.currentTimeMillis();
    }

    /**
     *
     * 获得当前Chinese月份
     *
     * @return
     */
    public static int month() {
        return calendar().get(Calendar.MONTH) + 1;
    }

    /**
     * 获得月份中的第几天
     *
     * @return
     */
    public static int dayOfMonth() {
        return calendar().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 今天是星期的第几天
     *
     * @return
     */
    public static int dayOfWeek() {
        return calendar().get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 今天是年中的第几天
     *
     * @return
     */
    public static int dayOfYear() {
        return calendar().get(Calendar.DAY_OF_YEAR);
    }

    /**
     *判断原日期是否在目标日期之前
     *
     * @param src
     * @param dst
     * @return
     */
    public static boolean isBefore(Date src, Date dst) {
        return src.before(dst);
    }

    /**
     *判断原日期是否在目标日期之后
     *
     * @param src
     * @param dst
     * @return
     */
    public static boolean isAfter(Date src, Date dst) {
        return src.after(dst);
    }

    /**
     *判断两日期是否相同
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isEqual(Date date1, Date date2) {
        return date1.compareTo(date2) == 0;
    }

    /**
     * 判断某个日期是否在某个日期范围
     *
     * @param beginDate
     *            日期范围开始
     * @param endDate
     *            日期范围结束
     * @param src
     *            需要判断的日期
     * @return
     */
    public static boolean between(Date beginDate, Date endDate, Date src) {
        return beginDate.before(src) && endDate.after(src);
    }

    /**
     * 获得当前月的最后一天
     * <p>
     * HH:mm:ss为0，毫秒为999
     *
     * @return
     */
    public static Date lastDayOfMonth() {
        Calendar cal = calendar();
        cal.set(Calendar.DAY_OF_MONTH, 0); // M月置零
        cal.set(Calendar.HOUR_OF_DAY, 0);// H置零
        cal.set(Calendar.MINUTE, 0);// m置零
        cal.set(Calendar.SECOND, 0);// s置零
        cal.set(Calendar.MILLISECOND, 0);// S置零
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);// 月份+1
        cal.set(Calendar.MILLISECOND, -1);// 毫秒-1
        return cal.getTime();
    }

    /**
     * 获得当前月的第一天
     * <p>
     * HH:mm:ss SS为零
     *
     * @return
     */
    public static Date firstDayOfMonth() {
        Calendar cal = calendar();
        cal.set(Calendar.DAY_OF_MONTH, 1); // M月置1
        cal.set(Calendar.HOUR_OF_DAY, 0);// H置零
        cal.set(Calendar.MINUTE, 0);// m置零
        cal.set(Calendar.SECOND, 0);// s置零
        cal.set(Calendar.MILLISECOND, 0);// S置零
        return cal.getTime();
    }

    private static Date weekDay(int week) {
        Calendar cal = calendar();
        cal.set(Calendar.DAY_OF_WEEK, week);
        return cal.getTime();
    }

    /**
     * 获得周五日期
     * <p>
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     *
     * @return
     */
    public static Date friday() {
        return weekDay(Calendar.FRIDAY);
    }

    /**
     * 获得周六日期
     * <p>
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     *
     * @return
     */
    public static Date saturday() {
        return weekDay(Calendar.SATURDAY);
    }

    /**
     * 获得周日日期
     * <p>
     * 注：日历工厂方法{@link #calendar()}设置类每个星期的第一天为Monday，US等每星期第一天为sunday
     *
     * @return
     */
    public static Date sunday() {
        return weekDay(Calendar.SUNDAY);
    }

    /**
     * 将字符串日期时间转换成java.util.Date类型
     * <p>
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     *
     * @param datetime
     * @return
     */
    public static Date parseDatetime(String datetime) throws ParseException {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        return datetimeFormat.parse(datetime);
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     *<p>
     * 日期时间格式yyyy-MM-dd
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        return dateFormat.parse(date);
    }

    /**
     * 将字符串日期转换成java.util.Date类型
     *<p>
     * 时间格式 HH:mm:ss
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static Date parseTime(String time) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "HH:mm:ss");
        return timeFormat.parse(time);
    }
    public static Date parseShortDateTime(String time) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");
        return timeFormat.parse(time);
    }
    /**
     * 根据自定义pattern将字符串日期转换成java.util.Date类型
     *
     * @param datetime
     * @param pattern
     * @return
     * @throws ParseException
     */  
    public static Date parseDatetime(String datetime, String pattern)  
            throws ParseException {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = (SimpleDateFormat) datetimeFormat.clone();  
        format.applyPattern(pattern);  
        return format.parse(datetime);  
    }  
    
    
    public static String getDateAfter(String date,int day){
        SimpleDateFormat dateShortFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
		
		
		try {
			now.setTime(dateShortFormat.parse(date));
		} catch (ParseException e) {
            logger.error("获取时间失败",e);
		}
		now.set(Calendar.DATE, now.get(Calendar.DATE)+day);
		dateShortFormat.format(now.getTime());
		return dateShortFormat.format(now.getTime());
	}
    
    /**
	 * 
	 * @reason:计算两个日期差几天，也可比较两个日期谁在前，谁在后
	 * @param:只支持yyyyMMdd格式
	 * @throws Exception 
	 * @return：int 如果firstDate在secondDate之前，返回一个负整数；反之返回正整数
	 */
	public static int getDiffBetweenTwoDate(String firstDate,String secondDate)  {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyyMMdd");//计算两天之差
		Date date1=null;
		Date date2=null;
		int cha=0;
		try{
			date1 = myFormatter.parse(firstDate);//起始日期
			date2 = myFormatter.parse(secondDate);//终止日期
			long  seconds=date1.getTime()-date2.getTime();//起始日期-终止日期=毫秒 
	        cha=(int)(seconds/(24*60*60*1000));//再除以每天多少毫秒(24*60*60*1000) ＝差几天
		}catch(Exception e){
            logger.error("获取时间失败",e);
		}
		return cha;
	}

    /**
     * 增加或减少天数
     * @param date    日期
     * @param num     天数
     * @return
     */
    public static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
    /**
     * 增加或减少小时
     * @param date    日期
     * @param num     小时数
     * @return
     */
    public static Date addHour(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.HOUR_OF_DAY, num);
        return startDT.getTime();
    }
    /**
     * 时间戳转换成日期格式字符串
     * @param timeStamp   时间戳
     * @return
     * @throws ParseException
     */
    public static String convertTimeStampToDate(String timeStamp) throws ParseException {
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        long time=new Long(timeStamp);
        Date dateTime=new Date(time);
        return datetimeFormat.format(dateTime);
    }

    /**
     * 获取两个日期之间的日期
     * @param start 开始日期
     * @param end 结束日期
     * @return 日期集合
     */
    public static List<Date> getBetweenDates(Date start, Date end) {
        List<Date> result = new ArrayList<Date>();
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        tempStart.add(Calendar.DAY_OF_YEAR, 1);

        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        while (tempStart.before(tempEnd)) {
            result.add(tempStart.getTime());
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }
        return result;
    }

}  