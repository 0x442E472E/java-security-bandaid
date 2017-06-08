package oss.security.bandaid.support;

import java.util.Map;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public interface BandaidHandler {
    public static final int LAST_HANDLER = Integer.MAX_VALUE;
    public int getBandaidOrder();

    /**
     *
     * @param sender
     * @param args
     * @return true if the event has been handled and no further handlers shall be invoked. This will also suppress the default behaviour
     */
    public boolean handleMethodEvent(Map<String, String> metadata, Object sender, String methodName, Object...args);
    /**
     *
     * @param sender
     * @param args
     * @return true if the event has been handled and no further handlers shall be invoked. This will also suppress the default behaviour
     */
    public boolean handleStaticMethodEvent(Map<String, String> metadata, Class sender, String methodName, Object...args);

    public void handleStaticMethodBlock(Map<String, String> metadata, Class sender, String methodName, Object... args);

    public void handleMethodBlock(Map<String, String> metadata, Object sender, String methodName, Object... args);
}
