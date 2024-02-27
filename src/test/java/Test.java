import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public interface RootInterface {
        void name();
    }

    public static class A implements RootInterface {
        @Override
        public void name() {
            System.out.print("A");
        }
    }

    @Extends({A.class})
    public static class B implements RootInterface {
        @Override
        public void name() {
            System.out.print("B");
        }
    }

    @Extends({A.class})
    public static class C implements RootInterface {
        @Override
        public void name() {
            System.out.print("C");
        }
    }

    @Extends({B.class, C.class})
    public static class D implements RootInterface {
        @Override
        public void name() {
            System.out.print("D");
        }
    }

    public static void main(String[] args) {
        List<Object> proxies = new ArrayList<>();
        proxies.add(createProxy(D.class));
        proxies.addAll(callNextMethod(D.class));
        for (Object proxy : proxies) {
            try {
                Method nameMethod = proxy.getClass().getMethod("name");
                nameMethod.invoke(proxy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Object> callNextMethod(Class<?> clazz) {
        List<Object> proxyList = new ArrayList<>();
        Extends annotation = clazz.getAnnotation(Extends.class);
        if (annotation != null) {
            Class<?>[] extendedClasses = annotation.value();
            for (Class<?> extendedClass : extendedClasses) {
                proxyList.add(createProxy(extendedClass));
            }
        }
        return proxyList;
    }

    public static <T> T createProxy(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        });
        return (T) enhancer.create();
    }
}

