package fr.uge.concurrence.td03;

import java.util.Objects;

/**
 * Si on commente la ligne avec le sleep le JIT va faire une boucle infinie sur while (value == null) car il ne regardera
 * plus la valeur de value, parce qu'il n'a aucun moyen de vérifié qu'elle est modifiée.
 */
public class StupidRendezVous<V> {
  private V value;
  
  public void set(V value) {
    Objects.requireNonNull(value);
    this.value = value;
  }
  
  public V get() throws InterruptedException {
    while(value == null) {
       // Thread.sleep(1);  // then comment this line !
    }
    return value;
  }
  
  public static void main(String[] args) throws InterruptedException {
    StupidRendezVous<String> rendezVous = new StupidRendezVous<>();
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
      rendezVous.set("hello");
    });
    
    System.out.println(rendezVous.get());
  }
}
