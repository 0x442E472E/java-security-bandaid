package oss.security.bandaid.core.processor.writers;

import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by 0x442E472E on 27.05.2017.
 */
public class JarClassWriter implements ClassWriter {
    private static final Logger logger = Logger.getLogger(JarClassWriter.class.getName());
    protected File jarFile;
    protected JarFile jar;

    public JarClassWriter(File jarFile) {
        this.jarFile = jarFile;
    }

    public JarClassWriter(File jarFile, JarFile jar) {
        this.jarFile = jarFile;
        this.jar = jar;
    }

    protected List<JarUtils.SaveEntry> saveEntries = new ArrayList<>();

    @Override
    public void write(CtClass ctClass) throws CannotCompileException, IOException {
        String name = packageNameToFileName(ctClass.getName());
        byte[] bytecode = ctClass.toBytecode();
        saveEntries.add(new JarUtils.SaveEntry(bytecode, name));
    }

    protected String packageNameToFileName(String name) {
        return name.replace(".", "/") + ".class";
    }

    public void saveJar() throws IOException {
        FileUtils.copyFile(jarFile, new File(jarFile+".beforebandaid"));
        if(jar == null) {
            jar = new JarFile(jarFile);
        }
        File tempJarFile = JarUtils.saveJarFile(jar, saveEntries);
        jar.close();
        if(jarFile.delete()){
            FileUtils.copyFile(tempJarFile, jarFile);
        }else {
            throw new IOException("Could not delete original jar file");
        }
    }

    @Override
    public void close() throws IOException {
        if(jar != null) {
            jar.close();
        }
    }


}
