package com.nhnacademy;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;

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
    void testAcquire2() throws InterruptedException {
        MySemaphore semaphore = new MySemaphore(1);
        CountDownLatch latch = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                semaphore.acquire();
                latch.countDown(); // 스레드가 세마포어를 획득한 후 countDownLatch를 감소시킴
                Thread.sleep(1000); // 일정 시간 동안 블록되도록 함
                semaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        latch.await();
        assertEquals(0, semaphore.availablePermits());
    }

    @Test
    void testAcquireAskingForMore() throws InterruptedException {
        MySemaphore semaphore = new MySemaphore(1);
        CountDownLatch latch = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                semaphore.acquire();
                Thread.sleep(1000); 
                semaphore.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                latch.await(); // t1이 세마포어를 획득할 때까지 기다림
                assertThrows(IllegalAccessError.class, semaphore::acquire);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();

        latch.countDown(); // t1이 세마포어 획득하도록 함

        t1.join();
        t2.join();
    }

    @Test
    void testRelease() throws InterruptedException {
        MySemaphore semaphore = new MySemaphore(1);
        CountDownLatch latch = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            semaphore.acquire();
            semaphore.release();
            latch.countDown(); // 세마포어 해제 후 countDownLatch를 감소시킴
        });

        Thread t2 = new Thread(() -> {
            try {
                latch.await(); // t1이 세마포어를 해제할 때까지 기다림
                assertTrue(semaphore.tryAcquire()); // 세마포어를 다시 획득하는지 확인
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
