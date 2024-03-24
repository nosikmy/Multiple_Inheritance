package ru.nsu.inheritance;

import ru.nsu.inheritance.annotations.Extends;
import ru.nsu.inheritance.examples.IRoot1;
import ru.nsu.inheritance.examples.IRoot2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        /*
            Ca 4
            Aa 4
            Ba 4
            Gc 3
            Dc 3
            Ec 3
            Fc 3
         */
        FirstTree.C c = MultipleInheritancer.create(FirstTree.C.class);
        SecondTree.G g = MultipleInheritancer.create(SecondTree.G.class);
        ExecutorService es = Executors.newFixedThreadPool(2);
        es.submit(() -> {
            c.a(4);
        });
        es.submit(() -> {
            g.c(3);
        });
        es.shutdown();
    }

    static class FirstTree {
        static abstract class A implements IRoot1 {
            @Override
            public void a(int x) {
                System.out.println("Aa " + x);
            }
        }

        static abstract class B implements IRoot1, IRoot2 {
            @Override
            public void a(int x) {
                System.out.println("Ba " + x);
            }
        }

        @Extends({A.class, B.class})
        static class C implements IRoot1 {

            @Override
            public void a(int x) {
                System.out.println("Ca " + x);
            }

            @Override
            public int b(int x) {
                return 0;
            }
        }
    }

    static class SecondTree {
        static abstract class D implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println("Dc " + x);
            }
        }

        static abstract class E implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println("Ec " + x);
            }
        }

        static abstract class F implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println("Fc " + x);
            }
        }

        @Extends({D.class, E.class, F.class})
        static class G implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println("Gc " + x);
            }

            @Override
            public long d(int x) {
                return 0;
            }
        }
    }
}
