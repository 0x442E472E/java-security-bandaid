package oss.security.bandaid.support;

import java.util.*;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class Bandaid {
    protected final static List<BandaidHandler> HANDLERS = Collections.synchronizedList(new ArrayList<>());
    protected final static Comparator<BandaidHandler> COMPARATOR = new Comparator<BandaidHandler>() {
        @Override
        public int compare(BandaidHandler o1, BandaidHandler o2) {
            return Integer.compare(o1.getBandaidOrder(), o2.getBandaidOrder());
        }
    };

    public static void addHandler(BandaidHandler handler) {
        synchronized (HANDLERS) {
            HANDLERS.add(handler);
            HANDLERS.sort(COMPARATOR);
        }
    }

    public boolean hasHandlers() {
        return HANDLERS.size() > 0;
    }

    public static void removeHandler(BandaidHandler handler) {
        HANDLERS.remove(handler);
    }

    public static boolean newMethodEvent(Map<String, String> metadata, Object sender, String methodname, Object...args) {
        synchronized (HANDLERS) {
            for(BandaidHandler handler: HANDLERS) {
                if(handler.handleMethodEvent(metadata, sender, methodname, args)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean newStaticMethodEvent(Map<String, String> metadata, Class sender, String methodname, Object...args) {
        synchronized (HANDLERS) {
            for(BandaidHandler handler: HANDLERS) {
                if(handler.handleStaticMethodEvent(metadata, sender, methodname, args)) {
                    return true;
                }
            }
        }
        return false;
    }


}
