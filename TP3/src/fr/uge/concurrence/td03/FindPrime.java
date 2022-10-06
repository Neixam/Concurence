package fr.uge.concurrence.td03;

import java.util.Random;

/**
 * Il arrive que le nombre renvoyer ne soit pas premier, car le data-race entre la value dans rdv n'est pas protégée.
 */
public class FindPrime {
  public static boolean isPrime(long l) {
    if (l <= 1)
      return false;
    for (long i = 2L; i <= l / 2; i++) {
      if (l % i == 0) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var rdv = new RendezVous<Long>();
    for (var i = 0; i < nbThreads; i++) {
      var fi = i;
      Thread.ofPlatform().daemon().start(() -> {
        var random = new Random();
        for (;;) {
          var nb = 10_000_000_000L + (random.nextLong() % 10_000_000_000L);
          if (isPrime(nb)) {
            rdv.set(nb);
            System.out.println("A prime number was found in thread " + fi);
            return;
          }
        }
      });
    }
    Long prime = rdv.get();
    System.out.println("I found a large prime number : " + prime);
  }
}
