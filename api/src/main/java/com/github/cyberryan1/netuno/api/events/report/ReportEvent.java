package com.github.cyberryan1.netuno.api.events.report;

import com.github.cyberryan1.netuno.api.events.NetunoEvent;
import com.github.cyberryan1.netuno.api.models.ApiReport;

import java.util.List;

/**
 * This event is fired when a player is reported
 *
 * @author Ryan
 */
public class ReportEvent implements NetunoEvent {

    private final List<ApiReport> reports;

    public ReportEvent( List<ApiReport> report ) {
        this.reports = report;
    }

    /**
     * @return The list of {@link ApiReport} that were sent
     */
    public List<ApiReport> getReport() {
        return reports;
    }
}