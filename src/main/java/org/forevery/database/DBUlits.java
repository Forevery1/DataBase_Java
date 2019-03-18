package org.forevery.database;

import org.forevery.database.bean.Config;
import org.forevery.database.helper.DBHelper;
import org.forevery.database.helper.Impl.DBHelperImpl;
import org.forevery.database.parameter.SQLParameter;
import org.forevery.database.parameter.SQLParameters;
import org.forevery.database.utils.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作封装类
 *
 * @author Forevery
 */
public final class DBUlits {

    private static final DBHelper db = new DBHelperImpl();

    /**
     * 执行sql
     *
     * @param sql    sql语句
     * @param params 参数
     * @return 影响的行
     */
    public static int execute(String sql, SQLParameter... params) {
        int effectLine = -1;
        try {
            effectLine = db.execute(sql, params);
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return effectLine;
    }

    /**
     * 执行sql
     *
     * @param sql           sql语句
     * @param parameterList 参数
     * @return 影响的行
     */
    public static int execute(String sql, List<SQLParameter> parameterList) {
        return execute(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 执行sql
     *
     * @param parameter 参数类
     * @return 影响的行
     */
    public static int execute(SQLParameters parameter) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return -1;
        }
        return execute(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 执行查询
     *
     * @param sql
     * @param parameterList
     * @return
     */
    public static ResultSet executeQuery(String sql, List<SQLParameter> parameterList) {
        return executeQuery(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 执行查询
     *
     * @param sql
     * @param params
     * @return
     */
    public static ResultSet executeQuery(String sql, SQLParameter... params) {
        ResultSet resultSet = null;
        try {
            resultSet = db.executeQuery(sql, params);
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return resultSet;
    }

    /**
     * 执行查询
     *
     * @param parameter 参数类
     * @return
     */
    public static ResultSet executeQuery(SQLParameters parameter) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeQuery(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static List<List<Object>> executeQueryToListStr(String sql, List<SQLParameter> parameterList) {
        return executeQueryToListStr(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static List<List<Object>> executeQueryToListStr(String sql, SQLParameter... params) {
        List<List<Object>> result = new ArrayList<>();
        try {
            result.addAll(db.executeQueryToListStr(sql, params));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return result;
    }

    /**
     * 执行查询
     *
     * @param parameter 参数类
     * @return
     */
    public static List<List<Object>> executeQueryToListStr(SQLParameters parameter) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeQueryToListStr(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static List<Map<String, Object>> executeQueryToList(String sql, SQLParameter... params) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            result.addAll(db.executeQueryToList(sql, params));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return result;
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static List<Map<String, Object>> executeQueryToList(String sql, List<SQLParameter> parameterList) {
        return executeQueryToList(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static List<Map<String, Object>> executeQueryToList(SQLParameters parameter) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeQueryToList(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static <T> List<T> executeQueryToList(String sql, Class<?> mClass, SQLParameter... params) {
        List<T> result = new ArrayList<>();
        try {
            result.addAll((List<T>) db.executeQueryToList(sql, mClass, params));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return result;
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static <T> List<T> executeQueryToList(String sql, List<SQLParameter> parameterList, Class mClass) {
        return executeQueryToList(sql, mClass, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 执行查询
     *
     * @return
     */
    public static <T> List<T> executeQueryToList(SQLParameters parameter, Class<T> mClass) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeQueryToList(parameter.getSql(), mClass, parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 得到一个
     *
     * @param sql    sql语句
     * @param params 参数
     * @return
     */
    public static Map<String, Object> executeGetToMap(String sql, SQLParameter... params) {
        Map<String, Object> result = new HashMap();
        try {
            result = db.executeGetToMap(sql, params);
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return result;
    }

    /**
     * 得到一个
     *
     * @param sql           sql语句
     * @param parameterList 参数
     */
    public static Map<String, Object> executeGetToMap(String sql, List<SQLParameter> parameterList) {
        return executeGetToMap(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 得到一个
     *
     * @param parameter 参数类
     * @return 影响的行
     */
    public static Map<String, Object> executeGetToMap(SQLParameters parameter) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeGetToMap(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 得到一个
     *
     * @param sql    sql语句
     * @param params 参数
     * @return
     */
    public static List<Object> executeGetToList(String sql, SQLParameter... params) {
        List<Object> result = new ArrayList<>();
        try {
            result.addAll(db.executeGetToList(sql, params));
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return result;
    }

    /**
     * 得到一个
     *
     * @param sql           sql语句
     * @param parameterList 参数
     */
    public static List<Object> executeGetToList(String sql, List<SQLParameter> parameterList) {
        return executeGetToList(sql, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 得到一个
     *
     * @param parameter 参数类
     * @return 影响的行
     */
    public static List<Object> executeGetToList(SQLParameters parameter) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeGetToList(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 得到一个
     *
     * @param sql    sql语句
     * @param params 参数
     * @return
     */
    public static <T> T executeGet(String sql, Class<T> mClass, SQLParameter... params) {
        T t = null;
        try {
            t = db.executeGet(sql, mClass, params);
        } catch (Exception e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return t;
    }

    /**
     * 得到一个
     *
     * @param sql           sql语句
     * @param parameterList 参数
     */
    public static <T> T executeGet(String sql, Class<T> mClass, List<SQLParameter> parameterList) {
        return executeGet(sql, mClass, parameterList.toArray(new SQLParameter[parameterList.size()]));
    }

    /**
     * 得到一个
     *
     * @param parameter 参数类
     * @return 影响的行
     */
    public static <T> T executeGet(SQLParameters parameter, Class<T> mClass) {
        if (parameter.getSql().isEmpty()) {
            if (Config.DEBUG) Log.e("必须设置Sql语句");
            return null;
        }
        return executeGet(parameter.getSql(), mClass, parameter.build().toArray(new SQLParameter[parameter.build().size()]));
    }

    /**
     * 开启事务
     */
    public static void beginTran() {
        db.beginTran();
    }

    /**
     * 事务回滚
     */
    public static void rollback() {
        db.rollback();
    }

    /**
     * 事务提交
     */
    public static void commit() {
        db.commit();
    }
}
