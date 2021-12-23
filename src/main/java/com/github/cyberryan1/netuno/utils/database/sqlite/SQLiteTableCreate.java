package com.github.cyberryan1.netuno.utils.database.sqlite;

public enum SQLiteTableCreate {

    CREATE_PUNS_TABLE(  "CREATE TABLE IF NOT EXISTS database (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "`staff` STRING NOT NULL," +
            "`type` STRING NOT NULL," +
            "`date` STRING NOT NULL," +
            "`length` STRING NOT NULL," +
            "`reason` STRING NOT NULL," +
            "`active` STRING NOT NULL," +
            "PRIMARY KEY (`id`));" ),

    CREATE_NOTIFS_TABLE( "CREATE TABLE IF NOT EXISTS notifs (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "PRIMARY KEY (`id`));" ),

    CREATE_IP_TABLE( "CREATE TABLE IF NOT EXISTS ip (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "`ip` STRING NOT NULL," +
            "PRIMARY KEY (`id`));" ),

    CREATE_IP_PUNS_TABLE( "CREATE TABLE IF NOT EXISTS ippuns (" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "`staff` STRING NOT NULL," +
            "`type` STRING NOT NULL," +
            "`date` STRING NOT NULL," +
            "`length` STRING NOT NULL," +
            "`reason` STRING NOT NULL," +
            "`active` STRING NOT NULL," +
            "`alts` STRING NOT NULL," +
            "PRIMARY KEY (`id`));" ),

    CREATE_NO_SIGN_NOTIFS_TABLE( "CREATE TABLE IF NOT EXISTS nosignnotifs (" +
            "`player` STRING NO NULL," +
            "PRIMARY KEY (`player`));" ),

    CREATE_REPORTS_TABLE( "CREATE TABLE IF NOT EXISTS reports(" +
            "`id` INTEGER NOT NULL," +
            "`target` STRING NOT NULL," +
            "`reporter` STRING NOT NULL," +
            "`date` STRING NOT NULL," +
            "`reason` STRING NOT NULL," +
            "PRIMARY KEY (`id`));" ),

    CREATE_PUNISH_GUI_TABLE( "CREATE TABLE IF NOT EXISTS guipuns(" +
            "`id` INTEGER NOT NULL," +
            "`player` STRING NOT NULL," +
            "`type` STRING NOT NULL," +
            "`reason` STRING NOT NULL," +
            "PRIMARY KEY (`id`));" ),

    CREATE_OTHER_TABLE( "CREATE TABLE IF NOT EXISTS other(" +
            "`key` STRING NOT NULL," +
            "`value` STRING NOT NULL);" );


    private String contents;
    SQLiteTableCreate( String str ) {
        contents = str;
    }
    public String toString() { return contents; }
}