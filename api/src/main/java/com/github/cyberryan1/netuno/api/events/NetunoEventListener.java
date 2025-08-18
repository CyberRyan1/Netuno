package com.github.cyberryan1.netuno.api.events;

/**
 * Should be implemented by all classes that want to listen to events.
 */
public interface NetunoEventListener {

    /**
     * Called when an event is fired.
     * @param event The event that was fired.
     */
    void onEvent( NetunoEvent event );
}