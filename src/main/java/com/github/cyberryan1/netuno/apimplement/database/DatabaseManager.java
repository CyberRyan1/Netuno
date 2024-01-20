package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.*;

public class DatabaseManager implements NetunoDatabases {

//    private static NetunoAltsDatabase altsDatabase = new NetunoAltsDatabase();
    private static NetunoTempAltsDatabase tempAltsDatabase = new NetunoTempAltsDatabase();
    private static NetunoPunishmentsDatabase punDatabase = new NetunoPunishmentsDatabase();
    private static NetunoRandomDatabase randomDatabase = new NetunoRandomDatabase();
    private static NetunoReportsDatabase reportsDatabase = new NetunoReportsDatabase();

//    /**
//     * @return The {@link AltsDatabase} instance
//     */
//    public AltsDatabase getAltsDatabase() {
//        return altsDatabase;
//    }
//
//    /**
//     * @return The {@link AltsDatabase} instance
//     */
//    public AltsDatabase getAlts() {
//        return altsDatabase;
//    }
//
//    /**
//     * @return The {@link NetunoAltsDatabase} instance
//     */
//    public NetunoAltsDatabase getNetunoAlts() {
//        return altsDatabase;
//    }

    public TempAltsDatabase getTempAltsDatabase() {
        return tempAltsDatabase;
    }

    /**
     * @return The {@link PunishmentsDatabase} instance
     */
    public PunishmentsDatabase getPunDatabase() {
        return punDatabase;
    }

    /**
     * @return The {@link PunishmentsDatabase} instance
     */
    public PunishmentsDatabase getPun() {
        return punDatabase;
    }

    /**
     * @return The {@link NetunoPunishmentsDatabase} instance
     */
    public NetunoPunishmentsDatabase getNetunoPuns() {
        return punDatabase;
    }

    /**
     * @return The {@link RandomDatabase} instance
     */
    public RandomDatabase getRandomDatabase() {
        return randomDatabase;
    }

    /**
     * @return The {@link RandomDatabase} instance
     */
    public RandomDatabase getRandom() {
        return randomDatabase;
    }

    /**
     * @return The {@link NetunoRandomDatabase} instance
     */
    public NetunoRandomDatabase getNetunoRandom() {
        return randomDatabase;
    }

    /**
     * @return The {@link ReportsDatabase} instance
     */
    public ReportsDatabase getReportsDatabase() {
        return reportsDatabase;
    }

    /**
     * @return The {@link ReportsDatabase} instance
     */
    public ReportsDatabase getReports() {
        return reportsDatabase;
    }

    /**
     * @return The {@link NetunoReportsDatabase} instance
     */
    public NetunoReportsDatabase getNetunoReports() {
        return reportsDatabase;
    }
}