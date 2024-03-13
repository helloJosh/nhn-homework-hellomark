package com.nhnacademy;

import java.util.concurrent.Semaphore;

public class Item{
    public static final String[] items = {
        "GPU", "CPU", "NIC", "MONITOR", "AP"
    };
    private Semaphore semaphore;
    private String name;
    private int currentQuantity;
    private int maxQuantity;
    public Item(String name, int maxQuantity){
        semaphore = new Semaphore(1);
        this.name = name;
        this.maxQuantity = maxQuantity;
        this.currentQuantity = 0;
    }
    public String getName() {
        return name;
    }
    public int getMaxQuantity() {
        return maxQuantity;
    }
    public int getCurrentQuantity() {
        return currentQuantity;
    }
    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
    public boolean tryLogin() {
        return semaphore.tryAcquire();
    }
    public void semaphoreAcquire(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public boolean hasAvailableSlot(){
        return semaphore.availablePermits() == 1;
    }
    public void semaphoreLogout() {
        semaphore.release();
    }

    
}