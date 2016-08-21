package thinkingInJavaTest;

import sun.nio.cs.Surrogate;

/**
 * Created by wangqchf on 2016/8/17.
 */
public class TemplateTest {
    public static void main(String[] args)
    {
        int a = 10;
        NewTest test = new NewTest(a);
        test.printThis();
    }
}

class NewTest<T>{
    T what;
    public NewTest(T _what){
        what = _what;
    }

    public void printThis(){
        System.out.println(what);
    }
}
