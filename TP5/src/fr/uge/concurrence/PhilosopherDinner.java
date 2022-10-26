package fr.uge.concurrence;

import java.util.Arrays;
import java.util.stream.IntStream;

/*
  Le problème dans ce code est qu'il y a un interblocage car les forks ne se font pas dans le même ordre.
  Dans le cas où un thread se dé-schedule alors qu'il a pris son premier verrou et que le thread de l'indice d'après
  prend son premier verrou et se dé-schedule aussi et ainsi de suite jusqu'à ce que tous les threads soient bloqué sur le
  deuxième synchronized.
  Oui, il est possible que 2 philosophes mangent en même temps car il y a suffisamment de fork pour ça,
  si le philosophe 0 prend les forks 0 et 1 le philosophe 2 peut prendre les forks 2 et 3 ou le philosophe 3 peut prendre
  les forks 3 et 4. Oui, cela est normal.
 */
public class PhilosopherDinner {
  private final Object[] forks;

  private PhilosopherDinner(Object[] forks) {
    this.forks = forks;
  }

  public static PhilosopherDinner newPhilosopherDinner(int forkCount) {
    Object[] forks = new Object[forkCount];
    Arrays.setAll(forks, i -> new Object());
    return new PhilosopherDinner(forks);
  }

  public void eat(int index) {
    Object fork1;
    Object fork2;
    if (index % 2 == 0) {
      fork1 = forks[index];
      fork2 = forks[(index + 1) % forks.length];
    } else {
      fork2 = forks[index];
      fork1 = forks[(index + 1) % forks.length];
    }
    synchronized (fork1) {
      synchronized (fork2) {
        System.out.println("philosopher " + index + " eat");
      }
    }
  }

  public static void main(String[] args) {
    var dinner = newPhilosopherDinner(5);
    IntStream.range(0, 5).forEach(i -> {
      new Thread(() -> {
        for (;;) {
          dinner.eat(i);
        }
      }).start();
    });
  }
}