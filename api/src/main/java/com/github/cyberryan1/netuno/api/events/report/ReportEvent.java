package com.github.cyberryan1.netuno.api.events.report;

import com.github.cyberryan1.netuno.api.events.NetunoEvent;
import com.github.cyberryan1.netuno.api.models.ApiReport;

/**
 * This event is fired when a player is reported
 *
 * @author Ryan
 */
public class ReportEvent implements NetunoEvent {

    private final ApiReport report;

    public ReportEvent( ApiReport report ) {
        this.report = report;
    }

    /**
     * @return The {@link ApiReport} that was sent
     */
    public ApiReport getReport() {
        return report;
    }
}