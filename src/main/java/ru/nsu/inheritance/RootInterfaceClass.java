package ru.nsu.inheritance;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RootInterfaceClass {
    private Class<?> root;
    private Method[] methods;

    public RootInterfaceClass(String packageName) {
        Class<?> interfaze = findRootInterface(packageName);
        if (interfaze == null) {
            throw new IllegalArgumentException("Can't find interface with annotation @RootInterface");
        }
        this.root = interfaze;
        this.methods = interfaze.getDeclaredMethods();
    }

    static class MyMethodInterceptor implements MethodInterceptor {
        public static <T> T create(Class<T> clazz) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(new MyMethodInterceptor());
            return (T) enhancer.create();
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            // Дополнительная логика для генерации методов
            // Например, проверка наличия аннотаций и генерация кода
            return proxy.invokeSuper(obj, args);
        }
    }

    private Class<?> findRootInterface(String packageName) {
        InputStream stream = null;
        BufferedReader reader = null;
        try {
            stream = ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(packageName.replaceAll("[.]", "/"));
            if (stream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(stream));
            Set<Class> classes = reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet());
            if (!classes.isEmpty()) {
                return classes.stream().filter(Class::isInterface).
                        filter(i -> i.getAnnotation(RootInterface.class) != null).
                        findFirst().orElse(null);
            } else {
                return null;
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                System.err.println("Error occurred while trying closing reader or stream");
            }
        }
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
