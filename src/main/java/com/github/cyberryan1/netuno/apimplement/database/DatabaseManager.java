package com.github.cyberryan1.netuno.apimplement.database;

import com.github.cyberryan1.netunoapi.database.*;

public class DatabaseManager implements NetunoDatabases {

    private static NetunoIpHistoryDatabase ipHistoryDatabase = new NetunoIpHistoryDatabase();
    private static NetunoPunishmentsDatabase punDatabase = new NetunoPunishmentsDatabase();
    private static NetunoRandomDatabase randomDatabase = new NetunoRandomDatabase();
    private static NetunoReportsDatabase reportsDatabase = new NetunoReportsDatabase();

    /**
     * @return The {@link IpHistoryDatabase} instance
     */
    public IpHistoryDatabase getIpHistoryDatabase() {
        return ipHistoryDatabase;
    }

    /**
     * @return The {@link NetunoIpHistoryDatabase} instance
     */
    public NetunoIpHistoryDatabase getIpHistory() { return ipHistoryDatabase; }

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