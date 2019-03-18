package org.forevery.database.utils;

import java.sql.Types;

public final class SQLParmUtils {

    public static int toSQLType(Object object) {
        String name = object.getClass().getName();
        if (name.contains("String"))
            return Types.VARCHAR;
        if (name.contains("Integer"))
            return Types.INTEGER;
        if (name.contains("Boolean"))
            return Types.BOOLEAN;
        if (name.contains("Long"))
            return Types.BIGINT;
        return Types.NULL;
    }
}
