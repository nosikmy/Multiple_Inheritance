package ru.nsu.inheritance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.inheritance.annotations.Extends;
import ru.nsu.inheritance.examples.IRoot;
import ru.nsu.inheritance.examples.TestIRoot;

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
            e.voidMethodForAllClasses(2);
            System.setOut(stdOut);

            String actual = outputStream.toString();
            String expected = """
                    E: intMethodForAllClasses, parameter: 2\r
                    C: intMethodForAllClasses, parameter: 2\r
                    D: intMethodForAllClasses, parameter: 2\r
                    B: intMethodForAllClasses, parameter: 2\r
                    A: intMethodForAllClasses, parameter: 2\r
                    """;
            Assertions.assertEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRootMethod() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outputStream)) {

            E e = MultipleInheritancer.create(E.class);
            PrintStream stdOut = System.out;

            System.setOut(printStream);
            Assertions.assertEquals(e.messageToA("Hello from E"), "Hello from A");
            System.setOut(stdOut);
            String actualOutput = outputStream.toString();
            String expectedOutput = "A: rootMethod, parameter: Hello from E\r\n";
            Assertions.assertEquals(expectedOutput, actualOutput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        E e = MultipleInheritancer.create(E.class);
        e.a();
//        f.a(2);
    }

    /* Classes structure

                                                                               IRoot
                                                                             /      \
                                voidMethodForAllClasses, messageToA, a -->  A        B <-- voidMethodForAllClasses, a, b
                                                                           / \      /
                                         voidMethodForAllClasses, b  -->  C   D  <-- voidMethodForAllClasses, a
                                                                          \   |   /
                                                                           \  |  /
                                                                            \ | /
                                           voidMethodForAllClasses, a, b --> E--F  <-- voidMethodForAllClasses, methodNotFromIRoot
    */
    static abstract class A implements TestIRoot {
        private final String name = "A";

        @Override
        public void voidMethodForAllClasses(int i) {
            System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
        }

        @Override
        public String messageToA(String message) {
            System.out.println(name + ": rootMethod, parameter: " + message);
            return "Hello from " + name;
        }

        @Override
        public void a(){
            System.out.println("This method a() from A");
        }
    }

    static abstract class B implements TestIRoot {
        private final String name = "B";

        @Override
        public void voidMethodForAllClasses(int i) {
            System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
        }

        @Override
        public void a(){
            System.out.println("This method a() from B");
        }

        @Override
        public void b(){
            System.out.println("This method b() from B, Call a()");
            a();
        }
    }

    @Extends({A.class})
    static abstract class C implements TestIRoot {
        private final String name = "C";

        @Override
        public void voidMethodForAllClasses(int i) {
            System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
        }

        @Override
        public void b(){
            System.out.println("This method b() from C, Call a()");
            a();
        }
    }

    @Extends({A.class})
    static abstract class D implements TestIRoot {
        private final String name = "D";

        @Override
        public void voidMethodForAllClasses(int i) {
            System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
        }

        @Override
        public void a(){
            System.out.println("This method a() from D");
        }
    }

    @Extends({C.class, D.class, B.class})
    static abstract class E implements TestIRoot {
        private final String name = "E";

        @Override
        public void voidMethodForAllClasses(int i) {
            System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
        }

        @Override
        public void a(){
            System.out.println("This method a() from E, Call b()");
            b();
        }

        @Override
        public void b(){
            System.out.println("This method b() from E");
        }
    }

    @Extends({C.class, D.class, B.class})
    static abstract class F implements TestIRoot {
        private final String name = "F";

        @Override
        public void voidMethodForAllClasses(int i) {
            System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
        }
    }
}

