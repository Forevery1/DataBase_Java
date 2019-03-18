package org.forevery.database.utils;

import java.util.Date;

/**
 * 日志工具
 */
public final class Log {
    /**
     * 信息
     *
     * @param o Object
     */
    public static void i(Object o) {
        System.out.println("[" + new Date().toString() + "]" + "\033[37;0m" + " [信息]" + "\033[0m" + "\033[34;0m" + " ===>  " + "\033[0m" + "\033[37;0m" + o.toString() + "\033[0m");
    }

    /**
     * 错误
     *
     * @param o Object
     */
    public static void e(Object o) {
        System.out.println("[" + new Date().toString() + "]" + "\033[31;0m" + " [错误]" + "\033[0m" + "\033[34;0m" + " ===>  " + "\033[0m" + "\033[31;0m" + o.toString() + "\033[0m");
    }

    /**
     * 警告
     *
     * @param o Object
     */
    public static void w(Object o) {
        System.out.println("[" + new Date().toString() + "]" + "\033[33;0m" + " [警告]" + "\033[0m" + "\033[34;0m" + " ===>  " + "\033[0m" + "\033[33;0m" + o.toString() + "\033[0m");
    }
}
