package com.nhnacademy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Store implements Runnable{
    public static final int MAX_CONSUMER_INSTORE = 5;
    public static final int MAX_PRODUCER_INSTORE = 5;
    public static final int ITEM_TYPE_COUNT = 5;
    private List<Item> itemList = new ArrayList<>();
    private ExecutorService consumerExecutor;
    private ExecutorService producerExecutor;
    private Instant constructorStoreTime;
    private Instant runningStoreTime;
    private Instant enterProducerTime;
    private Instant runningProducerTime;
    private Instant enterConsumerTime;
    private Instant runningConsumerTime;
    private int currentConsumerCount;
    private int currentProducerCount;

    public Store(){
        setConstructorStoreTime(Instant.now());
        setConsumerExecutor(Executors.newFixedThreadPool(MAX_CONSUMER_INSTORE));
        setProducerExecutor(Executors.newFixedThreadPool(MAX_PRODUCER_INSTORE));

        getItemList().add(new Item("GPU", 5));
        getItemList().add(new Item("CPU", 4));
        getItemList().add(new Item("NIC", 3));
        getItemList().add(new Item("MONITOR", 10));
        getItemList().add(new Item("AP", 9));

        for(int i=0; i< Consumer.TOTAL_CONSUMER ; i++){
            getConsumerExecutor().submit(new Consumer(this,"consumer"+(i+1)));
        }
        for(int i=0; i< Producer.TOTAL_PRODUCER; i++){
            getProducerExecutor().submit(new Producer(this,"producer"+(i+1)));
        }
    }

    public synchronized void buy(){
        setEnterProducerTime(Instant.now());
        setRunningProducerTime(Instant.now());
        int toBuyItemTypeNumber = ThreadLocalRandom.current().nextInt(1,ITEM_TYPE_COUNT);
        int[] toBuyItemNumber = new int[toBuyItemTypeNumber];
        for(int i=0;i<toBuyItemTypeNumber;i++){
            toBuyItemNumber[i] = ThreadLocalRandom.current().nextInt(1,ITEM_TYPE_COUNT);
            for(int j=0; j<i;j++){
                if(toBuyItemNumber[i]==toBuyItemNumber[j]){
                    i--;
                }
            }
        }

        while(Duration.between(getEnterProducerTime(), getRunningProducerTime()).toSeconds()>60l && isItemFull(toBuyItemTypeNumber, toBuyItemNumber)){
            try{
                wait();
                setRunningProducerTime(Instant.now());
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        buyAllItems(toBuyItemTypeNumber, toBuyItemNumber);
        notifyAll();
    }
    public synchronized void buyAllItems(int toBuyItemTypeNumber, int[] toBuyItemNumber){
        System.out.println("===== 생산자 트랜잭션 시작 =====");
        for(int i=0;i<toBuyItemTypeNumber;i++){
            Item tempItem = getItemList().get(toBuyItemNumber[i]);
            if(tempItem.hasAvailableSlot() 
                    && tempItem.getCurrentQuantity() < tempItem.getMaxQuantity()){
                tempItem.semaphoreAcquire();
                tempItem.setCurrentQuantity(tempItem.getCurrentQuantity()+1);   
                System.out.println("가게에서 "+tempItem.getName()+"을 1개 납품 받았습니다, 총 재고 : "+tempItem.getCurrentQuantity());
                tempItem.semaphoreLogout();
            }
        }
        System.out.println("===== 트랜잭션 끝 =====");
    }
    public boolean isItemFull(int toBuyItemTypeNumber, int[] toBuyItemNumber){
        for(int i=0;i<toBuyItemTypeNumber;i++){
            Item tempItem = getItemList().get(toBuyItemNumber[i]);
            if(tempItem.getCurrentQuantity() == tempItem.getMaxQuantity()){
                return true;
            }
        }
        return false;
    }

    public synchronized void sell(){
        setEnterConsumerTime(Instant.now());
        setRunningConsumerTime(Instant.now());
        int toSellItemTypeNumber = ThreadLocalRandom.current().nextInt(1,ITEM_TYPE_COUNT);
        int[] toSellItemNumber = new int[toSellItemTypeNumber];
        for(int i=0;i<toSellItemTypeNumber;i++){
            toSellItemNumber[i] = ThreadLocalRandom.current().nextInt(1,ITEM_TYPE_COUNT);
            for(int j=0; j<i;j++){
                if(toSellItemNumber[i]==toSellItemNumber[j]){
                    i--;
                }
            }
        }
        while(Duration.between(getEnterConsumerTime(), getRunningConsumerTime()).toSeconds()>60l && isItemEmpty(toSellItemTypeNumber, toSellItemNumber)){
            try{
                wait();
                setRunningProducerTime(Instant.now());
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        sellAllItems(toSellItemTypeNumber, toSellItemNumber);
        notifyAll();
    }
    public void sellAllItems(int toSellItemTypeNumber, int[] toSellItemNumber){
        System.out.println("===== 소비자 트랜잭션 시작 =====");
        for(int i=0;i<toSellItemTypeNumber;i++){
            Item tempItem = getItemList().get(toSellItemNumber[i]);
            if(tempItem.hasAvailableSlot() 
                    && tempItem.getCurrentQuantity()>0){
                tempItem.semaphoreAcquire();
                tempItem.setCurrentQuantity(tempItem.getCurrentQuantity()-1);   
                System.out.println("가게에서 "+tempItem.getName()+"을 1개 팔았습니다, 총 재고 : "+tempItem.getCurrentQuantity());
                tempItem.semaphoreLogout();
            }
        }
        System.out.println("===== 트랜잭션 끝 =====");
    }
    public boolean isItemEmpty(int toSellItemTypeNumber, int[] toSellItemNumber){
        for(int i=0;i<toSellItemTypeNumber;i++){
            Item tempItem = getItemList().get(toSellItemNumber[i]);
            if(tempItem.getCurrentQuantity() < 2){
                return true;
            }
        }
        return false;
    }

    @Override
    public void run(){
        setRunningStoreTime(Instant.now());
        while(Duration.between(getConstructorStoreTime(), getRunningStoreTime()).toMinutes()<5l){
            setRunningStoreTime(Instant.now());
        }
        consumerExecutor.shutdownNow();
        producerExecutor.shutdownNow();
    }
    


    public ExecutorService getConsumerExecutor() {
        return consumerExecutor;
    }

    public void setConsumerExecutor(ExecutorService consumerExecutor) {
        this.consumerExecutor = consumerExecutor;
    }

    public ExecutorService getProducerExecutor() {
        return producerExecutor;
    }

    public void setProducerExecutor(ExecutorService producerExecutor) {
        this.producerExecutor = producerExecutor;
    }

    public Instant getConstructorStoreTime() {
        return constructorStoreTime;
    }

    public void setConstructorStoreTime(Instant constructorTime) {
        this.constructorStoreTime = constructorTime;
    }

    public Instant getRunningStoreTime() {
        return runningStoreTime;
    }

    public void setRunningStoreTime(Instant runningTime) {
        this.runningStoreTime = runningTime;
    }

    public int getCurrentConsumerCount() {
        return currentConsumerCount;
    }

    public void setCurrentConsumerCount(int currentConsumerCount) {
        this.currentConsumerCount = currentConsumerCount;
    }

    public int getCurrentProducerCount() {
        return currentProducerCount;
    }
    public void setCurrentProducerCount(int currentProducerCount) {
        this.currentProducerCount = currentProducerCount;
    }
    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public Instant getEnterProducerTime() {
        return enterProducerTime;
    }

    public void setEnterProducerTime(Instant enterProducerTime) {
        this.enterProducerTime = enterProducerTime;
    }

    public Instant getRunningProducerTime() {
        return runningProducerTime;
    }

    public void setRunningProducerTime(Instant exitProducerTime) {
        this.runningProducerTime = exitProducerTime;
    }

    public Instant getEnterConsumerTime() {
        return enterConsumerTime;
    }

    public void setEnterConsumerTime(Instant enterConsumerTime) {
        this.enterConsumerTime = enterConsumerTime;
    }

    public Instant getRunningConsumerTime() {
        return runningConsumerTime;
    }

    public void setRunningConsumerTime(Instant exitConsumerTime) {
        this.runningConsumerTime = exitConsumerTime;
    }
    
    
    
}
