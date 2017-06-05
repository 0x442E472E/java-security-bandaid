package oss.security.bandaid.core.processor.writers;

import javassist.CannotCompileException;
import javassist.CtClass;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.loader.jar.JarFile;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

public class SpringJarClassWriter implements ClassWriter {
    protected File jarFile;
    protected List<JarUtils.SaveEntry> saveEntries = new ArrayList<>();

    public SpringJarClassWriter(File jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public void write(CtClass ctClass) throws CannotCompileException, IOException {
        String name = ctClass.getName();
        byte[] bytecode = ctClass.toBytecode();
        saveEntries.add(new JarUtils.SaveEntry(bytecode, name));
    }

    public void saveJar() throws IOException {
        //init files and variables
        File tempDir = File.createTempFile("spring", "extracted");
        tempDir.deleteOnExit();
        File tempRootJarFile = File.createTempFile("spring",".jar");
        tempRootJarFile.deleteOnExit();
        tempDir.delete();
        tempDir.mkdirs();
        FileUtils.copyFile(jarFile, new File(jarFile+".beforebandaid"));
        FileUtils.copyFile(jarFile, tempRootJarFile);
        JarFile baseJar = new JarFile(tempRootJarFile);
        extractNestedDependencies( baseJar, tempDir);

        //build a map with every jar file (so, base jar + nested dependency jars)
        List<JarFile> jarFiles = new ArrayList<>();
        jarFiles.add(baseJar);
        File[] files = tempDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return FilenameUtils.isExtension(name, "jar");
            }
        });
        Map<JarFile, File> jarFileToFile = new HashMap<>();
        if(files != null) {
            for(File file: files) {
                JarFile tmp = new JarFile(file);
                jarFiles.add(tmp);
                jarFileToFile.put(tmp, file);
            }
        }
        List<File> nestedFiles = new ArrayList<>();
        ClassMap classMap = ClassMap.of(jarFiles.toArray(new JarFile[jarFiles.size()]));

        //for every nested dependency jar in the extraction folder, apply the changes in a temp file and overwrite
        //the original one
        for(Map.Entry<JarFile, List<String>> entry: classMap.getJarFileToClasses().entrySet()) {
            List<JarUtils.SaveEntry> converted = saveEntries.stream().filter(s -> entry.getValue().contains(s.getFilename())).map(s -> new JarUtils.SaveEntry(s.getBytes(), classMap.getNameInJar(s.getFilename()))).collect(Collectors.toList());
            if(!converted.isEmpty()) {
                if(entry.getKey() != baseJar) {
                    File tempFile = JarUtils.saveJarFile(entry.getKey(), converted);
                    FileUtils.copyFile(tempFile,jarFileToFile.get(entry.getKey()));
                    nestedFiles.add(jarFileToFile.get(entry.getKey()));
                }
            }
        }

        //build a new jar where changes to the base jar + changes to nested dependency jars are applied
        List<String> rootJarClasses = classMap.getJarFileToClasses().get(baseJar);
        List<JarUtils.SaveEntry> converted = saveEntries.stream().filter(s -> rootJarClasses.contains(s.getFilename())).map(s -> new JarUtils.SaveEntry(s.getBytes(), classMap.getNameInJar(s.getFilename()))).collect(Collectors.toList());
        for(File nestedFile: nestedFiles) {
            try(FileInputStream in = new FileInputStream(nestedFile)) {
                byte[] bytes = IOUtils.toByteArray(in);//;JarUtils.jarToUncompressedBytes(nestedFile);
                converted.add(new JarUtils.SaveEntry(bytes, "BOOT-INF/lib/"+nestedFile.getName(), false));
            }

        }
        File tempJarFile = JarUtils.saveJarFile(baseJar, converted);

        //now overwrite the original one
        if(tempJarFile != null) {
            baseJar.close();
            if(!jarFile.exists() || jarFile.delete()){
                FileUtils.copyFile(tempJarFile, jarFile);
            }else {
                throw new IOException("Could not delete original jar file");
            }
        }
    }

    protected static void extractNestedDependencies(JarFile jar, File tempDir) throws IOException {
        Enumeration<JarEntry> jarEntryEnumeration = jar.entries();
        while(jarEntryEnumeration.hasMoreElements()) {
            java.util.jar.JarEntry jarEntry = jarEntryEnumeration.nextElement();
            if(jarEntry.getName().startsWith("BOOT-INF/lib/") && jarEntry.getName().endsWith(".jar")) {
                File dest = new File(tempDir, FilenameUtils.getName(jarEntry.getName()));
                dest.deleteOnExit();
                FileUtils.copyToFile(jar.getInputStream(jarEntry), dest);
//                JarFile nestedJarFile = jar.getNestedJarFile(jarEntry);
//                Enumeration<JarEntry> nestedJarEntryEnumeration = nestedJarFile.entries();
//                try(JarOutputStream out = new JarOutputStream(new FileOutputStream(dest))) {
//                    out.setLevel(ZipOutputStream.STORED);
//                    while(nestedJarEntryEnumeration.hasMoreElements()) {
//                        java.util.jar.JarEntry nestedJarEntry = nestedJarEntryEnumeration.nextElement();
//                        JarEntry newJarEntry = new JarEntry(nestedJarEntry.getCve());
//                        out.putNextEntry(newJarEntry);
//                        out.write(IOUtils.toByteArray(nestedJarFile.getInputStream(nestedJarEntry)));
//                        out.closeEntry();
//                    }
//                }
            }
        }
    }

    @Override
    public void close() throws IOException {

    }

    private static class ClassMap {
        private final Map<String, JarFile> classToJarFile;
        private final Map<JarFile, List<String>> jarFileToClasses;
        private final Set<String> springClasses;

        public ClassMap(final Map<String, JarFile> classToJarFile, final Map<JarFile, List<String>> jarFileToClasses, final Set<String> springClasses) {
            this.classToJarFile = classToJarFile;
            this.jarFileToClasses = jarFileToClasses;
            this.springClasses = springClasses;
        }

        public Map<JarFile, List<String>> getJarFileToClasses() {
            return jarFileToClasses;
        }

        public boolean containsClass(String clazz) {
            return classToJarFile.containsKey(clazz);
        }

        public JarFile getJarFile(String clazz) {
            if(classToJarFile.containsKey(clazz)) {
                return classToJarFile.get(clazz);
            } else {
                throw new IllegalArgumentException("Class not found: "+clazz);
            }
        }

        public String getNameInJar(String clazz) {
            if(springClasses.contains(clazz)) {
                return "BOOT-INF/classes/" + packageNameToFileName(clazz);
            } else if(classToJarFile.containsKey(clazz)) {
                return packageNameToFileName(clazz);
            } else {
                throw new IllegalArgumentException("Class not found: "+clazz);
            }
        }

        protected String packageNameToFileName(String name) {
            return name.replace(".", "/") + ".class";
        }

        public static ClassMap of(JarFile[] jars) throws IOException {

            Map<String, JarFile> classToJarFile = new HashMap<>();
            Set<String> springClasses = new HashSet<>();
            final Map<JarFile, List<String>> jarFileToClasses = new HashMap<>();

            for(JarFile jar: jars) {
                jarFileToClasses.put(jar, new ArrayList<>());
                Enumeration<JarEntry> jarEntryEnumeration = jar.entries();
                while(jarEntryEnumeration.hasMoreElements()) {
                    java.util.jar.JarEntry jarEntry = jarEntryEnumeration.nextElement();
                    if(jarEntry.getName().toLowerCase().endsWith(".class")) {
                        if(jarEntry.getName().startsWith("BOOT-INF/classes/")) {
                            String clazz = jarEntry.getName().substring("BOOT-INF/classes/".length(), jarEntry.getName().length() - ".class".length()).replace("/", ".");
                            classToJarFile.put(clazz, jar);
                            springClasses.add(clazz);
                            jarFileToClasses.get(jar).add(clazz);
                        } else {
                            String clazz = jarEntry.getName().substring(0, jarEntry.getName().length() - ".class".length()).replace("/", ".");
                            classToJarFile.put(clazz, jar);
                            jarFileToClasses.get(jar).add(clazz);
                        }
                    }

                }
            }
            return new ClassMap(classToJarFile, jarFileToClasses, springClasses);
        }
    }
}
