package org.forevery.database.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL参数类
 */
public final class SQLParameters {

    private List<SQLParameter> parameters = new ArrayList<>();
    private StringBuilder mSql = new StringBuilder();

    /**
     * 唯一实例
     *
     * @return SQLParameters
     */
    public static SQLParameters start() {
        return new SQLParameters();
    }

    public SQLParameters setParameter(Object value, int targetSqlType) {
        parameters.add(SQLParameter.makeParam(value, targetSqlType));
        return this;
    }

    public SQLParameters setSql(String sql) {
        mSql = new StringBuilder();
        mSql.append(sql);
        return this;
    }

    public List<SQLParameter> build() {
        return parameters;
    }

    public String getSql() {
        return mSql.toString();
    }
}
