package ru.nsu.inheritance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.inheritance.annotations.Extends;
import ru.nsu.inheritance.examples.IRoot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Multiple inheritance tests.
 */
public class MultipleInheritanceTest {
    // сделать больше тестов для разных случаев
    @Test
    public void testVoidMethod() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outputStream)) {

            E e = MultipleInheritancer.create(E.class);
            PrintStream stdOut = System.out;

            System.setOut(printStream);
            e.b(2);
            System.setOut(stdOut);

            String actual = outputStream.toString();
            String expected = "Eb 7\r\n" + "Cb 5\r\n" + "Db 6\r\n" + "Bb 4\r\n" + "Ab 3\r\n";
            Assertions.assertEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testIntMethod() {
        E e = MultipleInheritancer.create(E.class);
        int actual = e.a(0);
        int expected = 2;
        Assertions.assertEquals(expected, actual);
    }

    public static void main(String[] args) {
        F f = MultipleInheritancer.create(F.class);
//        f.say();
//        f.a(2);
    }

    /* Classes structure

           IRoot
         /      \
        A        B
       / \      /
      C   D    /
      \   |   /
       \  |  /
        \ | /
          E
    */
    static class A implements IRoot {

        @Override
        public int a(int i) {
            System.out.println("Method a from class A");
            return 1 + i;
        }

        @Override
        public void b(int i) {
            int res = 1 + i;
            System.out.println("Ab " + res);
        }
    }

    static class B implements IRoot {
        @Override
        public int a(int i) {
            System.out.println("Method a from class B");
            return 2 + i;
        }

        @Override
        public void b(int i) {
            int res = 2 + i;
            System.out.println("Bb " + res);
        }
    }

    @Extends({A.class})
    static abstract class C implements IRoot {
        @Override
        public void b(int i) {
            int res = 3 + i;
            System.out.println("Cb " + res);
        }
    }

    @Extends({A.class})
    static abstract class D implements IRoot {

        @Override
        public void b(int i) {
            int res = 4 + i;
            System.out.println("Db " + res);
        }
    }

    @Extends({C.class, D.class, B.class})
    static abstract class E implements IRoot {
        @Override
        public void b(int i) {
            int res = 5 + i;
            System.out.println("Eb " + res);
        }

        public void say() {
            System.out.println("sdasd");
        }
    }

    @Extends({C.class, D.class, B.class})
    static abstract class F implements IRoot {
        @Override
        public void b(int i) {
            int res = 5 + i;
//            int res1 = a(2);
//            System.out.println("Fb " + res + " " + res1);
            System.out.println("Fb " + res);
        }

        @Override
        public int a(int i) {
            b(2);
            return 0;
        }

        public void say() {
            System.out.println("sdasd");
        }
    }
}

