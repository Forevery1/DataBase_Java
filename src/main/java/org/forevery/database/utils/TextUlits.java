package org.forevery.database.utils;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUlits {

    /**
     * 驼峰转 SQL命名
     *
     * @param str String
     * @return String
     */
    public static String S_T_J(String str) {
        StringBuilder name = new StringBuilder(str);
        String[] ss = name.toString().split("(?<!^)(?=[A-Z])");
        name = new StringBuilder();
        for (int i = 0; i < ss.length; i++) {
            if (i != 0) name.append("_");
            name.append(ss[i].toLowerCase());
        }
        return name.toString();
    }

    /**
     * 参数 用【,】隔开
     *
     * @param condition String... condition
     * @return String
     */
    public static String toPoint(String... condition) {
        StringBuilder temp = new StringBuilder();
        int index = 0;
        for (String name : condition) {
            if (index++ != 0) temp.append(",");
            temp.append(name);
        }
        return temp.toString();
    }

    /**
     * 字符串截取
     *
     * @param paramString1
     * @param paramString2
     * @param paramString3
     * @return
     */
    public static List<Object> Substring(String paramString1, String paramString2, String paramString3) {
        if (("".equals(paramString1)) || ("".equals(paramString2)) || ("".equals(paramString3))) {
            return new ArrayList<>();
        }
        String regex = "(?<=\\Q" + paramString2 + "\\E).*?(?=\\Q" + paramString3 + "\\E)";
        Matcher String1 = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL).matcher(paramString1);
        ArrayList<Object> String2 = new ArrayList<>();
        while (String1.find()) {
            Log.i(String1.group().getClass().getName());
            String2.add(String1.group());
        }
        return String2;
    }

    /**
     * @param str
     * @param start
     * @param end
     * @return
     */
    public static String Delete(String str, String start, String end) {
        Matcher String1 = Pattern.compile("(?<=\\Q" + start + "\\E).*?(?=\\Q" + end + "\\E)", Pattern.MULTILINE | Pattern.DOTALL).matcher(str);
        String tmp = str;
        while (String1.find()) {
            String lamp = start + String1.group() + end;
            tmp = tmp.replaceAll(lamp, "");
        }
        return tmp;
    }
}
