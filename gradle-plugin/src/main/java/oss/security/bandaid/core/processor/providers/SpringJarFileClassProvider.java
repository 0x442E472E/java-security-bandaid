package oss.security.bandaid.core.processor.providers;

import oss.security.bandaid.core.processor.providers.ClassProvider;
import javassist.*;
import org.springframework.boot.loader.jar.JarFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by 0x442E472E on 29.05.2017.
 */
public class SpringJarFileClassProvider implements ClassProvider {
    private ClassPool classPool;
    private List<ClassPath> classPaths = Collections.synchronizedList(new ArrayList<>());
    private URLClassLoader urlClassLoader;
    private JarFile jarFile;

    public SpringJarFileClassProvider(boolean useDefaultClasspath) {
        classPool = new ClassPool(useDefaultClasspath);

    }

    public void addJarFile(File file) throws IOException {
        addJarFile(new JarFile(file));
    }
    public void addJarFile(JarFile jarFile) throws IOException {
        this.jarFile = jarFile;
        urlClassLoader = new URLClassLoader(findUrls(jarFile), null);
        ClassPath classPath = new LoaderClassPath(urlClassLoader);
        classPaths.add(classPath);
        classPool.appendClassPath(classPath);
    }

    private URL[] findUrls(JarFile jarFile) throws IOException {
        List<URL> urls = new ArrayList<>();
        urls.add(new URL(jarFile.getUrl().toString()+"BOOT-INF/classes/"));
        Enumeration<java.util.jar.JarEntry> jarEntryEnumeration = jarFile.entries();
        while(jarEntryEnumeration.hasMoreElements()) {
            java.util.jar.JarEntry jarEntry = jarEntryEnumeration.nextElement();
            if(jarEntry.getName().startsWith("BOOT-INF/lib/") && jarEntry.getName().toLowerCase().endsWith(".jar")) {
                JarFile nestedJarFile = jarFile.getNestedJarFile(jarEntry);
                urls.add(nestedJarFile.getUrl());
                nestedJarFile.close();
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }

    @Override
    public CtClass getClass(String className) throws NotFoundException {
        return classPool.get(className);
    }

    @Override
    public void close() throws IOException {
        if(jarFile != null) {
            jarFile.close();
        }
        if(urlClassLoader != null) {
            urlClassLoader.close();
        }
        for(ClassPath cp: classPaths) {
            classPool.removeClassPath(cp);
        }
    }
}
