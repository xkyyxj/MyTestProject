package thinkingInJavaTest;

/**
 * Created by wangqchf on 2016/8/20.
 */
public class WaitAndNotifyTest {
}

class TaskA implements Runnable{
    private Obj obj;

    public TaskA(Obj inObj)
    {
        obj = inObj;
    }
    @Override
    public void run() {

    }
}

class TaskB implements Runnable{
    private Obj obj;

    public TaskB(Obj inObj){
        obj = inObj;
    }
    @Override
    public void run() {

    }
}

class Obj{
    private boolean doOrNot = false;
    public synchronized void taskA(){
        doOrNot = true;
    }

    public synchronized void taskADone(){

    }

    public synchronized void taskB(){

    }

    public synchronized void taskBDone(){

    }
}