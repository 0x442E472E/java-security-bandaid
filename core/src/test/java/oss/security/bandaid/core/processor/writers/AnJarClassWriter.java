package oss.security.bandaid.core.processor.writers;

import javassist.*;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by 0x442E472E on 27.05.2017.
 */
public class AnJarClassWriter {

    @Test
    public void canWriteJarFiles() throws IOException, NotFoundException, CannotCompileException {
        File tempJar = File.createTempFile("test",".jar");
        FileUtils.copyToFile(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/processor/providers/test.jar"),
                tempJar);
        long sizeBeforeModification = tempJar.length();

        ClassPool classPool = new ClassPool(null);
        classPool.appendSystemPath();
        ClassPath cp = classPool.appendClassPath(tempJar.getAbsolutePath());

        CtClass clazz = classPool.get("oss.security.bandaid.samples.gradle.Application");
        CtMethod method = clazz.getDeclaredMethod("someMethod");
        method.insertBefore("System.out.println(\"some text\");");

        JarClassWriter classWriter = new JarClassWriter(tempJar);
        classWriter.write(clazz);
        classPool.removeClassPath(cp);
        classWriter.saveJar();

        long sizeAfterModification = tempJar.length();

        assertNotEquals(sizeAfterModification, sizeBeforeModification);
    }

}
