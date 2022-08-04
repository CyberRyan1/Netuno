package com.github.cyberryan1.netuno.api.database;

import com.github.cyberryan1.netunoapi.database.*;

public class DatabaseManager implements NetunoDatabases {

    private static AltsDatabase altsDatabase = new NetunoAltsDatabase();
    private static PunishmentsDatabase punDatabase = new NetunoPunishmentsDatabase();
    private static RandomDatabase randomDatabase = new NetunoRandomDatabase();
    private static ReportsDatabase reportsDatabase = new NetunoReportsDatabase();

    /**
     * @return The {@link AltsDatabase} instance
     */
    public AltsDatabase getAltsDatabase() {
        return altsDatabase;
    }

    /**
     * @return The {@link AltsDatabase} instance
     */
    public AltsDatabase getAlts() {
        return altsDatabase;
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

}