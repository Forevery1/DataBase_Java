package org.forevery.database.parameter;

/**
 *
 */
public class SQLParameter {

    private int index;
    private Object value;
    private int targetSqlType;

    private SQLParameter(int index, Object value, int targetSqlType) {
        this.index = index;
        this.value = value;
        this.targetSqlType = targetSqlType;
    }

    public static SQLParameter makeParam(int index, Object value, int targetSqlType) {
        return new SQLParameter(index, value, targetSqlType);
    }

    public static SQLParameter makeParam(Object value, int targetSqlType) {
        return new SQLParameter(0, value, targetSqlType);
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTargetSqlType() {
        return this.targetSqlType;
    }

    public void setTargetSqlType(int targetSqlType) {
        this.targetSqlType = targetSqlType;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
