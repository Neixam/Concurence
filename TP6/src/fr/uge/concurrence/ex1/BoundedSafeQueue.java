package fr.uge.concurrence.ex1;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * On avait utilisé un notifyAll() ici car on devait être sur de réveiller celui de la bonne condition car il y avait
 * possiblement plusieurs threads en wait().
 */

public class BoundedSafeQueue<V> {
  private final ArrayDeque<V> internQueue;
  private final int capacity;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition empty = lock.newCondition();
  private final Condition full = lock.newCondition();

  public BoundedSafeQueue(int capacity) {
    if (capacity < 0) {
      throw new IllegalStateException("Negative value forbidden");
    }
    this.capacity = capacity;
    internQueue = new ArrayDeque<>(capacity);
  }

  public void put(V value) throws InterruptedException {
    Objects.requireNonNull(value);
    lock.lock();
    try {
      while (internQueue.size() == capacity) {
        full.await();
      }
      empty.signal();
      internQueue.add(value);
    } finally {
      lock.unlock();
    }
  }

  public V take() throws InterruptedException {
    lock.lock();
    try {
      while (internQueue.isEmpty()) {
        empty.await();
      }
      full.signal();
      return internQueue.remove();
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var safeQueue = new BoundedSafeQueue<String>(5);
    IntStream.range(0, 12).forEach(i ->
            Thread.ofPlatform().name("Thread " + i).start(() -> {
              for (;;) {
                try {
                  safeQueue.put(Thread.currentThread().getName());
                  Thread.sleep(2000);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              }
            }));
    while (true) {
      System.out.println(safeQueue.take());
    }
  }
}
