package com.github.cyberryan1.netuno.utils.database.sql;

public enum SQLTableCreate {

    CREATE_PUNS_TABLE ( "CREATE TABLE IF NOT EXISTS punishments (" + // modified
            "id INT NOT NULL," +
            "player VARCHAR(100) NOT NULL," +
            "staff VARCHAR(100) NOT NULL," +
            "type VARCHAR(50) NOT NULL," +
            "date VARCHAR(100) NOT NULL," +
            "length VARCHAR(100) NOT NULL," +
            "reason VARCHAR(100) NOT NULL," +
            "active VARCHAR(20) NOT NULL," +
            "PRIMARY KEY (id));" ),

    CREATE_NOTIFS_TABLE( "CREATE TABLE IF NOT EXISTS notifs (" +
            "id INT NOT NULL," +
            "player VARCHAR(100) NOT NULL," +
            "PRIMARY KEY (id));" ),

    CREATE_IP_TABLE( "CREATE TABLE IF NOT EXISTS ip (" +
            "id INT NOT NULL," +
            "player VARCHAR(100) NOT NULL," +
            "ip VARCHAR(25) NOT NULL," +
            "PRIMARY KEY (id));" ),

    CREATE_IP_PUNS_TABLE( "CREATE TABLE IF NOT EXISTS ippuns (" +
            "id INT NOT NULL," +
            "player VARCHAR(100) NOT NULL," +
            "staff VARCHAR(100) NOT NULL," +
            "type VARCHAR(50) NOT NULL," +
            "date VARCHAR(100) NOT NULL," +
            "length VARCHAR(100) NOT NULL," +
            "reason VARCHAR(100) NOT NULL," +
            "active VARCHAR(20) NOT NULL," +
            "alts VARCHAR(300) NOT NULL," +
            "PRIMARY KEY (id));" ),

    CREATE_NO_SIGN_NOTIFS_TABLE( "CREATE TABLE IF NOT EXISTS nosignnotifs (" +
            "player VARCHAR(100) NOT NULL," +
            "PRIMARY KEY (player));" ),

    CREATE_REPORTS_TABLE( "CREATE TABLE IF NOT EXISTS reports(" +
            "id INT NOT NULL," +
            "target VARCHAR(100) NOT NULL," +
            "reporter VARCHAR(100) NOT NULL," +
            "date VARCHAR(100) NOT NULL," +
            "reason VARCHAR(100) NOT NULL," +
            "PRIMARY KEY (id));" ),

    CREATE_PUNISH_GUI_TABLE( "CREATE TABLE IF NOT EXISTS guipuns(" +
            "id INT NOT NULL," +
            "player VARCHAR(100) NOT NULL," +
            "type VARCHAR(50) NOT NULL," +
            "reason VARCHAR(100) NOT NULL," +
            "PRIMARY KEY (id));" ),

    CREATE_OTHER_TABLE( "CREATE TABLE IF NOT EXISTS other(" +
            "k VARCHAR(100) NOT NULL," + // modified
            "val VARCHAR(100) NOT NULL);" ); // modified


    private String contents;
    SQLTableCreate( String str ) {
        contents = str;
    }
    public String toString() { return contents; }
}