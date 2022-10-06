package fr.uge.concurrence.td02;

public class StopThreadBug implements Runnable {
  private boolean stop = false;

  public void stop() {
    stop = true;
  }

  @Override
  public void run() {
    while (!stop) {
      System.out.println("Up");
    }
    System.out.print("Done");
  }

  /**
   * Le comportement espéré est de pouvoir arrêter le thread à l'aide de la méthode stop().
   * La data-race est la variable this.stop.
   * On observe qu'il arrive que des "Up" s'affiche alors qu'on appelle stop.
   * Le programme ne s'arrête plus et continue de tourner, on peut expliquer cela par le fait qu'il n'y a aucun
   * changement d'état entre les tours de boucle du coup le JIT optimise la boucle en la laissant tourné à l'infinie.
   * Je pense que oui car println a effet de bord et n'est donc pas supprimé par le JIT mais le code étant faux
   * si le JIT venait à optimiser les println on aurait donc plus cette garantie.
   */

  public static void main(String[] args) throws InterruptedException {
    var stopThreadBug = new StopThreadBug();
    Thread.ofPlatform().start(stopThreadBug::run);
    Thread.sleep(5_000);
    System.out.println("Trying to tell the thread to stop");
    stopThreadBug.stop();
  }
}
