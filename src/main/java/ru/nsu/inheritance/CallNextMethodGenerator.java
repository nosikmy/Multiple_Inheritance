package ru.nsu.inheritance;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

public class CallNextMethodGenerator implements MethodInterceptor {
    private static Deque<Class<?>> queue = new ArrayDeque<>();
    private static Deque<Class<?>> resultingQueue = new ArrayDeque<>();
    public static <T> T create(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new CallNextMethodGenerator());
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        callNextMethod(obj, method, args);
        return proxy.invokeSuper(obj, args);
    }

    private static void callNextMethod(Object obj, Method method, Object[] args) {
        if (obj.getClass().getSuperclass().getAnnotation(Extends.class) == null) {
            return;
        }
        List<Class<?>> extendsClasses = new ArrayList<>(List.of(obj.getClass().getSuperclass()));
        extendsClasses.addAll(Arrays.stream(obj.getClass().getSuperclass().
                getAnnotation(Extends.class).value()).toList());

        addToQueue(extendsClasses);
        System.out.println(queue);
        Class<?>[] classes = obj.getClass().getSuperclass().getAnnotation(Extends.class).value();
        for (Class<?> aClass : classes) {
                Object instanse = CallNextMethodGenerator.create(aClass);
            try {
                Method m = instanse.getClass().getMethod(method.getName(), method.getParameterTypes());
                m.invoke(instanse, args);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
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
