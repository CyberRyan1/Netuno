package com.github.cyberryan1.netuno.utils.data;

public enum SQLCreateTables {

    CREATE_PUNISHMENTS_TABLE( "CREATE TABLE IF NOT EXISTS punishments (" +
            "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
            "uuid VARCHAR(36) NOT NULL," +
            "punishment BLOB NOT NULL );" );

    private final String sql;
    SQLCreateTables( String sql ) {
        this.sql = sql;
    }

    public String toString() { return sql; }
}