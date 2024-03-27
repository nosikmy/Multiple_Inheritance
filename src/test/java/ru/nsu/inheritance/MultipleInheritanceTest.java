package ru.nsu.inheritance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.inheritance.annotations.Extends;
import ru.nsu.inheritance.examples.IRoot;
import ru.nsu.inheritance.examples.IRoot1;
import ru.nsu.inheritance.examples.IRoot2;
import ru.nsu.inheritance.examples.IRootWithoutAnnotation;
import ru.nsu.inheritance.examples.TestIRoot;
import ru.nsu.inheritance.exception.NoRootInterfaceException;
import ru.nsu.inheritance.exception.NoSuchMethodRealizationException;
import ru.nsu.inheritance.exception.SeveralRootInterfacesException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Multiple inheritance tests.
 */
public class MultipleInheritanceTest {
    // TODO: тест с возвращаемым значением
    @Test
    public void testVoidMethod() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outputStream)) {

            FirstTree.E e = MultipleInheritancer.create(FirstTree.E.class);
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

            FirstTree.E e = MultipleInheritancer.create(FirstTree.E.class);
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

    @Test
    void testCallEachOther() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outputStream)) {

            FirstTree.E e = MultipleInheritancer.create(FirstTree.E.class);
            PrintStream stdOut = System.out;

            System.setOut(printStream);
            e.a("");
            System.setOut(stdOut);

            String actual = outputStream.toString();
            String expected = """
                    This method a() from E, Call b()\r
                    -This method b() from E\r
                    -This method b() from C, Call a()\r
                    --This method a() from A\r
                    -This method b() from B, Call a()\r
                    --This method a() from B\r
                    This method a() from D\r
                    This method a() from B\r
                    This method a() from A\r
                    """;

            Assertions.assertEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testNoSuchMethodRealizationException() {
        FirstTree.A a = MultipleInheritancer.create(FirstTree.A.class);
        RuntimeException exception = assertThrows(NoSuchMethodRealizationException.class, () -> {
            a.b("");
        });
    }

    @Test
    void testNoRootInterfaceException() {
        BadTree.C c = MultipleInheritancer.create(BadTree.C.class);
        RuntimeException exception = assertThrows(NoRootInterfaceException.class, () -> {
            c.b("");
        });
    }

    @Test
    void testSeveralRootInterfacesException() {
        SecondTree.B b = MultipleInheritancer.create(SecondTree.B.class);
        RuntimeException exception = assertThrows(SeveralRootInterfacesException.class, () -> {
            b.a(2);
        });
    }

    @Test
    void testThreads() throws InterruptedException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream printStream = new PrintStream(outputStream)) {

            SecondTree.C c = MultipleInheritancer.create(SecondTree.C.class);
            ThirdTree.G g = MultipleInheritancer.create(ThirdTree.G.class);
            ExecutorService es = Executors.newFixedThreadPool(2);
            PrintStream stdOut = System.out;

            System.setOut(printStream);

            es.submit(() -> {
                c.a(1);
            });
            es.submit(() -> {
                g.c(2);
            });
            es.awaitTermination(1, TimeUnit.SECONDS);
            System.setOut(stdOut);
            List<String> threads = Arrays.stream(outputStream.toString().split("\r\n")).toList();
            List<String> thread1 = new ArrayList<>();
            List<String> thread2 = new ArrayList<>();
            for (String s : threads){
                if(s.charAt(0) == '1'){
                    thread1.add(s);
                }
                else {
                    thread2.add(s);
                }
            }
            System.out.println(thread1);
            System.out.println(thread2);
            Assertions.assertEquals(List.of("1 Ca", "1 Aa", "1 Ba"), thread1);
            Assertions.assertEquals(List.of("2 Gc", "2 Dc", "2 Ec", "2 Fc"), thread2);
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        SecondTree.C c = MultipleInheritancer.create(SecondTree.C.class);
        ThirdTree.G g = MultipleInheritancer.create(ThirdTree.G.class);
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
        /* Classes structure
                                                                             TestIRoot
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
            public void a(String s) {
                System.out.println(s + "This method a() from A");
            }
        }

        static abstract class B implements TestIRoot {
            private final String name = "B";

            @Override
            public void voidMethodForAllClasses(int i) {
                System.out.println(name + ": intMethodForAllClasses, parameter: " + i);
            }

            @Override
            public void a(String s) {
                System.out.println(s + "This method a() from B");
            }

            @Override
            public void b(String s) {
                System.out.println(s + "This method b() from B, Call a()");
                a(s + "-");
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
            public void b(String s) {
                System.out.println(s + "This method b() from C, Call a()");
                a(s + "-");
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
            public void a(String s) {
                System.out.println(s + "This method a() from D");
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
            public void a(String s) {
                System.out.println(s + "This method a() from E, Call b()");
                b(s + "-");
            }

            @Override
            public void b(String s) {
                System.out.println(s + "This method b() from E");
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

    static class BadTree {
            /* Classes structure
                                                                   TestIRoot     IRootWithoutAnnotation
                                                                          \      |
                                                                    a --> A2     B2 <-- a
                                                                            \   /
                                                                             \ /
                                                                       a --> C2
    */

        static abstract class A implements TestIRoot {
            private final String name = "A";

            @Override
            public void a(String s) {
                System.out.println(s + "This method a() from A");
            }
        }

        static abstract class B implements IRootWithoutAnnotation {
            private final String name = "B";

            @Override
            public void a(String s) {
                System.out.println(s + "This method a() from B");
            }
        }

        @Extends({A.class, B.class})
        static abstract class C implements TestIRoot {
            private final String name = "C";

            @Override
            public void a(String s) {
                System.out.println(s + "This method a() from C");
            }
        }
    }

    static class SecondTree {
        /* Classes structure
                                                                            IRoot1  IRoot2
                                                                             /   \ /
                                                                     a -->  A     B <-- a
                                                                             \   /
                                                                               C  <-- a, b
    */
        static abstract class A implements IRoot1 {
            @Override
            public void a(int x) {
                System.out.println(x + " Aa");
            }
        }

        static abstract class B implements IRoot1, IRoot2 {
            @Override
            public void a(int x) {
                System.out.println(x + " Ba");
            }
        }

        @Extends({A.class, B.class})
        static class C implements IRoot1 {

            @Override
            public void a(int x) {
                System.out.println(x + " Ca");
            }

            @Override
            public int b(int x) {
                return 0;
            }
        }
    }

    static class ThirdTree {
        /* Classes structure
                                                                                IRoot2
                                                                             /   |      \
                                                                     c -->  D    E <--c F <-- c
                                                                             \   |     /
                                                                              \  |    /
                                                                               \ |   /
                                                                                 G  <-- c, d
    */
        static abstract class D implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println(x + " Dc");
            }
        }

        static abstract class E implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println(x + " Ec");
            }
        }

        static abstract class F implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println(x + " Fc");
            }
        }

        @Extends({D.class, E.class, F.class})
        static class G implements IRoot2 {
            @Override
            public void c(int x) {
                System.out.println(x + " Gc");
            }

            @Override
            public long d(int x) {
                return 0;
            }
        }
    }
}
