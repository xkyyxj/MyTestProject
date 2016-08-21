package thinkingInJavaTest;

import nc.bs.framework.execute.Executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/20.
 */
public class NotifyTest {

    public static void main(String[] args)
    {
        Task0002 task0002 = new Task0002();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(task0002);
        executorService.execute(new Task0003(task0002));
        executorService.shutdown();
    }
}

class Task0002 implements Runnable{

    private boolean isRun = false;

    @Override
    public void run() {
        try {
            synchronized (this) {
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Print some message!!");
    }
}

class Task0003 implements Runnable{
    private Task0002 task0002;
    public Task0003(Task0002 task0002){
        this.task0002 = task0002;
    }
    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (task0002) {
            task0002.notify();
        }
    }
}
