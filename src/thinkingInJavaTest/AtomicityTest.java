package thinkingInJavaTest;

import nc.bs.framework.execute.Executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/18.
 */
public class AtomicityTest implements Runnable{
    private int i = 0;
    public int getValue(){return i;}
    private synchronized void evenIncrement(){
        i++;
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i++;
    }
    @Override
    public void run() {
        while(true)
        {
            evenIncrement();
        }
    }

    public static void main(String[] args)
    {
        ExecutorService executorService = Executors.newCachedThreadPool();
        AtomicityTest atomicityTest = new AtomicityTest();
        executorService.execute(atomicityTest);
        while(true)
        {
            int value = atomicityTest.getValue();
            if(value % 2 != 0)
            {
                System.out.println(value);
                System.exit(0);
            }
            else
                System.out.println("the value is " + value);
        }
    }
}
