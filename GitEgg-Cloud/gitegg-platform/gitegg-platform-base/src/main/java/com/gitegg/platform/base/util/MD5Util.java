package com.gitegg.platform.base.util;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class MD5Util {
    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static String createSign(Map<String, String> packageParams) {
        StringBuffer sb = new StringBuffer();
        Map<String, String> params = new TreeMap<String, String>();
        for(String key : packageParams.keySet()){
            params.put(key, packageParams.get(key));
        }
        Set<Entry<String, String>> es = params.entrySet();
        Iterator<Entry<String, String>> it = es.iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + v + "&");
        }
        //System.out.println("data:" + sb);
        String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
        //System.out.println("---------------------sign:" + sign);
        return sign;
    }

    public static String MD5Encode(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(bytes));
        } catch (Exception exception) {
            return "";
        }
    }

    public static String MD5Encode(String origin, String charsetname) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                return byteArrayToHexString(md.digest(origin.getBytes()));
            else
                return byteArrayToHexString(md.digest(origin.getBytes(charsetname)));
        } catch (Exception exception) {
            return "";
        }
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
