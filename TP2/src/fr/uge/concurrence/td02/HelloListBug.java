package fr.uge.concurrence.td02;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class HelloListBug {

  /**
   * On obtient que des listes de tailles aléatoires entre 2 et 20 000.
   * On peut expliquer cette différence car la taille du tableau interne de liste fait un size = size + 1 sauf que
   * cette opération n'étant pas atomique elle peut se faire dé scheduler pendant une lecture ou une écriture d'un
   * thread ce qui peut faire écrire un thread une valeur de size qui soit inférieure à la valeur réelle dans la mémoire
   * ce qui fait qu'on écrive donc dans une case précédente du tableau interne de la liste.
   * Si on venait à supprimer la taille initiale on a donc un tableau qui n'a pas la taille maximale possible, si on a
   * un thread qui écrit ses valeurs et qui se fait dé scheduler à la ligne 237 d'ArrayList au moment il est en train
   * d'augmenter la taille du tableau interne avec une size de 5 et qu'un autre thread fasse l'opération à sa place et
   * continue l'opération et fait un tableau plus grand que 5 et ajoute des valeurs puis se bloque à la ligne 455
   * d'ArrayList lors de l'affectation de la nouvelle valeur à la case size = 25 et on re schedule le thread qui s'est
   * arrêté lors de l'allocation d'un tableau de taille 5 et va recréer un tableau de taille 5 et en re schedulant sur
   * l'autre thread qui s'est arrêté à la ligne 455 avec la valeur 23 il y a donc une exception qui est lancée.
   */

  public static void main(String[] args) throws InterruptedException {
    var nbThread = 4;
    var list = new ArrayList<Integer>();

    var threads = IntStream.range(0, nbThread)
            .mapToObj(i -> Thread.ofPlatform().start(() ->
                    IntStream.range(0, 5000).forEach(list::add)))
            .toList();
    for (var t : threads) {
      t.join();
    }
    System.out.println("le programme est fini, la taille de la liste est de " + list.size());
  }
}
