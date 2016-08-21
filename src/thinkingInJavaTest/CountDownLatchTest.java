package thinkingInJavaTest;

import com.sun.javafx.tk.Toolkit;
import nc.bs.framework.execute.Executor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/21....................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................
 */
public class CountDownLatchTest {

    static final int SIZE = 100;
    public static void main(String[] args)
    {
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);
        for(int i = 0;i < 10;i++)
        {
            executorService.execute(new WaitingTask(countDownLatch));
        }
        for(int i = 0;i < SIZE;i++)
            executorService.execute(new TaskProtion(countDownLatch));
        System.out.println("All tasks started!! Sleeping for awhile");
        try {
            TimeUnit.MILLISECONDS.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("sleeping finished!!");
        executorService.execute(new WaitingTask(countDownLatch));
        executorService.shutdown();
    }

}

class TaskProtion implements Runnable{

    private static int count = 0;
    private final int id = count++;
    private static Random random = new Random(47);
    private final CountDownLatch countDownLatch;

    public TaskProtion(CountDownLatch countDownLatch)
    {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            doWork();
            countDownLatch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doWork() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000) + 2000);
        System.out.println(this + "Completed!");
    }

    public String toString(){
        return String.format("%1$-3d",id);
    }
}

class WaitingTask implements Runnable{
    private static int count = 0;
    private final int id = count++;
    private final CountDownLatch countDownLatch;

    public WaitingTask(CountDownLatch countDownLatch)
    {
        this.countDownLatch = countDownLatch;
    }
    @Override
    public void run() {
        try {
            countDownLatch.await();
            System.out.println("Latch barrier passed for " + this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return String.format("%1$-3d",id);
    }
}
