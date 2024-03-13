package ru.nsu.inheritance;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class MultipleInheritancer implements MethodInterceptor {
    private static Class<?> rootInterface;
    private static final Deque<Class<?>> queue = new ArrayDeque<>();
    private static Deque<Class<?>> resultingQueue = new ArrayDeque<>();

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, String packageName) {
        Class<?> interfaze = RootInterfaceFinder.findRootInterface(packageName);
        if (interfaze == null) {
            throw new IllegalArgumentException("Can't find @RootInterface in package: \"" + packageName + "\"");
        }
        rootInterface = interfaze;

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MultipleInheritancer());
        return (T) enhancer.create();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        callNextMethod(obj, method, args);

        Object[] returningValues = null;
        int counter = 0;
//        return proxy.invokeSuper(obj, args);
        if (queue.isEmpty() && !resultingQueue.isEmpty()) {
            returningValues = new Object[resultingQueue.size()];
            for (Class<?> c : resultingQueue) {
                try {
                    Object instance = c.newInstance();
                    Method m = c.getMethod(method.getName(), method.getParameterTypes());

                    // Вызывается только если нет такой аннотации
                    if (m.getAnnotation(NotImplemented.class) == null) {
                        Object ret = m.invoke(instance, args);
                        if (ret != null) {
                            returningValues[counter++] = ret;
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException |
                         InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    // обработка случая когда класс не переписывает методы суперклассов
                }
            }
            resultingQueue = new ArrayDeque<>();
        }
        if (counter > 0) {
            // как-то вернуть в виде object'a массив object'ов
            return returningValues;
        }
        return null;
    }

    public static Object deserializeBytes(byte[] bytes) {
        Object obj;
        try {
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bytesIn);
            obj = ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }


    public static byte[] serializeObject(Object obj) {
        byte[] bytes;
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
            oos.writeObject(obj);
            oos.flush();
            bytes = bytesOut.toByteArray();
            bytesOut.close();
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
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
