package com.gitegg.platform.base.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class MyObjectUtil {
    public static Object[] nullFilter(Object... ps) {
        List<Object> pls = new ArrayList<Object>();
        for (Object p : ps) {
            if (p != null) {
                pls.add(p);
            }
        }
        Object[] parray = new Object[pls.size()];
        for (int i = 0; i < pls.size(); i++) {
            parray[i] = pls.get(i);
        }
        return parray;
    }

    public static <T> Set<T> array2Set(T[] args) {
        Set<T> ret = new HashSet<T>();
        for (T t : args) {
            ret.add(t);
        }
        return ret;
    }

    /**
     * 取得整数值
     *
     * @param o
     * @return
     */
    static public int getInteger(Object o) {
        try {
            if (o instanceof Integer) return (Integer) o;
            if (o == null) return 0;
            Double d = Double.parseDouble(o.toString());
            return d.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 取得整数值
     *
     * @param o
     * @param def
     * @return
     */
    static public int getInteger(Object o, int def) {
        try {
            if (o instanceof Integer) return (Integer) o;
            if (o == null) return def;
            Double d = Double.parseDouble(o.toString());
            return d.intValue();
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 取得整数值
     *
     * @param o
     * @return
     */
    static public short getShort(Object o) {
        try {
            if (o instanceof Short) return (Short) o;
            if (o == null) return 0;
            Double d = Double.parseDouble(o.toString());
            return d.shortValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 取得字符串值
     *
     * @param o
     * @return
     */
    static public String getString(Object o) {
        try {
            if (o == null) {
                return "";
            } else {
                return o.toString().trim();
            }
        } catch (Exception e) {
            return "";
        }
    }

    static public String[] getStringArray(Object o) {
        try {
            return (String[])o;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 取得字符串值
     *
     * @param o
     * @return
     */
    static public String getString(Object o, int maxlength) {
        try {
            if (o == null) {
                return "";
            } else {
                String result = o.toString().trim();
                if (result.length() > maxlength) {
                    return result.substring(0, maxlength);
                } else {
                    return result;
                }
            }
        } catch (Exception e) {
            return "";
        }
    }

    static public Boolean getBoolean(Object o) {
        try {
            if (o == null) {
                return false;
            } else {
                if (o instanceof Boolean) return (Boolean) o;
                String s = o.toString().toLowerCase().trim();
                return ("t".equals(s) || "1".equals(s) || "true".equals(s) || "yes".equals(s) || "on".equals(s) || "是".equals(s));
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 取得长整型值
     *
     * @param o
     * @return
     */
    static public long getLong(Object o) {
        try {
            if (o == null) return 0;
            if (o instanceof Long) return (Long) o;
            Double d = Double.parseDouble(o.toString());
            return d.longValue();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 取得长整型值
     *
     * @param o
     * @param def
     * @return
     */
    static public long getLong(Object o, long def) {
        try {
            if (o instanceof Long) return (Long) o;
            if (o == null) return def;
            Double d = Double.parseDouble(o.toString());
            return d.longValue();
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 取得浮点数值
     *
     * @param o
     * @return
     */
    static public float getFloat(Object o) {
        try {
            if (o instanceof Float) return (Float) o;
            if (o == null) return 0;
            return Float.valueOf(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 取得浮点数值
     *
     * @param o
     * @return
     */
    static public double getDouble(Object o) {
        try {
            if (o instanceof Double) return (Double) o;
            if (o == null) return 0;
            return Double.valueOf(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 取得浮点数值
     *
     * @param o
     * @return
     */
    static public BigDecimal getBigDecimal(Object o) {
        try {
            if (o instanceof BigDecimal) return (BigDecimal) o;
            if (o == null) return BigDecimal.ZERO;
            return BigDecimal.valueOf(Double.valueOf(o.toString()));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 取得浮点数值
     *
     * @param o
     * @return
     */
    static public float getFloat(Object o, int precision) {
        try {
            int p = (int) Math.pow(10, precision);
            int v = (int) Math.round(Float.valueOf(o.toString()) * p);
            return ((float) v) / p;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将字符串转换成为日期类型
     *
     * @param o
     * @return
     */
    public static Date getDate(Object o) {
        if (o == null) return null;
        if (o instanceof Date) {
            return (Date) o;
        }

        try {
            String dateStr = o.toString();
            String[] ds = dateStr.split("[^0-9]");
            int dl = ds.length;
            if (dl < 3) return null;
            if (dl > 6) dl = 6;

            int[] di = {0, 0, 0, 0, 0, 0};
            Calendar date = Calendar.getInstance();
            for (int i = 0; i < dl; i++) {
                di[i] = Integer.parseInt(ds[i]);
            }
            date.set(di[0], di[1] - 1, di[2], di[3], di[4], di[5]);
            date.set(Calendar.MILLISECOND, 0);
            return date.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    static public Object getValue(Object val, Class clazz) {
        try {
            if (val.getClass().equals(clazz)) return val;
            if (clazz.equals(String.class)) return getString(val);
            if (clazz.equals(Long.class)) return getLong(val);
            if (clazz.equals(Boolean.class)) return getBoolean(val);
            if (clazz.equals(Date.class)) return getDate(val);
            if (clazz.equals(Double.class)) return getDouble(val);
            if (clazz.equals(BigDecimal.class)) return getBigDecimal(val);
            if (clazz.equals(Float.class)) return getFloat(val);
            if (clazz.equals(Short.class)) return getShort(val);
        } catch (Exception e) {
        }
        return val;
    }


    /**
     * 将父类对象的值赋值给子类
     *
     * @param father
     * @param child
     * @param <T>
     * @throws Exception
     */
    public static <T> void fatherToChild(T father, T child) throws Exception {
        if (child.getClass().getSuperclass() != father.getClass()) {
            throw new Exception("child 不是 father 的子类");
        }
        Class<?> fatherClass = father.getClass();
        Field[] declaredFields = fatherClass.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            Method method = fatherClass.getDeclaredMethod("get" + upperHeadChar(field.getName()));
            Object obj = method.invoke(father);
            ReflectionUtils.makeAccessible(field);
            field.set(child, obj);
        }

    }


    /**
     * 首字母大写，in:deleteDate，out:DeleteDate
     */
    public static String upperHeadChar(String in) {
        String head = in.substring(0, 1);
        String out = head.toUpperCase() + in.substring(1, in.length());
        return out;
    }

//    /**
//     * 把指定的复杂对象属性，按照指定的内容，封装到新的map中
//     *
//     * @param source 目标对象
//     * @param ps     需要封装到map中的属性
//     * @return
//     */
//    public static Map<String, Object> obj2map(Object source, String[] ps) {
//        Map<String, Object> map = new HashMap<>();
//        if (source == null)
//            return null;
//        if (ps == null || ps.length < 1) {
//            return null;
//        }
//        for (String p : ps) {
//            PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(
//                    source.getClass(), p);
//            if (sourcePd != null && sourcePd.getReadMethod() != null) {
//                try {
//                    Method readMethod = sourcePd.getReadMethod();
//                    if (!Modifier.isPublic(readMethod.getDeclaringClass()
//                            .getModifiers())) {
//                        readMethod.setAccessible(true);
//                    }
//                    Object value = readMethod.invoke(source, new Object[0]);
//                    map.put(p, value);
//                } catch (Exception ex) {
//                    log.warn(ex.getMessage(), ex);
////                    throw new RuntimeException(
////                            "Could not copy properties from source to target",
////                            ex);
//                }
//            }
//        }
//        return map;
//    }

    public static Serializable objectClone(Serializable o) throws CloneNotSupportedException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;

        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(o);
            os.flush();
            bos.flush();
            byte[] data = bos.toByteArray();
            IoObjTryUtil.tryClose(os);
            os = null;
            IoObjTryUtil.tryClose(bos);
            bos = null;

            bis = new ByteArrayInputStream(data);
            is = new ObjectInputStream(bis);
            Object newObj = is.readObject();
            IoObjTryUtil.tryClose(is);
            IoObjTryUtil.tryClose(bis);
            return (Serializable) newObj;
        } catch (Exception e) {
            throw new CloneNotSupportedException(e.getMessage());
        } finally {
            IoObjTryUtil.tryClose(is);
            IoObjTryUtil.tryClose(bis);
            IoObjTryUtil.tryClose(os);
            IoObjTryUtil.tryClose(bos);
        }
    }

    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;
        objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        String string = byteArrayOutputStream.toString("ISO-8859-1");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return string;
    }

    public static Object serializeToObject(String str) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return object;
    }


}
