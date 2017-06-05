package oss.security.bandaid.core.util;

import oss.security.bandaid.core.io.rules.reader.RuleReader;
import oss.security.bandaid.core.manipulator.Selector;
import oss.security.bandaid.core.processor.providers.ClassProvider;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.*;

/**
 * Created by 0x442E472E on 04.06.2017.
 */
public class SelectorLister {
    private Set<String> classNames;
    private ClassProvider classProvider;

    public SelectorLister(Set<String> classNames, ClassProvider classProvider) {
        this.classNames = classNames;
        this.classProvider = classProvider;
    }

    public Map<String, List<FoundMethod>> detect() throws NotFoundException {
        Map<String, List<FoundMethod>> result = new HashMap<>();
        for(String className: classNames) {
            CtClass clazz = classProvider.getClass(className);
            List<FoundMethod> foundMethods = new ArrayList<>();
            for(CtMethod method: clazz.getDeclaredMethods()) {
                StringBuilder stringBuilder = new StringBuilder(className);
                stringBuilder.append(RuleReader.SELECTOR_SEPARATOR);
                stringBuilder.append(method.getName());
                stringBuilder.append(RuleReader.SELECTOR_SEPARATOR);
                stringBuilder.append(method.getSignature());
                foundMethods.add(new FoundMethod(cutLongName(method.getLongName(), className), stringBuilder.toString()));
            }
            for(CtConstructor constructor: clazz.getDeclaredConstructors()) {
                StringBuilder stringBuilder = new StringBuilder(className);
                stringBuilder.append(RuleReader.SELECTOR_SEPARATOR);
                stringBuilder.append(constructor.getName());
                stringBuilder.append(RuleReader.SELECTOR_SEPARATOR);
                stringBuilder.append(constructor.getSignature());
                foundMethods.add(new FoundMethod(cutLongName(constructor.getLongName(), className), stringBuilder.toString()));
            }
            result.put(className, foundMethods);
        }
        return result;
    }

    private static String cutLongName(String longName, String className) {
        return longName.substring(className.length());
    }

    public static class FoundMethod {
        private String name;
        private String selector;

        public FoundMethod(String name, String selector) {
            this.name = name;
            this.selector = selector;
        }

        public String getName() {
            return name;
        }

        public String getSelector() {
            return selector;
        }
    }
}
