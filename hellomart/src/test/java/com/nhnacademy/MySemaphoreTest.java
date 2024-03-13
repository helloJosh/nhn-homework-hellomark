package com.nhnacademy;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import org.junit.jupiter.api.Test;

class MySemaphoreTest {
    @Test
    void testAcquire(){
        assertDoesNotThrow(()->{
            MySemaphore semaphore = new MySemaphore(1);
            Runnable task = () ->{
                try {
                    semaphore.acquire();
                    Thread.sleep(10000000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Thread t1 = new Thread(task, "Thread1");
            t1.start();
        });
    }
    @Test
    void testAcquireAskingForMore() {
        assertThrowsExactly(IllegalAccessError.class,()->{
            MySemaphore semaphore = new MySemaphore(1);
            Runnable task1 = () ->{
                try {
                    semaphore.acquire();
                    Thread.sleep(10000000);
                    semaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            Runnable task2 = () ->{
                if(!semaphore.tryAcquire())
                    throw new IllegalAccessError();
            };
            Thread t1 = new Thread(task1, "Thread1");
            Thread t2 = new Thread(task2, "Thread2");
            t1.start();
            t2.start();
        });

    }
    @Test
    void testRelease() {
        assertDoesNotThrow(()->{
            MySemaphore semaphore = new MySemaphore(1);
            Runnable task1 = () ->{
                semaphore.acquire();
                semaphore.release();
            };
            Runnable task2 = () ->{
                assertEquals(true, semaphore.tryAcquire());
            };
            Thread t1 = new Thread(task1, "Thread1");
            Thread t2 = new Thread(task2, "Thread2");
            t1.start();
            t2.start();
        });
    }
}
