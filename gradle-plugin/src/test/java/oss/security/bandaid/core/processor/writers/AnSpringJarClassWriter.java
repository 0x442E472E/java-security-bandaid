package oss.security.bandaid.core.processor.writers;

import oss.security.bandaid.core.io.rules.reader.RuleReader;
import oss.security.bandaid.core.manipulator.Rule;
import oss.security.bandaid.core.manipulator.RuleGroup;
import oss.security.bandaid.core.processor.RuleProcessor;
import oss.security.bandaid.core.processor.providers.SpringJarFileClassProvider;
import javassist.*;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.loader.jar.JarFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

public class AnSpringJarClassWriter {

    private File jarFile;
    private ClassPool classPool;
    private ClassPath classPath;
    private URLClassLoader classLoader;

    @Before
    public void prepare() throws IOException, NotFoundException {
        jarFile = File.createTempFile("test", ".jar");
        FileUtils.copyToFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/processor/spring-test.jar"), jarFile);
        File tempJarFile = File.createTempFile("test", ".jar");
        jarFile.deleteOnExit();
        tempJarFile.deleteOnExit();
        FileUtils.copyFile(jarFile, tempJarFile);
        classPool = new ClassPool(false);
        classLoader = new URLClassLoader(findUrls(new JarFile(tempJarFile)));
        classPath = new LoaderClassPath(classLoader);
        classPool.appendClassPath(classPath);
    }

    @Test
    public void canWriteClassFiles() throws IOException, NotFoundException, CannotCompileException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CtClass clazz = modifyClass("oss.security.bandaid.samples.gradle.Application", "someMethod");

        SpringJarClassWriter springJarClassWriter = new SpringJarClassWriter(jarFile);
        springJarClassWriter.write(clazz);
        classPool.removeClassPath(classPath);
        classLoader.close();
        springJarClassWriter.saveJar();

//        assertNotEquals(oldSize, jarFile.length());
        testMethod("oss.security.bandaid.samples.gradle.Application", "someMethod");
    }

    @Test
    public void canWriteDependencyFiles() throws IOException, NotFoundException, CannotCompileException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CtClass clazz = modifyClass("oss.security.bandaid.support.BandaidHandlerAdapter", "getBandaidOrder");

        SpringJarClassWriter springJarClassWriter = new SpringJarClassWriter(jarFile);
        springJarClassWriter.write(clazz);
        classPool.removeClassPath(classPath);
        classLoader.close();
        springJarClassWriter.saveJar();

//        assertNotEquals(oldSize, jarFile.length());
        testMethod("oss.security.bandaid.support.BandaidHandlerAdapter", "getBandaidOrder");
    }

    @Test
    public void canWriteClassAndDependencyFiles() throws IOException, NotFoundException, CannotCompileException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        CtClass clazz = modifyClass("oss.security.bandaid.samples.gradle.Application", "someMethod");
        CtClass clazz2 = modifyClass("oss.security.bandaid.support.BandaidHandlerAdapter", "getBandaidOrder");

        SpringJarClassWriter springJarClassWriter = new SpringJarClassWriter(jarFile);
        springJarClassWriter.write(clazz);
        springJarClassWriter.write(clazz2);
        classPool.removeClassPath(classPath);
        classLoader.close();
        springJarClassWriter.saveJar();

        testMethod("oss.security.bandaid.support.BandaidHandlerAdapter", "getBandaidOrder");
        testMethod("oss.security.bandaid.samples.gradle.Application", "someMethod");
    }

    @Test
    public void integratesWithSpringJarFileProviderAndRuleProcessor() throws IOException, Rule.RuleException, CannotCompileException, NotFoundException {
        File fixXmlPath = File.createTempFile("rules", ".xml");
        FileUtils.copyToFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("oss/security/bandaid/core/processor/rules.xml"), fixXmlPath);
        SpringJarClassWriter classWriter = new SpringJarClassWriter(jarFile);
        try(SpringJarFileClassProvider classProvider = new SpringJarFileClassProvider(false)) {
            classProvider.addJarFile(jarFile);
            RuleReader ruleReader = new RuleReader(fixXmlPath);
            List<RuleGroup> ruleGroups = ruleReader.read();
            RuleProcessor ruleProcessor = new RuleProcessor(ruleGroups, classProvider, classWriter);
            ruleProcessor.process();
        }
        classWriter.saveJar();

    }

    private CtClass modifyClass(String className, String methodname) throws NotFoundException, CannotCompileException {
        CtClass clazz = classPool.get(className);
        CtMethod method = clazz.getDeclaredMethod(methodname);
        method.insertBefore("throw new RuntimeException(\"TESTSUCCESS\");");
        return clazz;
    }

    private void testMethod(String className, String methodName) throws NotFoundException, CannotCompileException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        SpringJarFileClassProvider springJarFileClassProvider = new SpringJarFileClassProvider(false);
        springJarFileClassProvider.addJarFile(new JarFile(jarFile));
//        ClassPool classPool = new ClassPool(false);
//        classPool.appendClassPath(jarFile.getAbsolutePath());
        CtClass ctClass = springJarFileClassProvider.getClass(className);
        ctClass.setName(ctClass.getName()+System.currentTimeMillis());
        Class clazz = ctClass.toClass();
        Method method = clazz.getDeclaredMethod(methodName);
        method.setAccessible(true);
        try {
            if(java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                method.invoke(null);
            } else {
                Object instance = clazz.newInstance();
                method.invoke(instance);
            }
            fail("Did not apply changes");
        }catch(InvocationTargetException e) {
            if(e.getCause() == null || !(e.getCause() instanceof RuntimeException) || !e.getCause().getMessage().equals("TESTSUCCESS")) {
                throw e;
            }
        }
    }

    private URL[] findUrls(JarFile jarFile) throws IOException {
        List<URL> urls = new ArrayList<>();
        urls.add(new URL(jarFile.getUrl().toString()+"BOOT-INF/classes/"));
        Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while(jarEntryEnumeration.hasMoreElements()) {
            java.util.jar.JarEntry jarEntry = jarEntryEnumeration.nextElement();
            if(jarEntry.getName().startsWith("BOOT-INF/lib/") && jarEntry.getName().toLowerCase().endsWith(".jar")) {
                urls.add(jarFile.getNestedJarFile(jarEntry).getUrl());
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }

}
