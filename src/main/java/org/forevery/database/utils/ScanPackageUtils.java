package org.forevery.database.utils;

import org.forevery.database.annotation.Column;
import org.forevery.database.annotation.Entity;
import org.forevery.database.annotation.PrimaryKey;
import org.forevery.database.annotation.Table;
import org.forevery.database.bean.Config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 扫描包
 */
public final class ScanPackageUtils {

    private static Connection connection;

    /**
     * 不扫描的包
     */
    private static final String[] noScan = new String[]{
            "DBUlits", "DataBaseImpl", "DataBaseImpl", "DataBaseImpl", "DataBaseImpl$Join",
            "DBHelperImpl", "ResultImpl", "SQLParameter", "SQLParameters", "HikariDataSourceConfig",
            "Trans", "DateUtils", "Env", "Log", "SQLParmUtils", "TextUlits", "SacnPackageUtils",
            "Constant", "SaveData", "ConfigInfo", "Start", "DataSourceConfig", "DataSourceConfigBuilder",
            "DataBaseImpl$1"
    };

    private static void scanPackage(String packageName, File currentfile) {
        File[] fatalist = currentfile.listFiles(pathName -> {
            if (pathName.isDirectory()) {
                return true;
            }
            return pathName.getName().endsWith(".class");
        });

        assert fatalist != null;
        for (File file : fatalist) {
            if (file.isDirectory()) {
                scanPackage(packageName + "." + file.getName(), file);
            } else {
                String fileName = file.getName().replace(".class", "");
                String className = packageName + "." + fileName;
                if (!hasKey(noScan, fileName)) {
                    try {
                        Class<?> klass = Class.forName(className);
                        if (klass.isAnnotation() || klass.isEnum() || klass.isInterface() || klass.isPrimitive()) {
                            continue;
                        }
                        dealClass(klass);
                    } catch (ClassNotFoundException e) {
                        if (Config.DEBUG) e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 处理Class
     *
     * @param mClass
     */
    private static void dealClass(Class<?> mClass) {
        //是否是实体类 是的话生成数据库表
        if (mClass.isAnnotationPresent(Entity.class)) {
            dealAnnotation(mClass);
        }
    }

    /**
     * 处理注解
     *
     * @param mClass
     */
    private static void dealAnnotation(Class<?> mClass) {
        String tableName = mClass.getSimpleName().toLowerCase();
        if (mClass.isAnnotationPresent(Table.class)) {
            tableName = mClass.getAnnotation(Table.class).value();
            tableName = tableName.isEmpty() ? mClass.getSimpleName().toLowerCase() : tableName;
        }
        Field[] fields = mClass.getDeclaredFields();
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(tableName).append("(");
        List<String> key = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                String column = field.getName();
                if (field.isAnnotationPresent(Column.class)) column = field.getAnnotation(Column.class).name();
                key.add(column);
                boolean zzz = field.getAnnotation(PrimaryKey.class).autoIncrement();
                int columnLength = 255;
                if (field.isAnnotationPresent(Column.class)) columnLength = field.getAnnotation(Column.class).length();
                sql.append(column).append(sqlType(field.getType().getName(), columnLength)).append(" NOT NULL ");
                if (zzz) sql.append("AUTO_INCREMENT");
                sql.append(",");
            }
            if (field.isAnnotationPresent(Column.class)) {
                if (!field.isAnnotationPresent(PrimaryKey.class)) {
                    Column co = field.getAnnotation(Column.class);
                    String column = co.name().isEmpty() ? TextUlits.S_T_J(field.getName()) : co.name();
                    String columnType = field.getType().getName();
                    String tmp = column + sqlType(columnType, co.length());
                    sql.append(tmp);
                    if (!co.nullable()) sql.append(" NOT NULL ");
                    sql.append(",");
                }
            }
        }
        if (Config.AUTOTIMESTAMP) {
            String ctime = Env.getProperty("Forevery.database.timeStamp.createTimeColumn", "create_time");
            String uptime = Env.getProperty("Forevery.database.timeStamp.updateTimeColumn", "update_time");
            sql.append(ctime).append(" varchar(255), ").append(uptime).append(" varchar(255),");
        }
        if (!key.isEmpty()) {
            StringBuilder tmp = new StringBuilder();
            for (String k : key) tmp.append(k).append(",");
            sql.append(" PRIMARY KEY (").append(tmp).append(")");
        }
        //TODO 增加默认值
        sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.executeLargeUpdate();
            if (Config.DEBUG) Log.i("表：[" + tableName + "]创建成功!");
            connection.commit();
            connection.setAutoCommit(true);
            ps.close();
        } catch (Exception e) {
            if (Config.DEBUG) Log.i("表：[" + tableName + "] 创建失败! 该表已存在！");
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                if (Config.DEBUG) e1.printStackTrace();
            }
        }
    }

    /**
     * 扫描包
     *
     * @param packageName 包
     */
    public synchronized static void packageScan(Connection connection1, String packageName) {
        connection = connection1;
        String packOpperPath = packageName.replace(".", "/");
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classloader.getResources(packOpperPath);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if (!url.getProtocol().equals("jar")) {
                    File file = new File(url.toURI());
                    if (!file.exists()) {
                        continue;
                    }
                    scanPackage(packageName, file);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasKey(String[] arr, String targetValue) {
        for (String s : arr) {
            if (s.contains(targetValue))
                return true;
        }
        return false;
    }

    private static String sqlType(String strType, int length) {
        if (strType.toLowerCase().contains("int"))
            return " int(" + length + ")";
        if (strType.toLowerCase().contains("long"))
            return " bigint(" + length + ")";
        if (strType.toLowerCase().contains("float"))
            return " float(" + length + ")";
        if (strType.toLowerCase().contains("double"))
            return " double(" + length + ")";
        if (strType.toLowerCase().contains("boolean"))
            return " tinyint(" + 1 + ")";
        return " varchar(" + length + ")";
    }
}
