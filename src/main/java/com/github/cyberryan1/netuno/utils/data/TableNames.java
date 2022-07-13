package com.github.cyberryan1.netuno.utils.data;

public enum TableNames {

    PUNISHMENTS( "punishments" );

    private final String tableName;
    TableNames( String tableName ) {
        this.tableName = tableName;
    }
    @Override
    public String toString() { return tableName; }
}