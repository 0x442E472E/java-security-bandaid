package oss.security.bandaid.support;

import java.util.Map;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class BandaidHandlerAdapter implements BandaidHandler {

    @Override
    public int getBandaidOrder() {
        return 0;
    }

    @Override
    public boolean handleMethodEvent(Map<String, String> metadata, Object sender, String methodName, Object... args) {
        return false;
    }

    @Override
    public boolean handleStaticMethodEvent(Map<String, String> metadata, Class sender, String methodName, Object... args) {
        return false;
    }

}
