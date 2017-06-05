package oss.security.bandaid.core.processor.providers;

import javassist.CtClass;
import javassist.NotFoundException;

import java.io.Closeable;

/**
 * Created by 0x442E472E on 25.05.2017.
 *
 * Loads a CtClass by name
 */
public interface ClassProvider extends AutoCloseable, Closeable {
    public CtClass getClass(String className) throws NotFoundException;
}
