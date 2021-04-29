package com.shf.jdbc.extension;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Iterator;
import java.util.List;

class SqlParserTest {

    @Test
    void test() throws JSQLParserException, ParseException {
        listSelectTable(parseSingleSql("select a.c1,b.c2,c.* from tbl_a a left join tbl_b b on a.c3=b.c3 left join tbl_c c on a.c4=b.c4 where c.c5='tbl_d'"));
        String sqls = "SELECT * FROM TABLE1;SELECT * FROM TABLE2";
        for (Statement statement : parseMultiSql1(sqls)) {
            listSelectTable(statement);
        }
        for (Statement statement : parseMultiSql2(sqls)) {
            listSelectTable(statement);
        }
    }

    public static void listSelectTable(Statement statement) throws JSQLParserException {
        Select selectStatement = (Select) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List tableList = tablesNamesFinder.getTableList(selectStatement);
        for (Iterator iter = tableList.iterator(); iter.hasNext(); ) {
            String tableName = (String) iter.next();
            System.out.println(tableName);
        }
    }

    public Statement parseSingleSql(String sql) throws JSQLParserException {
        return CCJSqlParserUtil.parse(sql);
    }

    public List<Statement> parseMultiSql1(String sqls) throws JSQLParserException, ParseException {
        return CCJSqlParserUtil.parseStatements(sqls).getStatements();
    }

    public List<Statement> parseMultiSql2(String sqls) throws JSQLParserException, ParseException {
        CCJSqlParser ccjSqlParser = new CCJSqlParser(sqls);
        Statements statements = ccjSqlParser.Statements();
        return statements.getStatements();
    }
}
