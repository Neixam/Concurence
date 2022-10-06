package fr.uge.concurrence.td02;

public class Counter {
  private int value;

  public void addALot() {
    for (var i = 0; i < 100_000; i++) {
      this.value++;
    }
  }
  /**
   *  La valeur finale de value est aléatoire, car this.value++ n'est pas une opération atomique elle peut donc être
   *  dé scheduler durant une lecture, et donc réécrire plus tard une valeur inférieure à la valeur réelle de value.
   *  Le code peut effectivement afficher moins que 10 000 car dans le cas où le scheduler coupe l'incrémentation lors
   *  de la lecture d'une valeur en dessous de 9 998, et que le scheduler laisse le temps à l'autre thread de finir
   *  presque tous ses calculs en arrêtant la boucle à i = 99 998, et en ré schedulant le premier thread durant son
   *  écriture et donc la valeur en mémoire devient la valeur < 9 998 + 1, et si on ré schedule le second thread à ce
   *  moment-là on lit la valeur < 9 999 et on ré schedule le premier et on finit ses tours de boucles et lors de la
   *  reprise du second thread on écrit < 9 999 + 1, ce qui nous donne donc une valeur < 10 000
   **/
  public static void main(String[] args) throws InterruptedException {
    var counter = new Counter();
    var thread1 = Thread.ofPlatform().start(counter::addALot);
    var thread2 = Thread.ofPlatform().start(counter::addALot);
    thread1.join();
    thread2.join();
    System.out.println(counter.value);
  }
}