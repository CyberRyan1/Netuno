package com.github.cyberryan1.netuno.apimplement.database.helpers;

public enum SQLiteTables {

    PUNS_TABLE( "CREATE TABLE IF NOT EXISTS punishments (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`player` STRING NOT NULL," +
            "`staff` STRING NOT NULL," +
            "`type` INTEGER NOT NULL," +
            "`length` INTEGER NOT NULL," +
            "`timestamp` INTEGER NOT NULL," +
            "`reason` STRING NOT NULL," +
            "`active` INTEGER NOT NULL," +
            "`guipun` INTEGER NOT NULL," +
            "`reference` INTEGER NOT NULL," +
            "`notif` INTEGER NOT NULL );"
    ),

//    IP_HISTORY_TABLE( "CREATE TABLE IF NOT EXISTS ip (" +
//            "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
//            "`uuid` STRING NOT NULL," +
//            "`ip` STRING NOT NULL," +
//            "`group_id` INTEGER DEFAULT -1 NOT NULL );"
//    ),

    IP_TABLE( "CREATE TABLE IF NOT EXISTS ip (" +
            "inde INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "ip VARCHAR(20)," +
            "uuid VARCHAR(40) );" ),

    REPORTS_TABLE( "CREATE TABLE IF NOT EXISTS reports (" +
            "`id` INTEGER PRIMARY KEY," +
            "`player` STRING NOT NULL," +
            "`reporter` STRING NOT NULL," +
            "`timestamp` INTEGER NOT NULL," +
            "`reason` STRING NOT NULL );"
    ),

    RANDOM_TABLE( "CREATE TABLE IF NOT EXISTS random (" +
            "`k` STRING NOT NULL," +
            "`v` STRING NOT NULL );"
    );

    private String sql;
    SQLiteTables( String sql ) {
        this.sql = sql;
    }

    public String getSql() { return sql; }
}