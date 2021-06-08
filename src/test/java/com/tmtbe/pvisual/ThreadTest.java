package com.tmtbe.pvisual;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        test2();
    }

    public static void test1() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();
        transmittableThreadLocal.set("value-set-in-parent");
        Runnable task = () -> System.out.println("[child thread] get " + transmittableThreadLocal.get() + " in Runnable");
        executorService.submit(task);
        transmittableThreadLocal.set("value-set-in-parent2");
        executorService.submit(task);
    }

    public static void test2() throws InterruptedException {
        TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();
        transmittableThreadLocal.set("C");
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            integers.add(i);
        }
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            transmittableThreadLocal.set("A");
            integers.stream().parallel().forEach(i -> {
                System.out.println("1:" + transmittableThreadLocal.get());
            });
            countDownLatch.countDown();
        }).start();
        new Thread(() -> {
            transmittableThreadLocal.set("B");
            integers.stream().parallel().forEach(i -> {
                System.out.println("2:" + transmittableThreadLocal.get());
            });
            countDownLatch.countDown();
        }).start();
        countDownLatch.await();
    }
}
