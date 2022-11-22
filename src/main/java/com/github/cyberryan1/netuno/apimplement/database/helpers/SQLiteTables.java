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

    ALTS_TABLE( "CREATE TABLE IF NOT EXISTS alts ("  +
            "`id` INTEGER NOT NULL," +
            "`item` STRING NOT NULL," +
            "`type` STRING NOT NULL );"
    ),

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