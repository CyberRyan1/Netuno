package com.github.cyberryan1.netuno.api.exceptions;

/**
 * Thrown when a report is attempted to be executed but is
 * missing some required data or its current data is invalid
 *
 * @author Ryan
 */
public class IncompleteReportException extends RuntimeException {
    public IncompleteReportException( String message ) {
        super( message );
    }
}