package com.github.cyberryan1.netuno.api.exceptions;

/**
 * Thrown when a punishment is attempted to be executed but is
 * missing some required data or its current data is invalid
 *
 * @author Ryan
 */
public class IncompletePunishmentException extends RuntimeException {
    public IncompletePunishmentException( String message ) {
        super( message );
    }
}