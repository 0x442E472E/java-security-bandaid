package oss.security.bandaid.core.processor.providers;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class ClasspathClassProvider implements ClassProvider {
    private ClassPool classPool;
    private List<ClassPath> classPaths = Collections.synchronizedList(new ArrayList<>());

    public ClasspathClassProvider(boolean useDefaultClasspath) {
        classPool = new ClassPool(useDefaultClasspath);
    }

    public void addPath(String path) throws NotFoundException {
        classPaths.add(classPool.appendClassPath(path));
    }

    @Override
    public CtClass getClass(String className) throws NotFoundException {
        return classPool.get(className);
    }

    @Override
    public void close() throws IOException {
        for(ClassPath cp: classPaths) {
            classPool.removeClassPath(cp);
        }
    }
}
