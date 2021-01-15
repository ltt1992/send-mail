package com.example.mail.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	public static final String dt = "yyyy-MM-dd HH:mm:ss";
	public static final String d = "yyyy-MM-dd";
	public static final String dd = "yyyyMMdd";
	public static final String yw = "yyyyww";
	public static final String ym = "yyyyMM";
	public static final String dts = "yyyyMMddHHmmssSSS";
	public static final String dh = "yyyyMMddHH";
	public static final long ONEDAY = 24 * 60 * 60 * 1000 - 1;
	public static DateFormat dfnow = new SimpleDateFormat("yyyyMMddHHmmss");

	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public static String getNowTimestamp() {
		return dfnow.format(new Date());
	}

	/**
	 * 日期转字符串
	 * @param d date
	 * @param p 格式
	 * @return
	 */
	public static String dTS(Date d,String p){
		if(d == null){
			return null;
		}
		DateFormat df = new SimpleDateFormat(p);
		return df.format(d);
	}

	/**
	 * 日期转字符串
	 * @param d date
	 * @param p 格式
	 * @return
	 * @throws ParseException 
	 */
	public static Date sTd(String d,String p) throws ParseException{
		if(d == null){
			return null;
		}
		DateFormat df = new SimpleDateFormat(p);
		return df.parse(d);
	}

	public static Date next(Date time, int i) {
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		Date nowtime = new Date();
		while(nowtime.after(c.getTime())){
			c.add(Calendar.YEAR, i);
		}
		return c.getTime();
	}

	public static String edts(Date time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM,dd,yyyy",Locale.ENGLISH);
		if (time==null) {
			return sdf.format(new Date());
		}else{
			return sdf.format(time);
		}
	}

	public static String edts(Date time,String p) {
		SimpleDateFormat sdf = new SimpleDateFormat(p,Locale.ENGLISH);
		if (time==null) {
			return sdf.format(new Date());
		}else{
			return sdf.format(time);
		}
	}

	/**
	 * 时长秒转时长天时分秒
	 * @param diff
	 * @return
	 */
	public static String tTS(Integer diff) {
		if (diff == null) {
			return null;
		}
		StringBuffer str = new StringBuffer();
        long nd = 24 * 60 * 60;
        long nh = 60 * 60;
        long nm = 60;
        
        long day = diff / nd;// 计算差多少天
        if(day>0) {
        	str.append(day + "天");
        }
        long hour = diff % nd / nh;// 计算差多少小时
        if(str.length()>0||hour>0) {
        	str.append(hour + "小时");
        }
        long min = diff % nh / nm;// 计算差多少分钟
        if(str.length()>0||min>0) {
        	str.append(min + "分钟");
        }
        long sec = diff % nm;// 计算差多少秒
        if(str.length()>0||sec>0) {
        	str.append(sec + "秒");
        }
        return str.toString();
	}

	/**
	 * UNIXTIME转字符串
	 * @param time
	 * @return
	 */
	public static String intTS(Integer datetime) {
		Date d = new Date(datetime.longValue()*1000);
		return dTS(d,dt);
	}
	
	/**
	 * int time转字符串
	 * @param time
	 * @return
	 */
	public static String inttimeTohmsS(int time) {
		return String.format("%02d", (time/3600))+":"+String.format("%02d", ((time%3600)/60))+":"+String.format("%02d", ((time%3600)%60));
	}

	public static int getNextDay(Date d) {
		return Long.valueOf((d.getTime()+ONEDAY)/1000).intValue();
	}
	public static int getNowDay(Date d) {
		return Long.valueOf(d.getTime()/1000).intValue();
	}

	public static Date getDayEnd(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	public static Date getSystemNextDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}
	
	public static int compareDate(Date DATE1, Date DATE2) {
        try {
            if (DATE1.getTime() > DATE2.getTime()) {
                return 1;
            } else if (DATE1.getTime() < DATE2.getTime()) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

	public static Date getDayStart(Date d) {
		Calendar c = Calendar.getInstance();
		if (d != null) {
			c.setTime(d);
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

    public static Date getMonthStart(Date d) {
        Calendar c = Calendar.getInstance();
        if (d != null) {
            c.setTime(d);
        }
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    
	public static Date getLastWeek(Date d) {
		Calendar c = Calendar.getInstance();
		if (d != null) {
			c.setTime(d);
		}
		c.add(Calendar.DATE, -7);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	/**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }
	
	/**
	 * 获取上周周序号
	 * @return
	 */
	public static String getLastWeekNo() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.WEEK_OF_YEAR, -1);
		DateFormat sdf = new SimpleDateFormat(yw);
		return sdf.format(c.getTime());
	}
	
	/**
	 * 获取上月序号
	 * @return
	 */
	public static String getLastMonthNo() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		DateFormat sdf = new SimpleDateFormat(ym);
		return sdf.format(c.getTime());
	}
	
	/**
	 * 获取上季度序号
	 * @return
	 */
	public static String getLastQuarter() {
        Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -3);
		return c.get(Calendar.YEAR)+""+(c.get(Calendar.MONTH)/3+1);
	}
	
	/**
	 * 获取上个半年序号
	 * @return
	 */
	public static String getLastHalfYear() {
        Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		return c.get(Calendar.YEAR)+""+(c.get(Calendar.MONTH)/6+1);
	}

	public static Long getAMinuteAgo() {
		Long now = System.currentTimeMillis();
		return now - 60*1000;
	}

	/**
	 * 获取时间在当日的秒数
	 * @param date
	 * @return
	 */
	public static int getDaySecond(Date date){
		return (int)(date.getTime() - getZeroDate(date).getTime())/1000;
	}

	/**
	 * 获取00:00:00的date
	 * @param date
	 * @return
	 */
	public static Date getZeroDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		return cal.getTime();
	}

	/**
	 * 通过零点的date 和秒数获取 目标date
	 * @param date
	 * @param second
	 * @return
	 */
	public static Date getDate(Date date, int second){
		return new Date(date.getTime() + second * 1000);
	}
	
	/**
	 * 输入一个日期，获取这个日期之后的nums天的日期类型
	 * @param date
	 * @param nums
	 * @return
	 */
	public static Date getLaterDate(Date date, Integer nums) {
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE,nums);
		Date time=cal.getTime();
		return time;
	}
	
	/**
	 * 输入一个日期，获取这个日期之前nums天的日期类型
	 * @param date
	 * @param nums
	 * @return
	 */
	public static Date getAgoDate(Date date, Integer nums) {
		Calendar now = Calendar.getInstance();      
        now.setTime(date);      
        now.set(Calendar.DATE, now.get(Calendar.DATE) - nums);  
        return now.getTime();
	}
	
	/**
	 * 当前时间短第一天
	 * @param d
	 * @return
	 */
	public static Date getThisPeriodStart(Date d, Integer type) {
		if (type == 3 || type ==4) {
			return d;
		}
		Calendar c = Calendar.getInstance();
		if (d != null) {
			c.setTime(d);
		}
		c.set(type, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	/**
	 * 当前时间短第一天
	 * @param d
	 * @return
	 */
	public static Date getLastPeriodStart(Date d, Integer type) {
		if (type == 3 || type ==4) {
			return d;
		}
		Calendar c = Calendar.getInstance();
		if (d != null) {
			c.setTime(d);
		}
		if (type == Calendar.DAY_OF_YEAR) {
			c.add(Calendar.YEAR, -1);
		}else if (type == Calendar.DAY_OF_MONTH) {
			c.add(Calendar.MONTH, -1);
		}else {
			c.add(Calendar.DATE, -7);
		}
		c.set(type, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
	
	/**
	 * 上时间段最后一天
	 * @param d
	 * @return
	 */
	public static Date getLastPeriodEnd(Date d, Integer type) {
		if (type == 3 || type ==4) {
			return d;
		}
		Calendar c = Calendar.getInstance();
		if (d != null) {
			c.setTime(d);
		}
		if (type == Calendar.DAY_OF_YEAR) {
			c.add(Calendar.YEAR, -1);
		}else if (type == Calendar.DAY_OF_MONTH) {
			c.add(Calendar.MONTH, -1);
		}else {
			c.add(Calendar.DATE, -7);
		}
		c.set(type, c.getActualMaximum(type));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}
	
	/**
	 * 是否本时间段最后一天
	 * @param d
	 * @return
	 */
	public static boolean isThisPeriodEnd(Date d, Integer type) {
		if (type == 3 || type ==4) {
			return true;
		}
		Calendar c = Calendar.getInstance();
		if (d != null) {
			c.setTime(d);
		}
		c.set(type, c.getActualMaximum(type));
		DateFormat sdf = new SimpleDateFormat(dd);
		String endDay = sdf.format(c.getTime());
		String thisDay = sdf.format(d);
		if (thisDay.compareTo(endDay) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 通过时间秒毫秒数判断两个时间的间隔
	 * @param endTime
	 * @param beginTime
	 * @return 秒
	 */
	public static Integer diffSecond(Date endTime, Date beginTime) {
		if(endTime!=null&&beginTime!=null) {
			Long diff = (endTime.getTime()-beginTime.getTime())/1000;
			if(diff.intValue()>0) {
				return diff.intValue();
			}
		}
		return 0;
	}

	public static String localDateToStr(LocalDate localDate,DateTimeFormatter formatter){
		return formatter.format(localDate);
	}

	public static String localDateTimeToStr(LocalDateTime localDateTime, DateTimeFormatter formatter){
		return formatter.format(localDateTime);
	}

	public static LocalDate strToLocalDate(String time,DateTimeFormatter dateTimeFormatter){
		return LocalDate.parse(time,dateTimeFormatter);
	}

	public static LocalDate localDatePlusDay(LocalDate localDate,int days){
		localDate = localDate.plusDays(days);
		return localDate;
	}
}
