package ru.nsu.inheritance;

import ru.nsu.inheritance.examples.IRoot;

public class Main {
    public static void main(String[] args) {
        E e = MultipleInheritancer.create(E.class, "ru.nsu.inheritance.examples");
//        Object obj = e.a(1);

    }

    static class A implements IRoot {

        @Override
        public int a(int i) {
            System.out.println("Method a from class A");
            return 0;
        }

        @Override
        public int b(int i) {
            System.out.println("Method b from class A");
            return 0;
        }
    }
//    @Extends({RootClass.class})
    static class B implements IRoot {
    @Override
    public int a(int i) {
        System.out.println("Method a from class B");
        return 0;
    }

    @Override
    public int b(int i) {
        System.out.println("Method b from class B");
        return 0;
    }
    }
    @Extends({A.class})
    static class C implements IRoot {
        @Override
        public int a(int i) {
            System.out.println("Method a from class C");
            return 0;
        }

        @Override
        public int b(int i) {
            System.out.println("Method b from class C");
            return 0;
        }
    }

    @Extends({A.class})
    static class D implements IRoot {
        @NotImplemented
        @Override
        public int a(int i) {
            System.out.println("Method a from class D");
            return 0;
        }

        @Override
        public int b(int i) {
            System.out.println("Method b from class D");
            return 0;
        }
    }

    @Extends({C.class, D.class, B.class})
    static abstract class E implements IRoot {
        @Override
        public int a(int i) {
            System.out.println("OVERRIDE Method a from class E");
            return 0;
        }
    }
}
