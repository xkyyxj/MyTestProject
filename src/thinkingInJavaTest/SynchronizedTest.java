package thinkingInJavaTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/17.
 */
/**
 * Synchronized keyword only works on one object.
 * Different objects can't share the lock of Synchronized*/
public class SynchronizedTest {
    private int id;
    public SynchronizedTest(int _id)
    {
        id = _id;
    }
    public synchronized void task(){
        System.out.println("Task id enter: " + id);
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        System.out.println("Task id leave: " + id);
    }

    public static void main(String[] args)
    {
        SynchronizedTest synchronizedTest = new SynchronizedTest(11);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0;i < 10;i++)
            executorService.execute(new RunTask());
        for(int i = 0;i < 10;i++)
            executorService.execute(new RunTaskOneObject(synchronizedTest));
        executorService.shutdown();
    }
}

class RunTask implements Runnable{
    private static int id = 0;
    @Override
    public void run() {
        SynchronizedTest test = new SynchronizedTest(id++);
        test.task();
    }
}

class RunTaskOneObject implements Runnable{
    SynchronizedTest synchronizedTest = null;
    public RunTaskOneObject(SynchronizedTest _test)
    {
        synchronizedTest = _test;
    }
    @Override
    public void run() {
        synchronizedTest.task();
    }
}
