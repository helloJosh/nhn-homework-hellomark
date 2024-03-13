package com.nhnacademy;

public class Item{
    public static final String[] items = {
        "GPU", "CPU", "NIC", "MONITOR", "AP"
    };
    private MySemaphore semaphore;
    private String name;
    private int currentQuantity;
    private int maxQuantity;
    public Item(String name, int maxQuantity){
        semaphore = new MySemaphore(1);
        this.name = name;
        this.maxQuantity = maxQuantity;
        this.currentQuantity = 4;
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
        this.semaphore.acquire();
    }
    public boolean hasAvailableSlot(){
        return semaphore.availablePermits() == 1;
    }
    public void semaphoreLogout() {
        this.semaphore.release();
    }

    
}