package thinkingInJavaTest;

import sun.awt.image.ImageWatched;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangqchf on 2016/8/21.
 */
public class BlckingQueueTest {
    public static void main(String[] args)
    {
        LinkedBlockingDeque<Toast> toastLinkedBlockingDeque = new LinkedBlockingDeque<>();
        Thread thread = new Thread(new Task0004(toastLinkedBlockingDeque));
        thread.start();
        Toast toast = new Toast("fitst");
        System.out.println("add Toast!");
        System.out.println("sleep for a while!!");
        toastLinkedBlockingDeque.add(toast);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        toast.setInfo("second");
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }
}

class Toast{
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    String info = null;
    public Toast(String input) {
        info = input;
    }
}

class Task0004 implements Runnable{

    private LinkedBlockingDeque<Toast> linkedBlockingDeque;

    public Task0004(LinkedBlockingDeque<Toast> toastLinkedBlockingDeque)
    {
        linkedBlockingDeque = toastLinkedBlockingDeque;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                Toast toast = linkedBlockingDeque.take();
                TimeUnit.SECONDS.sleep(3);
                System.out.println(toast.getInfo());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
