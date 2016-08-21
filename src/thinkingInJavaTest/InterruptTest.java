package thinkingInJavaTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/19.
 */
public class InterruptTest {
    public static void main(String[] args) throws Exception
    {
       /* ExecutorService executorService = Executors.newCachedThreadPool();
       *//* ServerSocket serverSocket = new ServerSocket(8081);
        InputStream inputStream = new Socket("localhost",8080).getInputStream();*//*
        InputStream inputStream = new FileInputStream(new File("D:\\Temp\\temp3\\manifest.xml"));
        executorService.execute(new IOBlocked(inputStream));
        //executorService.execute(new IOBlocked(System.in));
        System.out.println("Waiting for 3 seconds");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //executorService.shutdownNow();
        System.out.println("stop them");
        executorService.shutdownNow();
        TimeUnit.SECONDS.sleep(1);
        inputStream.close();
        System.in.close();*/
        Thread thread = new Thread(new TaskA001());
        thread.start();
        TimeUnit.MILLISECONDS.sleep(500);
        thread.interrupt();
        //serverSocket.close();
        /*Thread thread = new Thread(new InterruptedMethodTest());
        thread.start();*/

    }
}

class InterruptedMethodTest implements Runnable{

    @Override
    public void run() {
        while(true)
        {
            System.out.println("running!!");
            //Thread.currentThread().interrupt();
            Thread.interrupted();
        }
    }
}

class IOBlocked implements Runnable{
    private InputStream inputStream;
    private int id = 0;
    public IOBlocked(InputStream in) {inputStream = in;id++;}
    @Override
    public void run() {
        try{
            System.out.println("waiting for interrupt!");
            inputStream.read();
        } catch (IOException e) {
            if(Thread.currentThread().isInterrupted())
                System.out.println("Interrupted from blocked IO");
            else
                throw new RuntimeException(e);
        }
        System.out.println("Exiting from blocked IO " + id);
    }
}

class TaskA001 implements Runnable{
    @Override
    public void run() {
        System.out.println("shenmeqingkuang ??");
        while(!Thread.interrupted())
        {
            System.out.println("in run method!!!");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Catch InterruptedException");
                //e.printStackTrace();
                if(!Thread.interrupted())
                {
                    System.out.println("is interrupted??? false!");
                }
                else
                    System.out.println("is interrupted??? true!!");
            }
        }
    }
}
