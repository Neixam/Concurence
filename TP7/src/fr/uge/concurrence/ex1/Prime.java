package fr.uge.concurrence.ex1;

import java.util.OptionalLong;
import java.util.concurrent.ThreadLocalRandom;

/**
 * La différence entre les méthodes interrupted et isInterrupted est que la première reset le flag d'interruption tandis
 * que la seconde ne fais que donner sa valeur.
 * On peut throw une InterruptedException.
 */
public class Prime {
  public static boolean isPrime(long candidate) {
    if (candidate <= 1) {
      return false;
    }
    for (var i = 2; i <= Math.sqrt(candidate); i++) {
      if (candidate % i == 0 || Thread.currentThread().isInterrupted()) {
        return false;
      }
    }
    return true;
  }

  public static OptionalLong findPrime() {
    var generator = ThreadLocalRandom.current();
    for (; !Thread.interrupted();) {
      var candidate = generator.nextLong();
      if (isPrime(candidate)) {
        return OptionalLong.of(candidate);
      }
    }
    return OptionalLong.empty();
  }

  public static void main(String[] args) throws InterruptedException {
    var t = Thread.ofPlatform().start(() -> {
      System.out.println("Found a random prime : " + findPrime().orElseThrow());
    });
    Thread.sleep(3000);
    t.interrupt();
  }
}
