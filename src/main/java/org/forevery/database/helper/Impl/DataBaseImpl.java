package org.forevery.database.helper.Impl;

import org.forevery.database.annotation.Column;
import org.forevery.database.bean.Config;
import org.forevery.database.bean.SaveData;
import org.forevery.database.helper.DBHelper;
import org.forevery.database.helper.DataBase;
import org.forevery.database.helper.SQLResult;
import org.forevery.database.mEnum.JoinParam;
import org.forevery.database.mEnum.OrderParam;
import org.forevery.database.parameter.SQLParameter;
import org.forevery.database.parameter.SQLParameters;
import org.forevery.database.utils.*;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.forevery.database.utils.DateUtils.*;

public class DataBaseImpl implements DataBase {

    private DBHelper db;
    /**
     * Build DBParam
     */
    private StringBuilder whereSql;
    private List<Object> whereParameter;
    private StringBuilder limitSql;
    private StringBuilder selectSql;
    private StringBuilder orderSql;
    private StringBuilder groupSql;
    private StringBuilder joinSql;

    private static int whereCount;
    private static int whereOrCount;
    private static int orderCount;

    /**
     * 初始化
     */
    public DataBaseImpl(DBHelper db) {
        whereSql = new StringBuilder();
        whereParameter = new ArrayList<>();
        limitSql = new StringBuilder();
        selectSql = new StringBuilder();
        orderSql = new StringBuilder();
        groupSql = new StringBuilder();
        joinSql = new StringBuilder();
        whereCount = 0;
        whereOrCount = 0;
        orderCount = 0;
        this.db = db;
    }

    @Override
    public long save(SaveData data) {
        HashMap<String, Object> mData = data.getData();
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(Config.Table_Name).append(" (");
        String ctime = Env.getProperty("Forevery.database.timeStamp.createTimeColumn", "create_time");
        String uptime = Env.getProperty("Forevery.database.timeStamp.updateTimeColumn", "update_time");
        if (Config.AUTOTIMESTAMP) {
            sql.append(ctime).append(",").append(uptime).append(",");
        }
        mData.forEach((k, v) -> sql.append(k).append(","));
        sql.deleteCharAt(sql.lastIndexOf(",")).append(") VALUES (");
        mData.forEach((k, v) -> sql.append("?,"));
        if (Config.AUTOTIMESTAMP) sql.append("?,,?");
        sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
        if (Config.AUTOTIMESTAMP) {
            if (Config.TIMESTAMPTYPE.toLowerCase().equals("long")) {
                mData.put(ctime, getNowDateLong());
                mData.put(uptime, getNowDateLong());
            } else {
                mData.put(ctime, getNowDateString(Config.DATEFORMAT));
                mData.put(uptime, getNowDateString(Config.DATEFORMAT));
            }
        }
        return execute(sql, mData);
    }

    @Override
    public <T> long save(T data) {
        Class cls = data.getClass();
        Field[] fields = cls.getDeclaredFields();
        SaveData data1 = new SaveData();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (isHaveAnn(field)) {
                    data1.Save(toColumn(field), field.get(data));
                } else
                    data1.Save(TextUlits.S_T_J(field.getName()), field.get(data));
            } catch (Exception e) {
                if (Config.DEBUG) Log.e("错误信息:" + e.getMessage());
            }
        }
        return save(data1);
    }

    @Override
    public long delete() {
        StringBuilder sql = new StringBuilder("DELETE FROM " + Config.Table_Name + (whereSql.toString().isEmpty() ? "" : " WHERE " + whereSql));
        return execute(sql, new HashMap<>());
    }

    @Override
    public long update(SaveData data) {
        HashMap<String, Object> mData = data.getData();
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(Config.Table_Name).append(" SET ");
        String uptime = Env.getProperty("Forevery.database.timeStamp.updateTimeColumn", "update_time");
        if (Config.AUTOTIMESTAMP) sql.append(uptime).append(" = ?,");
        if (mData.containsKey("id")) mData.remove("id");
        if (mData.containsKey("Id")) mData.remove("Id");
        mData.forEach((k, v) -> sql.append(k).append(" = ?").append(","));
        sql.deleteCharAt(sql.lastIndexOf(","));
        if (Config.AUTOTIMESTAMP) {
            if (Config.TIMESTAMPTYPE.toLowerCase().equals("long"))
                mData.put(uptime, getNowDateLong());
            else mData.put(uptime, getNowDateString(Config.DATEFORMAT));
        }
        if (!whereSql.toString().isEmpty()) sql.append(" WHERE ").append(whereSql);
        return execute(sql, mData);
    }

    @Override
    public <T> long update(T data) {
        Class cls = data.getClass();
        Field[] fields = cls.getDeclaredFields();
        SaveData data1 = new SaveData();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String name = TextUlits.S_T_J(field.getName());
                if (name.contains("id") || name.contains("ID") || field.get(data) == null)
                    continue;
                if (isHaveAnn(field)) {
                    data1.Save(toColumn(field), field.get(data));
                } else
                    data1.Save(name, field.get(data));
            } catch (Exception e) {
                if (Config.DEBUG) Log.e("错误信息:" + e.getMessage());
            }
        }
        return update(data1);
    }

    @Override
    public boolean find() {
        SQLParameters parameter = SQLParameters.start().setSql(buildSql());
        boolean result = false;
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        whereParameter = new ArrayList<>();
        try {
            ResultSet resultSet = db.executeQuery(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
            while (resultSet.next()) {
                result = true;
            }
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        return result;
    }

    @Override
    public DataBase select(String... columnName) {
        if (columnName.length == 0) return this;
        this.selectSql = new StringBuilder();
        this.selectSql.append(TextUlits.toPoint(columnName));
        return this;
    }

    @Override
    public DataBase where(Object... condition) {
        switch (condition.length) {
            case 0:
            case 1:
                return this;
            case 2:
                if (!whereSql.toString().equals("") && whereCount == 0) whereSql.append(" AND ");
                if (whereCount++ != 0) whereSql.append(" AND ");
                whereSql.append(condition[0]).append(" = ?");
                whereParameter.add(condition[1]);
                return this;
            case 3:
                if (!whereSql.toString().equals("") && whereCount == 0) whereSql.append(" AND ");
                if (whereCount++ != 0) whereSql.append(" AND ");
                whereSql.append(condition[0]).append(" ").append(condition[1]).append(" ?");
                whereParameter.add(condition[2]);
                return this;
            default:
                return this;
        }
    }

    @Override
    public DataBase whereOr(Object... condition) {
        switch (condition.length) {
            case 0:
            case 1:
                return this;
            case 2:
                if (!whereSql.toString().equals("") && whereOrCount == 0) whereSql.append(" OR ");
                if (whereOrCount++ != 0) whereSql.append(" OR ");
                whereSql.append(condition[0]).append(" = ?");
                whereParameter.add(condition[1]);
                return this;
            case 3:
                if (!whereSql.toString().equals("") && whereOrCount == 0) whereSql.append(" OR ");
                if (whereOrCount++ != 0) whereSql.append(" OR ");
                whereSql.append(condition[0]).append(" ").append(condition[1]).append(" ?");
                whereParameter.add(condition[2]);
                return this;
            default:
                return this;
        }
    }

    @Override
    public DataBase page(int page, int page_size) {
        limitSql = new StringBuilder();
        limitSql.append((page - 1) * page_size).append(",").append(page_size);
        return this;
    }

    @Override
    public DataBase limit(int start, int count) {
        limitSql = new StringBuilder();
        limitSql.append(start).append(",").append(count);
        return this;
    }

    @Override
    public DataBase groupBy(String... condition) {
        if (condition.length == 0) return this;
        this.groupSql = new StringBuilder();
        this.groupSql.append(TextUlits.toPoint(condition));
        return this;
    }

    @Override
    public DataBase orderBy(OrderParam order, String... condition) {
        if (orderCount++ != 0) orderSql.append(", ");
        switch (order) {
            case ASC:
                StringBuilder temp = new StringBuilder();
                int index = 0;
                for (String k : condition) {
                    if (index++ != 0) temp.append(",");
                    temp.append(k);
                }
                orderSql.append(temp).append(" ASC");
                return this;
            case DESC:
                StringBuilder temp1 = new StringBuilder();
                int index1 = 0;
                for (String k : condition) {
                    if (index1++ != 0) temp1.append(",");
                    temp1.append(k);
                }
                orderSql.append(temp1).append(" DESC");
                return this;
            default:
                return this;
        }
    }

    @Override
    public Join join(String table) {
        return join(table, JoinParam.NULL);
    }

    @Override
    public Join join(String table, JoinParam type) {
        switch (type) {
            case LEFT:
                joinSql.append(" LEFT JOIN ");
                joinSql.append(table);
                return new Join(joinSql, this);
            case RIGHT:
                joinSql.append(" RIGHT JOIN ");
                joinSql.append(table);
                return new Join(joinSql, this);
            case INNER:
                joinSql.append(" INNER JOIN ");
                joinSql.append(table);
                return new Join(joinSql, this);
            default:
                joinSql.append(" JOIN ");
                joinSql.append(table);
                return new Join(joinSql, this);
        }
    }

    @Override
    public SQLResult query() {
        List<Object> tmp = whereParameter;
        whereParameter = new ArrayList<>();
        return new ResultImpl(db, buildSql(), tmp);
    }

    /**
     * 生成SQL
     *
     * @return DBParam
     */
    private String buildSql() {
        if (selectSql.toString().equals("")) selectSql.append("*");
        String sql = "SELECT " + selectSql.toString() + " FROM " + Config.Table_Name;
        if (!whereSql.toString().equals(""))
            sql += " WHERE " + whereSql.toString();
        if (!joinSql.toString().equals(""))
            sql += joinSql.toString();
        if (!groupSql.toString().equals(""))
            sql += " GROUP BY " + groupSql.toString();
        if (!orderSql.toString().equals(""))
            sql += " ORDER BY " + orderSql.toString();
        if (!limitSql.toString().equals(""))
            sql += " LIMIT " + limitSql.toString();
        return sql;
    }

    /**
     * 代码抽离
     *
     * @return 影响的行
     */
    private long execute(StringBuilder sql, HashMap<String, Object> mData) {
        int row = -1;
        SQLParameters parameter = SQLParameters.start().setSql(sql.toString());
        for (Map.Entry<String, Object> value : mData.entrySet()) {
            parameter.setParameter(value.getValue(), SQLParmUtils.toSQLType(value.getValue()));
        }
        for (Object obj : whereParameter) parameter.setParameter(obj, SQLParmUtils.toSQLType(obj));
        whereParameter = new ArrayList<>();
        try {
            row = db.execute(parameter.getSql(), parameter.build().toArray(new SQLParameter[parameter.build().size()]));
        } catch (SQLException e) {
            if (Config.DEBUG) Log.e(e.getMessage());
        }
        db.destroy();
        return row;
    }

    public class Join {
        private StringBuilder sql;
        private DataBase dataBase;

        Join(StringBuilder sql, DataBase dataBase) {
            this.sql = sql;
            this.dataBase = dataBase;
        }

        public DataBase on(String parameter) {
            sql.append(" ON ").append(parameter);
            return this.dataBase;
        }
    }

    /**
     * 是否存在注解
     */
    private String toColumn(Field field) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty()) {
            return field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }

    /**
     * 是否存在注解
     */
    private boolean isHaveAnn(Field field) {
        field.setAccessible(true);
        return field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty();
    }
}
