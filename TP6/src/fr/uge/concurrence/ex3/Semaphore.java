package fr.uge.concurrence.ex3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Semaphore {
  private int permits;
  private final ReentrantLock lock;

  private final Condition empty;

  public Semaphore(int permits) {
    if (permits <= 0) {
      throw new IllegalArgumentException("Permits must > 0");
    }
    this.permits = permits;
    lock = new ReentrantLock();
    empty = lock.newCondition();
  }

  public void release() {
    lock.lock();
    try {
      permits++;
      empty.signal();
    } finally {
      lock.unlock();
    }
  }

  public boolean tryAcquire() {
    lock.lock();
    try {
      if (permits == 0) {
        return false;
      }
      permits--;
      return true;
    } finally {
      lock.unlock();
    }
  }

  public void acquire() throws InterruptedException {
    lock.lock();
    try {
      while (permits == 0) {
        empty.await();
      }
      permits--;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) {
    var semaphore = new Semaphore(5);
    IntStream.range(0, 10).forEach(i -> Thread.ofPlatform().start(() -> {
      try {
        semaphore.acquire();
        System.out.println("Thread " + i + " acquire permit");
        Thread.sleep(1000);
        System.out.println("Thread " + i + " release permit");
        semaphore.release();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }));
  }
}
