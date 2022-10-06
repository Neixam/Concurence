package fr.uge.concurrence.td03;

import java.util.Objects;

public class RendezVous<V> {
  private V value;
  private final Object lock = new Object();

  public void set(V value) {
    synchronized (lock) {
      Objects.requireNonNull(value);
      this.value = value;
    }
  }

  public V get() throws InterruptedException {
    for (;;) {
      Thread.sleep(1);
      synchronized (lock) {
        if (value == null) {
          continue;
        }
        return value;
      }
    }
  }
}
