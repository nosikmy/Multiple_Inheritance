package ru.nsu.inheritance;

import ru.nsu.inheritance.annotations.RootInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class RootInterfaceFinder {

    @SuppressWarnings("unchecked")
    protected static Class<?> findRootInterface(String packageName) {
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

    private static Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
