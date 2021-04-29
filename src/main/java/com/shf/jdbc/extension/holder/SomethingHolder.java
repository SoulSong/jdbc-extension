package com.shf.jdbc.extension.holder;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 21:54
 */
public class SomethingHolder {
    public static final ThreadLocal<String> SOMETHING = new ThreadLocal<>();

    public static void set(String something) {
        SOMETHING.set(something);
    }

    public static String get() {
        return SOMETHING.get();
    }

    public static void remove() {
        SOMETHING.remove();
    }
}
