package org.forevery.database.helper;


import org.forevery.database.parameter.SQLParameter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 数据库操作接口
 *
 * @author Forevery
 */
public interface DBHelper {

    /**
     * 开启事务
     */
    void beginTran();

    /**
     * 事务回滚
     */
    void rollback();

    /**
     * 事务提交
     */
    void commit();

    /**
     * 释放
     */
    void destroy();

    /**
     * 执行语句
     *
     * @param sql        sql语句
     * @param parameters 参数
     * @return 影响的行
     * @throws SQLException SQLException
     */
    int execute(String sql, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return ResultSet
     * @throws SQLException SQLException
     */
    ResultSet executeQuery(String sql, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return List<List < Object>>
     * @throws SQLException SQLException
     */
    List<List<Object>> executeQueryToListStr(String sql, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return List<Map < String, Object>>
     * @throws SQLException SQLException
     */
    List<Map<String, Object>> executeQueryToList(String sql, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return <T> List<T>
     * @throws SQLException SQLException
     */
    <T> List<T> executeQueryToList(String sql, Class<T> mClass, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return Map<String, Object>
     * @throws SQLException SQLException
     */
    Map<String, Object> executeGetToMap(String sql, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return List<Object>
     * @throws SQLException SQLException
     */
    List<Object> executeGetToList(String sql, SQLParameter... parameters) throws SQLException;

    /**
     * 执行查询
     *
     * @param sql        sql        sql语句
     * @param parameters 参数
     * @return <T> T
     * @throws SQLException SQLException
     */
    <T> T executeGet(String sql, Class<T> mClass, SQLParameter... parameters) throws SQLException;

}
