package ru.nsu.inheritance;

import ru.nsu.inheritance.annotations.Extends;
import ru.nsu.inheritance.examples.IRoot;

public class Main {
    public static void main(String[] args) {
        E e = MultipleInheritancer.create(E.class);
//        e.adsa();
//        int ans = e.a(1);
//        System.out.println(ans);
        e.b(1);
    }

    static class A implements IRoot {

        @Override
        public int a(int i) {
            System.out.println("Method a from class A");
            return 0;
        }

        @Override
        public void b(int i) {
            System.out.println("Method b from class A, value = " + i);
        }
    }

    static class B implements IRoot {
        @Override
        public int a(int i) {
            System.out.println("Method a from class B");
            return 0;
        }

        @Override
        public void b(int i) {
            System.out.println("Method b from class B, value = " + i);
        }
    }

    @Extends({A.class})
    static abstract class C implements IRoot {
//        @Override
//        public int a(int i) {
//            System.out.println("Method a from class C");
//            return 0;
//        }

        @Override
        public void b(int i) {
            System.out.println("Method b from class C, value = " + i);
        }
    }

    @Extends({A.class})
    static abstract class D implements IRoot {
//        @NotImplemented
//        @Override
//        public int a(int i) {
//            System.out.println("Method a from class D");
//            return 0;
//        }

        @Override
        public void b(int i) {
            System.out.println("Method b from class D, value = " + i);
        }
    }

    @Extends({C.class, D.class, B.class})
    static abstract class E implements IRoot {
//        @Override
//        public int a(int i) {
//            System.out.println("OVERRIDE Method a from class E");
//            return 0;
//        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
