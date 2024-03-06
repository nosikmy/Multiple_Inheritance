package ru.nsu.inheritance;

import ru.nsu.inheritance.examples.IRoot;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
//        RootInterfaceClass rootInterfaceClass = new RootInterfaceClass("ru.nsu.inheritance.examples");
        E e = CallNextMethodGenerator.create(E.class);
        e.say();
    }

    static class RootClass implements IRoot {

        @Override
        public void say() {
            System.out.println("Root");
        }
    }

    @Extends({RootClass.class})
    static class A extends RootClass {
        @Override
        public void say() {
            System.out.println("A");
        }
    }
    @Extends({RootClass.class})
    static class B extends RootClass {
        @Override
        public void say() {
            System.out.println("B");
        }
    }
    @Extends({A.class})
    static class C extends RootClass {
        @Override
        public void say() {
            System.out.println("C");
        }
    }

    @Extends({A.class})
    static class D extends RootClass {
        @Override
        public void say() {
            System.out.println("D");
        }
    }

    @Extends({C.class, D.class, B.class})
    static class E extends RootClass {
        @Override
        public void say() {
            System.out.println("E");
        }
    }
}
