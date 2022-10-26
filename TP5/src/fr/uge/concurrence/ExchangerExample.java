package fr.uge.concurrence;

import fr.uge.concurrence.ex01.Exchanger;


/* L'affichage attendu est thread 1 null et main foo1 */
/* On peut par exemple mettre un compteur */
public class ExchangerExample {
  public static void main(String[] args) throws InterruptedException {
    var exchanger = new Exchanger<String>();
    Thread.ofPlatform().start(() -> {
      try {
        System.out.println("thread 1 " + exchanger.exchange("foo1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    System.out.println("main " + exchanger.exchange(null));
  }
}
