package thinkingInJavaTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/19.
 */
public class NumberInitTest {
    public static void main(String[] args)
    {
        System.out.println("Before forName");
        try {
            Class.forName("thinkingInJavaTest.ClassA");
            TimeUnit.SECONDS.sleep(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("After forName");
        //TestClass testClass = new TestClass();
    }
}

class TestClass
{
    private ClassA classA = new ClassA("dynamic initialized!!!");
    public static ClassA classB = new ClassA("Static initialized!!");
    public TestClass(){
        System.out.println("TestClass Initialized!!");
    }
}

class ClassA{
    public ClassA(String input){
        System.out.println("ClassA initialized " + input);
    }
}
