package fr.uge.concurrence.ex1;

/**
 * Il est impossible d'arrêter un thread de façon non coopérative car on ne sait pas si la mémoire est cohérente si on
 * stop le thread au milieu d'un calcule.
 * Une opération bloquante est une opération qui endort un thread le temps qu'on a un autre thread qui le réveil.
 * La méthode d'instance interrupt dans Thread permet de mettre un flag à true qui permet de dire à un thread qu'il est
 * interrompu.
 */
public class Main {
  public static void main(String[] args) {
    Thread.ofPlatform().start(() -> {
      for (var i = 1;; i++) {
        try {
          Thread.sleep(1_000);
          System.out.println("Thread slept " + i + " seconds.");
        } catch (InterruptedException e) {
          return; // interrompt le thread
        }
      }
    });
  }
}
