package org.forevery.database.helper.Impl;

import org.forevery.database.bean.Config;
import org.forevery.database.helper.DBHelper;
import org.forevery.database.helper.SQLResult;
import org.forevery.database.parameter.SQLParameter;
import org.forevery.database.parameter.SQLParameters;
import org.forevery.database.utils.Log;
import org.forevery.database.utils.SQLParmUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultImpl implements SQLResult {

    private String sql;
    private List<Object> whereParameter;
    private DBHelper db;

    ResultImpl(DBHelper db, String sql, List<Object> whereParameter) {
        this.sql = sql;
        this.db = db;
        this.whereParameter = whereParameter;
    }

    @Override
    public <T> List<T> toList(Class<T> mClass) {
        SQLParameters parameter = SQLParameters.start().setSql(sql);
        List<T> tmp = new ArrayList<>();
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        try {
            tmp = db.executeQueryToList(parameter.getSql(), mClass, parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return tmp;
    }

    @Override
    public List<List<Object>> toList() {
        SQLParameters parameter = SQLParameters.start().setSql(sql);
        List<List<Object>> tmp = new ArrayList<>();
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        try {
            tmp = db.executeQueryToListStr(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return tmp;
    }

    @Override
    public List<Map<String, Object>> toMap() {
        SQLParameters parameter = SQLParameters.start().setSql(sql);
        List<Map<String, Object>> tmp = new ArrayList<>();
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        try {
            tmp = db.executeQueryToList(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return tmp;
    }

    @Override
    public <T> T get(Class<T> mClass) {
        SQLParameters parameter = SQLParameters.start().setSql(sql);
        T tmp = null;
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        try {
            tmp = db.executeGet(parameter.getSql(), mClass, parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return tmp;
    }

    @Override
    public List<Object> get() {
        SQLParameters parameter = SQLParameters.start().setSql(sql);
        List<Object> tmp = new ArrayList<>();
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        try {
            tmp = db.executeGetToList(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return tmp;
    }

    @Override
    public Map<String, Object> getToMap() {
        SQLParameters parameter = SQLParameters.start().setSql(sql);
        Map<String, Object> tmp = new HashMap<>();
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        try {
            tmp = db.executeGetToMap(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return tmp;
    }

    @Override
    public String buildSql() {
        return sql;
    }
}
