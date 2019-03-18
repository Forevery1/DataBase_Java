package org.forevery.database.helper;

import java.util.List;
import java.util.Map;

public interface SQLResult {

    /*
     * 生成sql
     */
    String buildSql();

    /**
     * toList
     *
     * @param mClass Class
     * @param <T>    <T>
     * @return <T> List<T>
     */
    <T> List<T> toList(Class<T> mClass);

    /**
     * toList
     *
     * @return List<List < Object>>
     */
    List<List<Object>> toList();

    /**
     * toList
     *
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> toMap();

    /**
     * get
     *
     * @param mClass Class
     * @param <T>    <T>
     * @return <T> T
     */
    <T> T get(Class<T> mClass);

    /**
     * get
     *
     * @return List<Object>
     */
    List<Object> get();

    /**
     * getToMap
     *
     * @return Map<String, Object>
     */
    Map<String, Object> getToMap();
}
