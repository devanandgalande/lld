package org.psuedovert.rate_limiter;

import org.psuedovert.rate_limiter.model.User;
import org.psuedovert.rate_limiter.model.UserTier;
import org.psuedovert.rate_limiter.service.RateLimiterService;

import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        User freeuser = new User("free", UserTier.FREE);  //2 request per 10 seconds
        User premium = new User("premium", UserTier.PREMIUM); //10 req per 10 sec

        RateLimiterService service = new RateLimiterService();

        System.out.println("=== FREE USER REQUEST ===");
        for (int i=1; i<=15; i++) {
            Instant now = Instant.now();
            boolean allowed = service.allowRequest(freeuser);
            System.out.printf("Request %d for %s User: %s | Time: %s%n",
                    i, freeuser.getTier(), allowed, now);
            Thread.sleep(1000);
        }
        System.out.println();
        System.out.println("=== PREMIUM USER REQUEST ===");
        for (int i=1; i<=30; i++) {
            Instant now = Instant.now();
            boolean allowed = service.allowRequest(premium);
            System.out.printf("Request %d for %s User: %s | Time: %s%n",
                    i, premium.getTier(), allowed, now);
            Thread.sleep(500);
        }

        checkConcurrency(service);
    }

    private static void checkConcurrency(RateLimiterService rateLimiterService) throws InterruptedException {
        User user = new User("user1", UserTier.FREE);
        int threads = 20;
        ExecutorService executors = Executors.newFixedThreadPool(threads);
        CyclicBarrier barrier = new CyclicBarrier(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        for (int i=1; i<=threads; i++) {
            final int reqNum = i;
            executors.submit(() -> {
                try {
                    //all threads wait here until barrier is full
                    barrier.await();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
                boolean allowed = rateLimiterService.allowRequest(user);
                System.out.printf("%s | Request %d for %s User: %s%n",
                        Thread.currentThread().getName(),
                        reqNum,
                        user.getTier(),
                        allowed ? "ALLOWED" : "BLOCKED"
                        );
                latch.countDown();
            });
        }
        latch.await(); //wait for all threads to complete
        executors.shutdown();
    }
}