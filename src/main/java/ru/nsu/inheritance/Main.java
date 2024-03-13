package ru.nsu.inheritance;

import ru.nsu.inheritance.examples.IRoot;

public class Main {
    public static void main(String[] args) {
        E e = MultipleInheritancer.create(E.class, "ru.nsu.inheritance.examples");
        Object obj = e.say(1);

    }

//    static class RootClass implements IRoot {
//        @Override
//        public void say(int i) {
//            System.out.println("Root" + i);
//        }
//    }

//    @Extends({RootClass.class})
    static class A implements IRoot {
        @Override
        public int say(int i) {
            System.out.println("A" + i);
            return i;
        }
    }
//    @Extends({RootClass.class})
    static class B implements IRoot {
        @Override
        public int say(int i) {
            System.out.println("B" + i);
            return i;
        }
    }
    @Extends({A.class})
    static class C implements IRoot {
        @Override
        public int say(int i) {
            System.out.println("C" + i);
            return i;
        }
    }

    @Extends({A.class})
    static class D implements IRoot {
        @Override
        public int say(int i) {
            System.out.println("D" + i);
            return i;
        }
    }

    @Extends({C.class, D.class, B.class})
    static class E implements IRoot {
        @NotImplemented
        @Override
        public int say(int i) {
//            System.out.println("E" + i);
            return i;
        }
    }
}
