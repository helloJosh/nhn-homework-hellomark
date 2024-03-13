package com.nhnacademy;

import java.util.concurrent.ThreadLocalRandom;

public class Producer implements Runnable{
    public static final int TOTAL_PRODUCER = 10;
    private final Store store;
    private final String name;

    public Producer(Store store, String name){
        this.store = store;
        this.name = name;
    }

    @Override
    public void run(){
        while(true){
            try{
                store.buy();
                System.out.println(this.name);
                System.out.println();
                Thread.sleep(ThreadLocalRandom.current().nextInt(10000,20000));
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
