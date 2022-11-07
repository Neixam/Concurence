package fr.uge.concurrence.ex2;

import java.util.Random;
import java.util.stream.IntStream;

public class SumFirstTenPrimes {
  private static final int SIZE = 10;

  public static boolean isPrime(long l) {
    if (l <= 1) {
      return false;
    }
    for (long i = 2L; i <= l / 2; i++) {
      if (l % i == 0) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) throws InterruptedException {
    var numbers = new MyThreadSafeClass(SIZE);
    IntStream.range(0, 5).forEach(i -> Thread.ofPlatform().daemon().start(() -> {
      var random = new Random();
      for (;;) {
        long nb = 1_000_000_000L + (random.nextLong() % 1_000_000_000L);
        if (isPrime(nb)) {
        //  System.out.println("Thread " + i + " finding " + nb);
          numbers.add(nb);
        }
      }
    }));
    System.out.println("Sum ten first primes " + numbers.gets());
  }
}
