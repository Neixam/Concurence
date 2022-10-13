package fr.uge.ex2;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.IntStream;

public class BoundedSafeQueue<V> {
  private final Queue<V> internQueue;
  private final int capacity;

  public BoundedSafeQueue(int capacity) {
    if (capacity < 0) {
      throw new IllegalStateException("Negative value forbidden");
    }
    this.capacity = capacity;
    internQueue = new ArrayDeque<>(capacity);
  }

  public void put(V value) throws InterruptedException {
    Objects.requireNonNull(value);
    synchronized (internQueue) {
      while (internQueue.size() >= capacity) {
        internQueue.wait();
      }
      internQueue.add(value);
      internQueue.notifyAll();
    }
  }

  public V take() throws InterruptedException {
    synchronized (internQueue) {
      while (internQueue.isEmpty()) {
        internQueue.wait();
      }
      var ret = internQueue.poll();
      internQueue.notifyAll();
      return ret;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var safeQueue = new BoundedSafeQueue<String>(10);
    IntStream.range(0, 50).forEach(i ->
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
