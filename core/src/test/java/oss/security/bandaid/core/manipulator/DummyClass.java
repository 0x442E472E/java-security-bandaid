package oss.security.bandaid.core.manipulator;

/**
 * Created by 0x442E472E on 25.05.2017.
 */
public class DummyClass {

    public DummyClass() {

    }

    public void method() {
        System.out.println("do something");
    }

    public void method(String string) {
        System.out.println("do something");
        double d = 1;
        for(int i = 0; i < 10; ++i) {
            d += Math.random();
        }
        System.out.println(d);
    }

    public void method(int integer) {
        System.out.println("do something");
    }

    public void otherMethod() {
        System.out.println("do something");
    }

    private void privateMethod() {
        System.out.println("do something");
    }

    public static void staticMethod() {
        System.out.println("do something");
    }
    public static void staticMethod(String arg) {
        System.out.println("do something");
    }

    public void emptyMethod() {

    }
}
