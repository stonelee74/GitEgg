/**
 * id: DateUtils 2017年2月4日 下午2:48:31 lixin
 * <p>
 * Copyright (c) 2017 Yuntu Credit.
 * All permissions reserved.
 */
package com.gitegg.platform.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 说明:日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * </pre>
 *
 * @author mlx
 * @since 2017年2月4日下午2:48:31
 */
@Slf4j
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    public static final String QUARTERYYYYMMDD = "2013-03-30";
    private final static String FORMATSS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMMDD_1 = "yyyy/MM/dd";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_CN = "yyyy年MM月dd日";

    private static String[] parsePatterns = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
            "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        String formatDate = null;
        if (pattern != null) {
            return DateFormatUtils.format(date, pattern);
        } else {
            return formatDate(date);
        }
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HHmmss）
     */
    public static String getTime_1() {
        return formatDate(new Date(), "HHmmss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return Integer.toString(LocalDate.now().getYear());
        //return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前年份前一年字符串 格式（yyyy）
     */
    public static String getPreYear() {
        return Integer.toString(LocalDate.now().getYear() - 1);
        //return formatDate(new Date(), "yyyy");
    }

    /**
     * 取上一年
     *
     * @param date
     * @return
     */
    public static Date getPreYearDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.YEAR, -1);//向前一年
        return cal.getTime();
    }

    /**
     * 根据传入的值返回数据
     *
     * @param date
     * @return
     */
    public static Date getPreYearDate(Date date, int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.YEAR, -num);//向前一年
        return cal.getTime();
    }

    public static Date getDateAfterMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//设置起时间
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }


    public static Date getDateFromString(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYY_MM_DD);
        Date date = null;
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            log.error("", e);
        }
        return date;
    }

    public static Date getDateFromString(String str, String patten) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        Date date = null;
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            log.error("", e);
        }
        return date;
    }

    /**
     * 根据2个年份 计算其中所有的年
     * type 1:正常获取 2：需要获取开始前的一年
     */
    public static String[] getTwoYearsDate(String startdate, String enddate, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List twoYearsDateList = new ArrayList();
        //判定时间是否为空，如果为空默认时间段
        if (startdate != null && !"".equals(startdate) && enddate != null && !"".equals(enddate)) {
            int startdateint = Integer.parseInt(startdate);
            if (type.equals("2")) {
                startdateint = Integer.parseInt(startdate) - 1;
            }
            int enddateint = Integer.parseInt(enddate);
            int count = enddateint - startdateint;
            for (int i = 0; count > i; i++) {
                if (i == 0) {
                    twoYearsDateList.add(startdateint + "-12-31");
                } else {
                    twoYearsDateList.add((startdateint + i) + "-12-31");
                }
            }
            //list转换为数据
            String[] strings = new String[twoYearsDateList.size()];
            twoYearsDateList.toArray(strings);
            return strings;
        }
        return null;
    }

    /**
     * 根据2个年份 计算其中所有的年
     * type 1:正常获取 2：需要获取开始前的一年
     */
    public static String[] getTwoYearsDateBn(String startdate, String enddate, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List twoYearsDateList = new ArrayList();
        //判定时间是否为空，如果为空默认时间段
        if (startdate != null && !"".equals(startdate) && enddate != null && !"".equals(enddate)) {
            int startdateint = Integer.parseInt(startdate);
            if (type.equals("2")) {
                startdateint = Integer.parseInt(startdate) - 1;
            }
            int enddateint = Integer.parseInt(enddate);
            int count = enddateint - startdateint;
            for (int i = 0; count >= i; i++) {
                if (i == 0) {
                    twoYearsDateList.add(startdateint + "-12-31");
                } else {
                    twoYearsDateList.add((startdateint + i) + "-12-31");
                }
            }
            //list转换为数据
            String[] strings = new String[twoYearsDateList.size()];
            twoYearsDateList.toArray(strings);
            return strings;
        }
        return null;
    }

    /**
     * 时间进行排序
     */
    public static List<String> Datelist(List<String> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Collections.sort(list, new Comparator<String>() {
            public int compare(String o1, String o2) {
                Date d1;
                Date d2;
                try {
                    d1 = (Date) sdf.parseObject(o1);
                    d2 = (Date) sdf.parseObject(o2);
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    log.error("", e);
                }
                return 0;
            }
        });
        return list;
    }

    /**
     * 计算2个时间相差几个月
     */
    public static int monthDate(String startdate, String enddate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        try {
            bef.setTime(sdf.parse(startdate));
            aft.setTime(sdf.parse(enddate));
        } catch (ParseException e) {
            log.error("", e);
        }
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        System.out.println(Math.abs(month + result));
        return Math.abs(month + result);
    }

    /**
     * 根据传入的日期返回上个月最后一天的日期
     *
     * @return
     */
    public static String lastMonthLastDate(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sf.format(calendar.getTime());
    }

    /**
     * 根据传入的日期返回对应的名称
     *
     * @return
     */
    public static String dateName(String date) {
        String dateName = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month == 3) {
            dateName = year + "年一季度";
        } else if (month == 6) {
            dateName = year + "年上半年";
        } else if (month == 9) {
            dateName = year + "年三季度";
        } else if (month == 12) {
            dateName = year + "年";
        }
        return dateName;
    }


    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return Integer.toString(LocalDate.now().getMonthValue());
        //return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return Integer.toString(LocalDate.now().getDayOfMonth());
        //return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek(Date date) {
        if (date != null && !"".equals(date)) {
            return formatDate(date, "E");
        } else {
            return formatDate(new Date(), "E");
        }
    }

    /**
     * 得到月份下日期字符串
     */
    public static String getDateDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null && !"".equals(date)) {
            calendar.setTime(date);//设置起时间
        } else {
            calendar.setTime(new Date());//设置起时间
        }
        return calendar.get(Calendar.DATE) + "";
    }

    /**
     * 得到月份和日期字符串
     */
    public static String getMonthDateDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null && !"".equals(date)) {
            calendar.setTime(date);//设置起时间
        } else {
            calendar.setTime(new Date());//设置起时间
        }
        return calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DATE);
    }

    /**
     * 日期型字符串转化为日期 格式 { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
     * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(String date) {
        if (StringUtils.isBlank(date)) return 0;
        long t = new Date().getTime() - getDateFromString(date).getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDay(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    public static long pastSeconds(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (1000);
    }

    /**
     * long 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

//	/**
//	 * long 转换为时间（yyyy-mm-dd）
//	 * @param time
//	 * @return
//	 */
//	public static String longFormatDataTime(long time){
//		Date date = new Date(time);
//		return formatDate(date);
//
//	}

    /**
     * 两个时间之间相差距离多少天
     *
     * @param str1 时间参数 1：
     * @param str2 时间参数 2：
     * @return 相差天数
     */
    public static String getDistanceDays(String str1, String str2) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long days = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            days = diff / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            log.error("", e);
        }
        return days + "";
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before 起始
     * @param after  结束
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * <pre>
     * 说明:判断日期格式是否正确
     * 	1、平年二月没有29号
     * 	2、月份1-12月
     * 	3、以下月份有31日:1,3,5,7,8,10,12.剩余月份无31日
     * Note:日期格式: yyyy-MM-dd ,yyyyMMdd均可
     * </pre>
     *
     * @param date 日期字符串
     * @return
     * @author malixin@yuntucredit.com
     * @date 2016年12月19日 上午11:42:20
     */
    public static boolean isDate(String date) {
        /**
         * 判断日期格式和范围
         */
        String rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(date);
        boolean dateType = mat.matches();
        return dateType;
    }

    /**
     * 得到时间月份字符串 格式（MM）
     */
    public static String getMonth(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        int month = calendar.get(Calendar.MONTH) + 1;
        return month + "";
    }

    /**
     * 根据传入的日期返回上个月最后一天的日期
     *
     * @return
     */
    public static String nextMonthLastDate(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sf.format(calendar.getTime());
    }

    /**
     * 根据传入的日期返回当季度第一天的日期
     *
     * @return
     */
    public static String quarterFirstDayDate(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        if (month > 0 && month < 4) {
            return year + "-01-01";
        } else if (month > 3 && month < 7) {
            return year + "-04-01";
        } else if (month > 6 && month < 10) {
            return year + "-07-01";
        } else {
            return year + "-10-01";
        }
    }

    /**
     * 根据传入的 日期和 时间区间段 返回对应的时间点年
     *
     * @return
     */
    public static String lastDate(String date, int dateRange) {
        String dates = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        //获取对应的时期月
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        //循环处理时间点 （2018-06-30，3   2018-06-30,2017-12-31,2016-12-31）
        for (int i = 0; dateRange > i; i++) {
            System.out.println("i   " + i);
            if (i == 0) {
                dates = date;
            } else {
                calendar.add(Calendar.YEAR, -1);
                calendar.set(Calendar.MONTH, 11);
                calendar.set(Calendar.DATE, 31);
                dates = dates + "," + sf.format(calendar.getTime());
            }
        }
        return dates;
    }


    /**
     * 根据传入的日期返回去年的日期
     *
     * @return
     */
    public static String lastYear(String date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        //把时间添加到calerdar对象中
        try {
            calendar.setTime(sf.parse(date));
        } catch (ParseException e) {
            log.error("", e);
        }
        calendar.add(Calendar.YEAR, -1);
        return sf.format(calendar.getTime());
    }

    /**
     * 根据一个起始年月日推算至最近的一个季度
     * 默认为QUARTERYYYYMMDD 2013-03-30
     *
     * @return
     */
    public static String quarterDate() {
        int quartercount = quarterCount(QUARTERYYYYMMDD, dateQ());
        String dateS = getdateQ(quartercount, "1");
//		System.out.println(dateS);
        return dateS;
    }

    /*******  封装季度开始  *******/
    //根据时间段返回范围
    public static int quarterCount(String startdate, String enddate) {
        int a = 0;
        String[] ss = startdate.split("-");
        String[] ee = enddate.split("-");
        String sy = ss[0];
        String sm = ss[1];
        String ey = ee[0];
        String em = ee[1];
        int syi = Integer.parseInt(sy);
        int smi = Integer.parseInt(sm);
        int eyi = Integer.parseInt(ey);
        int emi = Integer.parseInt(em);
        a = ((eyi - syi) * 12 + (emi - 1)) / 3 - (smi - 1) / 3 + 1;
        return a;
    }

    //根据常量 获取最近年度的时间区间段
    public static String getdateY(int dateqj, String type) {
        String dateY = DateUtils.getYear();
        String dates = "";
        //由于按年查询目前只查询前3年的数据 但是年份数据要做对比所以取得是4年的数据
        for (int i = 0; dateqj > i; i++) {
            dateY = (Integer.parseInt(dateY) - 1) + "";
            if (i == 0) {
                if (dateqj == 1) {
                    if ("1".equals(type)) {
                        dates = "'" + dateY + "-12-31'";
                    } else {
                        dates = "'" + dateY + "/12/31 0:00:00'";
                    }
                } else {
                    if ("1".equals(type)) {
                        dates = "'" + dateY + "-12-31',";
                    } else {
                        dates = "'" + dateY + "/12/31 0:00:00',";
                    }
                }
            } else {
                if (dateqj == i + 1) {
                    if ("1".equals(type)) {
                        dates = dates + "'" + dateY + "-12-31'";
                    } else {
                        dates = dates + "'" + dateY + "/12/31 0:00:00'";
                    }
                } else {
                    if ("1".equals(type)) {
                        dates = dates + "'" + dateY + "-12-31',";
                    } else {
                        dates = dates + "'" + dateY + "/12/31 0:00:00',";
                    }
                }
            }
        }
        return dates;
    }

    //根据常量 获取最近季度的时间区间段
    public static String getdateQ(int dateqj, String type) {
        //获取最近季度的时间
        String date = dateQ();
        String dates = "";
        for (int i = 0; dateqj > i; i++) {
            if (i == 0) {
                if (dateqj == 1) {
                    if ("1".equals(type)) {
                        dates = "'" + date + "'";
                    } else {
                        date = date.split("-")[1].replaceAll("0", "");
                        dates = "'" + date.replaceAll("-", "/") + " 0:00:00'";
                    }
                } else {
                    if ("1".equals(type)) {
                        dates = "'" + date + "',";
                    } else {
                        date = date.split("-")[0] + "-" + date.split("-")[1].replaceAll("0", "") + "-" + date.split("-")[2];
                        dates = "'" + date.replaceAll("-", "/") + " 0:00:00',";
                    }
                }
            } else {
                if (dateqj == i + 1) {
                    dates = dates + "'" + getLastDayOfMonth(date, i, type) + "'";
                } else {
                    dates = dates + "'" + getLastDayOfMonth(date, i, type) + "',";
                }
            }
        }
        return dates;
    }

    //根据年月日减一个季度
    public static String getLastDayOfMonth(String date, int i, String type) {
        Calendar calendar = Calendar.getInstance();
        // 设置传入的时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        // 指定一个日期
        try {
            Date dateTime = dateFormat.parse(date);
            // 对 calendar 设置为 date 所定的日期
            calendar.setTime(dateTime);
        } catch (ParseException e) {
            log.error("", e);
        }
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - (3 * i - 1));
        int yearQ = calendar.get(Calendar.YEAR);
        int monthQ = calendar.get(Calendar.MONTH);
        if ("1".equals(type)) {
            return getLastDayOfMonth(yearQ, monthQ, type);
        } else {
            return getLastDayOfMonth(yearQ, monthQ, type) + " 0:00:00";
        }
    }

    //根据年 月份 类型 返回最近一季度最后一天的数据
    public static String getLastDayOfMonth(int year, int month, String type) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //设置默认的天
        cal.set(Calendar.DATE, 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        if ("1".equals(type)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String lastDayOfMonth = sdf.format(cal.getTime());
            return lastDayOfMonth;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd");
            String lastDayOfMonth = sdf.format(cal.getTime());
            return lastDayOfMonth;
        }
    }

    //获取最近季度的时间
    public static String dateQ() {
        /* 注释代码为动态的季度时期 目前查询数据库最近的季度即可*/
        Date dNow = new Date();   //当前时间
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        int year = calendar.get(Calendar.YEAR);
        int month = 0;
        int date = 0;
        int mm = calendar.get(Calendar.MONTH) + 1;
        if (mm > 0 && mm < 4) {
            month = 12;
            year = year - 1;
        } else if (mm > 3 && mm < 7) {
            month = 3;
        } else if (mm > 6 && mm < 10) {
            month = 6;
        } else if (mm > 9 && mm < 13) {
            month = 9;
        }
        return getLastDayOfMonth(year, month, "1");
    }
    /*******  封装季度结束  *******/


    /**
     * 根据一个起始年月日推算至最近的一个半年报
     * 默认为QUARTERYYYYMMDD 2013-03-30
     *
     * @return
     */
    public static String quarterBnbDate() {
        int quartercount = quarterBnCount(QUARTERYYYYMMDD, dateBn());
        String dateS = getdateBn(quartercount);
        System.out.println(dateS);
        return dateS;
    }


    /*******  封装半年度开始  *******/
    //根据时间段返回范围
    public static int quarterBnCount(String startdate, String enddate) {
        int a = 0;
        String[] ss = startdate.split("-");
        String[] ee = enddate.split("-");
        String sy = ss[0];
        String ey = ee[0];
        int syi = Integer.parseInt(sy);
        int eyi = Integer.parseInt(ey);
        a = (eyi - syi) * 2 + 2;
        return a;
    }

    //获取上一年的最后日期
    public static String dateBn() {
        Date dNow = new Date();   //当前时间
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        int year = calendar.get(Calendar.YEAR) - 1;
        return year + "-12-31";
    }

    //根据常量 获取最近季度的时间区间段
    public static String getdateBn(int dateqj) {
        //获取最近季度的时间
        String date = dateBn();
        String dates = "";
        for (int i = 0; dateqj > i; i++) {
            if (i == 0) {
                if (dateqj == 1) {
                    dates = "'" + date + "'";
                } else {
                    dates = "'" + date + "',";
                }
            } else {
                if (dateqj == i + 1) {
                    dates = dates + "'" + getLastDayOfMonth(date, i) + "'";
                } else {
                    dates = dates + "'" + getLastDayOfMonth(date, i) + "',";
                }
            }
        }
        return dates;
    }

    //根据年月日减一个季度
    public static String getLastDayOfMonth(String date, int i) {
        Calendar calendar = Calendar.getInstance();
        // 设置传入的时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        // 指定一个日期
        try {
            Date dateTime = dateFormat.parse(date);
            // 对 calendar 设置为 date 所定的日期
            calendar.setTime(dateTime);
        } catch (ParseException e) {
            log.error("", e);
        }
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - (6 * i - 1));
        int yearQ = calendar.get(Calendar.YEAR);
        int monthQ = calendar.get(Calendar.MONTH);
        return getLastDayOfMonth(yearQ, monthQ);
    }

    //根据年 月份 类型 返回最近一季度最后一天的数据
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month - 1);
        //设置默认的天
        cal.set(Calendar.DATE, 1);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }


    /*******  封装半年度结束  *******/


    /**
     * 根据传入时间，推算前几个月的时间
     *
     * @param date
     * @param month
     * @return
     */
    public static String getLastMonth(String date, int month) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(date));
        c.add(Calendar.MONTH, -month);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    /**
     * 根据传入时间，推算前几天或者后几天的时间的时间
     *
     * @param date
     * @param day
     * @return
     */
    public static String getSomeDay(String date, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(date));
        c.add(Calendar.DATE, day);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    /**
     * 根据传入时间，推算前几秒或者后几秒的时间的时间
     *
     * @param date
     * @param second
     * @return
     */
    public static String getSomeSecond(String date, int second) {
        SimpleDateFormat format = new SimpleDateFormat(FORMATSS);
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(date, FORMATSS));
        c.add(Calendar.SECOND, second);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    /**
     * 根据传入时间，推算前几天或者后几天的时间的时间
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getSomeDay(Date date, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return c.getTime();
    }


    /**
     * 取上个月月末的时间
     *
     * @return
     */
    public static String getLastMonthEnd() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        ca.add(Calendar.MONTH, -1);
        String last = format.format(ca.getTime());
        return last;
    }

    /**
     * 根据当前时间，推算前几小时的时间
     *
     * @return
     */
    public static long getBeforeDay(Date day) {
        Calendar dateOne = Calendar.getInstance(), dateTwo = Calendar.getInstance();
        dateOne.setTime(day);
        dateTwo.setTime(new Date());
        long timeOne = dateOne.getTimeInMillis();
        long timeTwo = dateTwo.getTimeInMillis();
        long minute = (timeOne - timeTwo) / (1000 * 60);//转化minute
        System.out.println("相隔" + minute / 60 + "小时");
        return minute / 60;
    }

    /**
     * 根据当前时间，推算前几天的时间
     *
     * @return
     */
    public static String getBeforeDay(int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.HOUR, -day);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }


    /**
     * 根据当前时间，推算前几天的时间
     *
     * @return
     */
    public static String getAftereMonth(int month) {
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.YYYYMMDD);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, month);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }

    /**
     * 根据开始时间和结束时间计算所有的季度时间
     *
     * @return
     */
    public static String getStartEndQuarter(String startdate, String enddate) {
        String dates = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        if (enddate == null || "".equals(enddate)) {
            return "";
        }
        Calendar calendarE = Calendar.getInstance();
        try {
            calendarE.setTime(sf.parse(enddate));
        } catch (ParseException e) {
            log.error("", e);
        }
        calendarE.set(Calendar.DATE, 1);
        int month = calendarE.get(Calendar.MONTH) + 1;
        if (month == 12 || month == 9 || month == 6 || month == 3) {
            //根据时间段返回范围
            int quarterCount = quarterCount(startdate, enddate);
            //根据时间和循环i的值逆推时间
            for (int i = 0; quarterCount > i; i++) {
                if ("".equals(dates)) {
                    dates = "'" + enddate + "'";
                } else {
                    String syjdate = getLastDayOfMonth(enddate, i, "1");
                    dates = dates + ",'" + syjdate + "'";
                }
            }
        }
        return dates;
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取改年所有月份的最后一天
     *
     * @param date
     * @return
     */
    public static List<String> getLastDayOfMonthForYear(String date) {
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(date));
        List<String> res = new ArrayList<>();
        for (int i = 1; i <= c.get(Calendar.MONTH); i++) {
            res.add(getMaxMonthDate(c.get(Calendar.YEAR) + "-" + i + "" + "-01"));
        }
        return res;
    }


    /**
     * 获取该日期本月的最后一天
     *
     * @param date
     * @return
     */
    public static String getMaxMonthDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException pa) {
            return null;
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat.format(calendar.getTime());
    }


    /**
     * 根据传入时间，推算前几天的时间
     *
     * @return
     */
    public static String getBeforeDay1(String data, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(data));
        c.add(Calendar.HOUR, -day * 24);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }


    public static String conversionFormat(String data) {
        return formatDate(getDateFromString(data), YYYY_MM_DD_CN);
    }


    /**
     * 根据传入日期求上季度末日期
     *
     * @return
     */
    public static String getQuarterEndTime(String date) {
        date = getLastMonth(date, 3);
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(date));
        int currentMonth = c.get(Calendar.MONTH) + 1;
        try {
            if (currentMonth >= 1 && currentMonth <= 3) {
                c.set(Calendar.MONTH, 2);
                c.set(Calendar.DATE, 31);
            } else if (currentMonth >= 4 && currentMonth <= 6) {
                c.set(Calendar.MONTH, 5);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 7 && currentMonth <= 9) {
                c.set(Calendar.MONTH, 8);
                c.set(Calendar.DATE, 30);
            } else if (currentMonth >= 10 && currentMonth <= 12) {
                c.set(Calendar.MONTH, 11);
                c.set(Calendar.DATE, 31);
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = df.format(c.getTime());
        } catch (Exception e) {
            log.error("", e);
        }
        return date;
    }

//	/**
//	 * YYYYMMDD转yyyy-mm-dd
//	 * @param str
//	 * @return
//	 */
//	public static String getStringDateFromString(String str) {
//		if (StringUtils.isBlank(str)) return "";
//        return formatDate(getDateFromString(str, YYYYMMDD));
//	}

    /**
     * 根据传入时间，推算前几天的时间，并计算推算天数过去的时间
     *
     * @return
     */
    public static String getHistoryDay(String data, int day) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(getDateFromString(data));
        c.add(Calendar.HOUR, -day * 24);
        Date m = c.getTime();
        long t = m.getTime() - getDateFromString("1900-01-01").getTime();
        return (t / (24 * 60 * 60 * 1000)) + "";
    }


    /**
     * 获取昨天的时间
     *
     * @return
     */
    public static String getYesterday(int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -day);
        return sdf.format(c.getTime());
    }

    /**
     * 根据时间戳转换string类型 yyyy-MM-dd HH:mm:ss
     */
    public static String format(Long dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMATSS);
        String dateStr = sdf.format(dateTime);
        return dateStr;
    }

    /**
     * 根据身份证号码取得年龄
     *
     * @param idCardNum
     * @return
     * @throws Exception
     */
    public static int getAgeByIdCard(String idCardNum) {
        try {
            String strdate = idCardNum.substring(6, 14);
            Date birthDay = DateUtils.getDateFromString(strdate, "yyyyMMdd");


            //获取当前系统时间
            Calendar cal = Calendar.getInstance();
            //如果出生日期大于当前时间，则抛出异常
            if (cal.before(birthDay)) {
                throw new IllegalArgumentException(
                        "The birthDay is after Now. It's unbelievable!");
            }

            //取出系统当前时间的年、月、日部分
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

            //将日期设置为出生日期
            cal.setTime(birthDay);
            //取出出生日期的年、月、日部分
            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH);
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            //当前年份与出生年份相减，初步计算年龄
            int age = yearNow - yearBirth;
            //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
            if (monthNow <= monthBirth) {
                //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
                if (monthNow == monthBirth) {
                    if (dayOfMonthNow < dayOfMonthBirth) age--;
                } else {
                    age--;
                }
            }
            return age;
        } catch (Exception e) {
            return -1;
        }
    }


    /**
     * 根据身份证号码取得生日
     *
     * @param idCardNum
     * @return
     * @throws Exception
     */
    public static Date getBirthdayByIdCard(String idCardNum) {
        try {
            String strdate = idCardNum.substring(6, 14);
            return DateUtils.getDateFromString(strdate, "yyyyMMdd");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    public static Date getMonthMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }


    /**
     * 获取本季度第一天
     *
     * @return
     */
    public static String getCurrentQuarterStartTime() {
        String date = DateUtils.getQuarterEndTime(DateUtils.getDate());
        return getBeforeDay1(date, -1);
    }

    public static Date parseDate(String str, String... parsePatterns) throws ParseException {
        try {
            return parseDate(str, (Locale) null, parsePatterns);
        } catch (Exception e) {
            return null;
        }
    }

}
