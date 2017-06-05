package oss.security.bandaid.core.processor.writers;

import javassist.CannotCompileException;
import javassist.CtClass;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public interface ClassWriter extends AutoCloseable, Closeable {
    public void write(CtClass ctClass) throws CannotCompileException, IOException;
}
