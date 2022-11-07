package fr.uge.concurrence.ex4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Sync<V> {
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition supplying = lock.newCondition();
  private boolean isSupplying;
  public boolean inSafe() {
    lock.lock();
    try {
      return isSupplying;
    } finally {
      lock.unlock();
    }
  }

  public V safe(Supplier<? extends V> supplier) throws InterruptedException {
    lock.lock();
    try {
      while (isSupplying) {
        supplying.await();
      }
      isSupplying = true;
    } finally {
      lock.unlock();
    }
    var ret = supplier.get();
    lock.lock();
    try {
      isSupplying = false;
      supplying.signal();
      return ret;
    } finally {
      lock.unlock();
    }
  }

  /*
  private final Object lock = new Object();
  private boolean isSupplying;
  public boolean inSafe() {
    synchronized (lock) {
      return isSupplying;
    }
  }

  public V safe(Supplier<? extends V> supplier) throws InterruptedException {
    synchronized (lock) {
      while (isSupplying) {
        lock.wait();
      }
      isSupplying = true;
    }
    var ret = supplier.get();
    synchronized (lock) {
      isSupplying = false;
      lock.notify();
      return ret;
    }
  }
  */
}
