package thinkingInJavaTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/20.
 */
public class InterruptTest2 {
    public static void main(String[] args)
    {

    }

}

class Task001 implements Runnable{
    @Override
    public void run() {
        while(!Thread.interrupted())
        {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}