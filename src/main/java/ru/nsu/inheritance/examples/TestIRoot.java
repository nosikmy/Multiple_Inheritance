package ru.nsu.inheritance.examples;

import ru.nsu.inheritance.annotations.RootInterface;

@RootInterface
public interface TestIRoot {
    void voidMethodForAllClasses(int i);
    String messageToA(String message);
    void a(String s);
    void b(String s);
}