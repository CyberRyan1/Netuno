package com.github.cyberryan1.netuno.utils.data;

public enum SQLiteCreateTables {

    CREATE_PUNISHMENTS_TABLE( "CREATE TABLE IF NOT EXISTS punishments (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "uuid VARCHAR(36) NOT NULL," +
            "punishment BLOB NOT NULL );" );;

    private final String sql;
    SQLiteCreateTables( String sql ) {
        this.sql = sql;
    }

    public String toString() { return sql; }
}