package oss.security.bandaid.support;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class AnBandaid {
    @Test
    public void sortsHandlers() {
        BandaidHandler h1 = new BandaidHandlerAdapter() {
            @Override
            public int getBandaidOrder() {
                return 1;
            }
        };
        BandaidHandler h2 = new BandaidHandlerAdapter() {
            @Override
            public int getBandaidOrder() {
                return 2;
            }
        };
        Bandaid.addHandler(h2);
        Bandaid.addHandler(h1);
        Bandaid.addHandler(h1);
        Bandaid.addHandler(h2);
        assertTrue(Bandaid.HANDLERS.get(0).getBandaidOrder() < Bandaid.HANDLERS.get(Bandaid.HANDLERS.size() -1).getBandaidOrder());
    }
}
