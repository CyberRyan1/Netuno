package com.github.cyberryan1.netuno.database.helpers;

public enum SQLTables {

    PUNS_TABLE( "CREATE TABLE IF NOT EXISTS punishments (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "player VARCHAR(42) NOT NULL," +
            "staff VARCHAR(42) NOT NULL," +
            "type INTEGER NOT NULL," +
            "length INTEGER NOT NULL," +
            "timestamp INTEGER NOT NULL," +
            "reason TEXT NOT NULL," +
            "active INTEGER NOT NULL," +
            "guipun INTEGER NOT NULL," +
            "reference INTEGER NOT NULL," +
            "notif INTEGER NOT NULL );"
    ),

    IP_TABLE( "CREATE TABLE IF NOT EXISTS ip (" +
            "inde INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "ip VARCHAR(20)," +
            "uuid VARCHAR(40) );" ),

    REPORTS_TABLE( "CREATE TABLE IF NOT EXISTS reports (" +
            "id INTEGER PRIMARY KEY," +
            "player VARCHAR(42) NOT NULL," +
            "reporter VARCHAR(42) NOT NULL," +
            "timestamp INTEGER NOT NULL," +
            "reason TEXT NOT NULL );"
    ),

    RANDOM_TABLE( "CREATE TABLE IF NOT EXISTS random (" +
            "inde INTEGER PRIMARY KEY AUTO_INCREMENT," + // we ignore this column
            "k TEXT NOT NULL," +
            "v TEXT NOT NULL );"
    );

    private String sql;
    SQLTables( String sql ) {
        this.sql = sql;
    }

    public String getSql() { return sql; }
}