package com.github.cyberryan1.netuno.guis.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class GUIEventManager implements Listener {

    private static final ArrayList<Object> objects = new ArrayList<>();

    @EventHandler
    public void onInventoryClick( InventoryClickEvent event ) {
        for ( int index = objects.size() - 1; index >= 0; index-- ) {
            Object obj = objects.get( index );
            for ( Method method : obj.getClass().getMethods() ) {
                if ( method.isAnnotationPresent( GUIEventInterface.class ) && method.getParameterTypes().length == 1 ) {
                    if ( method.getAnnotation( GUIEventInterface.class ).type() == GUIEventType.INVENTORY_CLICK ) {
                        try { method.invoke( obj, event );
                        } catch ( InvocationTargetException e ) { e.printStackTrace();
                        } catch ( IllegalAccessException e ) { e.printStackTrace(); }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag( InventoryDragEvent event ) {
        for ( int index = objects.size() - 1; index >= 0; index-- ) {
            Object obj = objects.get( index );
            for ( Method method : obj.getClass().getMethods() ) {
                if ( method.isAnnotationPresent( GUIEventInterface.class ) && method.getParameterTypes().length == 1 ) {
                    if ( method.getAnnotation( GUIEventInterface.class ).type() == GUIEventType.INVENTORY_DRAG ) {
                        try { method.invoke( obj, event );
                        } catch ( InvocationTargetException e ) { e.printStackTrace();
                        } catch ( IllegalAccessException e ) { e.printStackTrace(); }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose( InventoryCloseEvent event ) {
        for ( int index = objects.size() - 1; index >= 0; index-- ) {
            Object obj = objects.get( index );
            for ( Method method : obj.getClass().getMethods() ) {
                if ( method.isAnnotationPresent( GUIEventInterface.class ) && method.getParameterTypes().length == 1 ) {
                    if ( method.getAnnotation( GUIEventInterface.class ).type() == GUIEventType.INVENTORY_CLOSE ) {
                        try { method.invoke( obj, event );
                        } catch ( InvocationTargetException e ) { e.printStackTrace();
                        } catch ( IllegalAccessException e ) { e.printStackTrace(); }
                    }
                }
            }
        }
    }

    public static void addEvent( Object obj ) { objects.add( obj ); }

    public static void removeEvent( Object obj ) { objects.remove( obj ); }

    public static ArrayList<Object> getObjects() { return objects; }
}
