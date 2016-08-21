package thinkingInJavaTest;

import nc.bs.framework.execute.Executor;

import java.util.concurrent.*;

/**
 * Created by wangqchf on 2016/8/21.
 */
/**8await调用会削减计数，然后当计数削减到0时i，会调用创建CyclicBarrier时传入的runnable接口。
 * 而后就是使所有的调用过await函数的线程从await函数当中唤醒过来然后继续其操作（如果是循环的
 * 话，那么就会继续进行下一个循环,CyclicBarrier会重新计数）。
 *
 */
public class CyclicBarrierTest {
    private static final int count = 10;
    public static void main(String[] args)
    {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count, new Runnable() {
            @Override
            public void run() {
                System.out.println("finished waiting!!!! cyclicBarrier!");
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i = 0;i < count;i++)
        {
            executorService.execute(new WhatTask(cyclicBarrier));
        }
    }
}

class WhatTask implements Runnable {

    private static int count = 0;

    private final int id = count++;

    private final CyclicBarrier cyclicBarrier;

    public WhatTask(CyclicBarrier cyclicBarrier)
    {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("start waiting!" + this);
                cyclicBarrier.await();
                System.out.println("finished waiting!" + this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return " " + id;
    }
}