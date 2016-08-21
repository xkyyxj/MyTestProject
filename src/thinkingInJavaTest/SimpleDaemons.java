package thinkingInJavaTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/16.
 */
public class SimpleDaemons implements Runnable{

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
            System.out.println(Thread.currentThread() + " " + this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        for(int i = 0;i < 10;i++)
        {
            Thread daemon = new Thread(new SimpleDaemons());
            daemon.setDaemon(true);
            daemon.start();
        }
        System.out.println("All daemons started");
        try {
            TimeUnit.MILLISECONDS.sleep(175);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
