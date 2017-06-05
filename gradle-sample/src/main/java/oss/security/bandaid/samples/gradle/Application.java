package oss.security.bandaid.samples.gradle;

import oss.security.bandaid.support.Bandaid;
import oss.security.bandaid.support.BandaidHandlerAdapter;

import java.util.Map;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class Application {
    public static void main(String[] args){
        Bandaid.addHandler(new BandaidHandlerAdapter() {
            @Override
            public boolean handleStaticMethodEvent(Map<String, String> metadata, Class sender, String methodName, Object... args) {
                System.out.println(String.format("Received call to %s.%s with %d arguments", sender.getName(), methodName, args.length));
                return false;
            }

            @Override
            public boolean handleMethodEvent(Map<String, String> metadata, Object sender, String methodName, Object... args) {
                return false;
            }
        });
        someMethod();
    }

    private static void someMethod() {
        System.out.println("Fix not applied.");
    }
}
