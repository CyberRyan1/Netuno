package com.github.cyberryan1.netuno.apimplement.database.helpers;

public enum SQLTables {

    PUNS_TABLE( "CREATE TABLE IF NOT EXISTS punishments (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "player VARCHAR(42) NOT NULL," +
            "data BLOB NOT NULL," +
            "guipun VARCHAR(8) NOT NULL," +
            "reference INTEGER NOT NULL );"
    ),

    ALTS_TABLE( "CREATE TABLE IF NOT EXISTS alts (" +
            "id INTEGER NOT NULL," +
            "item VARCHAR(42) NOT NULL " +
            "type VARCHAR(6) NOT NULL );"
    ),

    REPORTS_TABLE( "CREATE TABLE IF NOT EXISTS reports (" +
            "id INTEGER PRIMARY KEY," +
            "player VARCHAR(42) STRING NOT NULL," +
            "data BLOB NOT NULL );"
    ),

    RANDOM_TABLE( "CREATE TABLE IF NOT EXISTS random (" +
            "key TEXT NOT NULL," +
            "value TEXT NOT NULL );"
    );

    private String sql;
    SQLTables( String sql ) {
        this.sql = sql;
    }

    public String getSql() { return sql; }
}