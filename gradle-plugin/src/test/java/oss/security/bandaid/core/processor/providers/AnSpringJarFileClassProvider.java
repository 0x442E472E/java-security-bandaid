package oss.security.bandaid.core.processor.providers;

import javassist.NotFoundException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.boot.loader.jar.JarFile;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by 0x442E472E on 29.05.2017.
 */
public class AnSpringJarFileClassProvider {
    @Test
    public void canLoadFromSpringJar() throws IOException, NotFoundException {
        File tmpFile = File.createTempFile("test", ".jar");
        FileUtils.copyToFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/processor/spring-test.jar"), tmpFile);
        JarFile jarFile = new JarFile(tmpFile);
        SpringJarFileClassProvider classProvider = new SpringJarFileClassProvider(false);
        classProvider.addJarFile(jarFile);
        assertNotNull(classProvider.getClass("oss.security.bandaid.samples.gradle.Application"));
    }

    @Test
    public void doesNotBlockFileAfterClose() throws IOException, NotFoundException {
        File tmpFile = File.createTempFile("test", ".jar");
        FileUtils.copyToFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/processor/spring-test.jar"), tmpFile);
        JarFile jarFile = new JarFile(tmpFile);
        SpringJarFileClassProvider classProvider = new SpringJarFileClassProvider(false);
        classProvider.addJarFile(jarFile);
        classProvider.getClass("oss.security.bandaid.samples.gradle.Application");
        classProvider.close();
        assertTrue(tmpFile.delete());
    }
}
