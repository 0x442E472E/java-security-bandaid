package oss.security.bandaid.core.manipulator;

import oss.security.bandaid.core.manipulator.behaviours.StaticCodeFix;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static org.junit.Assert.fail;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class AnRule {

    CtClass clazz;

    @Before
    public void setup() throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        clazz = classPool.getCtClass(DummyClass.class.getName());
        clazz.setName(clazz.getName()+System.currentTimeMillis());
    }

    @Test
    public void canApplyAllMethods() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName()), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);

        //TODO: Write test that checks every method, not just the constructor
        Class newClass = clazz.toClass();
        try {
            Object object = newClass.newInstance();
            fail("Did not apply behaviour");
        } catch (RuntimeException e) {
            if(!e.getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
    }

    @Test
    public void canApplyPrivateMethod() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName(), "privateMethod"), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);
    }

    @Test
    public void canApplyByMethodName() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName(), "method"), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);

        Class newClass = clazz.toClass();
        Object object = newClass.newInstance();
        try {
            newClass.getMethod("method").invoke(object);
            fail("Did not apply behaviour");
        } catch (RuntimeException ignored) {
        } catch (InvocationTargetException e) {
            if(e.getCause() == null || !e.getCause().getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }

        newClass.getMethod("otherMethod").invoke(object); //should not throw an exception

        try {
            newClass.getMethod("method", Integer.TYPE).invoke(object, 0);
            fail("Did not apply behaviour");
        } catch (RuntimeException ignored) {

        } catch (InvocationTargetException e) {
            if(e.getCause() == null || !e.getCause().getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
        try {
            newClass.getMethod("method", String.class).invoke(object, "");
            fail("Did not apply behaviour");
        } catch (RuntimeException ignored) {

        } catch (InvocationTargetException e) {
            if(e.getCause() == null || !e.getCause().getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
    }

    @Test
    public void canApplyByMethodNameWithDescriptor() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName(), "method", "(Ljava/lang/String;)V"), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);

        Class newClass = clazz.toClass();
        Object object = newClass.newInstance();

        newClass.getMethod("method").invoke(object); //should not throw an exception
        newClass.getMethod("otherMethod").invoke(object); //should not throw an exception
        newClass.getMethod("method", Integer.TYPE).invoke(object, 0); //should not throw an exception
        try {
            newClass.getMethod("method", String.class).invoke(object, "");
            fail("Did not apply behaviour");
        } catch (RuntimeException ignored) {

        } catch (InvocationTargetException e) {
            if(e.getCause() == null || !e.getCause().getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
    }

    @Test
    public void canApplyToConstructor() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName(), clazz.getSimpleName()), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);

        Class newClass = clazz.toClass();



        try {
            Object object = newClass.newInstance();
            fail("Did not apply behaviour");
        } catch (RuntimeException e) {
            if(!e.getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
    }

    @Test
    public void canApplyEmptyMethod() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName(), "emptyMethod"), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);

        Class newClass = clazz.toClass();
        Object object = newClass.newInstance();

        try {
            newClass.getMethod("emptyMethod").invoke(object);
            fail("Did not apply behaviour");
        } catch (RuntimeException ignored) {

        } catch (InvocationTargetException e) {
            if(e.getCause() == null || !e.getCause().getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
    }

    @Test
    public void canApplyStaticMethod() throws Rule.RuleException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RuleGroup ruleGroup = new RuleGroup(new HashMap<>());
        Rule rule = new Rule(new Selector(DummyClass.class.getName(), "staticMethod"), new StaticCodeFix("throw new RuntimeException(\"TESTTEST\");", StaticCodeFix.Entrypoint.After), ruleGroup);
        rule.apply(clazz);

        Class newClass = clazz.toClass();
        Object object = newClass.newInstance();

        newClass.getMethod("method").invoke(object); //should not throw an exception
        newClass.getMethod("otherMethod").invoke(object); //should not throw an exception
        newClass.getMethod("method", Integer.TYPE).invoke(object, 0); //should not throw an exception
        try {
            newClass.getMethod("staticMethod").invoke(object);
            fail("Did not apply behaviour");
        } catch (RuntimeException ignored) {

        } catch (InvocationTargetException e) {
            if(e.getCause() == null || !e.getCause().getMessage().startsWith("TESTTEST")) {
                throw e;
            }
        }
    }
}
