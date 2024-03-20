package ru.nsu.inheritance;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import ru.nsu.inheritance.annotations.Extends;
import ru.nsu.inheritance.annotations.RootInterface;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Multiple inheritance creator.
 * <p>
 * Usage:
 * Firstly, you need to init your classes that implements the same interface.
 * Then create annotations @Extends in such a way as to create multiple inheritance.
 * </p>
 * <p>
 * To create instance of class with multiple inheritance possibility execute
 * {@code MultipleInheritancer.create(yourClass)}
 * </p>
 * <p>
 * If you will call methods, that return {@code void}, method call will traverse all tree and
 * if it's possible will call methods in other classes in inheritance tree.
 * Otherwise (if method returns something except {@code void}) method call will execute method from the most specific class.
 * </p>
 */
public class MultipleInheritancer implements MethodInterceptor {
    private static boolean abstractFlag = false;
    private static Class<?> rootInterface = null;
    // Возникает проблема, если у пользователя два дерева с разными RootInterface?
    // Обработка исключений


    /**
     * Method to generate class bases on provided "clazz" with multiple inheritance possibility.
     *
     * @param clazz provided class
     * @param <T>   class type
     * @return generated class
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MultipleInheritancer());
        if (!abstractFlag) {
            rootInterface = null;
            for (Class<?> i : clazz.getInterfaces()) {
                if (i.getAnnotation(RootInterface.class) != null) {
                    if (rootInterface != null) {
                        throw new RuntimeException("The class has several root interfaces");
                    } else {
                        rootInterface = i;
                    }
                }
            }
            if (rootInterface == null) {
                throw new RuntimeException("The class doesn't have a root interface");
            }
        }
        return (T) enhancer.create();
    }

    /**
     * Method interceptor, which calls methods in BFS tree order.
     * If method return void, it will traverse all tree and if it's possible will call methods in other classes,
     * otherwise interceptor will execute method from the most specific class.
     *
     * @param obj    "this", the enhanced object
     * @param method intercepted Method
     * @param args   argument array; primitive types are wrapped
     * @param proxy  used to invoke super (non-intercepted method); may be called
     *               as many times as needed
     * @return the return value from the "method"
     * @throws Throwable if some exceptions occur
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (Arrays.stream(rootInterface.getDeclaredMethods()).
                noneMatch(intMethod -> intMethod.getName().equals(method.getName()))) {
            return proxy.invokeSuper(obj, args);
        }

        if (abstractFlag) {
            abstractFlag = false;
            return proxy.invokeSuper(obj, args);
        }
        Deque<Class<?>> resultingQueue = callNextMethod(obj, false);

        if (!resultingQueue.isEmpty()) {
            if (method.getReturnType().equals(void.class)) {
                for (Class<?> c : resultingQueue) {
                    Method m = c.getMethod(method.getName(), method.getParameterTypes());
                    if (!Modifier.isAbstract(m.getModifiers())) {
                        if (Modifier.isAbstract(c.getModifiers())) {
                            abstractFlag = true;
                            m.invoke(create(c), args);
                        } else {
                            m.invoke(c.getDeclaredConstructor().newInstance(), args);
                        }
                    }
                }
            } else {
                for (Class<?> c : resultingQueue) {
                    Method m = c.getMethod(method.getName(), method.getParameterTypes());
                    if (!Modifier.isAbstract(m.getModifiers())) {
                        if (Modifier.isAbstract(c.getModifiers())) {
                            abstractFlag = true;
                            return m.invoke(create(c), args);
                        } else {
                            return m.invoke(c.getDeclaredConstructor().newInstance(), args);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Method to collect classes using BFS to the resultingQueue.
     *
     * @param obj         entry class
     * @param changeOrder if false -> more specific class comes first, otherwise - vice versa
     */
    private static Deque<Class<?>> callNextMethod(Object obj, boolean changeOrder) {
        Deque<Class<?>> resultingQueue = new ArrayDeque<>();
        Deque<Class<?>> queue = new ArrayDeque<>();

        if (obj.getClass().getSuperclass().getAnnotation(Extends.class) == null) {
            return new ArrayDeque<>();
        }
        resultingQueue.addFirst(obj.getClass().getSuperclass());

        List<Class<?>> extendsClasses =
                Arrays.stream(obj.getClass().getSuperclass().getAnnotation(Extends.class).value()).toList();

        addToQueue(queue, extendsClasses);

        while (!queue.isEmpty()) {
            Class<?> aClass = queue.pollFirst();
            if (changeOrder) {
                resultingQueue.addFirst(aClass);
            } else {
                resultingQueue.addLast(aClass);
            }
            if (aClass.getAnnotation(Extends.class) != null) {
                extendsClasses = Arrays.stream(aClass.getAnnotation(Extends.class).value()).toList();
                addToQueue(queue, extendsClasses);
            }
        }
        return resultingQueue;
    }

    /**
     * Method that adds class to the queue.
     *
     * @param classes classes to add
     */
    private static void addToQueue(Deque<Class<?>> queue, List<Class<?>> classes) {
        classes.forEach(cl -> {
            if (Arrays.stream(cl.getInterfaces()).noneMatch(i -> i.getName().equals(rootInterface.getName()))) {
                throw new RuntimeException("Parent class doesn't have a root interface");
            }
            if (!queue.contains(cl)) {
                queue.addLast(cl);
            }
        });
    }
}
