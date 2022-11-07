package fr.uge.concurrence.ex4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class PermitSync<V> {
  private int permits;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition empty = lock.newCondition();

  public PermitSync(int permits) {
    if (permits <= 0) {
      throw new IllegalArgumentException("");
    }
    this.permits = permits;
  }

  public V safe(Supplier<? extends V> supplier) throws InterruptedException {
    lock.lock();
    try {
      while (permits == 0) {
        empty.await();
      }
      permits--;
    } finally {
      lock.unlock();
    }
    var ret = supplier.get();
    lock.lock();
    try {
      permits++;
      empty.signal();
      return ret;
    } finally {
      lock.unlock();
    }
  }
  /*
  private int permits;
  private final Object lock = new Object();

  public PermitSync(int permits) {
    if (permits <= 0) {
      throw new IllegalArgumentException("");
    }
    this.permits = permits;
  }

  public V safe(Supplier<? extends V> supplier) throws InterruptedException {
    synchronized (lock) {
      while (permits == 0) {
        lock.wait();
      }
      permits--;
    }
    var ret = supplier.get();
    synchronized (lock) {
      permits++;
      lock.notify();
      return ret;
    }
  }
   */
}
