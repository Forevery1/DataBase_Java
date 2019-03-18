package org.forevery.database.bean;

import org.forevery.database.utils.Env;
import org.forevery.database.utils.Log;

/**
 * 配置
 */
public final class Config {

    public static final String DRIVER;
    public static final String URL;
    public static final String USERNAME;
    public static final String PASSWORD;
    public static String Table_Name;
    public static final String PREFIX;
    public static final boolean DEBUG;
    public static final boolean SHOWSQL;
    public static final boolean AUTOTIMESTAMP;
    public static final String DATEFORMAT;
    public static final String TIMESTAMPTYPE;
    public static final int maxPoolSize;
    public static boolean isr = false;
    public static boolean isr_error = false;
    
    static {
        DRIVER = Env.getProperty("Forevery.database.driver", "com.mysql.jdbc.Driver");
        URL = getUrl();
        USERNAME = Env.getProperty("Forevery.database.username", "root");
        PASSWORD = Env.getProperty("Forevery.database.password", "root");
        PREFIX = Env.getProperty("Forevery.database.prefix", "");
        DEBUG = Boolean.parseBoolean(Env.getProperty("Forevery.debug"));
        SHOWSQL = Boolean.parseBoolean(Env.getProperty("Forevery.showSql"));
        AUTOTIMESTAMP = Boolean.parseBoolean(Env.getProperty("Forevery.database.timeStamp.autoTimeStamp"));
        DATEFORMAT = Env.getProperty("Forevery.database.dateFormat", "yyyy-MM-dd HH:mm:ss");
        TIMESTAMPTYPE = Env.getProperty("Forevery.database.timeStamp.type", "String");
        maxPoolSize = Integer.parseInt(Env.getProperty("Forevery.database.maxPoolSize", "5"));
        if (DEBUG) Log.i("DEBUG 模式开启!");
        if (SHOWSQL) Log.i("输出SQL语句开启!");
        if (AUTOTIMESTAMP) {
            Log.i("自动更新时间戳开启!");
            Log.i("时间戳类型 : " + DATEFORMAT);
        }
    }

    private static String getUrl() {
        String tmp = Env.getProperty("Forevery.database.url", "jdbc:mysql:///");
        if (!tmp.contains("charset")) {
            String ch = Env.getProperty("Forevery.database.charset");
            if (tmp.contains("?")) tmp += "&characterEncoding=" + (ch.isEmpty() ? "utf-8" : ch);
            else tmp += "?characterEncoding=" + (ch.isEmpty() ? "utf-8" : ch);
        }
        return tmp;
    }
}
