package fr.uge.concurrence.td02;

public class ExampleLongAffectation {
  long l = -1L;

  /**
   * Les différents affichages constatés peuvent être :
   * - "-4 294 967 296" si on schedule le thread lorsque l'affectation du long ne soit fini et que le pipeline fasse la
   * partie de poids fort d'abord
   * - "4 294 967 295" si on schedule le thread lorsque l'affectation du long ne soit fini et que le pipeline fasse la
   * partie de poids faible d'abord
   * - "-1" avant l'affectation
   * - "0" après l'affectation.
   */
  public static void main(String[] args) {
    var e = new ExampleLongAffectation();
    Thread.ofPlatform().start(() -> System.out.println("l = " + e.l));
    e.l = 0;
  }
}
