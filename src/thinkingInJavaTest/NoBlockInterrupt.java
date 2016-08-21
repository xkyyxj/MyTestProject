package thinkingInJavaTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/20.
 */
//非阻塞线程不会响应interrupt函数。
public class NoBlockInterrupt {
    public static void main(String[] args){
        //Thread thread = new Thread(new RunTask1());
        //thread.start();
        /*try {
            TimeUnit.MILLISECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //thread.interrupt();
        Thread thread2 = new Thread(new RunTask2());
        thread2.start();
        try {
            TimeUnit.MILLISECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread2.interrupt();
    }
}

class RunTask1 implements Runnable{
    private int i = 10;
    @Override
    public void run() {
        try {
            while (true) {
                ++i;
                if (i > 100 && (++i % 100) == 0)
                    System.out.println("times " + i);
            }
        } catch(Exception e){
            System.out.println("exception!!");
            System.exit(0);
        }
    }
}

class RunTask2 implements Runnable{
    @Override
    public void run() {
        while(!Thread.interrupted())
        {
            System.out.println("Still running!!");
        }
    }
}
