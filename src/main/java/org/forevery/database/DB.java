package org.forevery.database;

import org.forevery.database.annotation.Entity;
import org.forevery.database.annotation.Table;
import org.forevery.database.bean.Config;
import org.forevery.database.helper.DBHelper;
import org.forevery.database.helper.DataBase;
import org.forevery.database.helper.Impl.DBHelperImpl;
import org.forevery.database.helper.Impl.DataBaseImpl;
import org.forevery.database.transaction.Trans;
import org.forevery.database.utils.Env;

import static org.forevery.database.bean.Config.*;

/**
 * @author Forevery
 */
public final class DB {

    private static final DBHelper DB_HELPER = new DBHelperImpl();

    /**
     * 设置表
     *
     * @param tableName 表名
     * @return DBHelper
     */
    public static DataBase table(String tableName) {
        String pack = Env.getProperty("Forevery.scanPackage");
        Config.Table_Name = Config.PREFIX + tableName;
        if (!pack.isEmpty() && Character.isUpperCase(tableName.charAt(0)))
            Config.Table_Name = annTable(pack, tableName);
        return new DataBaseImpl(DB_HELPER);
    }


    /**
     * 开启事务
     */
    public static void startTrans() throws Exception {
        DB_HELPER.beginTran();
        isr = true;
    }

    /**
     * 提交事务
     */
    public static void commit() throws Exception {
        if (isr_error) throw new Exception();
        DB_HELPER.commit();
        isr = false;
    }

    /**
     * 事务回滚
     */
    public static void rollback() {
        try {
            DB_HELPER.rollback();
            isr = false;
        } catch (Exception e) {
            if (Config.DEBUG) System.out.println("错误信息 : " + e.getMessage());
        }
    }

    /**
     * 事务
     *
     * @param trans
     */
    public static void transaction(Trans trans) {
        try {
            startTrans();
            trans.runTrans();
            commit();
        } catch (Exception e) {
            if (Config.DEBUG) System.out.println("事务提交错误，以回滚!");
            rollback();
        }
    }

    /**
     * 扫描包
     * @param pack
     * @param tableName
     * @return
     */
    private static String annTable(String pack, String tableName) {
        try {
            Class<?> klass = Class.forName(pack + "." + tableName);
            if (klass.isAnnotationPresent(Entity.class)) {
                if (klass.isAnnotationPresent(Table.class)) {
                    String n = tableName.toLowerCase();
                    if (!klass.getAnnotation(Table.class).value().isEmpty())
                        n = klass.getAnnotation(Table.class).value();
                    return n;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableName;
    }
}
