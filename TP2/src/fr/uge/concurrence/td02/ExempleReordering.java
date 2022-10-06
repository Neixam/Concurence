package fr.uge.concurrence.td02;

public class ExempleReordering {
  int a = 0;
  int b = 0;
/**
 * Les différents affichages constatés peuvent être :
 * - "a = 0 b = 0" si on schedule le thread avant les affectations de a et b
 * - "a = 1 b = 0" si on schedule le thread après l'affectation de a, mais avant b
 * - "a = 0 b = 2" si on schedule le thread après l'affectation b mais pas de a car le JIT ou le pipeline à déplacer l'affectation
 * - "a = 1 b = 2" si on schedule le thread après les deux affectations.
 */
  public static void main(String[] args) {
    var e = new ExempleReordering();
    Thread.ofPlatform().start(() -> System.out.println("a = " + e.a + "  b = " + e.b));
    e.a = 1;
    e.b = 2;
  }
}