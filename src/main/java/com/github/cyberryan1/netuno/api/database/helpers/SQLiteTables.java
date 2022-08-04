package com.github.cyberryan1.netuno.api.database.helpers;

public enum SQLiteTables {

    PUNS_TABLE( "CREATE TABLE IF NOT EXISTS punishments (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "`player` STRING NOT NULL," +
            "`data` BLOB NOT NULL," +
            "`guipun` STRING NOT NULL," +
            "`reference` INTEGER NOT NULL );"
    ),

    ALTS_TABLE( "CREATE TABLE IF NOT EXISTS alts ("  +
            "`group` INTEGER," +
            "`item` STRING NOT NULL," +
            "`type` STRING NOT NULL );"
    ),

    REPORTS_TABLE( "CREATE TABLE IF NOT EXISTS reports (" +
            "`id` INTEGER PRIMARY KEY," +
            "`player` STRING NOT NULL," +
            "`data` BLOB NOT NULL );"
    ),

    RANDOM_TABLE( "CREATE TABLE IF NOT EXISTS random (" +
            "`key` STRING NOT NULL," +
            "`value` STRING NOT NULL );"
    );

    private String sql;
    SQLiteTables( String sql ) {
        this.sql = sql;
    }

    public String getSql() { return sql; }
}