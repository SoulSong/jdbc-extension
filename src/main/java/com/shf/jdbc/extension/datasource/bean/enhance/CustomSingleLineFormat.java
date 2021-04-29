package com.shf.jdbc.extension.datasource.bean.enhance;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import com.shf.jdbc.extension.holder.SomethingHolder;

/**
 * description :
 *
 * @author songhaifeng
 * @date 2021/4/26 11:38
 */
public class CustomSingleLineFormat implements MessageFormattingStrategy {

    /**
     * Formats a log message for the logging module
     *
     * @param connectionId the id of the connection
     * @param now          the current ime expressing in milliseconds
     * @param elapsed      the time in milliseconds that the operation took to complete
     * @param category     the category of the operation
     * @param prepared     the SQL statement with all bind variables replaced with actual values
     * @param sql          the sql statement executed
     * @param url          the database url where the sql statement executed
     * @return the formatted log message
     */
    @Override
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql, final String url) {
        return now + "|" + elapsed + "|" + category + "|connection " + connectionId + "|url " + url + "|" + P6Util.singleLine(prepared) + "|" + P6Util.singleLine(sql) + "|" + SomethingHolder.get();
    }
}