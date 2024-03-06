package ru.nsu.inheritance;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class CallNextMethodGenerator implements MethodInterceptor {
    private static final Deque<Class<?>> queue = new ArrayDeque<>();
    private static Deque<Class<?>> resultingQueue = new ArrayDeque<>();

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CallNextMethodGenerator());
        return (T) enhancer.create();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        callNextMethod(obj, method, args);
//        return proxy.invokeSuper(obj, args);
        if (queue.isEmpty() && !resultingQueue.isEmpty()) {
            resultingQueue.forEach(c -> {
                try {
                    Object instance = c.newInstance();
                    Method m = c.getMethod(method.getName(), method.getParameterTypes());
                    m.invoke(instance, args);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                }
            });
            resultingQueue = new ArrayDeque<>();
        }
        return null;
    }

    private static void callNextMethod(Object obj, Method method, Object[] args) {
        if (obj.getClass().getSuperclass().getAnnotation(Extends.class) == null) {
            return;
        }

        if (resultingQueue.isEmpty()) {
            resultingQueue.addFirst(obj.getClass().getSuperclass());
        }
        List<Class<?>> extendsClasses =
                Arrays.stream(obj.getClass().getSuperclass().getAnnotation(Extends.class).value()).toList();

        addToQueue(extendsClasses);

        while (!queue.isEmpty()) {
            Class<?> aClass = queue.pollFirst();
            resultingQueue.addFirst(aClass);
            if (aClass.getAnnotation(Extends.class) != null) {
                extendsClasses = Arrays.stream(aClass.getAnnotation(Extends.class).value()).toList();
                addToQueue(extendsClasses);
            }
        }
//        System.out.println(obj.getClass().getName() + " EXTENDS " + Arrays.toString(Arrays.stream(classes).toArray()));
    }

    private static void addToQueue(List<Class<?>> classes) {
        classes.forEach(cl -> {
            if (!queue.contains(cl)) {
                queue.addLast(cl);
            }
        });
    }
}
