import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

public class Main {
    public static void main(String[] args) {
        // Создание экземпляра Enhancer
        Enhancer enhancer = new Enhancer();
        // Установка класса, для которого будет создан прокси
        enhancer.setSuperclass(MyClass.class);
        // Установка метода-перехватчика
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println("Before method " + method.getName());
                Object result = proxy.invokeSuper(obj, args);
                System.out.println("After method " + method.getName());
                return result;
            }
        });
        // Создание прокси-объекта
        MyClass proxy = (MyClass) enhancer.create();
        // Вызов метода на прокси-объекте
        proxy.doSomething();
    }

    static class MyClass {
        public void doSomething() {
            System.out.println("Doing something...");
        }
    }
}
