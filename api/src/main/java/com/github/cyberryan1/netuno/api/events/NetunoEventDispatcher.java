package com.github.cyberryan1.netuno.api.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to dispatch events to listeners and manage those listeners.
 *
 * @author Ryan
 */
public class NetunoEventDispatcher {

    private List<NetunoEventListener> listeners = new ArrayList<>();

    /**
     * Adds a new listener to the list of listeners.
     * @param listener The listener to add
     */
    public void addListener( NetunoEventListener listener ) {
        listeners.add( listener );
    }

    /**
     * Removes a listener from the list of listeners.
     * @param listener The listener to remove
     */
    public void removeListener( NetunoEventListener listener ) {
        listeners.remove( listener );
    }

    /**
     * Checks if a listener is in the list of listeners.
     * @param listener The listener to check for
     * @return true if the listener is in the list, false otherwise
     */
    public boolean containsListener( NetunoEventListener listener ) {
        return listeners.contains( listener );
    }

    /**
     * Dispatches an event to all listeners.
     * @param event The event to dispatch
     */
    public void dispatch( NetunoEvent event ) {
        for ( NetunoEventListener listener : listeners ) {
            listener.onEvent( event );
        }
    }
}