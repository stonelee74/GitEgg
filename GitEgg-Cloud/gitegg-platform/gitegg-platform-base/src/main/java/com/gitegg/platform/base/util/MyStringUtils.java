/**
 * id: StringUtils 2017年2月4日 下午3:08:53 lixin
 * <p>
 * Copyright (c) 2017 Yuntu Credit.
 * All permissions reserved.
 */
package com.gitegg.platform.base.util;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 * 说明:字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * </pre>
 *
 * @author mlx
 * @since 2017年2月4日下午3:08:53
 */

@Slf4j
@Component
public class MyStringUtils extends StringUtils {

    private static final char _SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 首字母大写
     */
    public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    /**
     * 转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 转换为字节数组
     *
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
        if (str != null) {
            for (String s : strs) {
                if (str.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param html
     * @return
     */
    public static String replaceMobileHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
    }

    /**
     * 缩略字符串（不区分中英文字符）
     *
     * @param str    目标字符串
     * @param length 截取长度
     * @return
     */
    public static String abbr(String str, int length) {
        if (str == null) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            int currentLength = 0;
            for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
                currentLength += String.valueOf(c).getBytes("GBK").length;
                if (currentLength <= length - 3) {
                    sb.append(c);
                } else {
                    sb.append("...");
                    break;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return "";
    }

    public static String abbr2(String param, int length) {
        if (param == null) {
            return "";
        }
        StringBuffer result = new StringBuffer();
        int n = 0;
        char temp;
        boolean isCode = false; // 是不是HTML代码
        boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
        for (int i = 0; i < param.length(); i++) {
            temp = param.charAt(i);
            if (temp == '<') {
                isCode = true;
            } else if (temp == '&') {
                isHTML = true;
            } else if (temp == '>' && isCode) {
                n = n - 1;
                isCode = false;
            } else if (temp == ';' && isHTML) {
                isHTML = false;
            }
            try {
                if (!isCode && !isHTML) {
                    n += String.valueOf(temp).getBytes("GBK").length;
                }
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
            }

            if (n <= length - 3) {
                result.append(temp);
            } else {
                result.append("...");
                break;
            }
        }
        // 取出截取字符串中的HTML标记
        String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)", "$1$2");
        // 去掉不需要结素标记的HTML标记
        temp_result = temp_result.replaceAll(
                "</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
                "");
        // 去掉成对的HTML标记
        temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>", "$2");
        // 用正则表达式取出标记
        Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
        Matcher m = p.matcher(temp_result);
        List<String> endHTML = new ArrayList<String>();
        while (m.find()) {
            endHTML.add(m.group(1));
        }
        // 补全不成对的HTML标记
        for (int i = endHTML.size() - 1; i >= 0; i--) {
            result.append("</");
            result.append(endHTML.get(i));
            result.append(">");
        }
        return result.toString();
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }

    /**
     * 驼峰命名法工具
     *
     * @param s 下划线命名串
     * @return toCamelCase(" hello_world ") == "helloWorld"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == _SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @param s 下划线命名串
     * @return toCapitalizeCamelCase(" hello_world ") == "HelloWorld"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @param s 驼峰命名串
     * @return toUnderScoreCase(" helloWorld ") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(_SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     *
     * @param objectString 对象串 例如：row.user.id
     *                     返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString) {
        StringBuilder result = new StringBuilder();
        StringBuilder val = new StringBuilder();
        String[] vals = split(objectString, ".");
        for (int i = 0; i < vals.length; i++) {
            val.append("." + vals[i]);
            result.append("!" + (val.substring(1)) + "?'':");
        }
        result.append(val.substring(1));
        return result.toString();
    }

    public static boolean isENum(String input) {//判断输入字符串是否为科学计数法
        boolean bret = false;
        String reg = "^-?[0-9]+(.[0-9]+)?$";
        bret = input.matches(reg);
        if (bret)
            return bret;

        reg = "(-?\\\\d+\\\\.?\\\\d*)[Ee]{1}[\\\\+-]?[0-9]*";  //科学计数法
        bret = input.matches(reg);
        return bret;

    }

    public static boolean isNum(String input) {//判断输入字符串是否为数字
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(input);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 去掉小数点后面没用的0  111.01100万人民币
     *
     * @param str
     * @param unit 单位
     * @return
     */
    public static String remove0(String str, String unit) {
        if (str == null || "".equals(str)) {
            return "";
        }
        String[] arr = str.split(unit);
        if (arr.length > 0) {
            str = arr[0];
            if (str.indexOf(".") > 0) {
                //正则表达
                str = str.replaceAll("0+?$", "");//去掉后面无用的零
                str = str.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
            }
        }
        return str + unit;
    }

    /**
     * 提取字符串里的小数
     *
     * @param str
     * @return
     */
    public static String getNumber(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        // 需要取整数和小数的字符串
        // 控制正则表达式的匹配行为的参数(小数)
        Pattern p = Pattern.compile("(\\d+\\.\\d+)");
        //Matcher类的构造方法也是私有的,不能随意创建,只能通过Pattern.matcher(CharSequence input)方法得到该类的实例.
        Matcher m = p.matcher(str);
        //m.find用来判断该字符串中是否含有与"(\\d+\\.\\d+)"相匹配的子串
        if (m.find()) {
            //如果有相匹配的,则判断是否为null操作
            //group()中的参数：0表示匹配整个正则，1表示匹配第一个括号的正则,2表示匹配第二个正则,在这只有一个括号,即1和0是一样的
            str = m.group(1) == null ? "" : m.group(1);
        } else {
            //如果匹配不到小数，就进行整数匹配
            p = Pattern.compile("(\\d+)");
            m = p.matcher(str);
            if (m.find()) {
                //如果有整数相匹配
                str = m.group(1) == null ? "" : m.group(1);
            } else {
                //如果没有小数和整数相匹配,即字符串中没有整数和小数，就设为空
                str = "";
            }
        }
        return str;
    }

    /**
     * 求一个值所在的区间
     *
     * @param value
     * @return
     */
    public static String getInterval(String value) {
        StringBuffer res = new StringBuffer("—");
        if (isBlank(value) || Double.parseDouble(value) == 0) {
            return res.toString();
        }

        Double dou = Double.parseDouble(value);
        DecimalFormat df = new DecimalFormat("0.0");
        if (dou > 0 && dou < 10000000) {
            int a = (int) Math.floor((dou / 1000000));
            int b = (int) Math.ceil((dou / 1000000));
            res = new StringBuffer("[");
            if (dou > 0) {
                if (a == b) {
                    res.append(a * 100 + "万," + (a + 1) * 100 + "万)");
                } else {
                    res.append(a * 100 + "万," + b * 100 + "万)");
                }
            } else {
                if (a == b) {
                    res.append(a * 100 + "万," + (a - 1) * 100 + "万)");
                } else {
                    res.append(b * 100 + "万," + a * 100 + "万)");
                }
            }
        } else if (10000000 <= dou && dou < 100000000) {
            int a = (int) Math.floor((dou / 5000000));
            int b = (int) Math.ceil((dou / 5000000));
            res = new StringBuffer("[" + a * 500 + "万,");
            if (a == b) {
                if (a + 1 == 20) {
                    res.append("1亿)");
                } else {
                    res.append((a + 1) * 500 + "万)");
                }
            } else {
                res.append(b * 500 + "万)");
            }
        } else if (100000000 <= dou && dou < 500000000) {
            int a = (int) Math.floor((dou / 10000000));
            int b = (int) Math.ceil((dou / 10000000));
            if (a % 10 == 0) {
                res = new StringBuffer("[" + a / 10 + "亿,");
            } else {
                res = new StringBuffer("[" + df.format((float) a / 10) + "亿,");
            }
            if (a == b) {
                if ((a + 1) % 10 == 0) {
                    res.append((a + 1) / 10 + "亿)");
                } else {
                    res.append(df.format((float) (a + 1) / 10) + "亿)");
                }
            } else {
                if (b % 10 == 0) {
                    res.append(b / 10 + "亿)");
                } else {
                    res.append(df.format((float) b / 10) + "亿)");
                }
            }
        } else if (500000000 <= dou) {
            int a = (int) Math.floor((dou / 50000000));
            int b = (int) Math.ceil((dou / 50000000));
            if (a % 2 == 0) {
                res = new StringBuffer("[" + a / 2 + "亿,");
            } else {
                res = new StringBuffer("[" + df.format((float) a / 2) + "亿,");
            }
            if (a == b) {
                if ((a + 1) % 2 == 0) {
                    res.append((a + 1) / 2 + "亿)");
                } else {
                    res.append(df.format((float) (a + 1) / 2) + "亿)");
                }
            } else {
                if (b % 2 == 0) {
                    res.append(b / 2 + "亿)");
                } else {
                    res.append(df.format((float) b / 2) + "亿)");
                }
            }
        } else if (dou < 0 && dou > -10000000) {
            int a = (int) Math.floor((dou / 1000000));
            int b = (int) Math.ceil((dou / 1000000));
            res = new StringBuffer("[");
            if (dou > 0) {
                if (a == b) {
                    res.append(b * 100 + "万," + (b + 1) * 100 + "万)");
                } else {
                    res.append(b * 100 + "万," + a * 100 + "万)");
                }
            } else {
                if (a == b) {
                    res.append(b * 100 + "万," + (a - 1) * 100 + "万)");
                } else {
                    res.append(a * 100 + "万," + b * 100 + "万)");
                }
            }
        } else if (-10000000 >= dou && dou > -100000000) {
            int a = (int) Math.floor((dou / 5000000));
            int b = (int) Math.ceil((dou / 5000000));
            res = new StringBuffer("[" + a * 500 + "万,");
            if (a == b) {
                if (a + 1 == 20) {
                    res.append("1亿)");
                } else {
                    res.append((a + 1) * 500 + "万)");
                }
            } else {
                res.append(b * 500 + "万)");
            }
        } else if (-100000000 >= dou && dou > -500000000) {
            int a = (int) Math.floor((dou / 10000000));
            int b = (int) Math.ceil((dou / 10000000));
            if (a % 10 == 0) {
                res = new StringBuffer("[" + a / 10 + "亿,");
            } else {
                res = new StringBuffer("[" + df.format((float) a / 10) + "亿,");
            }
            if (a == b) {
                if ((a + 1) % 10 == 0) {
                    res.append((a + 1) / 10 + "亿)");
                } else {
                    res.append(df.format((float) (a + 1) / 10) + "亿)");
                }
            } else {
                if (b % 10 == 0) {
                    res.append(b / 10 + "亿)");
                } else {
                    res.append(df.format((float) b / 10) + "亿)");
                }
            }
        } else if (-500000000 >= dou) {
            int a = (int) Math.floor((dou / 50000000));
            int b = (int) Math.ceil((dou / 50000000));
            if (a % 2 == 0) {
                res = new StringBuffer("[" + a / 2 + "亿,");
            } else {
                res = new StringBuffer("[" + df.format((float) a / 2) + "亿,");
            }
            if (a == b) {
                if ((a + 1) % 2 == 0) {
                    res.append((a + 1) / 2 + "亿)");
                } else {
                    res.append(df.format((float) (a + 1) / 2) + "亿)");
                }
            } else {
                if (b % 2 == 0) {
                    res.append(b / 2 + "亿)");
                } else {
                    res.append(df.format((float) b / 2) + "亿)");
                }
            }
        }
        return res.toString();
    }

    public static Integer getInteger(String str) {
        if (MyStringUtils.isEmpty(str)) {
            return null;
        } else {
            return Integer.valueOf(str);
        }
    }

    public static BigDecimal getBigDecimal(String str) {
        if (MyStringUtils.isEmpty(str)) {
            return null;
        } else {
            return new BigDecimal(str);
        }
    }

    public static int getRandomNumber(int min, int max) {
        SecureRandom random = new SecureRandom();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 获取订单编号 20位 例：20191205160711496560
     *
     * @return result
     */
    public static String getOrderNumber() {
        SimpleDateFormat sfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String strDate = sfDate.format(new Date());
        return strDate + getRandomNumber(100, 999);
    }

    public static String getErrorMsg(Exception e) {
        String ret = e.toString();
        try {
            StackTraceElement stackTraceElement = e.getStackTrace()[0];
            ret = String.format("Error File=%s Line=%s Method=%s", stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), stackTraceElement.getMethodName());
        } catch (Exception ex) {
        }
        return ret;

    }

    // 对字节数组字符串进行Base64解码并-->生成图片
    public static byte[] GenerateImage(String imgStr) {
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            // Base64解码
            // byte[] bytes = decoder.decodeBuffer(imgStr);
            byte[] bytes = decoder.decode(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }


    public static String escapeHtml(String text) {
        if (text == null) return null;

        char[] chars = text.toCharArray();
        StringBuilder sb = new StringBuilder(chars.length + 20);

        for (int i = 0, len = chars.length; i < len; i++) {

            // 去除不可见字符
            if ((int) chars[i] < 32) continue;

            switch (chars[i]) {
                case '<':
                    sb.append("&#60;");
                    break;
                case '>':
                    sb.append("&#62;");
                    break;
                case '&':
                    sb.append("&#38;");
                    break;
                case '"':
                    sb.append("&#34;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '/':
                    sb.append("&#47;");
                    break;
                default:
                    sb.append(chars[i]);
                    break;
            }
        }
        return sb.toString();
    }

    public static String firstLower(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String firstUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 输出json
     *
     * @param jsonObject json格式响应实体
     */
    public static void printJson(JSONObject jsonObject) {
        if (jsonObject == null) {
            System.out.println("json 对象是空的！");
            return;
        }

        // 先将json对象转化为string对象
        String start = "  ";
        String jsonStr = jsonObject.toString();

        //jsonStr = jsonStr.replaceAll("\\\\/", OR);
        int level = 0;// 用户标记层级
        StringBuffer jsonResultStr = new StringBuffer(1024);// 新建stringbuffer对象，用户接收转化好的string字符串
        for (int i = 0; i < jsonStr.length(); i++) {// 循环遍历每一个字符
            char piece = jsonStr.charAt(i);// 获取当前字符
            // 如果上一个字符是断行，则在本行开始按照level数值添加标记符，排除第一行
            if (i != 0 && '\n' == jsonResultStr.charAt(jsonResultStr.length() - 1)) {
                for (int k = 0; k < level; k++) {
                    jsonResultStr.append(start);
                }
            }
            switch (piece) {
                case ',':
                    // 如果是“,”，则断行
                    char last = jsonStr.charAt(i - 1);
                    if ("\"0123456789le]}".contains(last + EMPTY)) {
                        jsonResultStr.append(piece + "\n");
                    } else {
                        jsonResultStr.append(piece);
                    }
                    break;
                case '{':
                case '[':
                    // 如果字符是{或者[，则断行，level加1
                    jsonResultStr.append(piece + "\n");
                    level++;
                    break;
                case '}':
                case ']':
                    // 如果是}或者]，则断行，level减1
                    jsonResultStr.append("\n");
                    level--;
                    for (int k = 0; k < level; k++) {
                        jsonResultStr.append(start);
                    }
                    jsonResultStr.append(piece);
                    break;
                default:
                    jsonResultStr.append(piece);
                    break;
            }
        }
        System.out.println(jsonResultStr);
    }
}
