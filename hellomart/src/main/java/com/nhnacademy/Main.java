package com.nhnacademy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    static Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        Thread storeThread = new Thread(new Store());
        storeThread.start();
    }
}