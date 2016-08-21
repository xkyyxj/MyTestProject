package mytest;

import jdk.internal.dynalink.linker.MethodHandleTransformer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangqchf on 2016/8/19.
 */
public class GetMethodTest extends GetMethodTestParent{

    public Parent getList(List<String> _list){
        System.out.println("List!!");
        System.out.println(_list.get(0));
        return new Parent();
    }

    public Parent getList(ArrayList<String> _list)
    {
        System.out.println("ArrayList!!");
        System.out.println(_list.get(0));
        return new Child();
    }

    public Parent getList(Parent p)
    {
        System.out.println("Parent!!");
        return new Parent();
    }

    public Child getList(Child c)
    {
        System.out.println("Child!!");
        return new Child();
    }

    /*public Parent getList(ArrayList<String> _list)
    {

    }*/

    public static void main(String[] args)
    {
        GetMethodTest getMethodTest = new GetMethodTest();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(arrayList.getClass().toString());
        List<String> list = new ArrayList<>();
        list.add(list.getClass().toString());
        String methodName = "getList";
        Class<?>[] classes1 = new Class[1];
        Class<?>[] classes2 = new Class[1];
        classes1[0] = arrayList.getClass();
        classes2[0] = list.getClass();
        try {
            Method method1 = getMethodTest.getClass().getMethod(methodName,classes1);
            method1.invoke(getMethodTest,arrayList);
            Method method2 = getMethodTest.getClass().getMethod(methodName,classes2);
            method2.invoke(getMethodTest,list);
            Parent p = new Parent();
            Child c = new Child();
            classes1[0] = p.getClass();
            classes2[0] = c.getClass();
            Method method3 = getMethodTest.getClass().getMethod(methodName,classes1);
            method3.invoke(getMethodTest,p);
            Method method4 = getMethodTest.getClass().getMethod(methodName,classes2);
            method4.invoke(getMethodTest,c);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

class GetMethodTestParent{
    public Child getTest(ArrayList<String> _list)
    {
        System.out.println("Parent ArrayList!!");
        System.out.println(_list.get(0));
        return null;
    }
}

class Parent{

}

class Child extends Parent{

}
