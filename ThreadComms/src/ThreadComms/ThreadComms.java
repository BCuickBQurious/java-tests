package ThreadComms;

import sun.misc.Signal;
import sun.misc.SignalHandler;

interface IThreadCallback {
    public void callback();
}
/**
 * Attempts at passing a main thread callback (that executes
 * only on the main thread, like an interrupt) into a child
 * thread. (It didn't work)
 * @author AswinB
 */
public class ThreadComms {

    /**
     * Keep pinging the console to show a thread is alive
     * @param source
     * @param times 
     */
    private static void threadPing(String source, int times) {
        for(int i=times;i>=0;i--) {
            System.out.print(source);
            System.out.println(i);
            noErrSleep(100);
        }
    }
    
    private static class ThreadWithCallback extends Thread {
        public IThreadCallback callbackObj;
        
        public ThreadWithCallback(IThreadCallback cb) {
            this.callbackObj = cb;
        }
        
        @Override
        public void run() {
            threadPing("thread",5);
            callbackObj.callback();
        }
    }
    
    public static void noErrSleep(long time) {
        try {
            Thread.sleep(time*5);
            //was lazy to change time in each call
        } catch (InterruptedException ex) {
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /**
         * Method 1: passing a callback interface
         * (didn't work)
         */
        ThreadWithCallback th = new ThreadWithCallback(new IThreadCallback() {
            @Override
            public void callback() {
                System.out.println("sleep");
                noErrSleep(1000);
                System.out.println("sleepEnd");
            }
        });
        th.start();
        pingMain();
        System.out.println("End of method 1. Starting method 2");
        
        /**
         * Method 2: signals
         * Also didn't work, signal handlers run on a new thread
         */
        Signal.handle(new Signal("INT"), new SignalHandler() {
            @Override
            public void handle(Signal signal) {
                System.out.println("sleep");
                noErrSleep(1000);
                System.out.println("sleepEnd");
            }
        });
        (new Thread(){
            @Override
            public void run() {
                threadPing("thread", 5);
                Signal.raise(new Signal("INT"));
                threadPing("thread", 5);
            }
        }).start();
        pingMain();
    }

    private static void pingMain() {
        noErrSleep(50); //so that outputs are staggered
        threadPing("main",10);
    }
}
