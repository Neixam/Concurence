package fr.uge.concurrence;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Si on venait à supprimer la taille initiale on a donc un tableau qui n'a pas la taille maximale possible, si on a
 * un thread qui écrit ses valeurs et qui se fait dé scheduler à la ligne 237 d'ArrayList au moment il est en train
 * d'augmenter la taille du tableau interne avec une size de 5 et qu'un autre thread fasse l'opération à sa place et
 * continue l'opération et fait un tableau plus grand que 5 et ajoute des valeurs puis se bloque à la ligne 455
 * d'ArrayList lors de l'affectation de la nouvelle valeur à la case size = 25 et on re schedule le thread qui s'est
 * arrêté lors de l'allocation d'un tableau de taille 5 et va recréer un tableau de taille 5 et en re schedulant sur
 * l'autre thread qui s'est arrêté à la ligne 455 avec la valeur 23 il y a donc une exception qui est lancée.
 */
public class HelloListBug {
  public static void main(String[] args) throws InterruptedException {
    var nbThreads = 4;
    var threads = new Thread[nbThreads];

    var list = new ArrayList<Integer>();

    IntStream.range(0, nbThreads).forEach(j -> {
      Runnable runnable = () -> {
        for (var i = 0; i < 5000; i++) {
          list.add(i);
        }
      };

      threads[j] = Thread.ofPlatform().start(runnable);
    });

    for (Thread thread : threads) {
      thread.join();
    }

    System.out.println("taille de la liste:" + list.size());
  }
}
