package org.forevery.database.helper.Impl;

import org.forevery.database.annotation.Column;
import org.forevery.database.bean.Config;
import org.forevery.database.config.ConfigInfo;
import org.forevery.database.helper.DBHelper;
import org.forevery.database.parameter.SQLParameter;
import org.forevery.database.utils.Env;
import org.forevery.database.utils.Log;
import org.forevery.database.utils.ScanPackageUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

import static org.forevery.database.config.Start.Init;

public class DBHelperImpl implements DBHelper {

    private Connection connection;
    private PreparedStatement preparedStatement = null;

    /**
     * 初始化连接
     */
    public DBHelperImpl() {
        Init();
        String basePackage = Env.getProperty("Forevery.scanPackage");
        connection = ConfigInfo.getInstance().getConnection();
        if (!basePackage.isEmpty()) ScanPackageUtils.packageScan(connection, basePackage);
    }

    @Override
    public void beginTran() {
        try {
            this.connection.setAutoCommit(false);
            if (Config.DEBUG) Log.i("事务开启");
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
    }

    @Override
    public void rollback() {
        try {
            this.connection.rollback();
            this.connection.setAutoCommit(true);
            if (Config.DEBUG) Log.i("事务已回滚");
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
    }

    @Override
    public void commit() {
        try {
            this.connection.commit();
            this.connection.setAutoCommit(true);
            if (Config.DEBUG) Log.i("事务提交");
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
    }

    @Override
    public void destroy() {
        try {
            if (this.preparedStatement != null) this.preparedStatement.close();
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
    }

    @Override
    public int execute(String sql, SQLParameter... params) throws SQLException {
        this.preparedStatement = this.connection.prepareStatement(sql);
        if (Config.SHOWSQL) Log.i("sql语句 ===> " + sql);
        for (int index = 0; index < params.length; ++index) {
            SQLParameter param = params[index];
            if (param.getIndex() == 0) {
                param.setIndex(index + 1);
            }
            if (Config.DEBUG) {
                Log.i("--------------------");
                Log.i("\u53c2\u6570 index : " + param.getIndex());
                Log.i("\u53c2\u6570\u7c7b\u578b : " + (param.getValue() == null ? "null" : param.getValue().getClass().getName()));
                Log.i("\u53c2\u6570\u503c : " + (param.getValue() == null ? "null" : param.getValue().toString()));
            }
            this.preparedStatement.setObject(param.getIndex(), param.getValue(), param.getTargetSqlType());
        }
        int effectLine = this.preparedStatement.executeUpdate();
        if (Config.DEBUG) Log.i("共" + effectLine + "行受到影响");
        this.closePreparedStatement();
        return effectLine;
    }

    @Override
    public ResultSet executeQuery(String sql, SQLParameter... parameters) throws SQLException {
        this.preparedStatement = this.connection.prepareStatement(sql);
        if (Config.SHOWSQL) Log.i("sql语句 ===> " + sql);
        for (int index = 0; index < parameters.length; ++index) {
            SQLParameter param = parameters[index];
            if (param.getIndex() == 0) {
                param.setIndex(index + 1);
            }
            if (Config.DEBUG) {
                Log.i(" -------------------- ");
                Log.i("参数 index : " + param.getIndex());
                Log.i("参数类型 : " + (param.getValue() == null ? "null" : param.getValue().getClass().getName()));
                Log.i("参数值 : " + (param.getValue() == null ? "null" : param.getValue().toString()));
            }
            this.preparedStatement.setObject(param.getIndex(), param.getValue(), param.getTargetSqlType());
        }
        return this.preparedStatement.executeQuery();
    }

    @Override
    public List<List<Object>> executeQueryToListStr(String sql, SQLParameter... parameters) throws SQLException {
        ResultSet resultSet = executeQuery(sql, parameters);
        List<List<Object>> result = new ArrayList<>();
        ResultSetMetaData rmd = resultSet.getMetaData();
        int columnCount = rmd.getColumnCount();
        while (resultSet.next()) {
            List<Object> tmp = new ArrayList<>();
            for (int i = 1; i <= columnCount; ++i) {
                tmp.add(resultSet.getObject(i));
            }
            result.add(tmp);
        }
        resultSet.close();
        this.closePreparedStatement();
        return result;
    }

    @Override
    public List<Map<String, Object>> executeQueryToList(String sql, SQLParameter... parameters) throws SQLException {
        ResultSet resultSet = executeQuery(sql, parameters);
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData rmd = resultSet.getMetaData();
        int columnCount = rmd.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> tmp = new HashMap<>();
            for (int i = 1; i <= columnCount; ++i) {
                tmp.put(rmd.getColumnName(i), resultSet.getObject(i));
            }
            result.add(tmp);
        }
        resultSet.close();
        this.closePreparedStatement();
        return result;
    }

    @Override
    public <T> List<T> executeQueryToList(String sql, Class<T> mClass, SQLParameter... parameters) throws SQLException {
        ResultSet resultSet = executeQuery(sql, parameters);
        List<T> result = new ArrayList<>();
        ResultSetMetaData rmd = resultSet.getMetaData();
        int columnCount = rmd.getColumnCount();
        HashSet<String> temp = new HashSet<>();
        HashSet<String> temp_e = new HashSet<>();
        while (resultSet.next()) {
            Object nIt = null;
            try {
                nIt = mClass.newInstance();
            } catch (Exception e) {
                if (Config.DEBUG) Log.e(e.getMessage());
            }
            for (int i = 0; i < columnCount; i++) {
                String name = rmd.getColumnName(i + 1).toLowerCase();
                StringBuilder l = new StringBuilder();
                if (name.contains("_")) {
                    int index = 0;
                    for (String str : name.split("_")) {
                        if (index == 0) l.append(str);
                        if (index++ != 0) l.append(toT(str));
                    }
                }
                try {
                    Field field;
                    if (isHaveAnn(name, mClass))
                        field = mClass.getDeclaredField(getName(name, mClass));
                    else
                        field = mClass.getDeclaredField(l.toString().equals("") ? name : l.toString());
                    field.setAccessible(true);
                    field.set(nIt, resultSet.getObject(name));
                } catch (Exception e) {
                    if (Config.DEBUG && !e.getMessage().contains("Can not set ")) temp.add(name);
                    else temp_e.add(e.getMessage());
                }
            }
            result.add((T) nIt);
        }
        if (Config.DEBUG && !temp.isEmpty()) {
            Log.w(">数据库字段" + temp + "未在实体类中找到对应的映射变量");
            Log.w(">如需要使用请创建对应的映射变量(遵循驼峰命名格式)");
        }
        if (Config.DEBUG && !temp_e.isEmpty()) {
            temp_e.forEach(Log::e);
        }
        resultSet.close();
        this.closePreparedStatement();
        return result;
    }

    @Override
    public Map<String, Object> executeGetToMap(String sql, SQLParameter... parameters) throws SQLException {
        ResultSet resultSet = executeQuery(sql, parameters);
        Map<String, Object> resultMap = new HashMap<>();
        ResultSetMetaData rmd = resultSet.getMetaData();
        int columnCount = rmd.getColumnCount();
        if (resultSet.first()) {
            for (int i = 1; i <= columnCount; ++i) {
                resultMap.put(rmd.getColumnName(i), resultSet.getObject(i));
            }
        }
        resultSet.close();
        this.closePreparedStatement();
        return resultMap;
    }

    @Override
    public List<Object> executeGetToList(String sql, SQLParameter... parameters) throws SQLException {
        ResultSet resultSet = executeQuery(sql, parameters);
        List<Object> resultMap = new ArrayList<>();
        ResultSetMetaData rmd = resultSet.getMetaData();
        int columnCount = rmd.getColumnCount();
        if (resultSet.first()) {
            for (int i = 1; i <= columnCount; ++i) {
                resultMap.add(resultSet.getObject(i));
            }
        }
        resultSet.close();
        this.closePreparedStatement();
        return resultMap;
    }

    @Override
    public <T> T executeGet(String sql, Class<T> mClass, SQLParameter... parameters) throws SQLException {
        ResultSet resultSet = executeQuery(sql, parameters);
        Object obj = null;
        try {
            obj = mClass.newInstance();
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        ResultSetMetaData rmd = resultSet.getMetaData();
        int columnCount = rmd.getColumnCount();
        HashSet<String> temp = new HashSet<>();
        HashSet<String> temp_e = new HashSet<>();
        if (resultSet.first()) {
            for (int i = 0; i < columnCount; i++) {
                String name = rmd.getColumnName(i + 1).toLowerCase();
                StringBuilder l = new StringBuilder();
                if (name.contains("_")) {
                    int index = 0;
                    for (String str : name.split("_")) {
                        if (index == 0) l.append(str);
                        if (index++ != 0) l.append(toT(str));
                    }
                }
                try {
                    Field field;
                    if (isHaveAnn(name, mClass))
                        field = mClass.getDeclaredField(getName(name, mClass));
                    else
                        field = mClass.getDeclaredField(l.toString().equals("") ? name : l.toString());
                    field.setAccessible(true);
                    field.set(obj, resultSet.getObject(name));
                } catch (Exception e) {
                    if (Config.DEBUG && !e.getMessage().contains("Can not set ")) temp.add(e.getMessage());
                    else temp_e.add(e.getMessage());
                }
            }
        }
        resultSet.close();
        this.closePreparedStatement();
        return (T) obj;
    }


    /**
     * 关闭PreparedStatement
     */
    private void closePreparedStatement() {
        try {
            if (this.preparedStatement != null) {
                this.preparedStatement.close();
                if (Config.DEBUG) Log.i("PreparedStatement =======> 关闭成功");
            }
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
    }


    private String toT(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 是否存在注解
     */
    private boolean isHaveAnn(String column, Class<?> tClass) {
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty() && field.getAnnotation(Column.class).name().equals(column)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注解对应的字段名
     */
    private String getName(String column, Class<?> tClass) {
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty() && field.getAnnotation(Column.class).name().equals(column)) {
                return field.getName();
            }
        }
        return column;
    }
}
