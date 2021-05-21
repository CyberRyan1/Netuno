package com.github.cyberryan1.netuno.utils.database;

public class Errors {

    public static String sqlConnectionExecute() {
        return "Couldn't execute MySQL statement: ";
    }

    public static String sqlConnectionClose() {
        return "Failed to close MySQL connection: ";
    }

    public static String noSQLConnection() {
        return "Unable to retreive MySQL connection: ";
    }

    public static String noTableFound() {
        return "Database Error: no table found";
    }
}
