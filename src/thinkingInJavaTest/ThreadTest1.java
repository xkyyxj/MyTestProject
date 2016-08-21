package thinkingInJavaTest;

import nc.bs.framework.execute.Executor;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangqchf on 2016/8/17.
 */
public class ThreadTest1 {
    public static void main(String[] args)
    {
        EventGenerator.test();
    }
}

class EventChecker implements Runnable{
    private IntGenerator generator;
    private final int id;
    public EventChecker(IntGenerator g,int _id)
    {
        generator = g;
        id= _id;
    }
    @Override
    public void run() {
        while(!generator.isCanceled())
        {
            int val = generator.next();
            if(val % 2 != 0)
            {
                System.out.println(val + "not even");
                generator.cancel();
            }
            else
                System.out.println("working right!");
        }
    }

    public static void test(IntGenerator generator,int count)
    {
        System.out.println("control-c to exit!");
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0;i < count;i++)
        {
            executorService.execute(new EventChecker(generator,i));
        }
        executorService.shutdown();
    }
}
abstract class IntGenerator{
    private volatile boolean canceled = false;
    public abstract int next();
    public void cancel() {canceled = true;}
    public boolean isCanceled() {return canceled;}
}

class EventGenerator extends IntGenerator{

    private int currentValue = 0;

    @Override
    public int next() {
       ++currentValue;
       ++currentValue;
       return currentValue;
    }

    public static void test(){
        EventChecker.test(new EventGenerator(),10);
    }
}
