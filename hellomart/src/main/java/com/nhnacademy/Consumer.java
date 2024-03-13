package com.nhnacademy;

import java.util.concurrent.ThreadLocalRandom;

public class Consumer implements Runnable{
    public static final int TOTAL_CONSUMER = 10;
    private final Store store;
    private final String name;
    public Consumer(Store store, String name){
        this.store = store;
        this.name = name;
    }
    

    @Override
    public void run(){
        while(true){
            try{
                store.sell();
                System.out.println(this.name);
                System.out.println();
                Thread.sleep(ThreadLocalRandom.current().nextInt(10000,30000));
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
