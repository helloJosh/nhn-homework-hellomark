package com.nhnacademy;

public class Main {
    public static void main(String[] args) {
        Thread storeThread = new Thread(new Store());
        storeThread.start();
    }
}