package com.tmtbe.pvisual;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            integers.add(i);
        }
        CountDownLatch countDownLatch = new CountDownLatch(2);
        TransmittableThreadLocal<String> transmittableThreadLocal = new TransmittableThreadLocal<>();
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
