package com.github.cyberryan1.netuno.api;

import com.github.cyberryan1.netuno.api.database.ConnectionManager;
import com.github.cyberryan1.netuno.api.database.DatabaseManager;
import com.github.cyberryan1.netunoapi.NetunoApi;
import com.github.cyberryan1.netunoapi.database.DatabaseConnection;
import com.github.cyberryan1.netunoapi.database.NetunoDatabases;

public class ApiNetuno implements NetunoApi {

    private DatabaseConnection connection = new ConnectionManager();
    private NetunoDatabases databases = new DatabaseManager();

    @Override
    public DatabaseConnection getConnectionManager() {
        return connection;
    }

    @Override
    public NetunoDatabases getDatabaseManager() {
        return databases;
    }
}
