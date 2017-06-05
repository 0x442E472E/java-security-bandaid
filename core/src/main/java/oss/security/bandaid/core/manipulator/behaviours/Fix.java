package oss.security.bandaid.core.manipulator.behaviours;

import javassist.CtBehavior;
import javassist.CtMethod;

import java.util.Map;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public interface Fix {
    void apply(CtBehavior ctBehavior, Map<String, String> metadata) throws FixException;

    public static class FixException extends Exception {
        public FixException() {
        }

        public FixException(String message) {
            super(message);
        }

        public FixException(String message, Throwable cause) {
            super(message, cause);
        }

        public FixException(Throwable cause) {
            super(cause);
        }
    }
}
