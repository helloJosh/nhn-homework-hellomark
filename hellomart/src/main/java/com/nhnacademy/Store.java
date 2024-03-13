package com.nhnacademy;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private Logger logger = LogManager.getLogger();

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

    public void buy(){

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
        for(int i=0; i<toBuyItemTypeNumber;i++){
            Item tempItem = getItemList().get(toBuyItemNumber[i]);
            tempItem.semaphoreAcquire();
            enterProducerTime = Instant.now();
            while(tempItem.getCurrentQuantity() + 1 > tempItem.getMaxQuantity()){
                tempItem.semaphoreLogout();
                try {
                    Thread.sleep(100);
                    runningProducerTime = Instant.now();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if(Duration.between(getConstructorStoreTime(), getRunningStoreTime()).toSeconds() < 10l){
                    logger.warn("생산자 물건 납품 포기");
                    return;
                }
                tempItem.semaphoreAcquire();
            }

            tempItem.setCurrentQuantity(tempItem.getCurrentQuantity()+1);
            logger.info("가게에서 {} 에게 {} 을 1개 납품 받았습니다, 총 재고 : {}",Thread.currentThread().getName(), tempItem.getName(),tempItem.getCurrentQuantity());
            tempItem.semaphoreLogout();
        }
    }

    public void sell(){
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
        for(int i=0; i<toSellItemTypeNumber;i++){
            Item tempItem = getItemList().get(toSellItemNumber[i]);
            tempItem.semaphoreAcquire();
            enterConsumerTime = Instant.now();
            while(tempItem.getCurrentQuantity() < 1){
                tempItem.semaphoreLogout();
                try {
                    Thread.sleep(100);
                    runningConsumerTime = Instant.now();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if(Duration.between(getConstructorStoreTime(), getRunningStoreTime()).toSeconds() < 10l){
                    logger.warn("소비자 물건 구매 포기");
                    return;
                }
                tempItem.semaphoreAcquire();
            }

            tempItem.setCurrentQuantity(tempItem.getCurrentQuantity()-1);
            logger.info("가게에서 {} 에게 {} 을 1개 팔았습니다, 총 재고 : {}",Thread.currentThread().getName(), tempItem.getName(),tempItem.getCurrentQuantity());
            tempItem.semaphoreLogout();
        }
    }

    @Override
    public void run(){
        setRunningStoreTime(Instant.now());
        while(Duration.between(getConstructorStoreTime(), getRunningStoreTime()).toMinutes()<5l){
            setRunningStoreTime(Instant.now());
        }
        consumerExecutor.shutdownNow();
        producerExecutor.shutdownNow();
        logger.warn("가게 문 닫습니다.");
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
