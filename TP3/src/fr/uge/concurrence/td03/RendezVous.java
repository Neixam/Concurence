package fr.uge.concurrence.td03;

import java.util.Objects;

public class RendezVous<V> {
  private V value;
  private final Object lock = new Object();

  public void set(V value) {
    synchronized (lock) {
      Objects.requireNonNull(value);
      this.value = value;
      lock.notify();
    }
  }

  public V get() throws InterruptedException {
    synchronized (lock) {
      while (value == null) {
        lock.wait();
      }
      return value;
    }
  }
}
