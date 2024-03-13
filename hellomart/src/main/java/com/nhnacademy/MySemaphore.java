package com.nhnacademy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MySemaphore {
    private final Lock lock = new ReentrantLock();
    private final Condition flagFree = lock.newCondition();
    private int flag;

    public MySemaphore(int permits){
        this.flag = permits;
    }
    
    public void acquire(){
        lock.lock();
        try{
            while(flag == 0){
                flagFree.await();
            }
            flag--;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally{
            lock.unlock();
        }
    }
    public void release(){
        lock.lock();
        try{
            flag++;
            flagFree.signal();
        } finally{
            lock.unlock();
        }
    }
    public int availablePermits(){
        return this.flag;
    }
    public boolean tryAcquire() {
        lock.lock();
        try {
            if (flag > 0) {
                flag--;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }
}
