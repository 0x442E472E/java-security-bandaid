package oss.security.bandaid.core.manipulator;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by 0x442E472E on 03.06.2017.
 */
public class Selector {
    private String className;
    private String methodName;
    private String methodDescriptor;

    public Selector(String className, String methodName, String methodDescriptor) {
        if(className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Invalid className: " + className);
        }
        if(methodName != null && methodName.isEmpty()) {
            methodName = null;
        }
        if(methodDescriptor != null && methodDescriptor.isEmpty()) {
            methodDescriptor = null;
        }
        this.className = className;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;

    }

    public Selector(String className, String methodName) {
        this(className, methodName, null);
    }

    public Selector(String className) {
        this(className, null, null);
    }


    public List<CtBehavior> selectFromClass(CtClass clazz) throws NotFoundException {
        List<CtBehavior> result = new ArrayList<>();
        if(methodName == null) { // all methods of a class
            result.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            result.addAll(Arrays.asList(clazz.getDeclaredConstructors()));
        } else if(methodDescriptor == null) {// all methods with a specific name
            if(methodName.equals(clazz.getSimpleName())) {
                result.addAll(Arrays.asList(clazz.getDeclaredConstructors()));
            } else {
                result.addAll(Arrays.asList(clazz.getDeclaredMethods(methodName)));
            }

        } else {// a very specific method
            if(methodName.equals(clazz.getSimpleName())) {
                result.add(clazz.getConstructor(methodDescriptor));
            } else {
                result.add(clazz.getMethod(methodName, methodDescriptor));
            }
        }
        return result;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMethodDescriptor() {
        return methodDescriptor;
    }

    @Override
    public String toString() {
        return "Selector{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", methodDescriptor='" + methodDescriptor + '\'' +
                '}';
    }
}
