package fr.uge.concurrence.ex2;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Elle doit posséder une méthode qui récupère un nombre premier et le sauvegarde et une autre méthode qui renvoie
 * la liste des nombres qu'elle a enregistré.
 */
public class MyThreadSafeClass {
  private final int capacity;
  private final ArrayList<Long> values;
  private final ReentrantLock lock;
  private final Condition full;

  public MyThreadSafeClass(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must > 0");
    }
    this.capacity = capacity;
    values = new ArrayList<>();
    lock = new ReentrantLock();
    full = lock.newCondition();
  }

  public void add(long value) {
    lock.lock();
    try {
      values.add(value);
      full.signal();
    } finally {
      lock.unlock();
    }
  }

  public long gets() throws InterruptedException {
    lock.lock();
    try {
      while (values.size() < capacity) {
        full.await();
      }
      return values.stream().limit(capacity).mapToLong(Long::longValue).sum();
    } finally {
      lock.unlock();
    }
  }
}
