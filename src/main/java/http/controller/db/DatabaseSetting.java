package http.controller.db;

import http.controller.db.controller.sql.DCL;
import http.controller.db.controller.sql.DDL;
import http.controller.db.controller.sql.DML;
import http.controller.db.define.DBType;
import http.items.Color;
import origin.exception.MatchException;
import origin.exception.MatchMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.StringTokenizer;

public class DatabaseSetting {
    private final static DCL dcl = new DCL();
    private final static DDL ddl = new DDL();
    private final static DML dml = new DML();
    private static Connection con = null;
    private final String dbUrl;
    private final String user;
    private final String password;

    public DatabaseSetting(String dbKind, String dbIp, String dbPort, String dbName, String user, String password) {
        this.user = user;
        this.password = password;
        //sqlite, mariadb... //localhost //9090
        dbUrl = String.format("jdbc:%s://%s:%s/%s", dbKind, dbIp, dbPort, dbName);
        createConnect();
    }

    public DatabaseSetting(String url, String user, String password) {
        this.user = user;
        this.password = password;
        this.dbUrl = url;
        createConnect();
    }

    private void createConnect() {
        try {
            con = DriverManager.getConnection(dbUrl, user, password);
        } catch (SQLException e) {
            System.out.printf("%s디비 생성에 실패하였습니다.%s\n", Color.RED, Color.RESET);
            throw new MatchException(e.getMessage());
        }
    }

    //데이터베이스 설정
    //sql = SELECT * FROM TEST
    public void setting(String sql) {
        StringTokenizer tokenizer = new StringTokenizer(sql);
        //commit, select, ...
        String kind = tokenizer.nextToken().strip().toUpperCase(Locale.ROOT);
        if (con == null) throw new MatchException(MatchMessage.grammarError);
        try {
            if (kind.startsWith(DBType.DCL.COMMIT.name())) dcl.commit(con); //DCL
            else if (kind.startsWith(DBType.DCL.ROLLBACK.name())) dcl.rollback(con);
            else if (kind.startsWith(DBType.DDL.ALTER.name())) ddl.alter(con, sql); //DDL
            else if (kind.startsWith(DBType.DDL.DROP.name())) ddl.drop(con, sql);
            else if (kind.startsWith(DBType.DDL.CREATE.name())) ddl.create(con, sql);
            else if (kind.startsWith(DBType.DML.DELETE.name())) dml.delete(con, sql); //DML
            else if (kind.startsWith(DBType.DML.UPDATE.name())) dml.update(con, sql);
            else if (kind.startsWith(DBType.DML.INSERT.name())) dml.insert(con, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //select 문 [SELECT * FROM TEST][Value]
    //return = [ㅁㄴㅇㄹ, ㅁㄹㄴㅇ, ㅁㄹㅇㄴ]
    public String getSelectValue(String sql) {
        sql = sql.trim();
        String[] sqlValue = sql.substring(1, sql.length()-1).split("]\\[");
        if (sqlValue.length != 2) throw new MatchException(MatchMessage.grammarError);
        try {
            return dml.select(con, sqlValue[0], sqlValue[1]).toString();
        } catch (SQLException e) {
            return sql;
        }
    }

    public String getSelectValue(String sql, String value) {
        if (con == null) throw new MatchException(MatchMessage.grammarError);
        try {
            return dml.select(con, sql, value).toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return sql;
        }
    }
}
