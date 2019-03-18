package org.forevery.database.helper;

import org.forevery.database.bean.SaveData;
import org.forevery.database.helper.Impl.DataBaseImpl;
import org.forevery.database.mEnum.JoinParam;
import org.forevery.database.mEnum.OrderParam;

import java.sql.SQLException;

/**
 *
 */
public interface DataBase {

    /**
     * 插入
     *
     * @param data 数据 SaveData
     * @return 插入数据的ID
     */
    long save(SaveData data);

    /**
     * 插入
     *
     * @param data 数据
     * @param <T>  实体类
     * @return 插入数据的ID
     */
    <T> long save(T data);

    /**
     * 删除
     *
     * @return 影响的行数
     */
    long delete();

    /**
     * 修改
     *
     * @param data 更新的数据
     * @return 影响的行数
     */
    long update(SaveData data);

    /**
     * 修改
     *
     * @param data 更新的数据
     * @return 影响的行数
     */
    <T> long update(T data);

    /**
     * 是否存在该条数据
     *
     * @return 是否存
     */
    boolean find() throws SQLException;

    /**
     * 选择表字段
     *
     * @param columnName 字段
     */
    DataBase select(String... columnName);

    /**
     * 条件
     *
     * @param condition 要查询的字段
     */
    DataBase where(Object... condition);

    /**
     * 条件
     *
     * @param condition 要查询的字段
     */
    DataBase whereOr(Object... condition);

    /**
     * 分页
     *
     * @param page      页数
     * @param page_size 页大小
     * @return DBHelper
     */
    DataBase page(int page, int page_size);

    /**
     * 分页
     *
     * @param start
     * @param count
     * @return this
     */
    DataBase limit(int start, int count);

    /**
     * 分组
     *
     * @param condition 排序字段
     * @return this
     */
    DataBase groupBy(String... condition);

    /**
     * 排序
     *
     * @param order     ASC / DESC
     * @param condition 排序字段
     * @return this
     */
    DataBase orderBy(OrderParam order, String... condition);

    /**
     * * Join
     *
     * @param table 表
     * @param type  DBParam 类型 LEFT, RIGHT, INNER , NULL
     * @return Join
     */
    DataBaseImpl.Join join(String table, JoinParam type);

    /**
     * * Join
     *
     * @param table 表
     * @return Join
     */
    DataBaseImpl.Join join(String table);

    /**
     * 查询
     *
     * @return List                                                                                                                                                                                         String>>
     */
    SQLResult query();

}
